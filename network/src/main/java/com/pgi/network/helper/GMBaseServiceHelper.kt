package com.pgi.network.helper

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.pgi.network.interceptors.NetworkConnectionInterceptor
import com.pgi.network.interceptors.PGiHeaderInterceptor
import com.pgi.network.interceptors.PGiTokenAuthenticator
import com.pgi.network.interceptors.TimeOutInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class GMBaseServiceHelper {
    companion object {
         fun okClient(interceptors: List<Interceptor>? = null): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
             val builder = OkHttpClient.Builder()
                     .connectTimeout(1, TimeUnit.MINUTES)
                     .readTimeout(1, TimeUnit.MINUTES)
                     .writeTimeout(1, TimeUnit.MINUTES)
                     .authenticator(PGiTokenAuthenticator())
                     .addInterceptor(logging)
                     .addInterceptor(PGiHeaderInterceptor())
                     .addInterceptor(TimeOutInterceptor())
                     .addInterceptor(NetworkConnectionInterceptor())
                     .addNetworkInterceptor(StethoInterceptor())
             if (!interceptors.isNullOrEmpty()) {
                 interceptors.forEach {
                     builder.addInterceptor(it)
                 }
             }
            return builder.build()
        }
    }
}