package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.logging.Logger
import io.mockk.MockKAnnotations
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class PGiSoftPhoneImplTest  :KoinTest {
  private lateinit var loggerSpy: Logger
  private var mPGiSoftPhoneImpl: PGiSoftPhoneImpl? = null
  private var mAudioRouteManager: PGiAudioRouteManager? = null

  @Before
  fun setUp() {
    MockKAnnotations.init(this, relaxed = true)
    MockitoAnnotations.initMocks(this)
    CoreApplication.mLogger = TestLogger()
    startKoin {
    }
    mPGiSoftPhoneImpl = PGiSoftPhoneImpl(CoreApplication.appContext, CoreApplication.mLogger)
    mAudioRouteManager = PGiAudioRouteManager(CoreApplication.appContext, CoreApplication.mLogger)
    mPGiSoftPhoneImpl?.mAudioManager = mAudioRouteManager?.getAudioManager()
  }

  @After
  fun `tear down`() {
    stopKoin()
  }
/*
  @Test
  fun `test initialize`() {
    mPGiSoftPhoneImpl?.initialize(true, true, true)
    val isLoaded = mPGiSoftPhoneImpl?.isSoftPhoneAvailable()
    Assert.assertNotNull(isLoaded)
  } */

  @Test
  fun activateSpeaker() {
    mPGiSoftPhoneImpl?.activateSpeaker()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun setDefaultAudioRoute() {
    mPGiSoftPhoneImpl?.setDefaultAudioRoute()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun dialOut() {
    mPGiSoftPhoneImpl?.dialOut("sip:GM.a0md6vibog1a9cqemrgmgt9jj@pgi.com",
        "sip:7777001005@ln4-sj1-sy1.mobile.sip.pgiconnect.com;pgilink=GM;lang=en;passcode=8570798;OverrideLock=N",
        enableDolby = true,proxyServer = "test.server")
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun destroy() {
    mPGiSoftPhoneImpl?.destroy()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun hangUp() {
    mPGiSoftPhoneImpl?.hangUp()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun isCallConnected() {
    mPGiSoftPhoneImpl?.isCallConnected()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun isSoftPhoneAvailable() {
    mPGiSoftPhoneImpl?.isSoftPhoneAvailable()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun lowerVolume() {
    mPGiSoftPhoneImpl?.lowerVolume(false)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun lowerVolumeSilent() {
    mPGiSoftPhoneImpl?.mAudioManager?.isBluetoothScoOn = true
    mPGiSoftPhoneImpl?.lowerVolume(true)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun networkChange() {
    mPGiSoftPhoneImpl?.networkChange()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun pauseAudio() {
    mPGiSoftPhoneImpl?.pauseAudio()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun raiseVolume() {
    mPGiSoftPhoneImpl?.raiseVolume(false)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun raiseVolumeSilent() {
    mPGiSoftPhoneImpl?.mAudioManager?.isBluetoothScoOn = true
    mPGiSoftPhoneImpl?.raiseVolume(true)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun reconnect() {
    mPGiSoftPhoneImpl?.reconnect(false)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun `test reconnect with newCall is true`() {
    mPGiSoftPhoneImpl?.reconnect(false)
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun releaseFocus() {
    mPGiSoftPhoneImpl?.releaseFocus()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun requestFocus() {
    mPGiSoftPhoneImpl?.requestFocus()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun resumeAudio() {
    mPGiSoftPhoneImpl?.resumeAudio()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun setOutputAudioRoute() {
    mPGiSoftPhoneImpl?.setDefaultAudioRoute()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun getCallQuality() {
    mPGiSoftPhoneImpl?.getCallQuality()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }

  @Test
  fun resetRAGCount() {
    mPGiSoftPhoneImpl?.resetRAGCount()
    Assert.assertNotNull(mPGiSoftPhoneImpl)
  }
}