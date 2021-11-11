package com.pgi.convergencemeetings.meeting.gm5.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.gson.Gson
import com.newrelic.agent.android.instrumentation.MetricCategory
import com.newrelic.agent.android.instrumentation.Trace
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.uapi.UAPIResponseType
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.JoinMeetingStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.Content
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback.SalesForceRepository
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FilesManager
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FilesRespository
import com.pgi.convergencemeetings.models.elkmodels.AttendeeModel
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.GMWebUAPIServiceManager
import com.pgi.network.TurnServerInfoManager
import com.pgi.network.models.*
import com.pgi.network.repository.FurlProxyRepository
import com.pgi.network.repository.GMWebUAPIRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * This is the ViewModel for all the UAPI Calls except meeting events.
 *
 * @author Sudheer R Chilumula
 * @since 5.19
 *
 */
class MeetingUserViewModel : BaseMeetingViewModel(), KoinComponent {
  private var compositeDisposable = CompositeDisposable()
  private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
  private val logger = CoreApplication.mLogger
  override val tag = MeetingUserViewModel::class.java.simpleName
  val featureManager: FeaturesManager by inject()

  lateinit var horsesEnvConfig: HorsesEnvConfig
  fun isHorsesConfigInitialized() = this::horsesEnvConfig.isInitialized
  var meetingRoomInfoResponse: MeetingRoomInfoResponse? = null
    set
  var meetingJoinResponse: JoinMeetingResponse? = null
    set
  var currentUserId: String? = null
    set
  var meetingFurl: String? = null
    set
  var resolvedFurl: String? = null
    private set
  var authResponse: AuthorizeResponse? = null
  var isCameraOn: Boolean = false
    set(value) {
      field = value
      cameraStatus.postValue(value)
    }
  var isCameraConnecting: Boolean = false
    set(value) {
      field = value
      cameraConnectingStatus.postValue(value)
    }

  var isFoxdenConnected: Boolean = false
    set(value) {
      field = value
      foxdenConnectionStatus.postValue(value)
    }
  var isMuteActivated: Boolean = false
  var isMute: Boolean = false
    set(value) {
      field = value
      muteStatus.postValue(value)
    }
  var isSpeakerOn: Boolean = false
  var isMuteBtnEnabled: Boolean = false
    set(value) {
      field = value
      muteBtnStatus.postValue(value)
    }
  var audioConnType: AudioType = AudioType.NO_AUDIO
    set(value) {
      field = value
      audioType.postValue(value)
      mlogger.attendeeModel.audioConnectionType = value.type
    }

  var audioConnState: AudioStatus = AudioStatus.NOT_CONNECTED
    set(value) {
      field = value
      audioStatus.postValue(value)
      mlogger.attendeeModel.audioConnectionState = value.status
    }

  var userIsInWaitingRoom: Boolean = false
  var screenShareFullScreen: Boolean = false
  var screenShareLandScape: Boolean = false
  var screenSharePortrait: Boolean = false
  var orientationChanged: Boolean = false
    set(value) {
      field = value
        userFlowStatus.postValue(UserFlowStatus.ORIENTATION_CHANGED)
    }
  var internetConnected: Boolean = true
    set(value) {
      field = value
      internetStatus.postValue(value)
    }

  var tabValue: Int = 0
    set(value) {
      field = value
      tabChangeStatus.postValue(value)
    }

  var appInForeground: Boolean?
    get() = appForegroundEvent.value
    set(value) {
      appForegroundEvent.postValue(value)
    }

  var turnWebcamOff: Boolean = true
    set(value) {
      field = value
      lowBandwidthEvent.postValue(value)
    }

  var isPrivateChatLocked: Boolean = false
    set(value) {
      field = value
      privateChatLocked.postValue(value)
    }

  var userInMeeting: Boolean = false
  var sharingContent: Content? = null
  var cloundFrontCookies: List<String> = emptyList()
  var contentPresentationActive = false;
  var mProfilePics : MutableList<String?> = mutableListOf()
  var mProfileInitials : MutableList<String?> = mutableListOf()
  var mUsersList : MutableList<User> = mutableListOf()
  var usersCount: Int = 0
  var userType: String? = null
  var createdConversationList : MutableList<String> = mutableListOf()
  lateinit var turnServerInfoManager: TurnServerInfoManager
  var privateChatVersionToastList: MutableList<String> = mutableListOf()

  private var coroutineExceptionHandler: (LogEventValue, String) -> CoroutineExceptionHandler =
      { logEventValue: LogEventValue, method: String ->
        CoroutineExceptionHandler { _, exception ->
          GlobalScope.launch {
            logger.error(tag, LogEvent.EXCEPTION, logEventValue, "MeetingUserViewModel: $method", exception)
          }
        }
      }

  var onMeetingRoomInfoSuccess: ((MeetingRoomInfoResponse)->Unit)? = null

  fun clear() {
    isMute = false
    isCameraOn = false
    audioConnState = AudioStatus.NOT_CONNECTED
    audioConnType = AudioType.NO_AUDIO
    userInMeeting = false
    userIsInWaitingRoom = false
    meetingRoomInfoResponse = null
    meetingJoinResponse = null
    meetingFurl = null
    ElkTransactionIDUtils.resetTransactionId()
    compositeDisposable.clear()
  }

  fun getJoinResponse(): JoinMeetingResponse? {
    return meetingJoinResponse
  }

  fun getRoomInfoResponse(): MeetingRoomInfoResponse? {
    return meetingRoomInfoResponse
  }

  fun isDialOutBlocked(): Boolean {
    var isBlocked = false
    meetingRoomInfoResponse?.let {
      it.audio?.let { audio ->
        audio.dialOutBlocked?.let { it ->
          isBlocked = it
        }
      }
    }
    return isBlocked
  }

  fun isFreemiumEnabled(): Boolean {
    var isFreemium = false
    if (meetingRoomInfoResponse != null) {
      isFreemium = meetingRoomInfoResponse?.freemiumEnabled as Boolean
    }
    return isFreemium
  }

