package com.pgi.convergencemeetings.repository

import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback.SalesForceRepository
import com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback.SalesForceService
import com.pgi.convergencemeetings.mockservers.MockUAPIServerDispatcher
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(JUnit4::class)
class SalesForceRepositoryTest {
    private lateinit var mockServer: MockWebServer

    @Before
    fun setup() {
        val dispatcher = MockUAPIServerDispatcher()
        mockServer = MockWebServer()
        mockServer.setDispatcher(dispatcher)
        mockServer.start()

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { it ->
                    val request = it.request().newBuilder()
                            .addHeader("Content-Type:application", "x-www-form-urlencoded")
                            .build()
                    it.proceed(request)
                }
                .build()

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mockServer.url("/").toString())
                .client(okHttpClient)
                .build()
        CoreApplication.mLogger = TestLogger()
        SalesForceRepository.service= retrofit.create(SalesForceService::class.java)
    }

    @After
    @Throws
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun testSalesForceTicket() {
        SalesForceRepository.baseUrl = "https://webto.salesforce.com/servlet/servlet.WebToCase"
        SalesForceRepository.instance.createSalesForceTicket("abc@hcl.com","","4567","Abhishek","Guest","abc@hcl.com","Android","6.30","Android","Android","GM","6.42","http://pgi.com","Abhishek","EN","GMC","Test Android", "APAC")
        Assert.assertNotNull(SalesForceRepository)
    }
}