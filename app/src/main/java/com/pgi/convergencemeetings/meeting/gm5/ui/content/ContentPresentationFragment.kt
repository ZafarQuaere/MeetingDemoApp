package com.pgi.convergencemeetings.meeting.gm5.ui.content

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.hardware.Camera
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.newrelic.agent.android.NewRelic
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.RETRY_8000_MS
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.CustomViewPager
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergence.utils.PermissionUtil
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.*
import com.pgi.convergencemeetings.meeting.gm5.data.model.*
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import net.openid.appauth.TokenResponse
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule

open class ContentPresentationFragment : Fragment() {

	lateinit var pdfActionsTimer: Timer
	lateinit var videoActionsTimer: Timer
	lateinit var loadInitalVideoTimer: Timer
	lateinit var whiteboardActionsTimer: Timer
	lateinit var foxdenSessionTimer: Timer


	// Active Talker Elements
	@BindView(R.id.web_active_talker_container)
	lateinit var llActiveTalkerDelay: LinearLayout

	@BindView(R.id.profile_pic_active_talker)
	lateinit var circleImageViewDelay: CircleImageView

	@BindView(R.id.tv_gm5_name_initials)
	lateinit var tvActiveTalkerInitialsDelay: TextView

	@BindView(R.id.tv_gm5_full_name)
	lateinit var tvActiveTalkerNameDelay: TextView

	@BindView(R.id.tv_gm5_talking_state)
	lateinit var tvActiveTalkerStateDelay: TextView

	// Full screen controls
	@BindView(R.id.landscpe_ss_controls)
	lateinit var meetingControls: RelativeLayout

	@BindView(R.id.btn_full_screen)
	lateinit var btnExitFullScreen: ImageButton

	@BindView(R.id.tv_participant_name_ss_portrait)
	lateinit var textViewParticipantNameSS: TextView

	@BindView(R.id.tv_talking_state_ss_portrait)
	lateinit var textViewTalkingStateSS: TextView

	@BindView(R.id.tv_participant_name_ss_landscape)
	lateinit var textViewParticipantNameLandscape: TextView

	@BindView(R.id.tv_talking_state_ss_landscape)
	lateinit var textViewTalkingStateSSLandscape: TextView

	@BindView(R.id.tv_no_one_spoken_ss)
	lateinit var textViewNoOneSpokenSS: TextView

	@BindView(R.id.btn_camera_ss)
	lateinit var btnCamera: ToggleButton

	@BindView(R.id.btn_mute_meeting_ss)
	lateinit var btnMute: ToggleButton

	@BindView(R.id.progress_camera_disabled)
	lateinit var progressCameraButton: ProgressBar

	// Top toast showing screen viewing
    @BindView(R.id.cc_presenter_name_container)
    lateinit var llNameContainer: ConstraintLayout

	@BindView(R.id.tv_presenter_name)
	lateinit var textViewPresenter: TextView

    @BindView(R.id.progress_mic_disabled)
    lateinit var progressMicButton: ProgressBar

    // Webview

	@BindView(R.id.screen_share_webview)
	lateinit var webView: WebView

	@BindView(R.id.btn_windowed_webview)
	lateinit var btnToggleFullScreenWebview: ImageButton

	@BindView(R.id.recording_indicator_ss)
	lateinit var recordingIndicator: ImageView

	@BindView(R.id.floatingSnackBar)
	lateinit var coordinatorLayout: CoordinatorLayout

    @BindView(R.id.btn_paginate_right_arrow)
    lateinit var btnPaginateNext: ImageButton

    @BindView(R.id.btn_paginate_left_arrow)
    lateinit var btnPaginatePrevious: ImageButton

	private val TAG = ContentPresentationFragment::class.java.simpleName
	private val mlogger: Logger = CoreApplication.mLogger

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var vpWebMeeting: CustomViewPager? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var myJavaScriptInterface: MyJavaScriptInterface? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var orientationListener: OrientationEventListener? = null
	protected var mCloudRegion: String? = null
	protected var mAccessToken: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isGranted: Int? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var sessionId: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var participantId: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isPortrait = true

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var videoPlayerReady = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isWebViewReady = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isStreamAdded = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isChecked = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var filePresentatationUrl: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var contentId: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var contentIdOld: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var talkingState: String? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	lateinit var contentPresentationNotificationToast: Snackbar

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	lateinit var contentLowerToast: Snackbar

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isCameraShared = false
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isMuteEnabled = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isFullScreen = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isSpotlight = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isRecordingOn = false

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	lateinit var meetingUserViewModel: MeetingUserViewModel

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	lateinit var meetingEventViewModel: MeetingEventViewModel

	var isMusicOnHold = false
	var initialScale = 1.0f
	var currentWebViewScale = 1.0f

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var isTest = false
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var foxdenSessionActive = false
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var contentPresenterId: String? = null
	private val javaScriptInterfaceInvoker = "platformInvokeHandler"
	var currentPageNumber = 0;
	var isCameraManageFeed = false
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var disableCameraDialog : AlertDialog.Builder
	var foxdenConnectionRetryCount = 0
	var maxRetryCount = 3
	var foxdenConnectionTimer: TimerTask? = null
	var webcamsPerPage = 3
	var pages = 3
	var showCamsBasedonTab: Boolean = false
	var parentActivity : WebMeetingActivity? = null
	var turnWebCamOff: Boolean = false
	private var btnCameraToggle: ToggleButton? = null
	private var progressBarCameraDisable: ProgressBar? = null
    @BindView(R.id.linear_gm5_no_one_spoken)
    lateinit var linearNoOneHasSpokenDelay: LinearLayout
	lateinit var webViewLayoutParams: RelativeLayout.LayoutParams

	@BindView (R.id.tv_gm5_no_one)
	lateinit var tvNoOneSpeaking : TextView

