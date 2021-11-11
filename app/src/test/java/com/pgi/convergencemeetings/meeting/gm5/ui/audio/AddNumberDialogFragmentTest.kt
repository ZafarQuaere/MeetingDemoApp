package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.content.ContentPresentationFragment
import io.mockk.*
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class AddNumberDialogFragmentTest {

  lateinit var fragment: AddNumberDialogFragment

  private fun launchFragment(onInstantiated: (AddNumberDialogFragment) -> Unit = {}):
      FragmentScenario<AddNumberDialogFragment> {
    return FragmentScenario.launchInContainer(AddNumberDialogFragment::class.java,
        null, R.style.AppTheme, object : FragmentFactory() {
      override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragment = super.instantiate(classLoader, className) as AddNumberDialogFragment
        this@AddNumberDialogFragmentTest.fragment = fragment
        onInstantiated(fragment)
        return fragment
      }
    })
  }

  @Before
  fun `set up`() {
    MockKAnnotations.init(this, relaxed = true)
    CoreApplication.mLogger = TestLogger()
  }

  @Test
  fun `test initConstructor`() {
    launchFragment()
    val activity = mockk<BaseMeetingActivity>(relaxed = true)
    val addNumberDialogListener = mockk<AddNumberDialogFragment.AddNumberDialogListener>(relaxed = true)
    fragment.initAddNumberDialog(activity,addNumberDialogListener )
  }

  @Test
  fun `test onAddButtonClicked`() {
    launchFragment()
    val activity = mockk<BaseMeetingActivity>(relaxed = true)
    fragment.mActivity = activity
    fragment.etCountryCode.setText("IN")
    fragment.etPhoneNumber.setText("9784847775775")
    assertEquals( fragment.etCountryCode.text.toString(),"IN")
    assertEquals( fragment.etPhoneNumber.text.toString(),"9784847775775")
    fragment.onAddButtonClicked()
    fragment.mActivity = activity
    fragment.mAddNumberDialogListener = null
    assertNull(fragment.mAddNumberDialogListener)
    fragment.etCountryCode.setText("IN")
    fragment.etPhoneNumber.setText("9784847775775")
    assertEquals( fragment.etCountryCode.text.toString(),"IN")
    assertEquals( fragment.etPhoneNumber.text.toString(),"9784847775775")
    fragment.onAddButtonClicked()

  }

  @Test
  fun `test onAddButtonClicked when countryCode is empty`() {
    launchFragment()
    val activity = mockk<BaseMeetingActivity>(relaxed = true)
    fragment.mActivity = activity
    fragment.etCountryCode.setText("")
    fragment.etPhoneNumber.setText("9784847775775")
    fragment.onAddButtonClicked()
  }

  @Test
  fun `test onAddButtonClicked when phoneNumber is empty`() {
    launchFragment()
    val activity = mockk<BaseMeetingActivity>(relaxed = true)
    fragment.mActivity = activity
    fragment.etCountryCode.setText("IN")
    fragment.etPhoneNumber.setText("")
    fragment.onAddButtonClicked()
  }

  @Test
  fun `test onAddButtonClicked when phoneNumber is long`() {
    launchFragment()
    val activity = mockk<BaseMeetingActivity>(relaxed = true)
    fragment.mActivity = activity
    fragment.etCountryCode.setText("+91111")
    fragment.etPhoneNumber.setText("989897889798")
    fragment.onAddButtonClicked()
  }

  @Test
  fun `test validatePhoneNumber`() {
    launchFragment()
    val phoneNumber = "1234567"
    fragment.validatePhoneNumber(phoneNumber)
    assertFalse(fragment.isValidPhNum)
    fragment.mActivity = null
    assertNull( fragment.mActivity)
    fragment.validatePhoneNumber(phoneNumber)
    assertEquals( fragment.isValidPhNum,false)
  }

  @Test
  fun `test validatePhoneNumber when phoneNumber is short`() {
    launchFragment()
    val phoneNumber2 = "12345"
    fragment.validatePhoneNumber(phoneNumber2)
  }

  @Test
  fun `test validateCountryCode`() {
    launchFragment()
    val countryCode = "+91"
    fragment.validateCountryCode(countryCode)
    val countryCode2 = "+91111"
    fragment.validateCountryCode(countryCode2)
  }

  @Test
  fun `test validateCountryCode when countryCode is long`() {
    launchFragment()
    val countryCode2 = "+91111"
    fragment.validateCountryCode(countryCode2)
  }

  @Test
  fun `test onCancelClicked`() {
    launchFragment()
    fragment.onCancelClicked()
  }

  @Test
  fun `test onCreate`() {
    launchFragment {
      assertNotNull(fragment)
      assertNull(fragment.context)
    }
    assertNotNull(fragment.context)
  }
}