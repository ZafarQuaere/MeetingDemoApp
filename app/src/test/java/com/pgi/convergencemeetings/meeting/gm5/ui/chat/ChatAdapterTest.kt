package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.models.Chat
import io.mockk.MockKAnnotations
import junit.framework.Assert.assertEquals
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class ChatAdapterTest {

    private var mChatsAdapter: ChatsAdapter<*>? = null
    private val mCoversationList: MutableList<Chat> = mutableListOf()
    lateinit var fragment: ChatFragment


    @Before
    fun `set Up`() {
        MockKAnnotations.init(this, relaxed = true)
        MockitoAnnotations.initMocks(this)
        CoreApplication.appContext = ApplicationProvider.getApplicationContext()
        fragment = mock(ChatFragment::class)
        mChatsAdapter = ChatsAdapter(CoreApplication.appContext, mCoversationList)
    }

    @Test
    fun `test item count`() {
        mChatsAdapter?.itemCount
        assertEquals(1, mChatsAdapter?.itemCount)
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "UT"
        mCoversationList.add(0, chat)
        mChatsAdapter = ChatsAdapter(CoreApplication.appContext, mCoversationList)
        mChatsAdapter?.itemCount
        assertEquals(1, mChatsAdapter?.itemCount)
    }

    @Test
    fun `test get item view type`() {
        assertEquals(ChatsAdapter.Companion.NO_CHAT_ITEM, mChatsAdapter?.getItemViewType(0))
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "Test"
        chat.message = "Hello PGI"
        mCoversationList.add(0, chat)
        mChatsAdapter = ChatsAdapter(CoreApplication.appContext, mCoversationList)
        assertEquals(ChatsAdapter.Companion.CHAT_ITEM, mChatsAdapter?.getItemViewType(1))
    }

    @Test
    fun `test self chat on BindViewHolder`() {
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "Test"
        chat.message = "Hello PGI"
        chat.initials = "Z I"
        chat.chatMessageState = ChatMessageState.RECEIVED
        chat.isSelf = true
        chat.isHostOrCoHost = true
        mCoversationList.add(0, chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.fragment_chat_item, null, false)
        val holder = ChatsAdapter(CoreApplication.appContext, mCoversationList).ChatViewHolder(listItemView)
        mChatsAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.mContentTextView.text.toString(), "Hello PGI")
        assertEquals(holder.mNameInitialsTextView.text.toString(), "Z I")
    }

    @Test
    fun `test chat failure on BindViewHolder`() {
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "Test"
        chat.message = "Hello PGI"
        chat.initials = "Z I"
        chat.chatMessageState = ChatMessageState.FAILED
        mCoversationList.add(0, chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.fragment_chat_item, null, false)
        val holder = ChatsAdapter(CoreApplication.appContext, mCoversationList).ChatViewHolder(listItemView)
        mChatsAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.mContentTextView.text.toString(), "Hello PGI")
        assertEquals(holder.mNameInitialsTextView.text.toString(), "Z I")
        chat.conversationId = "default"
        mCoversationList.add(0, chat)
        mChatsAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.mContentTextView.text.toString(), "Hello PGI")
        assertEquals(holder.mNameInitialsTextView.text.toString(), "Z I")
        assertEquals(holder.mSubContentTextView.text.toString(), chat.chatMessageState.value)
    }

    @Test
    fun `test others chat on BindViewHolder`() {
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "Test"
        chat.message = "Hello PGI"
        chat.initials = "Z I"
        chat.chatMessageState = ChatMessageState.SENDING
        chat.isSelf = false
        chat.isHostOrCoHost = false
        mCoversationList.add(0, chat)
        val inflater = CoreApplication.appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.fragment_chat_item, null, false)
        val holder = ChatsAdapter(CoreApplication.appContext, mCoversationList).ChatViewHolder(listItemView)
        mChatsAdapter?.onBindViewHolder(holder, 0)
        assertEquals(holder.mContentTextView.text.toString(), "Hello PGI")
        assertEquals(holder.mNameInitialsTextView.text.toString(), "Z I")
    }

    @Test
    fun `test subcontentText name and date`() {
        val chat = Chat()
        chat.firstName = "PGI"
        chat.lastName = "Test"
        chat.chatMessageState = ChatMessageState.SENDING
        chat.isSelf = false
        chat.timestamp = "2018-10-10T18:43:13.821Z"
        val name = chat.firstName + AppConstants.BLANK_SPACE + chat.lastName
        val nameNdate = String.format("%s %s %s", name, CoreApplication.appContext.getString(R.string.chat_sub_content_bullet), chat.timestamp)
        val nameAndDate = mChatsAdapter?.getNameAndDate(chat)
        assertEquals(nameAndDate, nameNdate)
        val chat1 = Chat()
        chat1.firstName = "PGI"
        chat1.lastName = "Test"
        chat1.chatMessageState = ChatMessageState.SENDING
        chat1.isSelf = true
        chat1.timestamp = "2018-10-10T18:43:13.821Z"
        val name1 = "You"
        val nameNdate1 = String.format("%s %s %s", name1, CoreApplication.appContext.getString(R.string.chat_sub_content_bullet), chat.timestamp)
        val nameAndDate1 = mChatsAdapter?.getNameAndDate(chat1)
        assertEquals(nameAndDate1, nameNdate1)
    }

}