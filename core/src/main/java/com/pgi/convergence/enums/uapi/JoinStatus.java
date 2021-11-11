package com.pgi.convergence.enums.uapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Join Meeting status
 * <p>
 * Created by surbhidhingra on 01-12-17.
 */

public enum JoinStatus {
    /**
     * The participant joined the meeting.
     */
    @JsonProperty("JOIN")
    JOIN("JOIN"),

    /**
     * The meeting is locked.
     */
    @JsonProperty("LOCK")
    LOCK("LOCK"),

    /**
     * The participant must wait to join meeting.
     */
    @JsonProperty("WAIT")
    WAIT("WAIT"),

    /**
     * The meeting is at capacity.
     */
    @JsonProperty("CAPACITY")
    CAPACITY("CAPACITY"),

    /**
     * The meeting has ended.
     */
    @JsonProperty("MEETING_ENDED")
    MEETING_ENDED("MEETING_ENDED"),

    /**
     * The participant session ID has already been used to join a meeting.
     */
    @JsonProperty("SESSION_USED")
    SESSION_USED("SESSION_USED"),

    /**
     * The join command timed out.
     */
    @JsonProperty("COMMAND_TIMEOUT")
    COMMAND_TIMEOUT("COMMAND_TIMEOUT"),

    /**
     * Unknown join status returned by domain command handler.
     */
    @JsonProperty("UNKNOWN")
    UNKNOWN("UNKNOWN");

    private final String value;

    private JoinStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

