package com.pgi.convergencemeetings.meeting.gm4.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.MeetingParticipant
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.logging.enums.Interactions
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * GuestMeetingRoomActivity extends MeetingRoomActivity to generate a view for GM4 meeting rooms for Guests.
 * Class implements the seprate logic for guests, like guest can leave the meeting but can't end a meeting.
 *
 */
@ObsoleteCoroutinesApi
@UnstableDefault
class GuestMeetingRoomActivity : MeetingRoomActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.GM4_GUEST_ROOM.interaction)
	}

	override fun initViews() {
		btnCallSetting.visibility = View.VISIBLE
		tvMuteAll.visibility = View.GONE
	}

	override fun handleExitLeaveMeeting() {
		mAlertDialog = createGuestExitDialog()
		mAlertDialog?.show()
	}

	override fun dismissExitLeaveMeetingDialogIfShowing() {
		if (mAlertDialog?.isShowing == true) {
			mAlertDialog?.dismiss()
		}
	}

	override fun onPositiveClicked() {
		//logic for leave meeting goes here
		stopSoftPhone()
		val participantId = currentParticipantId
		mMeetingRoomPresenter?.clearSocket()
		if (participantId != null) {
			mMeetingRoomPresenter?.leaveConference(participantId)
		} else {
			mMeetingRoomPresenter?.clearConferenceWatch()
		}
		mAlertDialog?.dismiss()
	}

	override fun onNegativeClicked() {
		//Not required for Guest...
	}

	override fun onNeutralClicked() {
		mAlertDialog?.dismiss()
	}

	/**
	 * Create guest exit dialog.
	 *
	 * @return the dialog
	 */
	fun createGuestExitDialog(): Dialog {
		val alertDialog = Dialog(this)
		alertDialog.setContentView(R.layout.guest_exit_dialog)
		val positiveBtn = alertDialog.findViewById<View>(R.id.tv_guest_dialog_yes) as TextView
		val negtiveBtn = alertDialog.findViewById<View>(R.id.tv_guest_dialog_no) as TextView
		positiveBtn.setOnClickListener { onPositiveClicked() }
		negtiveBtn.setOnClickListener { onNeutralClicked() }
		return alertDialog
	}

	override fun disableMuteButton() {}
	override fun enableMuteButton() {}
	override fun disableMuteAllButton() {}
	override fun enableMuteAllButton() {}
	override fun setAllMuted(mute: Boolean) {
		TODO("Not yet implemented")
	}

	override var meetingParticipants: List<MeetingParticipant> = emptyList()

	override fun setUserMuted(mute: Boolean) {
		btnMicToggle.isChecked = mute
	}

	override fun handleDoNotConnectAudio() {}

	override fun showDialOutSelectionFragment() {
	}

	override fun showDialOutFragment(selectedPhoneNumber: Phone) {
	}


	override fun handleDialIn(name: String) {
		TODO("Not yet implemented")
	}

	override fun handledialinsearch() {
		TODO("Not yet implemented")
	}

	override fun showDialInFragment() {
		TODO("Not yet implemented")
	}

	override fun showDialInSelectionFragment() {
		TODO("Not yet implemented")
	}

	public override fun showInternetConnectionTimeoutDialog() {}

	override val isSpeakerEnabled: Boolean
		get() = btnSpeakerToggle.isChecked
}