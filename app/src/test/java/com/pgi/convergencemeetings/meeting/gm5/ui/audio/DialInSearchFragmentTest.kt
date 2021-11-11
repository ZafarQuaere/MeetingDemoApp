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
class DialInSearchFragmentTest : KoinTest {

    lateinit var fragment: DialInSearchFragment
    lateinit var clientInfoDaoUtils: ClientInfoDaoUtils
    var dialInNumberAdapter = mockk<DialInNumberAdapter>(relaxed = true)
    val activity = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)
    val clientInfoDataSet =mockk<ClientInfoDataSet>(relaxed = true)
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel

    private fun launchFragment(onInstantiated: (DialInSearchFragment) -> Unit = {}):
            FragmentScenario<DialInSearchFragment> {
        return FragmentScenario.launchInContainer(DialInSearchFragment::class.java,
                null, R.style.AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as DialInSearchFragment
                this@DialInSearchFragmentTest.fragment = fragment
                fragment.mIsMeetingHost = true
                fragment.mIsUseHtml5 = true
                fragment.maccessNumbers = getAccessNumberList()
                fragment.mdescriptionLocation = "Noida"
                fragment.meetingUserViewModel = mockk<MeetingUserViewModel>(relaxed = true)
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        CoreApplication.mLogger = TestLogger()
        clientInfoDaoUtils = mockk<ClientInfoDaoUtils>(relaxed = true)
        Assert.assertNotNull(clientInfoDaoUtils)
        val phoneInformation = PhoneInformation(127653L, "1234566", "Karol Bagh", "7834908329", "Mobile", "New Delhi")
        startKoin { }
        clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
        phoneInformation.phoneNumber = "1-719-359-9722"
        phoneInformation.phoneType = "TOLL"
        val phoneInformationList = ArrayList<PhoneInformation>()
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
        fragment = DialInSearchFragment(true, mDialInCallbacknew, true)
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `test onBackButtonClicked`() {
        launchFragment()
        fragment.onBackButtonClicked()
        Assert.assertNotNull(fragment.context)
    }

    @Test
    fun `test onPhoneNumberSelected`() {
        launchFragment()
        fragment.onPhoneNumberSelected("Aus")
        fragment.mAudioSelectionCallbacks = null
        fragment.onPhoneNumberSelected("Aus")
        Assert.assertNotNull(fragment.context)
    }

    @Test
    fun `test onsearch_close_btn`() {
        launchFragment ()
        fragment.dialInNumberAdapter = dialInNumberAdapter
        fragment.onSearchClose()
        Assert.assertNotNull(fragment.dialInNumberAdapter)
        fragment.dialInNumberAdapter = null
        fragment.onSearchClose()
        Assert.assertNull(fragment.dialInNumberAdapter)
    }

    @Test
    fun `test filter`() {
        launchFragment ()
        fragment.filter("")
        fragment.mphoneNumbersList = getPhoneNumberList()
        fragment.dialInNumberAdapter = dialInNumberAdapter
        fragment.filter("Aus")
        fragment.dialInNumberAdapter = null
        fragment.filter("9876543210")
    }

    private fun getPhoneNumberList(): MutableList<DialInNumbers> {
        val numbers = emptyList<DialInNumbers>().toMutableList()
        numbers.add(object :DialInNumbers{
            override fun getPhoneNumber(): String { return "9876543210" }
            override fun getLocation(): String { return "Aus" }
            override fun getPhoneType(): String { return "Mobile" }
        })
        return numbers
    }

    @Test
    fun `test isListEmpty` () {
        launchFragment()
        fragment.mphoneNumbersList = mockk<MutableList<DialInNumbers>>(relaxed = true)
        fragment.mphoneNumbersList.add(0, mockk<DialInNumbers>())
        fragment.setListIfEmpty()
    }

    @Test
    fun `test setUp Number list`() {
        launchFragment()
        fragment.mIsUseHtml5 = true
        Assert.assertTrue(  fragment.mIsUseHtml5)
        fragment.maccessNumbers = getAccessNumberList()
        fragment.meetingUserViewModel = mockMeetingUserViewModel
        fragment.setUpNumberList()
        Assert.assertTrue(  fragment.mIsUseHtml5)
        every { clientInfoDataSet.phoneNumbersList }returns getPhoneInformation()
        fragment.meetingUserViewModel = mockMeetingUserViewModel
        fragment.setUpNumberList()
        fragment.mIsUseHtml5 = false
        Assert.assertFalse( fragment.mIsUseHtml5)
        every { mockMeetingUserViewModel.getDialInNumbers() } returns getEmpityAccessNumberList()
        fragment.meetingUserViewModel = mockMeetingUserViewModel
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.setUpNumberList()
        fragment.mIsUseHtml5 = false
        fragment.mClientInfoDataSet = null
        every {clientInfoDataSet.phoneNumbersList } returns null
        fragment.setUpNumberList()
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.mClientInfoDataSet = null
        every {clientInfoDataSet.phoneNumbersList } returns getPhoneEmpityInformation()
        fragment.setUpNumberList()
        Assert.assertFalse( fragment.mIsUseHtml5)
        fragment.mClientInfoDataSet = clientInfoDataSet
        every {clientInfoDataSet.phoneNumbersList } returns getPhoneInformation()
        fragment.setUpNumberList()
    }

    private fun getEmpityAccessNumberList(): List<AccessNumber> {
        val numbers = emptyList<AccessNumber>().toMutableList()
        return numbers
    }

    private fun getAccessNumberList(): List<AccessNumber> {
        val numbers = emptyList<AccessNumber>().toMutableList()
        numbers.add( AccessNumber("9876543210","mobile","description"))
        numbers.add( AccessNumber("9876543211","mobile1","description1"))
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