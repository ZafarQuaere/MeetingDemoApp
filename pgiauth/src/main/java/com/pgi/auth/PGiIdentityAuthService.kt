package com.pgi.auth

/**
 * Created by Sudheer Chilumula on 2018-12-05.
 * PGi
 * sudheer.chilumula@pgi.com
 */
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.utils.KotlinSingletonHolder
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import io.reactivex.subjects.PublishSubject
import net.openid.appauth.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference


class PGiIdentityAuthService private constructor(context: Context): PGiAuthService {
  private val mLogger = CoreApplication.mLogger
  private val TAG = PGiIdentityAuthService::class.java.simpleName
  private val mPGiAuthStateManager: PGiAuthStateManager
  private val mPGiAuthConfiguration: PGiAuthConfiguration
  private val mClientId = AtomicReference<String>()
  private val mAuthRequest = AtomicReference<AuthorizationRequest>()
  private val mAuthIntent = AtomicReference<CustomTabsIntent>()
  private val mLocale = AtomicReference<String>()
  private var mAuthIntentLatch = CountDownLatch(0)
  private var mRefreshingLatch = CountDownLatch(0)
  private val mExecutor = Executors.newSingleThreadExecutor()
  private val mContext: Context = context
  private val PGI_AUTH_RESPONSE = "PGI_AUTH_RESPONSE"
  private var mAuthService: AuthorizationService? = null
  val tokenSubject: PublishSubject<TokenResponse> = PublishSubject.create()
  val authExceptionSubject: PublishSubject<AuthorizationException> = PublishSubject.create()
  var skipWarmUp: Boolean = false


  init {
    mPGiAuthStateManager = PGiAuthStateManager.getInstance(context)
    mPGiAuthConfiguration = PGiAuthConfiguration.getInstance(context)
    mAuthRequest.set(null)
  }

  companion object: KotlinSingletonHolder<PGiIdentityAuthService, Context>({
    val instance: AtomicReference<WeakReference<PGiIdentityAuthService>> = AtomicReference(WeakReference(PGiIdentityAuthService(it)))
    instance.get().get()!!
  })

