package com.pgi.convergencemeetings.search.ui

import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.network.models.ImeetingRoomInfo

/**
 * Created by amit1829 on 8/29/2017.
 */
interface JoinMeetingContractor {
	interface View {
		fun notifyAdapterOnRecentUpdate(recentResults: List<ImeetingRoomInfo>?)
		fun notifyAdapterOnSearchUpdate(roomResults: List<ImeetingRoomInfo>?)
		fun onRecentMeetingInfoError(errorMsg: String?, response: Int)
		fun showProgress()
		fun hideProgress()
		fun onServiceRetryFailedParent(retryStatus: RetryStatus)
	}

	interface presenter {
		fun getRecentMeetingInfo(clientId: String)
		fun loadLocalData()
	}
}