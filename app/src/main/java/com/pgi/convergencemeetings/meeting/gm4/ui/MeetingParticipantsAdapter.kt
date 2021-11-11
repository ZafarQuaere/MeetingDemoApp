package com.pgi.convergencemeetings.meeting.gm4.ui

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
import com.pgi.convergencemeetings.utils.ConferenceStateCache
import java.util.*

/**
 * Created by amit1829 on 10/10/2017.
 *
 *
 * Adapter to display meeting participants..
 */
class MeetingParticipantsAdapter(private val mContext: Context,
																 private val mMeetingParticipants: List<MeetingParticipant>) : RecyclerView.Adapter<MeetingParticipantsAdapter.ViewHolder>() {

	private val isLocaleJapan: Boolean = CommonUtils.isUsersLocaleJapan()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.participants_list_item, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (mMeetingParticipants.isNotEmpty()) {
			val participant = mMeetingParticipants[position]
			holder.mItem = participant
			val nameInitials = CommonUtils.getNameInitials(holder.mItem.firstName, holder.mItem.lastName)
			val fullName = CommonUtils.getFullName(holder.mItem.firstName, holder.mItem.lastName)
			if (fullName != null) {
				if (isLocaleJapan && !participant.partID.equals(ConferenceStateCache.getInstance().myPartInfo.toString(), ignoreCase = true)) {
					holder.mParticipantsName.text = CommonUtils.formatJapaneseName(fullName)
				} else {
					holder.mParticipantsName.text = CommonUtils.formatCamelCase(fullName)
				}
			}
			if (nameInitials != null) {
				if (isLocaleJapan && !participant.partID.equals(ConferenceStateCache.getInstance().myPartInfo.toString(), ignoreCase = true)) {
					holder.mNameInitials.text = CommonUtils.formatJapaneseInitials(nameInitials.toUpperCase(Locale.getDefault()))
				} else {
					holder.mNameInitials.text = nameInitials.toUpperCase(Locale.getDefault())
				}
				holder.mNameInitials.visibility = View.VISIBLE
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
		//TODO need to check weather the participant is a host or not
		if (holder.mItem.partType == PIAPartType.MODERATOR.value) {
			if (holder.mItem.connected != null && !holder.mItem.connected) {
				holder.mParticipantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hostindicator_connecting_disabled, 0)
			} else {
				holder.mParticipantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hostindicator_connecting, 0)
			}
		} else {
			holder.mParticipantsName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
		}
	}

	private fun setMuteAndConnectingIcons(holder: ViewHolder) {
		//Changing the color code of joining participants..
		val initials = holder.mNameInitials
		val name = holder.mParticipantsName
		val muteIcon = holder.mMuteIcon
		val meetingParticipant = holder.mItem
		if (meetingParticipant.connected != null && !meetingParticipant.connected) {
			muteIcon.visibility = View.VISIBLE
			muteIcon.setImageResource(R.drawable.audiomeeting_connecting_spinner)
			initials.visibility = View.VISIBLE
			initials.background = mContext.resources.getDrawable(R.drawable.recent_meetings_circle_drawable_disabled)
			initials.setTextColor(mContext.resources.getColor(R.color.disabled_gray))
			name.setTextColor(mContext.resources.getColor(R.color.disabled_gray))
			startImageAnimation(holder)
		} else {
			//Changing color codes if participants is muted.....
			if (meetingParticipant.mute) {
				muteIcon.visibility = View.VISIBLE
				initials.visibility = View.VISIBLE
				initials.background = mContext.resources.getDrawable(R.drawable.recent_meetings_circle_drawable_red)
				initials.setTextColor(mContext.resources.getColor(R.color.color_red))
				name.setTextColor(mContext.resources.getColor(R.color.color_red))
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

		lateinit var mItem: MeetingParticipant

		init {
			ButterKnife.bind(this, mView)
		}
	}

}