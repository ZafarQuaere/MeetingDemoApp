package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.meeting.gm5.data.model.Audio
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.squareup.picasso.Picasso
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.mock
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class WebParticipantListAdapterTest {

    lateinit var mFragment: ParticipantListFragment
    lateinit var mMeetingUserViewModel: MeetingUserViewModel
    private var mAdapter: WebParticipantListAdapter? = null
    private var mUserList: ArrayList<User> = ArrayList()
    private var userId = "123"
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel
    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        MockitoAnnotations.initMocks(this)
        mFragment = mock(ParticipantListFragment::class)
        mMeetingUserViewModel = mock(MeetingUserViewModel::class)
        val user1 = User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        val user2 = User(id = "11223", firstName = "S", lastName = "B", name = "S B", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "HOST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        mUserList.add(user1)
        mUserList.add(user2)
        mAdapter = mMeetingUserViewModel?.let { WebParticipantListAdapter(it, CoreApplication.appContext, mUserList,true) }!!
        mockkStatic(Picasso::class)
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
    }

    @After
    fun tearDown() {
        mAdapter = null
        unmockkStatic(Picasso::class)
    }

    @Test
    fun `test setUser Initials and pic`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, 0) as WebParticipantListAdapter.UserViewHolder
        mAdapter?.onBindViewHolder(viewHolder, 0)
        Assert.assertNotNull(viewHolder)
        //user which have profile pic
        val user1 = User(id = "112233", firstName = "Z", lastName = "I", name = "Z I", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "GUEST", promoted = false)
        mAdapter?.setUserInitialsOrPic(viewHolder.mNameInitials, viewHolder.mProfilePic, user1)
        mAdapter?.setUserInitialsOrPic(null, viewHolder.mProfilePic, user1)
        Assert.assertNotNull(viewHolder.mNameInitials.text)
        //user with no profile image, it will go in else part for audio user
        val user2 = User(id = "1234abcd", firstName = "Z", lastName = "I", name = "Z I", initials = "setUserInitialsOrPic", email = "imam.z@hcl.com", profileImage = null, roomRole = "GUEST")
        mAdapter?.setUserInitialsOrPic(viewHolder.mNameInitials, viewHolder.mProfilePic, user2)
        mAdapter?.setUserInitialsOrPic(null, viewHolder.mProfilePic, user2)
        Assert.assertNotNull(viewHolder.mNameInitials.text)
        // user with no profile pic, joined using voip. initials will be checked here
        val user3 = User(id = "1234", firstName = "Z", lastName = "I", name = "Z I", initials = "ZI", profileImage = null, roomRole = "GUEST", audio = Audio(isVoip = true))
        mAdapter?.setUserInitialsOrPic(viewHolder.mNameInitials, viewHolder.mProfilePic, user3)
        Assert.assertEquals(viewHolder.mNameInitials.text,user3.initials)
        mAdapter?.setUserInitialsOrPic(null, viewHolder.mProfilePic, user3)
        Assert.assertNotNull(viewHolder.mNameInitials.text)

        val user4 = User(id = "1234abcd", firstName = "Z", lastName = "I", name = "Z I", initials = AppConstants.POUND_SYMBOL, email = "imam.z@hcl.com", profileImage = null, roomRole = "GUEST")
        mAdapter?.setUserInitialsOrPic(viewHolder.mNameInitials, viewHolder.mProfilePic, user4)
        mAdapter?.setUserInitialsOrPic(null, viewHolder.mProfilePic, user4)
        Assert.assertNotNull(viewHolder.mNameInitials.text)
    }


    @Test
    fun `test setAudioConnectionState for host`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, 0) as WebParticipantListAdapter.UserViewHolder
        mAdapter?.onBindViewHolder(viewHolder, 0)
        Assert.assertNotNull(viewHolder)
        val noAudioImg = viewHolder.noAudioIcon
        val progress = viewHolder.mAudioConnectingProgressBar
        val user = User(id = userId, roomRole = AppConstants.HOST, profileImage = null, isSelf = true, audio = Audio(mute = false, isConnecting = true))
        // when dial out is true, validate progress and audio icon visibility
        mAdapter?.mMeetingUserViewModel?.audioConnType = AudioType.DIAL_OUT
        mAdapter?.setAudioConnectionState(progress, noAudioImg, user)
        Assert.assertNotNull(progress)

        val user1 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, audio = Audio(mute = false, isConnecting = false))
        mAdapter?.setAudioConnectionState(progress, noAudioImg, user1)
        Assert.assertNotNull(progress)
        //for else condition
        mAdapter?.setAudioConnectionAllowed(true)
        mAdapter?.mMeetingUserViewModel?.audioConnType = AudioType.VOIP
        val user2 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, audio = Audio(mute = false, isConnecting = true, isVoip = true))
        mAdapter?.setAudioConnectionState(progress, noAudioImg, user2)
        Assert.assertNotNull(progress)

        val user3 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = false, audio = Audio(mute = false, isConnecting = false, isVoip = true))
        mAdapter?.setAudioConnectionState(progress, noAudioImg, user3)
        Assert.assertNotNull(progress)
    }

    @Test
    fun `test showPopup`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, AppConstants.ZERO) as WebParticipantListAdapter.UserViewHolder
        mAdapter?.onBindViewHolder(viewHolder, AppConstants.ZERO)
        Assert.assertNotNull(viewHolder)
        val selfUser = User(roomRole = "GUEST",isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.showPopup(AppConstants.ZERO, mUserList, selfUser)
        mAdapter?.mBottomSheetPopupDialog = null
        val selfUser1 = User(roomRole = "HOST",isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.showPopup(AppConstants.ZERO, mUserList, selfUser1)
        Assert.assertNotNull(mAdapter?.mBottomSheetPopupDialog)
        mAdapter?.showPopup(AppConstants.ZERO, mUserList, null)
        Assert.assertNotNull(mAdapter?.mBottomSheetPopupDialog)
    }

    @Test
    fun `test getMenuList`() {
        val user1 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        val selfUser = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser
        mAdapter?.getMenuList(user1)
        Assert.assertNotNull(mAdapter?.getMenuList(user1))

        val user2 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, promoted = true, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        val selfUser1 = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser1
        mAdapter?.getMenuList(user2)
        Assert.assertNotNull(mAdapter?.getMenuList(user2))

        val user3 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, promoted = false, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        val selfUser2 = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser2
        mAdapter?.getMenuList(user3)
        Assert.assertNotNull(mAdapter?.getMenuList(user3))

        val user4 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, promoted = false, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true, id = userId))
        val selfUser3 = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser3
        mAdapter?.getMenuList(user4)
        Assert.assertNotNull(mAdapter?.getMenuList(user4))
        val user5 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, promoted = false, isSelf = false, hasControls = true, initials = "RJ", audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true, id = userId))
        val selfUser4 = User(isSelf = false, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser4
        mAdapter?.getMenuList(user5)
        Assert.assertNotNull(mAdapter?.getMenuList(user5))
        val user6 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, promoted = false, isSelf = false, hasControls = true, initials = "#", audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true, id = userId))
        val selfUser5 = User(isSelf = false, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser5
        mAdapter?.getMenuList(user6)
        Assert.assertNotNull(mAdapter?.getMenuList(user6))
    }

    @Test
    fun `test getMenuList for Guest`() {
        val selfUser = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
        mAdapter?.mSelfUser = selfUser
        val user = User( isSelf = true, roomRole = AppConstants.GUEST, firstName = "Zafar", initials = "ZI")
        mAdapter?.getMenuListForGuest(user)
        Assert.assertNotNull(mAdapter?.getMenuListForGuest(user))
        val user1 = User( isSelf = false, roomRole = AppConstants.GUEST, firstName = "9876543210", initials = "ZI")
        mAdapter?.getMenuListForGuest(user1)
        Assert.assertNotNull(mAdapter?.getMenuListForGuest(user1))
        val user2 = User( isSelf = false, roomRole = AppConstants.GUEST, firstName = "Sani", initials = "san")
        mAdapter?.getMenuListForGuest(user2)
        Assert.assertNotNull(mAdapter?.getMenuListForGuest(user2))
        val user3 = User( isSelf = false, roomRole = AppConstants.GUEST, firstName = null, initials = "san")
        mAdapter?.getMenuListForGuest(user3)
        Assert.assertNotNull(mAdapter?.getMenuListForGuest(user3))
    }

    @Test
        fun `test updateGuestTalker for guest`() {
            val avail = mAdapter?.istalking
            Assert.assertTrue(avail == false)
            val context = ApplicationProvider.getApplicationContext<Context>()
            val activeTalker1 = ActiveTalker(User(id = "1234", firstName = "Z", lastName = "I", name = "Z I", initials = "ZI", profileImage = null, roomRole = "GUEST", audio = Audio(isVoip = true)), true)
            mAdapter?.updateGuestTalker(activeTalker1)
            val recyclerView = RecyclerView(context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, 0) as WebParticipantListAdapter.UserViewHolder
            mAdapter?.onBindViewHolder(viewHolder, 0)
            Assert.assertNotNull(viewHolder)
            mAdapter?.notifyDataSetChanged()
            val activeTalker2 = ActiveTalker(User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "ZI", profileImage = null, roomRole = "GUEST", audio = Audio(isVoip = true)), false)
            mAdapter?.updateGuestTalker(activeTalker2)
            mAdapter?.onBindViewHolder(viewHolder, 0)
            Assert.assertNotNull(viewHolder)
            mAdapter?.notifyDataSetChanged()
            val activeTalker3 = ActiveTalker(User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "ZI", profileImage = null, roomRole = "GUEST", audio = Audio(isVoip = true)), true)
            mAdapter?.updateGuestTalker(activeTalker3)
            mAdapter?.onBindViewHolder(viewHolder, 0)
            Assert.assertNotNull(viewHolder)
            mAdapter?.notifyDataSetChanged()
            val activeTalker4 = ActiveTalker(User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "ZI", profileImage = null, roomRole = "GUEST", audio = Audio(isVoip = true)), false)
            mAdapter?.updateGuestTalker(activeTalker4)
            mAdapter?.onBindViewHolder(viewHolder, 0)
            Assert.assertNotNull(viewHolder)
            mAdapter?.notifyDataSetChanged()
            val user5 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, hasControls = false, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            val selfUser4 = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mAdapter?.mSelfUser = selfUser4
            mAdapter?.getMenuList(user5)
            Assert.assertNotNull(mAdapter?.getMenuList(user5))
        }

        @Test
        fun `test onBindViewHolder`() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val recyclerView = RecyclerView(context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, AppConstants.ZERO) as WebParticipantListAdapter.UserViewHolder
            mAdapter?.onBindViewHolder(viewHolder, AppConstants.ZERO)
            Assert.assertNotNull(viewHolder)
            val selfUser = User(roomRole = "GUEST",isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mAdapter?.showPopup(AppConstants.ZERO, mUserList, selfUser)
            Assert.assertNotNull(mAdapter?.mBottomSheetPopupDialog)
        }

        @Test
        fun `test  setHostUser true`() {
            var mUserList: MutableList<User> = ArrayList()
            val user1 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mUserList.add(user1)
            mAdapter?.setHostUser(mUserList)
            Assert.assertTrue(mUserList.size > 0)
        }

        @Test
        fun `test  setHostUser false`() {
            var mUserList: MutableList<User> = ArrayList()
            val user1 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = false, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mUserList.add(user1)
            mAdapter?.setHostUser(mUserList)
            Assert.assertTrue(mUserList.size > 0)
        }

        @Test
        fun `test  getHostControlsVisibility`() {
            val user = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            val selfUser = User(isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mAdapter?.mSelfUser = selfUser
            mUserList.add(user)
            mAdapter?.getHostControlsVisibility(user, AppConstants.FOUR)
            Assert.assertTrue(mAdapter?.getHostControlsVisibility(user, AppConstants.FOUR)!!)

            val user1 = User(id = userId, roomRole = AppConstants.GUEST, profileImage = null, isSelf = true, hasControls = true, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            val selfUser1 = User(isSelf = true, hasControls = false, audio = Audio(mute = false, isConnecting = false, isConnected = true, isVoip = true))
            mAdapter?.mSelfUser = selfUser1
            mUserList.add(user1)
            mAdapter?.getHostControlsVisibility(user, AppConstants.FOUR)
            Assert.assertTrue(mAdapter?.getHostControlsVisibility(user, AppConstants.FOUR)!!)
            mAdapter?.getHostControlsVisibility(user, AppConstants.ZERO)
            Assert.assertFalse(mAdapter?.getHostControlsVisibility(user, AppConstants.ZERO)!!)
        }

        @Test
        fun `test  getAudioUserControls`() {
            val user = User(id = userId, roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,hasControls = true,audio = Audio(mute = true,isConnecting = false,isConnected = true,isVoip = true))
            mAdapter?.getAudioUserControls(user)
            Assert.assertNotNull(mAdapter?.getAudioUserControls(user))

            val user1 = User(id = userId, roomRole = AppConstants.GUEST,profileImage = null,isSelf = true,hasControls = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
            mAdapter?.getAudioUserControls(user1)
            Assert.assertNotNull(mAdapter?.getAudioUserControls(user1))
        }

    @Test
    fun `test on Host Control clicked when private chat` () {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val viewHolder: WebParticipantListAdapter.UserViewHolder = mAdapter?.onCreateViewHolder(recyclerView, AppConstants.ONE) as WebParticipantListAdapter.UserViewHolder
        mAdapter?.onBindViewHolder(viewHolder, AppConstants.ONE)
        every {
            mockMeetingUserViewModel.isOpenChatFragment.value
        } returns false
        mAdapter?.mMeetingUserViewModel = mockMeetingUserViewModel
        viewHolder.onHostControlClicked()
        Assert.assertEquals(mAdapter?.mIsPrivateChat,true)
    }

}