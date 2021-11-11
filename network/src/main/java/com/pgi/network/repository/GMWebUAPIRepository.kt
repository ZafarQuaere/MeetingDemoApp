package com.pgi.network.repository

import com.google.gson.Gson
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.UAPIConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.network.GMWebUAPIServiceAPI
import com.pgi.network.GMWebUAPIServiceManager
import com.pgi.network.UAPIEndPoints
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.models.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * GMWebUAPIRepository is a singleton class that serves as an entry point to all UAPI APIS.
 *
 * @author Sudheer R Chilumula
 * @since 5.18
 * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#overview">GMWeb Universal API Guide</a>
 */
class GMWebUAPIRepository private constructor() : BaseRepository() {

  private object Holder {
    val instance = GMWebUAPIRepository()
  }

  companion object {
    val instance: GMWebUAPIRepository by lazy {
      Holder.instance
    }
  }

  var gmUAPIServiceAPI: GMWebUAPIServiceAPI? = null
  private var lastMeetingEvent: UAPIMeetingEvent? = null
  private val logger = CoreApplication.mLogger

  private fun getUAPIService(): GMWebUAPIServiceAPI {
    return if (gmUAPIServiceAPI == null) {
      gmUAPIServiceAPI = GMWebUAPIServiceManager.create()
      gmUAPIServiceAPI!!
    } else {
      gmUAPIServiceAPI!!
    }
  }

