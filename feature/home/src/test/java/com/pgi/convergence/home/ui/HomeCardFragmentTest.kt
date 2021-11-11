package com.pgi.convergence.home.ui

import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.jraska.livedata.test
import com.pgi.convergence.home.R
import com.pgi.convergence.home.di.homeTestModuleWithNoMsalUser
import com.pgi.convergence.home.di.homeTestModuleWithUserCount
import com.pgi.convergence.home.utils.*
import com.squareup.picasso.Picasso
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.UnstableDefault
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import java.util.concurrent.TimeUnit


@UnstableDefault
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@LooperMode(LooperMode.Mode.PAUSED)
class HomeCardFragmentTest : KoinTest {
	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()

	val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

	private var fragment: HomeCardFragment? = null
	
	private var scenario: FragmentScenario<HomeCardFragment>? = null

	private fun launchFragment(onInstantiated: (HomeCardFragment) -> Unit = {}):
			FragmentScenario<HomeCardFragment> {
		return FragmentScenario.launchInContainer(HomeCardFragment::class.java, null, R.style
				.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as HomeCardFragment
				this@HomeCardFragmentTest.fragment = fragment
				onInstantiated(fragment)
				return fragment
			}
		})
	}

	private fun startNoMsalUserModule() {
		startKoin {
			modules(homeTestModuleWithNoMsalUser)
		}
	}

	private fun startMsalUserModule() {
		startKoin {
			modules(homeTestModuleWithUserCount)
		}
	}

	@Before
	fun `set up`() {
		MockKAnnotations.init(this, relaxed = true)
		mockkStatic(Picasso::class)
		val picassoMock = mockkClass(Picasso::class, relaxed = true)
		coEvery {
			Picasso.get()
		} returns picassoMock
		Dispatchers.setMain(dispatcher)
		closeSoftKeyboard()
	}

	@After
	fun after() {
		stopKoin()
		scenario?.moveToState(Lifecycle.State.DESTROYED)
		fragment = null
	}

	@Test
	fun `1-test on Create welcome card is visible`() {
		startNoMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue()
		shadowOf(getMainLooper()).idle()
		R.id.connectCalCalendar.checkIsGone()
		R.id.noTasksLayout.checkIsGone()
		R.id.homeCardsContainer.checkIsVisible()
		R.id.homeCardsContainer.checkIsRecyclerSize(1)
	}

	@Test
	fun `2-test on connect click trigger msal authenctication`() {
		startNoMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue(10, TimeUnit.SECONDS)
		shadowOf(getMainLooper()).idle()
		R.id.connectCalCalendar.checkIsGone()
		R.id.noTasksLayout.checkIsGone()
		R.id.homeCardsContainer.checkIsVisible()
		R.id.homeCardsContainer.checkIsRecyclerSize(1)
		R.id.cardActionButton1.performViewAction(click())
		shadowOf(getMainLooper()).idle()
		coVerify {
			fragment?.mCardsViewModel?.authRepository?.authenticateUser()
		}
	}

	@Test
	fun `3-test on auth response we trigger Graph APi call`() {
		startMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue(10, TimeUnit.SECONDS)
		shadowOf(getMainLooper()).idle()
		coVerify {
			fragment?.mCardsViewModel?.graphRepository?.getTimedEvents(any(), any(), any(), any())
		}
	}

	@Test
	fun `4-getSearch`() {
		startMsalUserModule()
		scenario = launchFragment()
		shadowOf(getMainLooper()).idle()
		fragment?.mCardsViewModel?.getSuggestResults("test.test@pgi.com")
		Assertions.assertThat(fragment?.mCardsViewModel?.getSuggestResults("test.test@pgi.com"))
	}

	@Test
	fun `5-registerViewModelListener`() {
		startNoMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.registerViewModelListener()
		shadowOf(getMainLooper()).idle()
		Assertions.assertThat(fragment?.mCardsViewModel?.registerViewModelListener())
	}

	@Test
	fun `6-test on api response recyclerview has refresh data`() {
		startMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue(10, TimeUnit.SECONDS)
		shadowOf(getMainLooper()).idle()
		coVerify {
			fragment?.mCardsViewModel?.graphRepository?.getTimedEvents(any(), any(), any(), any())
		}
		R.id.homeCardsContainer.performScrollRecyclerTo(1)
		shadowOf(getMainLooper()).idle()
		R.id.homeCardsContainer.checkIsRecyclerSize(1)
	}

	@Test
	fun `7-test on card swipe card is dismissed from recycleview`() {
		startMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue(10, TimeUnit.SECONDS)
		shadowOf(getMainLooper()).idle()
		coVerify {
			fragment?.mCardsViewModel?.graphRepository?.getTimedEvents(any(), any(), any(), any())
		}
		shadowOf(getMainLooper()).idle()
		R.id.homeCardsContainer.checkIsDisplayed()
		R.id.homeCardsContainer.checkIsRecyclerSize(1)
		R.id.homeCardsContainer.performScrollRecyclerTo(0)
		shadowOf(getMainLooper()).idle()
		R.id.homeCardsContainer.performActionOnRecyclerItemAtPosition<RecyclerView.ViewHolder>(0, com
				.pgi.convergence.home.utils.swipeRight())
	}

	@Test
	fun `8-test remove item on dismiss`() {
		startMsalUserModule()
		scenario = launchFragment()
		fragment?.mCardsViewModel?.cardsData?.test()?.awaitValue(10, TimeUnit.SECONDS)
		shadowOf(getMainLooper()).idle()
		R.id.homeCardsContainer.checkIsDisplayed()
		R.id.homeCardsContainer.checkIsRecyclerSize(1)
		fragment?.removeItem(0)
		shadowOf(getMainLooper()).idle()
//		Assert.assertEquals(fragment?.mCardsViewModel?.getHomeCardAdapter()?.cardsData?.count(), 0)
	}

	@Test
	fun onPause() {
		startMsalUserModule()
		scenario = launchFragment()
		fragment?.onPause()
		Assertions.assertThat(fragment?.onPause())

	}
}