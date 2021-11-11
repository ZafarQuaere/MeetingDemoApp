package com.pgi.convergencemeetings.landingpage.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.enums.PgiUserTypes
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.AppUpgrade.Companion.downloadAppInfoFromPlayStore
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoCallBack
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoService
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.model.MeetingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * Created by ashwanikumar on 11/13/2017.
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SplashScreenActivity : BaseActivity(), ClientInfoCallBack {
	private val SPLASH_DISPLAY_LENGTH = 2000
	private val mSharedPreferencesManager = SharedPreferencesManager.getInstance()
	private val TAG = SplashScreenActivity::class.java.simpleName
	private val logTags: Array<String>? = null
	private var pendingClientInfo = false

	@UnstableDefault
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initializeNewRelic(this.applicationContext)
		setContentView(R.layout.splash_screen)
		setInterActionName(Interactions.SPLASH_SCREEN.interaction)
		val handler = Handler()
		handler.postDelayed({
			enablePostAuthorizationFlows()
			setConnectionValues()
			// Need to come back to this and see why we have this check
			//				if (mlogger != null) {
			//				} else {
			//					Log.e(TAG, "logger is null")
			//					handler.postDelayed(this, 100)
			//				}
		}, SPLASH_DISPLAY_LENGTH.toLong())
		downloadAppInfoFromPlayStore(this, 45000)
	}

	@UnstableDefault
	fun enablePostAuthorizationFlows() {
		val authService = PGiIdentityAuthService.getInstance(this.applicationContext)
		if (authService.authState().getCurrent().isAuthorized) {
			val mAppAuthUtils = AppAuthUtils.getInstance()
			mlogger.userModel.firstName = mAppAuthUtils.firstName
			mlogger.userModel.lastName = mAppAuthUtils.lastName
			mlogger.userModel.email = mAppAuthUtils.emailId
			mlogger.userModel.type = mAppAuthUtils.pgiUserType
			if (PgiUserTypes.USER_TYPE_GUEST.toString().equals(mAppAuthUtils.pgiUserType, ignoreCase = true)) {
				mlogger.userModel.id = mAppAuthUtils.emailId
			} else {
				val clientInfoDoautils = ClientInfoDaoUtils.getInstance()
				clientInfoDoautils.refereshClientInfoResultDao()
				val meetingRoom = clientInfoDoautils.meetingRoomData
				if (meetingRoom != null) {
					val meetingRoomUrls = meetingRoom.meetingRoomUrls
					val meetingUrl = meetingRoomUrls.attendeeJoinUrl
					mlogger.userModel.furl = meetingUrl
					mlogger.setUserId(clientInfoDoautils.clientId)
				} else {
					pendingClientInfo = true
					clientInfo
				}
			}
			val telephonyManager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
			try {
				val carrier = telephonyManager.networkOperatorName
				mlogger.userModel.carrier = carrier
			} catch (ex: Exception) {
				mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME,
						"HomeActivity onStart() - TelephoneManager Failed to get Carrier Info", ex, null,
						true, false)
			}
			if (!pendingClientInfo) {
				launchApplicationFlow()
			}
		} else {
			clearAppData(CoreApplication.appContext)
			mlogger.deleteAllLogs()
			val mainIntent = Intent(this@SplashScreenActivity, OnBoardAuthActivity::class.java)
			startActivity(mainIntent)
			finish()
		}
		mlogger.meetingState = MeetingState();
		mlogger.endMetric(LogEvent.METRIC_COLD_START, "SplashScreenActivity - App startup in " +
				"Seconds")
	}

	private val clientInfo: Unit
		private get() {
			val clientInfoService = ClientInfoService(this, this)
			clientInfoService.getClientInfo()
		}

	@UnstableDefault
	fun launchApplicationFlow() {
		mlogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.SPLASHSCREEN, "User is authorized", null,
				logTags, true, false)
		val navigationIntent = Intent()
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.SPLASHSCREEN, "SplaschScreenActivity launchApplicationFlow() - Launching intent for " +
				"Home Activity", null,
				logTags, true, false)
		navigationIntent.setClass(this, AppBaseLayoutActivity::class.java)
		navigationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(navigationIntent)
		overridePendingTransition(0, 0)
		finish()
	}

	@UnstableDefault
	override fun onClientInfoSuccess(clientId: String, conferenceId: String) {
		pendingClientInfo = false
		val clientInfoDoautils = ClientInfoDaoUtils.getInstance()
		clientInfoDoautils.refereshClientInfoResultDao()
		val meetingRoom = clientInfoDoautils.meetingRoomData
		if (meetingRoom != null) {
			val meetingRoomUrls = meetingRoom.meetingRoomUrls
			val meetingUrl = meetingRoomUrls.attendeeJoinUrl
			mlogger.userModel.furl = meetingUrl
			mlogger.setUserId(clientInfoDoautils.clientId)
		}
		launchApplicationFlow()
	}

	override fun onClientInfoError(errMsg: String, response: Int) {
		pendingClientInfo = false
		val msg = "$TAG $errMsg"
		mlogger.error(TAG, LogEvent.ERROR, LogEventValue.SPLASHSCREEN, msg, null, null, false, false)
	}
}