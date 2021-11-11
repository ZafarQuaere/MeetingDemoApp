package com.pgi.convergencemeetings.repository

import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.mockservers.MockUAPIServerDispatcher
import com.pgi.network.GMWebUAPIServiceAPI
import com.pgi.network.models.*
import com.pgi.network.repository.GMWebUAPIRepository
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Test Cases for all GMWebUAPIRepository class
 *
 * @author Sudheer R Chilumula
 * @since 5.20
 * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#overview">GMWeb Universal API Guide</a>
 */

@RunWith(JUnit4::class)
class GMWebUAPIRepositoryTest {

  private lateinit var mockServer: MockWebServer
  private lateinit var accessToken: String

  @Before
  fun setup() {
    val dispatcher = MockUAPIServerDispatcher()
    mockServer = MockWebServer()
    mockServer.setDispatcher(dispatcher)
    mockServer.start()

    accessToken = "khshjsfsbfsfk232730213khshjsfsbfsfk232730213khshjsfsbfsfk232730213"
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
    CoreApplication.mLogger = TestLogger()
    GMWebUAPIRepository.instance.gmUAPIServiceAPI = retrofit.create(GMWebUAPIServiceAPI::class.java)
  }

  @After
  @Throws
  fun tearDown() {
    mockServer.shutdown()
  }

  @Test
  fun testAuthorizeMeeting() {
    val testObserver = TestObserver<AuthorizeResponse>()
    GMWebUAPIRepository.instance.authorizeMeeting("123456").subscribe(testObserver)
    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "ROOM_SESSION"
              && t.authToken == "khshjsfsbfsfk232730213"
              && t.loginName == "test user"
        }
  }

  @Test
  fun testAuthorizeMeetingError() {
    val testObserver = TestObserver<AuthorizeResponse>()
    accessToken = "expired_token"
    GMWebUAPIRepository.instance.authorizeMeeting("123456").subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertError { t ->
          t is HttpException && t.code() == 401
        }
  }

  @Test
  fun testGetMeetingRoomInfo() {
    val testObserver = TestObserver<MeetingRoomInfoResponse>()
    GMWebUAPIRepository.instance.getMeetingRoomInfo().subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "MEETING_ROOM_INFO" && t.meetingOwnerClientId == "123456"
        }
  }

  @Test
  fun testJoinMeeting() {
    val testObserver = TestObserver<JoinMeetingResponse>()
    GMWebUAPIRepository.instance.joinMeeting("test", "user", "test user", "test.user@example.com", null).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "JOIN_MEETING"
              && t.joinStatus == "JOIN"
        }
  }

  @Test
  fun testLeaveMeeting() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.leaveMeeting().subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "LEAVE_MEETING"
        }
  }

  @Test
  fun testEndMeeting() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.endMeeting(false).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "END_MEETING"
        }
  }


  @Test
  fun testGetMeetingEvents() {
    val testObserver = TestObserver<UAPIMeetingEvent>()
    GMWebUAPIRepository.instance.getMeetingEvents(null).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          !t.reset
        }
  }

  @Test
  fun testUpdateUserRole() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.updateUserRole("123456", true).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "PARTICIPANT_PROMOTED"
        }
  }

  @Test
  fun testDismissUser() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.dismissUser("123456").subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "DISMISS_PARTICIPANT"
        }
  }

  @Test
  fun testLockUnlockMeeting() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.lockUnlockMeeting(true).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "UPDATE_MEETING"
        }
  }

  @Test
  fun testMuteUnmuteUser() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.muteUnmuteUser("123456", true).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "UPDATE_AUDIO_PARTICIPANT"
        }
  }

  @Test
  fun testDismissAudioUser() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.dismissAudioUser("123456").subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "DISMISS_AUDIO_PARTICIPANT"
        }
  }

  @Test
  fun testDialOut() {
    val testObserver = TestObserver<DialOutResponse>()
    GMWebUAPIRepository.instance.dialOut("1", "1234567890", null,
        null, null, null, null, null).subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "DIAL_OUT" && t.dialoutId == "17iuvcarlkr9stcx9h5dzz1z3"
        }
  }

  @Test
  fun testCancelDialOut() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.cancelDialOut().subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "CANCEL_DIAL_OUT"
        }
  }

  @Test
  fun testAddChat() {
    val testObserver = TestObserver<UAPIResponse>()
    GMWebUAPIRepository.instance.addChat("Test Chat","default").subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "ADD_CHAT"
        }
  }

  @Test
  fun testStopScreenShare() {
    val testObserver = TestObserver<ContentResponse>()
    GMWebUAPIRepository.instance.stopScreenShare("8y5uwfvrms4w2lcnm49hicx2w", "123456", "test", "1234567890", "test.user@pgi.com").subscribe(testObserver)

    testObserver.awaitTerminalEvent()

    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue { t ->
          t.responseType == "UPDATE_CONTENT" && t.contentId == "8y5uwfvrms4w2lcnm49hicx2w"
        }
  }

    @Test
    fun testUpdateUserAdmit() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.updateUserAdmitDeny("8y5uwfvrms4w2lcnm49hicx2w", "GUEST", true).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_PARTICIPANT"
                }
    }


    @Test
    fun testUpdateUserDeny() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.updateUserAdmitDeny("8y5uwfvrms4w2lcnm49hicx2w", "GUEST", false).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_PARTICIPANT"
                }
    }


    @Test
    fun testOffWaitingRoom() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.offOnWaitingRoom(false).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_MEETING_OPTION"
                }
    }


    @Test
    fun testOnWaitingRoom() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.offOnWaitingRoom(true).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_MEETING_OPTION"
                }
    }

    @Test
    fun testOnRestrictSharing() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.toggleFrictionFree(false).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_MEETING_OPTION"
                }
    }

    @Test
    fun testOffRestrictSharing() {
        val testObserver = TestObserver<UAPIResponse>()
        GMWebUAPIRepository.instance.toggleFrictionFree(true).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "UPDATE_MEETING_OPTION"
                }
    }

    @Test
    fun testAddConversation() {
        val testObserver = TestObserver<AddConversationResponse>()
        val participantID = arrayOf("123", "234")
        GMWebUAPIRepository.instance.addConversation(participantID).subscribe(testObserver)

        testObserver.awaitTerminalEvent()

        testObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { t ->
                    t.responseType == "ADD_CONVERSATION"
                }
    }
}