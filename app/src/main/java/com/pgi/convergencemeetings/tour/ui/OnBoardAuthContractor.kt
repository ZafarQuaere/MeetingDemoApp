package com.pgi.convergencemeetings.tour.ui

import android.content.Intent
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus

/**
 * Created by ashwanikumar on 8/11/2017.
 */
interface OnBoardAuthContractor {
	interface view {
		fun onAuthenticationError(s: String)
		fun showProgress()
		fun hideProgress()
		fun onClientInfoReceived(clientId: String?, conferenceId: String?, email: String?)
		fun onClientInfoError(s: String)
		fun updateClientInfo()
		fun sendRecentsRequest()
		fun onServiceRetryFailed(retryStatus: RetryStatus)
		fun sendToLoginPage()
	}

	interface presenter {
		fun startAppAuthorization()
		fun handleAuthorizationResponse(intent: Intent)
		fun getRecentMeetingInfo(clientId: String)
	}
}