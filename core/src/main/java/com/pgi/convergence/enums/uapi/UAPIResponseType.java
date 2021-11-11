package com.pgi.convergence.enums.uapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UAPI Response types
 * <p>
 * Created by surbhidhingra on 01-12-17.
 */

public enum UAPIResponseType {
    @JsonProperty("JOIN_MEETING")
    JOIN_MEETING("JOIN_MEETING"),
    @JsonProperty("LEAVE_MEETING")
    LEAVE_MEETING("LEAVE_MEETING"),
    @JsonProperty("MEETING_EVENTS")
    MEETING_EVENTS("MEETING_EVENTS"),
    @JsonProperty("ROOM_SESSION")
    ROOM_SESSION("ROOM_SESSION"),
    @JsonProperty("END_MEETING")
    END_MEETING("END_MEETING"),
    @JsonProperty("DIAL_OUT")
    DIAL_OUT("DIAL_OUT"),
    @JsonProperty("CANCEL_DIAL_OUT")
    CANCEL_DIAL_OUT("CANCEL_DIAL_OUT"),
    @JsonProperty("MEETING_ROOM_INFO")
    MEETING_ROOM_INFO("MEETING_ROOM_INFO"),
    @JsonProperty("UPDATE_AUDIO_PARTICIPANT")
    UPDATE_AUDIO_PARTICIPANT("UPDATE_AUDIO_PARTICIPANT"),
    @JsonProperty("DISMISS_AUDIO_PARTICIPANT_RESPONSE")
    DISMISS_AUDIO_PARTICIPANT_RESPONSE("DISMISS_AUDIO_PARTICIPANT_RESPONSE"),
    @JsonProperty("UPDATE_PARTICIPANT")
    UPDATE_PARTICIPANT("UPDATE_PARTICIPANT"),
    @JsonProperty("DISMISS_PARTICIPANT")
    DISMISS_PARTICIPANT("DISMISS_PARTICIPANT"),
    @JsonProperty("DISMISS_AUDIO_PARTICIPANT")
    DISMISS_AUDIO_PARTICIPANT("DISMISS_AUDIO_PARTICIPANT"),
    @JsonProperty("ADD_CHAT")
    ADD_CHAT("ADD_CHAT"),
    @JsonProperty("UPDATE_CONTENT")
    UPDATE_CONTENT("UPDATE_CONTENT"),
    @JsonProperty("UPDATE_MEETING")
    UPDATE_MEETING("UPDATE_MEETING"),
    @JsonProperty("UPDATE_MEETING_OPTION")
    UPDATE_MEETING_OPTION("UPDATE_MEETING_OPTION"),
    @JsonProperty("ERROR")
    ERROR("ERROR"),
    @JsonProperty("START_RECORDING")
    START_RECORDING("START_RECORDING"),
    @JsonProperty("STOP_RECORDING")
    STOP_RECORDING("STOP_RECORDING"),
    @JsonProperty("ADD_CONVERSATION")
    ADD_CONVERSATION("ADD_CONVERSATION");

    private final String value;

    private UAPIResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
