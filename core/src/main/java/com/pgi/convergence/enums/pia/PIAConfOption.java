package com.pgi.convergence.enums.pia;

/**
 * Created by surbhidhingra on 25-10-17.
 */

public enum PIAConfOption {
    NULL(-1),
    LOCK(0),
    UNLOCK(1),
    MUSIC_ON(2),
    MUSIC_OFF(3),
    MUTE(4),
    UNMUTE(5),
    TALKER_NOTIFY_ON(6),
    TALKER_NOTIFY_OFF(7);

    private final int value;
    private PIAConfOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
