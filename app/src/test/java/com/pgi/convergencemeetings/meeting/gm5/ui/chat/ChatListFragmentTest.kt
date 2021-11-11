package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.landingpage.ui.SplashScreenActivity
import com.pgi.convergencemeetings.meeting.gm5.data.model.Audio
import com.pgi.convergencemeetings.meeting.gm5.di.meetingSecurityFragmentModule
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.ParticipantsOrder
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
class ChatListFragmentTest: KoinTest, RobolectricTest(){

    @MockK
    private var mWebMeetingActivity: WebMeetingActivity? = null
    @ExperimentalCoroutinesApi
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val mChatList: MutableList<Chat> = mutableListOf()
    private val conversationList: MutableList<Chat> = mutableListOf()

    private lateinit var mChatListFragment: ChatListFragment

    private fun launchFragment(onInstantiated: (ChatListFragment) -> Unit = {}):
            FragmentScenario<ChatListFragment> {
        return FragmentScenario.launchInContainer(ChatListFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as ChatListFragment
                this@ChatListFragmentTest.mChatListFragment = fragment
                mChatListFragment?.mConvId = null
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this,relaxed = true)
        mChatListFragment = ChatListFragment()
        Dispatchers.setMain(dispatcher)
        mockkStatic(Picasso::class)
        startKoinApp()
        val controller = Robolectric.buildActivity(WebMeetingActivity::class.java)
        mWebMeetingActivity = controller.get()
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
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
        Assert.assertNotNull(mChatListFragment.mMeetingUserViewModel)
        Assert.assertNotNull(mChatListFragment.mMeetingEventViewModel)
        Assert.assertNotNull(mChatListFragment.mChatRecyclerView)
        Assert.assertNotNull(mChatListFragment.chatListAdapter)
        Assert.assertNotNull(mWebMeetingActivity)
        Assert.assertNotNull(mChatListFragment.mContext)
    }

