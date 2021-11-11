package com.pgi.convergence.common.features

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.pgi.network.interceptors.PGiHeaderInterceptor
import com.pgi.network.interceptors.PGiTokenAuthenticator
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

class FeaturesConfigService {
	val gson = GsonBuilder().serializeNulls().create()

	fun getConfig(url: String): Observable<FeaturesConfig> {
		val retrofit = Retrofit.Builder()
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.baseUrl(url)
				.client(this.okClient())
				.build()
		val configAPi = retrofit.create(RemoteFeatureConfigAPi::class.java)
		return  configAPi.getRemoteConfig()
				.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
	}

	private fun okClient(): OkHttpClient {
		val logging = HttpLoggingInterceptor()
		logging.level = HttpLoggingInterceptor.Level.BODY
		return OkHttpClient.Builder()
				.connectTimeout(1, TimeUnit.MINUTES)
				.writeTimeout(1, TimeUnit.MINUTES)
				.readTimeout(1, TimeUnit.MINUTES)
				.addInterceptor(logging)
				.addInterceptor(PGiHeaderInterceptor())
				.addNetworkInterceptor(StethoInterceptor())
				.authenticator(PGiTokenAuthenticator())
				.build()
	}

}

interface RemoteFeatureConfigAPi {
	@GET("./pgi_mobile_config.json")
	fun getRemoteConfig(): Observable<FeaturesConfig>
}