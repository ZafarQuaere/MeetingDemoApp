package com.pgi.network.repository

import android.content.Context
import com.pgi.auth.PGiAuthStateManager
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.Logger
import com.pgi.network.GMWebServiceAPI
import com.pgi.network.GMWebServiceManager
import com.pgi.network.di.networkTestModule
import com.pgi.network.interceptors.PGiTokenValidationInterceptor
import com.pgi.network.mockservers.MockGMWebServerDispatcher
import com.pgi.network.models.GMMeetingInfoGetResponse
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.reactivex.observers.TestObserver
import net.openid.appauth.AuthState
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import io.mockk.mockkObject
import net.openid.appauth.TokenResponse
import org.junit.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@RunWith(JUnit4::class)
class GMWebServiceRepositoryTest: KoinTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var accessToken: String
    private lateinit var mockAuthState: AuthState
    private lateinit var mockAuthService: PGiIdentityAuthService

    @Before
    fun setup() {

        val mockContext = mockkClass(Context::class)
        CoreApplication.appContext = mockContext

        val dispatcher = MockGMWebServerDispatcher()
        mockServer = MockWebServer()
        mockServer.dispatcher = dispatcher
        mockServer.start()

        accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"

        val mockLogger = mockkClass(Logger::class)
        every { mockLogger.error(any(), any(), any(),any()) } returns Unit
        CoreApplication.mLogger = mockLogger
        mockAuthState = mockkClass(AuthState::class)
        val mockAuthStateManager = mockkClass(PGiAuthStateManager::class)
        mockAuthService = mockkClass(PGiIdentityAuthService::class, relaxed = true)
        every { mockAuthService.authState() } returns mockAuthStateManager
        every { mockAuthStateManager.getCurrent() } returns mockAuthState

        mockkObject(PGiIdentityAuthService)
        every { PGiIdentityAuthService.getInstance(mockContext) } returns mockAuthService

        val validationInterceptor = PGiTokenValidationInterceptor(mockAuthService)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { it ->
                    val request = it.request().newBuilder()
                            .addHeader("Authorization", "Bearer $accessToken")
                            .build()
                    it.proceed(request)
                }
                .addInterceptor(validationInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mockServer.url("/").toString())
                .client(okHttpClient)
                .build()
        val gmWebServiceAPI = retrofit.create(GMWebServiceAPI::class.java)
        mockkConstructor(GMWebServiceManager::class)
        every { anyConstructed<GMWebServiceManager>().create() } returns gmWebServiceAPI
    }

    @After
    @Throws
    fun tearDown() {
        mockServer.shutdown()
        stopKoin()
    }

    @Test
    fun `test getMeetingInfo`() {

        startKoin {
            modules(networkTestModule)
        }

        every { mockAuthState.needsTokenRefresh } returns false
        val testObserver = TestObserver<GMMeetingInfoGetResponse>()
        val gmWebServiceRepository = GMWebServiceRepository()
        gmWebServiceRepository.getMeetingInformation(12345).subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.meetingRoomGetResult.audioDetail?.primaryAccessNumber == "1-605-475-5603"
                            && t.meetingRoomGetResult.audioDetail?.phoneInformation?.size == 2
                }
    }

    @Test
    fun `test getReservation token expired`() {

        startKoin {
            modules(networkTestModule)
        }

        every { mockAuthState.needsTokenRefresh } returns true
        val testObserver = TestObserver<GMMeetingInfoGetResponse>()
        val gmWebServiceRepository = GMWebServiceRepository()
        gmWebServiceRepository.getMeetingInformation(12345).subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.meetingRoomGetResult.audioDetail?.primaryAccessNumber == "1-605-475-5603"
                            && t.meetingRoomGetResult.audioDetail?.phoneInformation?.size == 2
                }
    }

    @Test
    fun `test getReservation token expired refresh token`() {
        startKoin {
            modules(networkTestModule)
        }

        every { mockAuthState.needsTokenRefresh } returns true
        every { mockAuthState.accessToken } returns "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
        every { mockAuthService.refreshAccessToken() } answers {
            val tokenResponse = mockkClass(TokenResponse::class)
            mockAuthService.tokenSubject.onNext(tokenResponse)
        }
        mockkConstructor(CountDownLatch::class)
        every { anyConstructed<CountDownLatch>().await(15000, TimeUnit.MILLISECONDS) } returns true
        val testObserver = TestObserver<GMMeetingInfoGetResponse>()
        val gmWebServiceRepository = GMWebServiceRepository()
        gmWebServiceRepository.getMeetingInformation(12345).subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.meetingRoomGetResult.audioDetail?.primaryAccessNumber == "1-605-475-5603"
                            && t.meetingRoomGetResult.audioDetail?.phoneInformation?.size == 2
                }
    }
}