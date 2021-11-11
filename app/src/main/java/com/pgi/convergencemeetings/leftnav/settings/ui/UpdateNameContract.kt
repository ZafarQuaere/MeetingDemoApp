package com.pgi.convergencemeetings.leftnav.settings.ui

import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus

/**
 * Created by ashwanikumar on 12/14/2017.
 */
interface UpdateNameContract {
	interface view {
		fun onNameUpdateSuccess()
		fun onNameUpdateError()
		fun showProgress()
		fun hideProgress()
		fun sendUpdateNameBroadcast()
		fun onServiceRetryFailed(retryStatus: RetryStatus)
	}

	interface presenter {
		fun requestNameUpdate(clientId: String, firstName: String, lastName: String)
	}
}