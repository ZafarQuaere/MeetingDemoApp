package com.pgi.convergencemeetings.mockservers

import com.pgi.network.UAPIEndPoints
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File


/**
 * Created by Sudheer Chilumula on 10/13/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class MockUAPIServerDispatcher: Dispatcher() {

  override fun dispatch(request: RecordedRequest?): MockResponse {
    if(request?.getHeader("Authorization")?.contains("expired_token", true) == false) {
      val line = request.body?.readUtf8Line()
      when (request.path) {
        UAPIEndPoints.AUTHSESSION -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/authsession.json"))
        }

        UAPIEndPoints.ROOMINFO -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/roominfo.json"))
        }

        UAPIEndPoints.JOINMEETING -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/joinmeetingsuccess.json"))
        }

        UAPIEndPoints.LEAVEMEETING -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/leavemeeting.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/meetingevents.json"))
        }

        UAPIEndPoints.UPDATE_WAITING_ROOM -> {
          return MockResponse()
                  .setResponseCode(200)
                  .setBody(getJson("json/uapi/waitingroomoptions.json"))
        }

        UAPIEndPoints.UPDATE_FRICTION_FREE -> {
          return MockResponse()
                  .setResponseCode(200)
                  .setBody(getJson("json/uapi/frictionFreeOptions.json"))
        }

        "/uapi/v1/room/meeting/participants/123456" -> {
          if (line?.contains("role") == true) {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/partpromoted.json"))
          } else if (request.method == "DELETE") {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/dismissparticipnat.json"))
          }
        }

        "/uapi/v1/room/meeting/audio/participants/123456" -> {
          if (line?.contains("mute") == true) {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/muteaudiopart.json"))
          } else if (request.method == "DELETE") {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/dismissaudioparticipant.json"))
          }
        }

        UAPIEndPoints.UPDATEMEETING -> {
          if (line?.contains("mute") == true) {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/meetingmuted.json"))
          }
          if (line?.contains("lock") == true) {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/meetinglocked.json"))
          }
        }

        "/uapi/v1/room/meeting/participants/8y5uwfvrms4w2lcnm49hicx2w" -> {
            return MockResponse()
                    .setResponseCode(200)
                    .setBody(getJson("json/uapi/waitingroomadmitdeny.json"))
        }

        "/uapi/v1/room/meeting?audio=false" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/meetingended.json"))
        }

        UAPIEndPoints.DIALOUT -> {
          if (request.method == "POST") {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/dialout.json"))
          } else if (request.method == "DELETE") {
            return MockResponse()
                .setResponseCode(200)
                .setBody(getJson("json/uapi/dialoutcancel.json"))
          }
        }

        "/uapi/v1/room/meeting/conversations/default/chats" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/chatadd.json"))
        }

        "/uapi/v1/room/meeting/contents/8y5uwfvrms4w2lcnm49hicx2w" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/contentupdated.json"))
        }

        "/uapi/v1/room/meeting/conversations" -> {
          return MockResponse()
                  .setResponseCode(200)
                  .setBody(getJson("json/uapi/chatconversation.json"))
        }
      }
    } else {
        return MockResponse()
          .setResponseCode(401)
          .setBody(getJson("json/uapi/unautorizederror.json"))
    }
    return MockResponse().setResponseCode(404)
  }


  @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  private fun getJson(path : String) : String {
    val uri = javaClass.classLoader!!.getResource(path)
    val file = File(uri.file)
    return String(file.readBytes())
  }
}