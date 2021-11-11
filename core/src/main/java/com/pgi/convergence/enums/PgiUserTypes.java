package com.pgi.convergence.enums;

/**
 * Created by amit1829 on 11/16/2017.
 */

public enum PgiUserTypes {

    USER_TYPE_GUEST("Guest"),
    USER_TYPE_HOST("Host");

    private final String userType;

    PgiUserTypes(final String userType) {
        this.userType = userType;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return userType;
    }
}
