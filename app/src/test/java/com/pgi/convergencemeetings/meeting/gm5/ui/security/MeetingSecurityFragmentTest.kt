package com.pgi.convergencemeetings.meeting.gm5.ui.security

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import blockingObserve
import com.google.gson.Gson
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.WaitingRoomEvents
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.di.meetingSecurityFragmentModule
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.network.models.*
import com.squareup.picasso.Picasso
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config
import java.io.File

@ExperimentalCoroutinesApi
@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MeetingSecurityFragmentTest: RobolectricTest(), KoinTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private var mMeetingSecurityFragment: MeetingSecurityFragment? = null
    private fun launchFragment(onInstantiated: (MeetingSecurityFragment) -> Unit = {}):
            FragmentScenario<MeetingSecurityFragment> {
        return FragmentScenario.launchInContainer(MeetingSecurityFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as MeetingSecurityFragment
                this@MeetingSecurityFragmentTest.mMeetingSecurityFragment = fragment
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
        mMeetingSecurityFragment = MeetingSecurityFragment()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        stopKoin()
        mMeetingSecurityFragment = null
    }

    private fun startKoinApp() {
        startKoin {
            modules(meetingSecurityFragmentModule)
        }
    }

    @Test
    fun `test viewModel not null`() {
        startKoinApp()
        launchFragment()
        Assert.assertNotNull(mMeetingSecurityFragment?.mMeetingEventsModel)
        Assert.assertNotNull(mMeetingSecurityFragment?.mMeetingUserViewModel)
    }

    @Test
    fun `test newInstance`() {
        startKoinApp()
        launchFragment()
        Assert.assertNotNull(MeetingSecurityFragment.newInstance())
        Assert.assertNotNull(mMeetingSecurityFragment?.recyclerView)
        Assert.assertNotNull(mMeetingSecurityFragment?.mMeetingSecurityHeader)
    }

    @Test
    fun `test onMeetingSecurityBackBtn`() {
        startKoinApp()
        val mockWebMeetingActivity = mockkClass(WebMeetingActivity::class)
        every { mockWebMeetingActivity.resumeActivity() } just Runs

        launchFragment()
        val spyFragment = spyk(mMeetingSecurityFragment!!)
        every { spyFragment.activity } returns mockWebMeetingActivity
        mMeetingSecurityFragment?.onMeetingSecurityBackBtn()

        val mockFragmentActivity = mockkClass(FragmentActivity::class)
        every { spyFragment.activity } returns mockFragmentActivity
        mMeetingSecurityFragment?.onMeetingSecurityBackBtn()
    }

    @Test
    fun `test toggleWaitingRoomState OnOff`() {
        startKoinApp()
        launchFragment()
        //when status updated by UAPI events
        var meetingEvent = Gson().fromJson(getJson("json/uapi/events/eventWaitingRoomEnabled.json"), UAPIMeetingEvent::class.java)
        mMeetingSecurityFragment?.mMeetingEventsModel?.parseMeetingEvents(meetingEvent)

        mMeetingSecurityFragment?.mMeetingEventsModel?.waitingRoomEnabledStatus?.blockingObserve(1)
        Assert.assertTrue(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked!!)

        meetingEvent = Gson().fromJson(getJson("json/uapi/events/eventWaitingRoomDisabled.json"), UAPIMeetingEvent::class.java)
        mMeetingSecurityFragment?.mMeetingEventsModel?.parseMeetingEvents(meetingEvent)
        mMeetingSecurityFragment?.mMeetingEventsModel?.waitingRoomEnabledStatus?.blockingObserve(1)
        Assert.assertFalse(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked!!)
    }

    @Test
    fun `test updateGuestWaitingList`() {
        startKoinApp()
        launchFragment()
        val users: ArrayList<User> = mockUserList()
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.postValue(users)
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.blockingObserve(1)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.tvWaitingRoomGuestListCount?.text, users.size.toString())
    }

    @Test
    fun `test respondToFrictionFreeStatus On`() {
        startKoinApp()
        launchFragment()
        val meetingEvent = Gson().fromJson(getJson("json/uapi/events/eventFrictionFreeEnabled.json"), UAPIMeetingEvent::class.java)
        mMeetingSecurityFragment?.mMeetingEventsModel?.parseMeetingEvents(meetingEvent)
        mMeetingSecurityFragment?.mMeetingEventsModel?.frictionFreeEnabledStatus?.blockingObserve(1)
        Assert.assertFalse(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.swSecurityOption?.isChecked!!)
    }

    @Test
    fun `test respondToFrictionFreeStatus off`() {
        startKoinApp()
        launchFragment()
        val meetingEvent = Gson().fromJson(getJson("json/uapi/events/eventFrictionFreeOff.json"), UAPIMeetingEvent::class.java)
        mMeetingSecurityFragment?.mMeetingEventsModel?.parseMeetingEvents(meetingEvent)
        mMeetingSecurityFragment?.mMeetingEventsModel?.frictionFreeEnabledStatus?.blockingObserve(1)
        Assert.assertTrue(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.swSecurityOption?.isChecked!!)
    }

    @Test
    fun `test updateUIForRoomRole Presenter`() {
        mockkConstructor(MeetingEventViewModel::class)
        val users = listOf(User(id = "123", roomRole = AppConstants.HOST), User(id = "1234", roomRole = AppConstants.PRESENTER))
        every { anyConstructed<MeetingEventViewModel>().getCurrentUser() } returns users[1]
        startKoinApp()
        launchFragment()

        val authResponse = Gson().fromJson(getJson("json/uapi/authsessionPresenter.json"), AuthorizeResponse::class.java)
        mMeetingSecurityFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mMeetingSecurityFragment?.mMeetingEventsModel?.setCurrentUserId("1234")
        mMeetingSecurityFragment?.mMeetingEventsModel?.users?.postValue(users)
        mMeetingSecurityFragment?.updateUIForRoomRole()
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.lockRoomView?.visibility, View.GONE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.visibility, View.GONE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.visibility, View.GONE)
    }

    @Test
    fun `test respondToLockedRoomState OnOff`() {
        startKoinApp()
        launchFragment()
        //when meeting is locked
        mMeetingSecurityFragment?.mMeetingEventsModel?.meetingLockStatus?.postValue(true)
        Assert.assertTrue(mMeetingSecurityFragment?.mMeetingSecurityHeader?.lockRoomView?.swSecurityOption?.isChecked!!)
        Assert.assertFalse(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isEnabled!!)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.alpha, AppConstants.ALPHA_WAITINGROOM_DISABLED)
        //when meeting is unlocked
        mMeetingSecurityFragment?.mMeetingEventsModel?.meetingLockStatus?.postValue(false)
        Assert.assertFalse(mMeetingSecurityFragment?.mMeetingSecurityHeader?.lockRoomView?.swSecurityOption?.isChecked!!)
        Assert.assertTrue(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isEnabled!!)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.alpha, AppConstants.ALPHA_WAITINGROOM_ENABLED)
    }

    @Test
    fun `test updateUIForRoomRole Host`() {
        val roomInfoResponse = spyk(Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java))
        val waitingRoomInfo = WaitingRoomInfo(enabled = false, enabledLocked = false)
        val frictionFreeInfo = FrictionFreeInfo(enabledLocked = false, enabled = false)
        val users = listOf(User(id = "123", roomRole = AppConstants.HOST), User(id = "1234", roomRole = AppConstants.GUEST))
        val authResponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
        every { roomInfoResponse.waitingRoom } returns waitingRoomInfo
        every { roomInfoResponse.frictionFree } returns frictionFreeInfo
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().getRoomInfoResponse() } returns roomInfoResponse
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().authResponse } returns authResponse

        startKoinApp()
        launchFragment()

        mMeetingSecurityFragment?.mMeetingEventsModel?.setCurrentUserId("123")
        mMeetingSecurityFragment?.mMeetingEventsModel?.users?.postValue(users)
        mMeetingSecurityFragment?.updateUIForRoomRole()
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.lockRoomView?.visibility, View.VISIBLE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.visibility, View.VISIBLE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.visibility, View.VISIBLE)
    }

    @Test
    fun `test responseToUserStatus FRICTION_FREE_ON_FAILURE`() {
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.FRICTION_FREE_ON_FAILURE)
        Assert.assertTrue(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.swSecurityOption?.isChecked ?: false)
    }

    @Test
    fun `test responseToUserStatus FRICTION_FREE_OFF_FAILURE`() {
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.FRICTION_FREE_OFF_FAILURE)
        Assert.assertFalse(mMeetingSecurityFragment?.mMeetingSecurityHeader?.restrictSharingView?.swSecurityOption?.isChecked ?: true)
    }

    @Test
    fun `test responseToUserStatus WAITING_ROOM_ON_FAILURE`() {
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.WAITING_ROOM_ON_FAILURE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked, false)
    }

    @Test
    fun `test responseToUserStatus WAITING_ROOM_OFF_FAILURE`() {
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.WAITING_ROOM_OFF_FAILURE)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked , true)
    }

    @Test
    fun `test registerViewModelListener`() {
        val roomInfoResponse = spyk(Gson().fromJson(getJson("json/uapi/roominfo.json"), MeetingRoomInfoResponse::class.java))
        val waitingRoomInfo = WaitingRoomInfo(enabled = false, enabledLocked = true)
        val frictionFreeInfo = FrictionFreeInfo(enabledLocked = true, enabled = false)
        every { roomInfoResponse.waitingRoom } returns waitingRoomInfo
        every { roomInfoResponse.frictionFree } returns frictionFreeInfo
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().getRoomInfoResponse() } returns roomInfoResponse
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.let {
            Assert.assertTrue(it.isFrictionFreeEnabledLocked)
            Assert.assertTrue(it.isWaitingRoomEnabledLocked)
            Assert.assertEquals(it.mMeetingSecurityHeader.waitingRoomView.visibility, View.GONE)
            Assert.assertEquals(it.mMeetingSecurityHeader.restrictSharingView.visibility, View.GONE)
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getJson(path : String) : String {
        val uri = javaClass.classLoader!!.getResource(path)
        val file = File(uri.file)
        return String(file.readBytes())
    }

    @Test
    fun `test AdmitAllGustBtn`() {
        CoreApplication.mLogger = TestLogger()
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().updateUserAdmitDeny(any(), any()) } just runs
        every { anyConstructed<MeetingUserViewModel>().updateAudioUserAdmitDeny(any(), any()) } just runs
        startKoinApp()
        val mockWebMeetingActivity = mockkClass(WebMeetingActivity::class)
        every { mockWebMeetingActivity.resumeActivity() } just Runs
        launchFragment()
        val spyFragment = spyk(mMeetingSecurityFragment!!)
        val mockFragmentActivity = mockkClass(FragmentActivity::class)
        every { spyFragment.activity } returns mockFragmentActivity

        val users = mockUserList()
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.postValue(users)
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.blockingObserve(1)
        mMeetingSecurityFragment?.admitAllUsers()
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsAdmitted?.size, users.size)

        unmockkConstructor(MeetingUserViewModel::class)
    }

    @Test
    fun `test AdmitAllGustBtn when mMeetingUserViewModel is null`() {
        CoreApplication.mLogger = TestLogger()
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().updateUserAdmitDeny(any(), any()) } just runs
        every { anyConstructed<MeetingUserViewModel>().updateAudioUserAdmitDeny(any(), any()) } just runs
        startKoinApp()
        val mockWebMeetingActivity = mockkClass(WebMeetingActivity::class)
        every { mockWebMeetingActivity.resumeActivity() } just Runs
        launchFragment()
        val spyFragment = spyk(mMeetingSecurityFragment!!)
        val mockFragmentActivity = mockkClass(FragmentActivity::class)
        every { spyFragment.activity } returns mockFragmentActivity
        val users = mockUserList()
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.postValue(users)
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.blockingObserve(1)
        mMeetingSecurityFragment?.mMeetingUserViewModel = null
        mMeetingSecurityFragment?.admitAllUsers()
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsAdmitted?.size, users.size)
        unmockkConstructor(MeetingUserViewModel::class)
    }

    @Test
    fun `test AdmitAllButtonVisibility`() {
        startKoinApp()
        launchFragment()
        var users: ArrayList<User> = ArrayList<User>()
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.postValue(users)
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.blockingObserve(0)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.tvAdmitAll?.visibility, View.GONE)
        users = mockUserList()
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.postValue(users)
        mMeetingSecurityFragment?.mMeetingEventsModel?.guestWaitingList?.blockingObserve(1)
        Assert.assertEquals(mMeetingSecurityFragment?.mMeetingSecurityHeader?.tvAdmitAll?.visibility, View.VISIBLE)
    }

    @Test
    fun `test sendMixpanelWaitingRoomControlEvent`() {
        CoreApplication.mLogger = TestLogger()
        startKoinApp()
        launchFragment()
        mMeetingSecurityFragment?.let { meetingSecurityFragment: MeetingSecurityFragment ->
            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.OPEN_MEETING_SECURITY)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.OPEN_MEETING_SECURITY.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.CANCEL)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.CANCEL.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.ADMIT_ALL)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.ADMIT_ALL.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.ADMIT)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.ADMIT.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.DENY)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.DENY.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.ENABLE_RESTRICT_SHARING)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.ENABLE_RESTRICT_SHARING.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.DISABLE_RESTRICT_SHARING)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.DISABLE_RESTRICT_SHARING.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.LOCK_MEETING)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.LOCK_MEETING.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.UNLOCK_MEETING)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.UNLOCK_MEETING.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.ENABLE_WAITING_ROOM)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.ENABLE_WAITING_ROOM.toString())

            meetingSecurityFragment.sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.DISABLE_WAITING_ROOM)
            Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.waitingRoomAction, WaitingRoomEvents.DISABLE_WAITING_ROOM.toString())
        }

    }

    @Test
    fun `test onAdmitDenyUser`() {
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().updateUserAdmitDeny(any(), any()) } just runs
        CoreApplication.mLogger = TestLogger()
        startKoinApp()
        launchFragment()

        val user = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "test@pgi.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false,isInAudioWaitingRoom = false)
        mMeetingSecurityFragment?.onAdmitDenyUser(user, true)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsAdmitted, 1)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsDenied, 0)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsAdmitted?.first(), "test@pgi.com")
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsDenied?.size, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsAdmitted, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsDenied, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.guestAdmitted?.size, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.guestDenied?.size, 0)

        mMeetingSecurityFragment?.onAdmitDenyUser(user, false)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsAdmitted, 0)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsDenied, 1)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsAdmitted?.size, 0)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.guestsDenied?.first(), "test@pgi.com")
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsAdmitted, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsDenied, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.guestAdmitted?.size, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.guestDenied?.size, 0)

        every { anyConstructed<MeetingUserViewModel>().updateAudioUserAdmitDeny(any(), any()) } just runs
        val audioUser = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "test@pgi.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false,isInAudioWaitingRoom = true)

        mMeetingSecurityFragment?.onAdmitDenyUser(audioUser, false)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsAdmitted, 0)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsDenied, 1)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsAdmitted, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsDenied, 0)
        mMeetingSecurityFragment?.onAdmitDenyUser(audioUser, true)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsAdmitted, 1)
        Assert.assertEquals(CoreApplication.mLogger.mixpanelMeetingSecurityModel.numGuestsDenied, 0)

        unmockkConstructor(MeetingUserViewModel::class)
    }

    @Test
    fun `test onAdmitDenyUser when mMeetingUserViewModel is null`() {
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().updateUserAdmitDeny(any(), any()) } just runs
        CoreApplication.mLogger = TestLogger()
        startKoinApp()
        launchFragment()

        every { anyConstructed<MeetingUserViewModel>().updateAudioUserAdmitDeny(any(), any()) } just runs
        val audioUser = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "test@pgi.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false, isInAudioWaitingRoom = true)
        mMeetingSecurityFragment?.mMeetingUserViewModel = null
        mMeetingSecurityFragment?.onAdmitDenyUser(audioUser, false)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsAdmitted, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsDenied, 0)
        unmockkConstructor(MeetingUserViewModel::class)
    }

    @Test
    fun `test onAdmitDenyUser when Guest not in waitingroom and mMeetingUserViewModel is null`() {
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().updateUserAdmitDeny(any(), any()) } just runs
        CoreApplication.mLogger = TestLogger()
        startKoinApp()
        launchFragment()

        every { anyConstructed<MeetingUserViewModel>().updateAudioUserAdmitDeny(any(), any()) } just runs
        val audioUser1 = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "test@pgi.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false,isInAudioWaitingRoom = false)
        mMeetingSecurityFragment?.mMeetingUserViewModel = null
        mMeetingSecurityFragment?.onAdmitDenyUser(audioUser1, false)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsAdmitted, 0)
        Assert.assertEquals(mMeetingSecurityFragment?.numGuestsDenied, 0)

        unmockkConstructor(MeetingUserViewModel::class)
    }


    private fun mockUserList() :ArrayList<User>{
        val users: ArrayList<User> = ArrayList<User>()
        val user1 = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "rajni.j@hcl.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false, isInAudioWaitingRoom = false)
        val user2 = User(id = "92ubinoow077xrml2audnsb62", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "rajni.j@hcl.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false, isInAudioWaitingRoom = true)
        users.add(user1)
        users.add(user2)
        return users
    }
}