  fun triggerMeetingLaunch(furl: String) {
    meetingFurl = furl
    resolveFurl(furl)
  }

  fun getDialInNumbers(): List<AccessNumber> {
    var numbers = emptyList<AccessNumber>()
    try {
      val a = meetingRoomInfoResponse?.audio?.accessPhoneNumbers!!.asSequence()
      if (a != null && a.any()) {
        val b = a.filter { it.phoneType != AppConstants.TYPE_VOIP_SIP }
        if (b != null && b.any()) {
          numbers = b.toList()
          val regex = Regex("\\(\\d+\\)")
          numbers.forEach {
            it.phoneNumber = regex.replace(it.phoneNumber, "")
          }
        }
      }
    } catch (e: Exception) {
      logger.error(tag, LogEvent.EXCEPTION, LogEventValue.AUDIOSELECTION, e.message.toString(), e)
    }
    return numbers
  }

  fun getDialOutNumbers(): List<Phone> {
    return if (AppAuthUtils.getInstance().isUserTypeGuest) {
      try {
        val phoneDao = ApplicationDao.get(CoreApplication.appContext).phoneNumbers
        val phones = phoneDao.loadAll()
        phones.toList().reversed()
      } catch (e: Exception) {
        mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, "MeetingUserViewModel " +
            "getDialOutNumbers()", e)
        var phones = emptyList<Phone>()
        phones.toList()
      }
    } else {
      val numbers = meetingJoinResponse?.dialoutNumbers?.sortedWith(
          compareBy { it.lastUsed }
      )
      var phones = emptyList<Phone>()
      numbers?.forEach {
        val phone = Phone()
        phone.countryCode = it.countryCode
        phone.extension = it.phoneExt
        phone.lastUsed = it.lastUsed
        phone.number = it.phone
        phones += phone
      }
      phones.toList()
    }
  }

  internal fun updateLastUsedPhoneInDb(countryCode: String, phoneNumber: String) {
    val numberToBeAdded = countryCode + phoneNumber
    val applicationDao = ApplicationDao.get(CoreApplication.appContext)
    val phoneDao = applicationDao.phoneNumbers
    val phoneList = phoneDao.loadAll()
    if (phoneList != null && !phoneList.isEmpty()) {
      for (phoneInfo in phoneList) {
        val cntryCode = phoneInfo.countryCode
        val phnNumber = phoneInfo.number
        val existingNumber = cntryCode + phnNumber
        if (existingNumber.equals(numberToBeAdded, ignoreCase = true)) {
          phoneDao.deleteByKey(phoneInfo.id!!)
          break
        }
      }
    }
    val phone = Phone()
    phone.countryCode = countryCode
    phone.number = phoneNumber
    //insert
    phoneDao.insert(phone)
  }

  private var joinMeetingProcessActive = false
  private var maxDuration = 15001L

  @Trace(category = MetricCategory.NETWORK)
  fun resolveFurl(furl: String) {
    if (furl.isEmpty()) {
      userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED)
    } else {
      joinMeetingProcessActive = true
      val joinMeetingProcessStart = System.currentTimeMillis()
      val configValue = featureManager.appConfig.joinMeetingAttemptMaxDuration
      if (configValue != null && configValue > 0) {
        maxDuration = configValue
        logger.debug(tag, LogEvent.API_FURLPROXY, LogEventValue.JOIN, "join meeting duration setting from config = $maxDuration")
      } else {
        logger.error(tag, LogEvent.ERROR, LogEventValue.JOIN, "join meeting duration setting from config is zero or not set")
      }
      val handlerThread = HandlerThread("com.pgi.gmmeet.joinmeeting")
      handlerThread.start()
      val h = Handler(handlerThread.looper)
      h.postDelayed({
        var joinMeetingProcessDuration:Long
        while (joinMeetingProcessActive) {
          joinMeetingProcessDuration = System.currentTimeMillis() - joinMeetingProcessStart
          if (joinMeetingProcessDuration > maxDuration) {
            userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED_NETWORK)
            mlogger.error(tag, LogEvent.ERROR, LogEventValue.JOIN, "JoinMeetingProcessWatcher: time limit exceeded")
            break
          }
          Thread.sleep(1000)
        }
        joinMeetingProcessActive = false
        Looper.myLooper()!!.quit()
      }, 1000)
      val resolveUrlDisposable = FurlProxyRepository.instance.resolveFurl(furl + "/")
          .subscribe(
              {
                val resolvedUrl = it.headers().values("Location")
                val parsedUri = HttpUrl.parse(resolvedUrl[0])
                if (parsedUri != null) {
                  mlogger.debug(tag, LogEvent.API_FURLPROXY, LogEventValue.FURL_GETURL,
                      "MeetingUserViewModel resolveFurl() - Resolved Url from Furl Proxy",
                      null, null, false)
                  val roomId = parsedUri.queryParameterValues("roomId")[0]
                      ?: parsedUri.queryParameterValues("roomid")[0]
                  val host = AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS + parsedUri.host().replace("web", "director")
                  resolvedFurl = host
                  if (joinMeetingProcessActive) {
                    getMeetingServer(host, roomId)
                  }
                }
              },
              {
                userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED_NETWORK)
                if (it is SocketTimeoutException || it is ConnectException) {
                  mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.JOIN, "resolveFurl - ${it.message}")
                } else {
                  userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED)
                  handleError(it, LogEventValue.FURL_GETURL, "MeetingUserViewModel resolveFurl() - ")
                }
                joinMeetingProcessActive = false
              }
          )
      compositeDisposable.add(resolveUrlDisposable)
    }
  }

  @Trace(category = MetricCategory.NETWORK)
  fun getMeetingServer(domainUrl: String, roomId: String) {
    val furlProxyDisposable = FurlProxyRepository.instance.getMeetingServer(domainUrl)
        .subscribe(
            {
                  mlogger.debug(tag, LogEvent.API_FURLPROXY, LogEventValue.FURL_GETBOOTNOAUTH,
                      "MeetingUserViewModel getMeetingServer() - Got Meeting Server from Furl " +
                          "Proxy", null, null, false)
                  try {
                    horsesEnvConfig = Gson().fromJson<HorsesEnvConfig>(it.body(), HorsesEnvConfig::class.java)
                    horsesEnvConfig.let { envConfig ->
                      GMWebUAPIServiceManager.baseUrl = AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS + envConfig.uapiHost
                      GMWebUAPIRepository.instance.gmUAPIServiceAPI = null
                      SalesForceRepository.baseUrl = horsesEnvConfig.salesforceSupport
                      sharedPreferencesManager.cloudRegion = envConfig.cloudRegion
                      sharedPreferencesManager.droidNumSupportedVersions = envConfig.droidNumSupportedVersions?.toInt() ?: 8
                      if (joinMeetingProcessActive) {
                        authorizeMeeting(roomId)
                      }
                      TurnServerInfoManager.turnURL = envConfig.cave?.turnRest
                      getTurnServerInfo()
                    }
                  } catch (ex: Exception) {
                    mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.FURL_GETBOOTNOAUTH,
                        "MeetingUserViewModel getMeetingServer()", ex)
                }
            },
            {
              userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED_NETWORK)
              if (it is SocketTimeoutException || it is ConnectException) {
                mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.JOIN, "getMeetingServer - ${it.message}")
              } else {
                userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED)
                handleError(it, LogEventValue.FURL_GETURL, "MeetingUserViewModel getMeetingServer() - ")
              }
              joinMeetingProcessActive = false
            }
        )
    compositeDisposable.add(furlProxyDisposable)
  }

  fun getTurnServerInfo() {
    turnServerInfoManager = TurnServerInfoManager()
    turnServerInfoManager.getTurnRestInfo()
  }

  fun stopTurnServerTimer() {
    turnServerInfoManager.stopCredentialExpiryChecker()
  }

  @Trace(category = MetricCategory.NETWORK)
  fun authorizeMeeting(roomId: String) {
    meetingRoomInfoResponse = null
    meetingJoinResponse = null
    userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING)
    val authorizeRoomDisposable = GMWebUAPIRepository.instance.authorizeMeeting(roomId)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.ROOM_SESSION.value) {
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_SESSION,
                    "MeetingUserViewModel authorizeMeeting() - User is authorized in to room",
                    null, null, false)
                authResponse = it
                userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_SUCCESS)
                if (it.authToken != null) {
                  sharedPreferencesManager.authToken = it.authToken // Not sure if we need to store and why we need to store this in sharedpref
                  sharedPreferencesManager.setHasJoinedUsersRoom(true)
                  if (joinMeetingProcessActive) {
                    this.getMeetingRoomInfo(it)
                    mlogger.attendeeModel.role = it.roomRole
                    mlogger.attendeeModel.participantId = it.userId
                    mlogger.meetingModel.hubConfId = roomId
                  }
                } else {
                  userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED)
                }
              }
            },
            {
              mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.JOIN, "authorizeMeeting - ${it.message}")
              if (it is SocketTimeoutException || it is ConnectException) {
                userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED_NETWORK)
              } else {
                handleError(it, LogEventValue.UAPI_SESSION, "MeetingUserViewModel authorizeMeeting() - ")
                retryFailed(RetryStatus.UAPI_SESSION)
                userFlowStatus.postValue(UserFlowStatus.AUTH_MEETING_FAILED)
              }
              joinMeetingProcessActive = false
            }
        )
    compositeDisposable.add(authorizeRoomDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun getMeetingRoomInfo(authorizeResponse: AuthorizeResponse) {
    userFlowStatus.postValue(UserFlowStatus.ROOM_INFO)
    val roomInfoDisposable = GMWebUAPIRepository.instance.getMeetingRoomInfo()
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.MEETING_ROOM_INFO.value) {
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGROOMINFO,
                    "MeetingUserViewModel getMeetingRoomInfo()- Got room info",
                    null, null, false)
                userFlowStatus.postValue(UserFlowStatus.ROOM_INFO_SUCCESS)
                this.meetingRoomInfoResponse = it
                val firstName = authorizeResponse.loginName.trim().split(AppConstants.BLANK_SPACE).first()
                val lastName = authorizeResponse.loginName.trim().split(AppConstants.BLANK_SPACE).last()
                val attendeeModel = AttendeeModel.getInstance()
                attendeeModel.firstname = firstName
                attendeeModel.lastname = lastName
                attendeeModel.email = authorizeResponse.email
                mlogger.meetingModel.furl = meetingFurl
                mlogger.meetingModel.server = it.meetingHost
                mlogger.meetingModel.hostPgiClientId = it.meetingOwnerClientId
                mlogger.meetingModel.hostCompanyId = it.companyId
                mlogger.meetingModel.type = "GM5"
                mlogger.meetingModel.waitingRoomEnabled = it.waitingRoom.enabled
                // Notify listener of the room info success
                onMeetingRoomInfoSuccess?.invoke(it)
                if (joinMeetingProcessActive) {
                  this.joinMeeting(lastName, firstName, authorizeResponse.loginName, authorizeResponse.email, null)
                }
              }
            },
            {
              mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.JOIN, "getMeetingRoomInfo - ${it.message}")
              if (it is SocketTimeoutException || it is ConnectException) {
                userFlowStatus.postValue(UserFlowStatus.ROOM_INFO_FAILED_NETWORK)
              } else {
                handleError(it, LogEventValue.UAPI_MEETINGROOMINFO, "MeetingUserViewModel " +
                    "getMeetingRoomInfo() - ")
                retryFailed(RetryStatus.UAPI_ROOM_INFO)
                userFlowStatus.postValue(UserFlowStatus.ROOM_INFO_FAILED)
              }
              joinMeetingProcessActive = false
            }
        )
    compositeDisposable.add(roomInfoDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun joinMeeting(lastName: String, firstName: String, name: String, email: String, matterNumber: String?) {
    userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING)
    val joinMeetingDisposable = GMWebUAPIRepository.instance.joinMeeting(lastName, firstName, name, email, matterNumber)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.JOIN_MEETING.value) {
                this.meetingJoinResponse = it
                mlogger.attendeeModel.participantId = it.participantId
                mlogger.attendeeModel.companyId = it.companyId
                mlogger.attendeeModel.sipIdentifier = it.sipIdentifier
                mlogger.attendeeModel.meetingJoinStatus = it.joinStatus

                when (it.joinStatus) {
                  JoinMeetingStatus.JOIN.status -> {
                    mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_JOINMEETING, "MeetingUserViewModel " + "joinMeeting()- Joined Meeting", null, null, false)
                    userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING_SUCCESS)
                    this.userInMeeting = true
                    this.currentUserId = it.participantId
                  }
                  JoinMeetingStatus.WAIT.status -> {
                    mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_JOINMEETING, "MeetingUserViewModel " + "joinMeeting()- Waiting to join Meeting", null, null, false)
                    userFlowStatus.postValue(UserFlowStatus.JOIN_WAIT_ROOM)
                    this.userInMeeting = false
                    this.currentUserId = it.participantId
                  }
                }
              }
              joinMeetingProcessActive = false
            },
            {
              mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.JOIN, "joinMeeting - ${it.message}")
              if (it is SocketTimeoutException || it is ConnectException) {
                userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING_FAILED_NETWORK)
              } else {
                handleJoinError(it)
              }
              joinMeetingProcessActive = false
            }
        )
    compositeDisposable.add(joinMeetingDisposable)
  }


  fun handleJoinError(it: Throwable) {
    if(it is HttpException) {
      val errorJsonString = it.response().errorBody()?.string()
      val response = Gson().fromJson<JoinMeetingResponse>(errorJsonString, JoinMeetingResponse::class.java)
      if(response.responseType == UAPIResponseType.JOIN_MEETING.value ) {
        val joinStatus = response.joinStatus;
        when(joinStatus) {
          AppConstants.LOCK -> userFlowStatus.postValue(UserFlowStatus.JOIN_LOCK_MEETING)
          AppConstants.CAPACITY -> userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING_AT_CAPACITY)
          AppConstants.DELETED_ROOM -> userFlowStatus.postValue(UserFlowStatus.JOIN_DELETED_ROOM)
          AppConstants.SESSION_USED ->  userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING_SESSION_USED)
          AppConstants.WAIT -> userFlowStatus.postValue(UserFlowStatus.JOIN_WAIT_ROOM)
          AppConstants.WAIT_TIMEOUT -> userFlowStatus.postValue(UserFlowStatus.JOIN_WAIT_ROOM_TIMEOUT)
          else -> {
            retryFailed(RetryStatus.JOIN_MEETING)
            userFlowStatus.postValue(UserFlowStatus.JOIN_MEETING_FAILED)
            handleError(it, LogEventValue.UAPI_JOINMEETING, "MeetingUserViewModel handleJoinError() - ")
          }
        }
      }
    }
  }
  @Trace(category = MetricCategory.NETWORK)
  fun leaveMeeting() {
    if(this.userInMeeting) {
      userFlowStatus.value = UserFlowStatus.LEAVE_MEETING
      val leaveMeetingDisposable = GMWebUAPIRepository.instance.leaveMeeting()
          .subscribe(
              {
                if (it.responseType == UAPIResponseType.LEAVE_MEETING.value) {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_LEAVEMEETING,
                      "MeetingUserViewModel leaveMeeting() - Successfully left meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.LEAVE_MEETING_SUCCESS)
                }
              },
              {
                handleError(it,  LogEventValue.UAPI_LEAVEMEETING, "MeetingUserViewModel leaveMeeting() - ")
                retryFailed(RetryStatus.UAPI_LEAVE_MEETING)
                userFlowStatus.postValue(UserFlowStatus.LEAVE_MEETING_FAILED)
              }
          )
      compositeDisposable.add(leaveMeetingDisposable)
    }
  }

  @Trace(category = MetricCategory.NETWORK)
  fun updateUserRole(user: User, promote: Boolean) {
    if(!promote && user.isSharing && this.sharingContent != null) {
      this.stopScreenShare(
          this.sharingContent!!.id,
          this.sharingContent!!.dynamicMetaData.screenPresenter.partId!!,
          this.sharingContent!!.dynamicMetaData.screenPresenter.name!!,
          this.sharingContent!!.dynamicMetaData.screenPresenter.phoneNumber,
          this.sharingContent!!.dynamicMetaData.screenPresenter.email!!)
    }
    mlogger.record(LogEvent.FEATURE_DEMOTEPROMOTE)
    val updateUserDisposable = GMWebUAPIRepository.instance.updateUserRole(user.id!!, promote)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.UPDATE_PARTICIPANT.value) {
                var msg = " "
                if(promote) {
                  msg = "MeetingUserViewModel updateUserRole() - Successfully promoted ${user.id}"
                } else {
                  msg = "MeetingUserViewModel updateUserRole() - Successfully demoted ${user.id}"
                }
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UPDATEPARTICIPANT, msg,
                    null, null, false)
              }
            },
            {
              handleError(it,  LogEventValue.UAPI_UPDATEPARTICIPANT, "MeetingUserViewModel updateUserRole() - ")
              if(promote) {
                retryFailed(RetryStatus.UAPI_PROMOTE_PARTICIPANT)
              } else {
                retryFailed(RetryStatus.UAPI_DEMOTE_PARTICIPANT)
              }
            }
        )
    compositeDisposable.add(updateUserDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun updateUserAdmitDeny(user: User, admit: Boolean) {
    val updateUserDisposable = user.id?.let{GMWebUAPIRepository.instance.updateUserAdmitDeny(it, null, admit)
            .subscribe(
                    {
                      if (it.responseType == UAPIResponseType.UPDATE_PARTICIPANT.value) {
                        var msg = " "
                        if(admit) {
                          msg = "MeetingUserViewModel updateUserAdmitDeny() - Successfully admitted ${user.id}"
                          mlogger.record(LogEvent.PARTICIPANT_ADMITTED)
                        } else {
                          msg = "MeetingUserViewModel updateUserAdmitDeny() - Successfully denied ${user.id}"
                          mlogger.record(LogEvent.DISMISSED_BY_HOST)
                        }
                        mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UPDATEPARTICIPANT, msg,
                                null, null, false)
                      }
                    },
                    {
                      handleError(it,  LogEventValue.UAPI_UPDATEPARTICIPANT, "MeetingUserViewModel updateUserAdmitDeny() - ")
                      if(admit) {
                        retryFailed(RetryStatus.UAPI_WAITING_PARTICIPANT_ADMITTED)
                      } else {
                        retryFailed(RetryStatus.UAPI_WAITING_PARTICIPANT_DENIED)
                      }
                    }
            )}
    updateUserDisposable?.let { compositeDisposable.add(it) }
  }


  @Trace(category = MetricCategory.NETWORK)
  fun updateAudioUserAdmitDeny(user: User, isAdmit: Boolean) {
    val updateUserDisposable = user.id?.let {
      GMWebUAPIRepository.instance.updateAudioUserAdmitDeny(it, isAdmit)
              .subscribe(
                      {
                        if (it.responseType == UAPIResponseType.UPDATE_AUDIO_PARTICIPANT.value) {
                          var logMsg = " "
                          if (isAdmit) {
                            logMsg = "MeetingUserViewModel updateAudioUserAdmitDeny() - Successfully admitted ${user.id}"
                            mlogger.record(LogEvent.AUDIO_PARTICIPANT_ADMITTED)
                          } else {
                            logMsg = "MeetingUserViewModel updateAudioUserAdmitDeny() - Successfully denied ${user.id}"
                            mlogger.record(LogEvent.DISMISSED_BY_HOST)
                          }
                          mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UPDATEPARTICIPANT, logMsg,
                                  null, null, false)
                        }
                      },
                      {
                        handleError(it, LogEventValue.UAPI_UPDATEPARTICIPANT, "MeetingUserViewModel updateAudioUserAdmitDeny() - ")
                        if (isAdmit) {
                          retryFailed(RetryStatus.UAPI_WAITING_PARTICIPANT_ADMITTED)
                        } else {
                          retryFailed(RetryStatus.UAPI_WAITING_PARTICIPANT_DENIED)
                        }
                      }
              )
    }
    updateUserDisposable?.let { compositeDisposable.add(it) }
  }

  @Trace(category = MetricCategory.NETWORK)
  fun dismissUser(user: User) {
    val dismisssUserDisposable = GMWebUAPIRepository.instance.dismissUser(user.id!!)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.DISMISS_PARTICIPANT.value) {
                userFlowStatus.postValue(UserFlowStatus.DISMISS_PARTICIPANT)
                mlogger.debug(tag, LogEvent.API_UAPI,
                    LogEventValue.UAPI_DISMISSPARTICIPANT, "MeetingUserViewModel dismissUser() - Dismissed user " +
                    "${user.id}",
                    null, null, false)
              }
            },
            {
              handleError(it, LogEventValue.UAPI_DISMISSPARTICIPANT, "MeetingUserViewModel " +
                  "dismissUser() - ")
            }
        )
    compositeDisposable.add(dismisssUserDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun lockUnlockMeeting(lock: Boolean) {
    val lockUnlockDisposable = GMWebUAPIRepository.instance.lockUnlockMeeting(lock)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.UPDATE_MEETING.value) {
                if (lock) {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_LOCKMEETING,
                      "MeetingUserViewModel lockUnlockMeeting() - Successfully locked meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.LOCK_MEETING_SUCCESS)
                } else {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UNLOCKMEETING,
                      "MeetingUserViewModel lockUnlockMeeting() - Successfully unlocked meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.UNLOCK_MEETING_SUCCESS)
                }
              }
            },
            {
              if (lock) {
                handleError(it, LogEventValue.UAPI_LOCKMEETING, "MeetingUserViewModel lockUnlock() - ")
                userFlowStatus.postValue(UserFlowStatus.LOCK_MEETING_FAILURE)
              } else {
                handleError(it, LogEventValue.UAPI_UNLOCKMEETING, "MeetingUserViewModel lockUnlock() - ")
                userFlowStatus.postValue(UserFlowStatus.UNLOCK_MEETING_FAILURE)
              }
            }
        )
    compositeDisposable.add(lockUnlockDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun toggleWaitingRoom(wait: Boolean) {
    val lockUnlockDisposable = GMWebUAPIRepository.instance.offOnWaitingRoom(wait)
            .subscribe(
                    {
                      if (it.responseType == UAPIResponseType.UPDATE_MEETING_OPTION.value) {
                        if (wait) {
                          mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_WAITINGON,
                                  "MeetingUserViewModel turnOffOnWaitingRoom() - Successfully turned on waiting in meeting",
                                  null, null, false)
                          userFlowStatus.postValue(UserFlowStatus.WAITING_ROOM_ON_SUCCESS)
                        } else {
                          mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_WAITINGOFF,
                                  "MeetingUserViewModel turnOffOnWaitingRoom() - Successfully turned off waiting in meeting",
                                  null, null, false)
                          userFlowStatus.postValue(UserFlowStatus.WAITING_ROOM_OFF_SUCCESS)
                        }
                      }
                    },
                    {
                      if (wait) {
                        handleError(it, LogEventValue.UAPI_WAITINGON, "MeetingUserViewModel waitingRoom() - ")
                        userFlowStatus.postValue(UserFlowStatus.WAITING_ROOM_ON_FAILURE)
                      } else {
                        handleError(it, LogEventValue.UAPI_WAITINGOFF, "MeetingUserViewModel waitingRoom() - ")
                        userFlowStatus.postValue(UserFlowStatus.WAITING_ROOM_OFF_FAILURE)
                      }
                    }
            )
    compositeDisposable.add(lockUnlockDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun toggleFrictionFree(enabled: Boolean) {
    val frictionFreeUpdateDisposable = GMWebUAPIRepository.instance.toggleFrictionFree(enabled)
            .subscribe(
                    {
                      if (it.responseType == UAPIResponseType.UPDATE_MEETING_OPTION.value) {
                        if (enabled) {
                          mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_FRICTIONFREEON,
                                  "MeetingUserViewModel turnOffOnFrictionFree() - Successfully turned on friction-free in meeting",
                                  null, null, false)
                          userFlowStatus.postValue(UserFlowStatus.FRICTION_FREE_ON_SUCCESS)
                        } else {
                          mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_FRICTIONFREEOFF,
                                  "MeetingUserViewModel turnOffOnFrictionFree() - Successfully turned off friction-free in meeting",
                                  null, null, false)
                          userFlowStatus.postValue(UserFlowStatus.FRICTION_FREE_OFF_SUCCESS)
                        }
                      }
                    },
                    {
                      if (enabled) {
                        handleError(it, LogEventValue.UAPI_FRICTIONFREEON, "MeetingUserViewModel frictionFree() - ")
                        userFlowStatus.postValue(UserFlowStatus.FRICTION_FREE_ON_FAILURE)
                      } else {
                        handleError(it, LogEventValue.UAPI_FRICTIONFREEOFF, "MeetingUserViewModel frictionFree() - ")
                        userFlowStatus.postValue(UserFlowStatus.FRICTION_FREE_OFF_FAILURE)
                      }
                    }
            )
    compositeDisposable.add(frictionFreeUpdateDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun muteUnmuteMeeting(muteAll: Boolean) {
    val muteUnmuteAllDisposable = GMWebUAPIRepository.instance.muteUnmuteMeeting(muteAll)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.UPDATE_MEETING.value) {
                if (muteAll) {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MUTEALL,
                      "MeetingUserViewModel muteUnmuteMeeting() - Successfully muted meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.MUTE_ALL_SUCCESS)
                } else {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UNMUTEALL,
                      "MeetingUserViewModel muteUnmuteMeeting() - Successfully unmuted meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.UNMUTE_ALL_SUCCESS)
                }
              }
            },
            {
              if (muteAll) {
                handleError(it, LogEventValue.UAPI_MUTEALL, "MeetingUserViewModel muteUnmuteMeeting() - ")
                userFlowStatus.postValue(UserFlowStatus.MUTE_ALL_FAILURE)
              } else {
                handleError(it, LogEventValue.UAPI_UNMUTEALL, "MeetingUserViewModel muteUnmuteMeeting() - ")
                userFlowStatus.postValue(UserFlowStatus.UNMUTE_ALL_FAILURE)
              }
            }
        )
    compositeDisposable.add(muteUnmuteAllDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun endMeeting(audio: Boolean) {
    if(this.userInMeeting) {
      userFlowStatus.postValue(UserFlowStatus.END_MEETING)
      val endDisposable = GMWebUAPIRepository.instance.endMeeting(audio)
          .subscribe(
              {
                if (it.responseType == UAPIResponseType.END_MEETING.value) {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_ENDMEETING,
                      "MeetingUserViewModel endMeeting() - Successfully ended meeting",
                      null, null, false)
                  userFlowStatus.postValue(UserFlowStatus.END_MEETING_SUCCESS)
                }
              },
              {
                handleError(it, LogEventValue.UAPI_ENDMEETING, "MeetingUserViewModel endMeeting() - ")
                retryFailed(RetryStatus.UAPI_END_MEETING)
                userFlowStatus.postValue(UserFlowStatus.END_MEETING_FAILED)
              }
          )
      compositeDisposable.add(endDisposable)
    }
  }

  @Trace(category = MetricCategory.NETWORK)
  fun dialOut(countryCode: String, phoneNumber: String, phoneExtension: String?, locale: String?) {
    val dialOutDisposable = GMWebUAPIRepository.instance.dialOut(countryCode, phoneNumber, phoneExtension,
        0, AppAuthUtils.getInstance().firstName, AppAuthUtils.getInstance().lastName, null, locale)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.DIAL_OUT.value && AppAuthUtils.getInstance().isUserTypeGuest) {
                updateLastUsedPhoneInDb(countryCode, phoneNumber);
              }
              mlogger.attendeeModel.countryCode = countryCode
              mlogger.attendeeModel.phoneNumber = phoneNumber
              mlogger.attendeeModel.phoneExtension = phoneExtension
              mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_DIALOUT,
                  "MeetingUserViewModel dialOutDisposable() - DialOut Success",
                  null, null, false)
            },
            {
              handleError(it, LogEventValue.UAPI_DIALOUT, "MeetingUserViewModel dialOut() - ")
              retryFailed(RetryStatus.UAPI_DIALOUT)
              userFlowStatus.postValue(UserFlowStatus.DIAL_OUT_FAILED)
            }
        )
    compositeDisposable.add(dialOutDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun cancelDialOut() {
    val cancelDialOutDisposable = GMWebUAPIRepository.instance.cancelDialOut()
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.CANCEL_DIAL_OUT.value) {
                // Not implemented yet
              }
            },
            {
              handleError(it,  LogEventValue.UAPI_DIALOUT, "MeetingUserViewModel cancelDialOut() - ")
            }
        )
    compositeDisposable.add(cancelDialOutDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun dismissAudioUser(audioId: String) {
    val dismisssAudioUserDisposable = GMWebUAPIRepository.instance.dismissAudioUser(audioId)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.DISMISS_AUDIO_PARTICIPANT.value && !userInMeeting) {
                userFlowStatus.postValue(UserFlowStatus.DISMISS_AUDIO_PARTICIPANT)
                mlogger.debug(tag, LogEvent.API_UAPI,
                    LogEventValue.UAPI_DISMISSAUDIOPARTICIPANT, "MeetingUserViewModel dismissAudioUser() - Dismissing " +
                    "Audio user ${audioId} Success",
                    null, null, false)
              }
              // When meeting is active get the response from meeting events
            },
            {
              handleError(it,  LogEventValue.UAPI_DIALOUT, "MeetingUserViewModel dismissAudioUser() - ")
              retryFailed(RetryStatus.UAPI_DISMISS_AUDIO_PARTICIPANT)
            }
        )
    compositeDisposable.add(dismisssAudioUserDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun muteUnmuteUser(userId: String, audioId: String, mute: Boolean) {
    if (currentUserId == userId) {
      userFlowStatus.postValue(UserFlowStatus.MUTE_UNMUTE)
      mlogger.record(LogEvent.FEATURE_MUTEUNMUTE)
    } else{
      mlogger.record(LogEvent.FEATURE_MUTEUNMUTEOTHERS)
      userFlowStatus.postValue(UserFlowStatus.MUTE_UNMUTE)
    }
    val muteUnmuteDisposable = GMWebUAPIRepository.instance.muteUnmuteUser(audioId, mute)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.UPDATE_AUDIO_PARTICIPANT.value) {
                if(mute) {
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MUTE, "MeetingUserViewModel " +
                      "muteUnmuteUser() - Muting ${userId} Success",
                      null, null, false)
                } else{
                  mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UNMUTE, "MeetingUserViewModel " +
                      "muteUnmuteUser() - UnMuting ${userId} Success",
                      null, null, false)
                }
              }
            },
            {
              handleError(it,  LogEventValue.UAPI_DIALOUT, "MeetingUserViewModel muteUnmuteUser() - ")
              if(mute) {
                retryFailed(RetryStatus.UAPI_MUTE_PARTICIPANT)
              }
              if (currentUserId == userId) {
                userFlowStatus.postValue(UserFlowStatus.MUTE_UNMUTE_FAILED)
                isMute = !mute
              }
            }
        )
    compositeDisposable.add(muteUnmuteDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun addChat(chatMessage: String, chatType: String) {
    userFlowStatus.postValue(UserFlowStatus.CHAT_ADD)
    val addChatDisposable = GMWebUAPIRepository.instance.addChat(chatMessage, chatType)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.ADD_CHAT.value) {
                // Do Nothing on Success. This will be handled through meeting events
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_CHAT, "MeetingUserViewModel " +
                    "addChat() - Adding Chat Success",
                    null, null, false)
                mlogger.record(LogEvent.FEATURE_SENDCHAT)
                userFlowStatus.postValue(UserFlowStatus.CHAT_ADD_SUCCESS)
              }
            },
            {
              handleError(it,  LogEventValue.UAPI_CHAT, "MeetingUserViewModel addChat() - ")
              userFlowStatus.postValue(UserFlowStatus.CHAT_ADD_FAILURE)
            }
        )
    compositeDisposable.add(addChatDisposable)
  }

