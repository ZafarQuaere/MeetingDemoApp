package com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi


/**
 * Created by Sudheer Chilumula on 9/28/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
enum class JoinMeetingStatus(val status: String) {
  JOIN("JOIN"),
  DELETED_ROOM("DELETED_ROOM"),
  SESSION_USED("SESSION_USED"),
  LOCK("LOCK"),
  CAPACITY("CAPACITY"),
  WAIT("WAIT"),
  WAIT_TIMEOUT("WAIT_TIMEOUT")
}