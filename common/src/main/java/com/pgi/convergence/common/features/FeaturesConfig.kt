package com.pgi.convergence.common.features

data class FeaturesConfig(
		val version: Int = -1,
		val configurl: String? = null,
		val app: AppConfig = AppConfig(),
		val company: List<Map<String, AppConfig>> = emptyList(),
		val alpha: PreRelease = PreRelease(),
		val beta: PreRelease = PreRelease()
)

data class AppConfig(
		var agendaEnabled: Boolean? = null,
		var callsEnabled: Boolean? = null,
		var chatsEnabled: Boolean? = null,
		var homeEnabled: Boolean? = null,
		var webcamsEnabled: Boolean? = null,
		var webcamsOnWiFiOnlyEnabled: Boolean? = null,
		var webcamsOnCellularEnabled: Boolean? = null,
		var voiceMailEnabled: Boolean? = null,
		var joinMeetingAttemptMaxDuration: Long? = null,
		var bandwidth: Bandwidth? = null,
		var ucaas: Ucaas? = null,
		var encryptVoip: Boolean? = null,
		var caveEnabled: Boolean? = null,
		var ropeEnabled: Boolean? = null
)

data class Ucaas(
		var baseUrl: String? = null,
		var calls: Calls? = null,
		var voicemail: Voicemail? = null
)

data class Bandwidth(
		var meeting: BandWidthTest? = null,
		var home: BandWidthTest? = null
)

data class BandWidthTest(
		var testEnabled: Boolean? = null,
		var timerInterval: Long? = -1
)

data class Calls(
		var timerInterval: Long? = -1
)

data class Voicemail(
		var timerInterval: Long? = -1
)

data class PreRelease(
		val app: AppConfig? = null,
		val users: List<String> = emptyList()
)

fun AppConfig.updateFrom(config: AppConfig?) {
	config?.agendaEnabled?.let { agendaEnabled = it }
	config?.callsEnabled?.let { callsEnabled = it }
	config?.chatsEnabled?.let { chatsEnabled = it }
	config?.homeEnabled?.let { homeEnabled = it }
	config?.voiceMailEnabled?.let { voiceMailEnabled = it }
	config?.encryptVoip?.let { encryptVoip = it }
	config?.webcamsEnabled?.let { webcamsEnabled = it }
	config?.caveEnabled?.let { caveEnabled = it }
	config?.ropeEnabled?.let { ropeEnabled = it }
	config?.webcamsOnWiFiOnlyEnabled?.let { webcamsOnWiFiOnlyEnabled = it }
	config?.webcamsOnCellularEnabled?.let { webcamsOnCellularEnabled = it }
	config?.joinMeetingAttemptMaxDuration?.let {joinMeetingAttemptMaxDuration = it}
	config?.bandwidth.let { bwd ->
		bwd?.meeting?.testEnabled.let { bandwidth?.meeting?.testEnabled = it}
		bwd?.meeting?.timerInterval.let { bandwidth?.meeting?.timerInterval = it}
		bwd?.home?.testEnabled.let { bandwidth?.home?.testEnabled = it}
		bwd?.home?.timerInterval.let { bandwidth?.home?.timerInterval = it}
	}
	config?.ucaas?.let { uc ->
		uc.baseUrl?.let { ucaas?.baseUrl = it }
		uc.calls?.timerInterval?.let { ucaas?.calls?.timerInterval = it }
		uc.voicemail?.timerInterval?.let { ucaas?.voicemail?.timerInterval = it }
	}
}