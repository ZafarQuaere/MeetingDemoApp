package com.pgi.convergencemeetings.meeting.gm4.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.pgi.convergence.constants.ELKConstants
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.MeetingParticipant
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.ConferenceManager
import com.pgi.logging.enums.Interactions
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * HostMeetingRoomActivity extends MeetingRoomActivity to generate a view for GM4 meeting rooms for Host.
 * Class implements the seprate logic for hosts, like host can leave the meeting or can end the meeting.
 *
 */
@UnstableDefault
@ObsoleteCoroutinesApi
class HostMeetingRoomActivity : MeetingRoomActivity() {

	private val TAG = HostMeetingRoomActivity::class.java.simpleName
	private var isAllMuted = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.GM4_HOST_ROOM.interaction)
	}

	override fun initViews() {
		btnCallSetting.visibility = View.VISIBLE
		tvMuteAll.visibility = View.VISIBLE
	}

	override fun handleExitLeaveMeeting() {
		mAlertDialog = createHostExitAlertDialog()
		mAlertDialog?.show()
	}

	override fun dismissExitLeaveMeetingDialogIfShowing() {
		if (mAlertDialog?.isShowing == true) {
			mAlertDialog?.dismiss()
		}
	}

	override fun onPositiveClicked() {
		//logic for end meeting goes here
		stopSoftPhone()
		mMeetingRoomPresenter?.clearSocket()
//		logElkEvent(ELKConstants.ROOMEVENT_ENDMEETING)
		mMeetingRoomPresenter?.endConference()
		mAlertDialog?.dismiss()
	}

	override fun onNegativeClicked() {
		//disconnect softphone..
		stopSoftPhone()
		mMeetingRoomPresenter?.clearSocket()
		//logic for leave meeting goes here
		val participantId = currentParticipantId
		if (participantId != null) {
			mMeetingRoomPresenter?.leaveConference(participantId)
		} else {
			mMeetingRoomPresenter?.clearConferenceWatch()
		}
		mAlertDialog?.dismiss()
	}

	override fun onNeutralClicked() {
		mAlertDialog?.dismiss()
	}

	/**
	 * Create host exit alert dialog dialog.
	 *
	 * @return the dialog
	 */
	fun createHostExitAlertDialog(): Dialog {
		val alertDialog = Dialog(this)
		alertDialog.setContentView(R.layout.host_exit_dialog)
		val positiveBtn = alertDialog.findViewById<View>(R.id.tv_host_dialog_exit) as TextView
		val negtiveBtn = alertDialog.findViewById<View>(R.id.tv_host_dialog_leave) as TextView
		val neutralBtn = alertDialog.findViewById<View>(R.id.tv_host_dialog_cancel) as TextView
		positiveBtn.setOnClickListener { onPositiveClicked() }
		negtiveBtn.setOnClickListener { onNegativeClicked() }
		neutralBtn.setOnClickListener { onNeutralClicked() }
		return alertDialog
	}

	override fun disableMuteButton() {}
	override fun enableMuteButton() {}
	override fun disableMuteAllButton() {
		runOnUiThread { tvMuteAll.isEnabled = false }
	}

	override fun enableMuteAllButton() {
		runOnUiThread {
			tvMuteAll.isEnabled = true
			if (isAllMuted) {
				tvMuteAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.audiomeeting_muteall_mute_icon, 0)
			} else {
				tvMuteAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mic_hd, 0)
			}
		}
	}

	override fun setUserMuted(mute: Boolean) {
		btnMicToggle.isChecked = mute
	}

	override fun setAllMuted(mute: Boolean) {
		if (isAllMuted && !mute) {
			isAllMuted = mute
			enableMuteAllButton()
		}
	}

	override var meetingParticipants: List<MeetingParticipant> = emptyList()
	/**
	 * Click action for the meeting host, when Meeting host clicks canMute all button on GM4 meeting rooms,
	 * to canMute all participants together.
	 */
	@OnClick(R.id.tv_mute_all)
	fun onMuteAllClicked() {
		isAllMuted = if (isAllMuted) {
			mMeetingRoomPresenter?.handleMuteAllClick(false)
			tvMuteAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mic_hd, 0)
			false
		} else {
			mMeetingRoomPresenter?.handleMuteAllClick(true)
			tvMuteAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.audiomeeting_muteall_mute_icon, 0)
			true
		}
	}

	override fun handleDoNotConnectAudio() {}

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