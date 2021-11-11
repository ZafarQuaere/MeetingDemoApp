package com.pgi.network

import com.pgi.network.helper.GMBaseServiceHelper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Sudheer Chilumula on 9/19/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class GMWebUAPIServiceManager private constructor() {
  companion object {
    lateinit var baseUrl: String

    fun create(): GMWebUAPIServiceAPI? {
      return when (baseUrl.isNullOrBlank()) {
        true -> null
        false -> {
          val retrofit = Retrofit.Builder()
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl(baseUrl)
              .client(GMBaseServiceHelper.okClient())
              .build()
          retrofit.create(GMWebUAPIServiceAPI::class.java)
        }
      }
    }

  }
}