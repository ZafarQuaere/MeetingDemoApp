package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergence.utils.AvatarComponent
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.models.Chat
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {

	private val TAG = ChatFragment::class.java.simpleName

	@BindView(R.id.rv_chats)
	lateinit var mChatRecyclerView: RecyclerView

	@BindView(R.id.et_chat)
	lateinit var mChatEditText: EditText

	@BindView(R.id.rl_chatdetail_topbar)
	lateinit var topbar: RelativeLayout

	@BindView(R.id.tv_chat_detail_header)
	lateinit var mChatToolBarName: TextView

	@BindView(R.id.iv_chat_profile_pic)
	lateinit var mChatProfilePic: CircleImageView

	@BindView(R.id.iv_chat_profile_initial)
	lateinit var mChatProfileInitial: TextView

	@BindView(R.id.tv_chat_user_type)
	lateinit var mChatUserType: TextView

	@BindView(R.id.tv_chat_detail_badge)
	lateinit var mChatDetailBadge: TextView

	@BindView(R.id.btn_send)
	lateinit var btnSend: ImageView

	@BindView(R.id.avatarIconLayout)
	lateinit var mAvatarView: AvatarComponent
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mChatsAdapter: ChatsAdapter<*>? = null

	@BindView(R.id.chat_input_layout)
	lateinit var sendLayout: RelativeLayout

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	val mCoversationList: MutableList<Chat> = mutableListOf()
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mSelfParticipant: User? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var chatInfo: Chat? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var mMeetingEventViewModel: MeetingEventViewModel
	lateinit var mMeetingUserViewModel: MeetingUserViewModel
	private val mlogger: Logger = CoreApplication.mLogger

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var isChatDisabled = false

	@BindView(R.id.rlytParent)
	lateinit var rlytParent: RelativeLayout
	private var chatUnreadCount: Int = 0

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var parentActivity : WebMeetingActivity

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var mContext: Context

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var offlineConversationList: MutableList<Chat> = mutableListOf()

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
		NewRelic.setInteractionName(Interactions.CHAT.interaction)
		if (this.chatInfo == null)
		{
			var chat = Chat()
			chat.conversationId = "default"
			setChatObject(chat)
		}
		mContext.let {
			mChatsAdapter = ChatsAdapter(it, mCoversationList)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_chat, container, false)
		ButterKnife.bind(this, view)
		registerViewModelListener()
		// notify user that private chat have restriction to send/receive below particular app version
		chatInfo?.let {
			if (it.conversationId != AppConstants.CHAT_EVERYONE && !mMeetingUserViewModel.checkPrivateChatVersionToastShown(it.conversationId)) {
				parentActivity.showPrivateChatRestrictionToast()
			}
		}
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val linearLayoutManager = LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false)
		linearLayoutManager.stackFromEnd = true
		mChatRecyclerView.layoutManager = linearLayoutManager
		mChatRecyclerView.adapter = mChatsAdapter
		validateInputFieldTextChange()
	}

	private fun validateInputFieldTextChange() {
		mChatEditText.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
				// before text change use case
			}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
				if (p0.toString().isEmpty() && isChatDisabled) {
					enableDisableInputFields(true)
				}
			}
			override fun afterTextChanged(p0: Editable?) {
				//after text change use case
			}
		})
	}

	override fun onResume() {
		super.onResume()
		setHeaderView()
	}

	private fun registerViewModelListener() {
		activity?.let {
			mMeetingEventViewModel = ViewModelProviders.of(it).get(MeetingEventViewModel::class.java)
			mMeetingUserViewModel = ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
			mSelfParticipant = mMeetingEventViewModel.getCurrentUser()
			mMeetingEventViewModel.privateChatEnableStatus.observe(this@ChatFragment, Observer { updatePrivateChatStatus(it) })
			mMeetingEventViewModel.chats.observe(this, Observer { chats: List<Chat> ->
				if (chats.isEmpty()) {
					onClearChat()
				} else {
					onChatMessageReceived(chats)
				}
			})
			mMeetingEventViewModel.chatReceived.observe(this, Observer { chatReceived: Chat -> updateChatTabNotificationCount(chatReceived) })
			mMeetingUserViewModel.userFlowStatus.observe(this, Observer { userFlowStatus: UserFlowStatus -> respondToUserStatus(userFlowStatus) })
			mChatsAdapter?.reSendChatMsg = ::reSendChatMsg
			mMeetingEventViewModel.offlineChats.observe(this, Observer {offlineChats ->
				if (offlineChats.isNotEmpty()) {
					offlineConversationList.clear()
					offlineConversationList.addAll(offlineChats)
					scrollToLastPosition(true)
					disableOfflineConversation(offlineConversationList)
				}
			})
			mMeetingUserViewModel?.privateChatLocked?.observe(this, Observer { enabled: Boolean -> addOrRemoveTopBar(enabled) })
		}
	}

	//show topbar based on privatechat flag - if privateChatLocked true then remove topbar
	private fun addOrRemoveTopBar(enabled: Boolean) {
		if (enabled) {
			topbar.visibility = View.GONE
		}
	}

	fun reSendChatMsg(chat: Chat) {
		chat.chatMessageState = ChatMessageState.SENDING
		mChatsAdapter?.notifyDataSetChanged()
		scrollToLastPosition(true)
		mMeetingUserViewModel.addChat(chat.message, chatInfo?.conversationId.toString())
	}

	fun updateChatTabNotificationCount(chatReceived: Chat) {
		if (mMeetingEventViewModel.chatCount.let { it != null && it < mMeetingEventViewModel.chatsList.size}) {
			chatUnreadCount = chatReceived.unReadChatCount
		}
		if (this.chatInfo?.conversationId == chatReceived.conversationId) {
			chatReceived.unReadChatCount = 0;
		} else if (chatUnreadCount > 0) {
			mChatDetailBadge.visibility = View.VISIBLE
		}
	}

	/**
	 * update the isChatDisable flag on the basis of private chat enable status
	 */
	fun updatePrivateChatStatus(chatEnableStatus: Boolean) {
		isChatDisabled = if (!chatEnableStatus && this.chatInfo?.conversationId != AppConstants.CHAT_EVERYONE) {
			showDisablePrivateChatToast()
			enableDisableInputFields(true)
			true
		} else {
			enableDisableInputFields(false)
			false
		}
	}

	/**
	 * enable/disable chat edittext field
	 */
	fun enableDisableInputFields(isDisable: Boolean) {
		if (isDisable) {
			mChatEditText.isFocusable = mChatEditText.text.toString().isNotEmpty()
			mChatEditText.isCursorVisible = mChatEditText.text.toString().isNotEmpty()
			mChatEditText.isClickable = true
			btnSend.setBackgroundResource(R.drawable.ic_send_disable)
			mChatEditText.setHintTextColor(resources.getColor(R.color.email_support_text_colorinactive))
			sendLayout.isEnabled = false
			sendLayout.alpha = 0.5f
		} else {
			mChatEditText.isFocusableInTouchMode = true
			mChatEditText.isFocusable = true
			mChatEditText.requestFocus()
			mChatEditText.isCursorVisible = true
			mChatEditText.isClickable = false
			sendLayout.isEnabled = true
			sendLayout.alpha = 1f
			mChatEditText.setHintTextColor(resources.getColor(R.color.email_support_text_coloractive))
			btnSend.setBackgroundResource(R.drawable.ic_send)
		}
	}

	fun respondToUserStatus(userFlowStatus: UserFlowStatus) {
		if (userFlowStatus === UserFlowStatus.CHAT_ADD_FAILURE) {
			mMeetingEventViewModel.updateSelfChatFailure()
		}
	}

	fun showChatError() {
		mMeetingEventViewModel.updateSelfChatFailure()
	}

	private fun onClearChat() {
		mCoversationList.clear()
		mChatsAdapter?.notifyDataSetChanged()
		scrollToLastPosition(false)
	}

	fun onChatMessageReceived(chats: List<Chat>) {
		mCoversationList.clear()
		for ((chatIndex, chatValue) in chats.withIndex()) {
			if(chats[chatIndex].conversationId == this.chatInfo?.conversationId)
				mCoversationList.add(chats[chatIndex])
		}
		mChatsAdapter?.notifyDataSetChanged()
		scrollToLastPosition(true)
	}

	private fun scrollToLastPosition(isObjectAdded: Boolean) {
		val layoutManager = mChatRecyclerView.layoutManager
		if (layoutManager is LinearLayoutManager) {
			val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
			if (lastVisiblePosition == mCoversationList.size - (if (isObjectAdded) 2 else 1)) {
				mChatsAdapter?.itemCount?.let {
					mChatRecyclerView.scrollToPosition(it - 1)
				}
			} else {
				mChatsAdapter?.itemCount?.let {
					mChatRecyclerView.scrollToPosition(mCoversationList.size - 1)
				}
			}
		}
	}

	@OnClick(R.id.iv_chat_detail_back)
	fun onChatDetailBackBtn() {
		mMeetingUserViewModel.isOpenChatFragment.postValue(false)
		parentActivity.clearConvIdAndResumeAct()
		mMeetingEventViewModel.chatUnreadCountUpdated.postValue(this.chatInfo?.conversationId)
	}

	@OnClick(R.id.tv_chat_detail_exit_meeting)
	fun onChatDetailExitBtn() {
		parentActivity.exitMeeting()
	}

	@OnClick(R.id.et_chat)
	fun chatInputFieldClick() {
		if (isChatDisabled && mChatEditText.text.toString().isEmpty()) {
			showDisablePrivateChatToast()
		}
	}

	@OnClick(R.id.btn_send)
	fun onChatSendButtonClick() {
		if (mMeetingUserViewModel.isPrivateChatLocked || (!mMeetingUserViewModel.isPrivateChatLocked && !isChatDisabled)) {
			if (!InternetConnection.isConnected(context) || mSelfParticipant == null) {
				return
			}
			val selfInitials = mSelfParticipant?.initials
			val text = mChatEditText.text.toString().trim { it <= ' ' }
			val currentTime: String = SimpleDateFormat(AppConstants.CHAT_TIME_FORMAT, Locale.US).format(Date())
			if (!TextUtils.isEmpty(text)) {
				val chat = Chat()
				chat.message = text
				chat.chatMessageState = ChatMessageState.SENDING
				chat.timestamp = null
				chatInfo?.let {
					chat.conversationId = it.conversationId
					if (it.conversationId == AppConstants.CHAT_EVERYONE) {
						//everyone chat
						chat.isSelf = true
						chat.webPartId = mSelfParticipant?.id
						chat.initials = selfInitials
						chat.isHostOrCoHost = mSelfParticipant?.roomRole == AppConstants.HOST || mSelfParticipant?.delegateRole == true
						mlogger.mixpanelSendChat.chatType = AppConstants.CHAT_TYPE_GROUP
					} else {
						//private chat
						chat.webPartId = it.webPartId
						chat.firstName = it.firstName
						chat.lastName = it.lastName
						chat.initials = it.initials
						chat.profileImage = it.profileImage
						chat.isSelf = true
						chat.timestamp = currentTime
						mlogger.mixpanelSendChat.chatType = AppConstants.CHAT_TYPE_INDIVIDUAL
						mlogger.mixpanelSendChat.recipientUserType = mMeetingUserViewModel?.userType
					}
				}
				sendChatLogs(text)
				//Clear Input field EditText
				mChatEditText.setText(null)
				//Add chat object to conversation list and notify UI.
				mCoversationList.add(chat)
				mChatsAdapter?.notifyDataSetChanged()
				scrollToLastPosition(true)
				mMeetingEventViewModel.addSelfChat(chat)
				mMeetingUserViewModel.addChat(chat.message, chatInfo?.conversationId.toString())
			}
		} else {
			showDisablePrivateChatToast()
		}
	}

	private fun sendChatLogs(text: String) {
		val msg = AppConstants.MIXPANEL_EVENT + LogEventValue.MIXPANEL_SEND_CHAT.value
		when {
			Patterns.WEB_URL.matcher(text).matches() -> {
				mlogger.mixpanelSendChat.messageType = AppConstants.URL_ONLY
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, msg, null, null, false, true)
			}
			AppConstants.URL_MORE_PATTERN.matcher(text).find() -> {
				mlogger.mixpanelSendChat.messageType = AppConstants.URL_AND_MORE
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, msg, null, null, false, true)
			}
			containsOnlyEmoji(text) -> {
				mlogger.mixpanelSendChat.messageType = AppConstants.EMOJI_ONLY
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, msg, null, null, false, true)
			}
			else -> {
				mlogger.mixpanelSendChat.messageType = AppConstants.TEXT_ONLY
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SEND_CHAT, msg, null, null, false, true)
			}
		}
	}

	private fun containsOnlyEmoji(emojiText: String): Boolean {
		for (element in emojiText) {
			val type = Character.getType(element)
			if (type != Character.SURROGATE.toInt()) {
				return false
			}
		}
		return true
	}

	fun setHeaderView() {
		chatInfo?.let {
			if (it.conversationId == AppConstants.CHAT_EVERYONE) {
				mChatProfilePic.visibility = View.INVISIBLE
				mChatUserType.visibility = View.GONE
				mAvatarView.visibility = View.VISIBLE
				mMeetingUserViewModel?.let { viewModel ->
					mAvatarView.setAvatarIcon(viewModel.usersCount, viewModel.mProfilePics, viewModel.mProfileInitials)
				}
			} else {
				mChatProfilePic.visibility = View.VISIBLE
				mChatProfileInitial.visibility = View.VISIBLE
				mMeetingUserViewModel?.userType?.let { role ->
					mChatUserType.visibility = View.VISIBLE
					mChatUserType.text = role
				}
				if (!it.firstName.isNullOrEmpty()) {
					mChatToolBarName.text = CommonUtils.getFullName(it.firstName, it.lastName)
				}
				val profilePic = it.profileImage
				if (!profilePic.isNullOrEmpty()) {
					Picasso.get().load(profilePic).fit().into(mChatProfilePic, object : Callback {
						override fun onSuccess() {
							mChatProfilePic.visibility = View.VISIBLE
							mChatProfileInitial.visibility = View.INVISIBLE
						}

						override fun onError(e: Exception) {
							mChatProfilePic.visibility = View.INVISIBLE
							Log.e(TAG, "ChatListAdapter: error getting profile picture for participant")
						}
					})
				} else {
					mChatProfilePic.visibility = View.INVISIBLE
					mChatProfileInitial.visibility = View.VISIBLE
					mChatProfileInitial.text = it.initials
				}
			}
		}
	}

	fun setChatObject(chat: Chat) {
		this.chatInfo = chat
	}

	companion object {
		private val TAG = ChatFragment::class.java.simpleName
		@JvmStatic
		fun newInstance(): ChatFragment {
			return ChatFragment()
		}
	}

	/**
	 * display a snackbar notification if host disabled private chat
	 */
	fun showDisablePrivateChatToast() {
		parentActivity.showDisablePrivateChatToast(isChatDisabled)
	}

	fun disableOfflineConversation(offlineChats: MutableList<Chat>) {
		for (offlineChat in offlineChats) {
			if (chatInfo?.webPartId == offlineChat.webPartId && chatInfo?.conversationId != AppConstants.CHAT_EVERYONE) {
				context?.let {
					chatInfo?.isOffline = true
					chatInfo?.offlineTimestamp = offlineChat.offlineTimestamp
					mChatToolBarName.setTextColor(it.getColor(R.color.disable_conversation_color))
					mChatUserType.setTextColor(it.getColor(R.color.disable_conversation_color))
					mChatEditText.isClickable = false
					mChatEditText.isEnabled = false
					sendLayout.alpha = 0.5f
					parentActivity.showOfflineUserToast()
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mMeetingUserViewModel.isOpenChatFragment.postValue(false)
	}
}