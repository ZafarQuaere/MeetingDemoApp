package com.pgi.convergencemeetings.meeting.gm5.ui

import android.content.Context
import android.widget.Toast
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.meeting.BaseMeetingPresenter
import com.pgi.convergencemeetings.models.BaseModel
import com.pgi.convergencemeetings.models.ClientInfoResponse
import com.pgi.convergencemeetings.services.RecentMeetingService
import com.pgi.convergencemeetings.services.RecentMeetingServiceCallbacks
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateLastUsedPhoneService
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateLastUserPhoneServiceCallbacks
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.SearchResult
import java.util.*

class WebMeetingPresenter(context: Context?) : BaseMeetingPresenter(context),
																							 RecentMeetingServiceCallbacks<BaseModel>,
																							 UpdateLastUserPhoneServiceCallbacks<BaseModel> {

	private val mlogger: Logger = CoreApplication.mLogger
	private val mAppAuthUtils: AppAuthUtils
	private var mRecentMeetingService: RecentMeetingService
	private var mUpdateLastUsedPhoneService: UpdateLastUsedPhoneService
	private var mIsMeetingHost = false

	// for use with unit tests
	fun setRecentMeetingService(recentMeetingService: RecentMeetingService) {
		mRecentMeetingService = recentMeetingService
	}

	// for use with unit tests
	fun setUpdateLastUsedPhoneService(updateLastUsedPhoneService: UpdateLastUsedPhoneService) {
		mUpdateLastUsedPhoneService = updateLastUsedPhoneService
	}

	fun getOthersClientInfo(conferenceId: String?, isMeetingHost: Boolean) {
		mIsMeetingHost = isMeetingHost
		mRecentMeetingService.getOthersClientInfo(conferenceId)
	}

	override fun onRecentMeetingSuccessCallback() {
		var tempVal = java.lang.Boolean.FALSE
		val applicationDao = ApplicationDao.get(CoreApplication.appContext)
		val searchResults = applicationDao.allRecentMeetings
		val meetingRoomData = ClientInfoResultCache.getInstance().meetingRoomData
		var desktopId: Int? = null
		if (meetingRoomData != null) {
			for (searchResult in searchResults) {
				val hubConfId = searchResult.hubConfId
				if (hubConfId != null && hubConfId.toString() == meetingRoomData
								.meetingRoomId) {
					desktopId = searchResult.desktopMeetingId
					tempVal = java.lang.Boolean.TRUE
					break
				}
			}
			if (tempVal) {
				updateMeeting(desktopId.toString())
			} else {
				fetchMeetingRoom(meetingRoomData.meetingRoomId)
			}
		}
	}

	override fun onRecentMeetingErrorCallback(errorMsg: String, response: Int) {
		// Since this is a background task we wont notify user
	}

	override fun onClientInfoError(errorMsg: Int, response: Int) {
		// Since this is a background task we wont notify user
	}

	override fun onClientInfoSuccess(clientInfoResponse: ClientInfoResponse?) {
	}

	private fun prepareSearchResultsList(searchResults: List<SearchResult>,
																			 newResult: SearchResult): List<SearchResult> {
		val tempSearchResults: MutableList<SearchResult> = ArrayList()
		if (searchResults.size >= AppConstants.RECENT_MEETINGS_MAX_LIMIT) {
			searchResults.sorted()
			searchResults.dropLast(AppConstants.RECENT_MEETINGS_MAX_LIMIT - 1)
		}
		for (searchResult in searchResults) {
			val searchResultHubConfId = searchResult.hubConfId
			if (searchResultHubConfId == null || searchResultHubConfId != newResult.hubConfId) {
				tempSearchResults.add(searchResult)
			}
		}
		tempSearchResults.add(newResult)
		return tempSearchResults
	}

	fun updateHostLastDialOutPhoneNumber(clientId: String?, mCountryCode: String?, mPhoneNumber: String?, mExtension: String?) {
		mUpdateLastUsedPhoneService.updateLastUsedPhoneNumber(clientId, false, mCountryCode, mPhoneNumber, mExtension)
	}

	override fun onUpdateLastUsedPhoneNumberSuccess() {
		// Since this is a background task we wont notify user
	}

	override fun onUpdateLastUsedPhoneNumberError(errorMsg: String, response: Int) {
		// Since this is a background task we wont notify user
	}

	companion object {
		private val TAG = WebMeetingPresenter::class.java.simpleName
	}

	init {
		mRecentMeetingService = RecentMeetingService(this)
		mUpdateLastUsedPhoneService = UpdateLastUsedPhoneService(this)
		mAppAuthUtils = AppAuthUtils.getInstance()
	}
}