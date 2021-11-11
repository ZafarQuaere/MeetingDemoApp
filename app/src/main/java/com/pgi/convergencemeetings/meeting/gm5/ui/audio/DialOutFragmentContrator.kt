package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone

/**
 * Created by ashwanikumar on 12/21/2017.
 */
interface DialOutFragmentContrator {
	interface activity {
		fun showAudioFragment()
		fun showDialOutSelectionFragment()
		fun showDialOutFragment(selectedPhoneNumber: Phone)
		fun handleCallMeClick(selectedPhoneNumber: Phone?)
	}
}