package com.pgi.convergence.enums.pia;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by surbhidhingra on 25-10-17.
 */

public enum PIAWebSocketStatus {

    SUCCESS(0),
    INVALIDSID(1);


    private final int value;
    private PIAWebSocketStatus(final int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }
}
