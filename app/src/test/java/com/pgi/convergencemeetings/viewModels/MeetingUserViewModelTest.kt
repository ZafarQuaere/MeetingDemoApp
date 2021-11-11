package com.pgi.convergencemeetings.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import blockingObserve
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.base.di.featureTestMangerModule
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.mockservers.*
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FileApi
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FilesRespository
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.network.FurlProxyServiceManager
import com.pgi.network.GMWebUAPIServiceAPI
import com.pgi.network.interceptors.NetworkConnectionInterceptor
import com.pgi.network.models.AuthorizeResponse
import com.pgi.network.models.HorsesEnvConfig
import com.pgi.network.models.JoinMeetingResponse
import com.pgi.network.models.MeetingRoomInfoResponse
import com.pgi.network.repository.FurlProxyRepository
import com.pgi.network.repository.GMWebUAPIRepository
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.any
import org.junit.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.powermock.reflect.Whitebox
import org.robolectric.RuntimeEnvironment
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


/**
 * Test Cases for Meeting User view model
 *
 * @author Sudheer R Chilumula
 * @since 5.20
 */

@PrepareForTest(ApplicationDao::class, AppAuthUtils::class, FileApi::class,
    InternetConnection::class, FilesRespository::class, FurlProxyRepository::class, FurlProxyServiceManager::class)
class MeetingUserViewModelTest: RobolectricTest(), KoinTest {

  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val rule: PowerMockRule = PowerMockRule()

  @Mock
  private lateinit var mAppAuthUtils: AppAuthUtils

  @Mock
  private lateinit var fileApi: FileApi

  @Mock
  private lateinit var filesRespository: FilesRespository

  private val sessionresponse = Response.success<Void>(null)

  @InjectMocks
  private lateinit var mUserUserViewModel: MeetingUserViewModel
  private lateinit var mockServer: MockWebServer
  private lateinit var mockServerLocked: MockWebServer
  private lateinit var mockServerCapacity: MockWebServer
  private lateinit var mockServerDeleted: MockWebServer
  private lateinit var mockServerSessionUsed: MockWebServer
  private lateinit var mockServerWait: MockWebServer
  private lateinit var mockServerWaitTimeOut: MockWebServer
  private lateinit var mockServerFailure: MockWebServer
  private lateinit var mockResponse: MockResponse
  private lateinit var accessToken: String

  @Before
  fun setup() {
    CoreApplication.mLogger = TestLogger()
    CoreApplication.appContext = ApplicationProvider.getApplicationContext()
    startKoin {
      modules(featureTestMangerModule)
    }
    MockitoAnnotations.initMocks(this)
    val dispatcher = MockUAPIServerDispatcher()
    val dispatcherLocked = MockUAPIServerLockedDispatcher()
    val dispatcherCapacity = MockUAPIServerCapacityDispatcher()
    val dispatcherSessionUsed = MockUAPIServerSessionUsedDispatcher()
    val dispatcherDeleted = MockUAPIServerDeletedDispatcher()
    val dispatcherWait = MockUAPIServerWaitDispatcher()
    val dispatcherWaitTimeOut = MockUAPIServerWaitTimeOutDispatcher()
    val dispatcherFailure = MockUAPIServerJoinErrorDispatcher()


    mockServer = MockWebServer()
    mockServerLocked = MockWebServer()
    mockServerDeleted = MockWebServer()
    mockServerCapacity = MockWebServer()
    mockServerSessionUsed = MockWebServer()
    mockServerWait = MockWebServer()
    mockServerWaitTimeOut = MockWebServer()
    mockServerFailure = MockWebServer()
    mockResponse = MockResponse()
    mockServer.setDispatcher(dispatcher)
    mockServerLocked.setDispatcher(dispatcherLocked)
    mockServerDeleted.setDispatcher(dispatcherDeleted)
    mockServerCapacity.setDispatcher(dispatcherCapacity)
    mockServerSessionUsed.setDispatcher(dispatcherSessionUsed)
    mockServerWait.setDispatcher(dispatcherWait)
    mockServerWaitTimeOut.setDispatcher(dispatcherWaitTimeOut)
    mockServerFailure.setDispatcher(dispatcherFailure)
    mockServer.start()
    mockServerLocked.start()
    mockServerCapacity.start()
    mockServerDeleted.start()
    mockServerSessionUsed.start()
    mockServerWait.start()
    mockServerWaitTimeOut.start()
    mockServerFailure.start()
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
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

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofit.create(GMWebUAPIServiceAPI::class.java)
    PowerMockito.mockStatic(AppAuthUtils::class.java)
    whenever(AppAuthUtils.getInstance()).thenReturn(mAppAuthUtils)
    doReturn("user").`when`(mAppAuthUtils).lastName
    sessionresponse.headers().newBuilder().add("Set-Cookie", "CloudFront-Key-pair-Id=")
    sessionresponse.headers().newBuilder().add("Set-Cookie", "CloudFront-Policy=")
    sessionresponse.headers().newBuilder().add("Set-Cookie", "CloudFront-Signature=")
    whenever(fileApi.createSession(Mockito.anyString())).thenReturn(Observable.just(sessionresponse))
    filesRespository = FilesRespository.instance
    filesRespository.fileService = fileApi
  }

