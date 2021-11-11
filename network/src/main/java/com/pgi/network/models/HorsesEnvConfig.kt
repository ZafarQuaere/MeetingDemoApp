package com.pgi.network.models

import java.io.Serializable


/**
 * Created by Sudheer Chilumula on 10/1/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class HorsesEnvConfig(
    val browserMinVersions: BrowserMinVersions? = null,
    val c2Host: String? = null,
    val cloudRegion: String? = null,
    val communityUrl: String? = null,
    val droidNumSupportedVersions: String? = null,
    val fileCabinetHost: String? = null,
    val foxdenHost: String? = null,
    val horsesHost: String? = null,
    val iosNumSupportedVersions: String? = null,
    val logHost: String? = null,
    val o365AppId: String? = null,
    val pgiidHost: String? = null,
    val piaHost: String? = null,
    val privacyUrl: String? = null,
    val pulsarHost: String? = null,
    val region: String? = null,
    val salesforceSupport: String? = null,
    val sipHost: String? = null,
    val softphone_reconnect_delay: String? = null,
    val stunHost: String? = null,
    val supportHost: String? = null,
    val tosUrl: String? = null,
    val turnHost: String? = null,
    val uapiHost: String? = null,
    val wsHost: String? = null,
    val cave: Cave? = null

)

data class BrowserMinVersions(
    val Chrome: String? = null,
    val Firefox: String? = null,
    val IE: String? = null
)

data class Cave (
        val sipWss: String? = null,
        val sipTls: String? = null,
        val turnRest: String? = null
): Serializable