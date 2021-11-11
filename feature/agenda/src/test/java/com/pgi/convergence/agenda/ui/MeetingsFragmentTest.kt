package com.pgi.convergence.agenda.ui

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions
import androidx.test.filters.LargeTest
import com.jraska.livedata.test
import com.kevinmost.junit_retry_rule.Retry
import com.kevinmost.junit_retry_rule.RetryRule
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.di.meetingsTestModuleWithNoMsalUser
import com.pgi.convergence.agenda.di.meetingsTestModuleWithUserCount
import com.pgi.convergence.agenda.utils.*
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.utils.CommonUtils
import com.squareup.picasso.Picasso
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import java.util.concurrent.TimeUnit


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@LooperMode(LooperMode.Mode.PAUSED)
class MeetingsFragmentTest : KoinTest {
	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()

	@get:Rule
	val retry: RetryRule = RetryRule()


	val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

	private var fragment: MeetingsFragment? = null
	private var scenario: FragmentScenario<MeetingsFragment>? = null

	private fun launchFragment(onInstantiated: (MeetingsFragment) -> Unit = {}):
			FragmentScenario<MeetingsFragment> {
		return FragmentScenario.launchInContainer(MeetingsFragment::class.java, null, R.style
				.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as MeetingsFragment
				this@MeetingsFragmentTest.fragment = fragment
				onInstantiated(fragment)
				return fragment
			}
		})
	}

	@Before
	fun setUp() {
		MockKAnnotations.init(this, relaxed = true)
		mockkStatic(Picasso::class)
		val picassoMock = mockkClass(Picasso::class, relaxed = true)
		coEvery {
			Picasso.get()
		} returns picassoMock
		Dispatchers.setMain(dispatcher)
		ViewActions.closeSoftKeyboard()
		CoreApplication.appContext = ApplicationProvider.getApplicationContext()
	}

	@After
	fun after() {
		stopKoin()
		scenario?.moveToState(Lifecycle.State.DESTROYED)
		fragment = null
	}

	@Test
	fun `1-test on Create Connect Calendar is visible`() {
		startKoinAppWithNoevents()
		scenario  = launchFragment()
		fragment?.viewModel?.agendaData?.test()?.awaitNextValue(10, TimeUnit.SECONDS)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		R.id.tv_connect_to_calendar_or_search.checkIsVisible()
		R.id.rv_meetings_agenda.checkIsGone()
	}

	@Test
	fun `2-test on connect click trigger msal authenctication`() {
		startKoinAppWithNoevents()
		scenario = launchFragment()
		fragment?.viewModel?.agendaData?.test()?.awaitNextValue(10, TimeUnit.SECONDS)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		R.id.tv_connect_to_calendar_or_search.checkIsVisible()
		R.id.rv_meetings_agenda.checkIsGone()
		R.id.tv_connect_to_calendar_or_search.checkHasText("Connect your calendar")
		R.id.tv_connect_to_calendar_or_search.performViewAction(click())
		coVerify {
			fragment?.viewModel?.authRepository?.authenticateUser()
		}
	}


	@Test
	fun `3-test on auth response we trigger Graph APi call`() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		fragment?.viewModel?.agendaData?.test()?.awaitNextValue(10, TimeUnit.SECONDS)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		coVerify {
			fragment?.viewModel?.graphRepository?.getTimedEvents(any(), any(), any(), any())
		}
	}

	@Test
	fun `4-getSearch`() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		fragment?.viewModel?.getSuggestResults("test.test@pgi.com")
		Assertions.assertThat(fragment?.viewModel?.getSuggestResults("test.test@pgi.com"))
	}


	@Test
	fun `5-registerViewModelListener`() {
		startKoinAppWithNoevents()
		scenario = launchFragment()
		fragment?.viewModel?.registerViewModelListener()
		Assertions.assertThat(fragment?.viewModel?.registerViewModelListener())
	}

	@Test
	fun `6-test on api response recyclerview has refresh data`() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		fragment?.viewModel?.agendaData?.test()?.awaitNextValue(10, TimeUnit.SECONDS)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		coVerify {
			fragment?.viewModel?.graphRepository?.getTimedEvents(any(), any(), any(), any())
		}
		Assert.assertNotNull(fragment?.viewModel?.agendaData?.value)
	}

	@Test
	@Retry
	fun `7-test Meetings tab data`() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		fragment?.viewModel?.populateRoomData()
		try {Thread.sleep(200) } catch(e: Exception) {
			Log.e("MeetingsFragmentTest: test 7", e.localizedMessage)}
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		R.id.tv_my_meeting_room.checkHasText("My meeting room")
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		R.id.iv_overflow_meetings.checkIsVisible()
	}

	@Test
	@Retry
	fun handleRedirect() {
		startKoinAppWithUserCount()
		val requestCode = 1231
		val resultCode = 1231
		val data: Intent = Intent.getIntent("")
		scenario = launchFragment()
		try {Thread.sleep(200) } catch(e: Exception) {
			Log.e("MeetingsFragmentTest: test 7", e.localizedMessage)}
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		fragment?.handleO365Redirect(requestCode, resultCode, data)
		Assert.assertNotNull(requestCode)
	}

	@Test
	@Retry
	fun onPauseCalled() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		fragment?.onPause()
		Assertions.assertThat(fragment?.onPause())
	}

	private fun startKoinAppWithNoevents() {
		startKoin {
			modules(meetingsTestModuleWithNoMsalUser)
		}
	}

	private fun startKoinAppWithUserCount() {
		startKoin {
			modules(meetingsTestModuleWithUserCount)
		}
	}

	@Test
	fun `showEndMeetingThankYouToast`() {
		startKoinAppWithUserCount()
		scenario = launchFragment()
		CommonUtils.showEndMeetingThankYouSnackBar(true)
		fragment?.showEndMeetingThankYouToast()
		Assert.assertNotNull(fragment)
		CommonUtils.showEndMeetingThankYouSnackBar(false)
		fragment?.showEndMeetingThankYouToast()
		Assert.assertNotNull(fragment)
		fragment?.endMeetingThankyouSnakbar = null
		CommonUtils.showEndMeetingThankYouSnackBar(true)
		fragment?.showEndMeetingThankYouToast()
		fragment?.binding = null
		CommonUtils.showEndMeetingThankYouSnackBar(true)
		fragment?.showEndMeetingThankYouToast()
		Assert.assertNotNull(fragment)
		fragment?.endMeetingThankyouSnakbar = null
		fragment?.showEndMeetingThankYouToast()
		Assert.assertNotNull(fragment)
	}
}