//package com.pgi.convergencemeetings.meeting.gm5.ui
//
//import android.content.Intent
//import androidx.test.core.app.ActivityScenario
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.core.app.launchActivity
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.pgi.convergence.common.features.FeaturesConfigService
//import com.pgi.convergence.common.features.FeaturesManager
//import com.pgi.convergence.constants.AppConstants
//import com.pgi.convergencemeetings.application.TestApplication
//import com.pgi.logging.Logger
//import com.pgi.network.repository.FurlProxyRepository
//import io.mockk.*
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.ObsoleteCoroutinesApi
//import kotlinx.serialization.UnstableDefault
//import org.junit.After
//import org.junit.Assert.assertNotNull
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.koin.core.context.startKoin
//import org.koin.dsl.module
//import org.koin.test.KoinTest
//import org.robolectric.annotation.Config
//import org.robolectric.annotation.LooperMode
//
//@UnstableDefault
//@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//@UseExperimental(ObsoleteCoroutinesApi::class)
//@Config(application = TestApplication::class, sdk = [28])
//class WebMeetingActivityTest: KoinTest {
//
//
//  lateinit var activityScenario: ActivityScenario<WebMeetingActivity>
//  lateinit var webActivity: WebMeetingActivity
//
//  private fun launchWebMeetingActivity(intent: Intent?){
//    activityScenario = launchActivity(intent)
//  }
//
//  @Before
//  fun `set up`() {
//    MockKAnnotations.init(this, relaxed = true)
//    mockkStatic(FurlProxyRepository::class)
//    val mockFurlRepository = mockkClass(FurlProxyRepository::class, relaxed = true)
//    every {
//      FurlProxyRepository.instance
//    } returns mockFurlRepository
//
//    startKoin {
//      modules(module {
//        // TODO:: move this to baseTestModule
//        val loggerMock = mockkClass(Logger::class, relaxed = true)
//        mockkConstructor(FeaturesConfigService::class)
//        mockkConstructor(FeaturesManager::class)
//        every {
//          anyConstructed<FeaturesManager>().fetchRemoteConfig()
//        } just runs
//
//        single { FeaturesConfigService() }
//        single { FeaturesManager(get(), loggerMock) }
//      })
//    }
//    val furlIntent = Intent(ApplicationProvider.getApplicationContext(), WebMeetingActivity::class.java)
//    furlIntent.putExtra(AppConstants.KEY_FURL, "https://pgi.globalmeet.com/testurl")
//    launchWebMeetingActivity(furlIntent)
//  }
//
//  @After
//  fun `tear down`() {
////    activityScenario.close()
//  }
//
//  @Test
//  fun `test onCreate`() {
//    activityScenario.onActivity {
//      webActivity = it
//      assertNotNull(it)
//    }
//  }
//}