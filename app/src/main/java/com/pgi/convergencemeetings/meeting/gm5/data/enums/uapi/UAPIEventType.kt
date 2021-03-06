package com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi


/**
 * Created by Sudheer Chilumula on 9/20/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
enum class UAPIEventType(val type: String) {
    ACTIVE_TALKERS("ACTIVE_TALKERS"),
    AUDIO_MEETING_ENDED("AUDIO_MEETING_ENDED"),
    AUDIO_MEETING_LOCK_SYNC_FAILED("AUDIO_MEETING_LOCK_SYNC_FAILED"),
    AUDIO_MEETING_STARTED("AUDIO_MEETING_STARTED"),
    AUDIO_PARTICIPANT_JOINED("AUDIO_PARTICIPANT_JOINED"),
    AUDIO_PARTICIPANT_LEFT("AUDIO_PARTICIPANT_LEFT"),
    CANCEL_PARTICIPANT_DIALOUT_FAILED("CANCEL_PARTICIPANT_DIALOUT_FAILED"),
    PARTICIPANT_DIAL_OUT_TIMED_OUT("PARTICIPANT_DIAL_OUT_TIMED_OUT"),
    DISMISS_AUDIO_PARTICIPANT_FAILED("DISMISS_AUDIO_PARTICIPANT_FAILED"),
    MUTE_AUDIO_PARTICIPANT("MUTE_AUDIO_PARTICIPANT"),
    MUTE_AUDIO_PARTICIPANT_FAILED("MUTE_AUDIO_PARTICIPANT_FAILED"),
    PARTICIPANT_DIALOUT_CANCELED("PARTICIPANT_DIALOUT_CANCELED"),
    PARTICIPANT_DIAL_OUT_FAILED("PARTICIPANT_DIAL_OUT_FAILED"),
    PARTICIPANT_DIAL_OUT_INITIATED("PARTICIPANT_DIAL_OUT_INITIATED"),
    PARTICIPANT_DIAL_OUT_SUCCEEDED("PARTICIPANT_DIAL_OUT_SUCCEEDED"),
    SIP_DIAL_OUT_SUCCEEDED("SIP_DIAL_OUT_SUCCEEDED"),
    START_AUDIO_MEETING_FAILED("START_AUDIO_MEETING_FAILED"),
    UNMUTE_AUDIO_PARTICIPANT("UNMUTE_AUDIO_PARTICIPANT"),
    UNMUTE_AUDIO_PARTICIPANT_FAILED("UNMUTE_AUDIO_PARTICIPANT_FAILED"),
    CHAT_ADDED("CHAT_ADDED"),
    PRIVATE_CHAT_ADDED("PRIVATE_CHAT_ADDED"),
    CHAT_CLEARED("CHAT_CLEARED"),
    MEETING_ENDED("MEETING_ENDED"),
    MEETING_INACTIVITY_WARNING("MEETING_INACTIVITY_WARNING"),
    MEETING_JOINED("MEETING_JOINED"),
    MEETING_KEPT_ALIVE("MEETING_KEPT_ALIVE"),
    MEETING_LOCKED("MEETING_LOCKED"),
    MEETING_MUTED("MEETING_MUTED"),
    MEETING_STARTED("MEETING_STARTED"),
    MEETING_UNLOCKED("MEETING_UNLOCKED"),
    MEETING_UNMUTED("MEETING_UNMUTED"),
    MUTE_MEETING_FAILED("MUTE_MEETING_FAILED"),
    PARTICIPANT_DEMOTED("PARTICIPANT_DEMOTED"),
    PARTICIPANT_DISMISSED("PARTICIPANT_DISMISSED"),
    PARTICIPANT_ADMITTED("PARTICIPANT_ADMITTED"),
    AUDIO_PARTICIPANT_ADMITTED("AUDIO_PARTICIPANT_ADMITTED"),
    PARTICIPANT_DISCONNECTED("PARTICIPANT_DISCONNECTED"),
    PARTICIPANT_LEFT("PARTICIPANT_LEFT"),
    PARTICIPANT_PROMOTED("PARTICIPANT_PROMOTED"),
    PARTICIPANT_RECONNECTED("PARTICIPANT_RECONNECTED"),
    UNMUTE_MEETING_FAILED("UNMUTE_MEETING_FAILED"),
    CONTENT_CREATED("CONTENT_CREATED"),
    MEETING_OPTION_WS_UPDATED("MEETING_OPTION_WS_UPDATED"),
    CONTENT_CREATE_FAILED("CONTENT_CREATE_FAILED"),
    CONTENT_UPDATED("CONTENT_UPDATED"),
    CONTENT_UPDATE_FAILED("CONTENT_UPDATE_FAILED"),
    CONTENT_DELETED("CONTENT_DELETED"),
    CONTENT_DELETE_FAILED("CONTENT_DELETE_FAILED"),
    START_RECORDING_INITIATED("START_RECORDING_INITIATED"),
    START_RECORDING_FAILED("START_RECORDING_FAILED"),
    RECORDING_STARTED("RECORDING_STARTED"),
    STOP_RECORDING_INITIATED("STOP_RECORDING_INITIATED"),
    STOP_RECORDING_FAILED("STOP_RECORDING_FAILED"),
    RECORDING_STOPPED("RECORDING_STOPPED"),
    RECORDING_INFORMATION_UPDATED("RECORDING_INFORMATION_UPDATED"),
    RECORDING_FAILED("RECORDING_FAILED"),
    AUDIO_PART_RENAMED("AUDIO_PART_RENAMED"),
    CONVERSATION_ADDED("CONVERSATION_ADDED"),
    ADD_CONVERSATION_FAILED("ADD_CONVERSATION_FAILED"),
    ADD_CHAT_FAILED("ADD_CHAT_FAILED");

    companion object {
        @JvmStatic
        fun toStringArray(): Array<String> {
            val eventTypes = ArrayList<String>()
            for (event in UAPIEventType.values()) {
                eventTypes.add(event.type)
            }
            return eventTypes.toTypedArray();
        }
    }
}