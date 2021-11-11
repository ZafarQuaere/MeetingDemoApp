package com.pgi.network

import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.network.helper.GMBaseServiceHelper
import com.pgi.network.interceptors.PGiTokenValidationInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GMWebServiceManager {

    val gmWebServiceAPI: GMWebServiceAPI by lazy { create() }

    fun create(): GMWebServiceAPI {
        val authService = PGiIdentityAuthService.getInstance(CoreApplication.appContext)
        val okClient = GMBaseServiceHelper.okClient(listOf(PGiTokenValidationInterceptor(authService)))
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.PGI_BASE_URL)
                .client(okClient)
                .build()
        return retrofit.create(GMWebServiceAPI::class.java)
    }
}