package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.utils.AvatarComponent
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.models.Chat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(private val mContext: Context, private val mChatList: List<Chat>) : RecyclerView.Adapter<ChatListAdapter.ItemViewHolder>() {
    var onShowChatDetailPage: ((Chat) -> Unit)? = null
    private val CHAT_COUNT_99_PLUS = "99+"
    private val CHAT_COUNT_99 = 99
    private var TAG = ChatListFragment::class.java.simpleName
    var mProfilePics : MutableList<String?> = mutableListOf()
    var mProfileInitials : MutableList<String?> = mutableListOf()
    var usersCount : Int = 0
    var marginLayoutParams:ViewGroup.MarginLayoutParams? = null
    var relativeLayoutParams :RelativeLayout.LayoutParams? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_single_participant_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (mChatList.isNotEmpty()) {
            val item = mChatList[position]
            holder.textLastMessage.text = item.message
            holder.textTimeStamp.text = item.timestamp
            if (item.unReadChatCount > 0) {
                holder.tvChatBadgeCount.visibility = View.VISIBLE
                val count = if (item.unReadChatCount > CHAT_COUNT_99) CHAT_COUNT_99_PLUS else mChatList[position].unReadChatCount.toString()
                holder.tvChatBadgeCount.setText(count)
                holder.textName.setTypeface(null, Typeface.BOLD)
            } else {
                holder.tvChatBadgeCount.visibility = View.INVISIBLE
                holder.textName.setTypeface(null, Typeface.NORMAL)
            }
            alignBadgeIcon(holder)
            if (holder.itemViewType == TYPE_HEADER) {
                holder.avatarLayout.visibility = View.VISIBLE
                holder.imgProfilePic.visibility = View.INVISIBLE
                holder.textProfileNameInitial.visibility = View.INVISIBLE
                holder.avatarLayout.setAvatarIcon(usersCount, mProfilePics, mProfileInitials)
                setMarginOnBadgeIcon(holder)
            } else {
                holder.textName.text = CommonUtils.getFullName(item.firstName, item.lastName)
                setUserInitialsOrPic(holder.textProfileNameInitial, holder.imgProfilePic, item)
                holder.textName.setTextColor(mContext.getColor(R.color.participant_name_color))
                holder.textTimeStamp.setTextColor(mContext.getColor(R.color.grayMedium))
                holder.textLastMessage.setTextColor(mContext.getColor(R.color.grayMedium))
                if (item.isOffline) {
                    holder.textName.setTextColor(mContext.getColor(R.color.disable_conversation_color))
                    holder.textTimeStamp.setTextColor(mContext.getColor(R.color.disable_conversation_color))
                    holder.textLastMessage.setTextColor(mContext.getColor(R.color.disable_conversation_color))
                    holder.textTimeStamp.text = mContext.resources.getString(R.string.text_offline)
                }
            }
        } else {
            holder.textName.setTypeface(null, Typeface.NORMAL)
        }

        holder.rlytChatSingleRow.setOnClickListener() {
            if (mChatList.isEmpty()) {
                holder.tvChatBadgeCount.visibility = View.INVISIBLE
                val chat = Chat()
                chat.conversationId = AppConstants.CHAT_EVERYONE
                onShowChatDetailPage?.invoke(chat)
            } else {
                mChatList[position].unReadChatCount = 0
                holder.tvChatBadgeCount.visibility = View.INVISIBLE
                onShowChatDetailPage?.invoke(mChatList[position])
            }
            holder.textName.setTypeface(null, Typeface.NORMAL)
        }
    }

    override fun getItemCount(): Int {
        return if (mChatList.isEmpty()) {
            mChatList.size + 1
        } else {
            mChatList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    /*
     Here we are aligning  chat badge icon on basis on user profile list size
     */
    fun alignBadgeIcon(holder: ItemViewHolder) {
        relativeLayoutParams = holder.tvChatBadgeCount.layoutParams as? RelativeLayout.LayoutParams
        if (mProfilePics.size > AppConstants.DIGIT_3) {
            relativeLayoutParams?.addRule(RelativeLayout.ALIGN_RIGHT , holder.avatarLayout.id)
            holder.tvChatBadgeCount.layoutParams = relativeLayoutParams
        } else {
            relativeLayoutParams?.addRule(RelativeLayout.ALIGN_RIGHT , holder.imgProfilePic.id)
            holder.tvChatBadgeCount.layoutParams = relativeLayoutParams
        }
    }

   /*
   Here we are applying Margin on chat badge icon on basis on user profile list size
   */
    fun setMarginOnBadgeIcon(holder: ItemViewHolder) {
        marginLayoutParams = ( holder.tvChatBadgeCount.layoutParams as? ViewGroup.MarginLayoutParams)
        if (mProfilePics.size < AppConstants.DIGIT_4) {
            marginLayoutParams?.setMargins(AppConstants.DP_0, AppConstants.DP_30, AppConstants.DP_30, AppConstants.DP_0)
            holder.tvChatBadgeCount.layoutParams = marginLayoutParams
        } else {
            marginLayoutParams?.setMargins(AppConstants.DP_0, AppConstants.DP_10, AppConstants.DP_10, AppConstants.DP_0)
            holder.tvChatBadgeCount.layoutParams = marginLayoutParams
        }
    }

    fun updateUserCount(mUsers : MutableList<User>) {
        mProfilePics.clear()
        mProfileInitials.clear()
        mUsers.sortWith(compareBy { it.timestamp })
        for (users in mUsers) {
            mProfilePics.add(users.profileImage)
            mProfileInitials.add(users.initials)
        }
        for (i in 0 until mChatList.size) {
            val item = mChatList[i]
            if (item.conversationId == AppConstants.CHAT_EVERYONE) {
                usersCount = mUsers.size
                notifyItemChanged(i)
            }
        }
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_chat_badge_count)
        lateinit var tvChatBadgeCount: TextView

        @BindView(R.id.textName)
        lateinit var textName: TextView

        @BindView(R.id.textLastMessage)
        lateinit var textLastMessage: TextView

        @BindView(R.id.textTimeStamp)
        lateinit var textTimeStamp: TextView

        @BindView(R.id.rlytChatSingleRow)
        lateinit var rlytChatSingleRow: RelativeLayout

        @BindView(R.id.icProfile)
        lateinit var imgProfilePic: CircleImageView

        @BindView(R.id.textNameInitials)
        lateinit var textProfileNameInitial: TextView

        @BindView(R.id.avatarLayout)
        lateinit var avatarLayout: AvatarComponent

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    private fun setUserInitialsOrPic(nameInitials: TextView, profileImage: CircleImageView, chat: Chat) {
        val profilePic = chat.profileImage
        nameInitials.text = chat.initials
        nameInitials.visibility = View.VISIBLE
        profileImage.visibility = View.INVISIBLE
        nameInitials.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
        if (!profilePic.isNullOrEmpty()) {
            Picasso.get().load(profilePic).fit().into(profileImage, object : Callback {
                override fun onSuccess() {
                    profileImage.visibility = View.VISIBLE
                    nameInitials.visibility = View.INVISIBLE
                }
                override fun onError(e: Exception) {
                    Log.e(TAG, "ChatListAdapter: error getting profile picture for participant")
                }
            })
        } else if (chat.initials.equals(AppConstants.POUND_SYMBOL)) {
            nameInitials.setBackgroundResource(R.drawable.avatar59)
            nameInitials.text = ""
        } else {
            nameInitials.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
            nameInitials.text = chat.initials
        }
    }
}