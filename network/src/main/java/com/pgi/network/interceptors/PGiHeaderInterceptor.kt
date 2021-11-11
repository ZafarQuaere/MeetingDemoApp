package com.pgi.network.interceptors

import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Created by Sudheer Chilumula on 2019-01-11.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class PGiHeaderInterceptor: Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val authService = PGiIdentityAuthService.getInstance(CoreApplication.appContext)
    var state = authService.authState().getCurrent()
    val mAuthtoken = SharedPreferencesManager.getInstance().authToken
    var token: String? = state.accessToken
    var requestBuilder = if (chain.request().url().pathSegments().contains("forgotPasswordEmail") || chain.request().url().pathSegments().contains("revoke")) {
      chain.request().newBuilder()
    } else if ((chain.request().url().pathSegments().contains("uapi") && !chain.request().url().pathSegments().contains("session")) || chain.request().url().pathSegments().contains("files") && chain.request().url().pathSegments().contains("session")) {
      chain.request().newBuilder().addHeader("Authorization", "Bearer $token").addHeader("X-PGIAPI-UAPI", "Bearer $mAuthtoken")
    } else {
      chain.request().newBuilder().addHeader("Authorization", "Bearer $token")
    }
    val request = requestBuilder.build()
    return chain.proceed(request)
  }
}