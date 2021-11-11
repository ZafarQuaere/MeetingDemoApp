package com.pgi.network


/**
 * Created by Sudheer Chilumula on 10/10/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
object UAPIEndPoints {
  const val AUTHSESSION = "/uapi/v1/session"
  const val ROOMINFO = "/uapi/v1/room"
  const val JOINMEETING = "/uapi/v1/room/meeting/participants"
  const val LEAVEMEETING = "/uapi/v1/room/meeting/participant"
  const val MEETINGEVENTS = "/uapi/v1/room/meeting/events"
  const val UPDATEPART = "/uapi/v1/room/meeting/participants/{participantID}"
  const val UPDATEMEETING = "/uapi/v1/room/meeting"
  const val DIALOUT = "/uapi/v1/room/meeting/participant/dialout"
  const val UPDATEAUDIOPARTICIPANT = "/uapi/v1/room/meeting/audio/participants/{audioParticipantID}"
  const val CHAT = "/uapi/v1/room/meeting/conversations/{conversationId}/chats"
  const val ENABLE_PRIVATE_CHAT = "/uapi/v1/room/options/privatechat"
  const val CONTENTUPDATE = "/uapi/v1/room/meeting/contents/{contentID}"
  const val RECORDING = "/uapi/v1/room/meeting/recording"
  const val VIDEOROOM = "/uapi/v1/videoroom"
  const val UPDATE_WAITING_ROOM = "/uapi/v1/room/options/waitingroom"
  const val UPDATE_FRICTION_FREE = "/uapi/v1/room/options/frictionfree"
  const val JSONHEADER = "Content-Type: application/json"
  const val CREATE_CONVERSATION = "/uapi/v1/room/meeting/conversations"
}