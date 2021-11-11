package com.pgi.convergencemeetings.tour.ui

import com.pgi.convergencemeetings.models.enterUrlModel.Detail
import com.pgi.convergencemeetings.models.enterUrlModel.Result

/**
 * Created by nnennaiheke on 9/6/17.
 */
interface EnterUrlContractor {
	interface View {
		fun notifyAdapterOnEnterUrlSearchUpdate(results: List<Result>)
		fun onEnterUrlError()
		fun launchAudioSelectionScreen(detail: Detail)
		fun showInvalidUrlAlert()
		fun showProgress()
		fun hideProgress()
		fun closeFragment(joinMeeting: Boolean)
		fun sendToLoginPage()
	}

	interface presenter {
		fun getMeetingSearch(furl: String)
	}
}