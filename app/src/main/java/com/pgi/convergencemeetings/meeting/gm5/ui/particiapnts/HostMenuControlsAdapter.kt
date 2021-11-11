package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.content.Context
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.HostControlsEnum
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.models.HostControlsModel
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.Mixpanel
import java.util.*
import androidx.annotation.VisibleForTesting
import com.pgi.convergencemeetings.models.Chat

/**
 * Created by surbhidhingra on 02-02-18.
 */
class HostMenuControlsAdapter(bottomSheetPopupDialog: BottomSheetDialog, user: User, meetingModel: MeetingUserViewModel, private val mContext: Context,
                              @param:LayoutRes private val mLayoutResource: Int, hostControls: List<HostControlsModel>) : ArrayAdapter<Any?>(mContext, mLayoutResource, hostControls) {

	private var mlogger: Logger = CoreApplication.mLogger
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mMeetingUserViewModel: MeetingUserViewModel = meetingModel
	private val mHostControls: List<HostControlsModel> = hostControls
	private val mUser: User = user
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	val mBottomSheetPopupDialog: BottomSheetDialog = bottomSheetPopupDialog
	private val TAG = HostMenuControlsAdapter::class.java.simpleName
	private val MIXPANEL_EVENT = "Mixpanel Event: "

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val hostControl = mHostControls[position]
		val root: View = if(convertView == null) {
			val rootView = from(parent.context).inflate(mLayoutResource, null, false)
			val menuItem = rootView.findViewById<View>(R.id.tv_menu_control) as TextView
            var imgItemIcon = rootView.findViewById<View>(R.id.img_item_icon) as ImageView
            if (hostControl.controlName.equals(AppConstants.MENU_MUTE, ignoreCase = true)) {
                menuItem.text = String.format(mContext.resources.getString(R.string.mute_user),AppConstants.BLANK_SPACE+mUser.firstName)
                imgItemIcon.setImageResource(R.drawable.muted)
            } else if (hostControl.controlName.equals(AppConstants.MENU_UNMUTE, ignoreCase = true)) {
                menuItem.text = String.format(mContext.resources.getString(R.string.unmute_user),AppConstants.BLANK_SPACE+mUser.firstName)
                imgItemIcon.setImageResource(R.drawable.unmuted_standard)
            } else if (hostControl.controlName.equals(AppConstants.MENU_PROMOTE, ignoreCase = true)) {
                menuItem.text = String.format(mContext.resources.getString(R.string.promote_user),AppConstants.BLANK_SPACE+mUser.firstName)
                imgItemIcon.setImageResource(R.drawable.record_voiceover)
            } else if (hostControl.controlName.equals(AppConstants.MENU_DEMOTE, ignoreCase = true)) {
                menuItem.text = String.format(mContext.resources.getString(R.string.demote_user),AppConstants.BLANK_SPACE+mUser.firstName)
                imgItemIcon.setImageResource(R.drawable.person)
            } else if (hostControl.controlName.equals(AppConstants.MENU_DISMISS, ignoreCase = true)) {
                menuItem.text = String.format(mContext.resources.getString(R.string.dismiss_user),AppConstants.BLANK_SPACE+mUser.firstName)
                imgItemIcon.setImageResource(R.drawable.cancel_circle)
            }
			else if (hostControl.controlName.equals(AppConstants.MENU_PRIVATE_CHAT_WITH, ignoreCase = true)) {
				menuItem.text = String.format(mContext.resources.getString(R.string.private_chat_with),AppConstants.BLANK_SPACE+mUser.firstName)
				imgItemIcon.setImageResource(R.drawable.ic_private_chat)
			}
			rootView.setOnClickListener {
				mBottomSheetPopupDialog.dismiss()
				when (hostControl.hostControlType) {
					HostControlsEnum.MUTE -> {
						mUser.id?.let { id ->
							mUser.audio.id?.let {
								mMeetingUserViewModel.muteUnmuteUser(id, it, true)
							}
						}
						MixpanelMuteGuestsEvent(Mixpanel.INDIVIDUAL)
					}
					HostControlsEnum.UNMUTE -> {
						mUser.id?.let { id ->
							mUser.audio.id?.let {
								mMeetingUserViewModel.muteUnmuteUser(id, it, false)
							}
						}
						MixpanelUnmuteGuestsEvent(Mixpanel.INDIVIDUAL)
					}
					HostControlsEnum.PROMOTE -> mMeetingUserViewModel.updateUserRole(Objects.requireNonNull(mUser), true)
					HostControlsEnum.DEMOTE -> mMeetingUserViewModel.updateUserRole(Objects.requireNonNull(mUser), false)
					HostControlsEnum.DISMISS -> mMeetingUserViewModel.dismissUser(Objects.requireNonNull(mUser))
					HostControlsEnum.DISMISS_AUDIO_PARTICIPANT -> {
						mUser.audio.id?.let {
							mMeetingUserViewModel.dismissAudioUser(it)
						}
					}
					HostControlsEnum.PRIVATE_CHAT_WITH ->
						mUser.let { it -> onShowChatDetailPage(it)
							mMeetingUserViewModel.logMixPanelEventForNewPrivateChat(AppConstants.PRIVATE_CHAT,mMeetingUserViewModel.userType,
								AppConstants.PRIVATE_CHAT_FROM_PARTICIPANT_LIST)
						}
					else -> {

					}
				}
			}
			rootView
		} else{
			convertView
		}
		return root
	}

	private fun MixpanelMuteGuestsEvent(type: Mixpanel) {
		mlogger.userModel.role = Mixpanel.MEETING_HOST.value
		mlogger.mixpanelItem1 = type
		val msg = MIXPANEL_EVENT + "Mute Guests"
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MUTE_GUESTS, msg, null,
				null, false, true)
	}

	private fun MixpanelUnmuteGuestsEvent(type: Mixpanel) {
		mlogger.userModel.role = Mixpanel.MEETING_HOST.value
		mlogger.mixpanelItem1 = type
		val msg = MIXPANEL_EVENT + "Unmute Guests"
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNMUTE_GUESTS, msg, null,
				null, false, true)
	}

	fun onShowChatDetailPage(mUser: User) {
		val chatItem = Chat()
		if (!mUser.name.isNullOrEmpty()) {
			chatItem.firstName = mUser.firstName.toString()
			chatItem.lastName = mUser.lastName.toString()
		}
		chatItem.profileImage = mUser.profileImage
		chatItem.initials = mUser.initials
		chatItem.webPartId = mUser.id.toString()
		mMeetingUserViewModel?.setUserRole(mUser, mContext)
		mMeetingUserViewModel.isOpenChatFragment.postValue(true)
		// call the method to display ChatDetails fragment
		(mContext as? WebMeetingActivity)?.onShowChatDetailPage(chatItem)
	}
}