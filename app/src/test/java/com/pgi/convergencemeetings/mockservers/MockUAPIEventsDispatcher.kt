package com.pgi.convergencemeetings.mockservers

import com.pgi.network.UAPIEndPoints
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.TimeUnit


/**
 * Created by Sudheer Chilumula on 10/17/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class MockUAPIEventsDispatcher: Dispatcher() {

  override fun dispatch(request: RecordedRequest?): MockResponse {
    if(request?.getHeader("Authorization")?.contains("SocketTimeoutException", true) == true) {
      return MockResponse()
          .setBodyDelay(20000, TimeUnit.MILLISECONDS)
          .setResponseCode(200)
          .setBody(getJson("json/uapi/events/event1.json"))
    }else if(request?.getHeader("Authorization")?.contains("expired_token", true) == false) {
      when(URLDecoder.decode(request.path, "UTF-8")) {
        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event1.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&testReset" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/waitingRoomAdmit.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&waitingRoom" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/waitingroom.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&participaintWaitAdmit" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/waitingroomparticipantadmit.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&participaintWaitAdmitNoParticipantID" -> {
          return MockResponse()
                  .setResponseCode(200)
                  .setBody(getJson("json/uapi/participaintWaitAdmitNoParticipantID.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=-1&timeout=50000&waitingroomreset" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/waitingroomreset.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421328&timeout=50000&talkers=2018-10-17T05:14:28.557Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event4.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421330&timeout=50000&talkers=2018-10-17T05:14:28.557Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event5.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421331&timeout=50000&talkers=2018-10-17T05:16:05.400Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event6.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421331&timeout=50000&talkers=2018-10-17T05:16:07.310Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event7.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421332&timeout=50000&talkers=2018-10-17T05:16:07.310Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event8.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421333&timeout=50000&talkers=2018-10-17T05:16:07.310Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event10.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421335&timeout=50000&talkers=2018-10-17T05:16:22.937Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event12.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421337&timeout=50000&talkers=2018-10-17T05:16:22.937Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event13.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421338&timeout=50000&talkers=2018-10-17T05:16:22.937Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event14.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421339&timeout=50000&talkers=2018-10-17T05:17:27.646Z" -> {
        return MockResponse()
            .setResponseCode(200)
            .setBody(getJson("json/uapi/events/event19.json"))
      }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421340&timeout=50000&talkers=2018-10-17T05:17:39.420Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event21.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421342&timeout=50000&talkers=2018-10-17T05:17:39.420Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event23.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421344&timeout=50000&talkers=2018-10-17T05:17:39.420Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event24.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421345&timeout=50000&talkers=2018-10-17T05:19:00.450Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event26.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421346&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event27.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421347&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event28.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421348&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event31.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421351&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event32.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421352&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event33.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421353&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event34.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421354&timeout=50000&talkers=2018-10-17T05:19:02.320Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event36.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421355&timeout=50000&talkers=2018-10-17T05:20:04.533Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event39.json"))
        }

        UAPIEndPoints.MEETINGEVENTS + "?since=2421357&timeout=50000&talkers=2018-10-17T05:20:06.470Z" -> {
          return MockResponse()
              .setResponseCode(200)
              .setBody(getJson("json/uapi/events/event43.json"))
        }
      }
    }else {
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