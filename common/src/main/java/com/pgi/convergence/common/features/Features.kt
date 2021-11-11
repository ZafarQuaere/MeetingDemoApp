package com.pgi.convergence.common.features

/**
 * This should list all features within app we can enable and disable based on flag
 */
enum class Features(val feature: String) {
	CALLS("calls"),
	CHATS("chats"),
	HOME("home"),
	AGENDA("agenda"),
	VOICEMAIL("voicemail"),
	VOIP("voip"),
	DIALIN("dialin"),
	ENCRYPTVOIP("encryptVoip"),
	WEBCAMS("webcams"),
	WEBCAMS_WIFI_ONLY_ENABLE("webcamsOnWiFiOnlyEnabled"),
	WEBCAMS_ON_CELLULAR_ENABLE("webcamsOnCellularEnabled"),
    CAVE_ENABLED("caveEnabled"),
    ROPE_ENABLED("ropeEnabled")
}

/**
 * This should list all the service calls up on which a feature is dictated by
 */
enum class FeatureServices(val service: String) {
	CONFIG("config"),
	AUTH("auth"),
	MEETINGS("meetings"),
	CLIENTINFO("clientinfo")
}
