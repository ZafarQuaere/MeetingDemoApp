package com.pgi.network

import com.pgi.network.models.*
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by Sudheer Chilumula on 9/19/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
interface GMWebUAPIServiceAPI {
    /**
     * Authorize call.
     *
     * @param requestBody AuthorizeRequest
     * @return Observable<AuthorizeResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.AUTHSESSION)
    fun authorize(@Body requestBody: AuthorizeRequest): Observable<AuthorizeResponse>

    /**
     * Gets room info.
     *
     * @return the room info
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @GET(UAPIEndPoints.ROOMINFO)
    fun getMeetingRoomInfo(): Observable<MeetingRoomInfoResponse>

    /**
     * Join meeting call.
     *
     * @param requestBody JoinMeetingRequest
     * @return Observable<JoinMeetingResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.JOINMEETING)
    fun joinMeeting(@Body requestBody: JoinMeetingRequest): Observable<JoinMeetingResponse>

    /**
     * Leave meeting call.
     *
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:15000", "READ_TIMEOUT:15000", "WRITE_TIMEOUT:15000")
    @DELETE(UAPIEndPoints.LEAVEMEETING)
    fun leaveMeeting(): Observable<UAPIResponse>

    /**
     * Gets meeting events.
     *
     * @param since            the since
     * @param timeout          the timeout
     * @param talkersTimeStamp the talkers time stamp
     * @return Observable<MeetingEvent>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @GET
    fun getMeetingEvents(@Url eventUrl: String): Observable<UAPIMeetingEvent>

    /**
     * Update participant role call.
     *
     * @param requestBody   UpdateParticipantRequest
     * @param participantID the participant id
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATEPART)
    fun updateParticipantRequest(@Body body: UpdateParticipantRequest, @Path("participantID") participantID: String): Observable<UAPIResponse>

    /**
     * Update audio participant role call.
     *
     * @param requestBody   UpdateParticipantRequest
     * @param audioParticipantID the audio participant id
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATEAUDIOPARTICIPANT)
    fun updateAudioParticipantRequest(@Body body: UpdateAudioParticipantRequest, @Path("audioParticipantID") audioParticipantId: String): Observable<UAPIResponse>


    /**
     * Dismiss participant call.
     *
     * @param participantID the participant id
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @DELETE(UAPIEndPoints.UPDATEPART)
    fun dismissParticipant(@Path("participantID") participantID: String): Observable<UAPIResponse>

    /**
     * Lock Meeting.
     *
     * @param requestBody   MeetingLockRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATEMEETING)
    fun updateMeetingRoomLock(@Body body: MeetingLockRequest): Observable<UAPIResponse>

    /**
     * Waiting Room.
     *
     * @param requestBody   WaitingRoomRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATE_WAITING_ROOM)
    fun updateMeetingRoomWaiting(@Body body: WaitingRoomRequest): Observable<UAPIResponse>

    /**
     * Friction Free.
     *
     * @param requestBody   FrictionFreeRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATE_FRICTION_FREE)
    fun updateMeetingFrictionFree(@Body body: FrictionFreeRequest): Observable<UAPIResponse>

    /**
     * Mute Meeting.
     *
     * @param requestBody   MeetingMuteRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.UPDATEMEETING)
    fun updateMeetingRoomMute(@Body body: MeetingMuteRequest): Observable<UAPIResponse>

    /**
     * Keep Meeting Alive
     *
     * @param requestBody   shoudl be empty body
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:15000", "READ_TIMEOUT:15000", "WRITE_TIMEOUT:15000")
    @PUT(UAPIEndPoints.UPDATEMEETING)
    fun keepMeetingAlive(@Body body:  KeepMeetingAliveRequest): Observable<UAPIResponse>

    /**
     * End meeting call.
     *
     * @param endAudio    the end audio
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:15000", "READ_TIMEOUT:15000", "WRITE_TIMEOUT:15000")
    @DELETE(UAPIEndPoints.UPDATEMEETING)
    fun endMeeting(@Query("audio") endAudio: Boolean): Observable<UAPIResponse>

    /**
     * Dial out meeting call.
     *
     * @param requestBody DialOutRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.DIALOUT)
    fun dialOut(@Body requestBody: DialOutRequest): Observable<DialOutResponse>

    /**
     * Cancel Dial out meeting call.
     *
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @DELETE(UAPIEndPoints.DIALOUT)
    fun cancelDialOut(): Observable<UAPIResponse>

    /**
     * Dismiss audio participant call.
     *
     * @param audioParticipantID the audio participant id
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @DELETE(UAPIEndPoints.UPDATEAUDIOPARTICIPANT)
    fun dismissAudioParticipant(@Path("audioParticipantID") audioParticipantID: String): Observable<UAPIResponse>

    /**
     * Mute participant call.
     *
     * @param requestBody   MuteParticipantRequest
     * @param participantID the participant id
     * @return  Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:15000", "READ_TIMEOUT:15000", "WRITE_TIMEOUT:15000")
    @PUT(UAPIEndPoints.UPDATEAUDIOPARTICIPANT)
    fun muteParticipant(@Body requestBody: MuteParticipantRequest, @Path("audioParticipantID") audioParticipantID: String): Observable<UAPIResponse>

    /**
     * Add chat in meeting call.
     *
     * @param requestBody AddChatRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.CHAT)
    fun addChat(@Body requestBody: AddChatRequest, @Path("conversationId") conversationId: String): Observable<UAPIResponse>

    /**
     * Add chat in meeting call.
     *
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @DELETE(UAPIEndPoints.CHAT)
    fun clearChats(@Path("conversationId") conversationId: String): Observable<UAPIResponse>

    /**
     * Update content
     * @param requestBody   ContentUpdateRequest
     * @param contentID     the screen share content id
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @PUT(UAPIEndPoints.CONTENTUPDATE)
    fun updateContent(@Body requestBody: ContentUpdateRequest, @Path("contentID") contentID: String): Observable<ContentResponse>

    /**
     * Start Recording
     *
     * @param requestBody StartRecordingRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @POST(UAPIEndPoints.RECORDING)
    fun startRecording(@Body requestBody: StartRecordingRequest): Observable<UAPIResponse>

    /**
     * Stop Recording
     *
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @DELETE(UAPIEndPoints.RECORDING)
    fun stopRecording(): Observable<UAPIResponse>

    /**
     * Connect to Video room.
     *
     * @param requestBody ConnectVideoRoomRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.VIDEOROOM)
    fun connectVideoRoom(@Body requestBody: ConnectVideoRoomRequest): Observable<UAPIResponse>

    /**
     * create conversation for participant 1-1.
     *
     * @param requestBody AddChatRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(UAPIEndPoints.CREATE_CONVERSATION)
    fun addConversation(@Body requestBody: AddConversationRequest): Observable<AddConversationResponse>

    /**
     * Update private chat option.
     *
     * @param requestBody   EnablePrivateChatRequest
     * @return Observable<UAPIResponse>
     */
    @Headers(UAPIEndPoints.JSONHEADER, "CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000")
    @PUT(UAPIEndPoints.ENABLE_PRIVATE_CHAT)
    fun updatePrivateChat(@Body body: EnablePrivateChatRequest): Observable<UAPIResponse>

}