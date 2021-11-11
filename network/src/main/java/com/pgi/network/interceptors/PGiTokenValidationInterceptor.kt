package com.pgi.network.interceptors

import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PGiTokenValidationInterceptor(
        private val authService: PGiIdentityAuthService,
        val logger: Logger = CoreApplication.mLogger): Interceptor {

    private val TAG = PGiTokenValidationInterceptor::class.java.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val state = authService.authState().getCurrent()
        if (state.needsTokenRefresh) {
            val actionComplete = CountDownLatch(1)
            val disposable = authService.tokenSubject.subscribe {
                logger.info(TAG, LogEvent.API_IDENTITY, LogEventValue.IDENTITY_TOKENREFRESH,
                        "PGiTokenValidationInterceptor - Got a new Token for request:  ${request.url()}")
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
                request = request.newBuilder()
                        .header("Authorization", "Bearer ${state.accessToken}")
                        .build()
            }
        }
        return chain.proceed(request)
    }
}