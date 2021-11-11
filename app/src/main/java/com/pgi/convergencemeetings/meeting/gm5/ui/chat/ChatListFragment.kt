package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.ParticipantsOrder
import com.pgi.convergencemeetings.models.Chat


class ChatListFragment : Fragment() {

    @BindView(R.id.rvChatList)
    lateinit var mChatRecyclerView: RecyclerView

    @BindView(R.id.ivAddChat)
    lateinit var ivAddChat: ImageView

    @BindView(R.id.frameLytSelectChat)
    lateinit var frameLytSelectChat: FrameLayout

    @BindView(R.id.textChatLabel)
    lateinit var textChatLabel: TextView

    @BindView(R.id.textChatCount)
    lateinit var textChatCount: TextView

    @BindView(R.id.img_chat_menu_icon)
    lateinit var ivChatMenu: ImageView

    @BindView(R.id.ivCancelFragment)
    lateinit var ivCancelFragment: ImageView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var mMeetingEventViewModel: MeetingEventViewModel

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var mMeetingUserViewModel: MeetingUserViewModel

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var chatListAdapter: ChatListAdapter? = null

    var mConvId :String ?= null
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var mConversationList: MutableList<Chat> = mutableListOf()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var mContext: Context

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isChatEnabled : Boolean = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var user: User? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var parentActivity : WebMeetingActivity

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var offlineConversationList: MutableList<Chat> = mutableListOf()

    var mUsers: MutableList<User> = mutableListOf()
    var updatePosition : Int = 0

