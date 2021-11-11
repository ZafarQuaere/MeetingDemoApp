package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ChatMessageState
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.models.Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter<T : Chat>(private val mContext: Context, private val mCoversationList: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	var reSendChatMsg: ((Chat) -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return if (viewType == NO_CHAT_ITEM) {
			val view = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_item_no_chats, parent, false)
			NoChatViewHolder(view)
		} else {
			val view = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_item, parent, false)
			ChatViewHolder(view)
		}
	}

	override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
		if (mCoversationList.isNotEmpty()) {
			val chatMsg = mCoversationList[position]
			val holder = viewHolder as ChatsAdapter<*>.ChatViewHolder
			val mNameInitialsTextView = holder.mNameInitialsTextView
			val mMsgTextView = holder.mContentTextView
			val mSubContentTextView = holder.mSubContentTextView
			val circleImageView = holder.circleImageView
			val initials = chatMsg.initials
			val profilePic = chatMsg.profileImage
			if (profilePic != null) {
				Picasso.get().load(profilePic).fit().into(circleImageView)
				mNameInitialsTextView.visibility = View.INVISIBLE
				circleImageView.visibility = View.VISIBLE
			} else {
				circleImageView.visibility = View.INVISIBLE
				mNameInitialsTextView.visibility = View.VISIBLE
				mNameInitialsTextView.text = initials
			}
			var message = chatMsg.message
			if (message != null && message.isNotEmpty()) {
				message = message.replace(AppConstants.APOSTROPHE_CHARACTERS, AppConstants.APOSTROPHE_SYMBOL)
			}
			mMsgTextView.text = message
			val subContentTextColorRes = if (chatMsg.chatMessageState == ChatMessageState.FAILED) R.color.chat_sub_content_text_error_color else R.color.chat_sub_content_text_color
			mSubContentTextView.setTextColor(ContextCompat.getColor(mContext, subContentTextColorRes))
			setChatMsgLayout(chatMsg,circleImageView,mNameInitialsTextView,mMsgTextView,holder.mContentTextView,holder.mSubContentTextView)
			setChatMsgStatusLayout(chatMsg,mSubContentTextView)
			mSubContentTextView.setOnClickListener() {
				if (mSubContentTextView.text == mContext.getString(R.string.private_chat_failure)) {
					reSendChatMsg?.invoke(chatMsg)
				}
			}
		}
	}

	private fun setChatMsgStatusLayout(chatMsg: T, mSubContentTextView: TextView) {
		if (chatMsg.chatMessageState == ChatMessageState.FAILED && chatMsg.conversationId == AppConstants.CHAT_EVERYONE) {
			mSubContentTextView.text = chatMsg.chatMessageState.value
		} else if (chatMsg.chatMessageState == ChatMessageState.FAILED && chatMsg.conversationId != AppConstants.CHAT_EVERYONE) {
			mSubContentTextView.text = mContext.getString(R.string.private_chat_failure)
		} else if (chatMsg.chatMessageState == ChatMessageState.RECEIVED) {
			mSubContentTextView.text = getNameAndDate(chatMsg)
		} else {
			mSubContentTextView.text = chatMsg.chatMessageState.value
		}
	}

	private fun setChatMsgLayout(chatMsg: T, circleImageView: CircleImageView, mNameInitialsTextView: TextView, mMsgTextView: TextView, mMsgTvHolder: TextView, mMsgDetailTvHolder: TextView) {
		val contentTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
		val subContentTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
		var resDrawable = R.drawable.chat_item_bg_self
		if ((chatMsg.conversationId == AppConstants.CHAT_EVERYONE && chatMsg.isHostOrCoHost) || (chatMsg.conversationId != AppConstants.CHAT_EVERYONE && chatMsg.isSelf)) {
			contentTextParams.gravity = Gravity.RIGHT
			contentTextParams.marginStart = mContext.resources.getDimension(R.dimen.margin_32_dp).toInt()
			subContentTextParams.gravity = Gravity.RIGHT
			subContentTextParams.marginEnd= mContext.resources.getDimension(R.dimen.margin_13_dp).toInt()
			circleImageView.visibility = View.GONE
			mNameInitialsTextView.visibility = View.GONE
			mMsgTextView.setTextColor(mContext.resources.getColor(R.color.toolbarTitleTextGray))
			mMsgTextView.setLinkTextColor(mContext.resources.getColor(R.color.primary_color_500))
		} else {
			contentTextParams.gravity = Gravity.LEFT
			contentTextParams.marginEnd = mContext.resources.getDimension(R.dimen.margin_32_dp).toInt()
			subContentTextParams.gravity = Gravity.LEFT
			subContentTextParams.marginStart = mContext.resources.getDimension(R.dimen.margin_13_dp).toInt()
			mMsgTextView.setTextColor(mContext.resources.getColor(R.color.white))
			mMsgTextView.setLinkTextColor(mContext.resources.getColor(R.color.white))
			resDrawable =  R.drawable.chat_item_bg_other
		}
		mMsgTvHolder.layoutParams = contentTextParams
		mMsgDetailTvHolder.layoutParams = subContentTextParams
		mMsgTextView.setBackgroundResource(resDrawable)
	}

	fun getNameAndDate(t: Chat): String {
		val name = if (t.isSelf) mContext.getString(R.string.you) else t.firstName + AppConstants.BLANK_SPACE + t.lastName
		return String.format("%s %s %s", name, mContext.getString(R.string.chat_sub_content_bullet), t.timestamp)
	}

	override fun getItemCount(): Int {
		val isConversationEmpty = mCoversationList.isEmpty()
		return if (isConversationEmpty) 1 else mCoversationList.size
	}

	override fun getItemViewType(position: Int): Int {
		val size = mCoversationList.size
		return if (size == 0) NO_CHAT_ITEM else CHAT_ITEM
	}

	internal inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		@BindView(R.id.tv_name_initials)
		lateinit var mNameInitialsTextView: TextView

		@BindView(R.id.tv_content)
		lateinit var mContentTextView: TextView

		@BindView(R.id.tv_sub_content)
		lateinit var mSubContentTextView: TextView

		@BindView(R.id.profile_image_chat)
		lateinit var circleImageView: CircleImageView

		init {
			ButterKnife.bind(this, itemView)
		}
	}

	internal inner class NoChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	companion object {
		const val NO_CHAT_ITEM = 0
		const val CHAT_ITEM = 1
	}
}