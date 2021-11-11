package com.pgi.network

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.interceptors.NetworkConnectionInterceptor
import com.pgi.network.interceptors.PGiHeaderInterceptor
import com.pgi.network.interceptors.PGiTokenAuthenticator
import com.pgi.network.interceptors.TimeOutInterceptor
import com.pgi.network.models.TurnServerInfo
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class TurnServerInfoManager {
  companion object {
    var turnURL : String? = null
    var turnServerInfo: TurnServerInfo? = null
    var turnRestSuccess: Boolean = false
  }

  var credentialExpiryCheckerTimer: Timer? = null
  private var turnFrequency : Long  = 0
  private var toleranceTime : Long = 5 * 60

  fun getTurnServerInfo(turnURL : String): Observable<TurnServerInfo> {
    return create("$turnURL/").getTurnServerInfo()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(0, 15000))
  }

  fun create(turnURL : String): TurnServerInfoAPI {
    val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(turnURL)
        .client(this.okClient())
        .build()
    return retrofit.create(TurnServerInfoAPI::class.java)
  }

  private fun okClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .authenticator(PGiTokenAuthenticator())
        .addInterceptor(logging)
        .addInterceptor(PGiHeaderInterceptor())
        .addInterceptor(TimeOutInterceptor())
        .addInterceptor(NetworkConnectionInterceptor())
        .addNetworkInterceptor(StethoInterceptor())
        .build()
  }

    fun getTurnRestInfo() {
        turnURL?.let { it ->
            getTurnServerInfo(it).subscribe(
                    { turnInfo ->
                        turnRestSuccess = true
                        Log.i("PGiSoftPhoneImpl", "TurnServerInfo=$turnInfo")
                        turnServerInfo = turnInfo
                        turnFrequency = TimeUnit.SECONDS.toMillis(turnInfo.ttl - toleranceTime)
                        startCredentialExpiryChecker()
                    }, {
            })
        }
    }

    inner class CredentialsExpiryChecker : TimerTask() {
        override fun run() {
            stopCredentialExpiryChecker()
            getTurnRestInfo()
        }
    }

    fun startCredentialExpiryChecker() {
        val credentialsExpiryChecker = CredentialsExpiryChecker()
        credentialExpiryCheckerTimer = Timer()
        credentialExpiryCheckerTimer?.schedule(credentialsExpiryChecker, turnFrequency)
    }

    fun stopCredentialExpiryChecker() {
        turnRestSuccess = false
        credentialExpiryCheckerTimer.let {
            credentialExpiryCheckerTimer?.cancel()
            credentialExpiryCheckerTimer?.purge()
        }
    }
}