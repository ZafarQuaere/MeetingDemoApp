package com.pgi.convergencemeetings.base.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.newrelic.agent.android.NewRelic
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm4.ui.GuestMeetingRoomActivity
import com.pgi.convergencemeetings.meeting.gm4.ui.HostMeetingRoomActivity
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.models.About
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryCallbacks
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository.Companion.INSTANCE
import io.reactivex.disposables.Disposable
import net.openid.appauth.AuthorizationException
import org.koin.android.ext.android.getKoin
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by amit1829 on 10/31/2017.
 */
open class BaseActivity : AppCompatActivity() {
	protected var context: Context? = null
	protected var mRetryCallbacks: RetryCallbacks? = null
	private val TAG = BaseActivity::class.java.simpleName
	private var mDisposable: Disposable? = null
	private var showNetworkSnack = false
	private var snackParent: View? = null
	protected var mlogger: Logger = CoreApplication.mLogger
	var test: Boolean = false

	public override fun onCreate(savedInstanceState: Bundle?) {
		try {
			super.onCreate(savedInstanceState)
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
				requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
			}
		} catch (e: NoSuchMethodException) {
			super.onCreate(null)
		}
		context = this
		mDisposable = PGiIdentityAuthService.getInstance(CoreApplication.appContext).authExceptionSubject
				.subscribe { e: AuthorizationException? -> redirectToLoginPage() }
	}

	protected fun setInterActionName(name: String?) {
		NewRelic.setInteractionName(name)
	}

	override fun onDestroy() {
		super.onDestroy()
		mDisposable?.dispose()
	}

	protected fun initializeNewRelic(context: Context?) {
		if (!BuildConfig.DEBUG) {
			NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_KEY)
					.withCrashReportingEnabled(true)
					.withHttpResponseBodyCaptureEnabled(true)
					.withInteractionTracing(true)
					.withLoggingEnabled(true)
					.start(context)
		}
	}

	protected open fun initRetryCallBacks() {
		mRetryCallbacks = RetryCallbacks(this, this)
	}

	open fun onServiceRetryFailed(retryStatus: RetryStatus) {
		mRetryCallbacks?.onServiceRetryFailed(retryStatus)
	}

	fun redirectToLoginPage() {
		val intent = Intent(this, OnBoardAuthActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		startActivity(intent)
		finish()
	}

	fun openEmailApp(activity: WeakReference<Activity>, toOrganizer: Array<String>, emailSubject: String?, message: String?) {
		var subject = emailSubject
		subject = subject ?: AppConstants.EMPTY_STRING
		val mailContent = Uri.parse(AppConstants.MESSAGE_MAIL_TO)
		val intent = Intent(Intent.ACTION_SENDTO)
		intent.putExtra(Intent.EXTRA_EMAIL, toOrganizer)
		intent.putExtra(Intent.EXTRA_TEXT, message ?: AppConstants.EMPTY_STRING)
		intent.putExtra(Intent.EXTRA_SUBJECT, subject)
		intent.data = mailContent
		activity.get()?.let {
			if (intent.resolveActivity(it.packageManager) != null) {
				it.startActivity(intent)
			}
		}
	}

	fun showInvalidConferenceAlert(context: Context?) {
		if (!isFinishing) {
			context?.let {
				val dialog = AlertDialog.Builder(context)
						.setTitle(it.getString(R.string.invalid_url_meeting_prompt_title))
						.setMessage(it.getString(R.string.invalid_url_meeting_prompt_message))
						.setPositiveButton(context.getString(R.string.dialog_ok), null).create()
				dialog.show()
			}
		}
	}

	fun getAboutItems(context: Context): List<About> {
		val aboutItems: MutableList<About> = ArrayList(1)
		val aboutItem1 = About(R.drawable.ic_about_carat_btn, context.getString(R.string.software_credits))
		aboutItems.add(aboutItem1)
		return aboutItems
	}

	fun clearAppData(context: Context?) {
		try {
			PGiIdentityAuthService.getInstance(this.applicationContext).signOut()
			if (context != null) {
				val applicationDao = ApplicationDao.get(context)
				if (applicationDao != null) {
					applicationDao.deleteAllExecptLogs()
					applicationDao.deleteSearchOnly()
				}
				CommonUtils.checkSharedPrefManagerInstance(context)
			}
			val mSharedPreferencesManager = SharedPreferencesManager.getInstance()
			mSharedPreferencesManager?.clearPrefOnSignOut()
			mlogger.clearClientId()
			mlogger.clearModels()
            val featureManager: FeaturesManager = getKoin().get()
            featureManager.clear()
		} catch (ex: Exception) {
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.SQL, "BaseActivity clearAppData()", ex, null, true, false)
		}
	}

	@SuppressLint("CheckResult")
	fun launchRoom(url: String?, entryPoint: JoinMeetingEntryPoint, isHost: Boolean , intentActivity : Activity? = null) {
		if (url == null || url.isEmpty()) {
			showInvalidConferenceAlert(context)
			mlogger.record(LogEvent.METRIC_FAILED_MEETING_JOINS)
			mlogger.error(
					TAG, LogEvent.ERROR, LogEventValue.FURLLINKHANDLER,
					"BaseActivity launchRoom() - cannot join meeting as furl is null or empty", null, null, true, false)
		} else if (InternetConnection.isConnected(this)) {
			val elasticSearchRepository = INSTANCE
			elasticSearchRepository.getMeetingRoomInfoFromFurl(url, false).subscribe({ result: SearchResult ->
				if(entryPoint == JoinMeetingEntryPoint.EXTERNAL_LINK) {
					intentActivity?.finish()
				}
				                                                                         ClientInfoResultCache.getInstance().selectedMeetingRoomId = result.meetingRoomId
				                                                                         launchAudioSelectionActivity(
						                                                                         context,
						                                                                         Integer.toString(result.conferenceId),
						                                                                         result.furl, AppAuthUtils.getInstance().firstName,
						                                                                         result.useHtml5, System.currentTimeMillis(),
						                                                                         entryPoint, isHost, result
				                                                                                                     )
			                                                                         }
			                                                                        ) { error: Throwable? ->
				showInvalidConferenceAlert(context)
				mlogger.record(LogEvent.METRIC_FAILED_MEETING_JOINS)
				mlogger.warn(
						TAG, LogEvent.ERROR, LogEventValue.FURLLINKHANDLER,
						"BaseActivity launchRoom() - cannot join meeting", null, null, true, false)
			}
		} else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
		}
	}

	fun launchAudioSelectionActivity(context: Context?, conferenceId: String?,
	                                 furl: String?, firstName: String?,
	                                 isUseHtml5: Boolean, currentTime: Long,
	                                 entryPoint: JoinMeetingEntryPoint,
	                                 isHost: Boolean, searchResult: SearchResult? = null) {
		if (InternetConnection.isConnected(context)) {
			ApplicationDao.get(CoreApplication.appContext).saveRecentMeeting(searchResult)
			val audioSelectionIntent: Intent = if (isUseHtml5) {
				CoreApplication.mLogger.startMetric(LogEvent.METRIC_JOIN_GM5_MEETING)
				Intent(context, WebMeetingActivity::class.java)
			} else {
				CoreApplication.mLogger.startMetric(LogEvent.METRIC_JOIN_GM4_MEETING)
				if (isHost) {
					Intent(context, HostMeetingRoomActivity::class.java)
				} else {
					Intent(context, GuestMeetingRoomActivity::class.java)
				}
			}
			audioSelectionIntent.putExtra(AppConstants.IS_MEETING_HOST, false)
			audioSelectionIntent.putExtra(AppConstants.MEETING_CONFERENCE_ID, conferenceId)
			audioSelectionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
			audioSelectionIntent.putExtra(AppConstants.KEY_FURL, furl)
			audioSelectionIntent.putExtra(AppConstants.FIRST_NAME, firstName)
			audioSelectionIntent.putExtra(AppConstants.JOIN_MEETING_ENTRY_POINT, entryPoint.getValue())
			context?.let {
				it.startActivity(audioSelectionIntent)
			}
		} else {
			Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show()
		}
	}


	fun setConnectionValues(){
		val tm: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
		var plmn: String? = null
		if(test){
			 plmn  = "310456"
		}
		else {
			 plmn  = tm.getNetworkOperator()
		}
		if(!plmn.isNullOrEmpty()){
			val mcc = plmn.substring(0, 3)
			val mnc = plmn.substring(3, plmn.length)
			mlogger.mixpanelNetworkChangeModel.mobileCountryCode = mcc.toInt()
			mlogger.mixpanelNetworkChangeModel.mobileNetworkCode = mnc.toInt()
			println("plmn[$plmn] mcc[$mcc] mnc[$mnc]")
		}
		if(InternetConnection.isConnectedMobile(this) && !InternetConnection.isConnectedWifi(this))  {
			mlogger.mixpanelNetworkChangeModel.networkType = "Cellular"
		}
		else {
			mlogger.mixpanelNetworkChangeModel.networkType = "Wifi"
		}
	}

	fun logOpenApp(){
		val msg = AppConstants.MIXPANEL_EVENT + AppConstants.OPEN_APP
		mlogger.info(TAG, com.pgi.logging.enums.LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_OPEN_APP, msg,
				null, null, false, true)
	}
}