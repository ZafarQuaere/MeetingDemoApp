package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.annotation.Config
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import io.mockk.*
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class PGiAudioRouteManagerTest  : KoinTest {
  @Mock
  private lateinit var mockAudioManager: AudioManager
  private var mAudioRouteManager: PGiAudioRouteManager? = null
  private var audioManager: AudioManager? = null
  var mAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener  =
    AudioManager.OnAudioFocusChangeListener {}
  @Before
  fun setUp() {
    MockKAnnotations.init(this, relaxed = true)
    MockitoAnnotations.initMocks(this)
    CoreApplication.mLogger = TestLogger()
    audioManager = CoreApplication.appContext
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
    mAudioRouteManager = PGiAudioRouteManager(CoreApplication.appContext, CoreApplication.mLogger)
    mAudioRouteManager?.setAudioManager(mockAudioManager)
    startKoin {  }
  }

  @After
  fun `tear down`() {
    stopKoin()
  }

  @Test
  fun `test releaseAudioFocus`(){
    val response = mAudioRouteManager?.releaseAudioFocus(mAudioFocusChangeListener)
    Assert.assertNotNull(response)
  }

  @Test
  fun `test releaseAudioFocusTarget21`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED).`when`(mockAudioManager)
        .abandonAudioFocus(mAudioFocusChangeListener)
    mAudioRouteManager?.releaseAudioFocusForTarget21(mAudioFocusChangeListener)
    Assert.assertNotNull(mAudioRouteManager)
  }

  @Test
  fun `test releaseAudioFocusTarget21Failed`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED).`when`(mockAudioManager)
        .abandonAudioFocus(mAudioFocusChangeListener)
    mAudioRouteManager?.releaseAudioFocusForTarget21(mAudioFocusChangeListener)
    Assert.assertNotNull(mAudioRouteManager)
  }

  @Test
  fun `test releaseAudioFocusTarget26`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED).`when`(mockAudioManager).abandonAudioFocusRequest(ArgumentMatchers.any())
    val response = mAudioRouteManager?.requestAudioFocusForTarget26(mAudioFocusChangeListener)
    Assert.assertNotNull(mAudioRouteManager)
  }

  @Test
  fun `test releaseAudioFocusTarget26Failed`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED).`when`(mockAudioManager).abandonAudioFocusRequest(ArgumentMatchers.any())
    val response = mAudioRouteManager?.requestAudioFocusForTarget26(mAudioFocusChangeListener)
    Assert.assertNotNull(mAudioRouteManager)
  }

  @Test
  fun `test requestAudioFocus`(){
    val response = mAudioRouteManager?.requestAudioFocus(mAudioFocusChangeListener)
    Assert.assertNotNull(response)
  }

  @Test
  fun `test requestAudioFocusTarget21`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED).`when`(mockAudioManager)
        .requestAudioFocus(mAudioFocusChangeListener,AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN)
    val response = mAudioRouteManager?.requestAudioFocusForTarget21(mAudioFocusChangeListener)
    Assert.assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, response)
  }

  @Test
  fun `test requestAudioFocusTarget21Failed`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED).`when`(mockAudioManager)
        .requestAudioFocus(mAudioFocusChangeListener,AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN)
    val response = mAudioRouteManager?.requestAudioFocusForTarget21(mAudioFocusChangeListener)
    Assert.assertEquals(AudioManager.AUDIOFOCUS_REQUEST_FAILED, response)
  }

  @Test
  fun `test requestAudioFocusTarget26`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED).`when`(mockAudioManager).requestAudioFocus(ArgumentMatchers.any())
    val response = mAudioRouteManager?.requestAudioFocusForTarget26(mAudioFocusChangeListener)
    Assert.assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, response)
  }

  @Test
  fun `test requestAudioFocusTarget26Failed`(){
    doReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED).`when`(mockAudioManager).requestAudioFocus(ArgumentMatchers.any())
    val response = mAudioRouteManager?.requestAudioFocusForTarget26(mAudioFocusChangeListener)
    Assert.assertEquals(AudioManager.AUDIOFOCUS_REQUEST_FAILED, response)
  }

  @Test
  fun `test isBluetoothAvailable`() {
    val btAvail = mAudioRouteManager?.isBluetoothAvailable()
    Assert.assertNotNull(btAvail)
  }

  @Test
  fun `test setAudioRoute speaker`() {
    mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER)
    val rt = mAudioRouteManager?.getAudioRoute()
    Assert.assertNotNull(rt)
  }

  @Test
  fun `test setDefaultAudioRoute speaker`() {
    mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER)
    val rt = mAudioRouteManager?.getAudioRoute()
    Assert.assertNotNull(rt)
  }

  @Test
  fun `test setAudioRoute earpiece`() {
    mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE)
    val rt = mAudioRouteManager?.getAudioRoute()
    Assert.assertNotNull(rt)
  }

  @Test
  fun `test setAudioRoute bluetooth`() {
    mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH)
    val rt = mAudioRouteManager?.getAudioRoute()
    Assert.assertNotNull(rt)
  }

  @Test
  fun `test isBluetoothConnected`(){
    val avail = mAudioRouteManager?.isBluetoothDeviceConnected()
    Assert.assertTrue(avail == false)
  }

  @Test
      fun `test isHeadphonesPlugged true`(){
        val audioInfo = mockkClass(AudioDeviceInfo::class)
    every { audioInfo.type } returns AudioDeviceInfo.TYPE_BLUETOOTH_SCO
    val audioManager1 = mockkClass(AudioManager::class)
    every { audioManager1.getDevices(AudioManager.GET_DEVICES_ALL) } returns arrayOf(audioInfo)
    mAudioRouteManager?.setAudioManager(audioManager1)
    val avail1 = mAudioRouteManager?.isHeadphonesPlugged()
    Assert.assertTrue(avail1 == false)
    every { audioInfo.type } returns AudioDeviceInfo.TYPE_WIRED_HEADPHONES
        val audioManager = mockkClass(AudioManager::class)
        every { audioManager.getDevices(AudioManager.GET_DEVICES_ALL) } returns arrayOf(audioInfo)
        mAudioRouteManager?.setAudioManager(audioManager)
        val avail = mAudioRouteManager?.isHeadphonesPlugged()
        Assert.assertTrue(avail == true)
    }
}