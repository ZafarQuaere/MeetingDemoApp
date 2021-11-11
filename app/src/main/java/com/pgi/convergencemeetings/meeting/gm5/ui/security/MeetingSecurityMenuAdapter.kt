package com.pgi.convergencemeetings.meeting.gm5.ui.security

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.view.MeetingSecurityHeader
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MeetingSecurityMenuAdapter(private val mContext: Context, val mLogger: Logger, private val meetingSecurityHeader: MeetingSecurityHeader, private val mUsersList: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val MEETING_SECURITY_HEADER_TYPE = 0
    val WAITING_GUEST_EMPTY_MESSAGE_TYPE = 1
    val WAITING_GUEST_LIST_TYPE = 2
    private val TAG = MeetingSecurityMenuAdapter::class.java.simpleName
    var onAdmitDenyUser: ((User, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            WAITING_GUEST_LIST_TYPE -> GuestListItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.meeting_security_guest_list_item, parent, false))
            WAITING_GUEST_EMPTY_MESSAGE_TYPE -> EmptyGuestListHolder(LayoutInflater.from(parent.context).inflate(R.layout.meeting_security_empty_guest_list, parent, false))
            else -> HeaderViewHolder(meetingSecurityHeader)
        }
    }

    fun userForIndex(position: Int): User? {
        return if (mUsersList.size > position-1) {
            mUsersList[position - 1]
        } else {
            null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EmptyGuestListHolder) {
            val emptyMsgView = holder.mEmptyGuestView
            emptyMsgView.visibility = meetingSecurityHeader.rlWaitingRoomGuestFragment.visibility
        } else if (holder is GuestListItemHolder && mUsersList.isNotEmpty()) {
                    val user = userForIndex(position)
                    val nameInitials = holder.mGuestNameInitials
                    val name = holder.mGuestName
                    val profileImage = holder.mProfilePicGuest
                    setUserInitialsOrPic(nameInitials, profileImage, user)
                    name.text = user?.name
                    holder.mAdmitGuest.setOnClickListener {
                        user?.let {
                            onAdmitDenyUser?.invoke(it, true)
                        }
                    }
                    holder.mDenyGuest.setOnClickListener {
                        user?.let {
                            onAdmitDenyUser?.invoke(it, false)
                        }
                    }
                }
    }

    private fun setUserInitialsOrPic(nameInitials: TextView?, profileImage: CircleImageView?, user: User?) {
        val profilePic = user?.profileImage
        nameInitials?.text = user?.initials
        nameInitials?.visibility = View.VISIBLE
        profileImage?.visibility = View.INVISIBLE
        profilePic?.let { Picasso.get().load(profilePic).fit().into(profileImage, object : Callback {
                override fun onSuccess() {
                    profileImage?.visibility = View.VISIBLE
                    nameInitials?.visibility = View.INVISIBLE
                }
                override fun onError(e: Exception) {
                    mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.GUESTWAITINGLIST,
                           "MeetingSecurityMenuAdapter: error getting profile picture for guest", e, null, logToNewRelic = true, logToMixPanel = false)
                }
            })
        }

        user?.initials?.let{
            if(it.equals(AppConstants.POUND_SYMBOL,true)){
                profileImage?.setImageResource(R.drawable.avatar)
                nameInitials?.visibility = View.INVISIBLE
                profileImage?.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> MEETING_SECURITY_HEADER_TYPE
            mUsersList.isNotEmpty() -> WAITING_GUEST_LIST_TYPE
            else -> WAITING_GUEST_EMPTY_MESSAGE_TYPE
        }
    }

    override fun getItemCount(): Int {
        var count = 1 //Position 0 set for header
        count += if (mUsersList.isNotEmpty()) {
            mUsersList.size
        } else {
            1  // incrementing 1 for displaying empty guest list message when no one guest is in waiting room
        }
        return count
    }

    inner class GuestListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.tv_gm5_guest_name_initials)
        lateinit var mGuestNameInitials: TextView

        @BindView(R.id.tv_gm5_guest_name)
        lateinit var mGuestName: TextView

        @BindView(R.id.profile_pic_guest_list)
        lateinit var mProfilePicGuest: CircleImageView

        @BindView(R.id.tv_guest_admit)
        lateinit var mAdmitGuest: TextView

        @BindView(R.id.tv_guest_deny)
        lateinit var mDenyGuest: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) { }

    class EmptyGuestListHolder(view: View): RecyclerView.ViewHolder(view) {

        @BindView(R.id.rl_empty_guest_view)
        lateinit var mEmptyGuestView: RelativeLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}