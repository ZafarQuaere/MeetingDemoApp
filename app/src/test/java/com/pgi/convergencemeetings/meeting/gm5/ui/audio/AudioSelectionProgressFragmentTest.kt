package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class AudioSelectionProgressFragmentTest {

	lateinit var fragment: AudioSelectionProgressFragment
	private fun launchFragment(onInstantiated: (AudioSelectionProgressFragment) -> Unit = {}):
			FragmentScenario<AudioSelectionProgressFragment> {
		return FragmentScenario.launchInContainer(AudioSelectionProgressFragment::class.java,
		                                          null, R.style.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as AudioSelectionProgressFragment
				this@AudioSelectionProgressFragmentTest.fragment = fragment
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
	fun `test onCreate`() {
		launchFragment()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertNotNull(fragment.context)
	}

	@Test
	fun `test onCreate with parent view`() {
		var scenario = launchFragment()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		scenario.recreate()
		Assert.assertNotNull(fragment.context)
	}

}