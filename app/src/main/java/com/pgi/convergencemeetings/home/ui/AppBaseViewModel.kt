package com.pgi.convergencemeetings.home.ui

import androidx.lifecycle.ViewModel
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.home.services.LogoutUserService
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.Logger
import io.reactivex.Observable
import kotlinx.serialization.UnstableDefault

@UnstableDefault
class AppBaseViewModel(
		val appAuth: AppAuthUtils,
		val clientInfo: ClientInfoDaoUtils,
		val logoutServie: LogoutUserService,
		val sharedViewModel: SharedViewModel,
		val featuresManager: FeaturesManager,
		val logger: Logger,
		val sharedPreferencesManager: SharedPreferencesManager) : ViewModel() {

	private val TAG = AppBaseViewModel::class.java.simpleName

	init {
		if (clientInfo.clientId != null) {
			logger.setUserId(clientInfo.clientId)
		} else if (appAuth.emailId != null) {
			logger.setEmail(appAuth.emailId)
		}
	}

	fun getProfilePic(): String? {
		return appAuth.profileImage
	}

	fun getUserFirstName(): String? {
		return appAuth.firstName
	}

	fun getInitials(): String? {
		return if (appAuth.firstName != null && appAuth.lastName != null) {
			CommonUtils.getNameInitials(appAuth.firstName, appAuth.lastName)
		} else {
			null
		}
	}

	fun getUserLastName(): String? {
		return appAuth.lastName
	}

	fun isHost(): Boolean {
		return !appAuth.isUserTypeGuest
	}

	fun getFullName(): String? {
		return if (appAuth.firstName != null && appAuth.lastName != null) {
			CommonUtils.getFullName(appAuth.firstName, appAuth.lastName)
		} else {
			null
		}
	}

	fun getJoinUrl(): String? {
		var joinUrl: String? = null
		val meetingRoomList = clientInfo.meetingRooms
		if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
			for (meetingRoom in meetingRoomList) {
				if(meetingRoom.meetingRoomId.equals(sharedPreferencesManager.prefDefaultMeetingRoom)) {
					val meetingRoomUrls = meetingRoom.meetingRoomUrls
					if (meetingRoomUrls != null) {
						joinUrl = meetingRoomUrls.attendeeJoinUrl
						sharedPreferencesManager.joinUrl = joinUrl
					}
				}
			}
		}
		return joinUrl
	}

	fun signOutUser(): Observable<String> {
		return logoutServie.logoutUser(appAuth.refreshToken)
	}

	fun revokeUsersToken(): Observable<String> {
		return logoutServie.revokeTokenLogoutUser(appAuth.refreshToken)
	}

	fun getConfId(): String? {
		return clientInfo.conferenceId
	}
}