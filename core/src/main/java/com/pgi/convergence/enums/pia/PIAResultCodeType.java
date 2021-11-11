package com.pgi.convergence.enums.pia;

/**
 * Created by surbhidhingra on 25-10-17.
 */

public enum PIAResultCodeType {
    NULL(-1),
    SUCCESS(0),
    POLL_AGAIN(1),
    TIMEOUT(2),
    BACK_OFFICE_TIMEOUT_ERROR(3),
    INVALID_SID(4),
    INVALID_CREDENTIALS(5),
    INVALID_CONF_ID(6),
    INVALID_PART_ID(7),
    CONF_DOES_NOT_EXIST(8),
    PART_OPTION_ALREADY_SET(9),
    DIALOUT_FAIL_BLOCKED (10),
    DIALOUT_FAIL_ERROR(11),
    DIALOUT_FAIL_HANGUP(12),
    DIALOUT_FAIL_NO_DIGITS(13),
    DIALOUT_FAIL_INTL_NOT_ALLOWED(14),
    SUBSCRIBE_FAIL_MAX_SESSIONS(15),
    WATCH_FAIL_MAX_WATCHES(16),
    UNSUPPORTED_REQUEST(17),
    ERROR_PGI_BACKOFFICE(18);

    private final int value;
    private PIAResultCodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