  private fun recreateAuthorizationService(context: Context) {
    if (mAuthService != null) {
      try {
        mAuthService?.dispose()
      } catch (ex: Exception) {
        val msg: String = ex.message.toString()
        mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.IDENTITY_SIGNIN, msg, ex, null, true, false);
      }
      mAuthService = null
    }
    mAuthService = createAuthorizationService(context)
    mAuthRequest.set(null)
    mAuthIntent.set(null)
  }

  private fun createAuthorizationService(context: Context): AuthorizationService {
    val builder = AppAuthConfiguration.Builder()
    builder.setConnectionBuilder(mPGiAuthConfiguration.getConnectionBuilder())
    return AuthorizationService(context, builder.build())
  }


  override fun doAuth(context: Context, locale: String, completionClass: String, cancelClass: String) {
    mLocale.set(locale)
    recreateAuthorizationService(context)
    initializeAppAuth()
    val completionIntent =  Intent(context, Class.forName(completionClass))
    completionIntent.action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE"
    val cancelIntent =  Intent(context, Class.forName(cancelClass))
    cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    cancelIntent.putExtra("failed", true)
    mExecutor.submit {
      mAuthIntentLatch.await()
      mAuthIntent.get().intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      mAuthService?.performAuthorizationRequest(
          mAuthRequest.get(),
          PendingIntent.getActivity(context, mAuthRequest.hashCode(), completionIntent, 0),
          PendingIntent.getActivity(context, mAuthRequest.hashCode(), cancelIntent, 0),
          mAuthIntent.get())
    }
  }

  private fun initializeAppAuth() {
    if (mPGiAuthStateManager.getCurrent().authorizationServiceConfiguration != null) {
      initializeClient()
      return
    }
    if (mPGiAuthConfiguration.mDiscoveryUri == null) {
      val config = AuthorizationServiceConfiguration(
          mPGiAuthConfiguration.mAuthEndpointUri!!,
          mPGiAuthConfiguration.mTokenEndpointUri!!,
          mPGiAuthConfiguration.mRegistrationEndpointUri)

      mPGiAuthStateManager.replace(AuthState(config))
      initializeClient()
    }
  }

  private fun initializeClient() {
    if (mPGiAuthConfiguration.mClientId != null) {
      mClientId.set(mPGiAuthConfiguration.mClientId)
      createAuthRequest()
      if(!skipWarmUp) {
        warmUpBrowser()
      } else {
        // This is here only for test purpose
        mAuthIntent.set(CustomTabsIntent.Builder().build())
      }
    }
  }

  private fun createAuthRequest() {
    // Refer:: https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    val authRequestBuilder = AuthorizationRequest.Builder(
        mPGiAuthStateManager.getCurrent().authorizationServiceConfiguration!!,
        mClientId.get(),
        ResponseTypeValues.CODE,
        mPGiAuthConfiguration.mRedirectUri!!)
        .setAdditionalParameters(prepareAdditionalParams())
        .setScope(mPGiAuthConfiguration.mScope)
        .setPrompt("login")
    mAuthRequest.set(authRequestBuilder.build())
  }

  private fun warmUpBrowser() {
    mAuthIntentLatch = CountDownLatch(1)
    mExecutor.submit {
      val intentBuilder = mAuthService?.createCustomTabsIntentBuilder(mAuthRequest.get().toUri())
      intentBuilder?.enableUrlBarHiding()
      mAuthIntent.set(intentBuilder?.build())
      mAuthIntentLatch.countDown()
    }
  }

  private fun prepareAdditionalParams(): Map<String, String> {
    val gmParams = LinkedHashMap<String, String>()
    gmParams["auth_by_client"] = "true"
    gmParams["access_type"] = "offline"
    gmParams["brand_id"] = mPGiAuthConfiguration.mBrandId.toString()
    gmParams["label"] = mPGiAuthConfiguration.mLabel.toString()
    gmParams["locale"] = mLocale.get()
    return gmParams
  }

  override fun isAuthorized(): Boolean {
    return (mPGiAuthStateManager.getCurrent().isAuthorized) and (!mPGiAuthConfiguration.hasConfigurationChanged())
  }

  override fun onAuthResponse(intent: Intent) {
    val response = AuthorizationResponse.fromIntent(intent)
    val ex = AuthorizationException.fromIntent(intent)

    if (response != null || ex != null) {
      mPGiAuthStateManager.updateAfterAuthorization(response, ex)
    }

    if (response?.authorizationCode != null) {
      mPGiAuthStateManager.updateAfterAuthorization(response, ex)
      exchangeAuthorizationCode(response)
    }
  }

  private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
    performTokenRequest(
        authorizationResponse.createTokenExchangeRequest(),
        AuthorizationService.TokenResponseCallback { tokenResponse, authException -> this.handleCodeExchangeResponse(tokenResponse, authException) })
  }

  private fun handleCodeExchangeResponse(
      tokenResponse: TokenResponse?,
      authException: AuthorizationException?) {
    mPGiAuthStateManager.updateAfterTokenResponse(tokenResponse, authException)
    if(tokenResponse != null) {
      tokenSubject.onNext(tokenResponse)
    }
    if(authException != null) {
      authExceptionSubject.onNext(authException)
    }
  }

  private fun performTokenRequest(
      request: TokenRequest,
      callback: AuthorizationService.TokenResponseCallback) {
    val clientAuthentication: ClientAuthentication
    try {
      clientAuthentication = mPGiAuthStateManager.getCurrent().clientAuthentication
    } catch (ex: ClientAuthentication.UnsupportedAuthenticationMethod) {
      return
    }
    recreateAuthorizationService(mContext)
    mAuthService?.performTokenRequest(
        request,
        clientAuthentication,
        callback)
  }

  override fun refreshAccessToken() {
    mExecutor.submit {
      performTokenRequest(
          mPGiAuthStateManager.getCurrent().createTokenRefreshRequest(),
          AuthorizationService.TokenResponseCallback { tokenResponse, authException -> this.handleAccessTokenResponse(tokenResponse, authException) })
    }
  }

  private fun handleAccessTokenResponse(tokenResponse: TokenResponse?, authException: AuthorizationException?) {
    mPGiAuthStateManager.updateAfterTokenResponse(tokenResponse, authException)
    if(tokenResponse != null) {
      tokenSubject.onNext(tokenResponse)
    }
    if(authException != null) {
      authExceptionSubject.onNext(authException)
    }
  }

  override fun authState(): PGiAuthStateManager {
    return mPGiAuthStateManager
  }

  override fun signOut() {
    val currentState = mPGiAuthStateManager.getCurrent()
    if(currentState.authorizationServiceConfiguration != null) {
      val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)
      mPGiAuthStateManager.replace(clearedState)
    }
  }

  override fun destroy() {
    mAuthService?.dispose()
  }
}