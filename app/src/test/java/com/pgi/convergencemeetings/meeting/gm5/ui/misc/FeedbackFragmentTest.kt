package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.search.di.joinMeetingFragmentModule
import com.pgi.convergencemeetings.utils.ConferenceManager
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.UnstableDefault
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@UnstableDefault
@ObsoleteCoroutinesApi
@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class FeedbackFragmentTest : RobolectricTest(), KoinTest {

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private var viewFragment: FeedbackFragment? = null
    private var scenario: FragmentScenario<FeedbackFragment>? = null
    private var feedbackFragment: FeedbackFragment? = null

    private fun launchFragment(onInstantiated: (FeedbackFragment) -> Unit = {}):
            FragmentScenario<FeedbackFragment> {
        return FragmentScenario.launchInContainer(FeedbackFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as FeedbackFragment
                this@FeedbackFragmentTest.viewFragment = fragment
                this@FeedbackFragmentTest.feedbackFragment = fragment
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        startKoinApp()
        MockKAnnotations.init(this, relaxed = true)
        feedbackFragment = FeedbackFragment()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        stopKoin()
        scenario?.moveToState(Lifecycle.State.DESTROYED)
        feedbackFragment = null
    }

    private fun startKoinApp() {
        startKoin {
            modules(joinMeetingFragmentModule)
        }
    }

    @Test
    fun `test moveToLastFragment`() {
        launchFragment()
        ConferenceManager.setIsMeetinActive(true)
        feedbackFragment?.moveToLastFragment()
        Assert.assertEquals(feedbackFragment?.btnPositiveFeedback?.text.toString(), "It was great!")
        ConferenceManager.setIsMeetinActive(false)
        feedbackFragment?.moveToLastFragment()
        val firstTimeUserFlow = SharedPreferencesManager.getInstance().isFirstTimeGuestUserForThankYou
        Assert.assertNotNull(firstTimeUserFlow)
        Assert.assertEquals(feedbackFragment?.btnNegativeFeedback?.visibility, View.INVISIBLE)
        Assert.assertEquals(feedbackFragment?.btnPositiveFeedback?.visibility, View.INVISIBLE)
        Assert.assertEquals(feedbackFragment?.textSkip?.visibility, View.INVISIBLE)
        Assert.assertNotNull(firstTimeUserFlow)
        SharedPreferencesManager.getInstance().firstTimeGuestUserForThankYou(true)
        feedbackFragment?.moveToLastFragment()
        Assert.assertEquals(false,SharedPreferencesManager.getInstance().isFirstTimeGuestUserForThankYou)
    }
    
    @Test
    fun `test click Action`() {
        launchFragment()
        feedbackFragment?.mPositiveFeedClicked = false
        feedbackFragment?.takeClickAction()
        Assert.assertEquals(false,feedbackFragment?.mNegativeFeedClicked)
        feedbackFragment?.mPositiveFeedClicked = true
        feedbackFragment?.takeClickAction()
        Assert.assertEquals(true,feedbackFragment?.mPositiveFeedClicked)
    }

    @Test
    fun `test EndOfMeetingFeedback Positive`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        feedbackFragment?.sendFeedbackEventLog("Positive")
        Assert.assertEquals("Positive", CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating)
    }

    @Test
    fun `test EndOfMeetingFeedback Negative`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        feedbackFragment?.onNegativeFeedClick()
        Assert.assertNotNull(feedbackFragment)
    }

    @Test
    fun `test EndOfMeetingFeedback Skip`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        feedbackFragment?.onSkipClick()
        Assert.assertNotNull(feedbackFragment)
    }
}