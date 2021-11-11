package com.pgi.convergencemeetings

import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.Mixpanel
import com.pgi.logging.model.*
import io.realm.Realm

class TestLogger: Logger {
  override val userModel: UserModel = UserModel()
  override var meetingModel: MeetingModel = MeetingModel()
  override var attendeeModel: AttendeeModel = AttendeeModel()
  override var meetingState: MeetingState = MeetingState()
  override var mixpanelItem1: Mixpanel = Mixpanel.NOT_SET
  override var mixpanelSpotlightModel: MixpanelSpotlightModel = MixpanelSpotlightModel()
  override var mixpanelTurnOnCameraModel: MixpanelTurnOnCameraModel = MixpanelTurnOnCameraModel()

  override var mixpanelNetworkChangeModel: MixpanelNetworkChangeModel= MixpanelNetworkChangeModel()
  override var mixpanelMeetingSecurityModel: MixpanelMeetingSecurityModel = MixpanelMeetingSecurityModel()
  override var mixPanelEndOfMeetingFeedback: MixPanelEndOfMeetingFeedback = MixPanelEndOfMeetingFeedback()
  override var mixPanelVoIPCallQuality: MixPanelVoIPCallQuality = MixPanelVoIPCallQuality()
  override var mixpanelPoorNetworkNotification: MixpanelPoorNetworkNotification = MixpanelPoorNetworkNotification()
  override var mixpanelSendChat = MixpanelSendChat()
  override var mixpanelManagePrivateChat = MixpanelManagePrivateChat()
  override var mixpanelNewPrivateChat = MixpanelNewPrivateChat()
  override var mixpanelShareMeetingInformation = MixpanelShareMeetingInformation()

  override fun initLocalDB() {
    TODO("Not yet implemented")
  }

  override fun setUserId(id: String?) {
    userModel.id = id
  }

  override fun setEmail(id: String?) {
    userModel.email = id
  }

  override fun getRealmInstance(): Realm {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getlogsAndCount(realm: Realm, max: Int): Pair<String, Int> {
    return Pair("Test Data", 1)
  }

  override fun verbose(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
                       e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {

  }

  override fun info(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
                    e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {

  }

  override fun debug(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
                     e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {

  }

  override fun warn(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
           e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {

  }

  override fun error(loggerName: String, logEvent: LogEvent, value: LogEventValue, msg: String,
            e: Throwable?, tags: Array<String>?, logToNewRelic: Boolean, logToMixPanel: Boolean) {

  }

  override fun startMetric(event: LogEvent) {
  }

  override fun endMetric(event: LogEvent, msg: String) {

  }

  override fun record(event: LogEvent) {

  }

  override fun deleteLogs(count: Int, realm: Realm) {

  }

  override fun clearModels() {

  }

  override fun clearMeetingModel() {

  }

  override fun deleteAllLogs() {
  }

  override fun clearClientId() {
  }

}