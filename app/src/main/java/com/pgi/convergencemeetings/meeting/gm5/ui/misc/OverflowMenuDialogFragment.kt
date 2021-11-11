package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.newrelic.agent.android.NewRelic
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.logging.enums.Interactions

class OverflowMenuDialogFragment : BottomSheetDialogFragment() {
  @BindView(R.id.tv_change_connection)
	lateinit var tvChangeConnection: TextView

	@BindView(R.id.tv_turn_low_bandwidth_on)
	lateinit var tvLowBandwidthOn: TextView

	@BindView(R.id.tv_turn_low_bandwidth_off)
	lateinit var tvLowBandwidthOff: TextView

	@BindView(R.id.tv_disconnect_audio)
	lateinit var tvDisconnectAudio: TextView

  @BindView(R.id.tv_mute_all_participants)
	lateinit var tvMuteAllParticipants: TextView

  @BindView(R.id.tv_unmute_all_participants)
	lateinit var tvUnmuteAllParticipants: TextView

	@BindView(R.id.tv_speaker_turn_on)
	lateinit var tvSpeakerTurnOn: TextView

	@BindView(R.id.tv_speaker_turn_off)
	lateinit var tvSpeakerTurnOff: TextView

  @BindView(R.id.tv_start_recording)
	lateinit var tvStartRecording: TextView

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsHost = false
	fun setIsHost(value: Boolean) {
		mIsHost = value
	}

    internal var mIsWaitngRoomEnabled = false
	fun setIsWaitngRoomEnabled(value: Boolean) {
		mIsWaitngRoomEnabled = value
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mAreAllMuted = false
	fun setAreAllMuted(value: Boolean) {
		mAreAllMuted = value
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsPSTNAvailable = false
	var mIsVoipConnected =false
	fun setIsPSTNAvailable(value: Boolean, isVoip: Boolean) {
		mIsPSTNAvailable = value
		mIsVoipConnected = isVoip
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsSpeakerOn = false
	fun setIsSpeakerOn(value: Boolean) {
		mIsSpeakerOn = value
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsLowBandwidthOn = false
	fun setIsLowBandwidthOn(value: Boolean) {
		mIsLowBandwidthOn = value
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsRecording = false
	fun setIsRecording(value: Boolean) {
		mIsRecording = value
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mContext: Context? = null
	fun setContext(context: Context?) {
		mContext = context
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		if (mIsHost) {
			if (mIsRecording) {
				tvStartRecording.setText(R.string.stop_recording)
			} else {
				tvStartRecording.setText(R.string.start_recording)
			}
			if (mAreAllMuted) {
				tvMuteAllParticipants.visibility = View.GONE
				tvUnmuteAllParticipants.visibility = View.VISIBLE
			} else {
				tvMuteAllParticipants.visibility = View.VISIBLE
				tvUnmuteAllParticipants.visibility = View.GONE
			}
		} else {
			tvMuteAllParticipants.visibility = View.GONE
			tvStartRecording.visibility = View.GONE
			tvUnmuteAllParticipants.visibility = View.GONE
		}
		// The following should be on the menu for both guests and hosts
		if (mIsSpeakerOn) {
			tvSpeakerTurnOff.visibility = View.VISIBLE
			tvSpeakerTurnOn.visibility = View.GONE
		} else {
			tvSpeakerTurnOff.visibility = View.GONE
			tvSpeakerTurnOn.visibility = View.VISIBLE
		}
		// menu option will be shown based on pstn availablity and voip call status
		if(!mIsPSTNAvailable && mIsVoipConnected) {
			tvChangeConnection.visibility = View.GONE
			tvDisconnectAudio.visibility = View.VISIBLE
		}
		else {
			tvChangeConnection.visibility = View.VISIBLE
			tvDisconnectAudio.visibility = View.GONE
		}
		updateBandhwidth()
	}
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.OVERFLOW_MENU.interaction)
	}

	fun updateBandhwidth(){
		if (mIsLowBandwidthOn) {
			tvLowBandwidthOn.visibility = View.VISIBLE
			tvLowBandwidthOff.visibility = View.GONE
		} else {
			tvLowBandwidthOn.visibility = View.GONE
			tvLowBandwidthOff.visibility = View.VISIBLE
		}
	}

	override fun onCreateView(inflater: LayoutInflater,
														container: ViewGroup?,
														savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.overflow_menu_bottom_sheet, container,
				false)
		ButterKnife.bind(this, view)
		return view
	}

	@OnClick(R.id.tv_change_connection)
	fun onChangeConnectionClicked() {
		dismiss()
		val activity = mContext as WebMeetingActivity?
		activity?.onChangeAudioClicked()
	}

	// change menu option once disconnect audio clicked.
	@OnClick(R.id.tv_disconnect_audio)
	fun onDisconnectAudioClicked() {
		dismiss()
		val activity = mContext as WebMeetingActivity?
		activity?.onDisconnectAudioClicked()
		tvChangeConnection.visibility = View.VISIBLE
		tvDisconnectAudio.visibility = View.GONE
	}

	@OnClick(R.id.tv_turn_low_bandwidth_on)
	fun onLowBandwidthOffClicked() {
		handleLowBandwidthClick()
	}
	@OnClick(R.id.tv_turn_low_bandwidth_off)
	fun onLowBandwidthOnClicked() {
		handleLowBandwidthClick()
	}

	private fun handleLowBandwidthClick() {
		dismiss()
		(mContext as WebMeetingActivity).onLowBandwidthClicked()
	}

	@OnClick(R.id.tv_mute_all_participants)
	fun onMuteAllParticipantsClicked() {
		dismiss()
		(mContext as WebMeetingActivity).onMuteAllClicked()
	}

	@OnClick(R.id.tv_unmute_all_participants)
	fun onUnmuteAllParticipantsClicked() {
		dismiss()
		(mContext as WebMeetingActivity).onMuteAllClicked()
	}

	@OnClick(R.id.tv_speaker_turn_on)
	fun onSpeakerTurnOn() {
		handleSpeakerClick()
	}

	@OnClick(R.id.tv_speaker_turn_off)
	fun onSpeakerTurnOff() {
		handleSpeakerClick()
	}

	private fun handleSpeakerClick() {
		dismiss()
		(mContext as WebMeetingActivity).onSpeakerClicked()
	}

	@OnClick(R.id.tv_start_recording)
	fun onStartRecordingClicked() {
		dismiss()
		if (mIsRecording) {
			(mContext as WebMeetingActivity).onStopRecordingClicked()
		} else {
			(mContext as WebMeetingActivity).onStartRecordingClicked()
		}
	}

	companion object {
		fun newInstance(): OverflowMenuDialogFragment {
			return OverflowMenuDialogFragment()
		}
	}
}