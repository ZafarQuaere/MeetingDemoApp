package com.pgi.convergence.home.ui

import android.content.Context
import android.os.Looper
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import com.pgi.convergence.data.enums.home.HomeCardType
import com.pgi.convergence.data.enums.home.HomeCardType.HOST_JOIN_NOW_MEETINGS
import com.pgi.convergence.data.enums.home.HomeCardType.WELCOME_CARD
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom.GMROOM
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom.NONGMROOM
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus.NONE
import com.pgi.convergence.data.model.home.CardProfile
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.data.model.home.ProfileSubhead
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.home.R
import com.pgi.convergence.home.databinding.UcchomecardBinding
import com.pgi.convergence.home.di.homeTestModuleWithNoMsalUser
import com.pgi.convergence.home.di.homeTestModuleWithUserCount
import com.pgi.network.viewmodels.ViewModelResource
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.UnstableDefault
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.CountDownLatch

@UnstableDefault
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LooperMode(LooperMode.Mode.PAUSED)
class HomeCardsViewModelTest : KoinTest {
	@get:Rule
	val taskExecutorRule = InstantTaskExecutorRule()

	private val mCardsViewModel: HomeCardsViewModel by inject()

	@MockK
	lateinit var channel: ConflatedBroadcastChannel<String?>

	@MockK
	lateinit var context: Context

	private lateinit var fragment: HomeCardFragment

	@MockK
	lateinit var httpException: HttpException

	@MockK
	lateinit var binding: UcchomecardBinding