//  @Trace(category = MetricCategory.NETWORK)
//  fun clearChats() {
//    val clearChatDisposable = GMWebUAPIRepository.instance.clearChats()
//        .subscribe(
//            {
//              if (it.responseType == UAPIResponseType.DISMISS_PARTICIPANT.value) {
//                // Do Nothing on Success. This will be handled through meeting events
//              }
//            },
//            {
//              handleError(it,  LogEventValue.UAPI_CHAT, "MeetingUserViewModel clearChats() - ")
//            }
//        )
//    compositeDisposable.add(clearChatDisposable)
//  }

  @Trace(category = MetricCategory.NETWORK)
  fun stopScreenShare(sharingContentId: String, partId: String, name: String, phoneNumber: String?,
                      email: String) {
    val stopScreenShareDisposable = GMWebUAPIRepository.instance.stopScreenShare(sharingContentId, partId, name, phoneNumber, email)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.DISMISS_PARTICIPANT.value) {
                // Do Nothing on Success. This will be handled through meeting events
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_SCREENSHARE,
                    "MeetingUserViewModel stopScreenShare() - Stopped user screenshare before " +
                        "demoting", null, null, false)
              }
            },
            {
              handleError(it,  LogEventValue.UAPI_SCREENSHARE, "MeetingUserViewModel stopScreenShare() - ")
            }
        )
    compositeDisposable.add(stopScreenShareDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun startRecording(recordingName: String?, locale: String?) {
    val startRecordingDisposable = GMWebUAPIRepository.instance.startRecording(recordingName, locale)
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.START_RECORDING.value) {
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_STARTRECORDING,
                    "MeetingUserViewModel startRecording() - Successfully started recording",
                    null, null, false)
                userFlowStatus.postValue(UserFlowStatus.START_RECORDING_SUCCESS)
              }
            },
            {
              handleError(it, LogEventValue.UAPI_STARTRECORDING, "MeetingUserViewModel startRecording() - ")
              userFlowStatus.postValue(UserFlowStatus.START_RECORDING_FAILURE)
            }
        )
    compositeDisposable.add(startRecordingDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun stopRecording() {
    val stopRecordingDisposable = GMWebUAPIRepository.instance.stopRecording()
        .subscribe(
            {
              if (it.responseType == UAPIResponseType.STOP_RECORDING.value) {
                mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_STOPRECORDING,
                    "MeetingUserViewModel stopRecording() - Successfully stopped recording",
                    null, null, false)
                userFlowStatus.postValue(UserFlowStatus.STOP_RECORDING_SUCCESS)
              }
            },
            {
              handleError(it, LogEventValue.UAPI_STARTRECORDING, "MeetingUserViewModel stopRecording() - ")
              userFlowStatus.postValue(UserFlowStatus.STOP_RECORDING_FAILURE)
            }
        )
    compositeDisposable.add(stopRecordingDisposable)
  }

  fun makeSessionCallToCloudFront(contentid: String) {
    FilesManager.baseUrl = AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS + horsesEnvConfig.fileCabinetHost
    val makeSessionDisposable = FilesRespository.instance.createSession(contentid).subscribe(
        {
          cloundFrontCookies = it.headers().values("Set-Cookie")
          if(cloundFrontCookies.size >= 3) {
            userFlowStatus.postValue(UserFlowStatus.VIDEO_SESSION_SUCCESS)
          }
        },
        {
          handleError(it, LogEventValue.FILES_SESSION, "MeetingUserViewModel makeSessionCallToCloudFront() - ")
        }
                                                                                            )
    compositeDisposable.add(makeSessionDisposable)
  }

  @SuppressLint("SimpleDateFormat")
  fun addUsersList(mUsers: MutableList<User>) {
    mUsersList = mUsers
    mProfilePics.clear()
    mProfileInitials.clear()
    usersCount = mUsers.size
    mUsers.sortWith(compareBy { it.timestamp })
    for (users in mUsers) {
      mProfilePics.add(users.profileImage)
      mProfileInitials.add(users.initials)
    }

  }

  @Trace(category = MetricCategory.NETWORK)
  fun addConversation(participantIDs : Array<String>) {
    val addChatDisposable = GMWebUAPIRepository.instance.addConversation(participantIDs)
            .subscribe(
                    {
                      if (it.responseType == UAPIResponseType.ADD_CONVERSATION.value && checkConversationCreated(it.conversationId)) {
                        mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UAPI_CHAT, "MeetingUserViewModel " +
                                "createConversation() - Creating conversation ID Success",
                                null, null, false)
                        conversationId.postValue(it.conversationId)
                      }
                    },
                    {
                      handleError(it,  LogEventValue.UAPI_CHAT, "MeetingUserViewModel createConversation() - ")
                      userFlowStatus.postValue(UserFlowStatus.ADD_CONVERSATION_FAILED)
                    }
            )
    compositeDisposable.add(addChatDisposable)
  }

  @Trace(category = MetricCategory.NETWORK)
  fun enablePrivateChat(checked: Boolean) {
    val enablePrivatChatDisposable = GMWebUAPIRepository.instance.enablePrivateChat(checked)
      .subscribe({
          if (it.responseType == UAPIResponseType.UPDATE_MEETING_OPTION.value) {
            mlogger.debug(tag, LogEvent.API_UAPI, LogEventValue.UPDATE_MEETING_OPTION,
              "MeetingUserViewModel enablePrivateChat() - Meeting Options updated", null, null, false)
            logMixPanelEnableDisablePrivateChat(checked)
          }
        },
        {
          handleError(it, LogEventValue.UPDATE_MEETING_OPTION, "MeetingUserViewModel enablePrivateChat() - ")
        }
      )
    compositeDisposable.add(enablePrivatChatDisposable)
  }

  fun setUserRole(users: User, mContext: Context) {
    val host = mContext.resources.getString(R.string.host_lowercase)
    val coHost = mContext.resources.getString(R.string.roleDelegateHost_lowercase)
    val presenter = mContext.resources.getString(R.string.presenter_lowercase)
    val isPresenter = users.roomRole == AppConstants.PRESENTER || users.promoted
    val isCoHost = users.delegateRole
    val isHost = users.roomRole == AppConstants.HOST
    val nonHostType = if (isPresenter) presenter else mContext.resources.getString(R.string.guest_lowercase)
    userType = if (isHost) if (isCoHost) coHost else host else nonHostType
  }

  fun logMixPanelEnableDisablePrivateChat(chatState: Boolean) {
    if (chatState) {
      mlogger.mixpanelManagePrivateChat.privateChatAction = AppConstants.ENABLE_PRIVATE_CHAT
      mlogger.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT,
              "${AppConstants.MIXPANEL_EVENT} ${LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT.value}", null, null, false, true)
    } else {
      mlogger.mixpanelManagePrivateChat.privateChatAction = AppConstants.DISABLE_PRIVATE_CHAT
      mlogger.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT,
              "${AppConstants.MIXPANEL_EVENT} ${LogEventValue.MIXPANEL_MANAGE_PRIVATE_CHAT.value}", null, null, false, true)
    }
  }

  /**
   * Send new private chat logs to mixpanel
   */
  fun logMixPanelEventForNewPrivateChat(chatType: String, userType: String?, privateChatFrom: String) {
    mlogger.mixpanelNewPrivateChat.chatType = chatType
    mlogger.mixpanelNewPrivateChat.recipientUserType = userType
    mlogger.mixpanelNewPrivateChat.actionSource = privateChatFrom
    mlogger.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_NEW_PRIVATE_CHAT, AppConstants.MIXPANEL_EVENT+LogEventValue.MIXPANEL_NEW_PRIVATE_CHAT.value, null, null, false, true)
  }

  fun checkConversationCreated(conversationId: String): Boolean {
    var isCreated = false
    if (createdConversationList.isEmpty()) {
      createdConversationList.add(conversationId)
    }
    if (createdConversationList.contains(conversationId)) {
      isCreated = true
    } else {
      createdConversationList.add(conversationId)
    }
    return isCreated
  }

  fun checkPrivateChatVersionToastShown(conversationId : String): Boolean {
    var isToastShown = false
    if (privateChatVersionToastList.isEmpty()) {
      privateChatVersionToastList.add(conversationId)
      return isToastShown
    }
    if (privateChatVersionToastList.contains(conversationId)) {
      isToastShown = true
    } else {
      privateChatVersionToastList.add(conversationId)
    }
    return isToastShown
  }
 }