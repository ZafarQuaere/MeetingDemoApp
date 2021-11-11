package com.pgi.logging

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.garena.devalert.library.DevAlert
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newrelic.agent.android.NewRelic
import com.pgi.logging.database.LogRealmModule
import com.pgi.logging.database.RealmMapSerializer
import com.pgi.logging.enums.*
import com.pgi.logging.model.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import org.jetbrains.annotations.TestOnly
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.full.memberProperties


class PGiLogger(val context: Context): Logger {

  override val userModel = UserModel()
  override var meetingModel = MeetingModel()
  override var meetingState = MeetingState()
  override var mixpanelSpotlightModel = MixpanelSpotlightModel()
  override var mixpanelTurnOnCameraModel = MixpanelTurnOnCameraModel()
  override var mixpanelNetworkChangeModel = MixpanelNetworkChangeModel()
  override var mixpanelMeetingSecurityModel = MixpanelMeetingSecurityModel()
  override var mixPanelEndOfMeetingFeedback = MixPanelEndOfMeetingFeedback()
  override var mixPanelVoIPCallQuality = MixPanelVoIPCallQuality()
  override var mixpanelPoorNetworkNotification = MixpanelPoorNetworkNotification()
  override var mixpanelSendChat = MixpanelSendChat()
  override var mixpanelNewPrivateChat = MixpanelNewPrivateChat()
  override var mixpanelShareMeetingInformation = MixpanelShareMeetingInformation()
  override var attendeeModel = AttendeeModel()
  override var mixpanelManagePrivateChat = MixpanelManagePrivateChat()
  val logBaseMsg = ElkLogMessage()
  private var realmConfig: RealmConfiguration? = null
  private var uuid: String
  private var metricMap = mutableMapOf<String, Long>()
  private val tag = PGiLogger::class.java.simpleName
//  private val maxuploadLogs = 20
  internal var mPrefs: SharedPreferences? = null
  private var mPrefsLock: ReentrantLock = ReentrantLock()
  private val KEY_LOG_COUNT = "log_count"
  internal var backLogList : MutableList<ElkLogItem> = mutableListOf<ElkLogItem>()
  private val handlerThread = HandlerThread("com.pgi.gmmeet.pgilogger")
  private var backgroundHandler: Handler? = null
  private var backgroundProcessingActive: Boolean = false
  private val logTags = arrayOf(LogTags.AUDIO.eventName)
  private var logTagsData : Array<String>? = null
  private val EMPTY_STRING = ""

  init {
    if (BuildConfig.DEBUG) {
      DevAlert.init(context as Application?, true)
    } else {
      DevAlert.init(context as Application?, false)
    }
    uuid = UUID.randomUUID().toString()
    initLogBaseMsg()
    mPrefs = context.getSharedPreferences("LOG_STATE", Context.MODE_PRIVATE)
    loadSavedLogCount()
  }

  internal fun loadSavedLogCount() {
    if (mPrefs != null) {
      mPrefsLock.lock()
      try {
        val logCount = mPrefs?.getString(KEY_LOG_COUNT, null)
        if (logCount != null) {
          userModel.logCount = logCount.toInt() + 1
        }
      } finally {
        mPrefsLock.unlock()
      }
    }
  }

  internal fun saveLogCount() {
    mPrefsLock.lock()
    try {
      val editor = mPrefs?.edit()
      val count = userModel.logCount.toString()
      editor?.putString(KEY_LOG_COUNT, count)

      if (editor?.commit() == false) {
        error(tag, LogEvent.ERROR, LogEventValue.LOCAL_DB, "Failed to write logCount to shared prefs")
      }
    } finally {
      mPrefsLock.unlock()
    }

  }

  override fun getRealmInstance(): Realm? {
    try {
      return Realm.getInstance(realmConfig)
    } catch (e: Exception) {
      Log.e(tag, e.message)
      try {
        return Realm.getDefaultInstance()
      } catch (e: Exception) {
        Log.e(tag, e.message)
      }
    }
    return null
  }

  override fun clearClientId() {
    logBaseMsg.clientId = ""
  }

