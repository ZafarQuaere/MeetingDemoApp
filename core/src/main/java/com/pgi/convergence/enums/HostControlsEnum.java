package com.pgi.convergence.enums;

/**
 * Created by surbhidhingra on 01-02-18.
 */

public enum HostControlsEnum {
    MUTE(1),
    UNMUTE(2),
    PROMOTE(3),
    DEMOTE(4),
    DISMISS(5),
    DISMISS_AUDIO_PARTICIPANT(6),
    PRIVATE_CHAT_WITH(7);

    private final int value;

    private HostControlsEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
