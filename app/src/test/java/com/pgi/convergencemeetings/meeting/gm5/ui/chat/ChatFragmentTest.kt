package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.landingpage.ui.SplashScreenActivity
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.di.meetingSecurityFragmentModule
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.models.Chat
import com.squareup.picasso.Picasso
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@Config(application = TestApplication::class)
@RunWith(AndroidJUnit4::class)
class ChatFragmentTest: KoinTest, RobolectricTest() {

    @ExperimentalCoroutinesApi
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val mChatList: MutableList<Chat> = mutableListOf()
    @MockK
    private var mWebMeetingActivity: WebMeetingActivity? = null
    private lateinit var mChatFragment: ChatFragment
    @MockK
    lateinit var mockMeetingEventViewModel: MeetingEventViewModel
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel

    private fun launchFragment(onInstantiated: (ChatFragment) -> Unit = {}):
            FragmentScenario<ChatFragment> {
        return FragmentScenario.launchInContainer(ChatFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as ChatFragment
                this@ChatFragmentTest.mChatFragment = fragment
                val chatItem = Chat()
                chatItem.firstName = "Test"
                chatItem.lastName = "Account"
                chatItem.message = "Hello"
                chatItem.conversationId = "12345"
                chatItem.initials = "TA"
                chatItem.profileImage = "https://test"
                chatItem.webPartId = "123456"
                this@ChatFragmentTest.mChatFragment.setChatObject(chatItem)
                val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
                mChatFragment.initializeParentActivity(webMeetingActivity)
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        mChatFragment = ChatFragment()
        Dispatchers.setMain(dispatcher)
        mockkStatic(Picasso::class)
        startKoinApp()

        val controller = Robolectric.buildActivity(WebMeetingActivity::class.java)
        mWebMeetingActivity = controller.get()
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
        coEvery { mockMeetingUserViewModel.userType } returns "GUEST"
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    fun startKoinApp() {
        startKoin {
            modules(meetingSecurityFragmentModule)
        }
    }

    @Test
    fun `test vars not null`() {
        launchFragment()
        Assert.assertNotNull(mChatFragment.mMeetingEventViewModel)
        Assert.assertNotNull(mChatFragment.mChatRecyclerView)
    }

    @Test
    fun `test updateChatTabNotificationCount`() {
        launchFragment()
        var chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.conversationId = "default"
        chat.unReadChatCount = 1
        chat.message =  "test"
        mChatFragment.setChatObject(chat)
        mChatFragment?.updateChatTabNotificationCount(chat)
        Assert.assertEquals(chat.conversationId, mChatFragment.chatInfo?.conversationId)
        Assert.assertEquals(chat.unReadChatCount, 0)
        chat.conversationId = "1234"
        mChatFragment?.updateChatTabNotificationCount(chat)
        Assert.assertEquals(chat.conversationId, mChatFragment.chatInfo?.conversationId)
        mChatFragment.chatInfo = null
        mChatFragment.updateChatTabNotificationCount(chat)
        Assert.assertNull(mChatFragment.chatInfo)
        chat.unReadChatCount = 1
        mChatFragment?.mMeetingEventViewModel?.chatsList = mChatFragment?.mMeetingEventViewModel?.chatsList + chat
        mChatFragment.mMeetingEventViewModel.chatCount = 0
        val chat1 =Chat()
        chat1.conversationId = "12345"
        chat1.message = "hi"
        chat1.unReadChatCount = 1
        mChatFragment?.mMeetingEventViewModel?.chatReceived.postValue(chat1)
        Assert.assertEquals(mChatFragment.mChatDetailBadge.visibility, View.VISIBLE)
    }

    @Test
    fun `test onChatMessageReceived`() {
        launchFragment()
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.conversationId = "12345"
        mChatList.add(0, chat)
        mChatFragment.onChatMessageReceived(mChatList)
        Assert.assertNotNull(mChatFragment.mCoversationList)
        Assert.assertNotNull(mChatFragment.chatInfo)
        Assert.assertEquals(mChatList[0].conversationId, mChatFragment.chatInfo?.conversationId)

        mChatFragment.chatInfo?.conversationId = "default"
        mChatFragment.onChatMessageReceived(mChatList)
        Assert.assertNotEquals(mChatList[0].conversationId, mChatFragment.chatInfo?.conversationId)

        mChatFragment.chatInfo =null
        mChatFragment.onChatMessageReceived(mChatList)
        Assert.assertNull(mChatFragment.chatInfo)

    }

    @Test
    fun `test reSendChatMsg`() {
        launchFragment()
        val chat = Chat()
        chat.firstName = "Test"
        chat.lastName = "Account"
        chat.message = "Hello"
        chat.conversationId = "1234"
        chat.chatMessageState = ChatMessageState.FAILED
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.reSendChatMsg(chat)
        Assert.assertEquals(chat.chatMessageState, ChatMessageState.SENDING)
        mChatFragment.chatInfo = null
        mChatFragment.mChatsAdapter = null
        mChatFragment.reSendChatMsg(chat)
        Assert.assertEquals(chat.chatMessageState, ChatMessageState.SENDING)
        Assert.assertNull(mChatFragment.chatInfo)
    }

    @Test
    fun `test showChatError`() {
        launchFragment()
        Assert.assertNotNull(mChatFragment.mMeetingEventViewModel)
        mChatFragment?.showChatError()
    }

    @Test
    fun `test respondToUserStatus`() {
        launchFragment()
        mChatFragment?.respondToUserStatus(UserFlowStatus.CHAT_ADD_FAILURE)
        Assert.assertNotNull(mChatFragment.mMeetingEventViewModel)
        mChatFragment.mMeetingEventViewModel?.updateSelfChatFailure()
    }

    @Test
    fun `test addOrRemoveTopBar`() {
        launchFragment()
        mChatFragment?.mMeetingUserViewModel?.privateChatLocked?.postValue(false)
        Assert.assertEquals(mChatFragment?.topbar.visibility, View.VISIBLE)
        mChatFragment?.mMeetingUserViewModel?.privateChatLocked?.postValue(true)
        Assert.assertEquals(mChatFragment?.topbar.visibility, View.GONE)
    }

    @Test
    fun `test onChatSendButtonClick`() {
        launchFragment()
        every {
            mockMeetingEventViewModel.getUserById(AppConstants.TEST_USER)
        } returns User(name = AppConstants.TEST_USER_ID)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment.mChatEditText.setText(AppConstants.TEST_TEXT_ONLY)
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.mChatEditText.text.toString())
        mChatFragment.mChatEditText.setText(AppConstants.TEST_URL_ONLY)
        mChatFragment.isChatDisabled = true
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.mChatEditText.text.toString())
        mChatFragment.mChatEditText.setText(AppConstants.TEST_URL_AND_MORE)
        mChatFragment.mMeetingUserViewModel.isPrivateChatLocked = true
        mChatFragment.isChatDisabled = false
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.mChatEditText.text.toString())
        mChatFragment.mChatEditText.setText(AppConstants.TEST_EMOJI_ONLY)
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.mChatEditText.text.toString())
        mChatFragment.isChatDisabled = true
        mChatFragment.onChatSendButtonClick()
        mChatFragment.mSelfParticipant = User(roomRole = AppConstants.HOST,delegateRole = false)
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.rlytParent)
        mChatFragment.mSelfParticipant = User(roomRole = AppConstants.HOST,delegateRole = true)
        mChatFragment.onChatSendButtonClick()
        Assert.assertNotNull(mChatFragment.rlytParent)
    }

    @Test
    fun `update private chat status` () {
        launchFragment()
        mChatFragment.chatInfo?.conversationId = "1234"
        mChatFragment.updatePrivateChatStatus(false)
        Assert.assertEquals(true,mChatFragment.isChatDisabled)
        mChatFragment.updatePrivateChatStatus(true)
        Assert.assertEquals(false,mChatFragment.isChatDisabled)
        mChatFragment.chatInfo?.conversationId = AppConstants.CHAT_EVERYONE
        mChatFragment.updatePrivateChatStatus(false)
        Assert.assertEquals(false,mChatFragment.isChatDisabled)
    }

    @Test
    fun `test newInstance`() {
        launchFragment()
        Assert.assertNotNull(ChatFragment.newInstance())
    }

    @Test
    fun `test onChatDetailBackBtn`() {
        launchFragment()
        Assert.assertNotNull(mWebMeetingActivity)
        mChatFragment?.onChatDetailBackBtn()
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)
        mChatFragment?.onChatDetailBackBtn()
    }

    @Test
    fun `test onChatDetailExitBtn`() {
        launchFragment()
        Assert.assertNotNull(mWebMeetingActivity)
        mChatFragment?.onChatDetailBackBtn()
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)
        mChatFragment?.onChatDetailExitBtn()
    }

    @Test
    fun `test set header view`() {
        launchFragment()
        val profilePics = mutableListOf<String?>()
        profilePics.add("https://test")
        val profileInitials = mutableListOf<String?>()
        profileInitials.add("TT")
        every {
            mockMeetingEventViewModel.getUserById(AppConstants.TEST_USER)
        } returns User(name = AppConstants.TEST_USER_ID)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment.setHeaderView()
        Assert.assertEquals(mChatFragment.mChatProfileInitial.visibility, View.VISIBLE)
        Assert.assertEquals(mChatFragment.mChatProfilePic.visibility, View.VISIBLE)
        Assert.assertEquals(mChatFragment.mChatUserType.visibility, View.VISIBLE)
    }

    @Test
    fun `test set header view with item type 0`() {
        launchFragment()
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "TA"
        chatItem.profileImage = "https://test"
        mChatFragment.setChatObject(chatItem)
        val profilePics = mutableListOf<String?>()
        profilePics.add("https://test")
        val profileInitials = mutableListOf<String?>()
        profileInitials.add("TT")
        every {
            mockMeetingEventViewModel.getUserById(AppConstants.TEST_USER)
        } returns User(name = AppConstants.TEST_USER_ID)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment.setHeaderView()
        Assert.assertEquals(mChatFragment.mAvatarView.visibility, View.VISIBLE)
    }

    @Test
    fun `test set header view with profile pic empty`() {
        launchFragment()
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "TA"
        chatItem.profileImage = ""
        mChatFragment.setChatObject(chatItem)
        val profilePics = mutableListOf<String?>()
        profilePics.add("")
        val profileInitials = mutableListOf<String?>()
        profileInitials.add("TT")
        every {
            mockMeetingEventViewModel.getUserById(AppConstants.TEST_USER)
        } returns User(name = AppConstants.TEST_USER_ID)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment.setHeaderView()
        Assert.assertEquals(mChatFragment.mChatProfileInitial.visibility, View.VISIBLE)
        Assert.assertEquals(mChatFragment.mChatProfilePic.visibility, View.INVISIBLE)
        Assert.assertEquals(mChatFragment.mChatToolBarName.text.toString(), "Test Account")
    }

    @Test
    fun `test set header view null`() {
        launchFragment()
        mChatFragment.chatInfo = null
        mChatFragment.setHeaderView()
        Assert.assertNull(mChatFragment.chatInfo)
    }

    @Test
    fun `test set header view with profile initials #`() {
        launchFragment()
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "#"
        chatItem.profileImage = ""
        mChatFragment.chatInfo = chatItem
        mChatFragment.setHeaderView()
        Assert.assertNotNull(mChatFragment.chatInfo)
    }

    @Test
    fun `test set header view private chat`() {
        launchFragment()
        coEvery { mockMeetingUserViewModel.userType } returns "GUEST"
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "1234"
        chatItem.initials = "TA"
        chatItem.profileImage = ""
        mChatFragment.setChatObject(chatItem)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment.setHeaderView()
        Assert.assertEquals(mChatFragment.mChatUserType.visibility, View.VISIBLE)
    }

    @Test
    fun `test set header null`() {
        launchFragment()
        coEvery { mockMeetingUserViewModel.userType } returns null
        mChatFragment.setHeaderView()
        Assert.assertEquals(mChatFragment.mChatUserType.visibility, View.GONE)
    }

    @Test
    fun `test onChatSendButtonClick header`() {
        launchFragment()
        every {
            mockMeetingEventViewModel?.getUserById(AppConstants.TEST_USER)
        } returns User(name = AppConstants.TEST_USER_ID)
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "#"
        chatItem.profileImage = ""
        mChatFragment.chatInfo = chatItem
        mChatFragment.setChatObject(chatItem)
        mChatFragment?.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment?.mMeetingEventViewModel = mockMeetingEventViewModel
        mChatFragment?.mChatEditText?.setText(AppConstants.TEST_TEXT_ONLY)
        Assert.assertNotNull(mChatFragment?.onChatSendButtonClick())
    }

    @Test
    fun `test chatInputFieldClick`() {
        launchFragment()
        Assert.assertNotNull(mWebMeetingActivity)
        mChatFragment.isChatDisabled = true
        mChatFragment.mChatEditText.setText("Hello")
        mChatFragment.chatInputFieldClick()
        Assert.assertEquals(mChatFragment.mChatEditText.text.toString().isNotEmpty(),true)
        Assert.assertEquals(mChatFragment.isChatDisabled,true)
        mChatFragment.isChatDisabled = false
        mChatFragment.chatInputFieldClick()
        Assert.assertEquals(mChatFragment.isChatDisabled,false)
        mChatFragment.isChatDisabled = true
        mChatFragment.mChatEditText.setText("")
        mChatFragment.chatInputFieldClick()
        Assert.assertEquals(mChatFragment.mChatEditText.text.toString().isNotEmpty(),false)
    }

    @Test
    fun `test enableDisableInputFields`() {
        launchFragment()
        mChatFragment.mChatEditText.setText("Hello")
        mChatFragment.enableDisableInputFields(true)
        Assert.assertEquals(mChatFragment.mChatEditText.isFocusable,true)
        Assert.assertEquals(mChatFragment.mChatEditText.isCursorVisible,true)
        Assert.assertEquals(mChatFragment.mChatEditText.isClickable,true)
        mChatFragment.mChatEditText.setText("")
        mChatFragment.enableDisableInputFields(true)
        Assert.assertEquals(mChatFragment.mChatEditText.isFocusable,false)
        Assert.assertEquals(mChatFragment.mChatEditText.isCursorVisible,false)
        mChatFragment.enableDisableInputFields(false)
        Assert.assertEquals(mChatFragment.mChatEditText.isFocusable,true)
        Assert.assertEquals(mChatFragment.mChatEditText.isCursorVisible,true)
        Assert.assertEquals(mChatFragment.mChatEditText.isFocusableInTouchMode,true)
        Assert.assertEquals(mChatFragment.mChatEditText.isClickable,false)
    }

    @Test
    fun `test initialize Parent Activity` () {
        launchFragment()
        val mainActivity = mockk<SplashScreenActivity>(relaxed = true)
        mChatFragment.initializeParentActivity(mainActivity)
        Assert.assertNotNull(mChatFragment.mContext)
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mChatFragment.initializeParentActivity(webMeetingActivity)
        Assert.assertNotNull(mChatFragment.parentActivity)
    }

    @Test
    fun `test showDisablePrivateChatToast`() {
        launchFragment()
        mChatFragment.showDisablePrivateChatToast()
        Assert.assertNotNull(mChatFragment.parentActivity)
    }

    @Test
    fun `test disable offline chat`() {
        launchFragment()
        var offlineChats = mutableListOf<Chat>()
        val chat1 = Chat()
        chat1.webPartId = "123456"
        offlineChats.add(chat1)
        mChatFragment.disableOfflineConversation(offlineChats)
        Assert.assertEquals(mChatFragment.chatInfo?.isOffline,true)

        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "#"
        chatItem.profileImage = ""
        chatItem.webPartId = "8cjlsjdlj"
        mChatFragment.chatInfo = chatItem
        mChatFragment.setChatObject(chatItem)
        mChatFragment.disableOfflineConversation(offlineChats)
        Assert.assertEquals(mChatFragment.chatInfo?.isOffline,false)
    }

    @Test
    fun `test check conversation exist in case of private chat version toast default true` () {
        launchFragmentCheckToast()
        every {
            mockMeetingUserViewModel.checkPrivateChatVersionToastShown(any())
        } returns true
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "default"
        chatItem.initials = "#"
        chatItem.profileImage = ""
        mChatFragment.chatInfo = chatItem
        mChatFragment.setChatObject(chatItem)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        launchFragment()
        Assert.assertNotNull(mChatFragment.chatInfo)

    }
    @Test
    fun `test check conversation exist in case of private chat version toast default false` () {
        launchFragmentCheckToast()
        every {
            mockMeetingUserViewModel.checkPrivateChatVersionToastShown(any())
        } returns false
        val chat = Chat()
        chat.firstName = "Test"
        chat.lastName = "Account"
        chat.message = "Hello"
        chat.conversationId = "default"
        chat.initials = "#"
        chat.profileImage = ""
        mChatFragment.chatInfo = chat
        mChatFragment.setChatObject(chat)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        Assert.assertNotNull(mChatFragment.chatInfo)
    }


    @Test
    fun `test check conversation exist in case of private chat version toast true` () {
        launchFragment()
        every {
            mockMeetingUserViewModel.checkPrivateChatVersionToastShown(any())
        } returns true
        val chatItem = Chat()
        chatItem.firstName = "Test"
        chatItem.lastName = "Account"
        chatItem.message = "Hello"
        chatItem.conversationId = "1234"
        chatItem.initials = "#"
        chatItem.profileImage = ""
        mChatFragment.chatInfo = chatItem
        mChatFragment.setChatObject(chatItem)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        Assert.assertNotNull(mChatFragment.chatInfo)
    }

    @Test
    fun `test check conversation exist in case of private chat version toast false` () {
        launchFragment()
        every {
            mockMeetingUserViewModel.checkPrivateChatVersionToastShown(any())
        } returns false
        val chat = Chat()
        chat.firstName = "Test"
        chat.lastName = "Account"
        chat.message = "Hello"
        chat.conversationId = "1234"
        chat.initials = "#"
        chat.profileImage = ""
        mChatFragment.chatInfo = chat
        mChatFragment.setChatObject(chat)
        mChatFragment.mMeetingUserViewModel = mockMeetingUserViewModel
        mChatFragment.mMeetingEventViewModel = mockMeetingEventViewModel
        Assert.assertNotNull(mChatFragment.chatInfo)
    }

    private fun launchFragmentCheckToast(onInstantiated: (ChatFragment) -> Unit = {}):
            FragmentScenario<ChatFragment> {
        return FragmentScenario.launchInContainer(ChatFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as ChatFragment
                this@ChatFragmentTest.mChatFragment = fragment
                val chatItem = Chat()
                chatItem.firstName = "Test"
                chatItem.lastName = "Account"
                chatItem.message = "Hello"
                chatItem.conversationId = "default"
                chatItem.initials = "TA"
                chatItem.profileImage = "https://test"
                chatItem.webPartId = ""
                this@ChatFragmentTest.mChatFragment.setChatObject(chatItem)
                val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
                mChatFragment.initializeParentActivity(webMeetingActivity)
                onInstantiated(fragment)
                return fragment
            }
        })
    }
}