  override fun initLocalDB() {
    Realm.init(context)
    realmConfig = RealmConfiguration.Builder()
        .name("library.log.realm")
        .schemaVersion(1)
        .modules(LogRealmModule())
        .deleteRealmIfMigrationNeeded()
        .compactOnLaunch()
        .build()
    try {
      getRealmInstance().use {
        if (it?.isEmpty == true) {
          it.executeTransaction { r ->
            val sortedElkLogItem = SortedElkLogItem()
            r.copyToRealmOrUpdate(sortedElkLogItem)
          }
        }
      }
    } catch (e:Exception) {
      error(tag, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, e.message.toString(), e)
    }
  }

  private fun initLogBaseMsg() {
    logBaseMsg.clientId = ""
    logBaseMsg.applicationType = ELKConstants.ANDROID_APP.value
    logBaseMsg.applicationId = getMixPanelAppID(WeakReference(context)) ?: "NA"
    logBaseMsg.applicationName = logBaseMsg.applicationId
    logBaseMsg.applicationVersion = getAppVersion(WeakReference(context)) ?: "NA"
    logBaseMsg.os = ELKConstants.ANDROID.value
    logBaseMsg.osVersion = Build.VERSION.RELEASE
    logBaseMsg.attendee_roomRole = attendeeModel.role.toString()
    logBaseMsg.deviceType = when {
      isTablet(context) -> ELKConstants.TABLET.value
      else -> ELKConstants.MOBILE.value
    }
    logBaseMsg.deviceModel = getDeviceModelName()
    logBaseMsg.deviceId = getDeviceUUid()
    logBaseMsg.logItems = RealmList()
  }

  override fun setUserId(id: String?) {
    if (id != null) {
      logBaseMsg.clientId = id
      userModel.id = id
      NewRelic.setUserId(id)
    }
  }


  override fun setEmail(id: String?) {
    if (id != null) {
      userModel.email = id
      if(logBaseMsg.clientId.isNullOrEmpty()) {
        logBaseMsg.clientId = id
        userModel.id = id
        NewRelic.setUserId(id)
      }
    }
  }

  override fun verbose(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e:
  Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    addLog(LoggerLevel.TRACE, loggerName, logEvent, value.value, msg, e, tags, logToNewRelic,
        logToMixPanel)
  }

  override fun info(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e:
  Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    addLog(LoggerLevel.INFO, loggerName, logEvent, value.value, msg, e, tags, logToNewRelic,
        logToMixPanel)
  }

  override fun debug(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
                     e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    addLog(LoggerLevel.DEBUG, loggerName, logEvent, value.value, msg, e, tags, logToNewRelic,
        logToMixPanel)
  }

  override fun warn(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e:
  Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    addLog(LoggerLevel.WARN, loggerName, logEvent, value.value, msg, e, tags, logToNewRelic,
        logToMixPanel)
  }

  override fun error(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
                     e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    addLog(LoggerLevel.ERROR, loggerName, logEvent, value.value, msg, e, tags, logToNewRelic,
        logToMixPanel)
  }

  private fun addLog(level: LoggerLevel, loggerName: String, logEvent: LogEvent, value: String,
                     msg: String, e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean = true,
                     logToMixPanel: Boolean = true) {
    when {
      level == LoggerLevel.ERROR -> {
        Log.e(loggerName, msg, e)
        if(e != null) {
          DevAlert.reportError(loggerName, msg, e)
        } else {
          DevAlert.reportError(loggerName, msg)
        }
      }
      level == LoggerLevel.WARN -> {
        Log.w(loggerName, msg, e)
        if(e != null) {
          DevAlert.reportWarning(loggerName, msg, e)
        } else {
          DevAlert.reportWarning(loggerName, msg)
        }      }
      level == LoggerLevel.INFO -> {
        Log.i(loggerName, msg, e)
      }
      level == LoggerLevel.DEBUG -> {
        Log.d(loggerName, msg, e)
      }
      else -> {
        Log.v(loggerName, msg, e)
      }
    }

    logTagsData = getAudioTags(value,logEvent,tags)
    addLogsForAnalysis(level, loggerName, logEvent, value, msg, e, logTagsData, logToNewRelic, logToMixPanel)
    // Log if userType is null and currently not logging for null userType
    if (userModel.type == null && logEvent != LogEvent.ERROR_USER_TYPE_NULL) {
      // Sending loggerName and logEvent value as it is to determine the event when userType is null.
      val logEventValue = LogEventValue.getByValue(value) ?: LogEventValue.ERROR_USER_TYPE_NULL
      error(loggerName, LogEvent.ERROR_USER_TYPE_NULL, logEventValue, "UserType is null")
    }
  }

