package com.pgi.network.models

/**
 * Created by Sudheer Chilumula on 9/19/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class Href(val href: String)

data class Links(
    val getEvents: Href?,
    val authorize: Href?,
    val joinMeeting: Href?,
    val updateMeeting: Href?,
    val meetingRoomInfo: Href?,
    val defaultPublicChat: Href?,
    val recording: Href?,
    val profileImage: Href?,
    val companyLogo: Href?,
    val productLogo: Href?,
    val primaryLogo: Href?,
    val iconLogo: Href?,
    val friendlyUrl: Href?,
    val privacyPolicyUrl: Href?,
    val privacyTermsUrl: Href?,
    val self: Href?
)

data class AudioError(
    val code: Int,
    val pgsErrorCode: Int,
    val errorText: String
)

data class UAPIMeetingEvent(
    val responseType: String,
    val timestamp: String,
    val meetingId: String,
    val since: Int,
    val lastSeqNum: Int,
    val reset: Boolean,
    val talkers: String,
    val events: List<UAPIEvent>,
    val _links: Links
)

data class UAPIEvent(
    val eventType: String,
    val timestamp: String,
    val participantId: String?,
    val webParticipantId: String?,
    val audioParticipantId: String?,
    val talkingParticipants: List<String>?,
    val partId: String?,
    val confId: String?,
    val subConfId: String?,
    val firstName: String?,
    val lastName: String?,
    val company: String?,
    val email: String?,
    val phone: String?,
    val partType: String?,
    val ani: String?,
    val dnis: String?,
    val codec: String?,
    val hold: Boolean?,
    val mute: Boolean?,
    val listenOnly: Boolean?,
    val listenLevel: Int?,
    val voiceLevel: Int?,
    val reason: String?,
    val conversationId: String?,
    val chatMsg: String?,
    val minutesToAcknowledge: Int?,
    val familyName: String?,
    val givenName: String?,
    val name: String?,
    val phoneExt: String?,
    val roomRole: String?,
    val delegateRole: Boolean?,
    val error: AudioError?,
    val roomId: String?,
    val meetingCapacity: Int?,
    val audioConferenceId: String?,
    val dialoutId: String?,
    val phoneNumber: String?,
    val countryCode: String?,
    val phoneExtension: String?,
    val extensionDelay: Int?,
    val companyName: String?,
    val contentLimit: Int?,
    val contentId: String?,
    val type: String?,
    val version: String?,
    val visible: Boolean?,
    val stageId: String?,
    val staticMetadata: String?,
    val dynamicMetadata: String?,
    val allowGuestUpdate: Boolean?,
    val failStatus: String?,
    val recordingId: String?,
    val recordingName: String?,
    val status: String?,
    val duration: Int?,
    val errorMessage: String?,
    val _links: Links?,
    val value: Boolean?,
    val waiting: Boolean?,
    val existingConversationId: String?,
    val participantIds: Array<String>
)

data class UAPIError(
    val errorCode: String,
    val errorMessage: String
)

data class UAPIResponse(
    val responseType: String,
    val timestamp: String,
    val errors: List<UAPIError>
)

data class ContentResponse(
    val responseType: String,
    val timestamp: String,
    val contentId: String,
    val _links: Links,
    val errors: List<UAPIError>
)

data class MatterTracking(
    val clientMatterMaxLength: Int,
    val clientMatterMinLength: Int,
    val matterNumberMaxLength: Int,
    val matterNumberMinLength: Int
)

data class AuthorizeResponse(
    val responseType: String,
    val timestamp: String,
    val email: String,
    val loginType: String,
    val loginName: String,
    val roomRole: String,
    val authToken: String?,
    val userId: String,
    val matterTracking: MatterTracking,
    val _links: Links,
    val errors: List<UAPIError>?
)

data class DialOutNumber(
    val countryCode: String,
    val phone: String,
    val phoneExt: String,
    val lastUsed: Boolean
)

data class JoinMeetingResponse(
    val responseType: String,
    val timestamp: String,
    val joinStatus: String,
    val participantId: String,
    val sipIdentifier: String,
    val waitStartTime: String,
    val dialoutNumbers: List<DialOutNumber>,
    val companyId: String,
    val _links: Links,
    val errors: List<UAPIError>?
)

data class AccessNumber(
    var phoneNumber: String,
    val phoneType: String,
    val description: String
)

data class MeetingRoomAudio(
    val audioConferenceId: String,
    val hostPasscode: String,
    val guestPasscode: String,
    val audioSecurityCode: String,
    val primaryAccessNumber: String?,
    val accessPhoneNumbers: List<AccessNumber>,
    val sipURI: String?,
    val premiumAudio: Boolean,
    val dialOutBlocked: Boolean?
)

data class VRCInfo(
    val vrcServer: String,
    val vrcUri: String
)

data class RecordingInfo(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class FrictionFreeInfo(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class ChatInfo(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class PrivateChat(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class FilePresentationInfo(
    val allowGuestLocked: Boolean,
    val enableLocked: Boolean,
    val allowGuest: Boolean,
    val enabled: Boolean
)

data class FileUploadInfo(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class RemoteDesktopInfo(
    val enabledLocked: Boolean,
    val enabled: Boolean
)

data class ScreenShareInfo(
    val allowGuestLocked: Boolean,
    val enabledLocked: Boolean,
    val allowGuest: Boolean,
    val enabled: Boolean
)

data class WhiteboardInfo(
        val allowGuestLocked: Boolean,
        val enabledLocked: Boolean,
        val allowGuest: Boolean,
        val enabled: Boolean
)

data class WaitingRoomInfo(
    val enabled: Boolean,
    val enabledLocked: Boolean
)

data class WebcamInfo(
    val allowGuestLocked: Boolean,
    val enabledLocked: Boolean,
    val allowGuest: Boolean,
    val enabled: Boolean
)

data class MeetingRoomInfoResponse(
    val responseType: String,
    val timestamp: String,
    val title: String,
    val meetingOwnerClientId: String,
    val meetingOwnerGivenName: String,
    val meetingOwnerFamilyName: String,
    val meetingOwnerName: String,
    val companyId: String,
    val productName: String,
    val meetingHost: String,
    val staticHost: String,
    val supportPhone: String,
    val supportEmail: String,
    val securityCode: String,
    val audio: MeetingRoomAudio?,
    val recordingEnabled: Boolean,
    val deleted: Boolean,
    val vrc: VRCInfo,
    val recording: RecordingInfo,
    val frictionFree: FrictionFreeInfo,
    val chat: ChatInfo,
    val privateChat: PrivateChat,
    val showOldLoginFlow: Boolean,
    val filePresentation: FilePresentationInfo,
    val fileUpload: FileUploadInfo,
    val freemiumEnabled: Boolean,
    val remoteDesktop: RemoteDesktopInfo,
    val screenShare: ScreenShareInfo,
    val waitingRoom: WaitingRoomInfo,
    val webcam: WebcamInfo,
    val whiteboard: WhiteboardInfo,
    val _links: Links,
    val errors: List<UAPIError>?
)

data class DialOutResponse(
    val responseType: String,
    val timestamp: String,
    val dialoutId: String,
    val errors: List<UAPIError>?
)

data class AddConversationResponse(
        val responseType: String,
        val timestamp: String,
        val conversationId: String,
        val errors: List<UAPIError>?
)
