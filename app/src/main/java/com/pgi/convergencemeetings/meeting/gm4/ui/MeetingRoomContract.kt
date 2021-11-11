package com.pgi.convergencemeetings.meeting.gm4.ui

import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.models.MeetingParticipant
import com.pgi.convergencemeetings.models.Talker
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone

/**
 * Contractor for GM4 meeting view and presenter.
 */
interface MeetingRoomContract {
	/**
	 * The interface View.
	 */
	interface view {
		/**
		 * Show meeting locked screen.
		 */
		fun showMeetingLockedScreen()

		/**
		 * Show meeting locked screen.
		 */
		fun showMeetingFullScreen()

		/**
		 * Show audio loading screen.
		 */
		fun showAudioLoadingScreen()

		/**
		 * Show audio selection view.
		 */
		fun showAudioSelectionView()

		/**
		 * Show loading screen.
		 */
		fun showLoadingScreen()

		/**
		 * Hide loading screen.
		 */
		fun hideLoadingScreen()

		/**
		 * Update active talker.
		 *
		 * @param talker the talker
		 */
		fun updateActiveTalker(talker: Talker)

		/**
		 * Update participants.
		 *
		 * @param participant the participant
		 */
		fun updateParticipants(participant: MeetingParticipant)

		/**
		 * Update canMute on entry.
		 */
		fun updateMuteOnEntry()

		/**
		 * Start softphone.
		 */
		fun connectSoftPhone(mFormattedSipUri: String)

		/**
		 * Disable canMute button.
		 */
		fun disableMuteButton()

		/**
		 * Enable canMute button.
		 */
		fun enableMuteButton()

		/**
		 * Disable canMute all button.
		 */
		fun disableMuteAllButton()

		/**
		 * Enable canMute all button.
		 */
		fun enableMuteAllButton()

		/**
		 * Sets user muted.
		 *
		 * @param mute the canMute
		 */
		fun setUserMuted(mute: Boolean)

		/**
		 * Sets all muted.
		 *
		 * @param mute the canMute
		 */
		fun setAllMuted(mute: Boolean)

		/**
		 * Gets meeting participants.
		 *
		 * @return the meeting participants
		 */
		var meetingParticipants: List<MeetingParticipant>

		/**
		 * Stop soft phone.
		 */
		fun stopSoftPhone()

		/**
		 * Finish activity.
		 */
		fun finishActivity()

		/**
		 * Gets phone number.
		 *
		 * @return the phone number
		 */
		val phoneNumber: String?

		/**
		 * Enable vo ip button.
		 */
		fun enableVoIPButton()

		/**
		 * Disable vo ip button.
		 */
		fun disableVoIPButton()

		/**
		 * Enable dial in button.
		 */
		fun enableDialInButton()

		/**
		 * Disable dial in button.
		 */
		fun disableDialInButton()

		/**
		 * Open thank you fragment.
		 */
		fun openFeedbackFragment()

		/**
		 * Gets country code.
		 *
		 * @return the country code
		 */
		val countryCode: String?

		/**
		 * Gets extension.
		 *
		 * @return the extension
		 */
		val extension: String?

		/**
		 * Phone numbers received successfully.
		 *
		 * @param phones the phones
		 */
		fun phoneNumbersReceivedSuccessfully(phones: List<Phone>)

		/**
		 * Show thank you fragment.
		 */
		fun showFeedbackFragment()

		/**
		 * Is meeting host boolean.
		 *
		 * @return the boolean
		 */
		val isMeetingHost: Boolean

		/**
		 * Sets first name.
		 *
		 * @param firstName the first name
		 */
		fun setFirstName(firstName: String)

		/**
		 * Show active meeting notification.
		 */
		fun showActiveMeetingNotification()

		/**
		 * On service retry failed.
		 *
		 * @param retryStatus the retry status
		 */
		fun onServiceRetryFailed(retryStatus: RetryStatus)
	}

	/**
	 * The interface Presenter.
	 */
	interface presenter {
		/**
		 * Start conference.
		 *
		 * @param conferenceId the conference id
		 */
		fun startConference(conferenceId: String)

		/**
		 * Start conference watch.
		 *
		 * @param sessionId    the session id
		 * @param conferenceId the conference id
		 */
		fun startConferenceWatch(sessionId: String, conferenceId: String)

		/**
		 * End conference.
		 */
		fun endConference()

		/**
		 * Clear conference watch.
		 */
		fun clearConferenceWatch()

		/**
		 * Leave conference.
		 *
		 * @param participantId the participant id
		 */
		fun leaveConference(participantId: String)

		/**
		 * Handle canMute button click.
		 *
		 * @param isMuted the is muted
		 */
		fun handleMuteButtonClick(isMuted: Boolean)

		/**
		 * Handle canMute all click.
		 *
		 * @param b the b
		 */
		fun handleMuteAllClick(b: Boolean)

		/**
		 * Subscribe to meeting.
		 *
		 * @param confId the conf id
		 */
		fun subscribeToMeeting(confId: String)

		/**
		 * Start voip meeting.
		 *
		 * @param isMuteMe          the is canMute me
		 * @param isActivateSpeaker the is activate speaker
		 * @param isMeetingHost     the is meeting host
		 */
		fun startVoipMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean)

		/**
		 * Clean up meeting state.
		 */
		fun cleanUpMeetingState()

		/**
		 * Start dial in meeting.
		 *
		 * @param isMuteMe          the is canMute me
		 * @param isActivateSpeaker the is activate speaker
		 * @param isMeetingHost     the is meeting host
		 * @param phoneNumber       the phone number
		 */
		fun startDialInMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, phoneNumber: String)

		/**
		 * Gets active talker participant.
		 *
		 * @param mMeetingParticipants the m meeting participants
		 * @param s                    the s
		 * @return the active talker participant
		 */
		fun getActiveTalkerParticipant(mMeetingParticipants: List<MeetingParticipant>, s: String): MeetingParticipant?

		/**
		 * Prepare participants data list.
		 *
		 * @param mMeetingParticipants the m meeting participants
		 * @param participant          the participant
		 * @return the list
		 */
		fun prepareParticipantsData(mMeetingParticipants: List<MeetingParticipant>, participant: MeetingParticipant): List<MeetingParticipant>

		/**
		 * Clear socket.
		 */
		fun clearSocket()

		/**
		 * Sets meeting host status.
		 *
		 * @param isMeetingHost the is meeting host
		 */
		fun setMeetingHostStatus(isMeetingHost: Boolean)

		/**
		 * Start dial out meeting.
		 *
		 * @param isMuteMe            the is canMute me
		 * @param isActivateSpeaker   the is activate speaker
		 * @param isMeetingHost       the is meeting host
		 * @param selectedPhoneNumber the selected phone number
		 */
		fun startDialOutMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, selectedPhoneNumber: Phone)

		/**
		 * Gets dial out phone numbers.
		 */
		val dialOutPhoneNumbers: Unit

		fun createSipFromAddress(): String?
	}
}