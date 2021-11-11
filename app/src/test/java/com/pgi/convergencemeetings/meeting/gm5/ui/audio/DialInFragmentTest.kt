package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoDataSet
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.powermock.core.classloader.annotations.PrepareForTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
@PrepareForTest(ClientInfoDaoUtils::class)
class DialInFragmentTest {

    lateinit var fragment: DialInFragment
    val mDialInCallback = mockk<DialInFragmentContractor.DialInActivityContract>(relaxed = true)
    val activity = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
    val mClientInfoDataSet = mockk<ClientInfoDataSet>(relaxed = true)
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel
    private fun launchFragment(onInstantiated: (DialInFragment) -> Unit = {}):
            FragmentScenario<DialInFragment> {
        return FragmentScenario.launchInContainer(DialInFragment::class.java,
                null, R.style.AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as DialInFragment
                this@DialInFragmentTest.fragment = fragment
                fragment.mDialInCallback = mDialInCallback
                onInstantiated(fragment)
                return fragment
            }
        })

    }

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        startKoin { }
        CoreApplication.mLogger = TestLogger()
    }

    @After
    fun `tear down`() {
        stopKoin()
    }

    @Test
    fun `test onCreate`() {
        launchFragment {
            Assert.assertNotNull(fragment)
            Assert.assertNull(fragment.context)
        }
        Assert.assertNotNull(fragment.context)
        fragment.mIsMeetingHost = true
        fragment.onCreate(null)
    }

    @Test
    fun `test initConstructor`() {
        val mDialInCallbacknew = mockk<DialInFragmentContractor.DialInActivityContract>(relaxed = true)
        fragment = DialInFragment(true, mDialInCallbacknew, true, "text", "97907000007")
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `test onBackButtonClicked`() {
        launchFragment ()
        fragment.onBackButtonClicked()
        Assert.assertNotNull(fragment.activity?.supportFragmentManager)
    }

    @Test
    fun `test onMoveToDialInListClicked`() {
        launchFragment ()
        fragment.moveToDialInListClicked()
        Assert.assertNull(fragment.mAudioSelectionCallbacks)
        fragment.mAudioSelectionCallbacks = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
        fragment.moveToDialInListClicked()
        Assert.assertNotNull(fragment.mAudioSelectionCallbacks)
    }

    @Test
    fun `test onDialInButtonClicked`() {
        launchFragment()
        fragment.mSelectedPhoneNumber = null
        fragment.onDialInButtonClicked()
        Assert.assertNull(fragment.mSelectedPhoneNumber)
        fragment.mSelectedPhoneNumber = "9889898898"
        fragment.onDialInButtonClicked()
        Assert.assertEquals(fragment.mSelectedPhoneNumber,"9889898898")
    }

    @Test
    fun `test onDoNotConnectAudioClicked`() {
        launchFragment ()
        fragment.onDoNotConnectAudioClicked()
        fragment.mAudioSelectionCallbacks = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
        fragment.onDoNotConnectAudioClicked()
    }

    @Test
    fun `test update UI ` () {
        launchFragment()
        launchFragment()
        fragment.mobile = "9876543210"
        fragment.mIsUseHtml5 = true
        fragment.updateUI()
        Assert.assertTrue(fragment.mIsUseHtml5)
        every { mockMeetingUserViewModel.getRoomInfoResponse()?.audio?.primaryAccessNumber } returns "90883773737"
        Assert.assertEquals(fragment.mobile,"9876543210")
        fragment.mobile = ""
        fragment.mIsUseHtml5 = false
        fragment.updateUI()
        Assert.assertFalse(fragment.mIsUseHtml5)
        fragment.mClientInfoDataSet = null
        fragment.updateUI()
        fragment.mClientInfoDataSet = mClientInfoDataSet
        fragment.updateUI()
        every { mClientInfoDataSet.primaryAccessNumber } returns "90883773737"
        Assert.assertEquals(fragment.mobile,"")
        fragment.updateUI()
    }
}