package com.pgi.convergence.enums.uapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Meeting Events for GM5 Meeting Room
 * <p>
 * Created by surbhidhingra on 05-12-17.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public enum UAPIMeetingEventType {

    @JsonProperty("AUDIO_MEETING_STARTED")
    AUDIO_MEETING_STARTED("AUDIO_MEETING_STARTED"),

    @JsonProperty("MEETING_STARTED")
    MEETING_STARTED("MEETING_STARTED"),

    @JsonProperty("CONTENT_DELETE_FAILED")
    CONTENT_DELETE_FAILED("CONTENT_DELETE_FAILED"),

    @JsonProperty("AUDIO_PARTICIPANT_LEFT")
    AUDIO_PARTICIPANT_LEFT("AUDIO_PARTICIPANT_LEFT"),

    @JsonProperty("GUEST_WEBCAM_DISABLED")
    GUEST_WEBCAM_DISABLED("GUEST_WEBCAM_DISABLED"),

    @JsonProperty("MEETING_JOINED")
    MEETING_JOINED("MEETING_JOINED"),

    @JsonProperty("QUESTION_ANSWERED")
    QUESTION_ANSWERED("QUESTION_ANSWERED"),

    @JsonProperty("START_AUDIO_MEETING_FAILED")
    START_AUDIO_MEETING_FAILED("START_AUDIO_MEETING_FAILED"),

    @JsonProperty("PRES_CURSOR_TRACKING_DISABLED")
    PRES_CURSOR_TRACKING_DISABLED("PRES_CURSOR_TRACKING_DISABLED"),

    @JsonProperty("PRES_SLIDE_UPDATED")
    PRES_SLIDE_UPDATED("PRES_SLIDE_UPDATED"),

    @JsonProperty("PARTICIPANT_DIAL_OUT_SUCCEEDED")
    PARTICIPANT_DIAL_OUT_SUCCEEDED("PARTICIPANT_DIAL_OUT_SUCCEEDED"),

    @JsonProperty("PARTICIPANT_DIAL_OUT_TIMED_OUT")
    PARTICIPANT_DIAL_OUT_TIMED_OUT("PARTICIPANT_DIAL_OUT_TIMED_OUT"),

    @JsonProperty("WAITING_ROOM_ENTERED")
    WAITING_ROOM_ENTERED("WAITING_ROOM_ENTERED"),

    @JsonProperty("SCREENSHARE_STOPPED")
    SCREENSHARE_STOPPED("SCREENSHARE_STOPPED"),

    @JsonProperty("MUTE_AUDIO_PARTICIPANT_FAILED")
    MUTE_AUDIO_PARTICIPANT_FAILED("MUTE_AUDIO_PARTICIPANT_FAILED"),

    @JsonProperty("PARTICIPANT_DISMISSED")
    PARTICIPANT_DISMISSED("PARTICIPANT_DISMISSED"),

    @JsonProperty("CHAT_ADDED")
    CHAT_ADDED("CHAT_ADDED"),

    @JsonProperty("AUDIO_PARTICIPANT_JOINED")
    AUDIO_PARTICIPANT_JOINED("AUDIO_PARTICIPANT_JOINED"),

    @JsonProperty("SIP_DIAL_OUT_SUCCEEDED")
    SIP_DIAL_OUT_SUCCEEDED("SIP_DIAL_OUT_SUCCEEDED"),

    @JsonProperty("PARTICIPANT_PROMOTED")
    PARTICIPANT_PROMOTED("PARTICIPANT_PROMOTED"),

    @JsonProperty("MUTE_AUDIO_PARTICIPANT")
    MUTE_AUDIO_PARTICIPANT("MUTE_AUDIO_PARTICIPANT"),

    @JsonProperty("UNMUTE_AUDIO_PARTICIPANT")
    UNMUTE_AUDIO_PARTICIPANT("UNMUTE_AUDIO_PARTICIPANT"),

    @JsonProperty("PARTICIPANT_DEMOTED")
    PARTICIPANT_DEMOTED("PARTICIPANT_DEMOTED"),

    @JsonProperty("AUDIO_MEETING_ENDED")
    AUDIO_MEETING_ENDED("AUDIO_MEETING_ENDED"),

    @JsonProperty("SCREENSHARE_APP_SHOWN")
    SCREENSHARE_APP_SHOWN("SCREENSHARE_APP_SHOWN"),

    @JsonProperty("SCREENSHARE_APP_STARTED")
    SCREENSHARE_APP_STARTED("SCREENSHARE_APP_STARTED"),

    @JsonProperty("MEETING_LOCKED")
    MEETING_LOCKED("MEETING_LOCKED"),

    @JsonProperty("SCREENSHARE_STARTED")
    SCREENSHARE_STARTED("SCREENSHARE_STARTED"),

    @JsonProperty("CHAT_CLEARED")
    CHAT_CLEARED("CHAT_CLEARED"),

    @JsonProperty("ACTIVE_TALKERS")
    ACTIVE_TALKERS("ACTIVE_TALKERS"),

    @JsonProperty("PARTICIPANT_DIAL_OUT_INITIATED")
    PARTICIPANT_DIAL_OUT_INITIATED("PARTICIPANT_DIAL_OUT_INITIATED"),

    @JsonProperty("MEETING_ENDED")
    MEETING_ENDED("MEETING_ENDED"),

    @JsonProperty("PARTICIPANT_DISCONNECTED")
    PARTICIPANT_DISCONNECTED("PARTICIPANT_DISCONNECTED"),

    @JsonProperty("MEETING_UNLOCKED")
    MEETING_UNLOCKED("MEETING_UNLOCKED"),

    @JsonProperty("PARTICIPANT_LEFT")
    PARTICIPANT_LEFT("PARTICIPANT_LEFT"),

    @JsonProperty("CONTENT_CREATED")
    CONTENT_CREATED("CONTENT_CREATED"),

    @JsonProperty("CONTENT_UPDATED")
    CONTENT_UPDATED("CONTENT_UPDATED"),

    @JsonProperty("PARTICIPANT_DIAL_OUT_FAILED")
    PARTICIPANT_DIAL_OUT_FAILED("PARTICIPANT_DIAL_OUT_FAILED"),

    @JsonProperty("PARTICIPANT_DIALOUT_CANCELED")
    PARTICIPANT_DIALOUT_CANCELED("PARTICIPANT_DIALOUT_CANCELED"),

    @JsonProperty("CONTENT_CREATE_FAILED")
    CONTENT_CREATE_FAILED("CONTENT_CREATE_FAILED"),

    @JsonProperty("PARTICIPANT_RECONNECTED")
    PARTICIPANT_RECONNECTED("PARTICIPANT_RECONNECTED"),

    @JsonProperty("DISMISS_AUDIO_PARTICIPANT_FAILED")
    DISMISS_AUDIO_PARTICIPANT_FAILED("DISMISS_AUDIO_PARTICIPANT_FAILED"),

    @JsonProperty("UNMUTE_AUDIO_PARTICIPANT_FAILED")
    UNMUTE_AUDIO_PARTICIPANT_FAILED("UNMUTE_AUDIO_PARTICIPANT_FAILED"),

    @JsonProperty("START_RECORDING_INITIATED")
    START_RECORDING_INITIATED("START_RECORDING_INITIATED"),

    @JsonProperty("START_RECORDING_FAILED")
    START_RECORDING_FAILED("START_RECORDING_FAILED"),

    @JsonProperty("RECORDING_STARTED")
    RECORDING_STARTED("RECORDING_STARTED"),

    @JsonProperty("STOP_RECORDING_INITIATED")
    STOP_RECORDING_INITIATED("STOP_RECORDING_INITIATED"),

    @JsonProperty("STOP_RECORDING_FAILED")
    STOP_RECORDING_FAILED("STOP_RECORDING_FAILED"),

    @JsonProperty("RECORDING_STOPPED")
    RECORDING_STOPPED("RECORDING_STOPPED"),

    @JsonProperty("RECORDING_INFORMATION_UPDATED")
    RECORDING_INFORMATION_UPDATED("RECORDING_INFORMATION_UPDATED"),

    @JsonProperty("RECORDING_FAILED")
    RECORDING_FAILED("RECORDING_FAILED"),

    @JsonProperty("MEETING_MUTED")
    MEETING_MUTED("MEETING_MUTED"),

    @JsonProperty("MEETING_UNMUTED")
    MEETING_UNMUTED("MEETING_UNMUTED");

    private final String value;

    private UAPIMeetingEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

