package com.pgi.network

/**
 * Created by Sudheer Chilumula on 9/6/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.pgi.network.interceptors.NetworkConnectionInterceptor
import com.pgi.network.interceptors.PGiHeaderInterceptor
import com.pgi.network.interceptors.PGiTokenAuthenticator
import com.pgi.network.repository.GMElasticSearchServiceAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class GMElasticSearchServiceManager private constructor() {

	companion object {
		fun create(): GMElasticSearchServiceAPI {
			val retrofit = Retrofit.Builder()
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.baseUrl(BuildConfig.GMSEARCH_URL)
					.client(okClient())
					.build()
			return retrofit.create(GMElasticSearchServiceAPI::class.java)
		}

		private fun okClient(): OkHttpClient {
			val logging = HttpLoggingInterceptor()
			logging.level = HttpLoggingInterceptor.Level.BODY
			return OkHttpClient.Builder()
					.connectTimeout(15, TimeUnit.SECONDS)
					.writeTimeout(15, TimeUnit.SECONDS)
					.readTimeout(15, TimeUnit.SECONDS)
					.addInterceptor(logging)
					.addInterceptor(PGiHeaderInterceptor())
					.addInterceptor(NetworkConnectionInterceptor())
					.addNetworkInterceptor(StethoInterceptor())
					.authenticator(PGiTokenAuthenticator())
					.build()

		}
	}
}