    private val TAG = ChatListFragment::class.java.simpleName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        initializeParentActivity(activity)
    }

    fun initializeParentActivity(activity: FragmentActivity?) {
        if (activity is WebMeetingActivity) {
            parentActivity = activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext.let {
            chatListAdapter = ChatListAdapter(it, mConversationList)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_chat_list, container, false)
        ButterKnife.bind(this, view)
        registerViewModelListener()
        chatListAdapter?.onShowChatDetailPage = ::onShowChatDetailPage
        (mContext as? WebMeetingActivity)?.setFragmentRefreshListener(object : WebMeetingActivity.FragmentRefreshListener {
            override fun onRefresh() {
               mConvId = null
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUIandClickEvents()
    }

    private fun initUIandClickEvents() {
        val linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        mChatRecyclerView.layoutManager = linearLayoutManager
        mChatRecyclerView.adapter = chatListAdapter
        ivChatMenu.visibility = if (user?.roomRole == AppConstants.HOST || user?.delegateRole == true) View.VISIBLE else View.GONE
        ivAddChat.setOnClickListener {
            if (isChatEnabled && mMeetingEventViewModel.users.value?.size ?: 0 > 1) {
                addOrRemoveFragment(true)
            } else {
                parentActivity.showDisablePrivateChatToast(isChatEnabled)
            }
        }
        ivCancelFragment.setOnClickListener {
            addOrRemoveFragment(false)
        }
        ivChatMenu.setOnClickListener{
            openChatEnableDisableDialog()
        }
    }

    /**
     * display a dialog for enable/disable private chat
     */
    fun openChatEnableDisableDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_chat_enable_disable_dialog, null)
        val customDialog = mContext.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
                .show()
        }
        val textSave = dialogView.findViewById<TextView>(R.id.textSave)
        val textCancel = dialogView.findViewById<TextView>(R.id.textCancel)
        val switchEnableDisable = dialogView.findViewById<Switch>(R.id.switchEnableDisable)
        switchEnableDisable.isChecked = isChatEnabled == true
        textCancel.setOnClickListener {
            switchEnableDisable.isChecked = isChatEnabled
            customDialog.dismiss()
        }
        textSave.setOnClickListener {
            mMeetingUserViewModel.enablePrivateChat(switchEnableDisable.isChecked)
            customDialog.dismiss()
        }
    }

    /**
    To add or remove the SelectPrivateChatFragment from ChatTab
    and update View accordingly
     */
    fun addOrRemoveFragment(isAdd: Boolean) {
        val privateChatFragment = NewPrivateChatSelectionFragment()
        (activity as? WebMeetingActivity)?.addOrRemoveSelectPrivateChat(isAdd, privateChatFragment)
        privateChatFragment.addOrRemoveFragment = ::removeFragmentCallback
        updateView(isAdd)
    }

    /**
    this callback is to remove SelectPrivateChatFragment from fragmentManger
    when private chat user is selected
     */
    fun removeFragmentCallback(removeFrag: Boolean) {
        addOrRemoveFragment(removeFrag)
    }

    /**
    Update the view of Select Private Chat or Conversation list
     */
    fun updateView(add: Boolean) {
        if (add) {
            mChatRecyclerView.visibility = View.GONE
            ivAddChat.visibility = View.GONE
            textChatCount.visibility = View.GONE
            ivChatMenu.visibility = View.GONE
            ivCancelFragment.visibility = View.VISIBLE
            textChatLabel.text = mContext.getString(R.string.text_new_chat)
        } else {
            mChatRecyclerView.visibility = View.VISIBLE
            ivAddChat.visibility = View.VISIBLE
            textChatCount.visibility = View.VISIBLE
            ivChatMenu.visibility = if (user?.roomRole == AppConstants.HOST || user?.delegateRole == true) View.VISIBLE else View.GONE
            ivCancelFragment.visibility = View.GONE
            textChatLabel.text = mContext.getString(R.string.tab_text_chat)
        }
    }

    private fun registerViewModelListener() {
        activity?.let {
            mMeetingUserViewModel = ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
            mMeetingEventViewModel = ViewModelProviders.of(it).get(MeetingEventViewModel::class.java)
            isChatEnabled = mMeetingUserViewModel.getRoomInfoResponse()?.privateChat?.enabled == true
            updatePrivateChatStatus(isChatEnabled)
            mMeetingEventViewModel.apply {
                chats.observe(this@ChatListFragment, Observer { chats: List<Chat> ->
                    if (chats.isNotEmpty()) {
                        addItemToList(chats)
                    } else if (chats.isEmpty()) {
                        clearGroupChat()
                    }
                })
                users.observe(this@ChatListFragment, Observer { users: List<User> ->
                    if (users.isNotEmpty()) {
                        updateUserList(users)
                        mMeetingUserViewModel?.addUsersList(mUsers)
                        if (mUsers.isNotEmpty() && mConversationList.isEmpty()) {
                            addDefaultChat()
                        }
                        chatListAdapter?.updateUserCount(mUsers)
                    }
                    updatePrivateChatStatus(isChatEnabled)
                })
                chatReceived.observe(this@ChatListFragment, androidx.lifecycle.Observer { chatReceived: Chat -> updateChatTabNotificationCount(chatReceived) })
                privateChatEnableStatus.observe(this@ChatListFragment, Observer {
                    isChatEnabled = it
                    updatePrivateChatStatus(it)
                    showPrivateChatDisableNotification(it)
                })
                offlineChats.observe(this@ChatListFragment, androidx.lifecycle.Observer { offlineChats ->
                    if (offlineChats.isNotEmpty()) {
                        offlineConversationList.clear()
                        offlineConversationList.addAll(offlineChats)
                        updateOfflineConversation()
                    }
                })
                chatUnreadCountUpdated.observe(this@ChatListFragment, androidx.lifecycle.Observer { conversationId: String -> updateChatUnreadCount(conversationId) })
                chatClearedEvent.observe(this@ChatListFragment, androidx.lifecycle.Observer { conversationId: String -> updateChatOnClearedEvent(conversationId)})
            }
            user = mMeetingEventViewModel.getCurrentUser()
        }
    }

    fun updateChatUnreadCount(conversationId: String) {
        for ((chatIndex, chatValue) in mConversationList.withIndex()) {
            if (mConversationList[chatIndex].conversationId == conversationId) {
                mConversationList[chatIndex].unReadChatCount = 0
                chatListAdapter?.notifyItemChanged(chatIndex)
                break
            }
        }
    }

    fun showPrivateChatDisableNotification(isEnable: Boolean) {
        if (!isEnable) {
            parentActivity.showDisablePrivateChatToast(isChatEnabled)
        }
    }

    fun updateChatOnClearedEvent(conversationId: String) {
        clearGroupChat()
    }

    fun updateChatTabNotificationCount(chatReceived: Chat) {
        var convNotExists : Boolean = false
        if (this.mConvId != chatReceived.conversationId && mConversationList.isNotEmpty()) {
            for ((chatIndex, chatValue) in mConversationList.withIndex()) {
                if (mConversationList[chatIndex].conversationId == chatReceived.conversationId) {
                    chatReceived.unReadChatCount = mConversationList[chatIndex].unReadChatCount + 1
                    convNotExists = true
                }
            }
            if (!convNotExists) {
                chatReceived.unReadChatCount = chatReceived.unReadChatCount +1
            }
        } else if (mConversationList.isEmpty()) {
            chatReceived.unReadChatCount = chatReceived.unReadChatCount +1
        }
    }

    /**
     * update UI on chat enable toggle
     */
    fun updatePrivateChatStatus(isEnable: Boolean) {
        if (isEnable && mMeetingEventViewModel.users.value?.size ?: 0 > 1) {
            ivAddChat.background = resources.getDrawable(R.drawable.ic_add)
        } else {
            ivAddChat.background = resources.getDrawable(R.drawable.ic_add_chat_disable)
        }
    }

    fun onShowChatDetailPage(chat : Chat) {
        this.mConvId = chat.conversationId
        if (chat.conversationId != AppConstants.CHAT_EVERYONE) {
            updateChatWithUserRole(chat.webPartId)
        }
        parentActivity.onShowChatFragment(chat)
        mMeetingEventViewModel?.chatCount = mMeetingEventViewModel?.chatsList?.size.let { it }
    }

    fun clearGroupChat() {
        if (mConversationList.isNotEmpty()) {
            for (chat in mConversationList) {
                if (chat.conversationId == AppConstants.CHAT_EVERYONE) {
                    chat.message = activity?.getString(R.string.no_message)
                    chat.timestamp = ""
                    chat.unReadChatCount = 0
                }
            }
            chatListAdapter?.notifyDataSetChanged()
        }
    }

    fun addItemToList(chats: List<Chat>) {
        updateOfflineConversation()
        for ((chatIndex, chatValue) in chats.withIndex()) {
            if (chats[chatIndex].conversationId == AppConstants.CHAT_EVERYONE) {
                updateEveryOneChat(chatValue)
            } else {
                updatePrivateChat(chatValue)
            }
        }
        textChatCount.text = mConversationList.size.toString()
        orderEveryOneChat()
        sortAndOrderConversation()
    }

    fun updateEveryOneChat(chat: Chat) {
       if(mConversationList.isEmpty()) {
            mConversationList.add(chat)
        } else {
           mConversationList[0] = chat
       }
        chatListAdapter?.notifyDataSetChanged()
    }

    fun updatePrivateChat(chatItems: Chat) {
        if (checkConversation(chatItems)) {
            //conversation started
            val item = mConversationList[updatePosition]
            chatItems.firstName = item.firstName
            chatItems.lastName = item.lastName
            chatItems.initials = item.initials
            chatItems.profileImage = item.profileImage
            chatItems.webPartId = item.webPartId
            mConversationList[updatePosition] = chatItems
            movePrivateChat(updatePosition)
        } else {
            // no conversation started
            if (mConversationList.isEmpty()) {
                addDefaultChat()
            }
            mConversationList.add(chatItems)
            movePrivateChat(mConversationList.size - 1)
        }
        chatListAdapter?.notifyDataSetChanged()
    }

    fun checkConversation(chat: Chat) : Boolean {
        var isNewOrEarlyChat = false
        for (count in 0 until mConversationList.size) {
            val item = mConversationList[count]
            if (item.conversationId != AppConstants.CHAT_EVERYONE && item.conversationId == chat.conversationId) {
                updatePosition = count
                isNewOrEarlyChat = true
            }
        }
        return isNewOrEarlyChat
    }

    /**
     move the "everyone" chat to top of the list
     */
     fun orderEveryOneChat() {
        var everyOneIndex = 0
        for (item in mConversationList.indices) {
            if (mConversationList[item].conversationId == AppConstants.CHAT_EVERYONE) {
                everyOneIndex = item
            }
        }
        val everyOneItem = mConversationList[everyOneIndex]
        mConversationList.remove(everyOneItem)
        mConversationList.add(0, everyOneItem)
    }

    fun addDefaultChat() {
        val chat = Chat()
        chat.conversationId = AppConstants.CHAT_EVERYONE
        chat.message = resources.getString(R.string.no_message)
        mConversationList.add(chat)
    }

    fun updateUserList(userList: List<User>) {
        mUsers.clear()
        for (user in userList) {
            if (user.initials.equals(AppConstants.POUND_SYMBOL) || ParticipantsOrder.checkUserforWebpresence(user)) { // audio only user is filtered
                continue
            }
            mUsers.add(user)
        }
    }

    fun updateChatWithUserRole(webPartId: String) {
        for (users in mUsers) {
            if (webPartId == users.id) {
                mMeetingUserViewModel.setUserRole(users, requireContext())
            }
        }
    }

    /**
    move the recent "privatechat" to top
     */
    fun movePrivateChat(position : Int) {
        val moveItem = mConversationList[position]
        mConversationList.remove(moveItem)
        mConversationList.add(1, moveItem)
    }

    fun updateOfflineConversation() {
        for (offlineChat in offlineConversationList) {
            for (chat in mConversationList) {
                if (chat.webPartId == offlineChat.webPartId && chat.conversationId != AppConstants.CHAT_EVERYONE) {
                    chat.isOffline = true
                    chat.offlineTimestamp = offlineChat.offlineTimestamp
                }
            }
        }
        sortAndOrderConversation()
    }

    fun sortAndOrderConversation() {
        val onlineConversationList = mConversationList.filter { !it.isOffline }
        val offlineConversationList = mConversationList.filter { it.isOffline }.sortedByDescending {
            it.offlineTimestamp
        }
        if (offlineConversationList.isNotEmpty()) {
            mConversationList.clear()
            mConversationList.addAll(onlineConversationList)
            mConversationList.addAll(offlineConversationList)
        }
        chatListAdapter?.notifyDataSetChanged()
    }
}