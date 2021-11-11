package com.pgi.convergencemeetings.meeting.gm5.ui

import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.SoftphoneConstants
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.*
import com.pgi.convergencemeetings.meeting.gm5.data.model.*
import com.pgi.convergencemeetings.models.Chat
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.interceptors.NoConnectivityException
import com.pgi.network.models.UAPIEvent
import com.pgi.network.models.UAPIMeetingEvent
import com.pgi.network.repository.GMWebUAPIRepository
import io.reactivex.disposables.CompositeDisposable
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer


/**
 * This is the ViewModel for all the UAPI Meeting Events.
 *
 * @author Sudheer R Chilumula
 * @since 5.19
 *
 */
class MeetingEventViewModel : BaseMeetingViewModel() {

  override val tag = MeetingEventViewModel::class.java.simpleName
  private val compositeDisposable = CompositeDisposable()
  private var keepPolling = false
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var usersMap = mutableMapOf<String, User>()
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var waitingUsersMap = mutableMapOf<String, User>()
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var chatsList = emptyList<com.pgi.convergencemeetings.models.Chat>()
  private val contentMap = mutableMapOf<String, Content>()
  private var recordStatus: MeetingRecordStatus = MeetingRecordStatus.RECORDING_STOPPED
  private var currentUserId: String? = null
  private var contentSessionId: String? = null
  private var lastActiveTalker: User? = null
  internal var receivedUsersEvent: Boolean = false
  private var receivedChatEvent: Boolean = false
  private var receivedContentUpdateEvent: Boolean = false
  internal var receivedContentCreatedEvent: Boolean = false
  internal var receivedContentCreatedEventFileShare: Boolean = false;
  internal var receivedContentDeletedEvent: Boolean = false
  internal var receivedRecordingEvent: Boolean = false
  private var receivedAudioParticipantRenamedEvent: Boolean = false;
  internal var receivedWaitingRoomOptionsEvent: Boolean = false;
  private var initCall: Boolean = true
  private var hasPostedHoldValue: Boolean = false
  private val logger = CoreApplication.mLogger
  private var consecutiveFailures: Int = 0
  private var consecutiveUnKnownHostFailures: Int = 0
  private var consecutiveNoConnectivityFailures: Int = 0
  private var consecutiveSocketTimeoutFailures: Int = 0
  private var eventsTimer: Timer? = null
  var deletedContent: Content? = null
  var userInWaitingRoom = false
  var lastMeetingEvent: UAPIMeetingEvent? = null
  var currentWebCamPage = 1
  var totalWebCam = 0
  var initialPage = 1
  var lastPage = 3
  var chatCount:Int? =0
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var offlineChatsList : MutableList<Chat> = mutableListOf()

  fun restartPolling(eventUrl: String?) {
    if(!this.keepPolling) {
      this.keepPolling = true
      compositeDisposable.clear()
      getMeetingEvents(eventUrl)
    }
  }

  private fun startPollTimer() {
    eventsTimer = fixedRateTimer(
        "meetingevents",
        false,
        5000L,
        50000L) {
      if(keepPolling) {
        getMeetingEvents(lastMeetingEvent?._links?.self?.href)
      }
    }
  }

  fun pausePolling() {
    this.keepPolling = false
    compositeDisposable.clear()
    eventsTimer?.cancel()
    eventsTimer?.purge()
  }

  fun keepMeetingAlive() {
    val keepAliveDisposable = GMWebUAPIRepository.instance.keepMeetingAlive().subscribe(
        {
        },
        {
          pausePolling()
          retryFailed(RetryStatus.NOCONNECTIVITY)
        })
    compositeDisposable.add(keepAliveDisposable)
  }

  fun getMeetingEvents(eventUrl: String?) {
    compositeDisposable.clear()
    eventsTimer?.cancel()
    eventsTimer?.purge()
    userInWaitingRoom = eventUrl?.let {
      Uri.parse(eventUrl).getQueryParameter("w") == "1"
    } ?: false
    val meetingEventsDisposable = GMWebUAPIRepository.instance.getMeetingEvents(eventUrl)
        .subscribe(
            {
              parseMeetingEvents(it)
            },
            {
              pareseMeetingEventsError(it)
            },
            {
              onSubscribeComplete()
            }
        )
    compositeDisposable.add(meetingEventsDisposable)
  }

