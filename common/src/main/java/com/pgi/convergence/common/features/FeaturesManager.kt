package com.pgi.convergence.common.features

import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.R
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import io.reactivex.subjects.PublishSubject
import okio.Buffer
import okio.buffer
import okio.source
import java.nio.charset.Charset

class FeaturesManager(
		val configService: FeaturesConfigService,
		val logger: Logger) {

	private val TAG = FeaturesManager::class.java.simpleName
	val features: MutableMap<String, Boolean> = mutableMapOf()

	var userId: String? = null
		set(value) {
			field = value
			registerAppFeatures()
		}
	var companyEmail: String? = null
		set(value) {
			field = value
			registerAppFeatures()
		}
	var appConfigSubject: PublishSubject<AppConfig> = PublishSubject.create()
	private val resources: Resources = CoreApplication.appContext.resources
	var configJson: FeaturesConfig = FeaturesConfig()
	var appConfig = AppConfig()
	var shouldUpdateRemoteConfig = false

	init {
		updateLocalConfig()
		fetchRemoteConfig()
	}

	fun updateLocalConfig() {
		try {
			val localConfig = readLocalConfig()
			onNewConfig(localConfig)
		} catch (ex: Exception) {
			logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.LOCAL_CONFIG, "FeaturesConfigService " +
					"init - Failed loading local config file", ex, null)
		}
	}

	/**
	 * This to read the local config json file based on flavor
	 */
	internal fun readLocalConfig(): FeaturesConfig {
		val configSource = resources.openRawResource(R.raw.pgi_mobile_config).source().buffer()
		val configData = Buffer()
		configSource.readAll(configData)
		return GsonBuilder().serializeNulls().create().fromJson(configData.readString(Charset.forName("UTF-8")),
				FeaturesConfig::class.java)
	}
	/**
	 * This is to figure out which config to use for registering app featuers
	 */
	fun onNewConfig (config: FeaturesConfig) {
		val sharedPrefConfig = Gson().fromJson(SharedPreferencesManager.getInstance().appConfig, FeaturesConfig::class.java) ?: FeaturesConfig()
		configJson = if(config.version >= sharedPrefConfig.version) {
			SharedPreferencesManager.getInstance().appConfig = Gson().toJson(config)
			config
		} else {
			sharedPrefConfig
		}
		registerAppFeatures()
	}
	/**
	 * This is to register the feature with the featuremanager
	 *
	 * @param featureKey - The featureKey should be a concat value of FeatureServices, ":" andFeatures
	 * ex: meetings:voip, auth:calls, auth:voicemail etc.
	 * @param value - True or false
	 */
	fun registerFeature(featureKey: String, value: Boolean) {
		features[featureKey] = value
	}
	/**
	 * This is to check if a feature is enabled.
	 *
	 * @param feature - This should be the value from the Features enum
	 * @see Features
	 */
	fun isFeatureEnabled(feature: String): Boolean {
		val selectedFeature = features.filterKeys { it.split(":")[1].contentEquals(feature) }
		return if (selectedFeature.isEmpty())
			false
		else
			!selectedFeature.values.contains(false)
	}
	/**
	 * This is to register app features
	 *
	 * The config to use to register features is based on hierarchy as below
	 * 	beta
	 * 		alpha
	 * 			company
	 * 				app (default)
	 */
	private fun registerAppFeatures() {
		appConfig = configJson.app
		when {
			configJson.beta.users.contains(userId) -> appConfig.updateFrom(configJson.beta.app)
			configJson.alpha.users.contains(userId) -> appConfig.updateFrom(configJson.alpha.app)
			configJson.company.isNotEmpty() && configJson.company[0].containsKey(companyEmail) -> appConfig.updateFrom(configJson.company[0][companyEmail])
		}
		appConfig.let {
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.HOME.feature, it.homeEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.AGENDA.feature, it.agendaEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.CALLS.feature, it.callsEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.VOICEMAIL.feature, it.voiceMailEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.CHATS.feature, it.chatsEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.ENCRYPTVOIP.feature, it.encryptVoip ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.WEBCAMS.feature, it.webcamsEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.WEBCAMS_WIFI_ONLY_ENABLE.feature, it.webcamsOnWiFiOnlyEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.WEBCAMS_ON_CELLULAR_ENABLE.feature, it.webcamsOnCellularEnabled ?: false)
            registerFeature(FeatureServices.CONFIG.service + ":" + Features.CAVE_ENABLED.feature, it.caveEnabled ?: false)
			registerFeature(FeatureServices.CONFIG.service + ":" + Features.ROPE_ENABLED.feature, it.ropeEnabled ?: false)
		}
	}
	/**
	 * This will fetch the remote config file based on url defined in local config
	 */
	fun fetchRemoteConfig() {
		configJson.configurl?.let {
			configService.getConfig(it).subscribe(
					{ config ->
						onNewConfig(config)
					},
					{ exception ->
						logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.REMOTE_CONFIG, "FeaturesConfigService " +
								"init - Failed loading remote config file", exception, null)
					},
					{
						appConfigSubject.onNext(appConfig)
					})
		}
		shouldUpdateRemoteConfig = false
	}

	fun checkAndUpdateRemoteConfig() {
		if (shouldUpdateRemoteConfig) {
			fetchRemoteConfig()
		}
	}

    fun clear() {
		if (userId != null) {
			companyEmail = null
			userId = null
			updateLocalConfig()
			shouldUpdateRemoteConfig = true
		}
    }
}
