package com.pgi.convergencemeetings.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.ConnectionQuality
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.base.di.featureTestMangerModule
import com.pgi.network.FurlProxyServiceAPI
import com.pgi.network.FurlProxyServiceManager
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config
import retrofit2.Response
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, application = TestApplication::class)
class NetworkQualityCheckerTest: AutoCloseKoinTest() {

	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()

	private val sharedPrefMock = mockkClass(SharedPreferencesManager::class, relaxed = true)
	private val furlProxyServiceAPIMock = mockkClass(FurlProxyServiceAPI::class, relaxed = true)

	@Before
	fun `set up`() {
		stopKoin()
		mockkStatic(SharedPreferencesManager::class)
		every {
			SharedPreferencesManager.getInstance()
		} returns sharedPrefMock
		mockkConstructor(FurlProxyServiceManager::class)
		every {
			anyConstructed<FurlProxyServiceManager>().create(any())
		} returns furlProxyServiceAPIMock
		every { furlProxyServiceAPIMock.getResolvedUrl() } returns Observable.just(Response.success("test"))
		every { sharedPrefMock.appConfig } returns null
		startKoin {
			modules(featureTestMangerModule)
		}
	}

	@Test
	fun `test quality using url`() {
		NetworkQualityChecker.testNetworkQualityUsingUrl("https:://test.com")
		NetworkQualityChecker.connectionQuality.test().assertNoErrors().await(5, TimeUnit.SECONDS)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is None`() {
		NetworkQualityChecker.determineNetworkQuality(50000.0)
		Assert.assertEquals(ConnectionQuality.NONE, NetworkQualityChecker.quality)
	}

	@Test
	fun `test quality is Poor`() {
		NetworkQualityChecker.determineNetworkQuality(10000.0)
		Assert.assertEquals(ConnectionQuality.POOR, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Moderate on 150`() {
		NetworkQualityChecker.determineNetworkQuality(6826.66)
		Assert.assertEquals(ConnectionQuality.MODERATE, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Moderate`() {
		NetworkQualityChecker.determineNetworkQuality(5000.0)
		Assert.assertEquals(ConnectionQuality.MODERATE, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}


	@Test
	fun `test quality is Moderate on 550`() {
		NetworkQualityChecker.determineNetworkQuality(1861.818)
		Assert.assertEquals(ConnectionQuality.MODERATE, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Good on 551`() {
		NetworkQualityChecker.determineNetworkQuality(1858.439)
		Assert.assertEquals(ConnectionQuality.GOOD, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Good`() {
		NetworkQualityChecker.determineNetworkQuality(1000.0)
		Assert.assertEquals(ConnectionQuality.GOOD, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Good on 1999`() {
		NetworkQualityChecker.determineNetworkQuality(512.0)
		Assert.assertEquals(ConnectionQuality.GOOD, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}

	@Test
	fun `test quality is Excellent`() {
		NetworkQualityChecker.determineNetworkQuality(50.0)
		Assert.assertEquals(ConnectionQuality.EXCELLENT, NetworkQualityChecker.quality)
		NetworkQualityChecker.stopBandWidthTest()
	}
}