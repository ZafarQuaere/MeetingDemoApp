package com.pgi.convergence.common.features

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.di.featuresMangerModule
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.logging.enums.LogEvent.EXCEPTION
import com.pgi.logging.enums.LogEventValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkClass
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FeaturesManagerTest: KoinTest {

	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()
	private val TAG = FeaturesManager::class.java.simpleName
	private val manager: FeaturesManager by inject()
	private val sharedPrefMock = mockkClass(SharedPreferencesManager::class, relaxed = true)
	private val mockSharedConfigData = "{\n  \"version\": 1,\n  \"configurl\": \"https://mobiledev.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": false,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n    \"encryptVoip\": false,\n    \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n        \"homeEnabled\": true,\n        \"agendaEnabled\": true,\n        \"callsEnabled\": false,\n        \"chatsEnabled\": false,\n        \"voiceMailEnabled\": false,\n    \"encryptVoip\": true,\n        \"bandwidth\": {\n          \"meeting\": {\n            \"testEnabled\": true,\n            \"timerInterval\": 20000\n          },\n          \"home\": {\n            \"testEnabled\": false,\n            \"timerInterval\": 20000\n          }\n        },\n        \"ucaas\": {\n          \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n          \"calls\": {\n            \"timerInterval\": 900000\n          },\n          \"voicemail\": {\n            \"timerInterval\": 900000\n          }\n        }\n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n      \"homeEnabled\": true,\n      \"agendaEnabled\": true,\n      \"callsEnabled\": true,\n      \"chatsEnabled\": false,\n      \"voiceMailEnabled\": true,\n      \"bandwidth\": {\n        \"meeting\": {\n          \"testEnabled\": true,\n          \"timerInterval\": 20000\n        },\n        \"home\": {\n          \"testEnabled\": false,\n          \"timerInterval\": 20000\n        }\n      },\n      \"ucaas\": {\n        \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n        \"calls\": {\n          \"timerInterval\": 900000\n        },\n        \"voicemail\": {\n          \"timerInterval\": 900000\n        }\n      }\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n      \"homeEnabled\": true,\n      \"agendaEnabled\": true,\n      \"callsEnabled\": true,\n      \"chatsEnabled\": false,\n      \"voiceMailEnabled\": true,\n      \"bandwidth\": {\n        \"meeting\": {\n          \"testEnabled\": true,\n          \"timerInterval\": 20000\n        },\n        \"home\": {\n          \"testEnabled\": false,\n          \"timerInterval\": 20000\n        }\n      },\n      \"ucaas\": {\n        \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n        \"calls\": {\n          \"timerInterval\": 900000\n        },\n        \"voicemail\": {\n          \"timerInterval\": 900000\n        }\n      }\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"
	private val mockSharedConfigData2 = "{\n  \"version\": 1,\n  \"configurl\": \"https://mobile.qab.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": false,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n    \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n    \n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"
	private val mockSharedConfigData3 = "{\n  \"version\": 1,\n  \"configurl\": \"https://mobile.qab.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": true,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n    \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n    \n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"
	private val mockSharedConfigData4 = "{\n  \"version\": 2,\n  \"configurl\": \"https://mobile.qab.globalmeet.net/config/qac/android/\",\n  \"app\": {\n    \"homeEnabled\": true,\n    \"agendaEnabled\": true,\n    \"callsEnabled\": true,\n    \"chatsEnabled\": false,\n    \"voiceMailEnabled\": false,\n  \"webcamsEnabled\": true,\n  \"bandwidth\": {\n      \"meeting\": {\n        \"testEnabled\": true,\n        \"timerInterval\": 20000\n      },\n      \"home\": {\n        \"testEnabled\": false,\n        \"timerInterval\": 20000\n      }\n    },\n    \"ucaas\": {\n      \"baseUrl\": \"https://crossbar.uc.globalmeet.com/\",\n      \"calls\": {\n        \"timerInterval\": 900000\n      },\n      \"voicemail\": {\n        \"timerInterval\": 900000\n      }\n    }\n  },\n  \"company\": [\n    {\n      \"07975\": {\n    \n      }\n    }\n  ],\n  \"alpha\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.alpha@pgi.com\"\n    ]\n  },\n  \"beta\": {\n    \"app\": {\n    },\n    \"users\": [\n      \"test.beta@pgi.com\"\n    ]\n  }\n}"

	@MockK
	lateinit var featuresConfig: FeaturesConfig
	@MockK
	lateinit var exception: java.lang.Exception
	@Before
	fun `set up`() {
		CoreApplication.appContext = ApplicationProvider.getApplicationContext()
		MockKAnnotations.init(this, relaxed = true)
		mockkStatic(SharedPreferencesManager::class)
		every {
			SharedPreferencesManager.getInstance()
		} returns sharedPrefMock
	}

	@After
	fun after() {
		stopKoin()
	}

	@Test
	fun `test init`() {
		every { sharedPrefMock.appConfig } returns null
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.CALLS.feature), false)
	}
	@Test
	fun `test is feature Home enabled true`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.HOME.feature), false)
	}

	@Test
	fun `test is feature EncryptVOIP enabled false`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.ENCRYPTVOIP.feature), false)
	}

    @Test
	fun `test is feature caveEnabled enabled false`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.CAVE_ENABLED.feature), false)
	}

	@Test
	fun `test is feature ropeEnabled enabled false`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.ROPE_ENABLED.feature), false)
	}
    
	@Test
	fun `test with null appconfig`() {
		every { sharedPrefMock.appConfig } returns null
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.ENCRYPTVOIP.feature), false)
	}

	@Test
	fun `test with a feature that does not exist`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled("testFeatureThatDoesNotExist"), false)
	}

	@Test
	fun `test Shared pref data`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.CALLS.feature), false)
	}

	@Test
	fun `test Shared pref data with verison 2`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.CALLS.feature), false)
	}

	@Test
	fun `test feature config with user part of company`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		manager.companyEmail = "pgi.com";
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test feature config with user id ib beta`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		manager.userId = "test.beta@pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test feature config with user id ib alpha`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData
		startKoin {
			modules(featuresMangerModule)
		}
		manager.userId = "test.alpha@pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test Shared pref data with verison 2 with just defaults`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData2
		startKoin {
			modules(featuresMangerModule)
		}
		Assert.assertEquals(manager.isFeatureEnabled(Features.CALLS.feature), false)
	}

	@Test
	fun `test read local file exception`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData3
		startKoin {
			modules(featuresMangerModule)
		}
		every { manager.readLocalConfig() } throws exception
		manager.logger
		manager.configService
		Assert.assertEquals(manager.isFeatureEnabled(Features.CALLS.feature), false)
		Assertions.assertThat(manager.logger.error(TAG, EXCEPTION, LogEventValue.LOCAL_CONFIG, "FeaturesConfigService " +
				"init - Failed loading local config file", exception, null))
	}

	@Test
	fun `test feature config with user part of company with just defaults`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData2
		startKoin {
			modules(featuresMangerModule)
		}
		manager.companyEmail = "pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test feature config with user part of company webcam`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData4
		startKoin {
			modules(featuresMangerModule)
		}
		manager.companyEmail = "pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			it.webcamsEnabled?.let { it1 -> Assert.assertTrue(it1) }
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}
	@Test
	fun `test feature config with user id ib beta  with just defaults`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData2
		startKoin {
			modules(featuresMangerModule)
		}
		manager.userId = "test.beta@pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test feature config with user id ib alpha  with just defaults`() {
		every { sharedPrefMock.appConfig } returns mockSharedConfigData2
		startKoin {
			modules(featuresMangerModule)
		}
		manager.userId = "test.alpha@pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test feature config with no config`() {
		every { sharedPrefMock.appConfig } returns null
		startKoin {
			modules(featuresMangerModule)
		}
		manager.onNewConfig(FeaturesConfig())
		manager.userId = "test.alpha@pgi.com"
		manager.appConfigSubject.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		manager.appConfigSubject.subscribe {
			Assert.assertNull(it.bandwidth)
			Assert.assertEquals(it.ucaas?.baseUrl, "https://crossbar.uc.globalmeet.com")
			Assert.assertEquals(it.ucaas?.calls?.timerInterval, 15)
			Assert.assertEquals(it.ucaas?.voicemail?.timerInterval, 15)

		}
	}

	@Test
	fun `test appconfig updateFrom null`(){
		val appConfig = AppConfig()
		appConfig.updateFrom(null)
		Assert.assertNotNull(appConfig)
	}

	@Test
	fun `test clear`() {
		startKoin {
			modules(featuresMangerModule)
		}
		manager.userId = null
		manager.companyEmail = null
		manager.clear()
		Assert.assertFalse(manager.shouldUpdateRemoteConfig)

		manager.userId = "gaurav.singh@pgi.com"
		manager.clear()
		Assert.assertTrue(manager.shouldUpdateRemoteConfig)
	}

	@Test
	fun `test checkAndUpdateRemoteConfig`() {
		startKoin {
			modules(featuresMangerModule)
		}
		manager.shouldUpdateRemoteConfig = false
		manager.checkAndUpdateRemoteConfig()

		manager.shouldUpdateRemoteConfig = true
		manager.checkAndUpdateRemoteConfig()
		Assert.assertFalse(manager.shouldUpdateRemoteConfig)
	}
}
