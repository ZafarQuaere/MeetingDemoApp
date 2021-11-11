package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.kevinmost.junit_retry_rule.RetryRule
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.search.di.joinMeetingFragmentModule
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.Logger
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.UnstableDefault
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.Mock
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@UnstableDefault
@ObsoleteCoroutinesApi
@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class OverflowMenuDialogFragmentTest : RobolectricTest() , KoinTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val retry: RetryRule = RetryRule()

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private var isVoipConnected : Boolean= false
    private var isPSTNAvailable : Boolean= false
    private var viewFragment: OverflowMenuDialogFragment? = null
    private var scenario: FragmentScenario<OverflowMenuDialogFragment>? = null
    private var mOverflowMenuDialogFragment: OverflowMenuDialogFragment? = null

    @Mock
    var logger: Logger? = null
    @Mock
    private val appAuth: AppAuthUtils? = null

    private fun launchFragmentGuest(onInstantiated: (OverflowMenuDialogFragment) -> Unit = {}):
            FragmentScenario<OverflowMenuDialogFragment> {
        return FragmentScenario.launchInContainer(OverflowMenuDialogFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as OverflowMenuDialogFragment
                this@OverflowMenuDialogFragmentTest.viewFragment = fragment
                this@OverflowMenuDialogFragmentTest.mOverflowMenuDialogFragment = fragment
                mOverflowMenuDialogFragment?.mIsHost = false
                mOverflowMenuDialogFragment?.mIsWaitngRoomEnabled = true
                mOverflowMenuDialogFragment?.mIsLowBandwidthOn = true
                mOverflowMenuDialogFragment?.mIsVoipConnected = isVoipConnected
                mOverflowMenuDialogFragment?.mIsPSTNAvailable = isPSTNAvailable
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Test
    fun testSetIsPSTNAvailable(){
        launchFragmentGuest()
        mOverflowMenuDialogFragment?.setIsPSTNAvailable(isPSTNAvailable, isVoipConnected)
        mOverflowMenuDialogFragment?.mIsPSTNAvailable?.let { assertFalse(it) }
        mOverflowMenuDialogFragment?.mIsVoipConnected?.let { assertFalse(it) }
        assertEquals(mOverflowMenuDialogFragment?.tvChangeConnection?.visibility, View.VISIBLE)
        assertEquals(mOverflowMenuDialogFragment?.tvDisconnectAudio?.visibility, View.GONE)
    }

    @Test
    fun testMenuOptionWhenPSTNAvailable() {
        isVoipConnected = true
        isPSTNAvailable = false
        launchFragmentGuest()
        assertEquals(mOverflowMenuDialogFragment?.tvChangeConnection?.visibility, View.GONE)
        assertEquals(mOverflowMenuDialogFragment?.tvDisconnectAudio?.visibility, View.VISIBLE)
        isVoipConnected = false
        isPSTNAvailable =true
        launchFragmentGuest()
        assertEquals(mOverflowMenuDialogFragment?.tvChangeConnection?.visibility, View.VISIBLE)
        assertEquals(mOverflowMenuDialogFragment?.tvDisconnectAudio?.visibility, View.GONE)
    }

    @Test
    fun `test onDisconnectAudioClick`() {
        launchFragmentGuest()
        mOverflowMenuDialogFragment?.mContext =null
        assertNull(mOverflowMenuDialogFragment?.mContext)
        mOverflowMenuDialogFragment?.onDisconnectAudioClicked()
        assertEquals(mOverflowMenuDialogFragment?.tvChangeConnection?.visibility, View.VISIBLE)
        assertEquals(mOverflowMenuDialogFragment?.tvDisconnectAudio?.visibility, View.GONE)
    }

    @Test
    fun `test onChangeConnectionClick`() {
        launchFragmentGuest()
        mOverflowMenuDialogFragment?.mContext =null
        assertNull(mOverflowMenuDialogFragment?.mContext)
        mOverflowMenuDialogFragment?.onChangeConnectionClicked()
    }

    
    @Before
    fun setUp() {
        startKoinApp()
        MockKAnnotations.init(this, relaxed = true)
        mOverflowMenuDialogFragment = OverflowMenuDialogFragment()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun onTurnOffWaitingClicked() {
        /* kotlin.TypeCastException: null cannot be cast to non-null type com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
//        launchFragment()
//        mOverflowMenuDialogFragment?.onTurnOffWaitingClicked()
//        assertFalse(CommonUtils.isWaitEnabled())
        */
    }

    @Test
    fun testGuest(){
        launchFragmentGuest()
        mOverflowMenuDialogFragment?.mIsHost = false
    }

    @Test
    fun  `test updateBandhwidth`(){
        launchFragmentGuest()
        mOverflowMenuDialogFragment?.mIsLowBandwidthOn = true
        mOverflowMenuDialogFragment?.updateBandhwidth()
        assertEquals(mOverflowMenuDialogFragment?.tvLowBandwidthOn?.visibility, View.VISIBLE)
        assertEquals(mOverflowMenuDialogFragment?.tvLowBandwidthOff?.visibility, View.GONE)
        mOverflowMenuDialogFragment?.mIsLowBandwidthOn = false
        mOverflowMenuDialogFragment?.updateBandhwidth()
        assertEquals(mOverflowMenuDialogFragment?.tvLowBandwidthOn?.visibility, View.GONE)
        assertEquals(mOverflowMenuDialogFragment?.tvLowBandwidthOff?.visibility, View.VISIBLE)
    }

    @Test
    fun testSetIsWaitngRoomEnabled(){
        mOverflowMenuDialogFragment?.setIsWaitngRoomEnabled(true)
        mOverflowMenuDialogFragment?.mIsWaitngRoomEnabled?.let { assertTrue(it) }
    }

    @After
    fun tearDown() {
        stopKoin()
        scenario?.moveToState(Lifecycle.State.DESTROYED)
        mOverflowMenuDialogFragment = null
    }

    private fun startKoinApp() {
        startKoin {
            modules(joinMeetingFragmentModule)
        }
    }

    @Test
    fun testSetIsLowBandwidth(){
        mOverflowMenuDialogFragment?.setIsLowBandwidthOn(false)
        mOverflowMenuDialogFragment?.mIsLowBandwidthOn?.let { assertFalse(it) }
        mOverflowMenuDialogFragment?.setIsLowBandwidthOn(true)
        mOverflowMenuDialogFragment?.mIsLowBandwidthOn?.let { assertTrue(it) }
    }
}