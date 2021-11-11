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
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import io.mockk.MockKAnnotations
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
class DialOutSelectionFragmentTest : KoinTest {

    lateinit var fragment: DialOutSelectionFragment
    var activity = mockk<AudioSelectionFragmentContractor.activity>(relaxed = true)

    private fun launchFragment(onInstantiated: (DialOutSelectionFragment) -> Unit = {}):
            FragmentScenario<DialOutSelectionFragment> {
        return FragmentScenario.launchInContainer(DialOutSelectionFragment::class.java,
                null, R.style.AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as DialOutSelectionFragment
                this@DialOutSelectionFragmentTest.fragment = fragment
                fragment.mIsMeetingHost = true
                fragment.mIsUseHtml5 = true
                fragment.mContext = CoreApplication.appContext
                fragment.addPhoneNumber = false
                var phone=Phone(1L, "IN", "9876543210", true, "mobile", "description")
                fragment.mSelectedPhoneNumber = phone
                fragment.mPhoneNumberList=getPhoneNumberArrayList()
                Assert.assertFalse(fragment.mPhoneNumberList.isEmpty())
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
            Assert.assertNull(fragment.mDialOutCallback)
        }
        Assert.assertNotNull(fragment.context)
        fragment.mIsMeetingHost = false
        fragment.onCreate(null)
    }

    @Test
    fun `test initConstructor`() {
        launchFragment()
        val mDialInCallbacknew = mockk<DialOutFragmentContrator.activity>(relaxed = true)
        fragment.initDialoutFragment(CoreApplication.appContext, true, getPhoneNumberList(), mDialInCallbacknew, true)
        Assert.assertNotNull(fragment)
        fragment.initDialoutFragment(CoreApplication.appContext, true, emptyList(), mDialInCallbacknew, true)
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `test onBackButtonClicked`() {
        launchFragment()
        val mDialoutCallbacknew = mockk<DialOutFragmentContrator.activity>(relaxed = true)
        fragment.mDialOutCallback = mDialoutCallbacknew
        Assert.assertNotNull(fragment.activity?.supportFragmentManager)
        fragment.onBackButtonClicked()
        fragment.mDialOutCallback = null
        fragment.onBackButtonClicked()
        Assert.assertNotNull(fragment.mSelectedPhoneNumber)
        fragment.onBackButtonClicked()
        fragment.mSelectedPhoneNumber = null
        fragment.onBackButtonClicked()
        fragment.mSelectedPhoneNumber = null
        fragment.mDialOutCallback = null
        fragment.onBackButtonClicked()

    }

    @Test
    fun `test onAddNumberClicked`() {
        launchFragment()
        fragment.onAddNumberClicked()
    }

    @Test
    fun `test onPhoneNumberSelected`() {
        launchFragment()
        var phone = Phone(1L, "IN", "9876543210", true, "mobile", "description")
        fragment.mSelectedPhoneNumber = phone
        fragment.onPhoneNumberSelected(phone)
        fragment.mSelectedPhoneNumber = null
        fragment.onPhoneNumberSelected(null)
    }

    @Test
    fun `test showAddNumberDialog`() {
        launchFragment()
        fragment.showAddNumberDialog()
    }

    @Test
    fun `test onDialOutButtonClicked`() {
        launchFragment()
        fragment.onDialOutButtonClicked()
        fragment.mPhoneNumberList = arrayListOf()
        var phone = Phone(1L, "IN", "9876543210", true, "mobile", "description")
        fragment.mPhoneNumberList = getPhoneNumberArrayList()
        fragment.mSelectedPhoneNumber = phone
        val mDialInCallbacknew = mockk<DialOutFragmentContrator.activity>(relaxed = true)
        fragment.mDialOutCallback = mDialInCallbacknew
        fragment.onDialOutButtonClicked()
        fragment.mPhoneNumberList = getPhoneNumberArrayList()
        fragment.mSelectedPhoneNumber = null
        fragment.onDialOutButtonClicked()
        fragment.mPhoneNumberList = getPhoneNumberEmpityArrayList()
        fragment.onDialOutButtonClicked()
        fragment.mDialOutCallback = null
        fragment.onDialOutButtonClicked()
    }

    @Test
    fun `test onNumberAdded`() {
        launchFragment()
        fragment.onNumberAdded("IN", "98962655527")
        Assert.assertTrue(fragment.addPhoneNumber)
        Assert.assertNotNull( fragment.mDialInNumberAdapter)
        fragment.mDialInNumberAdapter=null
        fragment.onNumberAdded("IN", "98962655527")
        Assert.assertFalse(fragment.addPhoneNumber)
        fragment.onNumberAdded("IN", "98962655527")
    }

    @Test
    fun `test isListEmpty`() {
        launchFragment()
        fragment.mPhoneNumberList = mockk<ArrayList<Phone>>(relaxed = true)
        fragment.mPhoneNumberList.add(0, mockk<Phone>())
        fragment.setListIfEmpty()
        fragment.mPhoneNumberList = getPhoneNumberArrayList()
        Assert.assertFalse(fragment.mPhoneNumberList.isEmpty())
        fragment.setListIfEmpty()
        fragment.mPhoneNumberList = getPhoneNumberEmpityArrayList()
        Assert.assertTrue(fragment.mPhoneNumberList.isEmpty())
        fragment.setListIfEmpty()
    }

    private fun getPhoneNumberList(): List<Phone> {
        val numbers = emptyList<Phone>().toMutableList()
        numbers.add(Phone(1L, "IN", "9876543210", true, "mobile", "description"))
        numbers.add(Phone(2L, "IN", "9876543210", true, "mobile", "description"))
        return numbers
    }

    private fun getPhoneNumberArrayList(): ArrayList<Phone> {
        val numbers = ArrayList<Phone>()
        numbers.add(Phone(1L, "IN", "9876543210", true, "mobile", "description"))
        numbers.add(Phone(2L, "IN", "9876543210", true, "mobile", "description"))
        return numbers
    }

    private fun getPhoneNumberEmpityArrayList(): ArrayList<Phone> {
        val numbers = ArrayList<Phone>()
        return numbers
    }
}
