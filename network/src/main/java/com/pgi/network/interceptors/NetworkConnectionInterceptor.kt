package com.pgi.network.interceptors

import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.utils.InternetConnection
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor: Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		if (!InternetConnection.isConnected(CoreApplication.appContext)) {
			throw NoConnectivityException()
		}
		return chain.proceed(request)
	}
}

class NoConnectivityException : IOException() {
	override val message: String
		get() = "No Internet Connection"
}