  /**
   * In order to use the UAPI, user agents must first authorize a session using a valid, unexpired PGIID access token.
   * This will return a UAPI access token that authorizes the bearer to access a particular meeting room using the assigned role.
   * The UAPI access token must be included with other requests to the UAPI.
   *
   * @param meetingRoomId      The ID of the meeting room to which access is being requested.
   *
   * @see Observable
   * @see AuthorizeResponse
   * @see AuthorizeRequest
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#authorize-session">Authorize Meeting</a>
   * @since 5.19
   */
  fun authorizeMeeting(meetingRoomId: String): Observable<AuthorizeResponse>  {
    val requestBody = AuthorizeRequest(meetingRoomId)
    return getUAPIService().authorize(requestBody)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout))
  }

  /**
   * This is to get meeting room configuration information required to start a meeting in that room
   * including audio configuration information.
   *
   * @see Observable
   * @see MeetingRoomInfoResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#get-meeting-room-info">Get Meeting Room Info</a>
   * @since 5.19
   */
  fun getMeetingRoomInfo(): Observable<MeetingRoomInfoResponse>  {
    return getUAPIService().getMeetingRoomInfo()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout))
  }

  /**
   * This is to request that the user agent joins the meeting for the room to which the user agent has authenticated.
   *
   * @param lastName       The family (i.e. last) name of the participant joining the meeting. Size must be between 0 and 50 inclusive.
   * @param firstName      The given (i.e. first) name of the participant joining the meeting. Size must be between 0 and 50 inclusive.
   * @param name           The name of the participant joining the meeting. Size must be between 0 and 50 inclusive.
   * @param email          The email of the participant joining the meeting. (Optional). Must be a well-formed email address. Size must be between 0 and 255 inclusive.
   * @param matterNumber   The matter number. (Optional).
   *
   * @see Observable
   * @see JoinMeetingResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#join-meeting">Join Meeting</a>
   * @since 5.19
   */
  fun joinMeeting(lastName: String?, firstName: String?, name: String?, email: String?, matterNumber: String?): Observable<JoinMeetingResponse>  {
    val requestBody = JoinMeetingRequest(lastName, firstName, name, email, matterNumber)
    return getUAPIService().joinMeeting(requestBody)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout))
  }

  /**
   * This is to request that the user agent leaves the meeting for the room to which the user agent has authenticated.
   * A Participant can leave the meeting only by self requesting.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#leave-meeting">Leave Meeting</a>
   * @since 5.19
   */
  fun leaveMeeting(): Observable<UAPIResponse> {
    return getUAPIService().leaveMeeting()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to get events for the meeting for which the user agent has authenticated.
   * The user agent must have joined a meeting and be an active participant in it.
   *
   * @param eventUrl    The url containig the since, tiimout and talkertimestamps
   * @param since       The sequence number of the event after which new events are requested.
   * @param timeout     The number of milliseconds to wait for new events, or -1 to return immediately.
   * @param talkers     The timestamp of the last active talkers event. (optional if since = -1).
   *
   * @see Observable
   * @see UAPIEvent
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#get-meeting-events">Get Meeting Events</a>
   * @since 5.19
   */
  fun getMeetingEvents(eventUrl: String?): Observable<UAPIMeetingEvent> {
    var url = eventUrl ?: lastMeetingEvent?._links?.self?.href
    if (url == null) {
        url = UAPIEndPoints.MEETINGEVENTS + "?${UAPIConstants.KEY_SINCE}=-1&${UAPIConstants.KEY_TIMEOUT}=50000"
    }
    return getUAPIService().getMeetingEvents(url)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .flatMap {
          lastMeetingEvent = it
          logger.meetingModel.uniqueMeetingId = it.meetingId
          Observable.just(it)
        }
        .retryWhen(RetryAfterTimeoutWithDelay(5, retryTimeout))
  }

  /**
   * This is to get events for the meeting for which the user agent has authenticated.
   * The user agent must have joined a meeting and be an active participant in it.
   *
   * @param userId      The id for the participant being demoted or promoted.
   * @param promote     True to Promote. False to demote
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#promote-participant">Promote Participant</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#demote-participant">Demote Participant</a>
   * @since 5.19
   */
  fun updateUserRole(userId: String, promote: Boolean): Observable<UAPIResponse> {
    val requestBody = if(promote) {
      UpdateParticipantRequest("PRESENTER", null)
    } else{
      UpdateParticipantRequest("GUEST", null)
    }
    return getUAPIService().updateParticipantRequest(requestBody, userId)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to get events for the meeting for which the user agent has authenticated.
   * The user agent must have joined a meeting and be a waiting participant.
   *
   * @param userId      The id for the participant being demoted or promoted.
   * @param admit     True to admit. False to deny
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#deny-participant">Deny Participant</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#admit-participant">Admit Participant</a>
   * @since 5.19
   */
  fun updateUserAdmitDeny(userId: String, role: String?, admit: Boolean): Observable<UAPIResponse> {
    val requestBody = UpdateParticipantRequest(role, admit)
    return getUAPIService().updateParticipantRequest(requestBody, userId)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to get events for the meeting for which the user agent has authenticated.
   * The user agent must have joined a meeting and be a waiting participant.
   *
   * @param audioParticipantId  The id for the audio participant being admitted or denied.
   * @param admit  True to admit. False to deny
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#admit-audio-participant>Admit Participant</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#_audio_participant_denied">Deny Participant</a>
   * @since 5.19
   */
  fun updateAudioUserAdmitDeny(audioParticipantId: String, admit: Boolean): Observable<UAPIResponse> {
    val requestBody = UpdateAudioParticipantRequest(admit)
    return getUAPIService().updateAudioParticipantRequest(requestBody, audioParticipantId)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to dismiss the specified participant from the meeting for the room to which the user agent has authenticated.
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role. Any Participant cannot self-dismiss.
   * Hence the requestorId in the Auth Token must be different from the one sent as path parameter{partId}.
   *
   * @param userId      The id for the participant being demoted or promoted.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#dismiss-meeting-participant">Dismiss Participant</a>
   * @since 5.19
   */
  fun dismissUser(userId: String): Observable<UAPIResponse> {
    return getUAPIService().dismissParticipant(userId)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that the web and audio meeting for which the user agent has authenticated be locked/unlocked
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role.
   *
   * @param lock      True to lock and False to unlock meeting
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#lock-meeting">Lock Meeting</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#unlock-meeting">Unlock Meeting</a>
   * @since 5.19
   */
  fun lockUnlockMeeting(lock: Boolean): Observable<UAPIResponse> {
    val body = MeetingLockRequest(lock)
    return getUAPIService().updateMeetingRoomLock(body)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that the web and audio meeting for which the user agent has authenticated to have friction free turned on/off
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role.
   *
   * @param wait  True to turn on  and False to turn off friction free
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#friction-free">Friction Free</a>
   * @since 5.19
   */
  fun toggleFrictionFree(enabled: Boolean): Observable<UAPIResponse> {
    val body = FrictionFreeRequest(enabled)
    return getUAPIService().updateMeetingFrictionFree(body)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that the web and audio meeting for which the user agent has authenticated to have waiting room turned on/off
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role.
   *
   * @param wait  True to turn on waiting  and False to turn off waiting
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#waiting-room">Waiting Room</a>
   * @since 5.19
   */
  fun offOnWaitingRoom(wait: Boolean): Observable<UAPIResponse> {
    val body = WaitingRoomRequest(wait)
    return getUAPIService().updateMeetingRoomWaiting(body)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that all audio participants in the meeting for which the user agent has authenticated be muted/unmuted
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST role.
   *
   * @param lock      True to mute and False to unmute meeting
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#mute-meeting">Mute Meeting</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#unmute-meeting">Unmute Meeting</a>
   * @since 5.19
   */
  fun muteUnmuteMeeting(lock: Boolean): Observable<UAPIResponse> {
    val body = MeetingMuteRequest(lock)
    return getUAPIService().updateMeetingRoomMute(body)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that all audio participants in the meeting for which the user agent has authenticated be muted/unmuted.
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role.
   *
   * @param audioId   The id of the audio participant.
   * @param mute      True to mute and False to unmute meeting
   *
   * @see Observable
   * @see UAPIResponse
   * @see MuteParticipantRequest
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#mute-meeting">Mute Meeting</a>
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#unmute-meeting">Unmute Meeting</a>
   * @since 5.19
   */
  fun muteUnmuteUser(audioId: String, mute: Boolean): Observable<UAPIResponse>  {
    val requestBody = if(mute) {
      MuteParticipantRequest(true)
    } else {
      MuteParticipantRequest(false)
    }
    return getUAPIService().muteParticipant(requestBody, audioId)
        .debounce(deBounceTimeout, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request that the web meeting, the audio meeting to which the user agent has authenticated be ended.
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role.
   *
   * @param audio      Set to true, if audio needs to be ended. Web Meeting will always ends.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#end-meeting">End Meeting</a>
   * @since 5.19
   */
  fun endMeeting(audio: Boolean): Observable<UAPIResponse> {
    return getUAPIService().endMeeting(audio)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request for dial out to a phone number in order to connect the authorized user to the audio portion of the meeting.
   *
   * @param phoneNumber       The phone number to dial out.
   * @param countryCode       The country code of the number to be dialed out. Phone number history will only be updated if specified. (Optional).
   * @param phoneExtension    The phone extension to dial out (Optional). Size must be between 0 and 10 inclusive.
   * @param extensionDelay    The delay before the extension is dialed (Optional).
   * @param firstName         The first name of the participant requesting dial out. (Optional).
   * @param lastName          The last name of the participant requesting dial out. (Optional).
   * @param companyName       The company of the participant requesting dial out. (Optional).
   * @param locale            The locale the voice prompts should be played in. (Optional). Size must be between 2 and 35 inclusive.
   *
   * @see Observable
   * @see DialOutResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#dial-out">Dial Out</a>
   * @since 5.19
   */
  fun dialOut(countryCode: String?, phoneNumber: String, phoneExtension: String?, extensionDelay: Int?,
              firstName: String?, lastName: String?, companyName: String?, locale: String?): Observable<DialOutResponse> {
    val body = DialOutRequest(phoneNumber, countryCode, phoneExtension, extensionDelay, firstName,
        lastName, companyName, locale)
    return getUAPIService().dialOut(body)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request cancellation of a pending dial out for the authorized user.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#cancel-dial-out">Cancel Dial Out</a>
   * @since 5.19
   */
  fun cancelDialOut(): Observable<UAPIResponse> {
    return getUAPIService().cancelDialOut()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to dismiss the specified audio participant from the meeting for the room to which the user agent has authenticated.
   *
   * Role Requirements:
   *
   * The user may dismiss their own audio, or the user agent must be authenticated with the HOST role.
   *
   * @param audioId   The id of the audio participant.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#dismiss-audio-participant">Dismiss Audio Participant</a>
   * @since 5.19
   */
  fun dismissAudioUser(audioId: String): Observable<UAPIResponse> {
    return getUAPIService().dismissAudioParticipant(audioId)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to add a chat message to the given conversation.
   *
   * Chats sent and received between meeting participants are done within the context of a conversation.
   * A conversation manages a set of recipients or roles and only sends chat messages to those recipients in the conversation.
   *
   * @param chatMessage  The chat message. Size must be between 1 and 1000 inclusive.
   *
   * @see Observable
   * @see AddChatRequest
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#add-chat">Add Chat</a>
   * @since 5.19
   */
  fun addChat(chatMessage: String , chatType : String): Observable<UAPIResponse>  {
    val requestBody = AddChatRequest(chatMessage)
    return getUAPIService().addChat(requestBody, chatType)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to clear all chat messages in the given conversation.
   *
   * Role Requirements:
   *
   * The user agent must be authenticated with either the HOST or PRESENTER role to clear chat messages in the conversation.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#add-chat">Clear Chat</a>
   * @since 5.19
   */
  fun clearChats(): Observable<UAPIResponse>  {
    return getUAPIService().clearChats("default")
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to stop screenshare
   *
   * @param contentId       The ID of the content to be updated.
   * @param partId          The participnat id currently sharing screen
   * @param name            The name of presenter currently sharing
   * @param phoneNumber     The phonenumber of presenter currently sharing
   * @param email           The email of presenter currently sharing
   *
   * @see Observable
   * @see ContentResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#update-content">Update Content</a>
   * @since 5.19
   */
  fun stopScreenShare(contentId: String, partId: String, name: String, phoneNumber: String?,
                      email: String): Observable<ContentResponse> {
    val screenPresenter = DynamicScreenUserInfo(partId, name, phoneNumber, email)
    val dynamicMetadata = DynamicScreenRequest(AppConstants.SCREEN_SHARE, AppConstants.ACTION_STOP, screenPresenter)
    val requestBody = ContentUpdateRequest(null, null, Gson().toJson(dynamicMetadata))
    return getUAPIService().updateContent(requestBody, contentId)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to start recording a meeting.
   *
   * @param recordingName       The name of the recording. Default value is the Room Name - Date Time. (Optional). Size must be between 0 and 32 inclusive.
   * @param locale              The locale the meeting should be recorded in.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#start-recording">Start Recording</a>
   * @since 5.19
   */
  fun startRecording(recordingName: String?, locale: String?): Observable<UAPIResponse> {
    val requestBody = StartRecordingRequest(recordingName, locale)
    return getUAPIService().startRecording(requestBody)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to request to stop recording a meeting.
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#start-recording">Start Recording</a>
   * @since 5.19
   */
  fun stopRecording(): Observable<UAPIResponse> {
    return getUAPIService().stopRecording()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

  /**
   * This is to keep meeting alive
   *
   * @see Observable
   * @see UAPIResponse
   *
   * @see <a href="https://uapi.qac.globalmeet.net/docs/v1#keep-meeting-alive">Keep ALive</wa>
   * @since 5.19
   */
  fun keepMeetingAlive(): Observable<UAPIResponse> {
    return getUAPIService().keepMeetingAlive(KeepMeetingAliveRequest())
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }

    /**
     * Here Host/Co-host can enable or disable private chat
     *
     * @param checked  True to enable private chat. False to disable
     *
     * @see Observable
     * @see UAPIResponse
     *
     * @see <a href="https://uapi.qab.globalmeet.net/docs/v1#private-chat>
     * @since 5.19
     */
    fun enablePrivateChat(checked: Boolean): Observable<UAPIResponse> {
        val requestBody = EnablePrivateChatRequest(checked)
        return getUAPIService().updatePrivateChat(requestBody)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
    }

  /**
   * This is to request to create a conversation for participant ids.
   *
   *
   * @param participantIDs  The participant id of sender and the receiver. This is an ArrayList of size 2
   *
   * @see Observable
   * @see AddConversationResponse
   * @see UAPIResponse
   */
  fun addConversation(participantIDs : Array<String>): Observable<AddConversationResponse>  {
    val requestBody = AddConversationRequest(participantIDs)
    return getUAPIService().addConversation(requestBody)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
  }
}