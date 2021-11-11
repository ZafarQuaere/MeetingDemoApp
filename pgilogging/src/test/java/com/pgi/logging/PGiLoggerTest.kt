package com.pgi.logging

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT
import com.pgi.logging.enums.Mixpanel
import com.pgi.logging.model.ElkLogItem
import com.pgi.logging.model.RealmMap
import com.pgi.logging.model.SortedElkLogItem
import io.mockk.*
import io.realm.Realm
import io.realm.RealmAsyncTask
import io.realm.RealmConfiguration
import io.realm.RealmQuery
import io.realm.internal.RealmCore
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PGiLoggerTest {

  private lateinit var mContext: Context
  private lateinit var mlogger: PGiLogger
  private lateinit var mloggerSpy: PGiLogger
  private lateinit var mockRealm: Realm
  private lateinit var mockRealmConfigBuilder: RealmConfiguration.Builder
  private val tag = PGiLoggerTest::class.java.simpleName
  private var MANAGE_WEBCAM_FEED = "Manage Webcam Feed"
  private var ENABLE_WEBCAM = "Enable Webcam"
  private var GOOD = "Good"
  private var ELEVEN_POINT_SEVEN = "11.7"
  private var SEVENTEEN = "17"
  private var TESTCLINETID = "testClientId"
  private var NINE_EIGHT_NUMBER = "987654"
  private var GUEST = "GUEST"
  private var HOST = "HOST"
  private val MEETING_ID = "987654"
  private val EMPTY_STRING = ""
  private val TEXT_ONLY = "Text-Only"
  private val TEXT_GROUP = "Group"
  private val TEXT_ENABLE_PRIVATE_CHAT = "Enable Private Chat"
  private val TEXT_DISABLE_PRIVATE_CHAT = "Disable Private Chat"

  private val ACTION_SOURCE = "com.test.message"
  private val OUT_MEETING_SHARE = "Out of Meeting Share"
  private val MIXPANEL_EVENT = "Mixpanel Event: "
  @Before
  fun `set up`() {
    mContext = ApplicationProvider.getApplicationContext<Context>()
    MockKAnnotations.init(this, relaxUnitFun = true)
    // Setup Realm + Mock
    mockkStatic(RealmCore::class)
    mockkStatic(Realm::class)
    mockkStatic(RealmConfiguration::class)
    mockkStatic(RealmConfiguration.Builder::class)
    mockkStatic(RealmAsyncTask::class)
    mockkStatic(RealmQuery::class)
    mockRealm = mockk<Realm>()
    val mockRealmConfig = mockk<RealmConfiguration>()
    val mockRealmAsyncTask = mockk<RealmAsyncTask>()
    val mockrealQuery = mockk<RealmQuery<SortedElkLogItem>>()
    every { RealmCore.loadLibrary(any()) } just Runs
    every { Realm.getInstance(any()) } returns mockRealm
    every { mockRealm.close() } just Runs

    val sortedElkLogItem = SortedElkLogItem()
    val elkLogItem = ElkLogItem()
    val map = RealmMap()
    map.key = "meeting_id"
    map.value = "1232344"
    elkLogItem.customFields.add(map)
    sortedElkLogItem.logItems.add(elkLogItem)
    mlogger = PGiLogger(mContext)
    mloggerSpy = spyk(mlogger, "mloggerSpy", recordPrivateCalls = true)
    every { mockRealm.executeTransaction(any()) } just Runs
    every { mockRealm.executeTransactionAsync(any()) } returns mockRealmAsyncTask
    every { mockRealm.where(SortedElkLogItem::class.java) } returns mockrealQuery
    every { mockrealQuery.findFirst() } returns sortedElkLogItem
    every { mockRealm.copyFromRealm(any<List<ElkLogItem>>()) } returns sortedElkLogItem
        .logItems.toMutableList()
  }

  @Test
  fun `test logger is not null`() {
    assertThat(mloggerSpy).isNotNull
  }

  @Test
  fun `test logging info`() {
    mloggerSpy.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Info")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging verbose`() {
    mloggerSpy.verbose(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Verbose")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging debug`() {
    mloggerSpy.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Debug")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging warn`() {
    mloggerSpy.warn(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Debug")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging error`() {
    mloggerSpy.error(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Error",
        Throwable("Tetsing"))
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with meeting model`() {
    mloggerSpy.meetingModel.furl = "https://pgi.globalmeet.com/test"
    mloggerSpy.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT, "Testing Info")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with join meeting event for Mixpanel`() {
    mloggerSpy.meetingModel.furl = "https://pgi.globalmeet.com/test"
    mloggerSpy.logBaseMsg.clientId = "testClientId"
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_MEETING, "testing mixpanel join event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with join meeting event for Mixpanel with values set`() {
    mloggerSpy.meetingModel.furl = "https://pgi.globalmeet.com/test"
    mloggerSpy.meetingModel.hostCompanyId = "12345"
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.logBaseMsg.clientId = "testClientId"
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_MEETING, "testing mixpanel join event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel LogIn event`() {
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOG_IN, "test mixpanel login event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Join Audio event with dial_in`() {
    mloggerSpy.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DIAL_IN.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, "test mixpanel join audio event with dial in",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Join Audio event with dial_out`() {
    mloggerSpy.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DIAL_OUT.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, "test mixpanel join audio event with dial out",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Join Audio event with VOIP`() {
    mloggerSpy.attendeeModel.audioConnectionType = Mixpanel.AUDIO_VOIP.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, "test mixpanel join audio event with VOIP",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Join Audio event with do not connect audio`() {
    mloggerSpy.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DO_NOT_CONNECT.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, "test mixpanel join audio event do not connect audio",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Record event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_RECORD, "test mixpanel record event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Mute Guests event`(){
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MUTE_GUESTS, "test mixpanel mute guests event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Lock Meeting event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOCK_MEETING, "test mixpanel lock meeting event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Lock Meeting event null`() {
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOCK_MEETING, "test mixpanel lock meeting event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }
  @Test
  fun `test logging info with Mixpanel Unlock Meeting event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNLOCK_MEETING, "test mixpanel unlock meeting event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Unmute Guests event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.userModel.role = Mixpanel.MEETING_HOST.value
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNMUTE_GUESTS, "test mixpanel unmute guests event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Open App event`() {
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_OPEN_APP, "Open App",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Open App event Blank`() {
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_OPEN_APP, "Open App",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Unmute Guests event without data`() {
    mloggerSpy.meetingModel.uniqueMeetingId = ""
    mloggerSpy.userModel.role = ""
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNMUTE_GUESTS, "test mixpanel unmute guests event without data",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Enable Integration event`() {
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_ENABLE_INTEGRATION, "test mixpanel enable integration event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Dismiss Home card event`() {
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_DISMISS_HOME_CARD, "test mixpanel dismiss home card event",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Spotlight Webcam event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = "987654"
    mloggerSpy.attendeeModel.role = "HOST"
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingState.maxCamerasPublished = 1
    mloggerSpy.meetingState.currentCamerasSubcribed = 1
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT, "Spotlight webcam",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Spotlight Webcam event guest`() {
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.meetingModel.hostCompanyId = null
    mloggerSpy.logBaseMsg.clientId = "testClientId"
    mloggerSpy.attendeeModel.role = "GUEST"
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingState.maxCamerasPublished = 1
    mloggerSpy.meetingState.currentCamerasSubcribed = 1
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT, "Spotlight webcam",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Spotlight Webcam event null params`() {
    mloggerSpy.meetingModel.hostCompanyId = null
    mloggerSpy.logBaseMsg.clientId = "testClientId"
    mloggerSpy.attendeeModel.role = "HOST"
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.maxCamerasPublished = 1
    mloggerSpy.meetingState.currentCamerasSubcribed = 1
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, MIXPANEL_WEBCAM_SPOTLIGHT, "Spotlight webcam",
        null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }


  @Test
  fun `test logging info with Mixpanel Waiting Room Controls event`() {
    mloggerSpy.mixpanelMeetingSecurityModel.waitingRoomAction = "Open Meeting Security"
    mloggerSpy.mixpanelMeetingSecurityModel.numGuestsWaiting = 4
    mloggerSpy.mixpanelMeetingSecurityModel.numGuestsAdmitted = 0
    mloggerSpy.mixpanelMeetingSecurityModel.numGuestsDenied = 0
    mloggerSpy.mixpanelMeetingSecurityModel.guestsAdmitted = null
    mloggerSpy.mixpanelMeetingSecurityModel.guestsDenied = null

    mloggerSpy.meetingModel.uniqueMeetingId = "12345"
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_MEETING_SECURITY, "Waiting Room Controls",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }

    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_MEETING_SECURITY, "Waiting Room Controls",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Network change event  `() {
    mloggerSpy.mixpanelNetworkChangeModel.networkType = "Cellular"
    mloggerSpy.mixpanelNetworkChangeModel.mobileCountryCode = 311
    mloggerSpy.mixpanelNetworkChangeModel.mobileNetworkCode = 480

    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_NETWORK_CHANGE, "Network Change",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Mixpanel Network change event null params`() {
    mloggerSpy.mixpanelNetworkChangeModel.networkType = null
    mloggerSpy.mixpanelNetworkChangeModel.mobileCountryCode = null
    mloggerSpy.mixpanelNetworkChangeModel.mobileNetworkCode = null
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_NETWORK_CHANGE, "Network Change",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }


  @Test
  fun `test logging start metric`() {
    mloggerSpy.startMetric(LogEvent.METRIC_SIP_CONNECT)
    mloggerSpy.endMetric(LogEvent.METRIC_SIP_CONNECT, "Test metric")
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test base  model`(){
    mloggerSpy.addModelTest(ElkLogItem())
  }
  
  @Test
  fun `test logging record metric`() {
    mloggerSpy.record(LogEvent.METRIC_SIP_CONNECT)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging getLogs and count`() {
    val result = mloggerSpy.getlogsAndCount(mockRealm, 20)
    verify { mockRealm.where(SortedElkLogItem::class.java)}
    assertThat(result.second).isEqualTo(1)
  }

  @Test
  fun `test logging deletelogs`() {
    mloggerSpy.deleteLogs(10, mockRealm)
    verify { mockRealm.executeTransaction(any())}
  }

  @Test
  fun `test logging deletelogs and throw exception`() {
    val exception = Exception("test exception")
    every { mockRealm.executeTransaction(any()) } throws exception
    mloggerSpy.deleteLogs(10, mockRealm)
    verify { mockRealm.executeTransaction(any())}
  }

  @Test
  fun `test logging clearMeetingModel`() {
    mloggerSpy.clearMeetingModel()
    assertThat(mloggerSpy.meetingModel.furl).isNull()
  }

  @Test
  fun `test logging clearModels`() {
    mloggerSpy.clearModels()
    assertThat(mloggerSpy.attendeeModel.participantId).isNull()
    assertThat(mloggerSpy.meetingModel.furl).isNull()
  }

  @Test
  fun `test logging deleteAlllogs`() {
    mloggerSpy.deleteAllLogs()
    verify { mockRealm.executeTransactionAsync(any())}
  }

  @Test
  fun `test set userid`() {
    mloggerSpy.setUserId("123456")
    assertThat(mloggerSpy.userModel.id).isEqualTo("123456")
  }

  @Test
  fun `test set email`() {
    mloggerSpy.setEmail("test@pgi.com")
    assertThat(mloggerSpy.userModel.email).isEqualTo("test@pgi.com")
  }

  @Test
  fun `test set email guest`() {
    mloggerSpy.setUserId(null)
    mloggerSpy.setEmail("test@pgi.com")
    assertThat(mloggerSpy.userModel.email).isEqualTo("test@pgi.com")
    assertThat(mloggerSpy.userModel.id).isEqualTo("test@pgi.com")
  }


  @Test
  fun `test set email host`() {
    mloggerSpy.setUserId("12345")
    mloggerSpy.setEmail("test@pgi.com")
    assertThat(mloggerSpy.userModel.id).isEqualTo("12345")
    assertThat(mloggerSpy.userModel.email).isEqualTo("test@pgi.com")
  }

  @Test
  fun `test set email null id`() {
    mloggerSpy.setEmail(null)
    assertThat(mloggerSpy.userModel.id).isEqualTo(null)
    assertThat(mloggerSpy.userModel.email).isEqualTo(null)
  }

  @Test
  fun `test loadSavedLogCount with null prefs`(){
    mloggerSpy.mPrefs = null
    mloggerSpy.loadSavedLogCount()
    assertThat(mloggerSpy.userModel).isNotNull()
  }

  @Test
  fun `test loadSavedLogCount`() {
    var sharedPrefSpy = mContext.getSharedPreferences("LOG_STATE", Context.MODE_PRIVATE)
    sharedPrefSpy = spyk(sharedPrefSpy, "sharedPrefSpy", recordPrivateCalls = true)
    mloggerSpy.mPrefs = sharedPrefSpy
    mloggerSpy.userModel.logCount = 123
    mloggerSpy.saveLogCount()
    mloggerSpy.loadSavedLogCount()
    assertThat(mloggerSpy.userModel).isNotNull()
  }

  @Test
  fun `test saveLogCount`() {
    var sharedPrefSpy = mContext.getSharedPreferences("LOG_STATE", Context.MODE_PRIVATE)
    sharedPrefSpy = spyk(sharedPrefSpy, "sharedPrefSpy", recordPrivateCalls = true)
    mloggerSpy.mPrefs = sharedPrefSpy
    mloggerSpy.userModel.logCount = 123
    mloggerSpy.saveLogCount()
    assertThat(mloggerSpy.userModel).isNotNull()
  }

  @Test
  fun `test addAnyBackLoggedItems`() {
    var logItem = ElkLogItem()
    logItem.id ="log id 1"
    logItem.message = "test log message"
    mloggerSpy.backLogList.add(logItem)
    mloggerSpy.addAnyBackLoggedItems()
    verify { mockRealm.executeTransactionAsync(any())}
  }

  @Test
  fun `test saveLogItem`() {
    var logItem = ElkLogItem()
    logItem.id ="log id 1"
    logItem.message = "test log message"
    mloggerSpy.backLogList.add(logItem)
    var logItem2 = ElkLogItem()
    logItem2.id ="log id 2"
    logItem2.message = "test log message 2"
    mloggerSpy.saveLogItem(logItem2)
    Assert.assertNotNull(logItem2)
  }

  @Test
  fun `test realm getinstance when exception`() {
    val exception: Exception = Exception("test exception")
    every { Realm.getInstance(any()) } throws exception
    every { Realm.getDefaultInstance() } returns mockRealm
    mloggerSpy.getRealmInstance()
  }

  @Test
  fun `test realm getinstance when two exceptions`() {
    val exception: Exception = Exception("test exception")
    every { Realm.getInstance(any()) } throws exception
    every { Realm.getDefaultInstance() } throws exception
    mloggerSpy.getRealmInstance()
  }

  @Test
  fun `test logging info with Turn On Camera event`() {
    mloggerSpy.meetingModel.uniqueMeetingId = NINE_EIGHT_NUMBER
    mloggerSpy.attendeeModel.role = HOST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Turn On Camera event guest`() {
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.meetingModel.hostCompanyId = null
    mloggerSpy.logBaseMsg.clientId = TESTCLINETID
    mloggerSpy.attendeeModel.role = GUEST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Turn On Camera event null params`() {
    mloggerSpy.meetingModel.hostCompanyId = null
    mloggerSpy.logBaseMsg.clientId = TESTCLINETID
    mloggerSpy.attendeeModel.role = HOST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.meetingState.currentCamerasPublished = 1
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Turn On Camera Bandwidh event null params`() {
    mloggerSpy.mixpanelTurnOnCameraModel.meetingId = null
    mloggerSpy.logBaseMsg.clientId = TESTCLINETID
    mloggerSpy.attendeeModel.role = HOST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth = GOOD
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.mixpanelTurnOnCameraModel.webcamAction = ENABLE_WEBCAM
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test mixpanelCameraOnEvent info with Turn On Camera Bandwidh`() {
    mloggerSpy.mixpanelTurnOnCameraModel.meetingId = null
    mloggerSpy.logBaseMsg.clientId = TESTCLINETID
    mloggerSpy.attendeeModel.role = HOST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth = GOOD
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.mixpanelTurnOnCameraModel.webcamAction = ENABLE_WEBCAM
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }
  @Test
  fun `test mixpanelCameraOnEvent info with Turn On Camera Bandwidh Emty`() {
    mloggerSpy.mixpanelTurnOnCameraModel.meetingId = null
    mloggerSpy.logBaseMsg.clientId = TESTCLINETID
    mloggerSpy.attendeeModel.role = HOST
    mloggerSpy.meetingModel.numGuests = 2
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth = ""
    mloggerSpy.meetingState.webcamCount = 1
    mloggerSpy.meetingModel.uniqueMeetingId = null
    mloggerSpy.mixpanelTurnOnCameraModel.webcamAction = ENABLE_WEBCAM
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }
  @Test
  fun `test set mixpanelCameraOnEvent`() {
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth = GOOD
    assertThat(mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth).isEqualTo(GOOD)
  }
  @Test
  fun `test handleCamera`() {
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth = GOOD
    mloggerSpy.mixpanelTurnOnCameraModel.webcamAction = ENABLE_WEBCAM
    mloggerSpy.mixpanelTurnOnCameraModel.webcamResolution = ELEVEN_POINT_SEVEN
    mloggerSpy.mixpanelTurnOnCameraModel.webcamError = SEVENTEEN
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }
  @Test
  fun `test handleCamera with Emty`() {
    mloggerSpy.mixpanelTurnOnCameraModel.webcamBandwidth =""
    mloggerSpy.mixpanelTurnOnCameraModel.webcamAction=""
    mloggerSpy.mixpanelTurnOnCameraModel.webcamResolution=""
    mloggerSpy.mixpanelTurnOnCameraModel.webcamError =""
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON,MANAGE_WEBCAM_FEED,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test EndOfMeetingFeedback Positive`() {
    mloggerSpy.mixPanelEndOfMeetingFeedback.rating ="Positive"
    mloggerSpy.mixPanelVoIPCallQuality.callQuality = "Good"
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK,"Metrics: End of Meeting Feedback",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test EndOfMeetingFeedback Negative`() {
    mloggerSpy.mixPanelEndOfMeetingFeedback.rating ="Negative"
    mloggerSpy.mixPanelVoIPCallQuality.callQuality = "Poor"
    mloggerSpy.mixPanelEndOfMeetingFeedback.issuesExperienced = mutableListOf()
    mloggerSpy.mixPanelEndOfMeetingFeedback.issueCategoriesExperienced = mutableListOf()
    mloggerSpy.mixPanelEndOfMeetingFeedback.issueDescription = ""
    mloggerSpy.mixPanelEndOfMeetingFeedback.emailAddressIncluded = false
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK,"Metrics: End of Meeting Feedback",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test clearClientId`() {
    mloggerSpy.clearClientId()
    assertThat(mloggerSpy.logBaseMsg.clientId).isEmpty()
  }

  @Test
  fun `test getAudioTag`() {
    mloggerSpy.userModel.type = null
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MUTE_GUESTS, LogEventValue.MIXPANEL_MUTE_GUESTS.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNMUTE_GUESTS, LogEventValue.MIXPANEL_UNMUTE_GUESTS.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.VOIP_SERVICE, LogEventValue.VOIP_SERVICE.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.UAPI_DIALOUT, LogEventValue.UAPI_DIALOUT.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.endMetric(LogEvent.METRIC_SIP_CONNECT, LogEvent.METRIC_SIP_CONNECT.name)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.endMetric(LogEvent.METRIC_VOIP_CONNECT, LogEvent.METRIC_VOIP_CONNECT.name)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.endMetric(LogEvent.FEATURE_DIALIN, LogEvent.FEATURE_DIALIN.name)
    verify { mockRealm.executeTransactionAsync(any()) }
    mloggerSpy.endMetric(LogEvent.FEATURE_DIALOUT, LogEvent.FEATURE_DIALOUT.name)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test VoIPCallQuality `() {
    mloggerSpy.mixPanelVoIPCallQuality.callQuality ="GOOD"
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_VOIP_CALL_QUALITY,"Metrics: VoIP Call Quality",
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Poor Network Notification`() {
    mloggerSpy.meetingModel.uniqueMeetingId = MEETING_ID
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test logging info with Poor Network Notification Blank`() {
    mloggerSpy.meetingModel.uniqueMeetingId = EMPTY_STRING
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test sendChat`() {
    mloggerSpy.meetingModel.uniqueMeetingId = MEETING_ID
    mloggerSpy.mixpanelSendChat.messageType = TEXT_ONLY
    mloggerSpy.mixpanelSendChat.chatType = TEXT_GROUP
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, LogEventValue.MIXPANEL_SEND_CHAT.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test sendChat Blank`() {
    mloggerSpy.meetingModel.uniqueMeetingId = EMPTY_STRING
    mloggerSpy.mixpanelSendChat.messageType = EMPTY_STRING
    mloggerSpy.mixpanelSendChat.chatType = EMPTY_STRING
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, LogEventValue.MIXPANEL_SEND_CHAT.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test send manage private chat enable`() {
    mloggerSpy.meetingModel.uniqueMeetingId = MEETING_ID
    mloggerSpy.mixpanelManagePrivateChat.privateChatAction = TEXT_ENABLE_PRIVATE_CHAT
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test send manage private chat disable`() {
    mloggerSpy.meetingModel.uniqueMeetingId = EMPTY_STRING
    mloggerSpy.mixpanelManagePrivateChat.privateChatAction = TEXT_DISABLE_PRIVATE_CHAT
    mloggerSpy.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test mixpanelShareMeetingInformation`() {
    mloggerSpy.mixpanelShareMeetingInformation.shareOptionSelected = OUT_MEETING_SHARE
    mloggerSpy.mixpanelShareMeetingInformation.actionSource = ACTION_SOURCE
    mloggerSpy.info(tag, LogEvent.MIXPANEL_METRICS_EVENT_NAME, LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION, MIXPANEL_EVENT+LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }

  @Test
  fun `test mixpanelShareMeetingInformation Blank`() {
    mloggerSpy.mixpanelShareMeetingInformation.shareOptionSelected = EMPTY_STRING
    mloggerSpy.mixpanelShareMeetingInformation.actionSource = EMPTY_STRING
    mloggerSpy.info(tag, LogEvent.MIXPANEL_METRICS_EVENT_NAME, LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION, MIXPANEL_EVENT+LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION.value,
            null, null, false, true)
    verify { mockRealm.executeTransactionAsync(any()) }
  }
}