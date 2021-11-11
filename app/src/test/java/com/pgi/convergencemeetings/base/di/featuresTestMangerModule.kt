package com.pgi.convergencemeetings.base.di

import com.pgi.convergence.common.features.FeaturesConfigService
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.logging.Logger
import io.mockk.every
import io.mockk.mockkClass
import io.reactivex.Observable
import org.koin.dsl.module

val featureTestMangerModule = module {

	val featureServiceMock = FeaturesConfigService()
	val loggerMock = mockkClass(Logger::class, relaxed = true)

	val mockSharedConfigData = "{\"app\":{\"agendaEnabled\":true,\"callsEnabled\":false," +
			"\"chatsEnabled\":false,\"homeEnabled\":true,\"voiceMailEnabled\":false, +" +
			"\"joinMeetingAttemptMaxDuration\": 15000},\"configurl\":\"https://mobiledev.globalmeet.net\", +" +
			"\"ucaas\":{\"baseUrl\":\"https://crossbar.uc.globalmeet.com/\",\"calls\":{\"timerInterval\":15}, +" +
			"\"voicemail\":{\"timerInterval\":15}}}"

//	every {
//		featureServiceMock.getConfig(any())
//	} returns Observable.create {
//		FeaturesConfig(configurl = "https://test.com")
//	}

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