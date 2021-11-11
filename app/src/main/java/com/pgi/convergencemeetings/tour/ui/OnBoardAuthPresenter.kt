package com.pgi.convergencemeetings.tour.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.newrelic.agent.android.NewRelic
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.PgiUserTypes
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.landingpage.ui.SplashScreenActivity
import com.pgi.convergencemeetings.models.BaseModel
import com.pgi.convergencemeetings.models.ClientInfoResponse
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoCallBack
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoService
import com.pgi.convergencemeetings.services.RecentMeetingService
import com.pgi.convergencemeetings.services.RecentMeetingServiceCallbacks
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import io.reactivex.disposables.Disposable
import net.openid.appauth.AuthorizationException
import net.openid.appauth.TokenResponse

/**
 * Created by ashwanikumar on 8/11/2017.
 */
class OnBoardAuthPresenter(
		private val mView: OnBoardAuthContractor.view,
		private val mContext: Context) : OnBoardAuthContractor.presenter,
																		 ClientInfoCallBack,
																		 RecentMeetingServiceCallbacks<BaseModel> {

	private val logger: Logger = CoreApplication.mLogger
	private var mClientId: String? = null
	private var mConferenceId: String? = null
	private var hasReturnedClientInfo = false
	private var reasonFailed = 0
	private var responseCode = 0
	private val mPGiAuthService: PGiIdentityAuthService = PGiIdentityAuthService.getInstance(mContext.applicationContext)
	private val mAppAuthUtils: AppAuthUtils = AppAuthUtils.getInstance()
	private val mRecentMeetingService: RecentMeetingService = RecentMeetingService(this)
	private var mDisposable: Disposable? = null
	private var nrInteractionId: String? = null
	private var needMixpanelLoginEvent = false

	fun destroy() {
		if (mDisposable != null) {
			mDisposable?.dispose()
		}
		mPGiAuthService.destroy()
	}

	override fun getRecentMeetingInfo(clientId: String) {
		logger.debug(LOG_TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthPresenter " +
				"getRecentMeetingInfo()", null, null, false, false)
		mRecentMeetingService.getRecentMeetingInfo(clientId)
	}

	override fun startAppAuthorization() {
		needMixpanelLoginEvent = true
		nrInteractionId = NewRelic.startInteraction("Identity Authorization")
		logger.debug(LOG_TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthPresenter " +
				"startAppAuthorization()", null, null, true, false)
		mPGiAuthService.doAuth(mContext, CommonUtils.getLocale(mContext),
				mContext.javaClass.name,
				SplashScreenActivity::class.java.name)
	}

	/**
	 * Exchanges the code, for the [TokenResponse].
	 *
	 * @param intent represents the [Intent] from the Custom Tabs or the System Browser.
	 */
	override fun handleAuthorizationResponse(intent: Intent) {
		mDisposable = mPGiAuthService.tokenSubject.subscribe { tokenResponse: TokenResponse? ->
			NewRelic.endInteraction(nrInteractionId)
			logger.info(LOG_TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthPresenter " +
					"handleAuthorizationResponse() - Got Token for App Auth", null, null, true, false)
			onTokenResponseSuccess()
			if (needMixpanelLoginEvent && PgiUserTypes.USER_TYPE_GUEST.toString().equals(mAppAuthUtils.pgiUserType, ignoreCase = true)) {
				logger.info(LOG_TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOG_IN, MIXPANEL_MSG, null,
						null, false, true)
				needMixpanelLoginEvent = false
			}
		}
		mPGiAuthService.onAuthResponse(intent)
	}

	fun onTokenResponseSuccess() {
		mDisposable?.dispose()
		logger.userModel.firstName = mAppAuthUtils.firstName
		logger.userModel.lastName = mAppAuthUtils.lastName
		logger.userModel.email = mAppAuthUtils.emailId
		logger.userModel.type = mAppAuthUtils.pgiUserType
		if (PgiUserTypes.USER_TYPE_GUEST.toString().equals(mAppAuthUtils.pgiUserType, ignoreCase = true)) {
			mView.onClientInfoReceived( null, null, mAppAuthUtils.emailId)
			logger.userModel.id = mAppAuthUtils.emailId
		} else {
			clientInfo
		}
	}

	private val clientInfo: Unit
		get() {
			logger.debug(LOG_TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthPresenter " +
					"getClientInfo()", null, null, false, false)
			val clientInfoService = ClientInfoService(mContext, this)
			clientInfoService.getClientInfo()
		}

	fun onTokenResponseError(exception: AuthorizationException?) {
		mView.hideProgress()
		ElkTransactionIDUtils.resetTransactionId()
		Toast.makeText(mContext, "Authorization service error", Toast.LENGTH_SHORT).show()
	}

	override fun onClientInfoSuccess(clientId: String, conferenceId: String) {
		mConferenceId = conferenceId
		mClientId = clientId
		logger.userModel.id = mClientId
		logger.debug(LOG_TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthPresenter " +
				"onClientInfoSuccess()", null, null, false, false)
		if (!hasReturnedClientInfo) {
			hasReturnedClientInfo = true
			mView.updateClientInfo()
		}
		mView.onClientInfoReceived(clientId, conferenceId, mAppAuthUtils.emailId)
		if (needMixpanelLoginEvent) {
			logger.info(LOG_TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOG_IN, MIXPANEL_MSG, null,
					null, false, true)
			needMixpanelLoginEvent = false
		}
	}

	override fun onClientInfoError(errMsg: String, response: Int) {
		hasReturnedClientInfo = false
		mView.onClientInfoError(errMsg)
		reasonFailed = RetryStatus.WS_CLIENT_INFO_OTHERS
		sendFailureResponse(response)
	}

	override fun onRecentMeetingSuccessCallback() {}
	override fun onClientInfoSuccess(clientInfoResponse: ClientInfoResponse) {}
	override fun onRecentMeetingErrorCallback(errorMsg: String, response: Int) {}
	override fun onClientInfoError(errorMsg: Int, response: Int) {
		reasonFailed = RetryStatus.WS_CLIENT_INFO_SELF
		sendFailureResponse(response)
	}

	private fun sendFailureResponse(response: Int) {
		responseCode = response
		val mRetryStatus = RetryStatus(responseCode, reasonFailed)
		if (!responseCode.toString().startsWith(AppConstants.CODE_4) && !responseCode.toString().startsWith(AppConstants.CODE_2)) {
			mView.onServiceRetryFailed(mRetryStatus)
		}
	}

	companion object {
		val LOG_TAG = OnBoardAuthPresenter::class.java.simpleName
		private const val MIXPANEL_MSG = "Mixpanel Event: Log In"
	}

}