package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.models.Chat
import io.mockk.MockKAnnotations
import junit.framework.Assert.*
import org.amshove.kluent.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class ChatListAdapterTest {

    private var mChatListAdapter: ChatListAdapter? = null
    private val mChatList: MutableList<Chat> = mutableListOf()
    lateinit var fragment: ChatListFragment


    @Before
    fun `set Up`() {
        MockKAnnotations.init(this, relaxed = true)
        MockitoAnnotations.initMocks(this)
        CoreApplication.appContext = ApplicationProvider.getApplicationContext()
        fragment = mock(ChatListFragment::class)
        mChatListAdapter = ChatListAdapter(CoreApplication.appContext, mChatList)
    }

    @Test
    fun `test item count`() {
        mChatListAdapter?.itemCount
        assertEquals(1, mChatListAdapter?.itemCount)
        var chat = Chat()
        chat.message = "hi"
        chat.firstName = "everyone"
        mChatList.add(0, chat)
        chat.firstName = "zafar"
        mChatList.add(1, chat)
        mChatListAdapter = ChatListAdapter(CoreApplication.appContext, mChatList)
        mChatListAdapter?.itemCount
        assertEquals(2, mChatListAdapter?.itemCount)
    }

    @Test
    fun `test get item view type`() {
        assertEquals(ChatListAdapter.Companion.TYPE_HEADER, mChatListAdapter?.getItemViewType(0))
        var chat = Chat()
        chat.message = "hi"
        chat.firstName = "everyone"
        mChatList.add(0, chat)
        mChatListAdapter = ChatListAdapter(CoreApplication.appContext, mChatList)
        assertEquals(ChatListAdapter.Companion.TYPE_ITEM, mChatListAdapter?.getItemViewType(1))
    }

    @Test
    fun `test onChatListClicked`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val everyOneHolder: ChatListAdapter.ItemViewHolder = mChatListAdapter?.onCreateViewHolder(recyclerView, 1) as ChatListAdapter.ItemViewHolder
        mChatListAdapter?.onBindViewHolder(everyOneHolder, 0)
        Assert.assertNotNull(everyOneHolder.rlytChatSingleRow)
        Assert.assertTrue(everyOneHolder.rlytChatSingleRow.visibility == View.VISIBLE)
        everyOneHolder.rlytChatSingleRow.performClick()
        Assert.assertTrue(everyOneHolder.tvChatBadgeCount.visibility == View.INVISIBLE)
        var chat = Chat()
        chat.message = "hi"
        chat.firstName = "everyone"
        chat.conversationId = "default"
        mChatList.add(0, chat)
        everyOneHolder.rlytChatSingleRow.performClick()
        Assert.assertTrue(everyOneHolder.tvChatBadgeCount.visibility == View.INVISIBLE)
    }

    @Test
    fun `test self chatItems on BindViewHolder`() {
        mChatListAdapter?.usersCount = 1
        var chat = Chat()
        chat.message = "hi"
        chat.firstName = "Imam"
        chat.initials ="TT"
        chat.conversationId = "default"
        mChatList.add(0, chat)
        chat.message = "hi"
        chat.firstName = "Zafar"
        chat.unReadChatCount = 2
        chat.initials ="TT"
        chat.conversationId = "1234"
        mChatList.add(1, chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.onBindViewHolder(holder, 1)
        Assert.assertNotEquals(chat.conversationId, AppConstants.CHAT_EVERYONE)
        assertEquals(holder.textName.text.toString(), "Zafar")
        assertEquals(holder.textLastMessage.text.toString(), "hi")
        Assert.assertTrue(holder.tvChatBadgeCount.visibility == View.VISIBLE)
        Assert.assertTrue(holder.tvChatBadgeCount.text == chat.unReadChatCount.toString())
    }

    @Test
    fun `test others chatItems on BindViewHolder`() {
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        var chat = Chat()
        chat.message = "hi"
        chat.firstName = "Everyone"
        chat.unReadChatCount = 0
        chat.conversationId = "default"
        chat.initials = "TT"
        mChatList.add(0, chat)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.textName.text.toString(), "Everyone")
        assertEquals(holder.textLastMessage.text.toString(), "hi")
        assertEquals(mChatList[0].unReadChatCount, 0)
        assertEquals(chat.conversationId, AppConstants.CHAT_EVERYONE)
        Assert.assertTrue(holder.tvChatBadgeCount.visibility == View.INVISIBLE)
        chat.message = "hi"
        chat.firstName = "Everyone"
        chat.unReadChatCount = 2
        mChatList.add(0, chat)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        Assert.assertNotEquals(mChatList[0].unReadChatCount, 0)
        Assert.assertTrue(holder.tvChatBadgeCount.visibility == View.VISIBLE)
        var chatCount = 99
        Assert.assertTrue(mChatList[0].unReadChatCount<chatCount)
        Assert.assertEquals(holder.tvChatBadgeCount.text.toString(),mChatList[0].unReadChatCount.toString())
        mChatList[0].unReadChatCount = 100
        mChatListAdapter?.onBindViewHolder(holder, 0)
        Assert.assertTrue(mChatList[0].unReadChatCount>chatCount)
        Assert.assertEquals(holder.tvChatBadgeCount.text.toString(),"99+")
    }

    @Test
    fun `test chatItems on BindViewHolder null`() {
        val chatItem = Chat()
        chatItem.message = "Hi Team"
        chatItem.firstName = "Everyone"
        chatItem.conversationId = "default"
        chatItem.initials = "SR"
        mChatListAdapter?.usersCount = 1
        mChatList.add(0, chatItem)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.textName.text.toString() , "Everyone")
    }

    @Test
    fun `test chatItems on BindViewHolder item private`() {
        val chat = Chat()
        chat.message = "Hi Team"
        chat.firstName = "Test"
        chat.conversationId = "12345"
        chat.initials = "SR"
        mChatListAdapter?.usersCount = 1
        mChatList.add(chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.textName.text.toString() , "Test")
    }

    @Test
    fun `test chatItems click`() {
        val chat = Chat()
        chat.message = "Hi Team"
        chat.firstName = "Everyone"
        chat.conversationId = "12345"
        val chat1 = Chat()
        chat1.message = "Hi Team"
        chat1.firstName = "Everyone"
        chat1.conversationId = "default"
        mChatList.add(chat)
        mChatList.add(chat1)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        holder.rlytChatSingleRow.performClick()
        assertEquals(mChatList.size , 2)
    }

    @Test
    fun `test chatItems on BindViewHolder item private #`() {
        val chat = Chat()
        chat.message = "Hi Team"
        chat.firstName = "Test"
        chat.initials ="#"
        chat.conversationId = "12345"
        mChatList.add(chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.textName.text.toString() , "Test")
    }

    @Test
    fun `test alignBadgeIcon on BindViewHolder`() {
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.alignBadgeIcon(holder)
        assertNotNull(mChatListAdapter)
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.alignBadgeIcon(holder)
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.setMarginOnBadgeIcon(holder)
        mChatListAdapter?.relativeLayoutParams =null
        assertNull(mChatListAdapter?.relativeLayoutParams)
        mChatListAdapter?.alignBadgeIcon(holder)
    }

    @Test
    fun `test setMarginOnBadgeIcon on BindViewHolder`() {
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.chat_list_single_participant_row, null, false)
        val holder = ChatListAdapter(CoreApplication.appContext, mChatList).ItemViewHolder(listItemView)
        mChatListAdapter?.setMarginOnBadgeIcon(holder)
        assertNotNull(mChatListAdapter)
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.setMarginOnBadgeIcon(holder)
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.mProfilePics?.add("pawan@globalmeet.com")
        mChatListAdapter?.setMarginOnBadgeIcon(holder)
        mChatListAdapter?.marginLayoutParams =null
        assertNull(mChatListAdapter?.marginLayoutParams)
        mChatListAdapter?.setMarginOnBadgeIcon(holder)
    }
}