	@BindView (R.id.tv_gm5_spoken)
	lateinit var tvSpeaking : TextView
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var rlTopBarContainer: RelativeLayout? = null
	var isSwipeRightEnabled = true
	var isSwipeLeftEnabled = false
	var x1 = 0.0f
	var y1 = 0.0f
	var x2 = 0.0f
	var y2 = 0.0f

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var isLocalStream = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mCloudRegion = SharedPreferencesManager.getInstance().cloudRegion
		mAccessToken = AppAuthUtils.getInstance().accessToken
		isGranted = isCameraPermissionGranted()
		vpWebMeeting = activity?.findViewById(R.id.viewPager)
		NewRelic.setInteractionName(Interactions.CONTENT_VIEW.interaction)
		btnCameraToggle = activity?.findViewById<ToggleButton>(R.id.btn_camera)
		progressBarCameraDisable = activity?.findViewById<ProgressBar>(R.id.progress_meeting_camera_disabled)
		rlTopBarContainer = activity?.findViewById(R.id.rl_top_bar_container)
		initializeParentActivity(activity)
	}

	fun initializeParentActivity(activity: FragmentActivity?) {
		activity?.let {
			if (it is WebMeetingActivity)
				parentActivity = it
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_content_view, container, false)
		ButterKnife.bind(this, view)
		setUpWebViewDefaults()
		registerViewModelListener()
		setUpOrientationChangeListener()
		setViewForNoOneSpeakingText()
		return view
	}

	private fun setViewForNoOneSpeakingText() {
		val speakingText = getString(R.string.no_one)+AppConstants.BLANK_SPACE+getString(R.string.speaking)
		textViewNoOneSpokenSS.text = speakingText
	}

	@SuppressLint("SourceLockedOrientationActivity")
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		isPortrait = newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE
		handleOrientationChange()

	}

	open fun setUpWebViewDefaults() {
		myJavaScriptInterface = MyJavaScriptInterface(context)
		webView.settings?.apply {
			javaScriptEnabled = true
			useWideViewPort = true
			loadWithOverviewMode = true
			builtInZoomControls = true
			domStorageEnabled = true
			this.setSupportZoom(true)
			displayZoomControls = false
			allowUniversalAccessFromFileURLs = true
			allowFileAccessFromFileURLs = true
			mediaPlaybackRequiresUserGesture = false
		}
		webView.apply {
			setBackgroundColor(Color.TRANSPARENT)
			requestFocusFromTouch()
			isScrollContainer = true
			isVerticalScrollBarEnabled = true
			isHorizontalScrollBarEnabled = false
			setInitialScale(1)
			activity?.let {
				addJavascriptInterface(myJavaScriptInterface, javaScriptInterfaceInvoker)
			}
			webViewClient = CustomWebViewClient()
			webChromeClient = CustomWebChromeClient()
			setOnTouchListener { view: View, motionEvent: MotionEvent ->
				onTouch(view,motionEvent)
				false
			}
		}
		if (BuildConfig.DEBUG) {
			WebView.setWebContentsDebuggingEnabled(true)
		}

		activity?.let {
			meetingEventViewModel = ViewModelProviders.of(it).get(MeetingEventViewModel::class.java)
			meetingUserViewModel = ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
		}
        webView.loadUrl("https://mobile.globalmeet.com/webapp/prod/v12/index.html")
		initialScale = resources.displayMetrics.density
		currentWebViewScale = resources.displayMetrics.density
		webViewLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		webViewLayoutParams.addRule(RelativeLayout.BELOW, R.id.cc_presenter_name_container)
		webView.setLayoutParams(webViewLayoutParams)
	}

	open fun setUpOrientationChangeListener() {
		orientationListener = object : OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {
			override fun onOrientationChanged(orientation: Int) {
				if (meetingUserViewModel.contentPresentationActive && (orientation == 0 || orientation == 180)) {
					Handler().postDelayed({
						activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
					}, 2000)
				}
			}
		}
		orientationListener?.enable()
	}

	open fun registerViewModelListener() {
		meetingEventViewModel.apply {
			activeTalker.observe(this@ContentPresentationFragment, Observer { talker: ActiveTalker -> updateTalker(talker) })
			meetingRecordStatus.observe(this@ContentPresentationFragment, Observer { status: MeetingRecordStatus -> handleRecording(status) })
			isMusicOnHold.observe(this@ContentPresentationFragment, Observer { isMusicOnHold: Boolean -> updateAfterMusicOnHold(isMusicOnHold) })
			meetingStatus.observe(this@ContentPresentationFragment, Observer { staus: MeetingStatus -> handleMeetingEnd(staus) })
			userFlowStatus.observe(this@ContentPresentationFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserStatus(userFlowStatus) })
			content.observe(this@ContentPresentationFragment, Observer { content: Content? -> onContent(content) })
			users.observe(this@ContentPresentationFragment, Observer {
				manageTopbar()
			})
		}
		meetingUserViewModel.apply {
			audioType.observe(this@ContentPresentationFragment, Observer { type: AudioType -> setMicDrawables(type) })
			cameraStatus.observe(this@ContentPresentationFragment, Observer { checked: Boolean -> toggleCamera(checked) })
			muteStatus.observe(this@ContentPresentationFragment, Observer { checked: Boolean -> toggleMic(checked) })
			muteBtnStatus.observe(this@ContentPresentationFragment, Observer { enabled: Boolean -> toggleMicEnableState(enabled) })
			internetStatus.observe(this@ContentPresentationFragment, Observer { status: Boolean -> handleInternetConnection(status) })
			userFlowStatus.observe(this@ContentPresentationFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserStatus(userFlowStatus) })
			cameraConnectingStatus.observe(this@ContentPresentationFragment, Observer { checked: Boolean -> toggleCameraEnabledState(checked) })
			tabChangeStatus.observe(this@ContentPresentationFragment, Observer { tabValue:Int->showHideWebCamOnTabSelection(tabValue) })
			appForegroundEvent.observe(this@ContentPresentationFragment, Observer { status:Boolean-> showHideWebCamOnLifeCycleEvent(status) })
			lowBandwidthEvent.observe(this@ContentPresentationFragment, Observer { status:Boolean-> showHideWebCamOnLowBandwidthEvent(status) })
			audioStatus.observe(this@ContentPresentationFragment, androidx.lifecycle.Observer { status: AudioStatus -> setAudioProgressState(status) })
		}
	}

	// ----------------------------  Meeting event listeners ---------------------------------------//

	fun manageTopbar() {
		if((meetingUserViewModel?.screenSharePortrait == true || meetingUserViewModel?.screenShareLandScape == true) && meetingUserViewModel?.contentPresentationActive == true && meetingUserViewModel?.screenShareFullScreen == true) {
			rlTopBarContainer?.visibility = View.GONE
		} else {
			rlTopBarContainer?.visibility = View.VISIBLE
		}
	}

	fun updateTalker( talker: ActiveTalker) {
		val talkingState = if (talker.isTalking) {
			getString(R.string.speaking)
		} else {
			getString(R.string.no_one)+AppConstants.BLANK_SPACE+getString(R.string.speaking)
		}
		val isLocaleJapan = CommonUtils.isUsersLocaleJapan()
		if (talker.user.initials != null && talker.user.name != null && activity?.isFinishing == false && talker.isTalking) {
			tvActiveTalkerNameDelay.visibility = View.VISIBLE
			tvActiveTalkerStateDelay.visibility = View.VISIBLE
            linearNoOneHasSpokenDelay.visibility = View.GONE
			tvSpeaking.visibility = View.GONE
			tvNoOneSpeaking.visibility = View.GONE
			textViewNoOneSpokenSS.visibility = View.GONE
		}else {
			tvNoOneSpeaking.visibility = View.VISIBLE
			linearNoOneHasSpokenDelay.visibility = View.VISIBLE
			tvSpeaking.visibility = View.VISIBLE
			tvActiveTalkerNameDelay.visibility = View.GONE
			tvActiveTalkerStateDelay.visibility = View.GONE
		}
		this.talkingState = talkingState
		tvActiveTalkerStateDelay.text = talkingState
		textViewTalkingStateSS.text = talkingState
		textViewTalkingStateSSLandscape.text = talkingState
		if (isLocaleJapan) {
			val japaneseInitials = talker.user.initials?.reversed()?.toUpperCase(Locale.getDefault())
			tvActiveTalkerInitialsDelay.text = japaneseInitials
			textViewParticipantNameSS.text = CommonUtils.formatJapaneseName(talker.user.name)
			textViewParticipantNameLandscape.text = CommonUtils.formatJapaneseName(talker.user.name)
			tvActiveTalkerNameDelay.text = CommonUtils.formatJapaneseName(talker.user.name)

		} else {
			tvActiveTalkerInitialsDelay.text = talker.user.initials
			tvActiveTalkerNameDelay.text = CommonUtils.formatCamelCase(talker.user.name)
			textViewParticipantNameSS.text = CommonUtils.formatCamelCase(talker.user.name)
			textViewParticipantNameLandscape.text = CommonUtils.formatCamelCase(talker.user.name)
		}
		val profilePic = talker.user.profileImage
		if (profilePic != null && talker.isTalking) {
			Picasso.get().load(profilePic.replace(AppConstants.RESOLUTION_100X100, AppConstants.RESOLUTION_200X200)).into(circleImageViewDelay)
			circleImageViewDelay.background = context?.let { ContextCompat.getDrawable(it, R.drawable.web_active_talker_circle_clear) }
			circleImageViewDelay.visibility = View.VISIBLE
			tvActiveTalkerInitialsDelay.visibility = View.GONE
			textViewParticipantNameLandscape.visibility = View.VISIBLE
		} else if (!talker.isTalking) {
			circleImageViewDelay.visibility = View.GONE
			tvActiveTalkerInitialsDelay.visibility = View.GONE
			textViewParticipantNameLandscape.visibility = View.GONE
		}
		 else {
			circleImageViewDelay.visibility = View.GONE
			tvActiveTalkerInitialsDelay.background = context?.let { ContextCompat.getDrawable(it, R.drawable.web_active_talker_circle_drawable) }
			tvActiveTalkerInitialsDelay.visibility = View.VISIBLE
			textViewNoOneSpokenSS.visibility = View.GONE
			textViewParticipantNameLandscape.visibility = View.VISIBLE
			talker.user.initials?.let{
				if(it.equals(AppConstants.POUND_SYMBOL,true)){
					linearNoOneHasSpokenDelay.visibility = View.VISIBLE
					tvActiveTalkerInitialsDelay.visibility = View.GONE
					tvNoOneSpeaking.visibility = View.GONE
					tvSpeaking.visibility = View.GONE
				}else{
					linearNoOneHasSpokenDelay.visibility = View.GONE
					tvSpeaking.visibility = View.GONE
					tvActiveTalkerInitialsDelay.background = context?.let { ContextCompat.getDrawable(it, R.drawable.web_active_talker_circle_drawable) }
					tvActiveTalkerInitialsDelay.visibility = View.VISIBLE
				}
			}


		}
		handleInitialActiveTalker(talker.isTalking)
	}

	fun handleRecording(status: MeetingRecordStatus) {
		if (status === MeetingRecordStatus.RECORDING_STARTED) {
			startRecordingIndicator(true)
		} else if (status === MeetingRecordStatus.RECORDING_STOPPED) {
			stopRecordingIndicator(false)
		}
	}

	fun handleInitialActiveTalker(isTalking: Boolean) {
		if ((isFullScreen)) {
			tvActiveTalkerStateDelay.visibility = View.GONE
		}
		if (!isPortrait) {
			textViewParticipantNameLandscape.visibility = if (isTalking) View.VISIBLE else View.GONE
			textViewTalkingStateSSLandscape.visibility = if (isTalking) View.VISIBLE else View.GONE
			textViewParticipantNameSS.visibility = View.INVISIBLE
			textViewTalkingStateSS.visibility = View.INVISIBLE
		} else {
			textViewParticipantNameLandscape.visibility = View.INVISIBLE
			textViewTalkingStateSSLandscape.visibility = View.INVISIBLE
			textViewParticipantNameSS.visibility = if (isTalking) View.VISIBLE else View.INVISIBLE
			textViewTalkingStateSS.visibility = if (isTalking) View.VISIBLE else View.INVISIBLE
		}

		if (isFullScreen || !isPortrait) {
			textViewNoOneSpokenSS.visibility = if (isTalking) View.INVISIBLE else View.VISIBLE
		}
	}

	// Button states and actions
	public fun setAudioProgressState(status: AudioStatus) {
		if (status === AudioStatus.CONNECTING) {
			meetingUserViewModel?.isMuteBtnEnabled = false
			if (meetingUserViewModel?.audioConnType === AudioType.VOIP ||
					meetingUserViewModel?.audioConnType === AudioType.DIAL_OUT) {
				progressMicButton.visibility = View.VISIBLE
				updateMicLevelEnabled(true)
			} else {
				progressMicButton.visibility = View.GONE
			}
		} else if (status === AudioStatus.CONNECTED) {
			progressMicButton.visibility = View.GONE
			if (meetingUserViewModel?.audioConnType === AudioType.DIAL_IN) {
				meetingUserViewModel?.isMuteBtnEnabled = true
			} // else wait for dial_out_success to enable microphone
		} else if (status === AudioStatus.DISCONNECTED || status === AudioStatus.NOT_CONNECTED) {
			progressMicButton.visibility = View.GONE
			meetingUserViewModel?.isMuteBtnEnabled = false
			updateMicLevelEnabled(false)
		}
	}

	private fun updateMicLevelEnabled(isEnabled: Boolean) {
		var resId = R.drawable.volume_indicator_empty
		if (!isEnabled) {
			resId = R.drawable.volume_indicator_empty
		}
	}

	private fun startRecordingIndicator(isRecording: Boolean) {
		isRecordingOn = isRecording
	}

	private fun stopRecordingIndicator(isRecording: Boolean) {
		isRecordingOn = isRecording
	}

	fun updateAfterMusicOnHold(isMusicOnHold: Boolean) {
		if (meetingUserViewModel.audioConnType !== AudioType.NO_AUDIO) {
			if (isMusicOnHold) {
				btnMute.isChecked = true
			}
			this.isMusicOnHold = isMusicOnHold
			btnMute.isClickable = !isMusicOnHold
			btnMute.isEnabled = !isMusicOnHold
			val isMuteActive = meetingUserViewModel?.isMuteActivated?: false
			if (!isMusicOnHold) {
				btnMute.isChecked = isMuteActive
			}
		}
	}

	fun handleMeetingEnd(staus: MeetingStatus) {
		if (staus === MeetingStatus.ENDED) {
			resetWebView()
		}
	}

	fun respondToUserStatus(userFlowStatus: UserFlowStatus) {
		if (userFlowStatus === UserFlowStatus.END_MEETING_SUCCESS || userFlowStatus === UserFlowStatus.LEAVE_MEETING_SUCCESS || userFlowStatus === UserFlowStatus.DISMISSED) {
			resetWebView()
		} else if (userFlowStatus == UserFlowStatus.VIDEO_SESSION_SUCCESS) {
			val cookieManager = CookieManager.getInstance()
			cookieManager.setAcceptCookie(true)
			CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
			cookieManager.removeAllCookies(null)
			meetingUserViewModel.cloundFrontCookies.forEach { cookie ->
				val cookieValues = cookie.split(";").toTypedArray()
				var domain = "*.globalmeet.com"
				for (values in cookieValues) {
					if (values.toLowerCase().contains("domain")) {
						domain = values.split("=").toTypedArray()[1]
					}
				}
				cookieManager.setCookie(domain, cookie)
				cookieManager.flush()
			}
			val lock = Object()
			loadInitalVideoTimer = fixedRateTimer(
					"loadVideoOnWebViewReady",
					false,
					0L,
					100) {
				if (isWebViewReady) {
					loadInitialVideo()
					synchronized(lock) {
						lock.notify()
					}
				}
			}
			synchronized(lock) {
				if (isTest) {
					lock.wait()
				}
			}
		}
	}

	fun loadInitialVideo() {
		loadInitalVideoTimer.cancel()
		loadInitalVideoTimer.purge()
		activity?.runOnUiThread {
			webView.loadUrl("javascript:addVideo('${contentId}', '${filePresentatationUrl}');")
		}
	}

	protected fun resetWebView() {
		webView.apply {
			loadUrl("javascript:disconnectFoxdenSession()")
			clearCache(false)
			clearHistory()
			clearFormData()
			loadUrl("about:blank")
		}
	}

	fun onContent(content: Content?) {
		content?.let {
			val (sessId, documentUrl, _, _, fileId, fileObj) = content.staticMetadata
			if (sessId != null) {
				sessionId = sessId
			}
			participantId = meetingUserViewModel.currentUserId
			filePresentatationUrl = documentUrl
			contentId = it.id
			if(contentIdOld != contentId) {
				if(contentIdOld != null) {
					webView.loadUrl("javascript:removeContent('${contentIdOld}');")
				}
			} else {
				contentId = contentIdOld
			}
			if (it.type.equals(AppConstants.FILE_PRESENTATION, ignoreCase = true)) {
				if (fileObj?.metaData != null) {
					val metaData: MetaData = fileObj.metaData
					if (metaData.presentationState == "READY" && metaData.presentationType == "PRESENTATION_TYPE_VIDEO" && fileId != null) {
						handleVideoMetaData(it, fileId)
					} else if (metaData.presentationType == "PRESENTATION_TYPE_PDF") {
						val pageNumber= content.dynamicMetaData.newPage
						if(pageNumber != null) {
							currentPageNumber = pageNumber.toInt()
						}
						handlePdfMetadata(it)
					}
				}
			} else if (it.type.equals(AppConstants.CONTENT_DELETED, ignoreCase = true)) {
				contentId = null
				filePresentatationUrl = null
				currentPageNumber = 0
				videoPlayerReady = false
				meetingUserViewModel.cloundFrontCookies = emptyList()
				webView.loadUrl("javascript:removeContent('${content.id}');")
			} else if (it.dynamicMetaData.action?.equals(AppConstants.ACTION_SCREEN_START) == true ||
				it.dynamicMetaData.action?.equals(AppConstants.ACTION_SCREEN_STOP) == true) {
					handleStartStopEvents(it)
			}
		}
	}

	private fun handleStartStopEvents(content: Content) {
		if (content.dynamicMetaData.action?.equals(AppConstants.ACTION_SCREEN_START) == true) {
			if (content.dynamicMetaData.type.equals(AppConstants.WHITEBOARD, ignoreCase = true)) {
				handleWhiteboardMetadata(content)
			} else {
				meetingUserViewModel.sharingContent = content
				contentPresenterId = content.dynamicMetaData.screenPresenter.partId
			}
		} else if (content.dynamicMetaData.action?.equals(AppConstants.ACTION_SCREEN_STOP) == true) {
			if (content.dynamicMetaData.type.equals(AppConstants.WHITEBOARD, ignoreCase = true)) {
				webView.loadUrl("javascript:removeContent('${content.id}');")
				contentId = null
			} else {
				meetingUserViewModel.sharingContent = null
				contentPresenterId = null
			}
		}
	}

	private fun handleVideoMetaData(content: Content, fileId: String) {
		val dynamicMetaData = content.dynamicMetaData
		if (meetingUserViewModel.cloundFrontCookies.isEmpty() == true) {
			meetingUserViewModel.makeSessionCallToCloudFront(fileId)
		}
		val lock = Object()
		videoActionsTimer = fixedRateTimer(
				"videoActionsTimer",
				false,
				0L,
				100) {
			if (videoPlayerReady) {
				videoActionsTimer.purge()
				videoActionsTimer.cancel()
				handleVideoActions(dynamicMetaData)
				synchronized(lock) {
					lock.notify()
				}
			}
		}
		synchronized(lock) {
			if (isTest) {
				lock.wait()
			}
		}
	}

	private fun handleVideoActions(dynamicMetaData: DynamicMetaData) {
		if ((dynamicMetaData.action == "PLAY_VIDEO" && dynamicMetaData.playState == "PLAY_VIDEO") || dynamicMetaData.action == "SEEK_VIDEO") {
			val currTime = System.currentTimeMillis()
			val diff = currTime - dynamicMetaData.eventTime
			val secs = if (diff < 0) 0 else diff / 1000 // Sometimes the diff is negative as the event timestamp is greater than the current time
			if (dynamicMetaData.action == "SEEK_VIDEO") {
				val time = dynamicMetaData.seekTime?.toLong() ?: 0
				if (dynamicMetaData.playOnSeek) {
					onVideoActionChange("play")
				} else {
					onVideoActionChange("pause")
				}
				val playTime = (time + secs).toString()
				onVideoSeek(playTime)
			} else if (dynamicMetaData.action == "PLAY_VIDEO") {
				val playTimeCode = dynamicMetaData.timeCode.toLong()
				val seekTime = (playTimeCode + secs).toString()
				onVideoActionChange("play")
				onVideoSeek(seekTime)
			}
		} else if (dynamicMetaData.action == "PAUSE_VIDEO" && dynamicMetaData.playState == "PAUSE_VIDEO") {
			onVideoActionChange("pause")
			onVideoSeek(dynamicMetaData.timeCode.toString())
		}
	}

	private fun handleWhiteboardMetadata(content: Content) {
		val dynamicMetaData = content.dynamicMetaData
		val lock = Object()
		whiteboardActionsTimer = fixedRateTimer(
				"whiteboardActionsTimer",
				false,
				0L,
				100) {
			if (isWebViewReady) {
				activity?.runOnUiThread {
					webView.loadUrl("javascript:addWhiteboardView('${content.id}','${content.user.id}');")
				}
				whiteboardActionsTimer.cancel()
				whiteboardActionsTimer.purge()
				synchronized(lock) {
					lock.notify()
				}
			}
		}
		synchronized(lock) {
			if (isTest) {
				lock.wait()
			}
		}
	}

    fun updateContentID(){
		contentIdOld = contentId
	}

	override fun onStop() {
		super.onStop()
		contentIdOld = contentId
	}

	private fun handlePdfMetadata(content: Content) {
		val dynamicMetaData = content.dynamicMetaData
		val lock = Object()
		pdfActionsTimer = fixedRateTimer(
				"pdfActionsTimer",
				false,
				0L,
				100) {
			if (isWebViewReady) {
				pdfActionsTimer.cancel()
				pdfActionsTimer.purge()
				renderPdf()
				synchronized(lock) {
					lock.notify()
				}
			}
		}
		synchronized(lock) {
			if (isTest) {
				lock.wait()
			}
		}
	}

	private fun renderPdf() {
		if (currentPageNumber > 0) {
			activity?.runOnUiThread {
				webView.loadUrl("javascript:queueRenderPage("+currentPageNumber+")");
			}
		} else {
			activity?.runOnUiThread {
				webView.loadUrl("javascript:addPdf('${contentId}', '${filePresentatationUrl}', " +currentPageNumber+");");
			}
		}
	}

	private fun handleCameraActions() {
		val lock = Object()
		foxdenSessionTimer = fixedRateTimer(
				"foxdenSessionTimer",
				false,
				0L,
				1000) {
			if (foxdenSessionActive) {
				foxdenSessionTimer.cancel()
				foxdenSessionTimer.purge()
				handleCamera()
				synchronized(lock) {
						lock.notify()
					}
			}
		}
		synchronized(lock) {
			if (isTest) {
				lock.wait()
			}
		}
	}

	private fun handleCamera() {
		if(isCameraShared) {
			activity?.runOnUiThread {
				webView.loadUrl("javascript:addCameraStream(true);")
				val maxResolution = getCameraResolution()
				mlogger.mixpanelTurnOnCameraModel.webcamResolution = maxResolution.toString()
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
						null, null, false, true)

			}
		} else {
			activity?.runOnUiThread {
				webView.loadUrl("javascript:stopLocalStream();")
			}
		}
	}
	// ------------------------- User flow event listeners ---------------------------------------//

	fun setMicDrawables(type: AudioType) {
		if (type === AudioType.DIAL_OUT) {
			if (isFullScreen || !isPortrait) {
				btnMute.setButtonDrawable(R.drawable.mic_button_pstn_selector_ss)
			}
			else {
				btnMute.setButtonDrawable(R.drawable.mic_btn_pstn_selector)
			}
			setColorOfProgressMicButton(meetingUserViewModel?.isMute, R.color.accent_color_400)
		} else if (type === AudioType.VOIP) {
			if (isFullScreen || !isPortrait) {
				btnMute.setButtonDrawable(R.drawable.mic_button_selector_ss)
			} else {
				btnMute.setButtonDrawable(R.drawable.mic_btn_selector)
			}
			setColorOfProgressMicButton(meetingUserViewModel?.isMute, R.color.primary_color_500)
		} else if (type === AudioType.NO_AUDIO || type === AudioType.DIAL_IN) {
			  btnMute.setButtonDrawable(R.drawable.speaker_btn_audio_not_connected_selector_ss)
		  }
	}

	fun setColorOfProgressMicButton(isMute: Boolean, color: Int) {
		activity?.let {
			progressMicButton.indeterminateTintList =
					ColorStateList.valueOf(resources.getColor( if(isMute)  R.color.color_red else color , it.theme))
		}
	}

	fun toggleMic(checked: Boolean) {
			isChecked = checked
			btnMute.isChecked = checked
	}

	fun toggleCamera(checked: Boolean) {
		if(btnCamera.isChecked != checked) {
			isCameraShared = checked
			btnCamera.isChecked = checked
			handleCameraActions()
		}
	}

	fun toggleMicEnableState(enabled: Boolean) {
		updateMuteButton(enabled)
	}

	private fun updateMuteButton(isMicEnabled: Boolean) {
		if (isMicEnabled ) {
			btnMute.isClickable = true
			btnMute.isEnabled = true
		} else {
			btnMute.isClickable = false
			btnMute.isEnabled = false
		}
		if(isMusicOnHold) {
			btnMute.isEnabled = true
		}
		if (meetingUserViewModel.audioConnType == AudioType.NO_AUDIO || meetingUserViewModel.audioConnType == AudioType.DIAL_IN) {
			btnMute.isClickable = true
			btnMute.isEnabled = true
		}
	}

	fun handleInternetConnection(status: Boolean) {
		webView.loadUrl("javascript:internetConnectionChanged('${status}');")
		stopSharedCameraForWifiOnlyUsers()
	}

	fun stopSharedCameraForWifiOnlyUsers() {
		//for non pgi user stop sharing camera on mobile
			if(meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS.feature) && (meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)
						&& InternetConnection.isConnectedMobile(context)) && !meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature) && isCameraShared) {
			  isCameraShared = false
			  btnCamera.isChecked = true
			  meetingUserViewModel.isCameraOn = false
		    }
	     }

	private fun showHideWebcam() {
		val isWifi = InternetConnection.isConnectedWifi(context)
		if (isStreamAdded) {
			activity?.runOnUiThread {
				if (meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS.feature)) {
					if(meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature) && meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)){
						webView.loadUrl("javascript:showHideWebcam(true, $showCamsBasedonTab);")
					}
					 else if (meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)) {
						 webView.loadUrl("javascript:showHideWebcam($isWifi, $showCamsBasedonTab);")
					 }
				}
			}
		} else Log.i(TAG, "showHideWebcam() isStreamAdded : $isStreamAdded")
	}

	// ------------------------ Full Screen Meeting Controls --------------------------------------//

	@SuppressLint("SourceLockedOrientationActivity")
	@OnClick(R.id.btn_full_screen)
	open fun exitfromFullScreenMode() {
		if (isCameraManageFeed) {
			isCameraManageFeed = false
			mlogger.mixpanelTurnOnCameraModel.webcamAction = AppConstants.SPOTLIGHT_DISABLE
			mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
					null, null, false, true)
		}
		isFullScreen = false
		setMicDrawables(meetingUserViewModel.audioConnType)
		isSpotlight = false
		if (!isPortrait) {
			activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		}
		transitionToPortrait()
		webView.apply {
			loadUrl(AppConstants.EXIT_FULL_SCREEN_FOXDEN)
		}
	}


	@OnClick(R.id.btn_camera_ss)
	open fun onCameraClicked() {
		if (isGranted != PackageManager.PERMISSION_GRANTED) {  // we don't have the permission.  Ask for it
			requestPermissions(arrayOf(Manifest.permission.CAMERA), AppConstants.PERMISSIONS_REQUEST_CAMERA)
			toggleCamera(false)
		} else {
			checkWebCamFeature()   //check camera feature enable/disable as per app config
		}
	}

	open fun isCameraPermissionGranted(): Int? {
		return activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
	}

	 fun handleShareCameraEvents() {
		if (!isCameraShared) { // camera is not sharing.  Start the camera sharing session
			isCameraShared = true
			btnCamera.isChecked = false
			meetingUserViewModel.isCameraOn = true
		} else { // camera was sharing.  Stop the camera sharing session
			isCameraShared = false
			btnCamera.isChecked = true
			meetingUserViewModel.isCameraOn = false
		}
		 //	Notify only when user is in spotlight mode
		 if (isSpotlight) {
			 notifyUserForWebCameUpdate()
		 }
		meetingUserViewModel.isCameraConnecting = true
		startCameraEnableDisableTimer(true)
	}

	 fun showCameraDisableDialog() {
		 	disableCameraDialog = activity.let { AlertDialog.Builder(it!!) }
		 	disableCameraDialog.setMessage(R.string.dialog_wifi_to_mobile_data_switch_message_for_webcam)
			disableCameraDialog.setCancelable(false)
			disableCameraDialog.setPositiveButton(R.string.dialog_dismiss) { dialog: DialogInterface, which: Int ->
				dialog.cancel()
			}
		 	disableCameraDialog.show()
	}

	// This function is to notify the user when user on/off the camera feed while user is in
	// spotlight mode for other feeds

	 fun notifyUserForWebCameUpdate() {
		activity?.let {
			it.runOnUiThread {
				val toastText = if (isCameraShared) resources.getString(R.string.your_webcam_is_on) else resources.getString(R.string.your_webcam_is_off)
				contentPresentationNotificationToast = Snackbar.make(coordinatorLayout, toastText, Snackbar.LENGTH_SHORT)
				val snackBarView = contentPresentationNotificationToast.view
				if (!isLocalStream) {
					val params = snackBarView.layoutParams as CoordinatorLayout.LayoutParams
					params.setMargins(params.leftMargin + 0,
							params.topMargin,
							params.rightMargin + 0,
							params.bottomMargin + resources.getDimension(R.dimen.someone_presenting_screen_share_toast_margin_top).toInt())
					snackBarView.layoutParams = params
				}
				contentPresentationNotificationToast.show()
			}
		}
	}

	fun getViewForNotification(activity: FragmentActivity?): View? {
		activity?.let {
			if (it is WebMeetingActivity) {
				return it.coordinatorLayout
			}
		}
		return null
	}

	@ObsoleteCoroutinesApi
	@UnstableDefault
	@ExperimentalCoroutinesApi
	fun showUserSnackBar(toastText: String) {
		getViewForNotification(parentActivity)?.let {
			contentPresentationNotificationToast = Snackbar.make(it, toastText, Snackbar.LENGTH_SHORT)
			contentPresentationNotificationToast.show()
		}
	}

	@OnClick(R.id.btn_mute_meeting_ss)
	open fun onMuteMeClicked() {
		if (meetingUserViewModel.audioConnType == AudioType.NO_AUDIO || meetingUserViewModel.audioConnType == AudioType.DIAL_IN) {
			parentActivity?.onChangeAudioClicked()
		} else {
			meetingUserViewModel.apply {
				isMuteBtnEnabled = false
				isMute = btnMute.isChecked
				isMuteEnabled = isMute
				currentUserId?.let { userId ->
					meetingEventViewModel.getCurrentUser()?.audio?.id?.let {
						muteUnmuteUser(userId, it, isMuteEnabled)
					}
				}
			}
		}
	}

	@OnClick(R.id.btn_windowed_webview)
	open fun onFullScreenClicked() {
		isSpotlight = false
		isFullScreen = btnToggleFullScreenWebview.isPressed
		Log.e(tag, "is full screen = $isFullScreen")
		handleOrientationChange()
		setMicDrawables(meetingUserViewModel.audioConnType)
	}
	// ------------------------ Orientation chnage handlers  --------------------------------------//
	fun handleOrientationChange() {
		if (isPortrait) {
			webView.loadUrl("javascript:orientationchanged(0);")
			transitionToPortrait()
		} else {
			webView.loadUrl("javascript:orientationchanged(1);")
			transitionToLandscape()
			setMicDrawables(meetingUserViewModel.audioConnType)
		}
	}

	var cameraFailedSnackbar: Snackbar? = null
	var timer: TimerTask? = null
	private fun startCameraEnableDisableTimer(isEnablingCamera: Boolean) {
		timer = Timer().schedule(10000){
			activity?.runOnUiThread {
				toggleCameraEnabledState(false)
				//Reset Camera button and camera shared status
				isCameraShared = !isCameraShared
				btnCamera.isChecked = !btnCamera.isChecked

				if (cameraFailedSnackbar == null) {
					cameraFailedSnackbar = if (isEnablingCamera)
						Snackbar.make(coordinatorLayout, R.string.failed_camera_start, Snackbar.LENGTH_LONG)
								.setActionTextColor(ContextCompat.getColor(context!!, R.color.white))
								.setAction(R.string.failed_camera_btn_retry) {
									btnCamera.performClick()
								}
					else
						Snackbar.make(coordinatorLayout, R.string.failed_camera_stop, Snackbar.LENGTH_LONG)
								.setActionTextColor(ContextCompat.getColor(context!!, R.color.white))
								.setAction(R.string.failed_camera_btn_retry) {
									btnCamera.performClick()
								}
				}
				cameraFailedSnackbar?.show()
			}
		}
	}

	private fun toggleCameraEnabledState(connecting: Boolean) {
		if(connecting) {
			progressCameraButton.visibility = View.VISIBLE
			btnCamera.alpha = 0.3f
			btnCamera.isClickable = false
		} else {
			timer?.cancel()
			progressCameraButton.visibility = View.GONE
			btnCamera.alpha = 1.0f
			btnCamera.isClickable = true
		}
	}

	fun showHideWebCamOnTabSelection(tabValue: Int) {
		showCamsBasedonTab = (tabValue == 1)
		if(meetingUserViewModel.turnWebcamOff) {
			showHideWebcam()
		}
	}

	fun showHideWebCamOnLowBandwidthEvent(status: Boolean) {
		activity?.runOnUiThread {
			turnWebCamOff = status
			webView.loadUrl("javascript:toggleWebcam($turnWebCamOff);")
		}
	}

	 fun showHideWebCamOnLifeCycleEvent(status: Boolean) {
		showCamsBasedonTab = status
		if(meetingUserViewModel.turnWebcamOff) {
			showHideWebcam()
		}
	}

	fun transitionToPortrait() {
		llNameContainer.setBackgroundColor(Color.BLACK)
		webViewLayoutParams.addRule(RelativeLayout.BELOW, R.id.cc_presenter_name_container)
		webView.setLayoutParams(webViewLayoutParams)
		if (isFullScreen) {
			transitionToFullScreen()
			val isUserSpeakingCurrently = checkIfUserIsSpeaking()
			if (isUserSpeakingCurrently) {
				textViewTalkingStateSS.visibility = View.VISIBLE
				textViewParticipantNameSS.visibility = View.VISIBLE
				textViewNoOneSpokenSS.visibility = View.GONE
			} else {
				textViewNoOneSpokenSS.visibility = View.VISIBLE
				textViewTalkingStateSS.visibility = View.INVISIBLE
				textViewParticipantNameSS.visibility = View.INVISIBLE
			}
			textViewParticipantNameLandscape.visibility = View.INVISIBLE
			textViewTalkingStateSSLandscape.visibility = View.INVISIBLE
		} else {
			meetingUserViewModel.screenShareFullScreen = false
			btnToggleFullScreenWebview.visibility = View.VISIBLE
            btnExitFullScreen.visibility = View.INVISIBLE
			meetingControls.visibility = View.GONE
			showNotificationBar()
			recordingIndicator.visibility = View.GONE
		}
		meetingUserViewModel.screenShareLandScape = false
		meetingUserViewModel.screenSharePortrait = true
		meetingUserViewModel.orientationChanged = !meetingUserViewModel.orientationChanged
	}

	fun checkIfUserIsSpeaking(): Boolean {
		talkingState?.let {
			return (it == getString(R.string.speaking))
		}
		return false
	}

	private fun transitionToLandscape() {
		activity?.let { ContextCompat.getColor(it,R.color.webcam_landscape_top_controls_bg) }?.let { llNameContainer.setBackgroundColor(it) }
		webViewLayoutParams.removeRule(RelativeLayout.BELOW)
		webView.setLayoutParams(webViewLayoutParams)
		transitionToFullScreen()
		val isUserSpeakingCurrently = checkIfUserIsSpeaking()
		textViewParticipantNameLandscape.visibility = if (isUserSpeakingCurrently) View.VISIBLE else View.GONE
		textViewTalkingStateSSLandscape.visibility = if (isUserSpeakingCurrently) View.VISIBLE else View.GONE
		textViewNoOneSpokenSS.visibility = if (isUserSpeakingCurrently) View.GONE else View.VISIBLE
		textViewTalkingStateSS.visibility = View.INVISIBLE
		textViewParticipantNameSS.visibility = View.INVISIBLE
		meetingUserViewModel.screenShareLandScape = true
		meetingUserViewModel.screenSharePortrait = false
		meetingUserViewModel.orientationChanged = !meetingUserViewModel.orientationChanged
	}

	private fun transitionToFullScreen() {
		meetingUserViewModel.screenShareFullScreen = true
		btnToggleFullScreenWebview.visibility = View.INVISIBLE
        btnExitFullScreen.visibility = View.VISIBLE
		meetingControls.visibility = View.VISIBLE
		updateRecordingIcon()
		hideNotificationBar()
	}

	protected fun hideNotificationBar() {
		activity?.let {
			CommonUtils.hideNotificationBar(it)
		}
	}

	protected fun showNotificationBar() {
		activity?.let {
			CommonUtils.showNotificationBar(it)
		}
	}

	fun updateRecordingIcon() {
		if (isRecordingOn) {
			recordingIndicator.visibility = View.VISIBLE
		} else {
			recordingIndicator.visibility = View.GONE
		}
	}

	// ------------------------ JavaScript Interface handlers -------------------------------------//
	inner class MyJavaScriptInterface internal constructor(var context: Context?) {
		@JavascriptInterface
		fun accessToken(): String? {
			mAccessToken = AppAuthUtils.getInstance().accessToken
			val pGiIdentityAuthService = context?.let { PGiIdentityAuthService.getInstance(it) }
			pGiIdentityAuthService?.tokenSubject?.subscribeOn(AndroidSchedulers.mainThread())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : io.reactivex.Observer<TokenResponse?> {
				override fun onSubscribe(d: Disposable) {
					mAccessToken = AppAuthUtils.getInstance().accessToken
					activity?.runOnUiThread { webView.loadUrl("javascript:updateAuthToken('$mAccessToken');") }
				}

				override fun onNext(tokenResponse: TokenResponse) {
					mAccessToken = tokenResponse.accessToken
					activity?.runOnUiThread { webView.loadUrl("javascript:updateAuthToken('$mAccessToken');") }
				}
				override fun onError(e: Throwable) {
					// NOT REQUIRED
				}
				override fun onComplete() {
					// NOT REQUIRED
				}
			})
			return mAccessToken
		}

		@JavascriptInterface
		fun showActiveTalker() {
			showActiveTalkerFragment()
		}

		@JavascriptInterface
		fun hideActiveTalker() {
			hideActiveTalkerFragment()
		}

		@JavascriptInterface
		fun foxDenSessionConnected() {
			mlogger.error(TAG, LogEvent.FEATURE_SCREENSHARE, LogEventValue.SCREENSHARE, AppConstants.FOXDEN_SESSION_CONNECTION_SUCCESS, null, null, false, false)
			activity?.runOnUiThread {
					foxdenSessionActive = true
					meetingUserViewModel.isCameraConnecting = false
				    meetingUserViewModel.isFoxdenConnected = true
					foxdenConnectionTimer?.cancel()
			}
		}

		@JavascriptInterface
		fun foxDenSessionDisconnected() {
			activity?.runOnUiThread {
				isStreamAdded = false
				foxdenSessionActive = false
				webView.loadUrl(AppConstants.START_SCREEN_SHARE_AUTH)
			}
		}

		@JavascriptInterface
		fun setInFullScreenMode(streamType: String) {
			activity?.runOnUiThread {
				isFullScreen = true
				isSpotlight = true
				handleOrientationChange()
				setMicDrawables(meetingUserViewModel.audioConnType)
				if(streamType.equals(AppConstants.CONTENT_TYPE_CAMERA, true) || streamType.equals(AppConstants.CONTENT_TYPE_LOCAL_CAMERA, true)) {
					isLocalStream = streamType.equals(AppConstants.CONTENT_TYPE_LOCAL_CAMERA, true)
					isCameraManageFeed = true
					mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT.value,
							null, null, false, true)
					mlogger.mixpanelTurnOnCameraModel.webcamAction = AppConstants.SPOTLIGHT_ENABLE
					mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
							null, null, false, true)
				}
			}
		}

		@JavascriptInterface
		fun exitFullScreenMode() {
			activity?.runOnUiThread {
				exitfromFullScreenMode()
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT.value,
						null, null, false, true)
			}
		}

		@JavascriptInterface
		fun setCloudRegion(): String? {
			return mCloudRegion
		}

		@JavascriptInterface
		fun logErrors(msg: String) {
			mlogger.error(TAG, LogEvent.FEATURE_SCREENSHARE, LogEventValue.SCREENSHARE, msg, null, null, false, false)
		}

		@JavascriptInterface
		fun logInfo(msg: String) {
			mlogger.info(TAG, LogEvent.FEATURE_SCREENSHARE, LogEventValue.SCREENSHARE, msg, null, null, false, false)
		}

		@JavascriptInterface
		fun getFileUrl(): String? {
			return filePresentatationUrl
		}

		@JavascriptInterface
		fun getCookies(): List<String> {
			return meetingUserViewModel.cloundFrontCookies ?: emptyList()
		}

		@JavascriptInterface
		fun playerReady() {
			videoPlayerReady = true
		}

		@JavascriptInterface
		fun getSessionId(): String? {
			return sessionId
		}

		@JavascriptInterface
		fun getParticipantId(): String? {
			return participantId
		}

		@JavascriptInterface
		fun getMeetingUrl(): String? {
			return meetingUserViewModel.meetingFurl
		}

		@JavascriptInterface
		fun updateStreamCount(contentCount: Int, currentCamerasSubcribed: Int, currentCamerasPublished: Int, maxCamerasPublished: Int, contentType: String?, partId: String?) {
			updateToastBasedonStreamCounts(contentCount, currentCamerasSubcribed, currentCamerasPublished, maxCamerasPublished, contentType, partId)
		    if (contentCount > 0 || currentCamerasSubcribed > 0) {
				meetingUserViewModel.contentPresentationActive = true
			}
		}

		@JavascriptInterface
		fun updateCameraButtonActiveState(active: Boolean) {
			meetingUserViewModel.isCameraOn = active
			showContentNotificationToast("localcamera")
			if(!active)
				meetingUserViewModel.isCameraConnecting = false
		}

		@JavascriptInterface
		fun showContentStartedToast(streamType: String) {
			showContentNotificationToast(streamType)
			if(streamType=="localcamera")
				meetingUserViewModel.isCameraConnecting = false
		}

		@JavascriptInterface
		fun getLoggerEndpoint(): String {
			return "https://logsvc.globalmeet.com"
		}

		@JavascriptInterface
		fun webViewReady() {
			isWebViewReady = true
			initiateFoxdenSeesion()
		}

		@JavascriptInterface
		fun toggleNativeZoom() {
			handleDbTabZoom();
		}

		@JavascriptInterface
		fun notifyCameraAdded() {
			isStreamAdded = true
			showHideWebcam()
		}

		@JavascriptInterface
		fun toggleControls() {
			activity?.runOnUiThread {
				if (meetingControls.visibility != View.VISIBLE) {
					meetingControls.animate().alpha(1.toFloat())
					meetingControls.visibility = View.VISIBLE
					llNameContainer.visibility = View.VISIBLE
				} else {
					meetingControls.animate().alpha(0.toFloat())
					meetingControls.visibility = View.GONE
					llNameContainer.visibility = View.GONE
				}
			}
		}

		@JavascriptInterface
		fun notifyMaxCameraLimitReached(limit: String) {
			meetingUserViewModel.isCameraOn = false
			activity?.runOnUiThread {
				getViewForNotification(parentActivity)?.let {
					contentLowerToast = Snackbar.make(it, resources.getString(R.string.maximum_webcams_limit_reached), Snackbar.LENGTH_SHORT)
					contentLowerToast.show()
				}
				mlogger.mixpanelTurnOnCameraModel.webcamError = limit
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT, LogEventValue.MIXPANEL_WEBCAM_SPOTLIGHT.value,
						null, null, false, true)
				mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
						null, null, false, true)

			}
		}
	}

	fun initiateFoxdenSeesion() {
		activity?.runOnUiThread {
			if (sessionId != null) {
				webView.loadUrl(AppConstants.START_SCREEN_SHARE_AUTH)
			}
			foxdenConnectionRetry()
		}
	}

	fun handleDbTabZoom() {
		activity?.runOnUiThread {
			if(currentWebViewScale == initialScale) {
				webView.zoomBy(2.0f)
			} else {
				webView.zoomBy(0.1f)
			}
		}
	}

	@SuppressLint("SourceLockedOrientationActivity")
	fun hideActiveTalkerFragment() {
		meetingUserViewModel.contentPresentationActive = true
		activity?.let {
			it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
			it.runOnUiThread { llActiveTalkerDelay.visibility = View.GONE }
		}
	}

	@SuppressLint("SourceLockedOrientationActivity")
	fun showActiveTalkerFragment() {
		meetingUserViewModel.contentPresentationActive = false
		meetingUserViewModel.isCameraOn = false
		activity?.let {
			it.runOnUiThread {
				if (llActiveTalkerDelay.visibility == View.GONE) {
					it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
					llActiveTalkerDelay.visibility = View.VISIBLE
					isFullScreen = false
					transitionToPortrait()
				}
			}
		}
	}

	fun updateToastBasedonStreamCounts(contentCount: Int, currentCamerasSubcribed: Int,
																		 currentCamerasPublished: Int, maxCamerasPublished: Int, contentType: String?, partId: String?) {
		activity?.runOnUiThread {
			var user: User? = null
			partId?.let {
				user = meetingEventViewModel.getUserById(it)
			}
			mlogger.meetingState.currentCamerasSubcribed = currentCamerasSubcribed
			mlogger.meetingState.currentCamerasPublished = currentCamerasPublished
			mlogger.meetingState.maxCamerasPublished = maxCamerasPublished
			textViewPresenter.visibility = View.VISIBLE
			llNameContainer.visibility = View.VISIBLE
			meetingEventViewModel.totalWebCam = currentCamerasSubcribed
			handlePaginationVisibility()
			if (meetingUserViewModel.turnWebcamOff) {
				if (contentCount == 0 && currentCamerasSubcribed == 1 && user != null) {
					textViewPresenter.text = String.format(resources.getString(R.string.webcam_presenter_name), user?.name + "'s")
				} else if (contentCount == 0 && currentCamerasSubcribed > 3) {
					processPaginationText()
				} else if (contentCount == 0 && currentCamerasSubcribed >= 1 && currentCamerasSubcribed <= 3) {
					textViewPresenter.text = String.format(resources.getString(R.string.presenting_webcams_count), currentCamerasSubcribed)
				} else if (contentCount == 1 && currentCamerasSubcribed == 0) {
					if (user != null && contentType == "screen") {
						textViewPresenter.text = String.format(resources.getString(R.string.presenter_name), user?.name + "'s")
					} else if (contentType == "whiteboard") {
						textViewPresenter.text = String.format(resources.getString(R.string.presenting_whiteboard))
					} else {
						textViewPresenter.text = String.format(resources.getString(R.string.someone_is_presenting_a_file))
					}
				} else if (contentCount > 0 && currentCamerasSubcribed > 3) {
					processPaginationText(true, contentType)
				} else if (contentCount > 0 && currentCamerasSubcribed >= 1 && currentCamerasSubcribed <= 3) {
					textViewPresenter.text = processMixedContentText(contentType, currentCamerasSubcribed)
				}
			} else {
				handleLowBandwidthText()
			}
		}
	}

	fun processMixedContentText (contentType: String?, currentCamerasSubcribed: Int) : String {
		var toastContentText: String = AppConstants.EMPTY_STRING
		if (currentCamerasSubcribed == 1) {
			toastContentText = when (contentType) {
				"screen" -> String.format(resources.getString(R.string.someone_sharing_with_one_camera), currentCamerasSubcribed)
				"pdf" -> String.format(resources.getString(R.string.someone_presenting_with_one_camera), currentCamerasSubcribed)
				"video" -> String.format(resources.getString(R.string.someone_presenting_with_one_camera), currentCamerasSubcribed)
				"whiteboard" -> String.format(resources.getString(R.string.someone_sharing_whiteboard_with_one_camera), currentCamerasSubcribed)
				else -> AppConstants.EMPTY_STRING
			}
		} else {
			toastContentText = when (contentType) {
				"screen" -> String.format(resources.getString(R.string.someone_sharing_with_cameras), currentCamerasSubcribed)
				"pdf" -> String.format(resources.getString(R.string.someone_presenting_with_cameras), currentCamerasSubcribed)
				"video" -> String.format(resources.getString(R.string.someone_presenting_with_cameras), currentCamerasSubcribed)
				"whiteboard" -> String.format(resources.getString(R.string.someone_sharing_whiteboard_with_cameras), currentCamerasSubcribed)
				else -> AppConstants.EMPTY_STRING
			}
		}
		return toastContentText
	}

	fun showContentNotificationToast(streamType: String) {
		activity?.runOnUiThread {
			val toastText = if (streamType == "screen") {
				contentPresenterId?.let {
					Html.fromHtml(String.format(getString(R.string.someone_presenting_screen_share),
							meetingEventViewModel.getUserById(it)?.name))
				}
			} else if (streamType == "camera") {
				Html.fromHtml(String.format(getString(R.string.someone_sharing_camera)))
			} else if (streamType == "pdf" || streamType == "video") {
				Html.fromHtml(String.format(getString(R.string.someone_is_presenting)))
			} else if (streamType == "localcamera") {
				if(meetingUserViewModel.isCameraOn) {
					Html.fromHtml(String.format(getString(R.string.you_are_sharing_camera)))
				} else {
					Html.fromHtml(String.format(getString(R.string.you_are_not_sharing_camera)))
				}
			} else if (streamType == "whiteboard"){
				Html.fromHtml(String.format(getString(R.string.someone_sharing_whiteboard)))
			} else {
				null
			}
			if (toastText != null && vpWebMeeting?.currentItem != 1) {
				showUserSnackBar(toastText.toString())
			}
		}
	}

	fun onVideoActionChange(action: String) {
		activity?.runOnUiThread {
			webView.loadUrl("javascript:changeVideoStatus('$action');")
		}
	}

	fun onVideoSeek(time: String) {
		activity?.runOnUiThread {
			webView.loadUrl("javascript:changeVideoPosition('$time');")
		}
	}

	inner class CustomWebViewClient: WebViewClient() {
		override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
			currentWebViewScale = newScale
		}
		override fun onPageFinished(view: WebView?, url: String?) {
			if(currentPageNumber > 0) {
				webView.loadUrl("javascript:addPdf('${contentId}', '${filePresentatationUrl}', " +currentPageNumber+");");
			}
			super.onPageFinished(view, url)
		}
		override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
			webView?.apply {
				clearCache(false)
				clearHistory()
				clearFormData()
				loadUrl("about:blank")
			}
			showActiveTalkerFragment()
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.FILEPRESENTATION, detail.toString(), null)
			return true
		}
	}

	inner class CustomWebChromeClient: WebChromeClient() {
		override fun onPermissionRequest(request: PermissionRequest) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				request.grant(request.resources)
			}
		}
	}

	override fun onDestroy() {
		resetWebView()
		super.onDestroy()
	}
	open fun getCameraResolution(): ArrayList<Long> {
		var pixelCount: Long = -1
		val resolutions = ArrayList<Long>()
		try {
			val camera = Camera.open()
			val cameraParams = camera.parameters
			var height = 0L
			var width = 0L
			for (j in cameraParams.supportedPictureSizes.indices) {
				val pixelCountTemp = cameraParams.supportedPictureSizes[j].width * cameraParams.supportedPictureSizes[j].height.toLong()
				if (pixelCountTemp > pixelCount) {
					pixelCount = pixelCountTemp
					width = cameraParams.supportedPictureSizes[j].width.toLong()
					height = cameraParams.supportedPictureSizes[j].height.toLong()
				}
			}
			resolutions.add(width)
			resolutions.add(height)
			camera.release()
		} catch (e: Exception){
			mlogger.error(TAG, LogEvent.FEATURE_SCREENSHARE, LogEventValue.SCREENSHARE, "Failure in getting the camera resolution.", e)
		}
		return resolutions
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults) // call to super is needed for fragments
		when (requestCode) {
			AppConstants.PERMISSIONS_REQUEST_CAMERA -> {
				if (PermissionUtil.verifyPermissions(grantResults)) {
					mlogger.mixpanelTurnOnCameraModel.webcamAction = AppConstants.PERMISSION_GRANTED
					mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
							null, null, false, true)
                    isGranted = PackageManager.PERMISSION_GRANTED
					checkWebCamFeature()    //check camera feature enable/disable as per App config
				} else {
					mlogger.mixpanelTurnOnCameraModel.webcamAction = AppConstants.PERMISSION_DENIED
					mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
							null, null, false, true)
					showUserSnackBar(getString(R.string.unable_to_get_camera_permission))
				}
			}
		}
	}
	override fun onResume() {
		super.onResume()
		if(isFullScreen)
			transitionToFullScreen()
	}

	private fun foxdenConnectionRetry() {
		foxdenConnectionTimer = Timer().schedule(RETRY_8000_MS) {
			activity?.runOnUiThread {
				if (foxdenConnectionRetryCount < maxRetryCount) {
					initiateFoxdenSeesion()
					foxdenConnectionRetryCount++
				} else {
					foxdenConnectionTimer?.cancel()
					progressBarCameraDisable?.visibility = View.GONE
					meetingUserViewModel.isFoxdenConnected = false
					getNotificationView()?.let { view ->
						activity?.let {
							val snackbar = Snackbar.make(view, resources.getString(R.string.foxden_error_msg), Snackbar.LENGTH_INDEFINITE)
							snackbar.setAction(R.string.close, View.OnClickListener {

							})
							snackbar.setActionTextColor(resources.getColor(R.color.primary_color_500))
							val snackbarView = snackbar.view
							val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
							textView.maxLines = 4
							snackbar.show()
						}
					}
					meetingUserViewModel.isCameraConnecting = false
				}
			}
		}
	}

	private fun getNotificationView(): View? {
		activity?.let {
			if (it is WebMeetingActivity)
				return (it as WebMeetingActivity).coordinatorLayout
		}
		return null
	}

	@OnClick(R.id.btn_paginate_right_arrow)
    open fun onPaginateNextClick() {
		handleNextClick()
    }

    @OnClick(R.id.btn_paginate_left_arrow)
    open fun onPaginatePreviousClick() {
		handlePreviousClick()
	}

	private fun handleNextClick(){
		if(foxdenSessionActive) {
			meetingEventViewModel.currentWebCamPage += 1
			webView.loadUrl("javascript:nextPage();")
			handlePaginationVisibility()
		}
	}

	private fun handlePreviousClick(){
		if(foxdenSessionActive) {
			meetingEventViewModel.currentWebCamPage -= 1
			webView.loadUrl("javascript:prevPage();")
			handlePaginationVisibility()
		}
	}

	private fun handlePaginationVisibility() {
		btnPaginateNext.visibility = View.INVISIBLE
		btnPaginatePrevious.visibility = View.INVISIBLE
		btnPaginateNext.isEnabled = false
		btnPaginatePrevious.isEnabled = false
		isSwipeRightEnabled = false
		isSwipeLeftEnabled = false
		val camCount = if(meetingUserViewModel.isCameraOn){
			meetingEventViewModel.totalWebCam -1
		} else {
			meetingEventViewModel.totalWebCam
		}
		if(camCount > 3) {
			btnPaginateNext.visibility = View.VISIBLE
			btnPaginatePrevious.visibility = View.VISIBLE
			val numPages = ((camCount-1)/webcamsPerPage) + 1
			if(numPages > 1){
				if(meetingEventViewModel.currentWebCamPage >= numPages) {
					meetingEventViewModel.currentWebCamPage = numPages
				}
				if(meetingEventViewModel.currentWebCamPage == 1) {
					btnPaginateNext.isEnabled = true
					isSwipeRightEnabled = true
				} else if(meetingEventViewModel.currentWebCamPage == numPages) {
					btnPaginatePrevious.isEnabled = true
					isSwipeLeftEnabled = true
				} else {
					btnPaginateNext.isEnabled = true
					btnPaginatePrevious.isEnabled = true
					isSwipeLeftEnabled = true
					isSwipeRightEnabled = true
				}
			}
		} else if(!isFullScreen) {
			meetingEventViewModel.currentWebCamPage = 1
		}
		if(!meetingUserViewModel.turnWebcamOff) {
			btnPaginateNext.visibility = View.INVISIBLE
			btnPaginatePrevious.visibility = View.INVISIBLE
		}
	}

    private fun processPaginationText(mixedContent: Boolean = false, contentType: String? ="") {
        val contentText = String.format(resources.getString(R.string.presenting_webcams_count_lowercase), meetingEventViewModel.totalWebCam)
        if (mixedContent) {
            textViewPresenter.text = when (contentType) {
                "screen" -> String.format(resources.getString(R.string.screenshare_with_webcam_pagination), contentText)
                "pdf" -> String.format(resources.getString(R.string.file_with_webcam_pagination), contentText)
                "video" -> String.format(resources.getString(R.string.file_with_webcam_pagination), contentText)
                "whiteboard" -> String.format(resources.getString(R.string.whiteboard_with_webcam_pagination), contentText)
                else -> AppConstants.EMPTY_STRING
            }
        } else {
			textViewPresenter.text = contentText
		}
    }

	fun handleLowBandwidthText(): Boolean {
		return if (!meetingUserViewModel.turnWebcamOff && meetingEventViewModel.totalWebCam > 0) {
			if(meetingEventViewModel.totalWebCam == 1 && meetingUserViewModel.isCameraOn) {
				textViewPresenter.text = resources.getString(R.string.low_bandwidth_mode)
			}
			else {
				textViewPresenter.text = resources.getString(R.string.low_bandwidth_mode_cam_hidden)
			}
			true
		} else if (!meetingUserViewModel.turnWebcamOff && meetingEventViewModel.totalWebCam == 0) {
			textViewPresenter.text = resources.getString(R.string.low_bandwidth_mode)
			true
		} else false
	}
	 private fun checkWebCamFeature(){
		if (meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS.feature)) {
			if ((meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature) &&
							InternetConnection.isConnectedWifi(context)) || (meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature) && meetingUserViewModel.featureManager.isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature))) {
				handleShareCameraEvents()
			} else {
				toggleCamera(false)
				showCameraDisableDialog()
			}
		}
	}

	@Override
	fun onTouch(v: View, event: MotionEvent): Boolean {
		val camCount = if (meetingUserViewModel.isCameraOn) {
			meetingEventViewModel.totalWebCam - 1
		} else {
			meetingEventViewModel.totalWebCam
		}
		if (camCount > 3 && meetingUserViewModel.turnWebcamOff) {
			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					x1 = event.x
					y1 = event.y
					vpWebMeeting?.requestDisallowInterceptTouchEvent(true)
					}
				MotionEvent.ACTION_UP -> {
					x2 = event.x
					y2 = event.y
					handleSwipe()
					vpWebMeeting?.requestDisallowInterceptTouchEvent(false)
				}
				MotionEvent.ACTION_CANCEL -> {
					vpWebMeeting?.isPagingEnabled = true
				}
			}
		}
		return false
	}

	private fun handleSwipe() {
		val deltaX = x2 - x1
		val deltaY = y2 - y1
		if ((Math.abs(deltaX) > 150) && (Math.abs(deltaX) > Math.abs(deltaY)))
		{
			if (x2 > x1)
			{ //swipe left to right
				if(isSwipeLeftEnabled) {
					handlePreviousClick()
				}
			}
			else
			{ // swipe right ot left
				if(isSwipeRightEnabled) {
					handleNextClick()
				}
			}
		}
	}
}