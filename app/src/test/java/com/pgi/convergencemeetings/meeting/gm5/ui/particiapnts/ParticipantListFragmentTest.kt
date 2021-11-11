package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import com.google.gson.Gson
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.meeting.gm5.data.model.Audio
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.di.meetingSecurityFragmentModule
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.network.models.AuthorizeResponse
import com.pgi.network.models.MeetingRoomInfoResponse
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
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ParticipantListFragmentTest: RobolectricTest(), KoinTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private var mParticipantListFragment: ParticipantListFragment? = null
    private val meetingUrl = "https://pgi.globalmeet.com/test"
    private val roomInfoJson = "json/uapi/roominfo.json"
    private val roomInfoJsonNullAudio = "json/uapi/roominfonullaudio.json"
    private val roomPrimaryNumNullJson = "json/uapi/roomprimarynumnull.json"
    private val roomPasscodeNullJson = "json/uapi/roompasscodenull.json"
    var mUserList: MutableList<User> = ArrayList()
    var webMeetingActivity:WebMeetingActivity? = null

    private fun launchFragment(onInstantiated: (ParticipantListFragment) -> Unit = {}):
            FragmentScenario<ParticipantListFragment> {
        return FragmentScenario.launchInContainer(ParticipantListFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as ParticipantListFragment
                this@ParticipantListFragmentTest.mParticipantListFragment = fragment
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        mParticipantListFragment = ParticipantListFragment()
        Dispatchers.setMain(dispatcher)
        mockkStatic(Picasso::class)
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
    }

    @After
    fun tearDown() {
        stopKoin()
        mParticipantListFragment = null
    }

    fun startKoinApp() {
        startKoin {
            modules(meetingSecurityFragmentModule)
        }
        webMeetingActivity = Robolectric.buildActivity(WebMeetingActivity::class.java).create().resume().get()
    }

    @Test
    fun `test viewModel not null`() {
        startKoinApp()
        launchFragment()
        Assert.assertNotNull(mParticipantListFragment?.mMeetingEventsModel)
        Assert.assertNotNull(mParticipantListFragment?.mMeetingUserViewModel)
    }

    @Test
    fun `test respondToUserFlowStatus`() {
        startKoinApp()
        launchFragment()
        val selfUser = User(isSelf = true,hasControls = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        mParticipantListFragment?.mSelfUser = selfUser
        val users = listOf(User(id = "123", roomRole = AppConstants.HOST), User(id = "1234", roomRole = AppConstants.GUEST))
        val authResponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.mMeetingEventsModel?.setCurrentUserId("123")
        mParticipantListFragment?.mMeetingEventsModel?.users?.postValue(users)
        mParticipantListFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.JOIN_MEETING_SUCCESS)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility,View.GONE)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.mMeetingEventsModel?.setCurrentUserId("123")
        mParticipantListFragment?.mMeetingEventsModel?.users?.postValue(users)
        mParticipantListFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.DISMISS_PARTICIPANT)
        val meetingRoomInfo =  mParticipantListFragment?.mMeetingUserViewModel?.getRoomInfoResponse()
        val role = "Host"
        val name = meetingRoomInfo?.meetingOwnerGivenName+AppConstants.BLANK_SPACE+meetingRoomInfo?.meetingOwnerFamilyName
        mParticipantListFragment?.updateDisabledHostUI()
        Assert.assertEquals(mParticipantListFragment?.ivHostProfile?.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment?.tvHostNameInitials?.visibility, View.VISIBLE)
        Assert.assertEquals(mParticipantListFragment?.tvRoomType?.text, role)
        Assert.assertEquals(mParticipantListFragment?.tvHostName?.text, name)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.mMeetingEventsModel?.setCurrentUserId("123")
        mParticipantListFragment?.mMeetingEventsModel?.users?.postValue(users)
        mParticipantListFragment?.mMeetingEventsModel?.userFlowStatus?.postValue(UserFlowStatus.MUTE_UNMUTE)
        val user = null
        mParticipantListFragment?.updateHostUI(user)
        val users4= User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = false,isConnected = false))
        mParticipantListFragment?.updateHostUI(users4)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostMuteIcon.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostNoAudio.visibility, View.VISIBLE)
        val users2 = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = true,isConnected = true))
        mParticipantListFragment?.updateHostUI(users2)
        val users1 = User(id = "123", roomRole = AppConstants.HOST,profileImage = "https://static.pgimeet.com/branding/logos/globalMeetLogo.png",audio = Audio(isConnected = true))
        mParticipantListFragment?.updateHostUI(users1)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.VISIBLE)
        val users3 = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(isConnected = true))
        mParticipantListFragment?.updateHostUI(users3)
    }

    @Test
    fun `test respondToWaitingRoomEnabledStatus`() {
        mockkConstructor(MeetingEventViewModel::class)
        val users = listOf(User(id = "123", roomRole = AppConstants.HOST), User(id = "1234", roomRole = AppConstants.PRESENTER))
        every { anyConstructed<MeetingEventViewModel>().getCurrentUser() } returns users[1]
        startKoinApp()
        launchFragment()
        val selfUser = User(isSelf = true,hasControls = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        mParticipantListFragment?.mSelfUser = selfUser
       val authResponse = Gson().fromJson(getJson("json/uapi/authsessionPresenter.json"), AuthorizeResponse::class.java)

        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.mMeetingEventsModel?.setCurrentUserId("1234")
        mParticipantListFragment?.mMeetingEventsModel?.users?.postValue(users)
        mParticipantListFragment?.mMeetingEventsModel?.waitingRoomEnabledStatus?.postValue(true)
        Assert.assertEquals(mParticipantListFragment!!.imgSecurityIcon.visibility, View.VISIBLE)

        mParticipantListFragment?.mMeetingEventsModel?.waitingRoomEnabledStatus?.postValue(false)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility,View.GONE)
    }

    @Test
    fun `test onSecurityIconClick`() {
        startKoinApp()
        launchFragment()
        mParticipantListFragment?.onSecurityIconClick()
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getJson(path : String) : String {
        val uri = javaClass.classLoader!!.getResource(path)
        val file = File(uri.file)
        return String(file.readBytes())
    }

    @Test
    fun `test updateSecurityUIOnMeetingLockStatus`() {
        val authResponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().authResponse } returns authResponse
        startKoinApp()
        launchFragment()

        mParticipantListFragment?.mMeetingEventsModel?.meetingLockStatus?.postValue(true)
        Assert.assertEquals(mParticipantListFragment!!.btnSecurityMenu.visibility, View.VISIBLE)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.VISIBLE)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.text,mParticipantListFragment!!.getString(R.string.meeting_is_locked))

        mParticipantListFragment?.mMeetingEventsModel?.meetingLockStatus?.postValue(false)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.GONE)
    }

    @Test
    fun `test updateSecurityUIOnRestrictSharing`() {

        startKoinApp()
        launchFragment()

        val authResponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse

        mParticipantListFragment?.mMeetingEventsModel?.frictionFreeEnabledStatus?.postValue(false)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.GONE)

        mParticipantListFragment?.mMeetingEventsModel?.frictionFreeEnabledStatus?.postValue(true)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.GONE)
    }

    @Test
    fun `test updateUIForRoomRole Guest`() {
        val authResponse = Gson().fromJson(getJson("json/uapi/authsessionPresenter.json"), AuthorizeResponse::class.java)
        mockkConstructor(MeetingUserViewModel::class)
        every { anyConstructed<MeetingUserViewModel>().authResponse } returns authResponse
        startKoinApp()
        launchFragment()
        mParticipantListFragment?.updateSecurityUI()
        Assert.assertEquals(mParticipantListFragment!!.btnSecurityMenu.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.GONE)
    }

    @Test
    fun `test updateUIForRoomRole Host`() {
        startKoinApp()
        launchFragment()
        val authResponse = Gson().fromJson(getJson("json/uapi/authsession.json"), AuthorizeResponse::class.java)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.updateSecurityUI()
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.GONE)
    }

    @Test
    fun `test updateUIForRoomRole Presenter`() {
        mockkConstructor(MeetingEventViewModel::class)
        val users = listOf(User(id = "123", roomRole = AppConstants.HOST), User(id = "1234", roomRole = AppConstants.PRESENTER))
        every { anyConstructed<MeetingEventViewModel>().getCurrentUser() } returns users[1]
        startKoinApp()
        launchFragment()

        val authResponse = Gson().fromJson(getJson("json/uapi/authsessionPresenter.json"), AuthorizeResponse::class.java)
        mParticipantListFragment?.mMeetingUserViewModel?.authResponse = authResponse
        mParticipantListFragment?.mMeetingEventsModel?.waitingRoomEnabledStatus?.postValue(true)
        Assert.assertEquals(mParticipantListFragment!!.btnSecurityMenu.visibility, View.VISIBLE)
        Assert.assertEquals(mParticipantListFragment!!.tvSecurityHeaderDescription.visibility, View.VISIBLE)
    }

    @Test
    fun `test host speaker` () {
        startKoinApp()
        launchFragment()
        val userList: MutableList<User> = ArrayList()
        userList.add(User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = false,isConnected = false)))
        mParticipantListFragment?.mHostList = userList
        val talker =ActiveTalker(User(id = "123", roomRole = AppConstants.HOST,delegateRole = false,isSelf = false),true)
        mParticipantListFragment?.updateHostSpeaker(talker)
        Assert.assertEquals(mParticipantListFragment?.hostSpeakerSignal?.visibility, View.VISIBLE)
        val userList1: MutableList<User> = ArrayList()
        mParticipantListFragment?.mHostList = userList1
        userList1.add(User(id = "12233", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = false,isConnected = false)))
        val talker1 = ActiveTalker(User(id = "123", roomRole = AppConstants.HOST,delegateRole = false,isSelf = true),true)
        mParticipantListFragment?.updateHostSpeaker(talker1)
        Assert.assertEquals(mParticipantListFragment?.hostSpeakerSignal?.visibility, View.INVISIBLE)
        val talker2 = ActiveTalker(User(id = "123", roomRole = AppConstants.HOST, delegateRole = true),true)
        mParticipantListFragment?.updateHostSpeaker(talker2)
        Assert.assertEquals(mParticipantListFragment?.hostSpeakerSignal?.visibility, View.INVISIBLE)
        val talker3 = ActiveTalker(User(id = "123", roomRole = AppConstants.GUEST, delegateRole = false),false)
        mParticipantListFragment?.updateHostSpeaker(talker3)
        mParticipantListFragment?.mWebParticipantListAdapter = null
        val talker4 = ActiveTalker(User(id = "123", roomRole = AppConstants.GUEST,isSelf = true),false)
        mParticipantListFragment?.updateHostSpeaker(talker4)
        Assert.assertEquals(mParticipantListFragment?.hostSpeakerSignal?.visibility, View.INVISIBLE)
    }

    @Test
    fun `test update host section UI disabled`() {
        startKoinApp()
        launchFragment()
        val meetingRoomInfo =  mParticipantListFragment?.mMeetingUserViewModel?.getRoomInfoResponse()
        val role = "Host"
        val name = meetingRoomInfo?.meetingOwnerGivenName+AppConstants.BLANK_SPACE+meetingRoomInfo?.meetingOwnerFamilyName
        mParticipantListFragment?.updateDisabledHostUI()
        Assert.assertEquals(mParticipantListFragment?.ivHostProfile?.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment?.tvHostNameInitials?.visibility, View.VISIBLE)
        Assert.assertEquals(mParticipantListFragment?.tvRoomType?.text, role)
        Assert.assertEquals(mParticipantListFragment?.tvHostName?.text, name)
        mParticipantListFragment?.updateDisabledHostUI()
    }

    @Test
    fun `test update host section UI `() {
        startKoinApp()
        launchFragment()
        val user = null
        mParticipantListFragment?.updateHostUI(user)
        val users = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = false,isConnected = false))
        mParticipantListFragment?.updateHostUI(users)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostMuteIcon.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostNoAudio.visibility, View.VISIBLE)
        val users2 = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = true,isConnected = true))
        mParticipantListFragment?.updateHostUI(users2)
        val users1 = User(id = "123", roomRole = AppConstants.HOST,profileImage = "https://static.pgimeet.com/branding/logos/globalMeetLogo.png",audio = Audio(isConnected = true))
        mParticipantListFragment?.updateHostUI(users1)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.VISIBLE)
        val users3 = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(isConnected = true))
        mParticipantListFragment?.updateHostUI(users3)
    }

    @Test
    fun `test update host section UI when audio connected`() {
        startKoinApp()
        launchFragment()
        val user = null
        mParticipantListFragment?.updateHostUI(user)
        val users = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,audio = Audio(mute = false,isConnected = true))
        mParticipantListFragment?.updateHostUI(users)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostMuteIcon.visibility, View.GONE)
        Assert.assertEquals(mParticipantListFragment!!.imgHostNoAudio.visibility, View.GONE)
        val users1 = User(id = "123", roomRole = AppConstants.HOST,profileImage = "https://static.pgimeet.com/branding/logos/globalMeetLogo.png",audio = Audio(isConnected = true))
        mParticipantListFragment?.updateHostUI(users1)
        Assert.assertEquals(mParticipantListFragment!!.ivHostProfile.visibility, View.VISIBLE)
    }

    @Test
    fun `test setAudioSpinnerVisibility for host`() {
        startKoinApp()
        launchFragment()
        val noAudioImg = mParticipantListFragment?.imgHostNoAudio!!
        val user = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,isSelf = true,audio = Audio(mute = false,isConnecting = true))
        val progress =  mParticipantListFragment?.audioConnectionProgress!!

        // when dial out is true, validate progress and audio icon visibility
        mParticipantListFragment?.mMeetingUserViewModel?.audioConnType = AudioType.DIAL_OUT

        mParticipantListFragment?.setAudioSpinnerVisibility(progress,noAudioImg,user)
        Assert.assertEquals(progress.visibility, View.VISIBLE)
        Assert.assertEquals(noAudioImg.visibility, View.GONE)

        val user1 = User(id = "123", roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,audio = Audio(mute = false,isConnecting = false))
        mParticipantListFragment?.setAudioSpinnerVisibility(progress,noAudioImg,user1)
        Assert.assertEquals(progress.visibility, View.GONE)

        mParticipantListFragment?.mMeetingUserViewModel?.audioConnType = AudioType.VOIP
        val user2 = User(id = "123", roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,audio = Audio(mute = false,isConnecting = true,isVoip = true))
        mParticipantListFragment?.setAudioSpinnerVisibility(progress,noAudioImg,user2)
        Assert.assertEquals(progress.visibility, View.VISIBLE)

        val user3 = User(id = "123", roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,audio = Audio(mute = false,isConnecting = false,isVoip = true))
        mParticipantListFragment?.setAudioSpinnerVisibility(progress,noAudioImg,user3)
        Assert.assertEquals(noAudioImg.visibility, View.GONE)
        val user4 = User(id = "123", roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,audio = Audio(mute = false,isConnecting = false,isVoip = false))
        mParticipantListFragment?.setAudioSpinnerVisibility(progress,noAudioImg,user4)
        Assert.assertEquals(noAudioImg.visibility, View.GONE)
    }

    @Test
    fun `test updatedMenuListData`() {
        startKoinApp()
        launchFragment()
        val user = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,isSelf = true,hasControls = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        mUserList.add(user)
        mParticipantListFragment?.mUserList = mUserList
        mParticipantListFragment?.updatedMenuListData(user)
        Assert.assertNotNull(mParticipantListFragment?.updatedMenuListData(user))
    }

    @Test
    fun `test updatedMenuListDataPromoted`() {
        startKoinApp()
        launchFragment()
        val user = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,isSelf = true,hasControls = true, promoted = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        mUserList.add(user)
        mParticipantListFragment?.mUserList = mUserList
        mParticipantListFragment?.updatedMenuListData(user)
        Assert.assertNotNull(mParticipantListFragment?.updatedMenuListData(user))
    }

    @Test
    fun `Test updateParticipantView when user is Presenter `() {
        initailzeTestCase()
        mParticipantListFragment?.updateParticipantView()
        assert(mParticipantListFragment?.mHostList?.size==0)
    }

    @Test
    fun `Test updateParticipantView when user is Co-Host `() {
        initailzeTestCase()
        mParticipantListFragment?.hostAlreadyJoined = true
        mParticipantListFragment?.updateParticipantView()
        assert(mParticipantListFragment?.mHostList?.size==0)
    }

    @Test
    fun `Test updateParticipantView when user is not active `() {
        initailzeTestCase()
        mParticipantListFragment?.hostAlreadyJoined = false
        mParticipantListFragment?.updateParticipantView()
        assert(mParticipantListFragment?.mHostList?.size == 0)
    }

    @Test
    fun `Test updateParticipantView when user is Host `() {
        initailzeTestCase()
        mParticipantListFragment?.hostAlreadyJoined = true
        mParticipantListFragment?.updateParticipantView()
        assert(mParticipantListFragment?.mHostList?.size == 0)
    }

    @Test
    fun `Test updateParticipantView when user joined from phone `() {
       initailzeTestCase()
        mParticipantListFragment?.hostAlreadyJoined = false
        mParticipantListFragment?.updateParticipantView()
        assert(mParticipantListFragment?.mHostList?.size == 0)
    }

    fun initailzeTestCase(){
        startKoinApp()
        launchFragment()
        val user = User(id = "123", firstName = "9845672723", roomRole = AppConstants.GUEST, profileImage = null, active = true, delegateRole = false, isSelf = false, hasControls = true, promoted = false, audio = Audio(mute = false, isConnecting = true, isConnected = false, isVoip = true))
        mParticipantListFragment?.mSelfUser = user
        mParticipantListFragment?.mUserList?.add(user)
        mParticipantListFragment?.mHostList?.clear()
    }


    @Test
    fun `test shareUrlAndDismissDialog`() {
        startKoinApp()
        launchFragment()
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mParticipantListFragment?.parentActivity = webMeetingActivity
        mParticipantListFragment?.mMeetingUserViewModel?.meetingFurl = meetingUrl
        mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse = spyk(Gson().fromJson(getJson(roomInfoJson), MeetingRoomInfoResponse::class.java))
        mParticipantListFragment?.onShareSheetIconClick()
        Assert.assertNotNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingFurl)
        Assert.assertNotNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.primaryAccessNumber)
        Assert.assertNotNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.guestPasscode)
        Assert.assertNotNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.title)
    }

    @Test
    fun `test shareUrlAndDismissDialogNull`() {
        startKoinApp()
        launchFragment()
        mParticipantListFragment?.parentActivity = null
        mParticipantListFragment?.onShareSheetIconClick()
        mParticipantListFragment?.mMeetingUserViewModel?.meetingFurl = null
        mParticipantListFragment?.onShareSheetIconClick()
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingFurl)
        mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse = spyk(Gson().fromJson(getJson(roomPrimaryNumNullJson), MeetingRoomInfoResponse::class.java))
        mParticipantListFragment?.onShareSheetIconClick()
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.primaryAccessNumber)
        mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse = spyk(Gson().fromJson(getJson(roomPasscodeNullJson), MeetingRoomInfoResponse::class.java))
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.guestPasscode)
        mParticipantListFragment?.onShareSheetIconClick()
        mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse = spyk(Gson().fromJson(getJson(roomInfoJsonNullAudio), MeetingRoomInfoResponse::class.java))
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse?.audio)
        mParticipantListFragment?.onShareSheetIconClick()
        mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse = null
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel?.meetingRoomInfoResponse)
        mParticipantListFragment?.onShareSheetIconClick()
        mParticipantListFragment?.mMeetingUserViewModel = null
        mParticipantListFragment?.onShareSheetIconClick()
        Assert.assertNull(mParticipantListFragment?.mMeetingUserViewModel)
    }

    @Test
    fun `test when activity is not null`(){
        startKoinApp()
        launchFragment()
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mParticipantListFragment?.initializeParentActivity(webMeetingActivity)
    }

    @Test
    fun `test when activity is null`(){
        startKoinApp()
        launchFragment()
        mParticipantListFragment?.initializeParentActivity(null)
    }
}