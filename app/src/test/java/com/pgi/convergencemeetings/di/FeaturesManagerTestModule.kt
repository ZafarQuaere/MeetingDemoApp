package com.pgi.convergencemeetings.di

import androidx.test.core.app.ApplicationProvider
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.common.features.FeaturesConfig
import com.pgi.convergence.common.features.FeaturesConfigService
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.common.profile.ProfileManager
import com.pgi.convergence.data.model.msal.MsalCalEvent
import com.pgi.convergence.data.repository.msal.MSALAuthRespositoryImpl
import com.pgi.convergence.data.repository.msal.MSALGraphRepositoryImpl
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.logging.Logger
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository
import com.pgi.network.repository.GMWebServiceRepository
import io.mockk.*
import io.reactivex.Observable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featuresMangerModule = module {

	val featureServiceMock = FeaturesConfigService()
	val loggerMock = mockkClass(Logger::class, relaxed = true)
	val featuresManager = mockkClass(FeaturesManager::class, relaxed = true)
	val sharedPreferencesManager = mockkClass(SharedPreferencesManager::class, relaxed = true)

	val mockSharedConfigData4 = "{\n  \"version\": 2,\n  \"configurl\": \"https://mobile.qab.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": true,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n  \"webcamsEnabled\": true,\n  \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n    \n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"

	every {
		sharedPreferencesManager.getCloudRegion()
	} returns "http://web-na.globalmeet"

	mockkStatic(SharedPreferencesManager::class)
	every {
		SharedPreferencesManager.getInstance().getCloudRegion()
	} returns "http://web-na.globalmeet"

	every {
		featuresManager.isFeatureEnabled(Features.WEBCAMS.feature)
	} returns false

	every {
		featuresManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)
	} returns false

	every {
		FeaturesManager(featureServiceMock, loggerMock)
	} returns featuresManager

	factory {
		FeaturesManager(featureServiceMock, loggerMock)
	}
}

val featuresMangerModuleCompany = module {

	val featureServiceMock = FeaturesConfigService()
	val loggerMock = mockkClass(Logger::class, relaxed = true)
	val featuresManager = mockkClass(FeaturesManager::class, relaxed = true)
	val sharedPreferencesManager = mockkClass(SharedPreferencesManager::class, relaxed = true)

	val mockSharedConfigData4 = "{\n  \"version\": 2,\n  \"configurl\": \"https://mobile.qab.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": true,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n  \"webcamsEnabled\": true,\n  \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n    \n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"

	every {
		sharedPreferencesManager.getCloudRegion()
	} returns "http://web-na.globalmeet"

	mockkStatic(SharedPreferencesManager::class)
	every {
		SharedPreferencesManager.getInstance().getCloudRegion()
	} returns "http://web-na.globalmeet"

	every {
		featuresManager.isFeatureEnabled(Features.WEBCAMS.feature)
	} returns true

	every {
		featuresManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)
	} returns true

	every {
		FeaturesManager(featureServiceMock, loggerMock)
	} returns featuresManager

	factory {
		FeaturesManager(featureServiceMock, loggerMock)
	}
}

val featuresRemoteErrorMangerModule = module {

	val featureServiceMock = mockkClass(FeaturesConfigService::class, relaxed = true)
	val loggerMock = mockkClass(Logger::class, relaxed = true)

	val mockSharedConfigData = "{\"app\":{\"agendaEnabled\":true,\"callsEnabled\":false," +
			"\"chatsEnabled\":false,\"homeEnabled\":true,\"voiceMailEnabled\":false},\"configurl\":\"https://mobiledev.globalmeet.net\",\"ucaas\":{\"baseUrl\":\"https://crossbar.uc.globalmeet.com/\",\"calls\":{\"timerInterval\":15},\"voicemail\":{\"timerInterval\":15}}}"

	every {
		featureServiceMock.getConfig(any())
	} returns Observable.error {
		Exception("Failed")
	}

	factory {
		FeaturesManager(featureServiceMock, loggerMock)
	}
}