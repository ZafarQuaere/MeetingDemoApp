package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone

/**
 * Created by amit1829 on 10/4/2017.
 */
interface AudioSelectionContractor {
	interface view {
		fun showLoadingScreen()
		fun hideLoadingScreen()
		fun enableVoIPButton()
		fun disableVoIPButton()
		fun enableDialInButton()
		fun disableDialInButton()
		fun finishActivity()
		fun phoneNumbersReceivedSuccessfully(mPhoneNumberList: List<Phone?>?)
	}

	interface presenter {
		val dialOutPhoneNumbers: Unit
		fun startVoipMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean)
		fun startDialInMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, phoneNumber: String)
		fun startDialOutMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, phone: Phone)
		fun startAudioMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean)
		fun subscribeToMeeting(conferenceId: String)
		fun unSubscribeMeeting()
		fun setMeetingHostStatus(mIsMeetingHost: Boolean)
	}
}