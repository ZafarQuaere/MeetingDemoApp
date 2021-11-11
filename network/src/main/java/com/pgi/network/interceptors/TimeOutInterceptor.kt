package com.pgi.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class TimeOutInterceptor : Interceptor {

	companion object {
		private const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
		private const val READ_TIMEOUT = "READ_TIMEOUT"
		private const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
	}

	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()

		var connectTimeout = chain.connectTimeoutMillis()
		var readTimeout = chain.readTimeoutMillis()
		var writeTimeout = chain.writeTimeoutMillis()

		val builder = request.newBuilder()

		request.header(CONNECT_TIMEOUT)?.also {
			connectTimeout = it.toInt()
			builder.removeHeader(CONNECT_TIMEOUT)
		}

		request.header(READ_TIMEOUT)?.also {
			readTimeout = it.toInt()
			builder.removeHeader(READ_TIMEOUT)
		}

		request.header(WRITE_TIMEOUT)?.also {
			writeTimeout = it.toInt()
			builder.removeHeader(WRITE_TIMEOUT)
		}

		return chain.run {
			withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
			withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
			withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
			proceed(builder.build())
		}
	}
}