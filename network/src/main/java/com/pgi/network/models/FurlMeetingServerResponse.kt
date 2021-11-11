package com.pgi.network.models


/**
 * Created by Sudheer Chilumula on 10/1/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class FurlMeetingServerResponse(
    val foxdenHost: String,
    val horsesHost: String,
    val logHost: String,
    val pgiidHost: String,
    val piaHost: String,
    val pulsarHost: String,
    val uapiHost: String,
    val wsHost: String,
    val privacyUrl: String,
    val tosUrl: String,
    val communityUrl: String,
    val region: String,
    val sipHost: String,
    val turnHost: String,
    val cloudRegion: String,
    val supportHost: String,
    val salesforceSupport: String,
    val softphone_reconnect_delay: String,
    val fileCabinetHost: String,
    val droidNumSupportedVersions: Int,
    val iosNumSupportedVersions: String,
    val c2Host: String,
    val o365AppId: String
)