  private fun addLogsForAnalysis(level: LoggerLevel, loggerName: String, logEvent: LogEvent,
                                 value: String, msg: String, e: Throwable?, tags:
                                 Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {
    val logItem = ElkLogItem()
    logItem.logLevel = level.level
    logItem.loggerName = loggerName
    logItem.timestamp = formatDateToString(Date())
    logItem.transactionId = uuid
    logItem.subTransactionId = ""
    logItem.message = msg
    if (e != null) {
      logItem.exceptionMessage = e.message
      logItem.exceptionType = e.javaClass.canonicalName
      val stackTrace = Log.getStackTraceString(e)
      logItem.exceptionTrace = stackTrace
    }
    if (tags != null) {
      for (tag in tags) {
        logItem.tags.add(tag)
      }
    }
    val customAttributes = mutableMapOf<String, Any>()
    userModel.logCount++
    for (prop in UserModel::class.memberProperties) {
      if (prop.get(userModel) != null) {
        val map = RealmMap()
        if(prop.name == "logCount") {
          map.key = prop.name
        } else {
          map.key = "user_" + prop.name
        }
        map.value = prop.get(userModel).toString()
        logItem.customFields.add(map)
        prop.get(userModel)?.let {
          customAttributes.put("User ${prop.name}", it)
        }
      }
    }

    for (prop in AttendeeModel::class.memberProperties) {
      if (prop.get(attendeeModel) != null) {
        val map = RealmMap()
        map.key = "attendee_" + prop.name
        map.value = prop.get(attendeeModel).toString()
        logItem.customFields.add(map)
        prop.get(attendeeModel)?.let {
          customAttributes.put("Attendee ${prop.name}", it)
        }
      }
    }
    for (prop in MeetingModel::class.memberProperties) {
      if (prop.get(meetingModel) != null) {
        val map = RealmMap()
        map.key = "meeting_" + prop.name
        map.value = prop.get(meetingModel).toString()
        logItem.customFields.add(map)
        prop.get(meetingModel)?.let {
          customAttributes.put("Meeting ${prop.name}", it)
        }
      }
    }

    for (prop in MeetingState::class.memberProperties) {
      if (prop.get(meetingState) != null) {
        val map = RealmMap()
        map.key = "meetingState_" + prop.name
        map.value = prop.get(meetingState).toString()
        logItem.customFields.add(map)
        prop.get(meetingState)?.let {
          customAttributes.put("Meeting ${prop.name}", it)
        }
      }
    }

    for (prop in MixpanelNetworkChangeModel::class.memberProperties) {
      if (prop.get(mixpanelNetworkChangeModel) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(mixpanelNetworkChangeModel).toString()
        logItem.customFields.add(map)
        prop.get(mixpanelNetworkChangeModel)?.let {
          customAttributes.put("networkChange ${prop.name}", it)
        }
      }
    }

    if (logToNewRelic && NewRelic.isStarted()) {
      NewRelic.recordBreadcrumb(logEvent.eventName + "_" + value, customAttributes)
      NewRelic.recordCustomEvent(EventCategory.UCCANDROID.category,
          logEvent.eventName + "_" + value,
          customAttributes)
      if ((level == LoggerLevel.ERROR || level == LoggerLevel.FATAL) && logEvent == LogEvent.EXCEPTION) {
        NewRelic.recordHandledException(Exception(e))
      }
    }

    val nrMap = RealmMap()
    nrMap.key = "newRelicSessionId"
    nrMap.value = NewRelic.currentSessionId()
    logItem.customFields.add(nrMap)

    val evntMap = RealmMap()
    evntMap.key = logEvent.eventName
    evntMap.value = value
    logItem.customFields.add(evntMap)

    if(logToMixPanel) {
      logItem.metricsAnalysis = true
      logItem.metrics.add(evntMap)

      if (logEvent.name == LogEvent.MIXPANEL_EVENT.name || logEvent.name == LogEvent.MIXPANEL_METRICS_EVENT_NAME.name) {
        processMixpanelEvent(logItem, value);
      }
    }
    saveLogItem(logItem)
  }

  internal fun saveLogItem(logItem: ElkLogItem) {
    try {
      getRealmInstance().use {
        it?.executeTransactionAsync { r ->
          val sortedLogItem = r.where(SortedElkLogItem::class.java).findFirst()
          sortedLogItem?.logItems?.add(logItem)
          saveLogCount()
        }
      }
    } catch (e: Exception) {
      // do not log the exception as that will just make the issue worse
      // yes it will eventually clear, but it just clutters up kibana.
      backLogList.add(logItem)
      saveLogCount()
    }
    if (backLogList.isNotEmpty()) {
      if (backgroundHandler == null) {
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
      }
      if (!backgroundProcessingActive) {
        backgroundHandler?.post {
          addAnyBackLoggedItems();
        }
      }
    }
  }
  // if we try to add log records too fast to the
  // local DB it will throw an exception
  // so we catch those and add them to a list
  // We need to add them as a group
  internal fun addAnyBackLoggedItems() {
    // if we are here we know the DB is backed up adding records
    // so pause first to try to let it clear.
    backgroundProcessingActive = true
    Thread.sleep(1000)
    try {
      getRealmInstance().use {
        it?.executeTransactionAsync { r ->
          val sortedLogItem = r.where(SortedElkLogItem::class.java).findFirst()
          while (backLogList.isNotEmpty()) {
            sortedLogItem?.logItems?.add(backLogList[0])
            backLogList.removeAt(0)
          }
        }
      }
    } catch (e: Exception) {
      error(tag, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, e.message.toString(), e)
    }
    backgroundProcessingActive = false
  }

  private fun processMixpanelEvent(logItem: ElkLogItem, value: String) {
    addMixpanelBaseEvent(logItem)
    when (value) {
      LogEventValue.MIXPANEL_DISMISS_HOME_CARD.value -> addMixpaneDismissHomeCard(logItem)
      LogEventValue.MIXPANEL_ENABLE_INTEGRATION.value -> addMixpanelEnableIntegration(logItem)
      LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION.value -> addMixpanelJoinAudio(logItem)
      LogEventValue.MIXPANEL_JOIN_MEETING.value -> addMixpanelJoinMeeting(logItem)
      LogEventValue.MIXPANEL_LOG_IN.value -> addMixpanelLogIn(logItem)
      LogEventValue.MIXPANEL_LOCK_MEETING.value -> addMixpanelLockMeeting(logItem)
      LogEventValue.MIXPANEL_MUTE_GUESTS.value -> addMixpanelMuteGuests(logItem, true)
      LogEventValue.MIXPANEL_RECORD.value -> addMixpanelRecord(logItem)
      LogEventValue.MIXPANEL_UNLOCK_MEETING.value -> addMixpanelLockMeeting(logItem)
      LogEventValue.MIXPANEL_UNMUTE_GUESTS.value -> addMixpanelMuteGuests(logItem, false)
      LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT.value -> addMixpanelSpotlight(logItem)
      LogEventValue.MIXPANEL_WEBCAM_ON.value -> addMixpanelCameraOn(logItem)
      LogEventValue.MIXPANEL_NETWORK_CHANGE.value -> addMixpanelNetworkChange(logItem)
      LogEventValue.MIXPANEL_MANAGE_MEETING_SECURITY.value -> addMixpanelWaitingRoom(logItem)
      LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK.value -> addMixpanelEndOfMeetingFeedback(logItem)
      LogEventValue.MIXPANEL_VOIP_CALL_QUALITY.value -> addMixpanelVoIPCallQuality(logItem)
      LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value -> addMixpanelPoorNetworkNotification(logItem)
      LogEventValue.MIXPANEL_SEND_CHAT.value -> addMixpanelSendChat(logItem)
      LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT.value -> addMixPanelManagePrivateChat(logItem)
      LogEventValue.MIXPANEL_NEW_PRIVATE_CHAT.value -> addMixPanelNewPrivateChat(logItem)
      LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION.value -> addMixpanelShareMeetingInformation(logItem)
    }
  }

  private fun addMixpanelProperty(nameIn: String?, valueIn: Any?, logItem: ElkLogItem) {
    if (nameIn != null  && valueIn != null) {
      val map = RealmMap()
      map.key = nameIn
      map.value = valueIn.toString()
      logItem.metrics.add(map)
    }
  }

  private fun addMixpanelBaseEvent(logItem: ElkLogItem) {
    val clientId = when (logBaseMsg.clientId.isNullOrEmpty()) {
      true -> userModel.email
      false -> logBaseMsg.clientId
    }
    val companyId = when (!meetingModel.hostCompanyId.isNullOrEmpty()) {
      true -> meetingModel.hostCompanyId
      false -> userModel.companyId
    }
    val locale = Locale.getDefault().language
    val base = MixpanelBaseModel(clientId, companyId, locale, getScreenResolution(context), meetingModel.numGuests.toString(), mixpanelUserType(), mixpanelNetworkChangeModel.networkType, mixpanelNetworkChangeModel.mobileCountryCode, mixpanelNetworkChangeModel.mobileNetworkCode, logBaseMsg.applicationVersion, waitingRoomEnabled = meetingModel.waitingRoomEnabled)
    for (prop in MixpanelBaseModel::class.memberProperties) {
      if (prop.get(base) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(base).toString()
        logItem.metrics.add(map)
      }
      addMixpanelProperty(prop.name, prop.get(base), logItem)
    }
    meetingState.participantCount = meetingModel.numGuests
    val stateBase = MeetingState(meetingState.currentCamerasPublished, meetingState.currentCamerasSubcribed, meetingState.maxCamerasPublished,meetingState.participantCount)
        for (stateProp in MeetingState::class.memberProperties) {
          if (stateProp.get(stateBase) != null) {
            val map = RealmMap()
            map.key = stateProp.name
            map.value = stateProp.get(stateBase).toString()
            logItem.metrics.add(map)
            addMixpanelProperty(stateProp.name, stateProp.get(stateBase), logItem)
          }
        }
  }

  private fun mixpanelUserType(): String? {
      val userType = userModel.type?.toLowerCase() ?: return null
      return when(userType) {
          ELKConstants.CLIENT.value.toLowerCase() -> ELKConstants.CLIENT.value
          ELKConstants.GUEST.value.toLowerCase()  -> ELKConstants.UNREGISTERED_GUEST.value
          else -> null
      }
  }

  override var mixpanelItem1: Mixpanel = Mixpanel.NOT_SET

  private fun addMixpaneDismissHomeCard(logItem: ElkLogItem) {
    val event = MixpanelDismissHomeCardModel(mixpanelItem1.value)
    for (prop in MixpanelDismissHomeCardModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
    mixpanelItem1 = Mixpanel.NOT_SET
  }

  private fun addMixpanelEnableIntegration(logItem: ElkLogItem) {
    val event = MixpanelEnableIntegrationModel()

    for (prop in MixpanelEnableIntegrationModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelCameraOn(logItem: ElkLogItem) {

    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
          true -> meetingModel.uniqueMeetingId
          false -> ""
      }

      val event = MixpanelTurnOnCameraModel(meetingId,meetingModel.numGuests.toString(), attendeeModel.role.toString(),mixpanelTurnOnCameraModel.webcamAction,mixpanelTurnOnCameraModel.webcamResolution,mixpanelTurnOnCameraModel.webcamBandwidth,mixpanelTurnOnCameraModel.webcamError)

      for (prop in MixpanelTurnOnCameraModel::class.memberProperties) {
          if (prop.get(event) != null) {
              val map = RealmMap()
              map.key = prop.name
              map.value = prop.get(event).toString()
              logItem.metrics.add(map)

              addMixpanelProperty(prop.name, prop.get(event), logItem)
          }
      }
  }
  private fun addMixpanelJoinAudio(logItem: ElkLogItem) {
    // first = audio connection, second = phone connection
    val connections = when (attendeeModel.audioConnectionType) {
      Mixpanel.AUDIO_DIAL_IN.value,
      Mixpanel.AUDIO_DIAL_OUT.value ->  Pair( "Phone",  attendeeModel.audioConnectionType.toString())
      Mixpanel.AUDIO_DO_NOT_CONNECT.value -> Pair(Mixpanel.AUDIO_DO_NOT_CONNECT.value, Mixpanel.AUDIO_DO_NOT_CONNECT.value)
      Mixpanel.AUDIO_VOIP.value -> Pair("Computer", Mixpanel.AUDIO_VOIP.value)
      else -> Pair("None", "None")
    }
    val event = MixpanelJoinAudioConnectionModel(connections.first, connections.second, attendeeModel.conferenceType, attendeeModel.audiocodec, attendeeModel.muted, attendeeModel.meetingServer)

    for (prop in MixpanelJoinAudioConnectionModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelJoinMeeting(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val event = MixpanelJoinMeetingModel(userModel.joinMeetingEntryPoint, meetingId, formatDateToString(Date()))

    for (prop in MixpanelJoinMeetingModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelLockMeeting(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val event = MixpanelLockModel(meetingId)

    for (prop in MixpanelLockModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }

  }

  private fun addMixpanelLogIn(logItem: ElkLogItem) {
    val event = MixpanelLogInModel(formatDateToString(Date()))

    for (prop in MixpanelLogInModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelMuteGuests(logItem: ElkLogItem, mute: Boolean) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val muted = when (mute) {
      true -> "TRUE"
      false -> "FALSE"
    }
    val event = MixpanelMuteGuestsModel(meetingId, mixpanelItem1.value, muted)

    for (prop in MixpanelMuteGuestsModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
    mixpanelItem1 = Mixpanel.NOT_SET;
  }

  private fun addMixpanelRecord(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val event = MixpanelRecordModel(meetingId)

    for (prop in MixpanelRecordModel::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelSpotlight(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val event = MixpanelSpotlightModel(meetingId,meetingModel.numGuests.toString(), attendeeModel.role.toString())

    for (prop in MixpanelSpotlightModel::class.memberProperties) {
      if (prop.get(event) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(event).toString()
        logItem.metrics.add(map)

        addMixpanelProperty(prop.name, prop.get(event), logItem)
      }
    }
  }

  private fun addMixpanelNetworkChange(logItem: ElkLogItem) {
    val event = MixpanelNetworkChangeModel(mixpanelNetworkChangeModel.networkType,mixpanelNetworkChangeModel.mobileCountryCode,mixpanelNetworkChangeModel.mobileNetworkCode);

    for (prop in MixpanelNetworkChangeModel::class.memberProperties) {
      if (prop.get(event) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(event).toString()
        logItem.metrics.add(map)

        addMixpanelProperty(prop.name, prop.get(event), logItem)
      }
    }
  }

  private fun addMixpanelWaitingRoom(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> ""
    }
    val event = MixpanelMeetingSecurityModel(meetingId, mixpanelMeetingSecurityModel.waitingRoomAction, mixpanelMeetingSecurityModel.numGuestsWaiting, mixpanelMeetingSecurityModel.numGuestsAdmitted, mixpanelMeetingSecurityModel.numGuestsDenied, mixpanelMeetingSecurityModel.guestsAdmitted, mixpanelMeetingSecurityModel.guestsDenied, attendeeModel.meetingServer)

    for (prop in MixpanelMeetingSecurityModel::class.memberProperties) {
      if (prop.get(event) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(event).toString()
        logItem.metrics.add(map)

        addMixpanelProperty(prop.name, prop.get(event), logItem)
      }
    }
  }

  private fun addMixpanelEndOfMeetingFeedback(logItem: ElkLogItem) {
    val event = if (mixPanelEndOfMeetingFeedback.rating == "Positive") {
      MixPanelEndOfMeetingFeedback(meetingModel.uniqueMeetingId, mixPanelVoIPCallQuality.callQuality, mixPanelEndOfMeetingFeedback.rating)
    } else {
      MixPanelEndOfMeetingFeedback(meetingModel.uniqueMeetingId, mixPanelVoIPCallQuality.callQuality, mixPanelEndOfMeetingFeedback.rating, mutableListOf(), mutableListOf(),mixPanelEndOfMeetingFeedback.issueDescription,false)
    }
    convertToJson(event, logItem)
  }

  private fun addMixpanelVoIPCallQuality(logItem: ElkLogItem) {
    val event = MixPanelVoIPCallQuality(meetingModel.uniqueMeetingId, meetingModel.server, mixPanelVoIPCallQuality.callQuality)
    convertToJson(event, logItem)
  }

  private fun convertToJson(event: Any?, logItem: ElkLogItem) {
    val json = Gson().toJson(event)  // json string
    try {
      val jsonObject = JSONObject(json)
      val iterator =  jsonObject.keys().iterator()
      while (iterator.hasNext())
      {
        val key = iterator.next()
        if(jsonObject.get(key) !=null) {
          val map = RealmMap()
          map.key = key
          map.value = jsonObject.get(key).toString()
          logItem.metrics.add(map)

          addMixpanelProperty(key, jsonObject.get(key), logItem)
        }
      }
    } catch (e: JSONException) {
      e.printStackTrace()
    }
  }

  private fun addMixpanelPoorNetworkNotification(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> EMPTY_STRING
    }
    val event = MixpanelPoorNetworkNotification(meetingId)

    for (prop in MixpanelPoorNetworkNotification::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixpanelSendChat(logItem: ElkLogItem) {
    val meetingId = when (!meetingModel.uniqueMeetingId.isNullOrEmpty()) {
      true -> meetingModel.uniqueMeetingId
      false -> EMPTY_STRING
    }
    val event = MixpanelSendChat(meetingId,mixpanelSendChat.messageType,mixpanelSendChat.chatType,mixpanelSendChat.recipientUserType)
    for (prop in MixpanelSendChat::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixPanelNewPrivateChat(logItem: ElkLogItem) {
    val event = MixpanelNewPrivateChat(mixpanelNewPrivateChat.chatType,mixpanelNewPrivateChat.recipientUserType,mixpanelNewPrivateChat.actionSource)
    for (prop in MixpanelNewPrivateChat::class.memberProperties) {
      addMixpanelProperty(prop.name, prop.get(event), logItem)
    }
  }

  private fun addMixPanelManagePrivateChat(logItem: ElkLogItem) {
    val event = MixpanelManagePrivateChat(mixpanelManagePrivateChat.privateChatAction)
    convertToJson(event, logItem)
  }

  private fun addMixpanelShareMeetingInformation(logItem: ElkLogItem) {
    val event = MixpanelShareMeetingInformation(mixpanelShareMeetingInformation.shareOptionSelected,mixpanelShareMeetingInformation.actionSource)
    convertToJson(event, logItem)
  }

  override fun startMetric(event: LogEvent) {
    metricMap[event.eventName] =  System.currentTimeMillis()
  }

  override fun endMetric(event: LogEvent, msg: String) {
    if (metricMap.containsKey(event.eventName) && metricMap[event.eventName] != null) {
      val diff = (System.currentTimeMillis() - metricMap[event.eventName]!!)/1000.toDouble()
      addLog(LoggerLevel.INFO, tag, event, diff.toString(), "${event.eventName} : ${msg}",
          null, null, false, true)
      NewRelic.recordMetric("${event.eventName}", EventCategory.METRIC.category, diff)
      metricMap.remove(event.eventName)
    }
  }

  override fun record(event: LogEvent) {
    NewRelic.incrementAttribute(event.eventName)
    // Do we need this log to be pushed to NewRelic ???
    addLog(LoggerLevel.INFO, tag, event, LogEventValue.TRUE.value, "${event.eventName} Metric", null,
        null, true, false)
  }

  override fun getlogsAndCount(realm: Realm, maxLogRecords: Int): Pair<String, Int> {
    val keyValueRealmListType = object : TypeToken<RealmList<RealmMap>>() {}.getType()
    val gson = GsonBuilder().registerTypeAdapter(keyValueRealmListType,
        RealmMapSerializer()).create()
    val results = realm.where(SortedElkLogItem::class.java)?.findFirst()?.logItems?.take(maxLogRecords)
    if (results != null  && results.isNotEmpty()) {
      val logItems = realm.copyFromRealm(results)?.toMutableList()
      logBaseMsg.logItems.addAll(logItems!!)
    }
    val logJson = gson.toJson(logBaseMsg)
    val count = results?.count() ?: 0
    logBaseMsg.logItems.clear()
    return Pair(first = logJson, second = count)
  }

  override fun deleteLogs(count: Int, realm: Realm) {
    try {
      realm.executeTransaction {
        val sortedLogItem = it.where(SortedElkLogItem::class.java).findFirst()
        for (i in count - 1 downTo 0) {
          sortedLogItem?.logItems?.removeAt(i)
        }
      }
    } catch (e:Exception) {
      error(tag, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, e.message.toString(), e)
    }
  }

  /* this allows us to clear just the meeting model
     so we don't lose some values in attendee model
     that don't get reset when the user leaves and joins
     meetings.
   */
  override fun clearMeetingModel() {
    meetingState = MeetingState()
    }

  override fun clearModels() {
    attendeeModel = AttendeeModel()
    meetingModel = MeetingModel()
    meetingState = MeetingState()
    mixpanelTurnOnCameraModel = MixpanelTurnOnCameraModel()
    mixpanelSpotlightModel = MixpanelSpotlightModel()
    mixpanelMeetingSecurityModel = MixpanelMeetingSecurityModel()
    mixPanelVoIPCallQuality = MixPanelVoIPCallQuality()
    /* can't just recreate userModel as some values
       in that object won't get reset and aren't
       really user specific but phone specific
     */
    userModel.id = null
    userModel.companyId = null
    userModel.email = null
    userModel.firstName = null
    userModel.lastName = null
    userModel.role = null
  }

  override fun deleteAllLogs() {
    logBaseMsg.logItems.clear()
    try {
      getRealmInstance().use {
        it?.executeTransactionAsync { r ->
          r.where(SortedElkLogItem::class.java).findFirst()?.logItems?.clear()
        }
      }
    } catch (e:Exception) {
      error(tag, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, e.message.toString(), e)
    }
  }

  private fun getDeviceUUid(): String {
    val filename = "gm_deviceid"
    val file = File(filename)
    if (file.exists()) {
      context.openFileInput(filename).use {
        return String(it.readBytes())
      }
    } else {
      val fileContents = UUID.randomUUID().toString()
      context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(fileContents.toByteArray())
      }
      return fileContents
    }
  }

  private fun getAppVersion(context: WeakReference<Context>): String? {
    var appVersion: String? = null
    try {
      val manager = context.get()?.packageManager
      val info = manager?.getPackageInfo(context.get()?.packageName, 0)
      if (info != null) {
        appVersion = "${info.versionName}.${info.versionCode}"
      }
    } catch (ex: Exception) {
      debug(tag, LogEvent.EXCEPTION, LogEventValue.ELK_LOG, "PGiLogger getAppVersion() - failed " +
          "geeting app version", ex)
    }
    return appVersion
  }

  private fun getMixPanelAppID(context: WeakReference<Context>): String {
    return context.get()?.resources?.getString(R.string.mixpanel_app_id) ?: "GMANDROID"
  }

  private fun isTablet(context: Context): Boolean {
    val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val densityDpi = displayMetrics.densityDpi
    val wInches = width / densityDpi.toDouble()
    val hInches = height / densityDpi.toDouble()
    val screenDiagonal = Math.sqrt(Math.pow(wInches, 2.0) + Math.pow(hInches, 2.0))
    return screenDiagonal >= 7.0
  }

  private fun getScreenResolution(context: Context): String {
    val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val resolution : String = width.toString() + "x" +height.toString();
    return resolution
  }

  private fun getDeviceModelName(): String {
    val manufacturer = if (Build.UNKNOWN == Build.MANUFACTURER) "" else Build.MANUFACTURER
    val model = if (Build.UNKNOWN == Build.MODEL) "" else Build.MODEL
    return if (model.startsWith(manufacturer)) {
      model
    } else {
      "$manufacturer $model"
    }
  }

  private fun formatDateToString(date: Date): String {
    @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSz")
    return format.format(date)
  }

  @TestOnly
   fun addModelTest(logItem: ElkLogItem) {
    val event = MeetingModel()
    for (prop in MeetingModel::class.memberProperties) {
      if (prop.get(event) != null) {
        val map = RealmMap()
        map.key = prop.name
        map.value = prop.get(event).toString()
        logItem.metrics.add(map)
        addMixpanelProperty(prop.name, prop.get(event), logItem)
      }
    }
  }

  private fun getAudioTags(value: String, logEvent: LogEvent, tags: Array<String>?): Array<String>? {
    logTagsData = if ((LogEventValue.MIXPANEL_MUTE_GUESTS.value == value || LogEventValue.MIXPANEL_UNMUTE_GUESTS.value == value ||
                    LogEventValue.VOIP_SERVICE.value == value ||
                    LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION.value == value || LogEventValue.UAPI_DIALOUT.value == value ||
                    logEvent == LogEvent.METRIC_SIP_CONNECT || logEvent == LogEvent.METRIC_VOIP_CONNECT ||
                    logEvent == LogEvent.FEATURE_DIALIN || logEvent == LogEvent.FEATURE_DIALOUT)
                    && !(userModel.type == null && logEvent != LogEvent.ERROR_USER_TYPE_NULL)) {
            logTags
        } else {
            tags
        }
        return logTagsData
    }
}