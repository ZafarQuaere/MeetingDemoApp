package com.pgi.convergencemeetings.meeting

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergence.enums.NotificationConstants
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneConstants
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.EMPTY
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.clear
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.getSoftPhoneAvailableEvents
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.getSoftPhoneBadNetworkToastEvents
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.getSoftPhoneConnectedEvents
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel.getSoftPhoneSignalLevelEvents
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneService
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneService.Companion.start
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.utils.ConferenceManager
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Abstract base class for meeting room, BaseMeetingActivity class implements the methods which are
 * common for both GM4 and GM5 meeting rooms, and defined some abstract methods, which are specific
 * to individual meeting room, So the sub class can implement these methods which is specific to meeting room.
 */
abstract class BaseMeetingActivity : BaseActivity(), InternetConnectionChangeListener {
  protected var mMeetingUserViewModel: MeetingUserViewModel? = null
  protected var mMeetingEventViewModel: MeetingEventViewModel? = null
  protected var sipIdentifier: String? = null
  protected var mFormattedSipUri: String? = null
  protected var mConferenceId: String? = null
  protected var mFurl: String? = null
  protected var mSoftPhoneAvailable = true // assume it will load
	private val isSoftPhoneRegistered = false
	var reconnectVoip = false
		protected set
	var isSoftPhoneConnected = false
	private val mInternetConnectionHandler = Handler()
	private var mInternetConnectivityChangeReceiver = InternetConnectivityChangeReceiver()
	private var mSoftPhoneDestroyed = true
	protected var mCompositeDisposable = CompositeDisposable()
	protected var enableDolby = true
	private var remoteWebcamCount = 0

	/**
	 * Update signal level.
	 *
	 * @param data the data
	 */
	abstract fun updateSignalLevel(data: String?)

	/**
	 * Dismiss exit leave meeting dialog if showing.
	 */
	protected abstract fun dismissExitLeaveMeetingDialogIfShowing()

	/**
	 * Show no internet connection banner.
	 */
	protected open fun showNoInternetConnectionBanner() {}

	/**
	 * Hide no internet connection banner.
	 */
	protected open fun hideNoInternetConnectionBanner() {}

	/**
	 * Show internet connection timeout dialog.
	 */
	protected abstract fun showInternetConnectionTimeoutDialog()

	/**
	 * Show network switch dialog.
	 */
	protected open fun showNetworkSwitchDialog() {}


	/**
	 * Set network change values.
	 */
	protected open fun setConnectionChangeValues() {
		/*
		//		sonar complaining of empty function
		//		adding nested comment
	 */
	}

