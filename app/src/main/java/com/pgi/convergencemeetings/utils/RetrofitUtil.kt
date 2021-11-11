package com.pgi.convergencemeetings.utils

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


var gson = GsonBuilder()
        .setLenient()
        .create()

val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

fun makeRetrofit(baseUrl: String, cache: Cache? = null, vararg interceptors: Interceptor) =
        retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(makeHttpClient(cache, interceptors))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

fun makeHttpClient(cache: Cache?, interceptors: Array<out Interceptor>) =
        okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor( StethoInterceptor())
                .addInterceptor(logging)
                .apply { for(i in interceptors) addInterceptor(i) }
                .build()

fun headerInterceptor() = Interceptor { chain ->
    chain.proceed(
            chain.request().newBuilder().addHeader("Accept", "application/json").addHeader(
                    "Content-Type",
                    "application/json"
            ).build()
    )

}

// TODO add logging intercepot