  @After
  @Throws fun tearDown() {
    mockServer.shutdown()
    stopKoin()
  }

  @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  private fun getJson(path : String) : String {
    val uri = javaClass.classLoader!!.getResource(path)
    val file = File(uri.file)
    return String(file.readBytes())
  }

  @Test
  fun `test mlogger attributes set`() {
    mUserUserViewModel.audioConnType = AudioType.DIAL_IN
    mUserUserViewModel.audioConnState = AudioStatus.CONNECTING
    Assert.assertEquals(CoreApplication.mLogger.attendeeModel.audioConnectionType,
        AudioType.DIAL_IN.type)
    Assert.assertEquals(CoreApplication.mLogger.attendeeModel.audioConnectionState,
        AudioStatus.CONNECTING.status)
  }

  @Test
  fun `test authorize meeting`() {
    mUserUserViewModel.authorizeMeeting("123456")
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
  }

  @Test
  fun `test join meeting`() {
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_MEETING_SUCCESS)
  }

  @Test
  fun `test join meeting locked`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerLocked.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_LOCK_MEETING)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joinmeetingfailure.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.LOCK)
  }

  @Test
  fun `test join meeting capacity`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerCapacity.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_MEETING_AT_CAPACITY)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joinroomatcapacity.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.CAPACITY)
  }

  @Test
  fun `test join meeting deleted`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerDeleted.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_DELETED_ROOM)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joindeletedroom.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.DELETED_ROOM)
  }

  @Test
  fun `test join meeting session used`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerSessionUsed.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_MEETING_SESSION_USED)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joinalreadyjoined.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.SESSION_USED)
  }


  @Test
  fun `test join meeting wait`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerWait.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_WAIT_ROOM)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joinwaitroom.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.WAIT)
  }

  @Test
  fun `test join meeting wait timeout`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerWaitTimeOut.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_WAIT_ROOM_TIMEOUT)
    val joinresponse = Gson().fromJson(getJson("json/uapi/joinwaittimeout.json"), JoinMeetingResponse::class.java)
    Assert.assertEquals(joinresponse.joinStatus, AppConstants.WAIT_TIMEOUT)
  }

  @Test
  fun `test join meeting failure`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServerFailure.url("/").toString())
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.joinMeeting(authresponse.loginName.split(" ").first(), authresponse.loginName.split(" ").last(), authresponse.loginName, authresponse.email, null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userflowObserver)
    //    Assert.assertEquals(userflowObserver, UserFlowStatus.JOIN_MEETING_FAILED)
  }

  @Test
  fun `test leave meeting`() {
    mUserUserViewModel.userInMeeting = true
    mUserUserViewModel.leaveMeeting()
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertEquals(userflowObserver, UserFlowStatus.LEAVE_MEETING_SUCCESS)
  }

  @Test
  fun `test update user role failure`() {
    accessToken = "expired_token"
    mUserUserViewModel.updateUserRole(User("123456"), true)
    val retryStatus = mUserUserViewModel.retryStatus.blockingObserve()
    Assert.assertEquals(retryStatus?.value(), RetryStatus.UAPI_PROMOTE_PARTICIPANT)
  }

  @Test
  fun `test end meeting`() {
    mUserUserViewModel.userInMeeting = true
    mUserUserViewModel.endMeeting(false)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertEquals(userflowObserver, UserFlowStatus.END_MEETING_SUCCESS)
  }

  @Test
  fun `test update user role `() {
    mUserUserViewModel.updateUserRole(User("123456"), true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test update user role promote false`() {
    var user = User("123456")
    user.isSharing = true
    var sharingContent = com.pgi.convergencemeetings.meeting.gm5.data.model.Content()
    sharingContent.id = "test id"
    sharingContent.dynamicMetaData.screenPresenter.partId = "testpartid"
    sharingContent.dynamicMetaData.screenPresenter.name = "testname"
    sharingContent.dynamicMetaData.screenPresenter.phoneNumber = "1234567890"
    sharingContent.dynamicMetaData.screenPresenter.email = "test@email.org"
    mUserUserViewModel.sharingContent = sharingContent
    mUserUserViewModel.updateUserRole(user, false)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test dismiss user role `() {
    mUserUserViewModel.dismissUser(User("123456"))
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver,UserFlowStatus.DISMISS_PARTICIPANT)
  }

  @Test
  fun `test dialout`() {
    mUserUserViewModel.dialOut("1", "123456789", "91", null)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test cancel dialout`() {
    mUserUserViewModel.cancelDialOut()
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test dismiss audio user`() {
    mUserUserViewModel.dismissAudioUser("123456")
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver, UserFlowStatus.DISMISS_AUDIO_PARTICIPANT)
  }

  @Test
  fun `test mute unmute`() {
    mUserUserViewModel.muteUnmuteUser("123456", "123456",true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver,UserFlowStatus.MUTE_UNMUTE)
  }

  @Test
  fun `test add chat`() {
    mUserUserViewModel.addChat("Test chat","default")
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertEquals(userflowObserver, UserFlowStatus.CHAT_ADD_SUCCESS)
  }


  @Test
  fun `test get a list of dialout phone numbers for host`() {
    val context = RuntimeEnvironment.application?.applicationContext;
    PowerMockito.mockStatic(ApplicationDao::class.java);
    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
    doReturn(false).`when`(mAppAuthUtils).isUserTypeGuest
    val joinMeetingResponse = Gson().fromJson(getJson("json/uapi/joinmeetingsuccess.json"), JoinMeetingResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingJoinResponse", joinMeetingResponse)
    val phoneList = mUserUserViewModel.getDialOutNumbers()
    Assert.assertNotNull(phoneList)
  }

  @Test
  fun `test get a list of dialout phone numbers for guest`() {
    val context = RuntimeEnvironment.application?.applicationContext;
    PowerMockito.mockStatic(ApplicationDao::class.java);
    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
    doReturn(true).`when`(mAppAuthUtils).isUserTypeGuest
    val joinMeetingResponse = Gson().fromJson(getJson("json/uapi/joinmeetingsuccess.json"), JoinMeetingResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingJoinResponse", joinMeetingResponse)
    val phoneList = mUserUserViewModel.getDialOutNumbers()
    Assert.assertNotNull(phoneList)
  }

  @Test
  fun `test get a list of dialout phone numbers when an exception occurs`() {
    val context = RuntimeEnvironment.application?.applicationContext;
    PowerMockito.mockStatic(ApplicationDao::class.java);
    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
    PowerMockito.`when`(applicationDao.phoneNumbers?.loadAll()).thenThrow(RuntimeException("testException"))
    doReturn(true).`when`(mAppAuthUtils).isUserTypeGuest
    val phoneList = mUserUserViewModel.getDialOutNumbers()
    Assert.assertNotNull(phoneList)
  }

  @Test
  fun `test files create session`() {
    mUserUserViewModel.horsesEnvConfig = HorsesEnvConfig()
    mUserUserViewModel.makeSessionCallToCloudFront("hwbfhc-sdcsdcds-vsvsvsdvds-svdsvds")
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test unknownhost error`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://thisIsARandomHost.com")
        .client(okHttpClient)
        .build()

    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    mUserUserViewModel.muteUnmuteUser("123456", "123456",true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver,UserFlowStatus.MUTE_UNMUTE)
  }

  @Test
  fun `test networkconnectivity error`() {
    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { it ->
          val request = it.request().newBuilder()
              .addHeader("Authorization", "Bearer $accessToken")
              .build()
          it.proceed(request)
        }
        .addInterceptor(NetworkConnectionInterceptor())
        .build()

    val retrofitFail = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(mockServer.url("/").toString())
        .client(okHttpClient)
        .build()

    PowerMockito.mockStatic(InternetConnection::class.java)
    Mockito.`when`(InternetConnection.isConnected(any())).thenReturn(false)
    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofitFail.create(GMWebUAPIServiceAPI::class.java)
    mUserUserViewModel.muteUnmuteUser("123456", "123456",true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver,UserFlowStatus.MUTE_UNMUTE)
  }

  @Test
  fun `test isMute true`() {
    mUserUserViewModel.isMute = true
    Assert.assertTrue(mUserUserViewModel.isMute)
  }

  @Test
  fun `test isSpeakerOn true`() {
    mUserUserViewModel.isSpeakerOn = true
    Assert.assertTrue(mUserUserViewModel.isSpeakerOn)
  }

  @Test
  fun `test isMuteBtnEnabled true`() {
    mUserUserViewModel.isMuteBtnEnabled = true
    Assert.assertTrue(mUserUserViewModel.isMuteBtnEnabled)
  }

  @Test
  fun `test isCameraOn true`() {
    mUserUserViewModel.isCameraOn = true
    Assert.assertTrue(mUserUserViewModel.isCameraOn)
  }

  @Test
  fun `test get a list of dialin phone numbers for guest`() {
    val context = RuntimeEnvironment.application?.applicationContext;
    PowerMockito.mockStatic(ApplicationDao::class.java);
    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
    doReturn(true).`when`(mAppAuthUtils).isUserTypeGuest
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val phoneList = mUserUserViewModel.getDialInNumbers()
    Assert.assertNotNull(phoneList)
  }

  @Test
  fun `test get a list of dialin phone numbers when list is empty`() {
    val context = RuntimeEnvironment.application?.applicationContext;
    PowerMockito.mockStatic(ApplicationDao::class.java);
    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
    doReturn(true).`when`(mAppAuthUtils).isUserTypeGuest
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfonophones.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val phoneList = mUserUserViewModel.getDialInNumbers()
    Assert.assertNotNull(phoneList)
  }

  @Test
  fun `test get a list of dailin phone numbers when meeting info response is null`() {
    val phoneList = mUserUserViewModel.getDialInNumbers()
    Assert.assertNotNull(phoneList)
  }

//  @Test
//  fun `test get a list of dialin phone numbers when an exception occurs`() {
//    val context = RuntimeEnvironment.application?.applicationContext;
//    PowerMockito.mockStatic(ApplicationDao::class.java);
//    val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
//    PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
//    PowerMockito.`when`(applicationDao.phoneNumbers?.loadAll()).thenThrow(RuntimeException("testException"))
//    doReturn(true).`when`(mAppAuthUtils).isUserTypeGuest
//    val phoneList = mUserUserViewModel.getDialInNumbers()
//    Assert.assertNotNull(phoneList)
//  }
//
//  @Test
//  fun `test resolveFurl with empty string`(){
//    mUserUserViewModel.resolveFurl("")
//    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
//    Assert.assertNotNull(userflowObserver)
//  }
//
//  @Mock
//  private lateinit var furlProxyServiceManager: FurlProxyServiceManager
//  @Test
//  fun `test resolveFurl with valid meetingurl`() {
//    val furl = "https://pgi.globalmeet.com/pepperbernhardt"
//    PowerMockito.mockStatic(FurlProxyServiceManager::class.java)
//    FurlProxyRepository.instance.furlProxyServiceManager = furlProxyServiceManager
//    mUserUserViewModel.resolveFurl(furl)
//    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
//    Assert.assertNotNull(userflowObserver)
//  }

  @Test
  fun `test getMeetingServer`() {
    mUserUserViewModel.getMeetingServer("https://director-na.globalmeet.com", "2830622")
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
//    Assert.assertNotNull(userflowObserver)
  }

  fun `test screenShareFullScreen true`() {
    mUserUserViewModel.screenShareFullScreen = true
    Assert.assertTrue(mUserUserViewModel.screenShareFullScreen)
  }

  @Test
  fun `test screenShareLandscape true`(){
    mUserUserViewModel.screenShareLandScape = true
    Assert.assertTrue(mUserUserViewModel.screenShareLandScape)
  }

  @Test
  fun `test screenShareLandscape false`(){
    mUserUserViewModel.screenShareLandScape = false
    Assert.assertFalse(mUserUserViewModel.screenShareLandScape)
  }

  @Test
  fun `test internetConnected false`() {
    mUserUserViewModel.internetConnected = false
    Assert.assertFalse(mUserUserViewModel.internetConnected)
  }

  @Test
  fun `test settabvalue `() {
    mUserUserViewModel.tabValue = 0
    Assert.assertEquals(mUserUserViewModel.tabValue, 0)
  }

  @Test
  fun `test clear`() {
    mUserUserViewModel.isMute = true
    mUserUserViewModel.userIsInWaitingRoom = true
    mUserUserViewModel.clear()
    Assert.assertFalse(mUserUserViewModel.isMute)
    Assert.assertFalse(mUserUserViewModel.userIsInWaitingRoom)
  }

  @Test
  fun `test isDialoutBlockedwithFalse`() {
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val blocked = mUserUserViewModel.isDialOutBlocked()
    Assert.assertFalse(blocked)
  }

  @Test
  fun `test isDialoutBlocked`() {
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo-dialoutblocked.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val blocked = mUserUserViewModel.isDialOutBlocked()
    Assert.assertTrue(blocked)
  }

  @Test
  fun `test isDialoutBlockedWithNUllMRIR`() {
    val blocked = mUserUserViewModel.isDialOutBlocked()
    Assert.assertFalse(blocked)
  }

  @Test
  fun `test screenSharePortrait is true`() {
    mUserUserViewModel.contentPresentationActive = true
    mUserUserViewModel.screenSharePortrait = true
    Assert.assertTrue(mUserUserViewModel.screenSharePortrait)
  }

  @Test
  fun `test screenSharePortrait is false`() {
    mUserUserViewModel.contentPresentationActive = true
    mUserUserViewModel.screenSharePortrait = false
    Assert.assertFalse(mUserUserViewModel.screenSharePortrait)
  }

  @Test
  fun `test screenShareFullScreen is true`() {
    mUserUserViewModel.contentPresentationActive = true
    mUserUserViewModel.screenShareFullScreen = true
    Assert.assertTrue(mUserUserViewModel.screenShareFullScreen)
  }

  @Test
  fun `test orientationChanged`() {
    mUserUserViewModel.orientationChanged = true
    Assert.assertTrue(mUserUserViewModel.orientationChanged)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve()
    Assert.assertEquals(userflowObserver, UserFlowStatus.ORIENTATION_CHANGED)
  }

  @Test
  fun `test getRoomInfoReponse`() {
    mUserUserViewModel.getRoomInfoResponse()
    Assert.assertNull(mUserUserViewModel.getRoomInfoResponse())
  }

  @Test
  fun `sharingContent`(){
    mUserUserViewModel.sharingContent
    Assert.assertNull(mUserUserViewModel.sharingContent)
  }

  @Test
  fun `test getJoinResponse`() {
    mUserUserViewModel.getJoinResponse()
    Assert.assertNull(mUserUserViewModel.getJoinResponse())
  }

  @Test
  fun `test triggerMeetingLaunch`() {
    mUserUserViewModel.triggerMeetingLaunch("https://pgi.globalmeet.com/pepperbernhardt")
    Assert.assertEquals("https://pgi.globalmeet.com/pepperbernhardt", mUserUserViewModel.meetingFurl)
  }

  @Test
  fun `test isFreemiumEnabled`() {
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val result = mUserUserViewModel.isFreemiumEnabled()
    Assert.assertFalse(result)
  }

  @Test
  fun `test isFreemiumEnabled true`() {
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo-dialoutblocked.json"), MeetingRoomInfoResponse::class.java)
    Whitebox.setInternalState(mUserUserViewModel, "meetingRoomInfoResponse", meetingRoomInfoResponse)
    val result = mUserUserViewModel.isFreemiumEnabled()
    Assert.assertTrue(result)
  }

  @Test
  fun `test isFreemiumEnabledWithNULLMRIR`() {
    val result = mUserUserViewModel.isFreemiumEnabled()
    Assert.assertFalse(result)
  }

  @Test
  fun `test setting meetingRoomInfoResponse`() {
    val meetingRoomInfoResponse = Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java)
    mUserUserViewModel.meetingRoomInfoResponse = meetingRoomInfoResponse
    Assert.assertEquals(meetingRoomInfoResponse, mUserUserViewModel.meetingRoomInfoResponse)
  }

  @Test
  fun `test setting meetingJoinResponse`() {
    val joinMeetingResponse = Gson().fromJson(getJson("json/uapi/joinmeetingsuccess.json"), JoinMeetingResponse::class.java)
    mUserUserViewModel.meetingJoinResponse = joinMeetingResponse
    Assert.assertEquals(joinMeetingResponse, mUserUserViewModel.meetingJoinResponse)
  }

  @Test
  fun `test setting authorizeResponse`() {
    val authresponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
    mUserUserViewModel.authResponse = authresponse
    Assert.assertEquals(authresponse, mUserUserViewModel.authResponse)
  }
/*
  @Test
  fun `test updateLastUsedPhoneInDb`() {
    val applicationDao = ApplicationDao.get(CoreApplication.appContext)
    val phoneDao = applicationDao.phoneNumbers
    val phoneListBefore = phoneDao.loadAll()
    mUserUserViewModel.updateLastUsedPhoneInDb("1", "1234567890")
    val phoneListAfter = phoneDao.loadAll()
    Assert.assertNotEquals(phoneListBefore.size, phoneListAfter.size)
  } */

  @Test
  fun `test updateLastUsedPhoneInDbWhenListNotEmpty`() {
    val applicationDao = ApplicationDao.get(CoreApplication.appContext)
    val phoneDao = applicationDao.phoneNumbers
    val phoneListBefore = phoneDao.loadAll()
    mUserUserViewModel.updateLastUsedPhoneInDb("1", "1234567890")
    // specifically add a duplicate to test existing number code
    mUserUserViewModel.updateLastUsedPhoneInDb("1", "1234567890")
    val phoneListAfter = phoneDao.loadAll()
    Assert.assertNotEquals(phoneListBefore.size, phoneListAfter.size)
  }

  @Test
  fun testAdmitUser(){
    mUserUserViewModel.updateUserAdmitDeny(User("123456"), true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun testAdmitAudioUser(){
    mUserUserViewModel.updateAudioUserAdmitDeny(User("123456"), true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun testAdmitAudioUserWithOutId(){
    mUserUserViewModel.updateAudioUserAdmitDeny(User(), true)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun testDenyAudioUser(){
    mUserUserViewModel.updateAudioUserAdmitDeny(User("123456"), false)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun testDenyUser(){
    mUserUserViewModel.updateUserAdmitDeny(User("123456"), false)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test toggleFrictionFree success`() {
    mUserUserViewModel.toggleFrictionFree(true)
    var userFlowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userFlowObserver)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.FRICTION_FREE_ON_SUCCESS)

    mUserUserViewModel.toggleFrictionFree(false)
    userFlowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userFlowObserver)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.FRICTION_FREE_OFF_SUCCESS)
  }

  @Test
  fun `test toggleFrictionFree failed`() {
    mockServer.dispatcher = MockUAPIServerRequestFailDispatcher()

    mUserUserViewModel.toggleFrictionFree(true)
    var userFlowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userFlowObserver)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.FRICTION_FREE_ON_FAILURE)

    mUserUserViewModel.toggleFrictionFree(false)
    userFlowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertNotNull(userFlowObserver)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.FRICTION_FREE_OFF_FAILURE)
  }

  @Test
  fun `test appInForeground`() {
    mUserUserViewModel.appInForeground = false
    Assert.assertFalse(mUserUserViewModel.appForegroundEvent.value!!)
    mUserUserViewModel.appInForeground = true
    Assert.assertTrue(mUserUserViewModel.appForegroundEvent.value!!)
  }

  @Test
  fun `test low bandwidth`() {
    mUserUserViewModel.turnWebcamOff = false
    Assert.assertFalse(mUserUserViewModel.lowBandwidthEvent.value!!)
    mUserUserViewModel.turnWebcamOff = true
    Assert.assertTrue(mUserUserViewModel.lowBandwidthEvent.value!!)
  }

  @Test
  fun `test add conversation`() {
    val participantIDs = arrayOf("123","234")
    mUserUserViewModel.addConversation(participantIDs)
    val userflowObserver = mUserUserViewModel.userFlowStatus.blockingObserve(2)
    //Assert.assertNull(userflowObserver)
  }

  @Test
  fun `test user type`() {
    mUserUserViewModel.userType = "GUEST"
    Assert.assertEquals("GUEST", mUserUserViewModel.userType)
    mUserUserViewModel.userType = null
    Assert.assertNull(mUserUserViewModel.userType)
  }

  @Test
  fun `test set user role`() {
    val user = User( roomRole = AppConstants.GUEST, firstName = "Test", lastName = "Account",isSelf = true, initials = "#", id = "12345")
    mUserUserViewModel.setUserRole(user , ApplicationProvider.getApplicationContext())
    Assert.assertEquals("Guest", mUserUserViewModel.userType)
    mUserUserViewModel.userType = null
    Assert.assertNull(mUserUserViewModel.userType)
    val user1 = User( roomRole = "HOST", firstName = "Test", lastName = "Account",isSelf = true, initials = "#", id = "12345")
    mUserUserViewModel.setUserRole(user1 , ApplicationProvider.getApplicationContext())
    Assert.assertEquals("Host", mUserUserViewModel.userType)
    val user2 = User( roomRole = "HOST", firstName = "Test", lastName = "Account",isSelf = true, initials = "#", id = "12345",delegateRole = true)
    mUserUserViewModel.setUserRole(user2 , ApplicationProvider.getApplicationContext())
    Assert.assertEquals("Co-Host", mUserUserViewModel.userType)
    val user3 = User( roomRole = "PRESENTER", firstName = "Test", lastName = "Account",isSelf = true, initials = "#", id = "12345",delegateRole = true)
    mUserUserViewModel.setUserRole(user3 , ApplicationProvider.getApplicationContext())
    Assert.assertEquals("Presenter", mUserUserViewModel.userType)
  }

  @Test
  fun `test mixPanel private chat log`() {
    mUserUserViewModel.logMixPanelEnableDisablePrivateChat(false)
    Assert.assertEquals(AppConstants.DISABLE_PRIVATE_CHAT, mUserUserViewModel.mlogger.mixpanelManagePrivateChat.privateChatAction)
    mUserUserViewModel.logMixPanelEnableDisablePrivateChat(true)
    Assert.assertEquals(AppConstants.ENABLE_PRIVATE_CHAT, mUserUserViewModel.mlogger.mixpanelManagePrivateChat.privateChatAction)
  }

  @Test
  fun `test addUsersList `() {
    mUserUserViewModel.mUsersList.add(User(profileImage = "",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.addUsersList(mUserUserViewModel.mUsersList)
    Assert.assertNotNull( mUserUserViewModel.mUsersList)
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.addUsersList(mUserUserViewModel.mUsersList)
    Assert.assertNotNull((mUserUserViewModel.mUsersList))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.addUsersList(mUserUserViewModel.mUsersList)
    Assert.assertNotNull((mUserUserViewModel.mUsersList))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.addUsersList(mUserUserViewModel.mUsersList)
    Assert.assertNotNull((mUserUserViewModel.mUsersList))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "https://test",initials = "#",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = null,initials = null,timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.mUsersList.add(User(profileImage = "",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
    mUserUserViewModel.addUsersList(mUserUserViewModel.mUsersList)
    Assert.assertNotNull((mUserUserViewModel.mUsersList))
    }

  @Test
  fun ` test select private chat logs to mixpanel` () {
      mUserUserViewModel.userType = null
      mUserUserViewModel.logMixPanelEventForNewPrivateChat("Individual",null,"ChatFragment")
      Assert.assertNull( mUserUserViewModel.userType)
      mUserUserViewModel.userType = "Co-Host"
      mUserUserViewModel.logMixPanelEventForNewPrivateChat("Individual","Co-Host","ChatFragment")
      Assert.assertEquals("Co-Host", mUserUserViewModel.userType)
      mUserUserViewModel.userType = "Guest"
      mUserUserViewModel.logMixPanelEventForNewPrivateChat("Individual","Guest","ChatFragment")
      Assert.assertEquals("Guest", mUserUserViewModel.userType)
      mUserUserViewModel.userType = null
      mUserUserViewModel.logMixPanelEventForNewPrivateChat("Group",null,"ChatFragment")
      Assert.assertNull( mUserUserViewModel.userType)
  }

  @Test
  fun `test conversation already exist list`() {
    mUserUserViewModel.createdConversationList.clear()
    mUserUserViewModel.createdConversationList.add("12345")
    Assert.assertTrue(mUserUserViewModel.checkConversationCreated("12345"))
    Assert.assertFalse(mUserUserViewModel.checkConversationCreated("123879"))
    Assert.assertEquals(mUserUserViewModel.createdConversationList.size, 2)
  }

    @Test
    fun `test private chat locked`() {
        Assert.assertNull(mUserUserViewModel.privateChatLocked.value)
        mUserUserViewModel.isPrivateChatLocked = true
        Assert.assertEquals(true, mUserUserViewModel.privateChatLocked.value)
        Assert.assertEquals(true, mUserUserViewModel.isPrivateChatLocked)
        mUserUserViewModel.isPrivateChatLocked = false
        Assert.assertNotNull(mUserUserViewModel.privateChatLocked.value)
    }

  @Test
  fun `test private chat version toast list`() {
    mUserUserViewModel.privateChatVersionToastList.clear()
    mUserUserViewModel.privateChatVersionToastList.add("12345")
    Assert.assertTrue(mUserUserViewModel.checkPrivateChatVersionToastShown("12345"))
    Assert.assertFalse(mUserUserViewModel.checkPrivateChatVersionToastShown("123879"))
  }
}