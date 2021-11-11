package com.pgi.logging

import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.Mixpanel
import com.pgi.logging.model.*
import io.realm.Realm

interface Logger {

  val userModel: UserModel
  var meetingModel: MeetingModel
  var attendeeModel: AttendeeModel
  var meetingState: MeetingState
  var mixpanelItem1: Mixpanel
  var mixpanelSpotlightModel : MixpanelSpotlightModel
  var mixpanelTurnOnCameraModel : MixpanelTurnOnCameraModel
  var mixpanelNetworkChangeModel : MixpanelNetworkChangeModel
  var mixpanelMeetingSecurityModel : MixpanelMeetingSecurityModel
  var mixPanelEndOfMeetingFeedback : MixPanelEndOfMeetingFeedback
  var mixPanelVoIPCallQuality : MixPanelVoIPCallQuality
  var mixpanelPoorNetworkNotification : MixpanelPoorNetworkNotification
  var mixpanelSendChat : MixpanelSendChat
  var mixpanelManagePrivateChat : MixpanelManagePrivateChat
  var mixpanelNewPrivateChat : MixpanelNewPrivateChat
  var mixpanelShareMeetingInformation : MixpanelShareMeetingInformation

  fun initLocalDB()

  fun setUserId(id: String?)

  fun setEmail(id: String?)

  fun verbose(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e: Throwable? =
      null, tags: Array<String>? = emptyArray(), logToNewRelic: Boolean = true, logToMixPanel: Boolean = false)

  fun info(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e: Throwable? =
      null, tags: Array<String>? = emptyArray(), logToNewRelic: Boolean = true, logToMixPanel: Boolean = false)

  fun debug(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e: Throwable? =
      null, tags: Array<String>? = emptyArray(), logToNewRelic: Boolean = true, logToMixPanel: Boolean = false)

  fun warn(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e: Throwable? = null,
           tags: Array<String>? = emptyArray(), logToNewRelic: Boolean = true, logToMixPanel: Boolean = false)

  fun error(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String, e: Throwable? =
      null, tags: Array<String>? = emptyArray(), logToNewRelic: Boolean = true, logToMixPanel: Boolean = false)

  fun startMetric(event: LogEvent)

  fun endMetric(event: LogEvent, msg: String)

  fun record(event: LogEvent)

  fun getlogsAndCount(realm: Realm, max: Int): Pair<String, Int>

  fun deleteLogs(count: Int, realm: Realm)

  fun clearModels()

  fun clearMeetingModel()

  fun deleteAllLogs()

  fun getRealmInstance():  Realm?

  fun clearClientId()
}