	@MockK
	lateinit var view: HomeCardAdapter.CardHolder
	var cardData = HomeCardData("", HomeCardType.WELCOME_CARD, MsalMeetingStatus.NONE, null, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM), null, true, 1, 10, 12, "test@pgi.com"), null, null, null, null, null, null, null)
	var aList = Arrays.asList(cardData)


	private fun startNoMsalUserModule() {
		startKoin {
			modules(homeTestModuleWithNoMsalUser)
		}
		mockkStatic(ContextCompat::class)

		every {
			ContextCompat.startActivity(any(), any(), null)
		} just Runs

		mCardsViewModel.appComponent = ApplicationProvider.getApplicationContext()

	}

	private fun startMsalUserModule() {
		startKoin {
			modules(homeTestModuleWithUserCount)
		}

		mockkStatic(ContextCompat::class)

		every {
			ContextCompat.startActivity(any(), any(), null)
		} just Runs

		mCardsViewModel.appComponent = ApplicationProvider.getApplicationContext()
	}

	@Before
	fun before() {
		MockKAnnotations.init(this, relaxed = true)
		mockkStatic(Calendar::class)
		mockkStatic(Picasso::class)
		val picassoMock = mockkClass(Picasso::class, relaxed = true)
		coEvery {
			Picasso.get()
		} returns picassoMock

		coEvery {
			channel.close()
		} returns true
	}

	@After
	fun after() {
		stopKoin()

	}

	@Test
	fun `test card adapter`() {
		startNoMsalUserModule()
		Assert.assertEquals(
				R.layout.ucchomecard, mCardsViewModel.getHomeCardAdapter()
				.getItemViewType(0)
											 )
	}

	@Test
	fun `test authenticate msaluser`() {
		startNoMsalUserModule()
		mCardsViewModel.authenticateMsalUser()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		coVerify {
			mCardsViewModel.authRepository.authenticateUser()
		}
	}

	@Test
	fun `test authenticate msaluser on accesstoken null`() {
		startNoMsalUserModule()
		mCardsViewModel.authRepository.msalAccessTokenChannel.offer(null)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals(mCardsViewModel.mCardsList.size, 1)
	}

	@Test
	fun `test start loading`() {
		startNoMsalUserModule()
		mCardsViewModel.startLoading()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		Assert.assertTrue(
				mCardsViewModel.mCardsList.isNotEmpty()
										 )
	}

	@Test
	fun `test trigger card data timer`() = runBlockingTest {
		startNoMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
				.assertValue { it.size == 1 }
		Assert.assertNotSame("Hello", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test remove all cards`() {
		startNoMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		mCardsViewModel.removeAllCards()
		Assert.assertTrue(
				mCardsViewModel.mCardsList.isEmpty()
										 )
	}

	@Test
	fun `test remove card`() {
		startMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		val cardata = HomeCardData(
				"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
				WELCOME_CARD,
				NONE, null, null, null, null, null, null, null, null, null, false)
		mCardsViewModel.mCardsList.add(cardata)
		mCardsViewModel.sortCardsDataAndUpdate()
		mCardsViewModel.removeCard(0)
	}

	@Test
	fun `test get card at index`() {
		startNoMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		Assert.assertEquals(
				mCardsViewModel.getCardAtIndex(0).dismissed, false
											 )
	}


	@Test
	fun `test join meeting non-GM room`() {
		startMsalUserModule()
		val before = mCardsViewModel.launchUrl.value
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, "https://pgi.globalmeet.com/test", CardProfile(null,
				"T", null, ProfileSubhead("Sample", true, com.pgi.convergence.data.enums.msal.MsalMeetingRoom.NONGMROOM),
				null, true,
				1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		Assert.assertEquals(before, mCardsViewModel.launchUrl.value)
	}

	@Test
	fun `test unregister channel`() {
		startMsalUserModule()
		mCardsViewModel.authRepository.msalAccessTokenChannel = channel
		mCardsViewModel.unRegisterMsalTokenChannel()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		coVerify {
			mCardsViewModel.authRepository.msalAccessTokenChannel.close()
		}
	}

	@Test
	fun `test interatcive redirect`() {
		startMsalUserModule()
		mCardsViewModel.handleInteractiveRequestRedirect(1, 0, null)
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		coVerify {
			mCardsViewModel.authRepository.handleInteractiveRequestRedirect(any(), any(), any())
		}
	}

	@Test
	fun `test get profile image search`() {
		startMsalUserModule()
		mCardsViewModel.getSuggestResults("test.user@pgi.com")
		Assert.assertEquals(mCardsViewModel.map.size, 0)
	}

	@Test
	fun `test get profile image search in exception`() {
		startMsalUserModule()
		var exception: HttpException? = httpException
		mCardsViewModel.getSuggestResults("test@pgi.com")
		mCardsViewModel.mSearchResponse.value = ViewModelResource.error(exception)
		Assert.assertEquals(mCardsViewModel.map.size, 0)
	}

	@Test
	fun `test sortCardsDataAndUpdate`() {
		startMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		val cardata = HomeCardData(
				"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
				com.pgi.convergence.data.enums.home.HomeCardType.UPCOMING_MEETINGS,
				NONE, null, null, null, null, null, null,
				null, null, null)
		cardata.dismissed = false
		cardata.isAllDay = false
		mCardsViewModel.mCardsList.add(cardata)
		mCardsViewModel.sortCardsDataAndUpdate()
		Assert.assertFalse(mCardsViewModel.mCardsList.isEmpty())
	}

	@Test
	fun `test sortCardsDataAndUpdate with two upcoming cards`() {
		startMsalUserModule()
		mCardsViewModel.triggerCardsDataTimer()
		val testObserver = mCardsViewModel.cardsData.test()
		testObserver
				.awaitValue()
				.assertHasValue()
		mCardsViewModel.appComponent = org.robolectric.RuntimeEnvironment.application.applicationContext
		val cardata = HomeCardData(
				"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
				com.pgi.convergence.data.enums.home.HomeCardType.UPCOMING_MEETINGS,
				NONE, null, null, null, null, null, null,
				null, null, null)
		cardata.dismissed = false
		cardata.isAllDay = false
		mCardsViewModel.mCardsList.add(cardata)
		val secondCard = HomeCardData("second test card", HomeCardType.UPCOMING_MEETINGS, NONE, null, null, null, null, null,
				null, null, null, null)
		secondCard.dismissed = false
		secondCard.isAllDay = false
		mCardsViewModel.mCardsList.add(secondCard)
		mCardsViewModel.sortCardsDataAndUpdate()
		Assert.assertFalse(mCardsViewModel.mCardsList.isEmpty())
		mCardsViewModel.appComponent = ApplicationProvider.getApplicationContext()
	}

	@Test
	fun `show pop up `() {
		startMsalUserModule()
		launchFragment()
		val cardata = HomeCardData(
				"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
				com.pgi.convergence.data.enums.home.HomeCardType.UPCOMING_MEETINGS,
				NONE, null, null, null, null, null, null,
				null, null, null)
		val view = View(fragment.context)
		mCardsViewModel.showPopupMenu(view, cardata)
	}

	@Test
	fun `on cleared`() {
		startMsalUserModule()
		mCardsViewModel.onCleared()
		Assert.assertNotNull(mCardsViewModel.cancelCardDataTimer())
	}

	@Test
	fun `test loadProfileImageOrTest`() {
		val latch = CountDownLatch(1)
		startMsalUserModule()
		launchFragment()
		val circleImageView: CircleImageView = CircleImageView(fragment.context)
		var image: String? = null
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		mCardsViewModel.loadProfileImageHome(circleImageView, image)
		io.jsonwebtoken.lang.Assert.isNull(image)
		latch.countDown()
	}

	@Test
	fun `get last first visible card`() {
		startNoMsalUserModule()
		mCardsViewModel.lastFirstVisiblePosition = 0
		junit.framework.Assert.assertEquals(mCardsViewModel.lastFirstVisiblePosition, 0)
	}

	@Test
	fun `test callsManager observable`() {
		startNoMsalUserModule()
		mCardsViewModel.registerMsalTokenChannel()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals(mCardsViewModel.mCardsList.count(), 1)
	}

	@Test
	fun `test greeting de morning`() {
		startMsalUserModule()
		setLocale("de")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 1
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Guten Morgen,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting de afternoon `() {
		startMsalUserModule()
		setLocale("de")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 13
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Guten Tag,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting de night `() {
		startMsalUserModule()
		setLocale("de")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 17
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Guten Abend,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting fr morning`() {
		startMsalUserModule()
		setLocale("fr")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 1
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Bonjour,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting fr afternoon`() {
		startMsalUserModule()
		setLocale("fr")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 13
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Bon après-midi,", mCardsViewModel.greetingMsg)

	}

	@Test
	fun `test greeting fr night`() {
		startMsalUserModule()
		setLocale("fr")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 18
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Bonsoir,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting ja morning`() {
		startMsalUserModule()
		setLocale("ja")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 1
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("おはようございます、", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting ja afternoon`() {
		startMsalUserModule()
		setLocale("ja")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 13
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("こんにちは、", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting ja night`() {
		startMsalUserModule()
		setLocale("ja")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 17
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("こんばんは、", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting en morning`() {
		startMsalUserModule()
		setLocale("en")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 1
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Good morning,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting en afternoon`() {
		startMsalUserModule()
		setLocale("en")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 13
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Good afternoon,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting en night`() {
		startMsalUserModule()
		setLocale("en")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 17
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Good evening,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting nl morning`() {
		startMsalUserModule()
		setLocale("nl")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 1
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Goedemorgen,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting nl afternoon`() {
		startMsalUserModule()
		setLocale("nl")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 13
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Goedemiddag,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test greeting nl night`() {
		startMsalUserModule()
		setLocale("nl")
		val calendarMock = mockkClass(Calendar::class, relaxed = true)
		coEvery {
			calendarMock.get(any())
		} returns 17
		coEvery {
			Calendar.getInstance()
		} returns calendarMock
		launchFragment()
		mCardsViewModel.getGreeting()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		Assert.assertEquals("Goedenavond,", mCardsViewModel.greetingMsg)
	}

	@Test
	fun `test join meeting lumen falvor`() {
		startMsalUserModule()
		mCardsViewModel.launchUrl.value = "test"
		mCardsViewModel.flavor = "lumen"
		val link = "https://sample.yourlumenworkplace.com/test"
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null,
				ProfileSubhead("Smaple", true, GMROOM), null, true, 1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		if (link.contains("yourlumenworkplace")) {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "https://sample.yourlumenworkplace.com/test")
		} else {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "test")
		}
	}


	@Test
	fun `test join meeting gm flavor`() {
		startMsalUserModule()
		mCardsViewModel.launchUrl.value = "test"
		mCardsViewModel.flavor = "prod"
		val link = "https://pgi.globalmeet.com/test"
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, GMROOM), null, true, 1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		if (link.contains("globalmeet")) {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "https://pgi.globalmeet.com/test")
		} else {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "test")
		}
	}

	@Test
	fun `test join meeting gm flavor non-gmroom`() {
		startMsalUserModule()
		mCardsViewModel.launchUrl.value = "test"
		mCardsViewModel.flavor = "prod"
		val link = "https://pgi.globalmeet.com/test"
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, NONGMROOM), null, true, 1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		Assert.assertEquals(mCardsViewModel.launchUrl.value, "test")
	}

	@Test
	fun `test join meeting lumen flavor`() {
		startMsalUserModule()
		mCardsViewModel.launchUrl.value = "test"
		mCardsViewModel.flavor = "lumen"
		val link = "https://sample.yourlumenworkplace.com/test"
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, GMROOM), null, true, 1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		if (link.contains("yourlumenworkplace")) {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "https://sample.yourlumenworkplace.com/test")
		} else {
			Assert.assertEquals(mCardsViewModel.launchUrl.value, "test")
		}
	}

	@Test
	fun `test join meeting lumen flavor non-gmroom`() {
		startMsalUserModule()
		mCardsViewModel.launchUrl.value = "test"
		mCardsViewModel.flavor = "lumen"
		val link = "https://sample.yourlumenworkplace.com/test"
		mCardsViewModel.joinMeeting(HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, NONGMROOM), null, true, 1, 10, 12, "organizer@test.com"), JoinMeetingEntryPoint.HOME_URL)
		Assert.assertEquals(mCardsViewModel.launchUrl.value, "test")
	}

	@Test
	fun `test sharePref`() {
		startMsalUserModule()
		val sp = mCardsViewModel.sharedPref
		Assert.assertNotNull(sp)
	}

	@Test
	fun `test profileImageHome`() {
		startNoMsalUserModule()
		val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
		mCardsViewModel.appComponent = context
		val circleImageView: CircleImageView = CircleImageView(context)
		val profileImage = "https://id-data.globalmeet.com/r/profile-images/100x100/1b3e9d5c-3baa-4738-ae62-eaf5ce6bdcf3"
		mCardsViewModel.loadProfileImageHome(circleImageView, profileImage)
		Assert.assertNotNull(circleImageView)
	}

	@Test
	fun `test set data`() {
		startMsalUserModule()
		Shadows.shadowOf(Looper.getMainLooper()).idle()
		val list = mCardsViewModel.mCardsList
		mCardsViewModel.getHomeCardAdapter().setData(list)
		Assertions.assertThat(mCardsViewModel.getHomeCardAdapter().setData(list))
	}

	private fun launchFragment(onInstantiated: (HomeCardFragment) -> Unit = {}):
			FragmentScenario<HomeCardFragment> {
		return FragmentScenario.launchInContainer(HomeCardFragment::class.java, null, R.style
				.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as HomeCardFragment
				this@HomeCardsViewModelTest.fragment = fragment
				onInstantiated(fragment)
				return fragment
			}
		})
	}

	private fun getTime(): Int {
		var calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY)
	}

	private fun setLocale(newLocale: String) {
		var locale = Locale(newLocale);
		Locale.setDefault(locale);
		var config = mCardsViewModel.appComponent?.getResources()?.getConfiguration();
		config?.locale = locale;
		mCardsViewModel.appComponent?.getResources()?.updateConfiguration(config,
				mCardsViewModel.appComponent?.getResources()?.getDisplayMetrics());
	}
}