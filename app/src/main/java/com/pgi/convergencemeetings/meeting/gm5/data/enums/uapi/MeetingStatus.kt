package com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi


/**
 * Created by Sudheer Chilumula on 9/24/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
enum class MeetingStatus(val status: String) {
  STARTED("MEETING_STARTED"),
  KEPT_ALIVE("MEETING_KEPT_ALIVE"),
  INACTIVITY_WARNING("MEETING_INACTIVITY_WARNING"),
  ENDED("MEETING_ENDED")
}

enum class MeetingLockStatus(val status: String) {
  LOCKED("MEETING_LOCKED"),
  UNLOCKED("MEETING_UNLOCKED")
}

enum class MeetingMuteStatus(val status: String) {
  MUTED("MEETING_MUTED"),
  UN_MUTED("MEETING_UNMUTED")
}

enum class MeetingRecordStatus(val status: String) {
  RECORDING_STARTED("RECORDING_STARTED"),
  RECORDING_STOPPED("RECORDING_STOPPED"),
  RECORDING_START_FAILED("RECORDING_START_FAILED"),
  RECORDING_STOP_FAILED("RECORDING_STOP_FAILED")
}

enum class MeetingScreenShareStatus(val status: String) {
  SCREEN_SHARE_STARTED("SCREEN_SHARE_STARTED"),
  SCREEN_SHARE_STOPPED("SCREEN_SHARE_STOPPED")
}

enum class MeetingWebCamStatus(val status: String) {
  WEB_CAM_STARTED("WEB_CAM_STARTED"),
  WEB_CAM_STOPPED("WEB_CAM_STOPPED")
}

enum class AudioStatus(val status: String) {
  CONNECTING("CONNECTING"),
  CONNECTED("CONNECTED"),
  DISCONNECTED("DISCONNECTED"),
  NOT_CONNECTED("NOT_CONNECTED")
}

enum class AudioType(val type: String) {
  VOIP("VOIP"),
  DIAL_IN("DIAL_IN"),
  DIAL_OUT("DIAL_OUT"),
  NO_AUDIO("NO_AUDIO")
}

