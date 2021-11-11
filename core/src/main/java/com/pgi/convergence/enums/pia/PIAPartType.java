package com.pgi.convergence.enums.pia;

/**
 * Created by surbhidhingra on 25-10-17.
 */

public enum PIAPartType {
    NORMAL(0),
    MODERATOR(1),
    LISTEN_ONLY(2);

    private final int value;
    private PIAPartType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
