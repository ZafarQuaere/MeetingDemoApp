package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.search.di.joinMeetingFragmentModule
import com.pgi.convergencemeetings.utils.AppAuthUtils
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.UnstableDefault
import org.junit.*
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

class FeedbackCommentFragmentTest : RobolectricTest(), KoinTest {
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private var viewFragment: FeedbackCommentFragment? = null
    private var scenario: FragmentScenario<FeedbackCommentFragment>? = null
    private var feedbackCommentFragment: FeedbackCommentFragment? = null

    private fun launchFragment(onInstantiated: (FeedbackCommentFragment) -> Unit = {}):
            FragmentScenario<FeedbackCommentFragment> {
        return FragmentScenario.launchInContainer(FeedbackCommentFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as FeedbackCommentFragment
                this@FeedbackCommentFragmentTest.viewFragment = fragment
                this@FeedbackCommentFragmentTest.feedbackCommentFragment = fragment
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        startKoinApp()
        MockKAnnotations.init(this, relaxed = true)
        mockkStatic(AppAuthUtils::class)
        val appAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
        every {
            AppAuthUtils.getInstance()
        } returns appAuthUtils
        every { appAuthUtils.emailId } returns "abhishek.singh@pgi.com"
        every { appAuthUtils.firstName } returns "Abhishek"
        every { appAuthUtils.lastName } returns "Singh"
        feedbackCommentFragment = FeedbackCommentFragment()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        stopKoin()
        scenario?.moveToState(Lifecycle.State.DESTROYED)
        feedbackCommentFragment = null
    }

    private fun startKoinApp() {
        startKoin {
            modules(joinMeetingFragmentModule)
        }
    }

    @Test
    fun `test onFeedbackCommentBackBtn`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        feedbackCommentFragment?.onFeedbackCommentBackBtn()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with feeback text`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        Assert.assertEquals("Negative", CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating)
        Assert.assertEquals("I am facing some audio issue .", feedbackCommentFragment?.commentEditText?.text.toString())
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertEquals("I am facing some audio issue .", CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.issueDescription)
    }

    @Test
    fun `test onSendFeedbackComment Negative with blank text`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText(" ")
        Assert.assertEquals("Negative", CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating)
        Assert.assertEquals(" ", feedbackCommentFragment?.commentEditText?.text.toString())
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertEquals("", CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.issueDescription)
    }

    @Test
    fun `test EOMFeedbackComment Cancel`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        feedbackCommentFragment?.onCancelClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with Guest User`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        mockkStatic(AppAuthUtils::class)
        val appAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
        every {
            AppAuthUtils.getInstance()
        } returns appAuthUtils
        every { appAuthUtils.emailId } returns "abc@hcl.com"
        every { appAuthUtils.firstName } returns "Abhishek"
        every { appAuthUtils.lastName } returns "Singh"
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with Room role Host`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        mockkStatic(AppAuthUtils::class)
        val appAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
        every {
            AppAuthUtils.getInstance()
        } returns appAuthUtils
        every { appAuthUtils.emailId } returns "abc@hcl.com"
        every { appAuthUtils.firstName } returns "Abhishek"
        every { appAuthUtils.lastName } returns "Singh"
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        CoreApplication.mLogger.attendeeModel.roomRole = "Host"
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with web-na region`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        CoreApplication.mLogger.attendeeModel.meetingServer = "web-na"
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with EMEA region`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        CoreApplication.mLogger.attendeeModel.meetingServer = "web-emea"
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with APAC region`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        CoreApplication.mLogger.attendeeModel.meetingServer = "APAC"
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with email max length`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        mockkStatic(AppAuthUtils::class)
        val appAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
        every {
            AppAuthUtils.getInstance()
        } returns appAuthUtils
        every { appAuthUtils.emailId } returns "abbbbbbbbbbbbbbccccccddddddddddddeeeeeeeeeffffffffffggggggghhhhhhiiiiiiikk@gmail.com"
        every { appAuthUtils.firstName } returns "Abhishek"
        every { appAuthUtils.lastName } returns "Singh"
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = "15rfhiir3tb3xbjed5z7x8eai"
        CoreApplication.mLogger.mixPanelEndOfMeetingFeedback.rating = "Negative"
        feedbackCommentFragment?.commentEditText?.setText("I am facing some audio issue .")
        CoreApplication.mLogger.attendeeModel.meetingServer = "APAC"
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
    }

    @Test
    fun `test onSendFeedbackComment Negative with NullOrEmpty uniqueMeetingId`() {
        CoreApplication.mLogger = TestLogger()
        launchFragment()
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = ""
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNotNull(feedbackCommentFragment)
        CoreApplication.mLogger.meetingModel.uniqueMeetingId = null
        feedbackCommentFragment?.onSendFeedbackCommentClick()
        Assert.assertNull(CoreApplication.mLogger.meetingModel.uniqueMeetingId)
    }
}