	/**
	 * Send user to rejoin meeting.
	 */
	open fun sendUserToRejoinMeeting() {}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		registerInternetConnectivityChangeReceiver()
		mCompositeDisposable.clear()
		clear()
		getSoftPhoneAvailableEvents()
				.filter { v: Any -> v !== EMPTY }
				.map { t -> t as Boolean }
				.subscribeOn(Schedulers.single())
				.subscribe(softPhoneAvailableObserver())
		getSoftPhoneConnectedEvents()
				.filter { v: Any -> v !== EMPTY }
				.map { t -> t as PGiSoftPhoneConstants }
				.subscribeOn(Schedulers.single())
				.subscribe(softPhoneConnectionObserver())
		getSoftPhoneSignalLevelEvents()
				.filter { v: Any -> v !== EMPTY }
				.map { t -> t as String }
				.subscribeOn(Schedulers.single())
				.subscribe(softPhoneSignalLevelObserver())
		getSoftPhoneBadNetworkToastEvents()
				.filter { v: Any -> v !== EMPTY }
				.map { t -> t as Boolean }
				.subscribeOn(Schedulers.single())
				.subscribe(softPhoneRagAlarmToastObserver())
		createNotificationChannel()
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterInternetConnectivityChangeReceiver()
		mInternetConnectionHandler.removeCallbacksAndMessages(null)
		if (!mSoftPhoneDestroyed) {
			if (mSoftPhoneAvailable) {
				createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_DESTROY.name)
			}
			mSoftPhoneDestroyed = true
		}
		mCompositeDisposable.clear()
		clear()
		onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, "")
		SharedPreferencesManager.getInstance().setHasJoinedUsersRoom(false)
		mMeetingUserViewModel?.userInMeeting = false
	}

	private fun softPhoneAvailableObserver(): Observer<Boolean> {
		return object : Observer<Boolean> {
			override fun onSubscribe(d: Disposable) {
				mCompositeDisposable.add(d)
			}

			override fun onNext(aBoolean: Boolean) {
				mSoftPhoneAvailable = aBoolean
			}

			override fun onError(e: Throwable) {
				val msg = "softPhoneAvailableObserver: " + e.message
				mlogger.error(TAG, LogEvent.ERROR, LogEventValue.VOIP_SERVICE, msg, e, null, false, false)
			}

			override fun onComplete() {}
		}
	}

	protected fun softPhoneConnectionObserver(): Observer<PGiSoftPhoneConstants> {
		return object : Observer<PGiSoftPhoneConstants> {
			override fun onSubscribe(d: Disposable) {
				mCompositeDisposable.add(d)
			}

			override fun onNext(state: PGiSoftPhoneConstants) {
				if (state === PGiSoftPhoneConstants.VOIP_CONNECTED) {
					softPhoneConnected()
					isSoftPhoneConnected = true
				} else if (state === PGiSoftPhoneConstants.VOIP_DISCONNECTED) {
					softPhoneDisconnected()
					isSoftPhoneConnected = false
				} else if (state === PGiSoftPhoneConstants.VOIP_RECONNECTED) {
					isSoftPhoneConnected = true
					softPhoneReconnected()
				}
			}

			override fun onError(e: Throwable) {
				val msg = "softPhoneConnectionObserver: " + e.message
				mlogger.error(TAG, LogEvent.ERROR, LogEventValue.VOIP_SERVICE, msg, e, null, false, false)
			}

			override fun onComplete() {}
		}
	}

	private fun softPhoneSignalLevelObserver(): Observer<String> {
		return object : Observer<String> {
			override fun onSubscribe(d: Disposable) {
				mCompositeDisposable.add(d)
			}

			override fun onNext(s: String) {
				updateSignalLevel(s)
			}

			override fun onError(e: Throwable) {
				val msg = "softPhoneSignalLevelObserver: " + e.message
				mlogger.error(TAG, LogEvent.ERROR, LogEventValue.VOIP_SERVICE, msg, e, null, false, false)
			}

			override fun onComplete() {}
		}
	}

	private fun softPhoneRagAlarmToastObserver(): Observer<Boolean> {
		return object : Observer<Boolean> {
			override fun onSubscribe(d: Disposable) {
				mCompositeDisposable.add(d)
			}

			override fun onNext(aBoolean: Boolean) {
				if (aBoolean) {
					if (mMeetingUserViewModel?.isCameraOn!!)
						remoteWebcamCount = mMeetingEventViewModel?.totalWebCam!! - 1;
					else
						remoteWebcamCount = mMeetingEventViewModel?.totalWebCam!!;
					if (mMeetingUserViewModel?.turnWebcamOff!! && remoteWebcamCount > 0) {
						showBadNetworkLowBandwidthToast()
					}
					else {
						showBadNetworkToast()
					}
				}
			}

			override fun onError(e: Throwable) {
				val msg = "softPhoneRagAlarmToastObserver: " + e.message
				mlogger.error(TAG, LogEvent.ERROR, LogEventValue.VOIP_SERVICE, msg, e, null, false, false)
			}

			override fun onComplete() {}
		}
	}

	override fun initRetryCallBacks() {
		super.initRetryCallBacks()
	}

	override fun onServiceRetryFailed(retryStatus: RetryStatus) {
		if (!this.isDestroyed) {
			mRetryCallbacks?.onServiceRetryFailed(retryStatus)
		}
	}

	/**
	 * abstract function to allow this class to query
	 * the state of the speaker UI button
	 */
	abstract val isSpeakerEnabled: Boolean

	/*  audio focus management functions */
	private val mAudioFocusChangeListener: OnAudioFocusChangeListener? = null

	/**
	 * releaseAudioFocus
	 * When a VOIP call is over, this function releases our request
	 * for the audio focus
	 */
	protected fun releaseAudioFocus() {
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_RELEASE_FOCUS.name)
	}

	/**
	 * requestAudioFocus
	 * When we start a VOIP call we need to get the audio focus
	 * which will allow the OS to inform us when we lose the focus
	 * to another application such as when a call comes in.
	 */
	protected fun requestAudioFocus() {
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_REQUEST_FOCUS.name)
	}

	/**
	 * Init softphone call backs.
	 */
	protected fun initSoftphoneCallBacks() {
		if (mSoftPhoneDestroyed) {
			onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, "")
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_INIT.name)
			mSoftPhoneDestroyed = false
		}
	}

	fun reInitSoftPhone() {
		mlogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
				"BaseMeetingActivity reInitSoftPhone()  - RECONNECTING AFTER NETWORK DROP", null,
				null, true, false)
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_INIT.name)
		mSoftPhoneDestroyed = false
	}

	abstract fun startVoipMeeting()

	open fun connectSoftPhone(mFormattedSipUri: String, enableDolby: Boolean) {
		this.mFormattedSipUri = mFormattedSipUri
		this.enableDolby = enableDolby
		onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, "")
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_DIALOUT.name)
	}

	/**
	 * Disconnect softphone.
	 */
	fun destroySoftphone() {
		stopSoftphone()
	}

	protected fun stopSoftphone() {
		reconnectVoip = false
		mSoftPhoneDestroyed = true
	}

	/**
	 * Activate speaker phone.
	 * @param isSpeakerActive   the is speaker active
	 */
	protected fun activateSpeakerPhone(isSpeakerActive: Boolean) {
		if (isSpeakerActive) {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_ACTIVATE_SPEAKER.name)
		} else {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_SET_AUDIO_ROUTE.name)
		}
	}

	protected fun activateMic(isMuted: Boolean) {
		if (isMuted) {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_MIC_MUTED.name)
		} else {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_MIC_ACTIVE.name)
		}
	}

	/**
	 * Sets call state text.
	 *
	 * @param stateText the state text
	 */
	fun setCallStateText(stateText: String?) {
		Log.d(TAG, stateText)
	}

	/**
	 * Sets rag alarm type text.
	 *
	 * @param ragAlarmType the state text
	 */
	fun setRagAlarmType(ragAlarmType: String?) {
		Log.d(TAG, ragAlarmType)
	}

	/**
	 * Sets rag alarm serverity text.
	 *
	 * @param ragAlarmSeverity the state text
	 */
	fun setRagAlarmSeverity(ragAlarmSeverity: String?) {
		Log.d(TAG, ragAlarmSeverity)
	}

	fun hangUpSoftPhone() {
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_STOP.name)
		destroySoftphone() // need to call this to stop the connection monitor
	}

	/**
	 * Soft phone connected.
	 */
	abstract fun softPhoneConnected()
	abstract fun softPhoneReconnected()
	private fun registerInternetConnectivityChangeReceiver() {
		if (mInternetConnectivityChangeReceiver != null) {
			val intentFilter = IntentFilter()
			intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
			registerReceiver(mInternetConnectivityChangeReceiver, intentFilter)
		}
	}

	private fun unregisterInternetConnectivityChangeReceiver() {
		unregisterReceiver(mInternetConnectivityChangeReceiver)
	}

	override fun onInternetConnected() {
		hideNoInternetConnectionBanner()
		mInternetConnectionHandler.removeCallbacksAndMessages(null)
		if (InternetConnection.isConnectedMobile(this)) {
			showNetworkSwitchDialog()
		}
			setConnectionChangeValues()
	}

	override fun onInternetDisconnected() {
		showNoInternetConnectionBanner()
		mInternetConnectionHandler.postDelayed({ showInternetConnectionTimeoutDialog() }, INTERNET_TIME_OUT_RETRY_LATER.toLong())
	}

	/**
	 * The type Internet connectivity change receiver.
	 */
	inner class InternetConnectivityChangeReceiver : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			// Since connections can be flaky wait a sec before taking action
			mInternetConnectionHandler.postDelayed({
				if (InternetConnection.isConnected(context)) {
					mlogger.info(TAG, LogEvent.NETWORK_CONNECTIVITY, LogEventValue.NETWORK_CONNECTED,
							"BaseMeetingActivity - On internet connected", null, null, true, false)
					onInternetConnected()
				} else {
					mlogger.info(TAG, LogEvent.NETWORK_CONNECTIVITY, LogEventValue.NETWORK_DISCONNECTED,
							"BaseMeetingActivity - On internet disconnect", null, null, true, false)
					onInternetDisconnected()
				}
			}, 1000)
		}
	}

	protected fun createSoftPhoneIntent(action: String) {
		val softPhoneIntent = Intent(this, PGiSoftPhoneService::class.java)
		softPhoneIntent.action = action
		softPhoneIntent.putExtra("targetClass", this.javaClass.name)
		softPhoneIntent.putExtra("furl", mFurl)
		mMeetingUserViewModel?.let {
			if (it.isHorsesConfigInitialized()) {
				softPhoneIntent.putExtra("caveInfo", mMeetingUserViewModel?.horsesEnvConfig?.cave)
			}
		}
		if (action == PGiSoftPhoneConstants.VOIP_DIALOUT.name) {
			softPhoneIntent.putExtra("sipFromAddress", sipIdentifier)
			softPhoneIntent.putExtra("formattedSipUrl", mFormattedSipUri)
			softPhoneIntent.putExtra("enableDolby", enableDolby.toString())
		}
		start(this, softPhoneIntent)
	}

	private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
		val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		try {
			for (service in manager.getRunningServices(Int.MAX_VALUE)) {
				if (serviceClass.name == service.service.className) {
					return true
				}
			}
			return false
		} catch (ex: SecurityException) {
			return false
		}
	}

	override fun onKeyDown(keycode: Int, event: KeyEvent): Boolean {
		var isConnectedToVoip = false
		isConnectedToVoip = if (mMeetingUserViewModel != null) {
			mMeetingUserViewModel!!.audioConnType === AudioType.VOIP
		} else {
			ConferenceManager.getConnectionType() == ConnectionType.VOIP
		}
		when (keycode) {
			KeyEvent.KEYCODE_VOLUME_UP -> if (isConnectedToVoip) {
				createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_RAISE_VOLUME.name)
				return true
			}
			KeyEvent.KEYCODE_VOLUME_DOWN -> if (isConnectedToVoip) {
				createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_LOWER_VOLUME.name)
				return true
			}
		}
		return super.onKeyDown(keycode, event)
	}

	/**
	 * Soft phone disconnected.
	 */
	abstract fun softPhoneDisconnected()
	abstract fun showBadNetworkToast()
	abstract fun showBadNetworkLowBandwidthToast()
	abstract fun startRecordingIndicator()
	abstract fun stopRecordingIndicator()
	fun onGoingMeetingIntent(action: String, furl: String?) {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		if (action == NotificationConstants.REMOVE_NOTIFICATION.name) {
			notificationManager.cancel(NotificationId.MEETING_ONGOING)
		} else {
			val notificationIntent = Intent(this, this.javaClass)
			notificationIntent.putExtra(AppConstants.MEETING_CONFERENCE_ID, furl)
			notificationIntent.action = AppConstants.OPEN_ACTIVITY
			notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
			val pendingIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
			val builder = NotificationCompat.Builder(this, AppConstants.ANDROID_CHANNEL_ID)
			builder.setContentTitle(getString(R.string.notification_title))
			builder.setContentText(getString(R.string.notification_content))
			builder.setSmallIcon(R.drawable.notification_icon_white)
			builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			builder.priority = NotificationCompat.PRIORITY_HIGH
			builder.setContentIntent(pendingIntent)
			builder.color = resources.getColor(R.color.brand_color_300)
			builder.setOngoing(true)
			val notification = builder.build()
			notificationManager.notify(NotificationId.MEETING_ONGOING, notification)
		}
	}

	private fun createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name: CharSequence = "GlobalMeet"
			val importance = NotificationManager.IMPORTANCE_LOW
			val channel = NotificationChannel(AppConstants.ANDROID_CHANNEL_ID, name, importance)
			channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
			channel.description = AppConstants.ANDROID_CHANNEL_ID
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			val notificationManager = getSystemService(NotificationManager::class.java)
			notificationManager?.createNotificationChannel(channel)
		}
	}

	companion object {
		private val TAG = BaseMeetingActivity::class.java.name
		private const val INTERNET_TIME_OUT_RETRY_LATER = 30000
	}
}