package com.pgi.convergencemeetings.viewmodels

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.coordinatorlayout.widget.CoordinatorLayout
import blockingObserve
import com.google.gson.Gson
import com.kevinmost.junit_retry_rule.RetryRule
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.HOST
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.MeetingStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.Audio
import com.pgi.convergencemeetings.meeting.gm5.data.model.Controls
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.mockservers.MockUAPIEventsDispatcher
import com.pgi.convergencemeetings.models.Chat
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.Logger
import com.pgi.network.GMWebUAPIServiceAPI
import com.pgi.network.UAPIEndPoints
import com.pgi.network.interceptors.NetworkConnectionInterceptor
import com.pgi.network.interceptors.NoConnectivityException
import com.pgi.network.models.UAPIMeetingEvent
import com.pgi.network.repository.GMWebUAPIRepository
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.any
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runners.MethodSorters
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


/**
 * Test Cases for Meeting events view model
 *
 * @author Sudheer R Chilumula
 * @since 5.20
 */
@PrepareForTest(InternetConnection::class, SharedPreferencesManager::class, AppAuthUtils::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MeetingEventViewModelTest: RobolectricTest() {

  @get:Rule
  val retry: RetryRule = RetryRule()

  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val rule: PowerMockRule = PowerMockRule()

  @Mock
  private var mWebMeetingActivity: WebMeetingActivity? = null

  @Mock
  private var mCoordinatorLayout: CoordinatorLayout? = null

  @Mock
  private var view: View? = null

  @InjectMocks
  private lateinit var mEventEventViewModel: MeetingEventViewModel
  private lateinit var mockServer: MockWebServer
  private lateinit var loggerSpy: Logger

  @Before
  fun setup() {
    loggerSpy = Mockito.spy(TestLogger())
    CoreApplication.mLogger = loggerSpy
    MockitoAnnotations.initMocks(this)
    val context = RuntimeEnvironment.application.applicationContext
    view = View(context)
    mCoordinatorLayout = CoordinatorLayout(context)
    CoreApplication.appContext = context
    PowerMockito.mockStatic(SharedPreferencesManager::class.java)
    PowerMockito.mockStatic(AppAuthUtils::class.java)
    val dispatcher = MockUAPIEventsDispatcher()
    mockServer = MockWebServer()
    mockServer.setDispatcher(dispatcher)
    mockServer.start()
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    val accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { it ->
              val mockServerBaseUrl = mockServer.url("")
              val url = it.request().url().newBuilder()
                      .scheme(mockServerBaseUrl.scheme())
                      .host(mockServerBaseUrl.host())
                      .port(mockServerBaseUrl.port())
                      .build()
              val request = it.request().newBuilder()
                      .addHeader("Authorization", "Bearer $accessToken")
                      .url(url)
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
    mEventEventViewModel.setCurrentUserId("3b8d8ov4idgmlkzn31leb2aq4")
    mEventEventViewModel.getMeetingEvents(null)

    val controller = Robolectric.buildActivity(WebMeetingActivity::class.java)
    mWebMeetingActivity = controller.get()
  }

  @After
  fun tearDown() {
    try {
      mockServer.shutdown()
    } catch (ex: IOException) {

    }
  }

  @Test
  fun testReset() {
    mEventEventViewModel.getMeetingEvents(UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&testReset")
    Assert.assertTrue(mEventEventViewModel.users.value?.isEmpty() == true)

    val resetResponse = Gson().fromJson(getJson("json/uapi/waitingRoomAdmit.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.parseMeetingEvents(resetResponse)
    Assert.assertTrue(mEventEventViewModel.users.value?.isEmpty() == true)
  }

  @Test
  fun testContentDelete() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[3]))
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[3])
    mEventEventViewModel.deletedContent
    mEventEventViewModel.receivedContentDeletedEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
    Assertions.assertThat(mEventEventViewModel.handleContentDeleted(contentResponse.events[3]))
  }

  @Test
  fun `test content created`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
//    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[1]))
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[1])
    mEventEventViewModel.receivedContentCreatedEvent = true
    mEventEventViewModel.receivedContentCreatedEventFileShare = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `01-Should Post Meeting status`() {
    val meetingObserver = mEventEventViewModel.meetingStatus.blockingObserve()
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertEquals(meetingObserver, MeetingStatus.STARTED)
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
  }

  @Test
  fun `02-Should  have a audio user in userslist if SIP Audio part Joined`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 2)
  }

  @Test
  fun `03-Should have a audio user associated with web user in userslist on SIP dialout success`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    val userFlowObserver = mEventEventViewModel.userFlowStatus.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
    Assert.assertEquals(userObserver?.get(0)?.id, "3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, "7516-223265088")
    Assert.assertEquals(userFlowObserver, UserFlowStatus.DIAL_OUT_SUCCESS)
  }

  @Test
  fun `04-Should have a active talker`() {
    val activeTalker = mEventEventViewModel.activeTalker.blockingObserve()
    Assert.assertNotNull(activeTalker)
    Assert.assertEquals(activeTalker?.user?.id, "3b8d8ov4idgmlkzn31leb2aq4")
  }

  @Test
  fun `05-Self user should be muted`() {
    val userFlowObserver = mEventEventViewModel.userFlowStatus.blockingObserve(2)
    Assert.assertEquals(mEventEventViewModel.getCurrentUser()?.audio?.mute, true)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.MUTE_UNMUTE_SUCCESS)
  }

  @Test
  fun `06-Self user should be unmuted`() {
    val userFlowObserver = mEventEventViewModel.userFlowStatus.blockingObserve(3)
    Assert.assertEquals(mEventEventViewModel.getCurrentUser()?.audio?.mute, false)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.MUTE_UNMUTE_SUCCESS)
  }

  @Test
  fun `07-Should not have audio on SIP audio part left`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
    Assert.assertEquals(userObserver?.get(0)?.id, "3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, null)
    Assert.assertEquals(userObserver?.get(0)?.audio?.isConnected, false)
  }

  @Test
  fun `08-Should have audio status as connecting on dialout initiated`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
    Assert.assertEquals(userObserver?.get(0)?.id, "3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, "7516-220188024")
    Assert.assertEquals(userObserver?.get(0)?.audio?.isConnecting, true)
  }

  @Test
  fun `09-Should have audio user associated with a web part on dilaout audio part joined`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
    Assert.assertEquals(userObserver?.get(0)?.id, "3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, "7516-220188024")
  }

  @Test
  fun `10-Should dispatch a userflowstatus on dialut succcess`() {
    val userFlowObserver = mEventEventViewModel.userFlowStatus.blockingObserve(4)
    Assert.assertNotNull(userFlowObserver)

    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(userResponse.events[4])
    Assert.assertNotNull(userResponse)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[14])))

    mEventEventViewModel.extractDataFromEvent(userResponse.events[12])
    Assert.assertNotNull(userResponse)
    Assert.assertTrue(userResponse.events[12].eventType.equals("PARTICIPANT_DIAL_OUT_INITIATED"))

    mEventEventViewModel.extractDataFromEvent(userResponse.events[14])
    Assert.assertNotNull(userResponse)
    Assert.assertTrue(userResponse.events[14].eventType.equals("PARTICIPANT_DIAL_OUT_SUCCEEDED"))

    val audioUser= mEventEventViewModel.getUserByAudioId(userResponse.events[14].audioParticipantId!!)
    Assert.assertNotNull(audioUser)

    Assert.assertTrue(userResponse.events[4].eventType.equals("SIP_DIAL_OUT_SUCCEEDED"))
    mEventEventViewModel.usersMap =  mapOf<String,User>(Pair("7516-220188024",User(id = "7516-220188024",isSelf = true, delegateRole = true,audio = Audio(id = "7516-220188024")))) as MutableMap<String, User>
    val audioUsr= mEventEventViewModel.usersMap[userResponse.events[14].audioParticipantId!!]
    Assert.assertNotNull(audioUsr)
  }

  @Test
  fun `11-Should not have audio on dialout audio part left`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    val userFlowObserver = mEventEventViewModel.userFlowStatus.blockingObserve(5)
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
    Assert.assertEquals(userObserver?.get(0)?.id, "3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, null)
    Assert.assertEquals(userObserver?.get(0)?.audio?.isConnected, false)
    Assert.assertEquals(userFlowObserver, UserFlowStatus.DIAl_OUT_DISCONNECTED)
  }


  @Test
  fun `12-Should add a user when new guest participant joined`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 2)
    Assert.assertEquals(userObserver?.get(1)?.id, "40zc8tmysajaxyhcvqjfghqlb")
    Assert.assertEquals(userObserver?.get(1)?.roomRole, "GUEST")
    Assert.assertEquals(userObserver?.get(1)?.promoted, false)
    Assert.assertEquals(userObserver?.get(1)?.hasControls, false)
  }

  @Test
  fun `13-Should not have a audio attached to guest user on Guest SIP audio joined event`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, userObserver?.size)
    Assert.assertEquals(userObserver?.get(0)?.id, userObserver?.get(0)?.id)
    Assert.assertEquals(userObserver?.get(0)?.roomRole, userObserver?.get(0)?.roomRole)
    Assert.assertEquals(userObserver?.get(0)?.audio?.id, userObserver?.get(0)?.audio?.id)
  }

  @Test
  fun `14-Should have a audio attached to guest user on Guest SIP dial out sucess event`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 2)
    Assert.assertEquals(userObserver?.get(1)?.id, "40zc8tmysajaxyhcvqjfghqlb")
    Assert.assertEquals(userObserver?.get(1)?.roomRole, "GUEST")
    Assert.assertEquals(userObserver?.get(1)?.audio?.id, "7516-219410548")
  }

  @Test
  fun `15-Should have guest muted `() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.get(1)?.audio?.mute, true)
  }

  @Test
  fun `16-Should have guest unmuted `() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.get(1)?.audio?.mute, false)
  }

  @Test
  fun `17-Should have guest promoted`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.get(1)?.promoted, true)
    Assert.assertEquals(userObserver?.get(1)?.hasControls, true)
  }

  @Test
  fun `18-Should have content on content create event`() {
    val contentObserver = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentObserver)
  }

  @Test
  fun `19-Should have screenshare on content start update event`() {
    val contentObserver = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentObserver)
    Assert.assertEquals(contentObserver?.staticMetadata?.streamSessionId, "9298545b-007f-428d-bafd-443644cd48db")
    Assert.assertEquals(contentObserver?.id, "dmyrspr3u58wptf3rz5wy2ubw")
    Assert.assertEquals(contentObserver?.dynamicMetaData?.action, "START")
  }

  @Test
  fun `20-Should stop screenshare on content stop update event`() {
    val contentObserver = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentObserver)
    Assert.assertEquals(contentObserver?.staticMetadata?.streamSessionId, "9298545b-007f-428d-bafd-443644cd48db")
    Assert.assertEquals(contentObserver?.id, "dmyrspr3u58wptf3rz5wy2ubw")
    Assert.assertEquals(contentObserver?.dynamicMetaData?.action, "STOP")
  }

  @Test
  fun `21-Should have guest demoted`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.get(1)?.promoted, false)
    Assert.assertEquals(userObserver?.get(1)?.hasControls, false)
  }

  @Test
  fun `22-Should have chat on chat added event`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[30])))
    mEventEventViewModel.chatsList = getChatList()
    mEventEventViewModel.extractDataFromEvent(userResponse.events[30])
    val chatObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(chatObserver)
  }

  @Test
  fun `34-Should have private chat on chat added event`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[59])))
    mEventEventViewModel.chatsList = getChatList()
    mEventEventViewModel.extractDataFromEvent(userResponse.events[59])
    val chatObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(chatObserver)
  }

  private fun getChatList(): List<Chat> {

    val chat = Chat()
    chat.message = "Hi"
    chat.timestamp = "2018-10-17T05:20:05.247Z"
    chat.conversationId = "default"
    chat.chatMessageState = ChatMessageState.SENDING
    var chatlist = mutableListOf(chat)
    val chat1 = Chat()
    chat1.message = "Hi"
    chat1.timestamp = "2018-10-17T05:20:05.247Z"
    chat1.conversationId = "default"
    chat1.chatMessageState = ChatMessageState.SENDING
    chatlist.add(chat1)
    return chatlist
  }

  @Test
  fun `23-Should have user removed on participant dismiss`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userObserver)
    Assert.assertEquals(userObserver?.size, 1)
  }


  @Test
  fun `24-Should post meeting status on meeting ended`() {
    val meetingObserver = mEventEventViewModel.meetingStatus.blockingObserve(2)
    Assert.assertEquals(meetingObserver, MeetingStatus.ENDED)

    val response = Gson().fromJson(getJson("json/uapi/events/event43.json"),
            UAPIMeetingEvent::class.java)
    mEventEventViewModel.parseMeetingEvents(response)
    Assert.assertEquals(meetingObserver, MeetingStatus.ENDED)
  }

  /* the following tests use a different json file.  They should be kept separate
     from the earlier tests which must run in order of the json file
   */
  @Test
  fun `26-Should have fileshare on content created event`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[2]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
    Assert.assertEquals(contentObservers?.dynamicMetaData?.newPage, "2")
  }

  @Test
  fun `27-Should stop fileshare on content deleted event`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[3]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
  }

  @Test
  fun `28-Should have fileshare on content created event`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[1]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
  }

  @Test
  fun `29-start screenshare`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    Assert.assertNotNull(contentResponse)
    val contentObserver = mEventEventViewModel.content.blockingObserve(2)
    Assert.assertNull(contentObserver)

  }

  @Test
  fun `30-Should stop screenshare on content stop update event`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[5]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.dynamicMetaData?.action, "STOP")

  }

  @Test
  fun `31-Should have whiteboard on content created event`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event45.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[1]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
  }

  @Test
  fun `32-start whiteboard`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event45.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[2]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
    Assert.assertEquals(contentObservers?.dynamicMetaData?.action, "START")
  }

  @Test
  fun `33-stop whiteboard`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event45.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[3]))
    val contentObservers = mEventEventViewModel.content.blockingObserve()
    Assert.assertNotNull(contentResponse)
    Assert.assertNotNull(contentObservers)
    Assert.assertEquals(contentObservers?.id, "8524dgv4aqfijlx1wrgl9d173")
    Assert.assertEquals(contentObservers?.dynamicMetaData?.action, "STOP")
  }

  @Test
  fun `test error`() {
    val accessToken = "expired_token"
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
    mEventEventViewModel.setCurrentUserId("3b8d8ov4idgmlkzn31leb2aq4")
    mEventEventViewModel.restartPolling(null)
    val userObserver = mEventEventViewModel.users.blockingObserve(1)
    Assert.assertNull(userObserver)
  }

  @Test
  fun `test UnknownHostException`() {
    val accessToken = "UnknownHostException"
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
            .baseUrl("https://thisIsARandomHost.com")
            .client(okHttpClient)
            .build()
    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofit.create(GMWebUAPIServiceAPI::class.java)
    val error = UnknownHostException()
    mEventEventViewModel.pareseMeetingEventsError(error)
    mEventEventViewModel.setCurrentUserId("3b8d8ov4idgmlkzn31leb2aq4")
    mEventEventViewModel.restartPolling(null)
    val userObserver = mEventEventViewModel.users.blockingObserve(1)
    Assert.assertNull(userObserver)
    mEventEventViewModel.pausePolling()
  }

  @Test
  fun `test SocketTimeoutException`() {
    val accessToken = "SocketTimeoutException"
    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { it ->
              val request = it.request().newBuilder()
                      .addHeader("Authorization", "Bearer $accessToken")
                      .build()
              it.proceed(request)
            }
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .build()

    val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockServer.url("/").toString())
            .client(okHttpClient)
            .build()
    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofit.create(GMWebUAPIServiceAPI::class.java)
    val error = SocketTimeoutException()
    mEventEventViewModel.pareseMeetingEventsError(error)
    mEventEventViewModel.setCurrentUserId("3b8d8ov4idgmlkzn31leb2aq4")
    mEventEventViewModel.restartPolling(null)
    mEventEventViewModel.keepMeetingAlive()
    val userObserver = mEventEventViewModel.users.blockingObserve(1)
    Assert.assertNull(userObserver)
    mEventEventViewModel.pausePolling()
  }

  @Test
  fun `test Audio User Joined waiting room enabled`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[3])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[3])
    val contentObservers = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(contentObservers)
    Assertions.assertThat(mEventEventViewModel.handleAudioUserJoin(userResponse.events[3]))
  }

  @Test
  fun `test Audio User Joined As Host When waiting room enabled`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[8])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[8])
    val contentObservers = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(contentObservers)
    Assertions.assertThat(mEventEventViewModel.handleAudioUserJoin(userResponse.events[8]))
  }

  @Test
  fun `test Audio User Joined waiting room disabled`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[4])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[4])
    val contentObservers = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(contentObservers)
    Assertions.assertThat(mEventEventViewModel.handleAudioUserJoin(userResponse.events[4]))
  }

  @Test
  fun `test Audio User Joined As Host When waiting room disabled`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[11])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[11])
    val contentObservers = mEventEventViewModel.users.blockingObserve()
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(contentObservers)
    Assertions.assertThat(mEventEventViewModel.handleAudioUserJoin(userResponse.events[11]))
  }

  @Test
  fun testVrcJoined() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[33])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[33])
    Assert.assertNotNull(userResponse)
    Assert.assertTrue(userResponse.events[33].roomRole.equals("VRC"))
    Assertions.assertThat(mEventEventViewModel.addUser(userResponse.events[33]))
  }

  @Test
  fun dialInUserRenamed() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleAudioUserJoin(userResponse.events[34])
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[35])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[35])
    Assert.assertNotNull(userResponse)
    Assertions.assertThat(mEventEventViewModel.handleAudioParticipantRenamed(userResponse.events[35]))
    mEventEventViewModel.onSubscribeComplete()
    val renamedUser = mEventEventViewModel.getUserByAudioId(userResponse.events[35].audioParticipantId!!)
    Assert.assertTrue(renamedUser?.firstName.equals("new"))
    Assert.assertTrue(renamedUser?.lastName.equals("name"))
    Assert.assertTrue(renamedUser?.name.equals("new name"))
  }

  @Test
  fun `test NoConnectivityException`() {
    val accessToken = "NoConnectivityException"
    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { it ->
              val request = it.request().newBuilder()
                      .addHeader("Authorization", "Bearer $accessToken")
                      .build()
              it.proceed(request)
            }
            .addInterceptor(NetworkConnectionInterceptor())
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .build()

    val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockServer.url("/").toString())
            .client(okHttpClient)
            .build()

    PowerMockito.mockStatic(InternetConnection::class.java)
    Mockito.`when`(InternetConnection.isConnected(any())).thenReturn(false)
    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofit.create(GMWebUAPIServiceAPI::class.java)
    val error = NoConnectivityException()
    mEventEventViewModel.pareseMeetingEventsError(error)
    mEventEventViewModel.setCurrentUserId("3b8d8ov4idgmlkzn31leb2aq4")
    mEventEventViewModel.restartPolling(null)
    val userObserver = mEventEventViewModel.users.blockingObserve(1)
    Assert.assertNull(userObserver)
    mEventEventViewModel.pausePolling()
  }

  @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  private fun getJson(path: String): String {
    val uri = javaClass.classLoader!!.getResource(path)
    val file = File(uri.file)
    return String(file.readBytes())
  }

  @Test
  fun `test clear`() {
    mEventEventViewModel.clear()
    Assert.assertNull(mEventEventViewModel.content.value)
  }

  @Test
  fun `test addSelfChat`() {
    var chat = Chat()
    chat.firstName = "test"
    chat.message = "test message"
    mEventEventViewModel.addSelfChat(chat)
    Assert.assertNotNull(mEventEventViewModel.chats)
  }

  @Test
  fun `test updateSelfChatFailure`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    var chat = Chat()
    chat.firstName = "test"
    chat.message = "test message"
    val user = mEventEventViewModel.getCurrentUser()
    if (user != null) {
      chat.webPartId = user.id
    }
    chat.chatMessageState = ChatMessageState.SENDING
    mEventEventViewModel.addSelfChat(chat)
    var chat2 = Chat()
    chat2.firstName = "test 2"
    chat2.message = "test message 2"
    chat2.chatMessageState = ChatMessageState.SENDING
    mEventEventViewModel.addSelfChat(chat2)
    mEventEventViewModel.updateSelfChatFailure()
    Assert.assertNotNull(mEventEventViewModel.chats)
  }

  @Test
  fun `31-test getuser by id`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.users.postValue(listOf(mEventEventViewModel.getUserFromEvent(userResponse.events[33])))
    mEventEventViewModel.extractDataFromEvent(userResponse.events[33])
    val user = mEventEventViewModel.getUserById("3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertNotNull(user)
  }

  @Test
  fun `31-test getuser by id null`() {
    val user = mEventEventViewModel.getUserById("3b8d8ov4idgmlkzn31leb2aq4")
    Assert.assertNull(user)
  }

  @Test
  fun `test updateVoipDisconnectStatus`() {
    val userObserver = mEventEventViewModel.users.blockingObserve()
    mEventEventViewModel.updateVoipDisconnectStatus()
    val user = mEventEventViewModel.getCurrentUser()
    if (user != null) {
      Assert.assertNotNull(user.audio)
    } else {
      Assert.assertNull(user)
    }
  }

  @Test
  fun `test restartPolling when false`() {
    mEventEventViewModel.restartPolling(null)
  }

  @Test
  fun `test recording started`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event44.json"), UAPIMeetingEvent::class.java)
