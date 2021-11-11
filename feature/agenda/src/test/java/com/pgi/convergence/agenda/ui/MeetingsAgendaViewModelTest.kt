package com.pgi.convergence.agenda.ui
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Looper
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.jraska.livedata.test
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.databinding.AgendaRecycleviewCellsBinding
import com.pgi.convergence.agenda.di.meetingsTestModuleWithNoMsalUser
import com.pgi.convergence.agenda.di.meetingsTestModuleWithUserCount
import com.pgi.convergence.agenda.utils.blockingObserve
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.data.enums.home.HomeCardType
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus
import com.pgi.convergence.data.model.home.CardProfile
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.data.model.home.ProfileSubhead
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.InternetConnection
import com.pgi.network.models.GMMeetingInfoGetResponse
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.UnstableDefault
import org.amshove.kluent.internal.assertSame
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
import java.io.File
import java.util.*


@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LooperMode(LooperMode.Mode.PAUSED)
class MeetingsAgendaViewModelTest :KoinTest{

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val meetingsAgendaViewModel: MeetingsAgendaViewModel by inject()
    var activity = MeetingsFragment()

    @ExperimentalCoroutinesApi
    @MockK
    lateinit var channel: ConflatedBroadcastChannel<String?>
    @MockK
    lateinit var view: MeetingsAgendaAdapter.ViewHolder
    var cardData =  HomeCardData("", HomeCardType.WELCOME_CARD, MsalMeetingStatus.NONE, null, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM), null, true, 1, 10, 12, "test@pgi.com"), null, null , null, null, null, null, null)
    var aList = Arrays.asList(cardData)
    @MockK
    lateinit var binding: AgendaRecycleviewCellsBinding
    private var mockMeetingEmptyResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{},\"MeetingRoomDetail\":{},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{}}}"
    private var mockMeetingNumbersEmptyResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"\",\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"+84 28 5678 4438\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Vietnam\",\"PhoneNumber\":\"1800 400 370\",\"PhoneType\":\"International toll free\",\"CustomLocation\":\"Vietnam (toll free)\"}],\"PrimaryAccessNumber\":\"\"},\"MeetingRoomDetail\":{\"EnableVrc\":true},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":\"335@337.1\"}}}"
    private var mockMeetingPhoneEmptyResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"1234567\",\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"\",\"PhoneType\":\"Local\",\"CustomLocation\":null}],\"PrimaryAccessNumber\":\"101-606-7789\"},\"MeetingRoomDetail\":{\"EnableVrc\":true},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":\"\"}}}"
    private var mockMeetingEnableVrcNullResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"\",\"phoneInformation\":[],\"PrimaryAccessNumber\":\"\"},\"MeetingRoomDetail\":{\"EnableVrc\":null},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":\"\"}}}"
    private var mockMeetingNumberNullResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"\",\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"\",\"PhoneType\":\"Local\",\"CustomLocation\":null}],\"PrimaryAccessNumber\":\"\"},\"MeetingRoomDetail\":{\"EnableVrc\":false},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":\"\"}}}"
    private var mockMeetingVrcNullResponse = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"\",\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"\",\"PhoneType\":\"Local\",\"CustomLocation\":null}],\"PrimaryAccessNumber\":\"\"},\"MeetingRoomDetail\":{\"EnableVrc\":null},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":null}}}"
    private var mockMeetingWithResp = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"1234567\",\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"\",\"PhoneType\":\"Local\",\"CustomLocation\":null}],\"PrimaryAccessNumber\":\"101-606-7789\"},\"MeetingRoomDetail\":{\"ConferenceTitle\":\"Mohammad\"},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":null}}}"
    private var mockMeetingPrimaryNoNullResp = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":null,\"phoneInformation\":[{\"Location\":\"Vietnam, Ho Chi Minh\",\"PhoneNumber\":\"\",\"PhoneType\":\"Local\",\"CustomLocation\":null}],\"PrimaryAccessNumber\":null},\"MeetingRoomDetail\":{\"ConferenceTitle\":null},\"MeetingRoomId\":3122397,\"MeetingRoomUrls\":{\"VRCUrl\":null}}}"
    private var mockMeetingGetResultNullResp = "{\"MeetingRoomGetResult\":null}"
    private var mockMeetingAudioDetailNullResp = "{\"MeetingRoomGetResult\":{\"AudioDetail\":null}}"
    private val meetingUrl = "https://pgi.globalmeet.com/test"
    private var mockMeetingWithNullPNumber = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":\"1234567\",\"PrimaryAccessNumber\":null}}}"
    private var mockMeetingWithNullPassCode = "{\"MeetingRoomGetResult\":{\"AudioDetail\":{\"ParticipantPassCode\":null,\"PrimaryAccessNumber\":\"101-606-7789\"}}}"

    @ExperimentalCoroutinesApi
    @Before()
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
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
    fun getFirstName() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.firstName ="Nnenna"
        Assert.assertNotNull(meetingsAgendaViewModel.firstName)
        Assert.assertTrue(meetingsAgendaViewModel.firstName == "Nnenna")
    }

    @Test
    fun getLastName() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.lastName = "Iheke"
        Assert.assertNotNull( meetingsAgendaViewModel.lastName )
        Assert.assertTrue( meetingsAgendaViewModel.lastName == "Iheke")
    }

    @Test
    fun getInitials() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.initials = "NI"
        Assert.assertNotNull(meetingsAgendaViewModel.initials)
        Assert.assertTrue(meetingsAgendaViewModel.initials == "NI")
    }

    @Test
    fun getProfileImage() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.profileImage = "https://id-data.globalmeet.com/r/profile-images/100x100/1b3e9d5c-3baa-4738-ae62-eaf5ce6bdcf3"
        Assert.assertNotNull(meetingsAgendaViewModel.profileImage)
        Assert.assertTrue(meetingsAgendaViewModel.profileImage == "https://id-data.globalmeet.com/r/profile-images/100x100/1b3e9d5c-3baa-4738-ae62-eaf5ce6bdcf3"
        )
    }

    @Test
    fun getJoinUrl() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = "http://pgi.com/nnenna"
        Assert.assertNotNull(meetingsAgendaViewModel.joinUrl)
        Assert.assertTrue(meetingsAgendaViewModel.joinUrl.value == "http://pgi.com/nnenna")
    }

    @Test
    fun getMeetingRoomName() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.meetingRoomName = "My Meeting Room"
        Assert.assertNotNull(meetingsAgendaViewModel.meetingRoomName)
        Assert.assertTrue(meetingsAgendaViewModel.meetingRoomName == "My Meeting Room")

    }

    @Test
    fun populateDefaultRoomData() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.populateRoomData()
        Assert.assertNotNull(meetingsAgendaViewModel.meetingRoomName)
        Assert.assertNotNull(meetingsAgendaViewModel.initials)
    }

    @Test
    fun testAgendaAdapter() {
        startNoMsalUserModule()
//        org.junit.Assert.assertEquals(R.layout.agenda_recycleview_cells, meetingsAgendaViewModel.getMeetingsAgendaAdapter()?.getItemViewType(0))
    }

    @Test
    fun `test start loading`() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.startLoading()
        val testObserver = meetingsAgendaViewModel.agendaData.test()
        testObserver
                .awaitValue()
                .assertHasValue()
        org.junit.Assert.assertTrue(
                meetingsAgendaViewModel.mAgendaList.isNotEmpty()
        )
    }
    @Test
    fun testAuthenticateMsaluser() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.authenticateMsalUser()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        coVerify {
            meetingsAgendaViewModel.authRepository.authenticateUser()
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun testTriggerDataTimer() = runBlockingTest {
        startNoMsalUserModule()
        meetingsAgendaViewModel.initials = "NI"
        meetingsAgendaViewModel.triggerAgendaDataTimer()
        Assert.assertEquals("NI", meetingsAgendaViewModel.initials)

    }


    @Test
    fun testJoinMeeting() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS, "https://pgi" +
                ".globalmeet.com/test", CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM), null, true, 1, 10, 12, "organizer@test.com"))
        Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "https://pgi.globalmeet.com/test")

    }

    @Test
    fun `testJoinMeeting with welcome card`() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = null
        meetingsAgendaViewModel.joinMeeting(HomeCardType.WELCOME_CARD, "https://pgi" +
                ".globalmeet.com/test", CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM),
                null, true, 1, 10, 12, "organizer@test.com"))
        org.junit.Assert.assertNull(meetingsAgendaViewModel.launchUrl.value)
    }

    @Test
    fun testUnregisterChannel() {
        startMsalUserModule()
        meetingsAgendaViewModel.authRepository.msalAccessTokenChannel = channel
        meetingsAgendaViewModel.unRegisterMsalTokenChannel()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        coVerify {
            meetingsAgendaViewModel.authRepository.msalAccessTokenChannel.close()
        }
    }

    @Test
    fun testInteratciveRedirect() {
        startMsalUserModule()
        meetingsAgendaViewModel.handleInteractiveRequestRedirect(1, 0, null)
        coVerify {
            meetingsAgendaViewModel.authRepository.handleInteractiveRequestRedirect(any(), any(), any())
        }
    }

    private fun startNoMsalUserModule() {
        startKoin {
            modules(meetingsTestModuleWithNoMsalUser)
        }
        mockkStatic(ContextCompat::class)

        every {
            ContextCompat.startActivity(any(), any(), null)
        } just Runs

        meetingsAgendaViewModel.appComponent = ApplicationProvider.getApplicationContext()

    }


    @Test
    fun `get profile image search`() {
        startMsalUserModule()
        meetingsAgendaViewModel.getSuggestResults("test.user@pgi.com")
        meetingsAgendaViewModel.mSearchResponse.blockingObserve(2)
        assertSame(meetingsAgendaViewModel.map.size, 0)
    }


    private fun startMsalUserModule() {
        startKoin {
            modules(meetingsTestModuleWithUserCount)
        }

        mockkStatic(ContextCompat::class)

        every {
            ContextCompat.startActivity(any(), any(), null)
        } just Runs

        meetingsAgendaViewModel.appComponent = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `test startMeetingAndDismissDialog`() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "https://pgi.globalmeet.com/test"
        meetingsAgendaViewModel.startMeetingAndDismissDialog()
        org.junit.Assert.assertNotNull(meetingsAgendaViewModel.launchUrl.value)
    }

    @Test
    fun `test copyUrlAndDismissDialog`() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "https://pgi.globalmeet.com/test"
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        meetingsAgendaViewModel.copyUrlAndDismissDialog()
        org.junit.Assert.assertNotNull(meetingsAgendaViewModel.launchUrl.value)
    }

    @Test
    fun `test getFirsttimeUserWelcomeCardData`() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.getFirstTimeUserWelcomeCardData()
        val list = meetingsAgendaViewModel.mAgendaList
        org.junit.Assert.assertTrue(!list.isEmpty())
    }

    @Test
    fun `test getFirsttimeUserWelcomeCardData with msal`() {
        startMsalUserModule()
        meetingsAgendaViewModel.getFirstTimeUserWelcomeCardData()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertTrue(!list.isEmpty())
    }

    @Test
    fun `test onClick for tv_meeting_url`() {
        startMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_meeting_url
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertFalse(list.isEmpty())
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getJson(path : String) : String {
        val uri = javaClass.classLoader!!.getResource(path)
        val file = File(uri.file)
        return String(file.readBytes())
    }

    @Test
    fun `test onClick for tv_schedule_a_meeting`() {
        startMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_schedule_a_meeting
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.mIsLoading = true
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginforesultemptyresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        meetingsAgendaViewModel.mIsLoading = false
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetingemptyresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        meetingsAgendaViewModel.mIsLoading = false
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfoemptyresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        meetingsAgendaViewModel.mIsLoading = false
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfonullresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        setupMockingForIntents()
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfocorrectphonenumemptyresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        setupMockingForOutlook()
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfocorrectresponse.json"), GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)

        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingEmptyResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingNumbersEmptyResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingPhoneEmptyResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingEnableVrcNullResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingNumberNullResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingVrcNullResponse, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
    }

    private fun setupMockingForIntents() {

        val mockPackageManager = mockPackageManager()
        val resolveInfoGoogle = mockResolveInfo("com.google.app.calendar", "com.google.app.calendar.CalendarDispatcherActivity")
        val resolveInfoOutlook = mockResolveInfo(AppConstants.OUTLOOK_APP_PACKAGE_NAME, "com.microsoft.office.outlook.calendar.CalendarDispatcherActivity")
        every { mockPackageManager.queryIntentActivities(any(), 0) } returns listOf(resolveInfoGoogle ,resolveInfoOutlook)

    }

    private fun setupMockingForOutlook() {
        val mockPackageManager = mockPackageManager()

        val resolveInfoOutlook = mockResolveInfo("com.microsoft.office.outlook", "com.microsoft.office.outlook.calendar.CalendarDispatcherActivity")
        every { mockPackageManager.queryIntentActivities(any(), 0) } returns listOf(resolveInfoOutlook)
    }

    private fun mockPackageManager(): PackageManager {
        val spyContext = spyk(meetingsAgendaViewModel.appComponent!!)
        val mockPackageManager = mockkClass(PackageManager::class)
        every { spyContext.packageManager } returns mockPackageManager
        meetingsAgendaViewModel.appComponent = spyContext
        return mockPackageManager
    }

    private fun mockResolveInfo(packageName: String, activityName: String): ResolveInfo {
        val resolveInfo = mockkClass(ResolveInfo::class)
        val activityInfo = mockkClass(ActivityInfo::class)
        activityInfo.packageName = packageName
        activityInfo.name = activityName
        resolveInfo.activityInfo = activityInfo
        return resolveInfo
    }

    @Test
    fun `test onClick for iv_overflow_meetings`() {
        startMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.iv_overflow_meetings
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertFalse(list.isEmpty())
    }

    @Test
    fun `test onClick for tv_join_my_meeting`() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = "https://pgi.globalmeet.com/test"
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_join_my_meeting
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertFalse(list.isEmpty())
    }
    @Test
    fun `test onClick for tv_join_my_meeting with null join url`() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = null
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_join_my_meeting
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertFalse(list.isEmpty())
    }

    @Test
    fun `test onClick for tv_copy_meeting_url`() {
        startMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_copy_meeting_url
        meetingsAgendaViewModel.onClick(view)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val list = meetingsAgendaViewModel.mAgendaList
        Assert.assertFalse(list.isEmpty())
    }

    @Test
    fun `test loadProfileImageOrTest`() {
        startNoMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        meetingsAgendaViewModel.appComponent = context
        val circleImageView: CircleImageView = CircleImageView(context)
        meetingsAgendaViewModel.profileImage = "https://id-data.globalmeet.com/r/profile-images/100x100/1b3e9d5c-3baa-4738-ae62-eaf5ce6bdcf3"
        meetingsAgendaViewModel.loadProfileImageOrText(circleImageView)
        meetingsAgendaViewModel.loadProfileImageHome(circleImageView, "test")
        Assert.assertNotNull(meetingsAgendaViewModel.profileImage)
    }

    @Test
    fun `test join meeting non-GM room`() {
        startMsalUserModule()
        meetingsAgendaViewModel.appComponent = org.robolectric.RuntimeEnvironment.application.applicationContext
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS,"https://pgi.globalmeet.com/test",
                CardProfile(null,
                        "T", null, ProfileSubhead("Sample", true, com.pgi.convergence.data.enums.msal.MsalMeetingRoom.NONGMROOM),
                        null, true,
                        1, 10, 12, "organizer@test.com"))
        Assert.assertNotNull(meetingsAgendaViewModel.launchUrl.value)
    }

    @Test
    fun `test getting invokedRedirect`() {
        startNoMsalUserModule()
        val ir = meetingsAgendaViewModel.invokedRedirect
        Assert.assertNotNull(ir)
    }

    @Test
    fun `test getting clip`() {
        startNoMsalUserModule()
        val clip = meetingsAgendaViewModel.clip
        Assert.assertNull(clip)
    }

    @Test
    fun `test getting clipboard`() {
        startNoMsalUserModule()
        val cb = meetingsAgendaViewModel.clipboard
        Assert.assertNull((cb))
    }

