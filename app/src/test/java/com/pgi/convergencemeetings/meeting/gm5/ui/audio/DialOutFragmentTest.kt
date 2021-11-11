package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.ui.security.MeetingSecurityFragment
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.spyk
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class DialOutFragmentTest : RobolectricTest(), KoinTest {


    lateinit var fragment: DialOutFragment

    private fun launchFragment(onInstantiated: (DialOutFragment) -> Unit = {}):
            FragmentScenario<DialOutFragment> {
        return FragmentScenario.launchInContainer(DialOutFragment::class.java,
                null, R.style.AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as DialOutFragment
                this@DialOutFragmentTest.fragment = fragment
                fragment.mMeetingHostName = "Test"
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        CoreApplication.mLogger = TestLogger()
        startKoin { }
        val phone = Phone(1L, "IN", "9876543210", true, "mobile", "description")
        launchFragment()
        initFragment(phone)
    }

    private fun initFragment(phone: Phone?) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val phone1 = Phone(1L, "IN", "9876543210", true, "mobile", "description")
        val phone2 = Phone(1L, "IN", "9876543456", true, "mobile", "description")
        val phoneList = mutableListOf<Phone>(phone1)
        phoneList.add(phone2)
        val spyDialOutFragmentContrator = spyk<DialOutFragmentContrator.activity>()
        fragment.initDialoutFragment(context, isMeetingHost = true, mHostName = "test", phoneList = phoneList, selectedPhoneNumber = phone
                , callback = spyDialOutFragmentContrator, isUseHtlm5 = true)
    }

    @Test
    fun `test initData`() {
        val phone = Phone(1L, "IN", "987654565610", true, "mobile", "description")
        fragment.mSelectedPhoneNumber = phone
        fragment.initData()
    }

    @Test
    fun `test initData when countryCode with plus`() {
        val phone1 = Phone(1L, "+91", "9876543210", true, "mobile", "description")
        fragment.mSelectedPhoneNumber = phone1
        fragment.initData()
    }

    @Test
    fun `test initData when countryCode without plus`() {
        val phone3 = Phone(1L, "91", "9876543210", true, "mobile", "description")
        fragment.mSelectedPhoneNumber = phone3
        fragment.initData()
    }

    @Test
    fun `test initData when mSelectedPhoneNumber null`() {
        val phone4 = null
        fragment.mSelectedPhoneNumber = phone4
        fragment.initData()
    }

    @Test
    fun `test onBackButtonClicked`() {
        fragment.onBackButtonClicked()
    }

    @Test
    fun `test onBackButtonClicked when mDialOutCallback is null`() {
        fragment.mDialOutCallback = null
        fragment.onBackButtonClicked()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test onDialOutButtonClicked`() {
        fragment.onDialOutButtonClicked()
    }

    @Test
    fun `test onDialOutButtonClicked when selectedPhone no is null`() {
        fragment.mSelectedPhoneNumber = null
        fragment.onDialOutButtonClicked()
    }

    @Test
    fun `test onDialOutButtonClicked when mDialOutCallback is null`() {
        fragment.mDialOutCallback = null
        fragment.onDialOutButtonClicked()
    }

    @Test
    fun `test onPhoneNumberSelected`() {
        val phone = Phone(1L, "IN", "9876543210", true, "mobile", "description")
        fragment.onPhoneNumberSelected(phone = phone)
        Assert.assertEquals(fragment.mSelectedPhoneNumber, phone)
    }

    @Test
    fun `test onSelectedPhoneNumberClicked`() {
        val spyDialOutFragmentContrator = spyk<DialOutFragmentContrator.activity>()
        fragment.mDialOutCallback = spyDialOutFragmentContrator
        fragment.onSelectedPhoneNumberClicked()
    }

    @Test
    fun `test onSelectedPhoneNumberClicked when callback is null`() {
        fragment.mDialOutCallback = null
        fragment.onSelectedPhoneNumberClicked()
    }

    @Test
    fun `test onDoNotConnectAudioClicked`() {
        val spyAudioSelectionFragmentContractor = spyk<AudioSelectionFragmentContractor.activity>()
        fragment.mAudioSelectionCallbacks = spyAudioSelectionFragmentContractor
        fragment.onDoNotConnectAudioClicked()
    }

    @Test
    fun `test onDoNotConnectAudioClicked when callback is null`() {
        fragment.mAudioSelectionCallbacks = null
        fragment.onDoNotConnectAudioClicked()
    }

    @Test
    fun `test newInstance`(){
        launchFragment()
        Assert.assertNotNull(DialOutFragment.newInstance())
    }
}