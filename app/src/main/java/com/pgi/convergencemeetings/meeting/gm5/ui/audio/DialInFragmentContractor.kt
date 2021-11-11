package com.pgi.convergencemeetings.meeting.gm5.ui.audio

/**
 * Created by ashwanikumar on 12/20/2017.
 */
interface DialInFragmentContractor {
	interface DialInActivityContract {
		fun showAudioFragment()
		fun handleDialInClick(mSelectedPhoneNumber: String)
		fun showDialInFragment()
		fun showDialInSelectionFragment()
	}
}