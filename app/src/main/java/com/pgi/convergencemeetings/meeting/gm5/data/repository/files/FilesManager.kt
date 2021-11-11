package com.pgi.convergencemeetings.meeting.gm5.data.repository.files

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.pgi.network.interceptors.PGiHeaderInterceptor
import com.pgi.network.interceptors.PGiTokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FilesManager private constructor(){
    companion object {
        lateinit var baseUrl: String

        fun create(): FileApi? {
            return when (baseUrl.isNullOrBlank()) {
                true -> null
                false -> {
                    val retrofit = Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(baseUrl)
                            .client(okClient())
                            .build()
                    retrofit.create(FileApi::class.java)
                }
            }
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
}