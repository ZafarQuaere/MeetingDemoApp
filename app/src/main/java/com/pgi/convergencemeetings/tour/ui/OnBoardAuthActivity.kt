package com.pgi.convergencemeetings.tour.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import butterknife.BindView
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ConnectionState
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm4.ui.HostMeetingRoomActivity
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.models.enterUrlModel.Detail
import com.pgi.convergencemeetings.search.ui.SearchActivity
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.convergencemeetings.utils.ConferenceManager
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.LogTags
import com.pgi.network.models.SearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import org.greenrobot.greendao.annotation.NotNull

/**
 * This class handle application login flow and shows application tour along with handles first time guest use cases.
 * It also shows permission or enter url flow depending up on the use case.
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@UnstableDefault
class OnBoardAuthActivity : BaseActivity(), WelcomeFragment.Callback, OnBoardAuthContractor.view, FirstTimeGuestActionListener {
	private lateinit var fragmentManager: FragmentManager
	private var viewMode = 0
	private var mSharedPreferencesManager = SharedPreferencesManager.getInstance();
	private var mOnBoardAuthPresenter: OnBoardAuthPresenter? = null

	/**
	 * This method informs if the app is in foreground or not.
	 *
	 * @return the boolean
	 */
	var isAppEnterForeground = false
		private set
	private var mResultCode = Activity.RESULT_CANCELED
	private var hasHandledAuthIntent = false
	private var mClientId: String? = null
	private var mEmail: String? = null
	private var mFirstName: String? = null
	private var mNavigationIntent = Intent()
	private var mAppAuthUtils = AppAuthUtils.getInstance()
	private var mConferenceId: String? = null
	private val logTags = arrayOf(LogTags.APP_LAUNCH.eventName)
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public var firstTimeGuestFromBackground = false

	fun getViewMode(): Int {
		return viewMode
	}
	@JvmField
  @BindView(R.id.fl_enter_url_fragment)
	var flEnterUrl: FrameLayout? = null

	@VisibleForTesting
	fun setPresenter(presenter: OnBoardAuthPresenter?) {
		mOnBoardAuthPresenter = presenter
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.ON_BOARDING.interaction)
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING,
				"OnBoardAuthActivity onCreate()", null, logTags, true, false)
		super.initRetryCallBacks()
		mSharedPreferencesManager = SharedPreferencesManager.getInstance()
		mOnBoardAuthPresenter = OnBoardAuthPresenter(this, this)
		setContentView(R.layout.activity_single_fragment)
		fragmentManager = supportFragmentManager
		if (null == savedInstanceState) {
			transitionToBlankView()
		} else {
			viewMode = savedInstanceState.getInt(KEY_VIEW_MODE)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mOnBoardAuthPresenter?.destroy()
	}

	public override fun onResume() {
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING,
				"OnBoardAuthActivity onResume()", null, logTags, true, false)
		super.onResume()
		checkAndLoadEnterUrlFragment()
	}

	fun checkAndLoadEnterUrlFragment() {
		if(firstTimeGuestFromBackground) {
			addEnterUrlFragment()
		} else {
			firstTimeGuestFromBackground = false
			enablePostAuthorizationFlows()
		}
	}

	public override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		checkIntent(intent)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt(KEY_VIEW_MODE, viewMode)
	}

	override fun notifyDismissRequested() {
		mSharedPreferencesManager.setHasShownWelcomeSlides(true)
		viewMode = VIEW_MODE_AUTH
	}

	override fun continueButtonClicked() {
		mOnBoardAuthPresenter?.startAppAuthorization()
	}

	override fun signInButtonClicked() {
		mOnBoardAuthPresenter?.startAppAuthorization()
	}

	private fun enablePostAuthorizationFlows() {
		if (hasHandledAuthIntent) {
			mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity enablePostAuthorizationFlows" +
					"() - Transitioning to Blank Fragment", null, logTags, true, false)
			transitionToBlankView()
		} else if (!shouldShowWelcomeView()) {
			mlogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity " +
					"enablePostAuthorizationFlows() - Starting App Authorization", null, logTags, true, false)
			mOnBoardAuthPresenter?.startAppAuthorization()
		} else {
			mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity " +
					"enablePostAuthorizationFlows() - Transitioning to Welcome Fragment", null, logTags, true, false)
			transitionToWelcomeView()
		}
	}

	private fun transitionToWelcomeView() {
		transitionToView(WelcomeFragment.newInstance(FragmentBinder.BINDING_ACTIVITY))
		viewMode = VIEW_MODE_WELCOME
	}

	private fun transitionToBlankView() {
		val blankFragment = BlankFragment.newInstance(FragmentBinder.BINDING_NONE)
		blankFragment.showSpinner = hasHandledAuthIntent
		transitionToView(blankFragment)
		viewMode = VIEW_MODE_BLANK
	}

	private fun transitionToView(@NotNull fragment: Fragment) {
		try {
			fragmentManager.beginTransaction()
					.replace(R.id.fragment_container, fragment)
					.commit()
		} catch (ise: IllegalStateException) {
			try {
				fragmentManager.beginTransaction()
						.replace(R.id.fragment_container, fragment)
						.commitAllowingStateLoss()
			} catch (ex: Exception) {
				mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ONBORADING,
						"OnBoardAutActivity - Failed to transition fragments", ex, logTags, true, false)
			}
		} catch (ex: Exception) {
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ONBORADING, "OnBoardAuthActivity" +
					" - Failed to transition fragments", ex, logTags, true, false)
		}
	}

	fun shouldShowWelcomeView(): Boolean {
		return (mSharedPreferencesManager?.hasShownWelcomeSlides() == false) && mSharedPreferencesManager?.isFirstTimeGuestUser ?: false
	}

	override fun onAuthenticationError(s: String) {}
	override fun showProgress() {}
	override fun hideProgress() {}
	override fun onClientInfoReceived(clientId: String?, conferenceId: String?, email: String?) {
		mClientId = clientId
		mConferenceId = conferenceId
		mEmail = email
		clientId?.let {
			mlogger.setUserId(mClientId)
			setDefaultMeetingRoom()
			sendRecentsRequest()
		}
		email?.let {
			mlogger.setEmail(mEmail)
			if(clientId == null){
				setDefaultMeetingRoom()
				sendRecentsRequest()
			}
		}
		checkForFirstTimeGuest(true)
	}

	override fun onClientInfoError(errMsg: String) {
		if (errMsg.equals(AppConstants.JWT_ACCESS_TOKEN_EXPIRED, ignoreCase = true)
				|| errMsg.equals(AppConstants.JWT_ACCESS_TOKEN_VALIDATION_FAILURE, ignoreCase = true)) {
			Toast.makeText(this, R.string.invalid_access_token, Toast.LENGTH_SHORT).show()
			clearAppData(this)
		}
	}

	override fun updateClientInfo() {
		val intent = Intent(AppConstants.UPDATE_CLIENT_INFO_BROADCAST)
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
	}

	override fun sendRecentsRequest() {
		if (CommonUtils.isConnectionAvailable(this)) {
			mClientId?.let {
				mOnBoardAuthPresenter?.getRecentMeetingInfo(it)
			}
		}
	}

	override fun onServiceRetryFailed(retryStatus: RetryStatus) {
		super.onServiceRetryFailed(retryStatus)
	}

	override fun sendToLoginPage() {
		super.redirectToLoginPage()
	}

	fun checkForFirstTimeGuest(isFirstLaunch: Boolean) {
		if (AppAuthUtils.getInstance().isUserTypeGuest && (mSharedPreferencesManager?.isFirstTimeGuestUser == true) && mResultCode != Activity.RESULT_OK) {
			SharedPreferencesManager.getInstance().firstTimeGuestUserForThankYou(true)
			addEnterUrlFragment()
		} else if (isFirstLaunch) {
			launchApplicationFlow(isFirstLaunch)
		}
		ElkTransactionIDUtils.resetTransactionId()
	}

	private fun addEnterUrlFragment() {
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity " +
				"addEnterUrlFragment() - Showing Enter Url Fragment for Guest", null,
				logTags, true, false)
		firstTimeGuestFromBackground = true
		flEnterUrl?.visibility = View.VISIBLE
		val fragment = EnterUrlFragment()
		val bundle = Bundle()
		bundle.putBoolean(AppConstants.KEY_ENTER_URL_FOR_NEW_GUEST, true)
		fragment.arguments = bundle
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.fragment_container, fragment)
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		fragmentTransaction.addToBackStack(AppConstants.KEY_ENTER_URL_FOR_NEW_GUEST)
		fragmentTransaction.commitAllowingStateLoss()
		viewMode = VIEW_MODE_ENTER_URL
	}

	private fun setDefaultMeetingRoom() {
		val clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
		clientInfoDaoUtils.refereshClientInfoResultDao()
		val meetingRoomList = clientInfoDaoUtils.meetingRooms
		if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
			val meetingRoom = meetingRoomList[0]
			if (meetingRoom != null) {
				val meetingRoomId = meetingRoom.meetingRoomId
				if (meetingRoomId != null) {
					mSharedPreferencesManager.prefDefaultMeetingRoom = meetingRoomId
				}
				mlogger.userModel.furl = meetingRoom.meetingRoomUrls.attendeeJoinUrl
			}
		}
	}

	fun launchApplicationFlow(isFirstLaunch: Boolean) {
		if (!isFirstLaunch) {
			mNavigationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		}
		if (mResultCode == Activity.RESULT_OK) {
			SharedPreferencesManager.getInstance().firstTimeGuestUserForThankYou(true)
			mNavigationIntent.setClass(this, OnBoardAuthActivity::class.java)
			setResult(mResultCode)
		} else if (mSharedPreferencesManager?.hasClickedStart() == true) {
			startMeeting()
			viewMode = VIEW_MODE_START_MEETING
			try {
				startActivity(mNavigationIntent)
			} catch (e: Exception) {
				e.message?.let { mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ONBORADING, it, e, null, false, false) }
			}
		} else if (mSharedPreferencesManager?.hasJoinedUsersRoom() == true) {
			viewMode = VIEW_MODE_JOIN_ROOM
			joinRoom()
		} else if (mSharedPreferencesManager?.hasClickedJoin() == true) {
			mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity " +
					"launchApplicationFlow() - Launching intent for join Meeting Activity ",
					null, null, true, false)
			mNavigationIntent.setClass(this, SearchActivity::class.java)
			if (mClientId != null) {
				mNavigationIntent.putExtra(AppConstants.CLIENT_ID, mClientId)
			}
			viewMode = VIEW_MODE_JOIN_MEETING
			try {
				startActivity(mNavigationIntent)
			} catch (e: Exception) {
				e.message?.let { mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ONBORADING, it, e, null, false, false) }
			}
		} else {
			mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING, "OnBoardAuthActivity launchApplicationFlow() - " +
					"Launching intent for Home Activity", null, null, true, false)
			mNavigationIntent.setClass(this, AppBaseLayoutActivity::class.java)
			viewMode = VIEW_MODE_ENTER_MEETING
			try {
				startActivity(mNavigationIntent)
			} catch (e: Exception) {
				e.message?.let { mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ONBORADING, it, e, null, false, false) }
			}
		}
		finish()
	}

	/**
	 * This method starts the meeting after refreshing the token, if the user has started the meeting and token was expired.
	 */
	fun startMeeting() {
		if (CommonUtils.isConnectionAvailable(this)) {
			ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO, ConnectionState.DISCONNECTED)
			val clientInfoDoautils = ClientInfoDaoUtils.getInstance()
			if (clientInfoDoautils != null) {
				clientInfoDoautils.refereshClientInfoResultDao()
				val conferenceId = clientInfoDoautils.conferenceId
				if (mAppAuthUtils != null) {
					mFirstName = mAppAuthUtils.firstName
					if (clientInfoDoautils.isUseHtml5) {
						val meetingRoomData = clientInfoDoautils.meetingRoomData
						if (meetingRoomData != null) {
							val meetingRoomUrls = meetingRoomData.meetingRoomUrls
							if (meetingRoomUrls != null) {
								val attendeeJoinUrl = meetingRoomUrls.attendeeJoinUrl
								if (attendeeJoinUrl != null && !attendeeJoinUrl.isEmpty()) {
									launchAudioSelectionActivity(this, conferenceId, attendeeJoinUrl,
											mFirstName, true, System.currentTimeMillis(),
											JoinMeetingEntryPoint.UNKNOWN, false)
								}
							}
						}
					} else {
						mNavigationIntent = Intent(this, HostMeetingRoomActivity::class.java)
						mNavigationIntent.setClass(this, HostMeetingRoomActivity::class.java)
						mNavigationIntent.putExtra(AppConstants.FIRST_NAME, mFirstName)
						mNavigationIntent.putExtra(AppConstants.IS_MEETING_HOST, true)
						mNavigationIntent.putExtra(AppConstants.MEETING_CONFERENCE_ID, conferenceId)
					}
				}
			}
		} else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
		}
	}

	fun joinRoom() {
		if (CommonUtils.isConnectionAvailable(this)) {
			ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO, ConnectionState.DISCONNECTED)
			val clientInfoDoautils = ClientInfoResultCache.getInstance()
			if (clientInfoDoautils != null && mConferenceId != null) {
				val meetingRoomData = clientInfoDoautils.meetingRoomData
				if (meetingRoomData != null) {
					val meetingRoomUrls = meetingRoomData.meetingRoomUrls
					if (meetingRoomUrls != null) {
						val attendeeJoinUrl = meetingRoomUrls.attendeeJoinUrl
						if (attendeeJoinUrl != null && !attendeeJoinUrl.isEmpty()) {
							launchAudioSelectionActivity(this, mConferenceId, attendeeJoinUrl,
									clientInfoDoautils.firstName, false, System.currentTimeMillis(),
									JoinMeetingEntryPoint.UNKNOWN, false)
						}
					}
				}
			}
		} else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
		}
	}

	override fun onBackClicked() {
		finish()
	}

	override fun onEnterUrlFragmentSkipped() {
		launchApplicationFlow(false)
	}

	override fun onJoinMeetingCalled(detail: Detail) {
		launchApplicationFlow(false)
		if (detail != null) {
			val useHtml5 = detail.useHtml5
			val conferenceId = detail.conferenceId.toString()
			val furl = detail.meetingUrl
			if (furl != null) {
				launchAudioSelectionActivity(this, conferenceId, furl, "", useHtml5, System.currentTimeMillis(),
						JoinMeetingEntryPoint.UNKNOWN, false, detail.searchResult)
			} else {
				mlogger.warn(TAG, LogEvent.ERROR, LogEventValue.UAPI_JOINMEETING,
						"OnBoardAuthActivity onJoinMeetingCalled() - Furl is null, cannot join " +
								"meeting", null, null, true, false)
			}
		} else {
			mlogger.warn(TAG, LogEvent.ERROR, LogEventValue.UAPI_JOINMEETING, "OnBoardAuthActivity " +
					"onJoinMeetingCalled() - Meeting info is " +
					"null, cannot join meeting", null, null, true, false)
		}
	}

	override fun onStart() {
		super.onStart()
		isAppEnterForeground = true
		val intent = intent
		if (intent != null && intent.extras != null && intent.getBooleanExtra(AppConstants.KEY_FROM_URI, false)) {
			mResultCode = Activity.RESULT_OK
		}
	}

	override fun onStop() {
		super.onStop()
		isAppEnterForeground = false
	}

	private fun checkIntent(intent: Intent?) {
		if (intent != null) {
			val action = intent.action
			if (action != null) {
				when (action) {
					AppConstants.AUTHORIZATION_RESPONSE -> if (!intent.hasExtra(USED_INTENT)) {
						intent.putExtra(USED_INTENT, true)
						hasHandledAuthIntent = true
						mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING,
								"OnBoardAuthActivity checkIntent() - Got Auth Code from App Auth",
								null, null, true, false)
						mlogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.ONBORADING,
								"OnBoardAuthActivity redirectUri -"+intent.data,
								null, null, true, true)
						mOnBoardAuthPresenter?.handleAuthorizationResponse(intent)
					}
				}
			}
		}
	}

	override fun onBackPressed() {
		moveTaskToBack(true)
	}

	companion object {
		private const val KEY_VIEW_MODE = "KEY_VIEW_MODE"
		private const val VIEW_MODE_WELCOME = 1
		private const val VIEW_MODE_AUTH = 2
		private const val VIEW_MODE_BLANK = 3
		private const val VIEW_MODE_ENTER_MEETING = 4
		private const val VIEW_MODE_ENTER_URL = 5
		private const val VIEW_MODE_JOIN_MEETING = 6
		private const val VIEW_MODE_START_MEETING = 7
		private const val VIEW_MODE_JOIN_ROOM = 8
		private const val USED_INTENT = "USED_INTENT"
		private val TAG = OnBoardAuthActivity::class.java.simpleName
	}
}