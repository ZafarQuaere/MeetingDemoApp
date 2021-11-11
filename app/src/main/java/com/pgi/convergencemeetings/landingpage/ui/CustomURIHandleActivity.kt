package com.pgi.convergencemeetings.landingpage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.enums.PgiUserTypes
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

class CustomURIHandleActivity : BaseActivity() {
	private var mFurl: String? = null // This we should force to be non null
	private var mIsHost = false
	protected var isAuthorized = false

	@JvmField
  @BindView(R.id.ll_fullscreen_progress_bar)
	var llProgressLayout: LinearLayout? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initializeNewRelic(this.applicationContext)
		setInterActionName(Interactions.APP_LINK.interaction)
		mlogger = CoreApplication.mLogger
		setContentView(R.layout.activity_custom_uri_handle)
		ButterKnife.bind(this)
		val userType = AppAuthUtils.getInstance().pgiUserType
		mlogger.userModel.type = userType ?: PgiUserTypes.USER_TYPE_GUEST.toString()
		if (userType == null || userType == PgiUserTypes.USER_TYPE_GUEST.toString()) {
			mIsHost = false
			mlogger.setUserId(AppAuthUtils.getInstance().emailId)
		} else {
			mIsHost = true
			mlogger.setUserId(ClientInfoDaoUtils.getInstance().clientId)
		}
		isAuthorized = PGiIdentityAuthService.getInstance(this.applicationContext).authState().getCurrent().isAuthorized
		handleAppLinking()
	}

	public override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		mlogger.record(LogEvent.FEATURE_FURLLINKHANDLER)
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.FURLLINKHANDLER,
				"CustomURIHandleActivity onNewIntent() - Got new app link intent", null, null, true,
				false)
		handleAppLinking()
	}

	override fun onStart() {
		super.onStart()
	}

	private fun handleAppLinking() {
		mlogger.endMetric(LogEvent.METRIC_COLD_START, "CustomURIHandleActivity - App Startup in " +
				"Seconds")
		val intent = intent
		if (intent != null) {
			val data = intent.data
			if (data != null) {
				mFurl = if (data.getQueryParameter(AppConstants.KEY_FURL) != null) data.getQueryParameter(AppConstants.KEY_FURL) else data.toString()
				if (isAuthorized) {
					launchRoom(mFurl, JoinMeetingEntryPoint.EXTERNAL_LINK, mIsHost, this)
				} else {
					launchLoginFlow()
				}
			}
		}
	}

	private fun launchLoginFlow() {
		val onBoardIntent = Intent(this, OnBoardAuthActivity::class.java)
		onBoardIntent.putExtra(AppConstants.KEY_FROM_URI, true)
		startActivityForResult(onBoardIntent, LOGIN_REQUEST_CODE)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (mFurl != null) {
				launchRoom(mFurl, JoinMeetingEntryPoint.EXTERNAL_LINK, mIsHost, this)
			}
		}
	}

	companion object {
		private val TAG = CustomURIHandleActivity::class.java.name
		const val LOGIN_REQUEST_CODE = 10
	}
}