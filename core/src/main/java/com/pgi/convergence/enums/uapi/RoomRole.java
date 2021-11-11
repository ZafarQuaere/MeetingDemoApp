package com.pgi.convergence.enums.uapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by visheshchandra on 1/5/2018.
 */

public enum RoomRole {
    @JsonProperty("HOST")
    HOST("Host"),
    @JsonProperty("PRESENTER")
    PRESENTER("Presenter"),
    @JsonProperty("GUEST")
    GUEST("Guest"),
    @JsonProperty("RECORDER")
    RECORDER("RECORDER");

    private final String value;

    private RoomRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