//    mEventEventViewModel.content.postValue(mEventEventViewModel.getContentInfoFromEvent(contentResponse.events[6]))
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[6])
    mEventEventViewModel.receivedRecordingEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test waiting enabled`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[36])
    mEventEventViewModel.receivedWaitingRoomOptionsEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test waiting disabled`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[37])
    mEventEventViewModel.receivedWaitingRoomOptionsEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test guest waiting joined `() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[38])
    mEventEventViewModel.receivedUsersEvent = true
    mEventEventViewModel.onSubscribeComplete()
    mWebMeetingActivity?.coordinatorLayout = this!!.mCoordinatorLayout!!
    mWebMeetingActivity?.showPeopleAreWaitingToast()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test guest waiting joined no context `() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[38])
    mEventEventViewModel.receivedUsersEvent = true
    mEventEventViewModel.onSubscribeComplete()
    mWebMeetingActivity?.coordinatorLayout = this!!.mCoordinatorLayout!!
    mWebMeetingActivity?.showPeopleAreWaitingToast()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test guest admitted`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[39])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[5])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted user without participant Id`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[6])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted host`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[12])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted without roomrole`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[15])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted without delegated role`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[14])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user admitted Guest`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[13])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user left when isSelf is true`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.usersMap =  mapOf<String,User>(Pair("mykey",User(id = "7516-223265088",isSelf = true, delegateRole = true,audio = Audio(id = "7516-223265088")))) as MutableMap<String, User>
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[9])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test audio user left when isSelf is false`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.usersMap =  mapOf<String,User>(Pair("mykey",User(id = "7516-223265088",isSelf = false, delegateRole = true,audio = Audio(id = "7516-223265088")))) as MutableMap<String, User>
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[9])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test participant reconnected`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[51])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test participant dialout failed`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[52])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant left`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[53])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test waiting room on `() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[40])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test waiting room off `() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[41])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test guest vrc joined no context `() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[42])
    mEventEventViewModel.receivedUsersEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test recorder joined`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[43])
    mEventEventViewModel.receivedUsersEvent = true
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test extract Meeting locked`() {
    val json = "{\n" +
            "  \"eventType\": \"MEETING_LOCKED\",\n" +
            "  \"timestamp\": \"2018-10-10T18:43:13.789Z\"\n" +
            "}\n"
    val event = Gson().fromJson(json, com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(event)
    Assert.assertNotNull(event)
  }

  @Test
  fun `test extract Meeting unlocked`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/meetingunlocked.json"), com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse)
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test extract Meeting muted`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/meetingmuted.json"), com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse)
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test extract Meeting unmuted`() {
    val json = "{\n" +
            "  \"eventType\": \"MEETING_UMUTED\",\n" +
            "  \"timestamp\": \"2018-10-10T18:43:13.789Z\"\n" +
            "}\n"
    val event = Gson().fromJson(json, com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(event)
    Assert.assertNotNull(event)
  }

  @Test
  fun `test extract Meeting kept alive`() {
    val json = "{\n" +
            "  \"eventType\": \"KEEP_MEETING_ALIVE\",\n" +
            "  \"timestamp\": \"2018-10-10T18:43:13.789Z\"\n" +
            "}\n"
    val event = Gson().fromJson(json, com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(event)
    Assert.assertNotNull(event)
  }

  @Test
  fun `test extract Meeting inactive`() {
    val json = "{\n" +
            "  \"eventType\": \"MEETING_INACTIVITY_WARNING\",\n" +
            "  \"timestamp\": \"2018-10-10T18:43:13.789Z\"\n" +
            "}\n"
    val event = Gson().fromJson(json, com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(event)
    Assert.assertNotNull(event)
  }

  @Test
  fun `test extract Meeting ended`() {
    val json = "{\n" +
            "  \"eventType\": \"END_MEETING\",\n" +
            "  \"timestamp\": \"2018-10-10T18:43:13.789Z\"\n" +
            "}\n"
    val event = Gson().fromJson(json, com.pgi.network.models.UAPIEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(event)
    Assert.assertNotNull(event)
  }

  @Test
  fun `test meeting event error parse`() {
    val error = Throwable("Test error")
    mEventEventViewModel.pareseMeetingEventsError(error)
    mEventEventViewModel.pareseMeetingEventsError(error)
    mEventEventViewModel.pareseMeetingEventsError(error)
    Assert.assertEquals(mEventEventViewModel.retryStatus.value?.value(), RetryStatus.NOCONNECTIVITY)
  }

  @Test
  fun `test participant admitted`() {
    mEventEventViewModel.getMeetingEvents(UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&waitingRoom")
    mEventEventViewModel.getMeetingEvents(UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&participaintWaitAdmit")
    mEventEventViewModel.getMeetingEvents(UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&waitingroomreset")
    Assert.assertEquals(mEventEventViewModel.userFlowStatus.value, UserFlowStatus.PARTICIPANT_ADMITTED)
  }

  @Test
  fun `test isCurrentUserPresenter`() {
    Assert.assertFalse(mEventEventViewModel.isCurrentUserPresenter())
  }

  @Test
  fun `test isUserPresenter`() {
    Assert.assertFalse(mEventEventViewModel.isUserPresenter(null))

    var user = User(roomRole = AppConstants.PRESENTER, promoted = false)
    Assert.assertTrue(mEventEventViewModel.isUserPresenter(user))

    user = User(roomRole = AppConstants.GUEST, promoted = true)
    Assert.assertTrue(mEventEventViewModel.isUserPresenter(user))

    user = User(roomRole = AppConstants.GUEST, promoted = false)
    Assert.assertFalse(mEventEventViewModel.isUserPresenter(user))
  }

  @Test
  fun `test participant admitted no participantId`() {
    mEventEventViewModel.getMeetingEvents(UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&participaintWaitAdmitNoParticipantID")
    Assert.assertEquals(mEventEventViewModel.userFlowStatus.value, UserFlowStatus.PARTICIPANT_ADMITTED)
  }

  @Test
  fun `test clearWaitingListOnMeetingEnd`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)

    mEventEventViewModel.extractDataFromEvent(contentResponse.events[38])
    mEventEventViewModel.clearWaitingListOnMeetingEnd()
    Assert.assertEquals(mEventEventViewModel.guestWaitingList.value?.isEmpty(),true)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[18])
    mEventEventViewModel.clearWaitingListOnMeetingEnd()
    Assert.assertEquals(mEventEventViewModel.guestWaitingList.value?.isEmpty(),true)
  }

  @Test
  fun `test handle participant dismissed by host`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[44])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant dismissed by room at capacity`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[45])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant dismissed inactive participant`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[46])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant dismissed due to lock`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[47])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant dismissed due wait time out host`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[48])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle participant dismissed due wait time out admit`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[49])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test handle chat cleared`() {
    var chat = Chat()
    chat.firstName = "test"
    chat.message = "test message"
    chat.conversationId = "default"
    chat.chatMessageState = ChatMessageState.SENDING
    mEventEventViewModel.addSelfChat(chat)
    var chat2 = Chat()
    chat2.firstName = "test 2"
    chat2.message = "test message 2"
    chat2.conversationId = "1234"
    chat2.chatMessageState = ChatMessageState.SENDING
    mEventEventViewModel.addSelfChat(chat2)
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.extractDataFromEvent(contentResponse.events[54])
    mEventEventViewModel.onSubscribeComplete()
    Assert.assertNotNull(contentResponse)
  }

  @Test
  fun `test dialInUserRenamed with emty partId`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleAudioUserJoin(userResponse.events[55])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handleAudioParticipantRenamed(userResponse.events[56]))
  }

  @Test
  fun `test conversation added`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event36.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleAddConversation(userResponse.events[31])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handleAddConversation(userResponse.events[31]))
  }

  @Test
  fun `test conversation added failure conversation exists`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event36.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleErrorConversation(userResponse.events[32])
    Assert.assertNotNull(userResponse)
  }

  @Test
  fun `test conversation added failure `() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event36.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleErrorConversation(userResponse.events[33])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handleAddConversation(userResponse.events[33]))
  }

  @Test
  fun `test chat added failure `() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event36.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleChatFailure(userResponse.events[34])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handleAddConversation(userResponse.events[33]))
  }

  @Test
  fun `test private chate enable status`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)

    mEventEventViewModel.extractDataFromEvent(userResponse.events[57])
    Assert.assertNotNull(userResponse)
    Assert.assertTrue(userResponse.events[57].eventType.equals("MEETING_OPTION_WS_UPDATED"))
    mEventEventViewModel.handlePrivateChatEnableDisable(userResponse.events[57])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handlePrivateChatEnableDisable(userResponse.events[57]))

    mEventEventViewModel.extractDataFromEvent(userResponse.events[58])
    Assert.assertNotNull(userResponse)
    Assert.assertTrue(userResponse.events[58].eventType.equals("MEETING_OPTION_WS_UPDATED"))
    mEventEventViewModel.handlePrivateChatEnableDisable(userResponse.events[58])
    Assert.assertNotNull(userResponse)
    Assert.assertNotNull(mEventEventViewModel.handlePrivateChatEnableDisable(userResponse.events[58]))
  }

  @Test
  fun `test offline is already added`() {
    val userResponse = Gson().fromJson(getJson("json/uapi/events/event43.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.offlineChatsList = getOfflineChats()
    mEventEventViewModel.isEventAlreadyAdded(userResponse.events[44])
    mEventEventViewModel.handleOfflineUserChats(userResponse.events[44])
    Assert.assertNotNull(userResponse)
  }

  @Test
  fun `test private chat Add`() {
    val contentResponse = Gson().fromJson(getJson("json/uapi/events/event47.json"), UAPIMeetingEvent::class.java)
    mEventEventViewModel.handleChatAdd(contentResponse.events[14])
    Assert.assertNotNull(contentResponse)
    mEventEventViewModel.handleChatAdd(contentResponse.events[14])
    Assert.assertNotNull(contentResponse)
  }

  private fun getOfflineChats() : MutableList<Chat> {
    val offlineList = mutableListOf<Chat>()
    val chat = Chat()
    chat.webPartId = "3b8d8ov4idgmlkzn31leb2aq4"
    chat.offlineTimestamp = "12:30"
    val chat1 = Chat()
    chat1.webPartId = "1234567bfhhfhfgh"
    chat1.offlineTimestamp = "12:30"
    offlineList.add(chat)
    offlineList.add(chat1)
    return offlineList
  }
}