  fun parseMeetingEvents(it: UAPIMeetingEvent) {
    consecutiveFailures = 0
    consecutiveUnKnownHostFailures = 0
    consecutiveNoConnectivityFailures = 0
    consecutiveSocketTimeoutFailures = 0
    lastMeetingEvent = it
    val events: List<UAPIEvent> = it.events
    if(events.any { uapiEvent -> uapiEvent.eventType == UAPIEventType.MEETING_ENDED.name }) {
      meetingStatus.postValue(MeetingStatus.ENDED)
      return
    }
    for ((count, event) in events.withIndex()) {
      try {
        extractDataFromEvent(event)
      } catch (ex: Exception) {
        mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.UAPI_MEETINGEVENT,
                      "MeetingEventViewModel: Got Exception parsing event - ${event.eventType }", ex)
      }
    }
    if(it.reset) {
      reset()
    }
  }

  fun pareseMeetingEventsError(it: Throwable) {
    consecutiveFailures++
    if(it is UnknownHostException) {
      consecutiveUnKnownHostFailures++
    }
    if(it is NoConnectivityException) {
      consecutiveNoConnectivityFailures++
    }
    if(it is SocketTimeoutException) {
      consecutiveSocketTimeoutFailures++
    }
    if(initCall) {
      retryFailed(RetryStatus.UAPI_MEETING_EVENTS) // This call can fail when we loose internet. We shouldn't be showing error popup in this case. SO currently we are showing if it fail on inital try
    }
    if(consecutiveFailures > 2 && !userInWaitingRoom)  {
      retryFailed(RetryStatus.NOCONNECTIVITY)
      this.keepPolling = false
      mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.UAPI_MEETINGEVENT,
                    "Failed getting meeting events, $it ", it)
    } else {
      this.keepPolling = true
      startPollTimer()
    }
  }

  internal fun onSubscribeComplete() {
    initCall = false
    if(receivedUsersEvent) {
      receivedUsersEvent = false
      users.postValue(sortAndOrderUsers())
      updateGuestUserCount()
      if(waitingUsersMap.size >= 1) {
        guestWaitingList.postValue(waitingUsersMap.values.toList())
        mlogger.mixpanelMeetingSecurityModel.numGuestsWaiting = waitingUsersMap.size
      }
    }
    if(receivedAudioParticipantRenamedEvent) {
      receivedAudioParticipantRenamedEvent = false
      users.postValue(sortAndOrderUsers())
    }
    if(receivedChatEvent) {
      receivedChatEvent = false
      chats.postValue(sortAndOrderChats())
    }
    if(receivedContentDeletedEvent) {
      receivedContentDeletedEvent = false
      deletedContent?.type = AppConstants.CONTENT_DELETED
      content.postValue(deletedContent)
    }
    if(receivedContentCreatedEvent && !contentMap.isNullOrEmpty()) {
      receivedContentCreatedEvent = false
      receivedContentCreatedEventFileShare = false;
      content.postValue(contentMap.values.last())
    }
    if(receivedContentUpdateEvent && !contentMap.isNullOrEmpty()) {
      receivedContentUpdateEvent = false
      content.postValue(contentMap.values.last())
      if (contentMap.values.last().dynamicMetaData.action?.toLowerCase() == AppConstants.ACTION_STOP.toLowerCase()) {
        contentMap.remove(contentMap.values.last().id)
      }
    }

    if(receivedRecordingEvent) {
      receivedRecordingEvent = false;
      meetingRecordStatus.postValue(recordStatus)
    }
    if (keepPolling) {
      this.getMeetingEvents(lastMeetingEvent?._links?.self?.href)
    }
  }

  @SuppressWarnings("kotlin:S1479")
  internal fun extractDataFromEvent(event: UAPIEvent) {
    when (event.eventType) {
      UAPIEventType.MEETING_STARTED.type -> {
        meetingStatus.postValue(MeetingStatus.STARTED)
        CommonUtils.setIsMeetingActive(true)
      }

      UAPIEventType.MEETING_JOINED.type -> {
        receivedUsersEvent = true
        addUser(event)
      }

      UAPIEventType.MEETING_LOCKED.type -> {
        meetingLockStatus.postValue(true)
        CommonUtils.setIsMeetingLocked(true)
      }

      UAPIEventType.MEETING_UNLOCKED.type -> {
        meetingLockStatus.postValue(false)
        CommonUtils.setIsMeetingLocked(false)
      }

      UAPIEventType.MEETING_MUTED.type -> {
        meetingMuteStatus.postValue(MeetingMuteStatus.MUTED)
      }

      UAPIEventType.MEETING_UNMUTED.type -> {
        meetingMuteStatus.postValue(MeetingMuteStatus.UN_MUTED)
      }

      UAPIEventType.MEETING_KEPT_ALIVE.type -> {
        meetingStatus.postValue(MeetingStatus.KEPT_ALIVE)
      }

      UAPIEventType.MEETING_INACTIVITY_WARNING.type -> {
        meetingStatus.postValue(MeetingStatus.INACTIVITY_WARNING)
      }

      UAPIEventType.MEETING_ENDED.type -> {
        meetingStatus.postValue(MeetingStatus.ENDED)
        keepPolling = false

        CommonUtils.setIsMeetingActive(false)
      }

      UAPIEventType.PARTICIPANT_RECONNECTED.type -> {
        receivedUsersEvent = true
        handleReconnect(event)
      }

      UAPIEventType.PARTICIPANT_DIAL_OUT_INITIATED.type -> {
        receivedUsersEvent = true
        handleUserDialOutInitiated(event)
      }

      UAPIEventType.PARTICIPANT_DIAL_OUT_SUCCEEDED.type,
      UAPIEventType.SIP_DIAL_OUT_SUCCEEDED.type -> {
        receivedUsersEvent = true
        handleUserDialOutSuccess(event)
      }

      UAPIEventType.PARTICIPANT_DIAL_OUT_TIMED_OUT.type,
      UAPIEventType.PARTICIPANT_DIALOUT_CANCELED.type,
      UAPIEventType.PARTICIPANT_DIAL_OUT_FAILED.type -> {
        receivedUsersEvent = true
        handleUserDialOutFailed(event)
      }

      UAPIEventType.PARTICIPANT_PROMOTED.type -> {
        receivedUsersEvent = true
        updateUserRole(event, true)
      }
      UAPIEventType.PARTICIPANT_DEMOTED.type -> {
        receivedUsersEvent = true
        updateUserRole(event, false)
      }

      UAPIEventType.PARTICIPANT_DISMISSED.type -> {
        receivedUsersEvent = true
        handleUserDismissed(event)
        handleOfflineUserChats(event)
      }

      UAPIEventType.PARTICIPANT_ADMITTED.type -> {
        receivedUsersEvent = true
        handleUserAdmitted(event)
      }

      UAPIEventType.AUDIO_PARTICIPANT_ADMITTED.type -> {
        receivedUsersEvent = true
        handleAudioUserAdmitted(event)
      }

      UAPIEventType.PARTICIPANT_DISCONNECTED.type,
      UAPIEventType.PARTICIPANT_LEFT.type -> {
        receivedUsersEvent = true
        handleUserLeft(event)
        handleOfflineUserChats(event)
      }

      UAPIEventType.AUDIO_PARTICIPANT_JOINED.type -> {
        receivedUsersEvent = true
        handleAudioUserJoin(event)
      }

      UAPIEventType.AUDIO_PARTICIPANT_LEFT.type -> {
        receivedUsersEvent = true
        handleAudioUserLeft(event)
        audioJoinedStatus.postValue(false)
      }

      UAPIEventType.MUTE_AUDIO_PARTICIPANT.type,
      UAPIEventType.UNMUTE_AUDIO_PARTICIPANT_FAILED.type -> {
        receivedUsersEvent = true
        handleAudioUserMute(event, true)
      }

      UAPIEventType.MUTE_AUDIO_PARTICIPANT_FAILED.type,
      UAPIEventType.UNMUTE_AUDIO_PARTICIPANT.type -> {
        receivedUsersEvent = true
        handleAudioUserMute(event, false)
      }

      UAPIEventType.ACTIVE_TALKERS.type -> {
        handleActiveTalkers(event)
      }

      UAPIEventType.CONTENT_CREATED.type -> {
        receivedContentCreatedEvent = true
        if (!getContentInfoFromEvent(event).staticMetadata.documentUrl.isNullOrEmpty()) {
          receivedContentCreatedEventFileShare = true
        }
        handleContentCreate(event)
        if (receivedContentDeletedEvent) {
          // starting new content so ignore old content deleted
          receivedContentDeletedEvent = false
        }
      }

      UAPIEventType.CONTENT_UPDATED.type -> {
        receivedContentUpdateEvent = true
        handleContentUpdate(event)
      }

      UAPIEventType.CONTENT_DELETED.type -> {
        receivedContentDeletedEvent = true
        handleContentDeleted(event)
      }
      UAPIEventType.MEETING_OPTION_WS_UPDATED.type -> {
        when(event.name) {
          UAPIEventName.WAITING_ROOM_ENABLED.eventName -> {
            receivedWaitingRoomOptionsEvent = true
            handleWaitingRoomEvent(event)
          }
          UAPIEventName.FRICTION_FREE_ENABLED.eventName -> {
            handleFrictionFreeEvent(event)
          }
          UAPIEventName.PRIVATE_CHAT_ENABLED.eventName -> {
            handlePrivateChatEnableDisable(event)
          }
        }
      }

      UAPIEventType.DISMISS_AUDIO_PARTICIPANT_FAILED.type,
      UAPIEventType.AUDIO_MEETING_STARTED.type,
      UAPIEventType.START_AUDIO_MEETING_FAILED.type,
      UAPIEventType.AUDIO_MEETING_LOCK_SYNC_FAILED.type,
      UAPIEventType.AUDIO_MEETING_ENDED.type,
      UAPIEventType.CONTENT_CREATE_FAILED.type,
      UAPIEventType.CONTENT_UPDATE_FAILED.type,
      UAPIEventType.CONTENT_DELETE_FAILED.type,
      UAPIEventType.START_RECORDING_INITIATED.type,
      UAPIEventType.RECORDING_INFORMATION_UPDATED.type,
      UAPIEventType.STOP_RECORDING_INITIATED.type -> {
        // no implementation these events aren't handled
      }

      UAPIEventType.STOP_RECORDING_FAILED.type -> {
        receivedRecordingEvent = true
        recordStatus = MeetingRecordStatus.RECORDING_STOP_FAILED
      }

      UAPIEventType.START_RECORDING_FAILED.type -> {
        receivedRecordingEvent = true
        recordStatus = MeetingRecordStatus.RECORDING_START_FAILED
      }

      UAPIEventType.RECORDING_STARTED.type -> {
        receivedRecordingEvent = true
        recordStatus = MeetingRecordStatus.RECORDING_STARTED
      }

      UAPIEventType.RECORDING_STOPPED.type -> {
        receivedRecordingEvent = true
        recordStatus = MeetingRecordStatus.RECORDING_STOPPED
      }

      UAPIEventType.CHAT_ADDED.type,
      UAPIEventType.PRIVATE_CHAT_ADDED.type -> {
        receivedChatEvent = true
        handleChatAdd(event)
      }

      UAPIEventType.CHAT_CLEARED.type -> {
        receivedChatEvent = true
        handleChatClear(event)
      }

      UAPIEventType.AUDIO_PART_RENAMED.type -> {
        receivedAudioParticipantRenamedEvent = true
        handleAudioParticipantRenamed(event)
      }

      UAPIEventType.CONVERSATION_ADDED.type -> {
        handleAddConversation(event)
      }

      UAPIEventType.ADD_CONVERSATION_FAILED.type-> {
        handleErrorConversation(event)
      }

      UAPIEventType.ADD_CHAT_FAILED.type-> {
        handleChatFailure(event)
      }

      else -> {
        mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MEETINGEVENT,
            "MeetingEventViewModel extractDataFromEvent() =- Got new event type")
      }
    }
  }

  /**
   * This is the method where we parse the event and map it to the user object
   *
   * @see User
   * @see UAPIEvent
   */
  internal fun getUserFromEvent(event: UAPIEvent): User {
    val eventUser = User()
    eventUser.id = event.participantId ?: event.webParticipantId
    eventUser.timestamp = event.timestamp
    val firstName = event.firstName ?: event.givenName ?: event.ani ?: ""
    val lastName = event.lastName ?: event.familyName ?: ""
    if (!CommonUtils.isUsersLocaleJapan()) {
      eventUser.firstName = firstName
      eventUser.lastName = lastName
    } else {
      eventUser.firstName = lastName
      eventUser.lastName = firstName
    }
    eventUser.name = when {
      !eventUser.firstName.isNullOrEmpty() && !eventUser.lastName.isNullOrEmpty() -> eventUser.firstName + " " + eventUser.lastName
      !eventUser.firstName.isNullOrEmpty() -> event.firstName
      event.ani != null && event.ani!!.contains(SoftphoneConstants.GM) -> "Unknown"
      else -> event.ani
    }
    if (eventUser.firstName.isNullOrEmpty()) {
      eventUser.name = event.ani
      eventUser.firstName = event.ani
    }
    eventUser.initials = CommonUtils.getNameInitials(eventUser.firstName, eventUser.lastName)
    eventUser.email = event.email
    eventUser.profileImage =  event._links?.profileImage?.href
    eventUser.roomRole = event.roomRole ?: "GUEST"
    eventUser.delegateRole = event.delegateRole ?: false
    eventUser.hasControls = (eventUser.roomRole != "GUEST") || event.delegateRole == true
    eventUser.isInAudioWaitingRoom = event.waiting ?: false

    eventUser.audio.id = event.audioParticipantId ?: event.partId
    eventUser.audio.confId = event.confId ?: event.audioConferenceId
    eventUser.audio.subConfId = event.subConfId ?: ""
    eventUser.audio.company = event.company ?: event.companyName ?: ""
    eventUser.audio.partType = event.partType ?: "NORMAL"
    eventUser.audio.phone = event.phone ?: ""
    eventUser.audio.phoneExt = event.phoneExt ?: event.phoneExtension ?: ""
    eventUser.audio.ani = event.ani ?: ""
    eventUser.audio.dnis = event.dnis ?: ""
    eventUser.audio.codec = event.codec ?: ""
    eventUser.audio.hold = event.hold ?: false
    eventUser.audio.mute = event.mute ?: false
    eventUser.audio.listenOnly = event.listenOnly ?: false
    eventUser.audio.listenLevel = event.listenLevel ?: 1
    eventUser.audio.voiceLevel = event.voiceLevel ?: 1
    eventUser.audio.dialoutId = event.dialoutId

    return eventUser
  }

  /**
   * This is the method where we parse the event and map it to the content object
   *
   * @see Content
   * @see UAPIEvent
   */
  fun getContentInfoFromEvent(event: UAPIEvent): Content {
    val content = Content()
    content.id = event.contentId!!
    if (event.eventType == UAPIEventType.CONTENT_CREATED.type) {
      content.visible = event.visible!!
      content.stageId = event.stageId
      content.type = event.type
      content.version = event.version
      content.allowGuestUpdate = event.allowGuestUpdate!!
      content.staticMetadata = Gson().fromJson(event.staticMetadata, StaticMetaData::class.java)
      if (content.staticMetadata.streamSessionId == null) {
        content.staticMetadata.streamSessionId = contentSessionId // Since we get the streamseesionid only on create we need to pass this on
      }
      if (content.type == AppConstants.WHITEBOARD) {
        content.dynamicMetaData = Gson().fromJson(event.dynamicMetadata, DynamicMetaData::class.java)
      }
    } else if (event.eventType == UAPIEventType.CONTENT_UPDATED.type) {
      content.staticMetadata.streamSessionId = contentSessionId // Since we get the streamseesionid only on create we need to pass this on
      content.dynamicMetaData = Gson().fromJson(event.dynamicMetadata, DynamicMetaData::class.java)
      try {
        val format = SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val time: Date = format.parse(event.timestamp)
        content.dynamicMetaData.eventTime = time.time
      } catch (e: Exception) {
        mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.SCREENSHARE, e.message.toString(), e, null, false, false)
      }
      processContentUpdateEvent(content)
    }
    else if(event.eventType == UAPIEventType.CONTENT_DELETED.type){
      content.type = event.type
      content.id = event.contentId!!
    }
    return content
  }
  /**
   * This is the method where we process the content received in content update event
   *
   * @see Content
   */
  private fun processContentUpdateEvent(content: Content) {
    var partId: String? = null
    if(content.dynamicMetaData.type == AppConstants.WHITEBOARD) {
      partId = content.dynamicMetaData.whiteboardPresenter.partId
    } else if (content.dynamicMetaData.type == AppConstants.SCREEN_SHARE) {
      partId = content.dynamicMetaData.screenPresenter.partId
    }
    val user = usersMap[partId]
    if (user != null && content.dynamicMetaData.action?.toLowerCase() == AppConstants.ACTION_START.toLowerCase()) {
      user.isSharing = true
      content.user = user
    }
    if (user != null && content.dynamicMetaData.action?.toLowerCase() == AppConstants.ACTION_STOP.toLowerCase()) {
      user.isSharing = false
    }
  }

  /**
   * This is the method where we parse the event and map it to the chat object
   *
   * @see Chat
   * @see UAPIEvent
   */
  private fun getChatFromEvent(event: UAPIEvent): com.pgi.convergencemeetings.models.Chat {
    val chat = Chat()
    val user = usersMap[event.participantId]
    if (user != null) {
      chat.conversationId = event.conversationId
      chat.message = decodeHTMLChars(event.chatMsg!!)
      chat.timestamp = formatChatDate(event.timestamp)
      if (CommonUtils.isUsersLocaleJapan()) {
        chat.firstName = user.lastName
        chat.lastName = user.firstName
      } else {
        chat.firstName = user.firstName
        chat.lastName = user.lastName
      }
      chat.firstName = user.firstName
      chat.lastName = user.lastName
      chat.initials = CommonUtils.getNameInitials(chat.firstName, chat.lastName)
      chat.webPartId = user.id
      chat.profileImage = user.profileImage
      chat.chatMessageState = ChatMessageState.RECEIVED
    }
    return chat
  }

  /**
   * This is to sort users based on role and timestamp
   *
   * Sort order
   *    - Host (Self)
   *    - Other Hosts (Sorted by timestamp)
   *    - Presenter (Sorted by timestamp)
   *    - Others include Guests, VRC (Sorted by Timestamp)
   */
  private fun sortAndOrderUsers(): List<User> {
    return usersMap.values.toList().filter { user -> user.active }.sortedWith(
        compareBy(
            { getRoomRoleOrder(it) },

            {
              try {
                if (it.timestamp.contains(".")) {
                  SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.US).parse(it.timestamp)
                } else {
                  SimpleDateFormat(AppConstants.SHORT_DATE_FORMAT, Locale.US).parse(it.timestamp)
                }
              } catch (e: Exception) {
                Date()
              }
            }
        )
    )
  }

  private fun getRoomRoleOrder(user: User): Int {
    return when (user.roomRole?.toLowerCase()) {
      AppConstants.HOST, AppConstants.HOST.toLowerCase() -> {
        CommonUtils.setIsOwnMeeting(user.isSelf);
        when {
          user.isSelf -> 0
          else -> 1
        }
      }
      AppConstants.PRESENTER, AppConstants.PRESENTER.toLowerCase() -> {
        2
      }
      else -> {
        when {
          user.promoted -> 3
          user.id != user.audio.id -> 4
          else -> 5
        }
      }
    }
  }

  /**
   * This is to sort chats based on timestamp
   */
  private fun sortAndOrderChats(): List<com.pgi.convergencemeetings.models.Chat> {
    return chatsList.sortedWith(
        compareBy { it.timestamp }
    )
  }

  /**
   * This is to format Chat date
   */
  private fun formatChatDate(timestamp: String): String {
    return try {
      val df = SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.US)
      df.timeZone = TimeZone.getTimeZone(AppConstants.TIME_ZONE_GMT)
      val date = df.parse(timestamp)
      df.timeZone = TimeZone.getDefault()
      df.applyPattern(AppConstants.CHAT_TIME_FORMAT)
      df.format(date)
    } catch (e: ParseException) {
      timestamp
    }
  }

  /**
   * This is to decode the HTML chars in a string
   */
  private fun decodeHTMLChars(msg: String): String {
    return if (Build.VERSION.SDK_INT >= 24) {
      Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
      @Suppress("DEPRECATION")
      Html.fromHtml(msg).toString()
    }
  }

  internal fun addUser(event: UAPIEvent): User? {
    val eventUser = getUserFromEvent(event)
    if (eventUser.id != null && currentUserId == eventUser.id) {
      eventUser.isSelf = true
    }
    if (eventUser.id == null && eventUser.audio.id != null) {
      eventUser.id = eventUser.audio.id
    }
    if (!eventUser.roomRole.equals(AppConstants.RECORDER, ignoreCase = true)) { // Adding only those with userrrole:  HOST, GUEST, PRESENTER & VRC
      if(eventUser.roomRole.equals(AppConstants.WAITING_VRC, ignoreCase = true) || eventUser.roomRole.equals(AppConstants.WAITING_GUEST, ignoreCase = true)) {
        eventUser.id?.let{waitingUsersMap[it] = eventUser}
      } else {
        eventUser.active = true
      }
      usersMap[eventUser.id!!] = eventUser
      return usersMap[eventUser.id!!]
    }
    return null
  }

  private fun removeUser(key: String) {
    usersMap.remove(key)
    waitingUsersMap.remove(key)
    guestWaitingList.postValue(waitingUsersMap.values.toList())
    mlogger.mixpanelMeetingSecurityModel.numGuestsWaiting = waitingUsersMap.size
  }

  private fun updateGuestUserCount() {
    var numGuests = 0
    for (user in usersMap.values) {
      if (user.roomRole == AppConstants.GUEST) {
        numGuests++
      }
    }
    mlogger.meetingModel.numGuests = numGuests
  }

  private fun handleUserDialOutInitiated(event: UAPIEvent) {
    val eventUser = usersMap[event.webParticipantId]
    if (eventUser != null) {
      eventUser.audio = Audio() // Clear out existing audio
      eventUser.audio.id = event.audioParticipantId
      eventUser.audio.isConnecting = true // This is to show a spinner in participant list while dialout is in progress
      eventUser.audio.isConnected = false
    }
  }

  internal fun handleAudioUserJoin(event: UAPIEvent) {
    val eventUser = getUserFromEvent(event)
    val user = event.partId?.let { getUserByAudioId(it) }
    event.partId?.let{ mlogger.attendeeModel.audioParticipantId = it }
    if (user != null) {
      user.audio = eventUser.audio
      user.audio.isConnecting = false
      user.audio.isConnected = true
      mlogger.attendeeModel.audiocodec = user.audio.codec
    } else {
      // If We do not get a web id associated with audio we are adding user to the map
      // If we know its a VOIP user make the user inactive so that they wont be displayed in UI.
      val ani = event.ani.toString()  // create local copy to get around error casting to string
      if (ani.isNotEmpty() && usersMap.get(ani.removePrefix(AppConstants.GMDPREFIX)) != null || !ani.contains(SoftphoneConstants.GM) ) {
        eventUser.audio.isVoip = ani.contains(SoftphoneConstants.GM)
        eventUser.audio.isDialIn = !event.dnis.isNullOrEmpty()
      }
      eventUser.audio.isDialOut = event.dnis.isNullOrEmpty()
      eventUser.id = eventUser.audio.id
      eventUser.audio.isConnecting = false
      eventUser.audio.isConnected = true
      if (eventUser.roomRole.equals(AppConstants.GUEST, ignoreCase = true) && eventUser.isInAudioWaitingRoom) {
        eventUser.id?.let { waitingUsersMap[it] = eventUser }
        eventUser.active = false
      } else {
        eventUser.active = true
      }
      usersMap[eventUser.id!!] = eventUser
      mlogger.attendeeModel.audiocodec = eventUser.audio.codec
    }
    var uid : String? = event.ani
    if (uid != null && uid[0] == 'G' && uid?.length > 3) {
      uid = uid.substring(3)
      if (uid == currentUserId) {
        mlogger.attendeeModel.dnis = event.dnis
        usersMap[eventUser.id]?.isSelf = true
      }
    }
    guestWaitingList.postValue(waitingUsersMap.values.toList())
  }

  private fun handleUserDialOutSuccess(event: UAPIEvent) {
    val user = usersMap[event.webParticipantId]
    var audioUser : User? = null
    if(event.eventType == UAPIEventType.PARTICIPANT_DIAL_OUT_SUCCEEDED.type){
      audioUser = event.audioParticipantId?.let { getUserByAudioId(it)}
    }
    else {
      audioUser = usersMap[event.audioParticipantId]
    }
    if (user != null) {
      if (audioUser != null) {
        user.audio = audioUser.audio
        if(user.audio.hold && user.isSelf) {
          isMusicOnHold.postValue(true)
        } else if(user.delegateRole || user.roomRole == "HOST") {
          if(!hasPostedHoldValue) {
            isMusicOnHold.postValue(false)
            hasPostedHoldValue = true;
          }
        }
        removeUser(event.audioParticipantId!!) // On dialout success remove the audio user we added to the list on Audio Join
      }
      if (user.audio.isConnected && user.isSelf) {
        userFlowStatus.postValue(UserFlowStatus.DIAL_OUT_SUCCESS)
        audioJoinedStatus.postValue(true)
        if(user.audio.isVoip) {
          mlogger.endMetric(LogEvent.METRIC_SIP_CONNECT, "SIP Connection times in seconds")
        } else if(user.audio.isDialOut) {
          mlogger.endMetric(LogEvent.METRIC_DIALOUT_CONNECT, "Dialout Connection times in seconds")
        } else if(user.audio.isDialIn) {
          mlogger.endMetric(LogEvent.METRIC_DIALIN_CONNECT, "Dialin Connection times in seconds")
        }
      }
    }
  }

  private fun handleUserDialOutFailed(event: UAPIEvent) {
    val user = usersMap[event.webParticipantId]
    if (user != null) {
      user.audio = Audio() // We need to clear this as we have assigned audio id on dialout initiated event
      if (user.isSelf) {
        userFlowStatus.postValue(UserFlowStatus.DIAL_OUT_FAILED)
      }
    }
  }

  private fun updateUserRole(event: UAPIEvent, promoted: Boolean) {
    val user = usersMap[event.participantId]
    if (user != null) {
      user.promoted = promoted
      if (!promoted) {
        user.hasControls = false
        if (user.isSharing) {
          val screenContent = Content()
          screenContent.dynamicMetaData.action = AppConstants.ACTION_STOP
          screenContent.dynamicMetaData.type = AppConstants.SCREEN_SHARE
          user.isSharing = false
          content.postValue(screenContent) // We are dispatching this STOP locally so that we can clear the Screenshare WebView
        }
        user.isSharing = false
        if(user.isSelf) {
          mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_PARTICIPANTDEMOTED,
              "MeetingEventViewModel updateUserRole()")
        }
      } else {
        if(user.isSelf) {
          mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_PARTICIPANTDEMOTED,
              "MeetingEventViewModel updateUserRole()")
        }
        user.hasControls = true
      }
    }
  }

  internal fun getUserByAudioId(id: String): User? {
    for (user in usersMap.values) {
      if (user.audio.id == id) {
        return user
      }
    }
    return null
  }

  fun getUserById(id: String): User? {
    for (user in usersMap.values) {
      if (user.id == id) {
        return user
      }
    }
    return null
  }

  private fun handleUserAdmitted(event: UAPIEvent) {
      val user = event.participantId?.let { getUserById(it) }
      waitingUsersMap.remove(user?.id)
      user?.active = true;
      mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_PARTICIPANTADMITTED,
              "MeetingEventViewModel handleUserAdmitted()")
      user?.roomRole = event.roomRole ?: "GUEST"
      user?.delegateRole = event.delegateRole ?: false
      user?.hasControls = (event.roomRole != "GUEST") || event.delegateRole == true
      guestWaitingList.postValue(waitingUsersMap.values.toList())
      mlogger.mixpanelMeetingSecurityModel.numGuestsWaiting = waitingUsersMap.size
    userFlowStatus.postValue(UserFlowStatus.PARTICIPANT_ADMITTED)
  }

  private fun handleAudioUserAdmitted(event: UAPIEvent) {
    val user = event.audioParticipantId?.let { getUserById(it) }
    waitingUsersMap.remove(user?.id)
    user?.active = true
    mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_AUDIO_PARTICIPANTADMITTED,
            "MeetingEventViewModel handleAudioUserAdmitted()")
    user?.roomRole = event.roomRole ?: "GUEST"
    user?.delegateRole = event.delegateRole ?: false
    user?.hasControls = (event.roomRole != "GUEST") || event.delegateRole == true
    guestWaitingList.postValue(waitingUsersMap.values.toList())
    mlogger.mixpanelMeetingSecurityModel.numGuestsWaiting = waitingUsersMap.size
    userFlowStatus.postValue(UserFlowStatus.AUDIO_PARTICIPANT_ADMITTED)
  }

  private fun handleUserDismissed(event: UAPIEvent) {
    val user = usersMap[event.participantId]
    if (user != null && user.isSelf) {
      mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_PARTICIPANTDISMISSED,
          "MeetingEventViewModel handleUserDismissed()")
      when(event.reason){
        UserFlowStatus.DISMISSED_BY_HOST.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_BY_HOST)
        UserFlowStatus.DISMISSED_ROOM_AT_CAPACITY.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_ROOM_AT_CAPACITY)
        UserFlowStatus.DISMISSED_INACTIVE_PARTICIPANT.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_INACTIVE_PARTICIPANT)
        UserFlowStatus.DISMISSED_LOCK.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_LOCK)
        UserFlowStatus.DISMISSED_WAIT_TIMEOUT_HOST.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_WAIT_TIMEOUT_HOST)
        UserFlowStatus.DISMISSED_WAIT_TIMEOUT_ADMIT.status -> userFlowStatus.postValue(UserFlowStatus.DISMISSED_WAIT_TIMEOUT_ADMIT)
        else -> userFlowStatus.postValue(UserFlowStatus.DISMISSED)
      }
    }
    if (user != null && user.isSharing) {
      val screenContent = Content()
      screenContent.dynamicMetaData.action = AppConstants.ACTION_STOP
      screenContent.dynamicMetaData.type = AppConstants.SCREEN_SHARE
      user.isSharing = false
      content.postValue(screenContent) // We are dispatching this STOP locally so that we can clear the Screenshare WebView
    }
    user?.active = false
    removeUser(event.participantId!!)
  }

  private fun handleUserLeft(event: UAPIEvent) {
    val user = usersMap[event.participantId]
    if (user != null) {
//      if (!user.audio.isConnected || user.audio.isVoip) { Commenting this check as users can be on voip but still loose web presence due to connectivity issues.
      if (!user.audio.isConnected) {
        user.active = false
        removeUser(event.participantId!!)
      } else {
        //We have audio presence still
        user.id = user.audio.id
      }
      if (user.isSharing) {
        val screenContent = Content()
        screenContent.dynamicMetaData.action = AppConstants.ACTION_STOP
        screenContent.dynamicMetaData.type = AppConstants.SCREEN_SHARE
        user.isSharing = false
        content.postValue(screenContent) // We are dispatching this STOP locally so that we can clear the Screenshare WebView
      }
    }
  }

  private fun handleReconnect(event: UAPIEvent) {
    val user = usersMap[event.participantId]
    if (user != null) {
      user.reconnected = true

    }
  }

  private fun handleAudioUserLeft(event: UAPIEvent) {
    val user = getUserByAudioId(event.partId!!)
    when {
      (user?.id != null && user.id == event.partId) -> {
        user.active = false
        if (user.isSelf) {
          guestWaitingList.postValue(waitingUsersMap.values.toList())
          userFlowStatus.postValue(UserFlowStatus.DIAl_OUT_DISCONNECTED)
        }
        removeUser(user.id.toString())
      }
      user?.id != null -> {
        if (user.isSelf && !user.audio.isVoip) {
          guestWaitingList.postValue(waitingUsersMap.values.toList())
          userFlowStatus.postValue(UserFlowStatus.DIAl_OUT_DISCONNECTED)
        }
        if(user.delegateRole || user.roomRole == "HOST") {
            hasPostedHoldValue = false;
        }
        user.audio = Audio() // Clearing out audio state
      }
    }
  }


  internal fun handleAudioParticipantRenamed(event: UAPIEvent) {
    var user = event.audioParticipantId?.let { getUserByAudioId(it) }
    event.audioParticipantId?.let{ mlogger.attendeeModel.audioParticipantId = it }
    if(!user?.firstName.equals(event.firstName) || !user?.lastName.equals(event.lastName) ) {
      user?.firstName = event.firstName
      user?.lastName = event.lastName
      user?.name = CommonUtils.getFullName(user?.firstName, user?.lastName)
      user?.initials = CommonUtils.getNameInitials(user?.firstName, user?.lastName)
    }
    usersMap[user?.id!!] = user
  }


  private fun handleAudioUserMute(event: UAPIEvent, mute: Boolean) {
    val user = getUserByAudioId(event.audioParticipantId!!)
    if (user?.audio?.id != null) {
      user.audio.mute = mute
      user.controls.isMuted = mute
      if (user.isSelf) {
        if(mute) {
          mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_MUTEAUDIOPARTICIPANT,
              "MeetingEventViewModel handleAudioUserMute()")
        } else {
          mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_UNMUTEAUDIOPARTICIPANT,
              "MeetingEventViewModel handleAudioUserMute()")
        }
        userFlowStatus.postValue(UserFlowStatus.MUTE_UNMUTE_SUCCESS)
      }
    }
  }

  private fun handleActiveTalkers(event: UAPIEvent) {
    if (event.talkingParticipants!!.isNotEmpty()) {
      val user = getUserByAudioId(event.talkingParticipants!![0])
      if (user != null && !user.name.isNullOrEmpty() && !user.name!!.contains(SoftphoneConstants.GM)) { // This is to make sure that SIP ani doesn't show up as active talker
        lastActiveTalker = user
        activeTalker.postValue(ActiveTalker(user, true))
      } else if (lastActiveTalker != null) {
        activeTalker.postValue(ActiveTalker(lastActiveTalker!!, false))
      }
    } else if (lastActiveTalker != null) {
      activeTalker.postValue(ActiveTalker(lastActiveTalker!!, false))
    }
  }

  private fun handleContentCreate(event: UAPIEvent) {
    val content = getContentInfoFromEvent(event)
    if (content != null) {
      if (content.staticMetadata.streamSessionId != null) {
        contentSessionId = content.staticMetadata.streamSessionId // We get the streamSessionId only once on create
      }
      contentMap[content.id] = content
    }
  }

  internal fun handleContentDeleted(event: UAPIEvent) {
    deletedContent = getContentInfoFromEvent(event)
    if (deletedContent != null) {
      contentMap.remove(deletedContent?.id)
    }
  }

  internal fun handleWaitingRoomEvent(event: UAPIEvent) {
    mlogger.meetingModel.waitingRoomEnabled = event.value == true
    when(event.value) {
      true -> waitingRoomEnabledStatus.postValue(true)
      false -> waitingRoomEnabledStatus.postValue(false)
    }
  }

  internal fun handlePrivateChatEnableDisable(event: UAPIEvent) {
    when (event.value) {
      true -> privateChatEnableStatus.postValue(true)
      false -> privateChatEnableStatus.postValue(false)
    }
  }

  internal fun handleFrictionFreeEvent(event: UAPIEvent) {
    when(event.value) {
      true -> { frictionFreeEnabledStatus.postValue(true) }
      false -> { frictionFreeEnabledStatus.postValue(false) }
    }
  }

  private fun handleContentUpdate(event: UAPIEvent) {
    val updateContent = getContentInfoFromEvent(event)
    val cont = contentMap[updateContent.id]
    if (cont != null) {
      cont.dynamicMetaData = updateContent.dynamicMetaData
    } else {
      if (updateContent.dynamicMetaData.newPage != null) {
        updateContent.type = AppConstants.FILE_PRESENTATION
      }
      contentMap[updateContent.id] = updateContent
    }
  }
  fun handleChatAdd(event: UAPIEvent) {
    val chat = getChatFromEvent(event)
    val user = usersMap[event.participantId]
    chat.isHostOrCoHost = user?.roomRole == AppConstants.HOST || user?.delegateRole == true
    if (chat.webPartId == currentUserId) {
      chatsList = chatsList.filter {
       it.chatMessageState != ChatMessageState.SENDING // This is to update the Chat Message state
      }
      chat.isSelf = true
    }
    chatsList += chat
    chatReceived.postValue(chat)
  }

  private fun handleChatClear(event: UAPIEvent) {
    chatsList =chatsList.filter {
      it.conversationId != event.conversationId
    }
    chatClearedEvent.postValue(event.conversationId)
  }

  fun getCurrentUser(): User? {
    return if (currentUserId != null) {
      usersMap[currentUserId!!]
    } else {
      null
    }
  }

  fun setCurrentUserId(partId: String) {
    currentUserId = partId
  }

  fun isCurrentUserPresenter(): Boolean {
    return isUserPresenter(getCurrentUser())
  }

  fun isUserPresenter(user: User?): Boolean {
    var isPresenter = false
    user?.let {
       val roomRole = it.roomRole
       isPresenter = roomRole == AppConstants.PRESENTER || it.promoted
    }
    return isPresenter
  }

  fun addSelfChat(chat: Chat) {
    chatsList += chat
    chats.value = chatsList
  }

  fun updateSelfChatFailure() {
    chatsList.forEach {
      if ((it.conversationId == AppConstants.CHAT_EVERYONE && it.webPartId == currentUserId) || (it.conversationId != AppConstants.CHAT_EVERYONE) && it.chatMessageState == ChatMessageState.SENDING) {
        it.chatMessageState = ChatMessageState.FAILED
      }
    }
    chats.value = chatsList
  }

  fun updateVoipDisconnectStatus() {
    this.getCurrentUser()?.audio = Audio()
    users.value = sortAndOrderUsers()
  }

  fun clearWaitingListOnMeetingEnd() {
    if (waitingUsersMap.isNotEmpty()) {
      waitingUsersMap.clear()
      guestWaitingList.postValue(waitingUsersMap.values.toList())
    }
  }

	fun reset() {
		usersMap.clear()
    users.postValue(sortAndOrderUsers())
    contentMap.clear() // Don't need to post the content
		contentSessionId = null
		chatsList = emptyList()
		chats.postValue(emptyList())
		activeTalker.value = null
		meetingStatus.value = null
        offlineChatsList.clear()
	}

  fun clear() {
    reset()
    currentUserId = null // This is here so that we don't run issues with VOIP connections
    compositeDisposable.clear()
  }

  override fun onCleared() {
    super.onCleared()
    this.clear()
  }

  fun handleAddConversation(event: UAPIEvent) {
    conversationId.postValue(event.conversationId)
  }

  fun handleErrorConversation(event: UAPIEvent) {
    if (event.failStatus == AppConstants.CONVERSATION_EXITS) {
      conversationId.postValue(event.existingConversationId)
    } else {
      userFlowStatus.postValue(UserFlowStatus.ADD_CONVERSATION_FAILED)
    }
    mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_CHAT, "MeetingEventViewModel " +
            "createConversation() - Creating conversation ID Error - ${event.failStatus}",
            null, null, false)
  }

  fun handleChatFailure(event: UAPIEvent) {
    if (event.eventType == AppConstants.CHAT_FAILED) {
      userFlowStatus.postValue(UserFlowStatus.ADD_CHAT_FAILED)
    }
    mlogger.info(tag, LogEvent.API_UAPI, LogEventValue.UAPI_CHAT, "MeetingEventViewModel " +
            "addChat() - Adding Chat Failure - ${event.failStatus}",
            null, null, false)
  }
  fun handleOfflineUserChats(event: UAPIEvent) {
    if (!isEventAlreadyAdded(event)) {
      val chat = Chat()
      chat.webPartId = event.participantId
      chat.offlineTimestamp = event.timestamp
      offlineChatsList.add(chat)
      offlineChats.postValue(offlineChatsList)
    }
  }

  fun isEventAlreadyAdded(event: UAPIEvent): Boolean {
    for (chat in offlineChatsList) {
      if (chat.webPartId == event.participantId) {
        return true
      }
    }
    return false
  }
}