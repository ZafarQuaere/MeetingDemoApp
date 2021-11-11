package com.pgi.convergence.enums;

/**
 * Created by amit1829 on 7/26/2017.
 */

public enum ApiConstants {

    PGI_WEB("PGII"),
    UAPI("UAPI"),
    PIA("PIA");

    private final String service;

    ApiConstants(final String service) {
        this.service = service;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return service;
    }
}
