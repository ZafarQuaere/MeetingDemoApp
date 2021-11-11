package com.pgi.convergence.enums;

/**
 * Created by ashwanikumar on 11/2/2017.
 */

public enum ConnectionType {
    VOIP(0),
    DIAL_IN(1),
    DIAL_OUT(2),
    NO_AUDIO(3);

    private final int value;
    private ConnectionType(int value) {
            this.value = value;
        }

    public int getValue() {
            return value;
        }
}