    @Test
    fun `test onShowDetailPage`() {
        launchFragment()
        val chatItem = Chat()
        chatItem.firstName = "everyone"
        chatItem.message = "Hi everyone"
        chatItem.initials = "TT"
        chatItem.conversationId ="default"
        chatItem.webPartId ="12345"
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)
        mChatListFragment.mConvId ="default"
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mChatListFragment.initializeParentActivity(webMeetingActivity)
        Assert.assertNotNull(mChatListFragment.mConvId)
        mChatListFragment.mConvId?.let { mChatListFragment.onShowChatDetailPage(chatItem) }
        chatItem.conversationId = "12345"
        mChatListFragment.onShowChatDetailPage(chatItem)
        Assert.assertNotNull(mChatListFragment.mConvId)
    }

    @Test
    fun `test add item to list` (){
        launchFragment()
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.initials = "PU"
        chat.conversationId = "default"
        mChatList.add(0, chat)
        val chat1 = Chat()
        chat1.firstName = "PGI"
        chat1.lastName = "UT"
        chat1.conversationId = "12345"
        chat1.initials = "PU"
        mChatList.add(1, chat1)
        Assert.assertNotNull(mChatList)
        mChatListFragment.addItemToList(mChatList)
        Assert.assertNotNull(mChatListFragment.mConversationList)
    }

    @Test
    fun `test updateChatTabNotificationCount`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.conversationId = "default"
        chat.unReadChatCount = 1
        chat.message =  "test"
        mChatListFragment?.updateChatTabNotificationCount(chat)
        Assert.assertTrue(mChatListFragment.mConversationList.isEmpty())
        mChatListFragment.mConversationList.add(0, chat)
        mChatListFragment?.updateChatTabNotificationCount(chat)
        Assert.assertNull(mChatListFragment.mConvId)
        Assert.assertNotEquals(chat.conversationId, mChatListFragment.mConvId)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        //in case of everyone chat- convid will be default
        mChatListFragment?.mConvId = "default"
        mChatListFragment?.updateChatTabNotificationCount(chat)
        Assert.assertNotNull(mChatListFragment.mConvId)
        Assert.assertEquals(chat.conversationId, mChatListFragment.mConvId)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(chat.conversationId, mChatListFragment.mConversationList[0].conversationId)
        val chat1 = Chat()
        chat.conversationId = "1234"
        chat.unReadChatCount = 1
        mChatListFragment.mConversationList.clear()
        mChatListFragment.mConversationList.add(0, chat1)
        mChatListFragment?.updateChatTabNotificationCount(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
    }

    @Test
    fun `test updateChatUnreadCount` () {
        launchFragment()
        val convId = "12345"
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.initials = "PU"
        chat.conversationId = "default"
        val chat1 = Chat()
        chat1.firstName = "PGI"
        chat1.lastName = "UT"
        chat1.conversationId = "12345"
        chat1.initials = "PU"
        chat1.unReadChatCount = 0
        mChatListFragment.mConversationList.add(0, chat)
        mChatListFragment.mConversationList.add(1, chat1)
        mChatListFragment?.updateChatUnreadCount(convId)
        Assert.assertEquals(mChatListFragment.mConversationList[1].unReadChatCount,0)
    }

    @Test
    fun `test update everyone row UI` (){
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chat = Chat()
        chat.message = "Hello Team"
        chat.timestamp = "05:05 PM"
        chat.conversationId = "default"
        chat.initials = "TT"
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        mChatListFragment.mConversationList.add(0,chat)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertEquals(2, mChatListFragment.mConversationList.size)
        mChatListFragment.chatListAdapter = null
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNull(mChatListFragment.chatListAdapter)
    }

    @Test
    fun `test Add or Remove fragment` () {
        launchFragment()
        mChatListFragment.addOrRemoveFragment(true)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(), "New Chat")
        Assert.assertEquals(mChatListFragment.ivCancelFragment.visibility, View.VISIBLE)
        mChatListFragment.addOrRemoveFragment(false)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(), "Chats")
        Assert.assertEquals(mChatListFragment.ivCancelFragment.visibility, View.GONE)
    }

    @Test
    fun `test update everyone with userlist` (){
        launchFragment()
        val chat = Chat()
        chat.message = "Hello Team"
        chat.timestamp = "2021-08-17T10:01:43.411Z"
        chat.conversationId = "default"
        mChatListFragment.mUsers.add(User(profileImage = "",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.chatListAdapter?.updateUserCount(mChatListFragment.mUsers)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(1,mChatListFragment.mConversationList.size)
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.chatListAdapter?.updateUserCount(mChatListFragment.mUsers)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(1,mChatListFragment.mConversationList.size)
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.chatListAdapter?.updateUserCount(mChatListFragment.mUsers)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(1,mChatListFragment.mConversationList.size)
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.chatListAdapter?.updateUserCount(mChatListFragment.mUsers)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(1,mChatListFragment.mConversationList.size)
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "https://test",initials = "#",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = null,initials = null,timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.mUsers.add(User(profileImage = "",initials = "TT",timestamp = "2021-08-17T10:01:43.411Z"))
        mChatListFragment.chatListAdapter?.updateUserCount(mChatListFragment.mUsers)
        mChatListFragment.updateEveryOneChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(1,mChatListFragment.mConversationList.size)
    }

    @Test
    fun `test add item to list private chat` (){
        launchFragment()
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        chat.conversationId = "12345"
        chat.webPartId = "12345"
        chat.initials = "TT"
        mChatList.add(chat)
        mChatListFragment.addItemToList(mChatList)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        val chatItems1 = Chat()
        chatItems1.firstName = "EveryOne"
        chatItems1.conversationId = AppConstants.CHAT_EVERYONE
        chatItems1.initials = "TT"
        val chatItems2 = Chat()
        chatItems2.firstName = "EveryOne"
        chatItems2.conversationId = AppConstants.CHAT_EVERYONE
        chatItems2.webPartId = "12345"
        chatItems2.initials = "TT"
        mChatListFragment.mConversationList.add(chatItems1)
        mChatListFragment.mConversationList.add(chatItems2)
        mChatListFragment.updatePrivateChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
        Assert.assertEquals(mChatListFragment.updatePosition ,1)

        mChatListFragment.chatListAdapter = null
        mChatListFragment.updatePrivateChat(chat)
        Assert.assertNotNull(mChatListFragment.mConversationList)
    }

    @Test
    fun `test clear group chat`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        mChatListFragment.clearGroupChat()
        Assert.assertEquals(0, mChatListFragment.mConversationList.size)
        val chat = Chat()
        chat.firstName = "everyone"
        chat.message = "Hi everyone"
        chat.timestamp = "05:05 PM"
        chat.conversationId = "default"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        mChatListFragment.clearGroupChat()
        Assert.assertEquals(mChatListFragment.mConversationList[0].timestamp, "")
        chat.conversationId = "default"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        mChatListFragment.clearGroupChat()
        Assert.assertEquals(2, mChatListFragment.mConversationList.size)
        mChatListFragment.chatListAdapter = null
        mChatListFragment.clearGroupChat()
        Assert.assertNull(mChatListFragment.chatListAdapter)
    }

    @Test
    fun `test add default`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        mChatListFragment.addDefaultChat()
        Assert.assertEquals(1, mChatListFragment.mConversationList.size)
    }

    @Test
    fun `test check Conversation`() {
        launchFragment()
        val chat = Chat()
        chat.firstName = "everyone"
        chat.message = "Hi everyone"
        chat.timestamp = "05:05 PM"
        chat.conversationId = "default"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        var chat1 = Chat()
        chat1.firstName = "RJ"
        chat1.message = "Hi"
        chat1.timestamp = "05:05 PM"
        chat1.conversationId = "1234"
        mChatListFragment.checkConversation(chat1)
        Assert.assertNotEquals(chat1.conversationId, mChatListFragment.mConversationList[0].conversationId)
        conversationList[0].conversationId = "1234"
        mChatListFragment.checkConversation(chat1)
        Assert.assertEquals(chat1.conversationId, mChatListFragment.mConversationList[0].conversationId)
    }

    @Test
    fun `test update private chat no conversation started`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chat = Chat()
        chat.firstName = "everyone"
        chat.message = "Hi everyone"
        chat.timestamp = "05:05 PM"
        chat.conversationId = "default"
        val chatItems2 = Chat()
        chatItems2.firstName = "EveryOne"
        chatItems2.conversationId = AppConstants.CHAT_EVERYONE
        chatItems2.webPartId = "12345"
        chatItems2.initials = "TT"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        mChatListFragment.updatePrivateChat(chatItems2)
        Assert.assertEquals(2, mChatListFragment.mConversationList.size)
    }

    @Test
    fun `test update UI ` () {
        launchFragment()
        mChatListFragment.updateView(true)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(), "New Chat")
        Assert.assertEquals(mChatListFragment.mChatRecyclerView.visibility, View.GONE)
        Assert.assertEquals(mChatListFragment.ivAddChat.visibility, View.GONE)
        Assert.assertEquals(mChatListFragment.textChatCount.visibility, View.GONE)
        Assert.assertEquals(mChatListFragment.ivChatMenu.visibility, View.GONE)
        Assert.assertEquals(mChatListFragment.ivCancelFragment.visibility, View.VISIBLE)
        mChatListFragment.updateView(false)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(), "Chats")
        Assert.assertEquals(mChatListFragment.mChatRecyclerView.visibility, View.VISIBLE)
        Assert.assertEquals(mChatListFragment.ivAddChat.visibility, View.VISIBLE)
        Assert.assertEquals(mChatListFragment.textChatCount.visibility, View.VISIBLE)
        Assert.assertEquals(mChatListFragment.ivCancelFragment.visibility, View.GONE)
        val user = User(roomRole = AppConstants.HOST,delegateRole = false)
        mChatListFragment.user = user
        mChatListFragment.updateView(false)
        Assert.assertEquals(mChatListFragment.ivChatMenu.visibility, View.VISIBLE)
        val user1 = User(roomRole = AppConstants.HOST,delegateRole = true)
        mChatListFragment.user = user1
        mChatListFragment.updateView(false)
        Assert.assertEquals(mChatListFragment.ivChatMenu.visibility, View.VISIBLE)
        val user2 = User(roomRole = AppConstants.GUEST,delegateRole = false)
        mChatListFragment.user = user2
        mChatListFragment.updateView(false)
        Assert.assertEquals(mChatListFragment.ivChatMenu.visibility, View.GONE)
    }

    @Test
    fun `test updateChatWithUserRole`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val user1 = User( roomRole = AppConstants.GUEST, firstName = "Test", lastName = "Account",isSelf = true, initials = "#", id = "12345")
        mChatListFragment.mUsers.add(user1)
        val chat = Chat()
        chat.firstName = "Test"
        chat.firstName = "Account"
        chat.conversationId = "2345"
        chat.webPartId = "12345"
        chat.initials = "TT"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        mChatListFragment.updateChatWithUserRole(chat.webPartId)
        Assert.assertEquals("Guest", mChatListFragment.mMeetingUserViewModel?.userType)
        val chat1 = Chat()
        chat1.firstName = "Test"
        chat1.firstName = "Account"
        chat1.conversationId = "2345"
        chat1.webPartId = "12345789"
        chat1.initials = "TT"
        conversationList.add(chat)
        mChatListFragment.mConversationList = conversationList
        mChatListFragment.updateChatWithUserRole(chat.webPartId)
        Assert.assertNotNull(mChatListFragment.mConversationList)
    }

    @Test
    fun `test order everyone to top`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chatItem1 = Chat()
        chatItem1.firstName = "everyone"
        chatItem1.message = "Hi everyone"
        chatItem1.timestamp = "05:05 PM"
        chatItem1.conversationId = "1234"
        chatItem1.webPartId = "1234"
        chatItem1.initials = "TT"
        val chatItem2 = Chat()
        chatItem2.firstName = "everyone"
        chatItem2.message = "Hi everyone"
        chatItem2.timestamp = "05:05 PM"
        chatItem2.conversationId = "3456"
        chatItem2.webPartId = "3456"
        chatItem2.initials = "TT"
        val chatItem3 = Chat()
        chatItem3.firstName = "everyone"
        chatItem3.message = "Hi everyone"
        chatItem3.timestamp = "05:05 PM"
        chatItem3.conversationId = "default"
        chatItem2.initials = "TT"
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.mConversationList.add(chatItem2)
        mChatListFragment.mConversationList.add(chatItem3)
        mChatListFragment.orderEveryOneChat()
        val zerothItem = mChatListFragment.mConversationList[0]
        Assert.assertEquals("everyone", zerothItem.firstName)
        Assert.assertEquals("Hi everyone", zerothItem.message)
        Assert.assertEquals("default", zerothItem.conversationId)
        Assert.assertEquals(3, mChatListFragment.mConversationList.size)
        mChatListFragment.removeFragmentCallback(false)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(),mChatListFragment.mContext?.getString(R.string.tab_text_chat))
    }

    @Test
    fun `test remove fragment callback`() {
        launchFragment()
        mChatListFragment.removeFragmentCallback(false)
        Assert.assertEquals(mChatListFragment.textChatLabel.text.toString(),mChatListFragment.mContext?.getString(R.string.tab_text_chat))
    }

    @Test
    fun `test update users list`() {
        launchFragment()
        val userList = mutableListOf<User>()
        val audio = Audio()
        audio.id = "1234"
        userList.add(User(profileImage = "https://test", initials = "TT", id = "1234", audio = audio,firstName = "Test"))
        userList.add(User(profileImage = "https://test",initials = "TT", id = "1234", audio = audio,firstName = "123456"))
        userList.add(User(profileImage = "https://test",initials = "#"))
        userList.add(User(profileImage = null,initials = null))
        userList.add(User(profileImage = "",initials = "TT"))
        mChatListFragment.updateUserList(userList)
        Assert.assertEquals(1, mChatListFragment.mUsers.size)
    }

    @Test
    fun `test open chat disable dialog` () {
        launchFragment()
        mChatListFragment.openChatEnableDisableDialog()
        Assert.assertEquals(Lifecycle.State.RESUMED,mChatListFragment.lifecycle.currentState)
        mChatListFragment.isChatEnabled = true
        mChatListFragment.openChatEnableDisableDialog()
        Assert.assertEquals(Lifecycle.State.RESUMED,mChatListFragment.lifecycle.currentState)
    }

    @Test
    fun `test updatePrivateChatStatus` () {
        launchFragment()
        mChatListFragment.mMeetingEventViewModel.privateChatEnableStatus.value = true
        mChatListFragment.updatePrivateChatStatus(true)
        Assert.assertEquals(mChatListFragment.mMeetingEventViewModel.privateChatEnableStatus.value,true)
        val mUserList: MutableList<User> = ArrayList()
        val user1 = User( roomRole = AppConstants.GUEST, firstName = "Zafar", isSelf = false, initials = "ZI",timestamp = "2021-08-17T10:01:43.411Z")
        mUserList.add(user1)
        mChatListFragment.mMeetingEventViewModel.users.value = mUserList
        mChatListFragment.updatePrivateChatStatus(true)
        val user2 = User( roomRole = AppConstants.GUEST, firstName = "Imam", isSelf = false, initials = "ZI",timestamp = "2021-08-17T10:01:43.411Z")
        mUserList.add(user2)
        mChatListFragment.mMeetingEventViewModel.users.value = mUserList
        mChatListFragment.updatePrivateChatStatus(true)
        Assert.assertNotNull(mChatListFragment.mMeetingEventViewModel.users)
        mChatListFragment.updatePrivateChatStatus(false)
        Assert.assertNotNull(mChatListFragment.mMeetingEventViewModel.users)
        mChatListFragment.user = user2
        Assert.assertNotNull(mChatListFragment.user)
    }

    @Test
    fun ` test showPrivateChatDisableNotification` () {
        launchFragment()
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mChatListFragment.initializeParentActivity(webMeetingActivity)
        mChatListFragment.mMeetingUserViewModel.privateChatEnableStatus.value = true
        mChatListFragment.showPrivateChatDisableNotification(true)
        Assert.assertEquals(mChatListFragment.mMeetingUserViewModel.privateChatEnableStatus?.value,true)
        mChatListFragment.mMeetingUserViewModel.privateChatEnableStatus.value = false
        mChatListFragment.showPrivateChatDisableNotification(false)
        Assert.assertEquals(mChatListFragment.mMeetingUserViewModel.privateChatEnableStatus?.value,false)
    }

    @Test
    fun ` test update ChatOnCleared Event` () {
        launchFragment()
        var convId="default"
        val chatItem1 = Chat()
        chatItem1.firstName = "everyone"
        chatItem1.message = "Hi everyone"
        chatItem1.timestamp = "05:05 PM"
        chatItem1.conversationId = "default"
        chatItem1.webPartId = "1234"
        chatItem1.initials = "TT"
        val chatItem2 = Chat()
        chatItem2.firstName = "rj"
        chatItem2.message = "Hi"
        chatItem2.timestamp = "05:05 PM"
        chatItem2.conversationId = "3456"
        chatItem2.webPartId = "3456"
        chatItem2.initials = "TT"
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.mConversationList.add(chatItem2)
        mChatListFragment.updateChatOnClearedEvent(convId)
        Assert.assertEquals(mChatListFragment.mConversationList[0].timestamp, "")
    }

    @Test
    fun `test initialize Parent Activity` () {
        launchFragment()
        val mainActivity = mockk<SplashScreenActivity>(relaxed = true)
        mChatListFragment.initializeParentActivity(mainActivity)
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mChatListFragment.initializeParentActivity(webMeetingActivity)
        Assert.assertNotNull(mChatListFragment.parentActivity)
    }

    @Test
    fun `test move recent private chat`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chatItem1 = Chat()
        chatItem1.firstName = "everyone"
        chatItem1.message = "Hi everyone"
        chatItem1.timestamp = "05:05 PM"
        chatItem1.conversationId = "default"
        chatItem1.webPartId = "1234"
        chatItem1.initials = "TT"
        val chatItem2 = Chat()
        chatItem2.firstName = "everyone"
        chatItem2.message = "Hi everyone"
        chatItem2.timestamp = "05:05 PM"
        chatItem2.conversationId = "3456"
        chatItem2.webPartId = "3456"
        chatItem2.initials = "TT"
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.mConversationList.add(chatItem2)
        mChatListFragment.movePrivateChat(1)
        Assert.assertEquals(mChatListFragment.mConversationList.size , 2)
    }

    @Test
    fun `test update offline conversation`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chatItem1 = Chat()
        chatItem1.firstName = "everyone"
        chatItem1.message = "Hi everyone"
        chatItem1.timestamp = "05:05 PM"
        chatItem1.offlineTimestamp = "05:05 PM"
        chatItem1.conversationId = "default"
        chatItem1.webPartId = "1234"
        chatItem1.initials = "TT"
        val chatItem2 = Chat()
        chatItem2.firstName = "everyone"
        chatItem2.message = "Hi everyone"
        chatItem2.timestamp = "05:05 PM"
        chatItem2.conversationId = "3456"
        chatItem2.webPartId = "3456"
        chatItem2.initials = "TT"
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.mConversationList.add(chatItem2)
        mChatListFragment.offlineConversationList.clear()
        mChatListFragment.offlineConversationList.add(chatItem1)
        mChatListFragment.offlineConversationList.add(chatItem2)
        mChatListFragment.updateOfflineConversation()
        Assert.assertTrue(mChatListFragment.mConversationList[1].isOffline)

        chatItem2.webPartId = "xyawww"
        mChatListFragment.offlineConversationList.clear()
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.offlineConversationList.add(chatItem2)
        mChatListFragment.updateOfflineConversation()
        Assert.assertTrue(mChatListFragment.offlineConversationList.isNotEmpty())
    }

    @Test
    fun `test sort and order conversation`() {
        launchFragment()
        mChatListFragment.mConversationList.clear()
        val chatItem1 = Chat()
        chatItem1.firstName = "everyone"
        chatItem1.message = "Hi everyone"
        chatItem1.timestamp = "05:05 PM"
        chatItem1.offlineTimestamp = "05:05 PM"
        chatItem1.conversationId = "default"
        chatItem1.webPartId = "1234"
        chatItem1.initials = "TT"
        chatItem1.isOffline = false
        val chatItem2 = Chat()
        chatItem2.firstName = "everyone"
        chatItem2.message = "Hi everyone"
        chatItem2.timestamp = "05:05 PM"
        chatItem2.offlineTimestamp = "05:06 PM"
        chatItem2.conversationId = "3456"
        chatItem2.webPartId = "3456"
        chatItem2.initials = "TT"
        chatItem1.isOffline = true
        mChatListFragment.mConversationList.add(chatItem1)
        mChatListFragment.mConversationList.add(chatItem2)
        mChatListFragment.sortAndOrderConversation()
        Assert.assertTrue(mChatListFragment.mConversationList[mChatListFragment.mConversationList.size-1].isOffline)

        mChatListFragment.mConversationList.clear()
        mChatListFragment.sortAndOrderConversation()
        Assert.assertTrue(mChatListFragment.mConversationList.isEmpty())

        mChatListFragment.mConversationList.clear()
        mChatListFragment.chatListAdapter = null
        mChatListFragment.sortAndOrderConversation()
        Assert.assertTrue(mChatListFragment.mConversationList.isEmpty())
    }
}