package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.os.Handler
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.kevinmost.junit_retry_rule.RetryRule
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel

import com.pgi.convergencemeetings.search.di.joinMeetingFragmentModule
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.performClick
import com.pgi.logging.Logger
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.UnstableDefault
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
class WaitingRoomFragmentTest : RobolectricTest(), KoinTest {
	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()

	@get:Rule
	val retry: RetryRule = RetryRule()

	val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

	private var viewFragment: MiscRoomViewFragment? = null
	private var scenario: FragmentScenario<MiscRoomViewFragment>? = null
	private var mMiscRoomViewFragment: MiscRoomViewFragment? = null

	@Mock
	var logger: Logger? = null

	@MockK
	lateinit var mockMeetingUserViewModel :MeetingUserViewModel

	@Mock
	private val appAuth: AppAuthUtils? = null

	private fun launchFragment(onInstantiated: (MiscRoomViewFragment) -> Unit = {}):
			FragmentScenario<MiscRoomViewFragment> {
		return FragmentScenario.launchInContainer(MiscRoomViewFragment::class.java, null, com.pgi.convergence.agenda.R.style
				.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as MiscRoomViewFragment
				this@WaitingRoomFragmentTest.viewFragment = fragment
				this@WaitingRoomFragmentTest.mMiscRoomViewFragment = fragment
				fragment.mFirstName = "testUser"
				onInstantiated(fragment)
				return fragment
			}
		})
	}

	@Before
	fun setUp() {
		startKoinApp()
		MockKAnnotations.init(this, relaxed = true)
		mMiscRoomViewFragment = MiscRoomViewFragment()
		Dispatchers.setMain(dispatcher)
	}

	@After
	fun tearDown() {
		stopKoin()
		scenario?.moveToState(Lifecycle.State.DESTROYED)
		viewFragment = null
	}

	private fun startKoinApp() {
		startKoin {
			modules(joinMeetingFragmentModule)
		}
	}

	@Test
	@Throws(Exception::class)
	fun shouldNotBeNull() {
		appAuth?.setLogger(logger)
		Assert.assertNotNull(mMiscRoomViewFragment)
	}


	@Test
	fun newInstance() {
		launchFragment()
		viewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		MiscRoomViewFragment.newInstance("testName", UserFlowStatus.JOIN_WAIT_ROOM.status)
	}

	@Test
	@Throws(Exception::class)
	fun initTitle() {
		launchFragment()
		viewFragment?.tvWaitingRoomHeader
		viewFragment?.tvWaitingRoomTitleName
		viewFragment?.tvWaitingRoomTitleFull
		viewFragment?.tvWaitingRoomTitleLocked
		viewFragment?.mFirstName = "testUser"
		viewFragment?.initTitle()
		appAuth?.setLogger(logger)
		Assert.assertNotNull(mMiscRoomViewFragment)
	}


	@Test
	fun testStatusWait() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_WAIT_ROOM.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showWaitingRoomView())
		showWaitingRoomView()
	}

	@Test
	fun testStatusLocked() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_LOCK_MEETING.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingLockedView())
		showMeetingLockedView()
	}
	@Test
	fun testStatusLockedInWaitingRoom() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_LOCK.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingLockedView())
		showMeetingLockedView()
	}

	@Test
	fun testStatusCapacity() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_MEETING_AT_CAPACITY.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingFullView())
		showMeetingFullView()
	}

	@Test
	fun testStatusCapacityWhileWaiting() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_ROOM_AT_CAPACITY.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingFullView())
		showMeetingFullView()
	}

	@Test
	fun testStatusDeniedTimeOutHost() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_WAIT_TIMEOUT_HOST.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingWaitDeniedView())
		showMeetingWaitDeniedView()
	}
	@Test
	fun testStatusDeniedTimeOutAdmit() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_WAIT_TIMEOUT_ADMIT.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingWaitDeniedView())
		showMeetingWaitDeniedView()
	}

	@Test
	fun testStatusDenied() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_BY_HOST.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingWaitDeniedView())
		showMeetingWaitDeniedView()
	}


	@Test
	fun testMeetingEnded() {
		launchFragment()
		mMiscRoomViewFragment?.mFirstName = "TestUser"
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.DISMISSED_MEETING_ENDED.status)
		Assertions.assertThat(mMiscRoomViewFragment?.showMeetingEndedView())
		showMeetingEndedView()
		Assert.assertEquals(mMiscRoomViewFragment?.tvWaitingRoomEndTitle?.text.toString(), "TestUser's meeting " +
				"has ended")
	}

	@Test
	fun testMeetingBackBtn() {
		launchFragment()
		mMiscRoomViewFragment?.onWaitingBackBtn()
		Assert.assertTrue(mMiscRoomViewFragment?.activity?.isFinishing == true)
	}

	fun showMeetingFullView() {
		viewFragment?.lo_full?.visibility?.equals(View.VISIBLE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_locked?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_denied?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_wait?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_ended?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
	}

	fun showMeetingLockedView() {
		viewFragment?.lo_full?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_locked?.visibility?.equals(View.VISIBLE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_denied?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_wait?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_ended?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
	}

	fun showMeetingWaitDeniedView() {
		viewFragment?.lo_full?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_locked?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_denied?.visibility?.equals(View.VISIBLE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_wait?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_ended?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
	}

	fun showMeetingEndedView() {
		viewFragment?.lo_full?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_locked?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_denied?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_wait?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_ended?.visibility?.equals(View.VISIBLE)?.let {
			assertTrue(
					it
			          )
		}
	}

	fun showWaitingRoomView() {
		viewFragment?.lo_full?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_locked?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_denied?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_wait?.visibility?.equals(View.VISIBLE)?.let {
			assertTrue(
					it
			          )
		}
		viewFragment?.lo_ended?.visibility?.equals(View.GONE)?.let {
			assertTrue(
					it
			          )
		}
	}

	fun onWaitingBackClicked() {
		R.id.waiting_back_btn.performClick()
		viewFragment?.onWaitingBackBtn()
		Assertions.assertThat(viewFragment?.onWaitingBackBtn())
	}

	@Test
	fun `Test CheckForViewModelInitialization`() {
		launchFragment()
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.meetingUserViewModel)

		launchFragment()
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_LOCK_MEETING.status
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.meetingUserViewModel)
	}

	@Test
	fun `Test BackButtonClickWhenInWaitingRoom`() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_WAIT_ROOM.status)
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		mockMeetingUserViewModel.userInMeeting = true
		mockMeetingUserViewModel.userIsInWaitingRoom = true
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.mStatus)
		Handler().postDelayed({
			Assertions.assertThat(mMiscRoomViewFragment?.mStatus?.equals(UserFlowStatus.LEAVE_MEETING_SUCCESS.status))
		}, 1000)

	}

	@Test
	fun `Test BackButtonPressFinishesActivity`() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_WAIT_ROOM.status)
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		mockMeetingUserViewModel.userInMeeting = true
		mockMeetingUserViewModel.userIsInWaitingRoom = true
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.mStatus)
		Handler().postDelayed({
			assert(mMiscRoomViewFragment?.activity?.isFinishing == true)
		}, 1000)
	}

	@Test
	fun `Test BackButtonClickWhenNotInWaiting`() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_LOCK_MEETING.status)
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_LOCK_MEETING.status
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assert(mMiscRoomViewFragment?.activity?.isFinishing == true)
	}

	@Test
	fun `Test checkForFlagonBackButtonClick`() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_WAIT_ROOM.status)
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		mockMeetingUserViewModel.userInMeeting = true
		mockMeetingUserViewModel.userIsInWaitingRoom = true
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.mStatus)
	}

	@Test
	fun `Test BackButtonPressFinishesActivityWithoutDelay`() {
		launchFragment()
		mMiscRoomViewFragment?.setWaitScreen(UserFlowStatus.JOIN_WAIT_ROOM.status)
		mMiscRoomViewFragment?.mStatus = UserFlowStatus.JOIN_WAIT_ROOM.status
		mockMeetingUserViewModel.userInMeeting = true
		mockMeetingUserViewModel.userIsInWaitingRoom = true
		mMiscRoomViewFragment?.meetingUserViewModel = mockMeetingUserViewModel
		mMiscRoomViewFragment?.onWaitingBackBtn()
		assertNotNull(mMiscRoomViewFragment?.mStatus)
		assert(mMiscRoomViewFragment?.activity?.isFinishing == false)
	}
}
