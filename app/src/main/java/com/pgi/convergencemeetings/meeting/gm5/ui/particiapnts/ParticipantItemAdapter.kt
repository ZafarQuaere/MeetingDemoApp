package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.enums.pia.PIAPartType
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.MeetingParticipant
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * Created by amit1829 on 10/10/2017.
 *
 *
 * Adapter to display meeting participants..
 */
class ParticipantItemAdapter(private val mContext: Context,
														 private val mMeetingParticipants: List<MeetingParticipant>) : RecyclerView.Adapter<ParticipantItemAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.participants_list_item, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (mMeetingParticipants.isNotEmpty()) {
			val participant = mMeetingParticipants[position]
			holder.mItem = participant
			val meetingParticipant = holder.mItem
			val nameInitialsView = holder.mNameInitials
			val participantsNameView = holder.mParticipantsName
			val profileImageView = holder.mProfileImage
			val nameInitials = CommonUtils.getNameInitials(meetingParticipant.firstName, meetingParticipant.lastName)
			val fullName = CommonUtils.getFullName(meetingParticipant.firstName, meetingParticipant.lastName)
			if (fullName != null) {
				participantsNameView.text = CommonUtils.formatCamelCase(fullName)
			}
			val profileImage = AppAuthUtils.getInstance().profileImage
			if (profileImage != null) {
				Picasso.get().load(profileImage).fit().into(profileImageView)
			} else if (nameInitials != null) {
				nameInitialsView.visibility = View.VISIBLE
				nameInitialsView.text = nameInitials.toUpperCase(Locale.getDefault())
			}
			setMuteAndConnectingIcons(holder)
			setHostIndicator(holder)
		}
	}

	override fun getItemCount(): Int {
		return mMeetingParticipants.size
	}

	private fun setHostIndicator(holder: ViewHolder) {
		//Setting the visibility of host indicator if participant is host...
		val partType = holder.mItem.partType
		val isConnected = holder.mItem.connected
		val participantsName = holder.mParticipantsName
		//TODO need to check weather the participant is a host or not
		if (partType == PIAPartType.MODERATOR.value) {
			if (isConnected != null && !isConnected) {
				participantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hostindicator_connecting_disabled, 0)
			} else {
				participantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hostindicator_connecting, 0)
			}
		} else {
			participantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
		}
	}

	private fun setMuteAndConnectingIcons(holder: ViewHolder) {
		//Changing the color code of joining participants..
		val partType = holder.mItem.partType
		val isConnected = holder.mItem.connected
		val nameInitials = holder.mNameInitials
		val participantsName = holder.mParticipantsName
		val muteIcon = holder.mMuteIcon
		val isMuted = holder.mItem.mute
		if (isConnected != null && !isConnected) {
			muteIcon.visibility = View.VISIBLE
			muteIcon.setImageResource(R.drawable.audiomeeting_connecting_spinner)
			nameInitials.background = mContext.resources.getDrawable(R.drawable.recent_meetings_circle_drawable_disabled)
			nameInitials.setTextColor(mContext.resources.getColor(R.color.disabled_gray))
			participantsName.setTextColor(mContext.resources.getColor(R.color.disabled_gray))
			startImageAnimation(holder)
		} else {
			//Changing color codes if participants is muted.....
			if (isMuted) {
				muteIcon.visibility = View.VISIBLE
				nameInitials.background = mContext.resources.getDrawable(R.drawable.recent_meetings_circle_drawable_red)
				nameInitials.setTextColor(mContext.resources.getColor(R.color.color_red))
				participantsName.setTextColor(mContext.resources.getColor(R.color.color_red))
			} else {
				muteIcon.visibility = View.INVISIBLE
			}
		}
	}

	//Rotating the connecting spinner........
	private fun startImageAnimation(holder: ViewHolder) {
		val muteIcon = holder.mMuteIcon
		val rotate = RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
		rotate.duration = 3000
		rotate.repeatCount = RotateAnimation.INFINITE
		rotate.interpolator = LinearInterpolator()
		muteIcon.startAnimation(rotate)
	}

	inner class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

		@BindView(R.id.tv_name_initials)
		lateinit var mNameInitials: TextView

		@BindView(R.id.tv_participant_name)
		lateinit var mParticipantsName: TextView

		@BindView(R.id.iv_mute_icon)
		lateinit var mMuteIcon: ImageView

		@BindView(R.id.profile_image_part_list)
		lateinit var mProfileImage: CircleImageView

		lateinit var mItem: MeetingParticipant

		init {
			ButterKnife.bind(this, mView)
		}
	}

}