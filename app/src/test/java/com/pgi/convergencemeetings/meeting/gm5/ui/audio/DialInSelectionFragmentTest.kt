package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.utils.DialInNumbers
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.models.PhoneInformation
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoDataSet
import com.pgi.network.models.AccessNumber
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
import org.koin.test.KoinTest
import org.powermock.core.classloader.annotations.PrepareForTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
@PrepareForTest(ClientInfoDaoUtils::class)
class DialInSelectionFragmentTest : KoinTest {

    lateinit var fragment: DialInSelectionFragment
    lateinit var clientInfoDaoUtils: ClientInfoDaoUtils
    val activity = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel
    @MockK
    val clientInfoDataSet =mockk<ClientInfoDataSet>(relaxed = true)

    private fun launchFragment(onInstantiated: (DialInSelectionFragment) -> Unit = {}):
            FragmentScenario<DialInSelectionFragment> {
        return FragmentScenario.launchInContainer(DialInSelectionFragment::class.java,
                null, R.style.AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as DialInSelectionFragment
                this@DialInSelectionFragmentTest.fragment = fragment
                fragment.mIsMeetingHost = true
                fragment.mIsUseHtml5 = true
                fragment.accessNumbers = getAccessNumberList()
                fragment.descriptionLocation = "Noida"
                fragment.mMeetingUserViewModel = mockk<MeetingUserViewModel>(relaxed = true)
                onInstantiated(fragment)
                return fragment
            }
        })

    }

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        CoreApplication.mLogger = TestLogger()
        clientInfoDaoUtils= mockk<ClientInfoDaoUtils>(relaxed = true)
        Assert.assertNotNull(clientInfoDaoUtils)
        val phoneInformation= PhoneInformation(127653L,"1234566","Karol Bagh","7834908329","Mobile","New Delhi")
        startKoin { }
        clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
        phoneInformation.phoneNumber = "1-719-359-9722"
        phoneInformation.phoneType = "TOLL"
        val phoneInformationList =ArrayList<PhoneInformation>()
        phoneInformationList.add(phoneInformation)
        clientInfoDaoUtils.prepareDialInList("1-719-359-9722", phoneInformationList)
        Assert.assertEquals(phoneInformationList.get(0).getPhoneNumber(), "1-719-359-9722")
        Assert.assertEquals(phoneInformationList.get(0).compareTo(phoneInformationList.get(0)).toLong(), -1)

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
            Assert.assertNull(fragment.mAudioSelectionCallbacks)
            Assert.assertNull(fragment.mDialInCallback)
        }
        Assert.assertNotNull(fragment.context)
        fragment.mIsMeetingHost = false
        fragment.onCreate(null)
    }

    @Test
    fun `test initConstructor`() {
        val mDialInCallbacknew = mockk<DialInFragmentContractor.DialInActivityContract>(relaxed = true)
        fragment = DialInSelectionFragment(true, mDialInCallbacknew, true)
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `test setUp Number list`() {
        launchFragment()
        fragment.mIsUseHtml5 = true
        Assert.assertTrue(  fragment.mIsUseHtml5)
         fragment.accessNumbers = getAccessNumberList()
        fragment.mMeetingUserViewModel = mockMeetingUserViewModel
        fragment.setupPhoneNumberList()
        Assert.assertTrue(  fragment.mIsUseHtml5)
        every { clientInfoDataSet.phoneNumbersList }returns getPhoneInformation()
        fragment.mMeetingUserViewModel = mockMeetingUserViewModel
        fragment.setupPhoneNumberList()
        fragment.mIsUseHtml5 = false
        Assert.assertFalse( fragment.mIsUseHtml5)
        every { mockMeetingUserViewModel.getDialInNumbers() } returns getEmpityAccessNumberList()
        fragment.mMeetingUserViewModel = mockMeetingUserViewModel
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.setupPhoneNumberList()
        fragment.mIsUseHtml5 = false
        fragment.mClientInfoDataSet = null
        every {clientInfoDataSet.phoneNumbersList } returns null
        fragment.setupPhoneNumberList()
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.mClientInfoDataSet = null
        every {clientInfoDataSet.phoneNumbersList } returns getPhoneEmpityInformation()
        fragment.setupPhoneNumberList()
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.mClientInfoDataSet = clientInfoDataSet
        every {clientInfoDataSet.phoneNumbersList } returns getPhoneInformation()
        fragment.setupPhoneNumberList()
    }

    @Test
    fun `test onBackButtonClicked`() {
        launchFragment ()
        fragment.onBackButtonClicked()
        Assert.assertNotNull(fragment.activity?.supportFragmentManager)
    }

    @Test
    fun `test onPhoneNumberSelected`() {
        launchFragment()
        fragment.onPhoneNumberSelected("Aus")
        Assert.assertNull(fragment.mAudioSelectionCallbacks)
        fragment.mAudioSelectionCallbacks = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
        fragment.onPhoneNumberSelected("Aus")
        Assert.assertNotNull(fragment.mAudioSelectionCallbacks)
    }

    @Test
    fun `test onSearch Clicked` () {
        launchFragment()
        fragment.onSearchClicked()
        Assert.assertNull(fragment.mAudioSelectionCallbacks)
        fragment.mAudioSelectionCallbacks = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
        fragment.onSearchClicked()
        Assert.assertNotNull(fragment.mAudioSelectionCallbacks)
    }

    @Test
    fun `test isListEmpty` () {
        launchFragment()
        fragment.phoneNumbersList = mockk<MutableList<DialInNumbers>>(relaxed = true)
        fragment.phoneNumbersList.add(0, mockk<DialInNumbers>())
        fragment.setListIfEmpity()
    }

    private fun getAccessNumberList(): List<AccessNumber> {
        val numbers = emptyList<AccessNumber>().toMutableList()
        numbers.add( AccessNumber("9876543210","mobile","description"))
        numbers.add( AccessNumber("9876543211","mobile1","description1"))
        return numbers
    }

    private fun getEmpityAccessNumberList(): List<AccessNumber> {
        val numbers = emptyList<AccessNumber>().toMutableList()
        return numbers
    }

    private fun getPhoneInformation(): List<PhoneInformation> {
        val numbers = emptyList<PhoneInformation>().toMutableList()
        numbers.add( PhoneInformation(1L,"9876543210","mobile","description","mobile1","description1"))
        numbers.add(PhoneInformation(2L,"9876543210","mobile","description","mobile1","description1"))
        return numbers
    }

    private fun getPhoneEmpityInformation(): List<PhoneInformation> {
        val numbers = emptyList<PhoneInformation>().toMutableList()
        return numbers
    }


}