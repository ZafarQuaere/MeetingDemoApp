package com.pgi.network.interceptors

import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Created by Sudheer Chilumula on 2019-01-11.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class PGiTokenAuthenticator: Authenticator {

  private val TAG = PGiTokenAuthenticator::class.java.simpleName
  override fun authenticate(route: Route?, response: Response): Request? {
    var newRequest: Request? = null
    var logger = CoreApplication.mLogger
    if(response.code() == 401 && requestHasAuthorizationHeader(response)) {
      val actionComplete = CountDownLatch(1)
      val authService = PGiIdentityAuthService.getInstance(CoreApplication.appContext)
      val state = authService.authState().getCurrent()
      val disposable = authService.tokenSubject.subscribe {
        logger.info(TAG, LogEvent.API_IDENTITY, LogEventValue.IDENTITY_TOKENREFRESH,
            "PGiTokenAuthenticator - Got a new Token for request:  ${response.request().url()}")
        actionComplete.countDown()
      }
      authService.refreshAccessToken()
      var complete: Boolean = try {
        actionComplete.await(15000, TimeUnit.MILLISECONDS)
      } catch (ex: InterruptedException) {
        false
      }
      disposable.dispose()
      if (complete) {
        newRequest = response.request().newBuilder()
            .header("Authorization", "Bearer ${state.accessToken}")
            .build()
      }
    }
    return newRequest
  }

  private fun requestHasAuthorizationHeader(response: Response): Boolean {
    val authorizationHeader = response.request().header("Authorization")
    return authorizationHeader?.startsWith("Bearer ") ?: false
  }
}