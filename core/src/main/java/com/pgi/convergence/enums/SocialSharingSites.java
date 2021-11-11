package com.pgi.convergence.enums;

/**
 * Created by amit1829 on 9/12/2017.
 */

public enum SocialSharingSites {

    FACEBOOK("facebook"),
    EMAIL("android.email"),
    TWITTER("twitter"),
    MMS("mms"),
    GMAIL("android.gm"),
    LINKEDIN("linkedin"),
    WHATSAPP("whatsapp"),
    GOOGLE_PLUS("google.android.apps.plus");

    private final String socialSite;

    SocialSharingSites(final String socialSite) {
        this.socialSite = socialSite;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return socialSite;
    }
}
