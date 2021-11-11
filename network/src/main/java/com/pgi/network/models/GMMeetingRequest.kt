package com.pgi.network.models


/**
 * Created by Sudheer Chilumula on 9/26/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class UpdateParticipantRequest(
    val role: String?,
    val admit: Boolean?
)

data class UpdateAudioParticipantRequest(
        val admit: Boolean?
)

data class MuteParticipantRequest(
    val mute: Boolean
)

data class ContentUpdateRequest(
    val visible: Boolean?,
    val stageId: String?,
    val dynamicMetadata: String
)

data class DynamicScreenRequest(
    val type: String,
    val action: String,
    val screenPresenter: DynamicScreenUserInfo
)

data class DynamicScreenUserInfo(
    val partId: String,
    val name: String,
    val phoneNumber: String?,
    val email: String
)

data class AuthorizeRequest(
    val roomId: String
)

data class JoinMeetingRequest(
    val familyName: String?,
    val givenName: String?,
    val name: String?,
    val email: String?,
    val matterNumber: String?
)

data class MeetingLockRequest(
  val lock: Boolean
)

data class WaitingRoomRequest(
   val value: Boolean
)

data class FrictionFreeRequest(
    val value: Boolean
)

class KeepMeetingAliveRequest()

data class MeetingMuteRequest(
    val mute: Boolean
)

data class DialOutRequest(
    val phoneNumber: String,
    val countryCode: String?,
    val phoneExtension: String?,
    val extensionDelay: Int?,
    val firstName: String?,
    val lastName: String?,
    val companyName: String?,
    val locale: String?
)

data class AddChatRequest(
    val chatMessage: String
)

data class StartRecordingRequest(
    val recordingName: String?,
    val locale: String?
)

data class ConnectVideoRoomRequest(
    val endpoint: String,
    val protocol: String,
    val displayName: String
)

data class EnablePrivateChatRequest(
    val value: Boolean?
)

data class AddConversationRequest(
        val participantIds: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddConversationRequest

        if (!participantIds.contentEquals(other.participantIds)) return false

        return true
    }

    override fun hashCode(): Int {
        return participantIds.contentHashCode()
    }
}