package com.pgi.convergencemeetings.meeting.gm5.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.enums.uapi.UAPIResponseType
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.*
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.meeting.gm5.data.model.Content
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.models.Chat
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.interceptors.NoConnectivityException
import com.pgi.network.models.UAPIResponse
import retrofit2.HttpException
import java.net.UnknownHostException


/**
 * Created by Sudheer Chilumula on 10/21/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
open class BaseMeetingViewModel: ViewModel() {

  val users: MutableLiveData<List<User>> by lazy { MutableLiveData<List<User>>() }
  val chats: MutableLiveData<List<com.pgi.convergencemeetings.models.Chat>> by lazy { MutableLiveData<List<com.pgi.convergencemeetings.models.Chat>>() }
  val content: MutableLiveData<Content> by lazy { MutableLiveData<Content>() }
  val activeTalker: MutableLiveData<ActiveTalker> by lazy { MutableLiveData<ActiveTalker>() }
  val userFlowStatus: MutableLiveData<UserFlowStatus> by lazy { MutableLiveData<UserFlowStatus>() }
  val audioJoinedStatus : MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>()}
  val meetingStatus: MutableLiveData<MeetingStatus> by lazy { MutableLiveData<MeetingStatus>() }
  // Not using these private vars for now. Will need to make it public whe we start using these status
  val meetingLockStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val waitingRoomEnabledStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val guestWaitingList: MutableLiveData<List<User>> by lazy { MutableLiveData<List<User>>() }
  val meetingMuteStatus: MutableLiveData<MeetingMuteStatus> by lazy { MutableLiveData<MeetingMuteStatus>() }
  val meetingRecordStatus: MutableLiveData<MeetingRecordStatus> by lazy { MutableLiveData<MeetingRecordStatus>() }
  val chatReceived: MutableLiveData<Chat> by lazy { MutableLiveData<Chat>() }
  val audioStatus: MutableLiveData<AudioStatus> by lazy { MutableLiveData<AudioStatus>() }
  val audioType: MutableLiveData<AudioType> by lazy { MutableLiveData<AudioType>() }
  val cameraStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val cameraConnectingStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val foxdenConnectionStatus: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>()}
  val muteStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val muteBtnStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val internetStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val retryStatus: MutableLiveData<RetryStatus> by lazy { MutableLiveData<RetryStatus>() }
  val isMusicOnHold: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>()}
  val frictionFreeEnabledStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val privateChatLocked: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val tabChangeStatus: MutableLiveData<Int> by lazy {MutableLiveData<Int>()}
  val appForegroundEvent: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>()}
  val lowBandwidthEvent: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>()}
  val privateChatEnableStatus: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
  val offlineChats: MutableLiveData<MutableList<Chat>> by lazy { MutableLiveData<MutableList<Chat>>() }
  val conversationId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
  val chatUnreadCountUpdated: MutableLiveData<String> by lazy { MutableLiveData<String>() }
  val isOpenChatFragment: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
  val chatClearedEvent: MutableLiveData<String> by lazy { MutableLiveData<String>() }

  val mlogger = CoreApplication.mLogger
  open val tag: String = BaseMeetingViewModel::class.java.simpleName


  fun retryFailed(statusCode: Int) {
    retryStatus.postValue(RetryStatus(400, statusCode))
  }

  fun handleError(it: Throwable, value: LogEventValue, msg: String) {
    if(it is HttpException) {
      val errorJsonString = it.response().errorBody()?.string()
      val response = Gson().fromJson<UAPIResponse>(errorJsonString, UAPIResponse::class.java)

      if (response != null && response.responseType == UAPIResponseType.ERROR.value) {
        for (error in response.errors) {
          mlogger.error(tag, LogEvent.ERROR, value, "${msg} - ${error.errorMessage}:${error.errorMessage}")
        }
      }
    } else if(it is NoConnectivityException || it is UnknownHostException) {
      retryFailed(RetryStatus.NOCONNECTIVITY)
    } else {
      mlogger.error(tag, LogEvent.EXCEPTION, value, "${msg}  ${it.message}", it)
    }
  }
}