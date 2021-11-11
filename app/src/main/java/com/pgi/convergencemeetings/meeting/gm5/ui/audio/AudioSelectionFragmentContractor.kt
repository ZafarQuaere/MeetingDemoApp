package com.pgi.convergencemeetings.meeting.gm5.ui.audio

/**
 * Created by ashwanikumar on 12/20/2017.
 */
interface AudioSelectionFragmentContractor {
	interface activity {
		fun finishActivity(showThankYouFragment: Boolean, resultCode: Int)
		fun startSoftphone()
		fun startVoipMeeting()
		fun setAudioBehaviour(isMuteMe: Boolean, isActivateSpeaker: Boolean)
		fun closeAudioPanel()
		fun handleDialInAction()
		fun handleCallMyPhoneClick()
		fun onNeedPermission(permission: String)
		fun onPermissionPreviouslyDenied(permission: String)
		val useHtml5: Boolean
		fun handleDoNotConnectAudio()
		fun handleDialIn(mobile:String)
        fun muteUnmuteChangeConnection(isMute: Boolean)
		fun resumeActivity()
		fun handledialinsearch()
	}

	interface fragment
}