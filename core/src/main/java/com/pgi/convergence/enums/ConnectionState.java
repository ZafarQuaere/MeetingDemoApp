package com.pgi.convergence.enums;

/**
 * Created by ashwanikumar on 11/2/2017.
 */

public enum ConnectionState {
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2);

    private final int value;
    private ConnectionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