//    @Test
//    fun `get last first visible card`(){
//        startNoMsalUserModule()
//        meetingsAgendaViewModel.lastFirstVisiblePosition = 0
//        assertEquals(meetingsAgendaViewModel.lastFirstVisiblePosition, 0)
//    }

    @Test
    fun `test that agenda is not empty`() {
        startNoMsalUserModule()
        meetingsAgendaViewModel.getFirstTimeUserWelcomeCardData()
        val list = meetingsAgendaViewModel.mAgendaList
        val empty = meetingsAgendaViewModel.isAgendaEmpty()
        org.junit.Assert.assertFalse(empty)
        meetingsAgendaViewModel.onCleared()
    }

    @Test
    fun testJoinMeetingWithLUMENflavor() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "test"
        meetingsAgendaViewModel.flavor = "lumen"
        val link = "https://sample.yourlumenworkplace.com/test"
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM), null, true, 1, 10, 12, "organizer@test.com"))
        if(link.contains("yourlumenworkplace")) {
            Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "https://sample.yourlumenworkplace.com/test")
        } else {
            Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "test")
        }

    }

    @Test
    fun `testJoinMeetingWithLUMENflavor non-gmroom`() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "test"
        meetingsAgendaViewModel.flavor = "lumen"
        val link = "https://sample.yourlumenworkplace.com/test"
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.NONGMROOM), null, true, 1, 10, 12, "organizer@test.com"))
        Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "test")
    }

    @Test
    fun testJoinMeetingWithProdflavor() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "test"
        meetingsAgendaViewModel.flavor = "prod"
        val link = "https://pgi.globalmeet.com/test"
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.GMROOM), null, true, 1, 10, 12, "organizer@test.com"))
        if(link.contains("globalmeet")) {
            Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "https://pgi.globalmeet.com/test")
        } else {
            Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "test")
        }
    }

    @Test
    fun `testJoinMeetingWithProdflavor non-gmroom`() {
        startMsalUserModule()
        meetingsAgendaViewModel.launchUrl.value = "test"
        meetingsAgendaViewModel.flavor = "prod"
        val link = "https://pgi.globalmeet.com/test"
        meetingsAgendaViewModel.joinMeeting(HomeCardType.HOST_JOIN_NOW_MEETINGS, link, CardProfile(null, "T", null, ProfileSubhead("Sample", true, MsalMeetingRoom.NONGMROOM), null, true, 1, 10, 12, "organizer@test.com"))
        Assert.assertEquals(meetingsAgendaViewModel.launchUrl.value, "test")
    }

    @Test
    fun `test flavor`() {
        startNoMsalUserModule()
        val flavor = meetingsAgendaViewModel.flavor
        Assert.assertEquals(meetingsAgendaViewModel.flavor, flavor)
    }

    @Test
    fun `test lastFirstVisiblePosition`() {
        startNoMsalUserModule()
        val pos = meetingsAgendaViewModel.lastFirstVisiblePosition
        Assert.assertEquals(meetingsAgendaViewModel.lastFirstVisiblePosition, pos)
    }

    @Test
    fun `test set data`() {
            startMsalUserModule()
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            val list = meetingsAgendaViewModel.mAgendaList
            meetingsAgendaViewModel.getMeetingsAgendaAdapter()?.setData(list)
        Assertions.assertThat(meetingsAgendaViewModel.getMeetingsAgendaAdapter()?.setData(list))
    }

    @Test
    fun `test launchCalendar no internet`() {
        mockkStatic(InternetConnection::class)
        every { InternetConnection.isConnected(any()) } returns false
        startNoMsalUserModule()
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.launchCalendar()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        unmockkStatic(InternetConnection::class)
    }

    @Test
    fun `test getMeetingRoomInfo`() {
        val mockSharedPreferencesManager = mockkClass(SharedPreferencesManager::class)
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns "12345"
        mockkStatic(SharedPreferencesManager::class)
        every { SharedPreferencesManager.getInstance() } returns mockSharedPreferencesManager
        startNoMsalUserModule()
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.mIsLoading = false
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns ""
        meetingsAgendaViewModel.getMeetingRoomInfo()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)

        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfocorrectresponse.json"), GMMeetingInfoGetResponse::class.java)
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns "12345"
        meetingsAgendaViewModel.getMeetingRoomInfo()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)

        meetingsAgendaViewModel.mIsLoading = true
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(getJson("json/meetinginfocorrectresponse.json"), GMMeetingInfoGetResponse::class.java)
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns "12345"
        meetingsAgendaViewModel.getMeetingRoomInfo(true)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)

        meetingsAgendaViewModel.mIsLoading = false
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns "12345"
        meetingsAgendaViewModel.getMeetingRoomInfo(true)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
    }

    @Test
    fun `test roomId empty`() {
        val mockSharedPreferencesManager = mockkClass(SharedPreferencesManager::class)
        every { mockSharedPreferencesManager.prefDefaultMeetingRoom } returns ""
        mockkStatic(SharedPreferencesManager::class)
        every { SharedPreferencesManager.getInstance() } returns mockSharedPreferencesManager
        startNoMsalUserModule()
        meetingsAgendaViewModel.getMeetingRoomInfo()
        Assert.assertEquals(meetingsAgendaViewModel.mRoomId,"")
    }

    @Test
    fun `test schedule meeting`() {
        startNoMsalUserModule()
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        meetingsAgendaViewModel.appComponent = context
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.scheduleMeeting(meetingsAgendaViewModel.mMeetingRoomInfo)
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson("""{"MeetingRoomGetResult": {}}""", GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.scheduleMeeting(meetingsAgendaViewModel.mMeetingRoomInfo)
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo)
    }

    @Test
    fun `test onClick for tv_share_meeting_url`() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = meetingUrl
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingWithResp, GMMeetingInfoGetResponse::class.java)
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_share_meeting_url
        meetingsAgendaViewModel.onClick(view)
        Assert.assertNotNull(meetingsAgendaViewModel.joinUrl)
        Assert.assertTrue(meetingsAgendaViewModel.joinUrl.value == meetingUrl)
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.primaryAccessNumber)
        Assert.assertNotNull(meetingsAgendaViewModel.mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.participantPassCode)
        Assert.assertNotNull(meetingsAgendaViewModel. mMeetingRoomInfo?.meetingRoomGetResult?.meetingRoomDetail?.conferenceTitle)

    }

    @Test
    fun `test onClick for tv_share_meeting_url_null`() {
        startMsalUserModule()
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingPrimaryNoNullResp, GMMeetingInfoGetResponse::class.java)
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.application.applicationContext
        val view = View(context)
        view.id = R.id.tv_share_meeting_url
        meetingsAgendaViewModel.onClick(view)
        meetingsAgendaViewModel.joinUrlData.value = null
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingWithNullPNumber, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingWithNullPassCode, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingAudioDetailNullResp, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail)
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingGetResultNullResp, GMMeetingInfoGetResponse::class.java)
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo?.meetingRoomGetResult)
        meetingsAgendaViewModel.mMeetingRoomInfo = null
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        meetingsAgendaViewModel.appComponent = null
        meetingsAgendaViewModel.shareUrlAndDismissDialog()
        Assert.assertNull(meetingsAgendaViewModel.mMeetingRoomInfo)
    }

    @Test
    fun `test shareUrlAndDismissDialog`() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = meetingUrl
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingWithResp, GMMeetingInfoGetResponse::class.java)
        Assert.assertNotNull(meetingsAgendaViewModel.shareUrlAndDismissDialog())
    }

    @Test
    fun `test shareUrlAndDismissDialogNull`() {
        startMsalUserModule()
        meetingsAgendaViewModel.joinUrlData.value = null
        meetingsAgendaViewModel.mMeetingRoomInfo = Gson().fromJson(mockMeetingPrimaryNoNullResp, GMMeetingInfoGetResponse::class.java)
        Assert.assertNotNull(meetingsAgendaViewModel.shareUrlAndDismissDialog())
    }
}