package com.pgi.convergencemeetings.meeting.gm5.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.SoftphoneConstants
import com.pgi.convergence.enums.NotificationConstants
import com.pgi.convergence.listeners.KeyboardOpenCloseListener
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.*
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.*
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneConstants
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.*
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.DialInFragmentContractor.DialInActivityContract
import com.pgi.convergencemeetings.meeting.gm5.ui.chat.ChatFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.chat.ChatListFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.chat.NewPrivateChatSelectionFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.content.ContentPresentationFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.FeedbackCommentFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.MiscRoomViewFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.OverflowMenuDialogFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.FeedbackFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.ParticipantListFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.security.MeetingSecurityFragment
import com.pgi.convergencemeetings.models.Chat
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.NetworkQualityChecker.connectionQuality
import com.pgi.convergencemeetings.utils.NetworkQualityChecker.stopBandWidthTest
import com.pgi.convergencemeetings.utils.NetworkQualityChecker.testNetworkQualityUsingUrl
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.Mixpanel
import com.pgi.network.TurnServerInfoManager
import com.pgi.logging.enums.*
import com.pgi.network.models.MeetingRoomInfoResponse
import com.pgi.network.models.UAPIEvent
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.util.*
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
@UnstableDefault
@ObsoleteCoroutinesApi
class WebMeetingActivity : BaseMeetingActivity(), AudioSelectionFragmentContractor.activity, DialInActivityContract, DialOutFragmentContrator.activity, AudioSelectionShimmerContractor.activity, KeyboardOpenCloseListener.Callback, LifecycleObserver {
	@BindView(R.id.root_view)
	lateinit var mRootView: View

	@BindView(R.id.ll_meeting_controls)
	lateinit var mMeetingControlsView: View

	@BindView(R.id.tabs)
	lateinit var tlWebMeeting: TabLayout

	@BindView(R.id.viewPager)
	lateinit var vpWebMeeting: CustomViewPager

	@BindView(R.id.imgvw_mic_level_1)
	lateinit var ivMicLevel_1: ImageView

	@BindView(R.id.imgvw_mic_level_2)
	lateinit var ivMicLevel_2: ImageView

	@BindView(R.id.imgvw_mic_level_3)
	lateinit var ivMicLevel_3: ImageView

	@BindView(R.id.imgvw_mic_level_4)
	lateinit var ivMicLevel_4: ImageView

	@BindView(R.id.imgvw_mic_level_5)
	lateinit var ivMicLevel_5: ImageView

	@BindView(R.id.imgvw_mic_level_6)
	lateinit var ivMicLevel_6: ImageView

	@BindView(R.id.imgvw_mic_level_7)
	lateinit var ivMicLevel_7: ImageView

	@BindView(R.id.imgvw_mic_level_8)
	lateinit var ivMicLevel_8: ImageView

	@BindView(R.id.imgvw_mic_level_9)
	lateinit var ivMicLevel_9: ImageView

	@BindView(R.id.imgvw_mic_level_10)
	lateinit var ivMicLevel_10: ImageView

	@BindView(R.id.rl_web_meeting_view)
	lateinit var rlMeetingView: RelativeLayout

	@BindView(R.id.shimmer_view_group)
	lateinit var mShimmerViewGroup: ViewGroup

	@BindView(R.id.fl_fragment_place_holder)
	lateinit var flFragmentPlaceHolder: FrameLayout

	@BindView(R.id.btn_overflow_options)
	lateinit var btnCallSetting: ImageButton

	@BindView(R.id.fl_chatfragment_place_holder)
	lateinit var flChatFragmentPlaceHolder: FrameLayout

	@BindView(R.id.btn_exit_meeting)
	lateinit var ibExitButton: ImageButton

	@BindView(R.id.btn_mute_meeting)
	lateinit var btnMicToggle: ToggleButton

	@BindView(R.id.progress_mic_disabled)
	lateinit var progressMicButton: ProgressBar

	@BindView(R.id.btn_camera)
	lateinit var btnCameraToggle: ToggleButton

	@BindView(R.id.ll_fullscreen_progress_bar)
	lateinit var llMeetingRoomProgressBar: LinearLayout

	@BindView(R.id.btn_close_shimmer)
	lateinit var btnCloseShimmer: ImageView

	@BindView(R.id.internet_reconnecting_banner_layout)
	lateinit var mInternetReconnectingBannerLayout: ViewGroup

	@BindView(R.id.recordingIndicator)
	lateinit var ivRecordingIndicator: ImageView

	@BindView(R.id.floatingSnackBar)
	lateinit var coordinatorLayout: CoordinatorLayout

	@BindView(R.id.progress_meeting_camera_disabled)
	lateinit var progressCameraButton: ProgressBar

	@BindView(R.id.rl_top_bar_container)
	lateinit var rlTopBarContainer: RelativeLayout

	@BindView(R.id.tv_gm5_full_name_web)
	lateinit var tvfullname: TextView

	@BindView(R.id.tv_total_participant)
	lateinit var tvToatalParticipant: TextView

	@BindView(R.id.tv_exit_meeting)
	lateinit var tvExitMeeting: TextView

	private var mShimmerLayout: ShimmerLayout? = null
	private var mFragmentManager: FragmentManager? = null
	private var mCurrentFragment: Fragment? = null
	private var mAdapter: WebMeetingTabAdapter? = null
	private var mChatTabView: View? = null
	private var mAlertDialog: Dialog? = null
	private var mMicLevelView: Array<ImageView?> = emptyArray()
	private var audioRecord: AudioRecord? = null
	private var networkSnackbar: Snackbar? = null
	private var audioSnackBar: Snackbar? = null
	private var waitingRoomSnackBar: SimpleCustomSnackbar? = null
	private var ragAlarmSnackbar: SimpleCustomSnackbar? = null
	private var lowBandwidthSnackbar: SimpleCustomSnackbar? = null
	private var mLastMicSignalLevel = 0
	private var mUnreadChatCount = 0
	private var mIsMeetingHost = false
	private var audioRecordMinSize = -1
	private var mFirstName: String? = null
	private var mIsOwnRoom: Boolean = false
	private var mIsVoipEnabled = false
	private var mIsDialInEnabled = false
	private var mIsChangeAudioFlow = false
	private var mIsInternetConnectionChange = false
	private var isMicButtonEnable = false
	private var isSpeakerButtonEnable = true
	private var hasNetworkSwitchDialogAlreadyShown = false
	private var exitActivity = false
	private var pendingDialOutDisconnect = false
	private var pendingVoipDisconnect = false
	private var isMusicOnHold = false
	private var isChecked = false
	private var isDelegateHost = false
	private var isSoftPhoneConnectSuccess = false
	private var mKeyboardOpenCloseListener: KeyboardOpenCloseListener? = null
	private var mWebMeetingPresenter: WebMeetingPresenter? = null
	private val nrInteractionId: String? = null
	private var entryPoint: String? = null
	private var softPhoneReconnectCount = 0
	private var dialOutPhoneNumber: Phone? = null
	private val compositeDisposable = CompositeDisposable()
	private var voipFailedDialog: AlertDialog? = null
	private var micLevelHandler: Handler? = null
	internal var mStatus: String? = null
	private var timeoutAlertDialog: DialogInterface? = null
	private var isCameraShared: Boolean = false
	private var isFoxdenSessionComplete: Boolean = false
	var errorSnackbar: Snackbar? = null
	private var mTurnWebCamOff = true
	lateinit var mActiveTalker: ActiveTalker
	fun getFragmentRefreshListener(): FragmentRefreshListener? {
		return fragmentRefreshListener
	}
	private var mChat : Chat? = null
	private var isCameraPermissonDialogOpen: Boolean = false
	fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener?) {
		this.fragmentRefreshListener = fragmentRefreshListener
	}

	private var fragmentRefreshListener: FragmentRefreshListener? = null
	private val mUserList: MutableList<User> = ArrayList()
	private var isPrivateChatEnable = true
	private var mConversationID: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		mlogger.startMetric(LogEvent.METRIC_ROOM_VIEW_STARTUP)
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.GM5_MEETING_ROOM.interaction)
		mlogger.debug(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity onCreate()",
				null, null, true, false)
		super.initRetryCallBacks()
		setContentView(R.layout.web_meeting_layout)
		initView()
		mWebMeetingPresenter = WebMeetingPresenter(this)
		registerViewModelListener()
		handleIntentData()
		setConnectionValues()
		lifecycle.addObserver(this)
	}

	// Init view and helpers start
	@SuppressLint("InflateParams")
	fun initView() {
		ButterKnife.bind(this)
		mAdapter = WebMeetingTabAdapter(supportFragmentManager)
		mAdapter?.addFragment(ParticipantListFragment())
		mAdapter?.addFragment(ContentPresentationFragment())
		mMicLevelView = arrayOf(
				ivMicLevel_1, ivMicLevel_2, ivMicLevel_3, ivMicLevel_4,
				ivMicLevel_5, ivMicLevel_6, ivMicLevel_7, ivMicLevel_8, ivMicLevel_9, ivMicLevel_10)
		vpWebMeeting.addOnPageChangeListener(SimpleOnPageChangeListener())
		vpWebMeeting.adapter = mAdapter
		vpWebMeeting.offscreenPageLimit = 2
		tlWebMeeting.setupWithViewPager(vpWebMeeting)
		mChatTabView = LayoutInflater.from(this).inflate(R.layout.tab_chat, tlWebMeeting, false)
		// error
		setupTab()
		mShimmerLayout = ShimmerLayout(this)
		mShimmerLayout?.layoutParams = FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT)
		mShimmerLayout?.setGhostImage(R.drawable.activity_web_meeting_ghost)
		if (mShimmerLayout?.parent != null) {
			(mShimmerLayout?.parent as ViewGroup).removeView(mShimmerLayout)
		}
		mShimmerViewGroup.addView(mShimmerLayout)
		updateMicButton(false)
		mKeyboardOpenCloseListener = KeyboardOpenCloseListener()
		mKeyboardOpenCloseListener?.registerKeyboardOpenCloseListener(mRootView, this)
	}

	private fun setupTab() {
		//Custom chat tab
		mUnreadChatCount = 0
		var tab: TabLayout.Tab? = null
		try {
			tab = tlWebMeeting.getTabAt(CHAT_FRAGMENT_INDEX)
			tab?.setCustomView(R.layout.custome_tab_chat)
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
					"setupTab() - Error Setting up tabs", ex, null, true, false)
		}
		try {
			tab = tlWebMeeting.getTabAt(PARTICIPANT_LIST_FRAGMENT_INDEX)
			tab = tlWebMeeting.getTabAt(SCREEN_SHARE_FRAGMENT_INDEX)
			// setting tab text Meeting
			tab?.setText(R.string.tab_text_metting)
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
					"setupTab() - Error setting up tab icons ", ex, null, true, false)
		}
		tlWebMeeting.addOnTabSelectedListener(
				object : ViewPagerOnTabSelectedListener(vpWebMeeting) {
					@SuppressLint("SourceLockedOrientationActivity")
					override fun onTabSelected(tab: TabLayout.Tab) {
						super.onTabSelected(tab)
						CommonUtils.hideKeyboard(this@WebMeetingActivity)
						val fragment = mAdapter?.getItem(SCREEN_SHARE_FRAGMENT_INDEX)
						if (tab.position == CHAT_FRAGMENT_INDEX) {
							requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
							val tv_Chat=tab.customView?.findViewById(R.id.tv_chat) as TextView
							tv_Chat.setTextColor(applicationContext.resources.getColor(R.color.grey,null))
							onChatTabSelected()
						}
						if (tab.position == SCREEN_SHARE_FRAGMENT_INDEX && findViewById<View>(R.id.web_active_talker_container).visibility == View.GONE) {
							requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
						} else {
							requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
						}
					}

					override fun onTabUnselected(tab: TabLayout.Tab) {
						super.onTabUnselected(tab)
						if (tab.position == CHAT_FRAGMENT_INDEX) {
							// chenging tab unselected color
							val tv_Chat=tab.customView?.findViewById(R.id.tv_chat) as TextView
							tv_Chat.setTextColor(applicationContext.resources.getColor(R.color.grayMedium,null))
							/**
							When user swipe or select other tab, then remove the SelectPrivateChatFragment from fragmentManager
							 */
							removeNewPrivateChatSelectionFragmentOnPause()
						}
					}
				})
		try {
			tab = tlWebMeeting.getTabAt(AppConstants.PARTICIPANT_TAB_INDEX)
			if (tab != null) {
				// setting tab text Guests
				tab?.setText(R.string.tab_text_guest)
				tab.select()
			}
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
					"setupTab() - Error: on Tab Selection Click", ex, null, true, false)
		}
	}

	private fun removeFragment() {
		try {
			mFragmentManager?.let {
				val transaction = it.beginTransaction()
				mCurrentFragment?.let {
					transaction.remove(it)
					mCurrentFragment = null
				}
				try {
					transaction.commit()
				} catch (ise: IllegalStateException) {
					mlogger.error(
							TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM,
							"WebMeetingActivity removeFragment() - Error Removing fragment", ise, null, true,
							false)
					transaction.commitAllowingStateLoss()
				}
			}
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM,
					"WebMeetingActivity removeFragment() - Error Removing fragment", ex, null, true,
					false)
		}
	}

	private fun replaceFragments(fragment: Fragment,isAddToBack : String="") {
		if (!isFinishing) {
			mCurrentFragment = fragment
			mFragmentManager = supportFragmentManager
			val transaction = mFragmentManager?.beginTransaction()
			if(fragment is ChatFragment) {
				transaction?.replace(R.id.fl_chatfragment_place_holder, fragment, fragment.javaClass.simpleName)
			}
			else {
				transaction?.replace(R.id.fl_fragment_place_holder, fragment, fragment.javaClass.simpleName)
			}
			if(isAddToBack.isNotEmpty()) {
				transaction?.addToBackStack(isAddToBack)
			}
			transaction?.commit()
		}
	}

	private fun replaceFragmentsFade(fragment: Fragment) {
		if (!isFinishing) {
			mCurrentFragment = fragment
			mFragmentManager = supportFragmentManager
			val transaction = mFragmentManager?.beginTransaction()
			transaction?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in)
			transaction?.replace(R.id.fl_fragment_place_holder, fragment, fragment.javaClass.simpleName)
			transaction?.commitAllowingStateLoss()
		}
	}

	private fun openFeedbackFragment(softPhoneDismissed: Boolean) {
		CommonUtils.hideKeyboard(this)
		dismissExitLeaveMeetingDialogIfShowing()
		if (mMeetingUserViewModel?.userInMeeting == true || softPhoneDismissed) {
			onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, "")
			createSoftPhoneIntent(PGiSoftPhoneConstants.STOP_SERVICE.name)
			val feedbackFragment = FeedbackFragment()
			replaceFragments(feedbackFragment)
			flFragmentPlaceHolder.visibility = View.VISIBLE
			reconnectVoip = false
		} else {
			finishActivity(true, Activity.RESULT_CANCELED)
		}
	}

	private fun hideLoadingScreen() {
		mShimmerLayout?.stopShimmerAnimation()
		mShimmerViewGroup.visibility = View.GONE
	}

	private fun showAudioLoadingScreen() {
		val audioSelectionProgressFragment = AudioSelectionProgressFragment()
		replaceFragments(audioSelectionProgressFragment)
		mlogger.debug(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity showAudioLoadingScreen() - Showing Shimmer Layout",
				null, null, true, false)
	}

	override fun showDialOutSelectionFragment() {
		val phones = mMeetingUserViewModel?.getDialOutNumbers() ?: emptyList()
		val dialOutSelectionFragment = DialOutSelectionFragment.newInstance()
		dialOutSelectionFragment.initDialoutFragment(this, mIsMeetingHost, phones, this, true)
		replaceFragments(dialOutSelectionFragment)
	}

	override fun showDialOutFragment(selectedPhoneNumber: Phone) {
		val phones = mMeetingUserViewModel?.getDialOutNumbers() ?: emptyList()
		val dialOutFragment = DialOutFragment.newInstance()
		mFirstName?.let { dialOutFragment.initDialoutFragment(this, mIsMeetingHost, it, phones, selectedPhoneNumber, this, true) }
		replaceFragments(dialOutFragment)
	}

	override fun showAudioFragment() {
		val isVoipEnabled = mIsVoipEnabled && mSoftPhoneAvailable
		if (!isVoipEnabled) {
			val msg = "showAudioFragment: VOIP is not enabled. Softphone available=" + java.lang.String.valueOf(mSoftPhoneAvailable)
			mlogger.error(TAG, LogEvent.ERROR, LogEventValue.AUDIOSELECTION, msg, null, null, false, false)
		}
		val audioSelectionFragment = mMeetingUserViewModel?.isSpeakerOn?.let { speaker ->
			mMeetingUserViewModel?.isMute?.let { mute ->
				mMeetingUserViewModel?.isFreemiumEnabled()?.let { freemium ->
					mMeetingUserViewModel?.isDialOutBlocked()?.let { dialoutblocked ->
						AudioSelectionFragment.newInstance(
								isVoipEnabled, mIsDialInEnabled, speaker,
								mute, mFirstName,mIsOwnRoom, true, freemium, dialoutblocked)
					}
				}
			}
		}
		audioSelectionFragment?.let {
			replaceFragments(it)
		}
		mlogger.debug(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity showAudioFragment() - Showing Audio Selection Fragment", null, null,
				true, false)
	}

	fun showAudioSelectionView() {
		showAudioFragment()
	}

	fun showAudioSelectionPage() {
		rlMeetingView.visibility = View.GONE
		rlTopBarContainer.visibility = View.GONE
		hideLoadingScreen()
		flFragmentPlaceHolder.visibility = View.VISIBLE
		showAudioSelectionView()
	}

	private val isFeedbackFragmentVisible: Boolean
		get() {
			val fragment = supportFragmentManager.findFragmentByTag(
					FeedbackFragment::class.java
							.simpleName)
			return fragment is FeedbackFragment && fragment.isVisible()
		}

	private val isAudioSelectionFragmentVisible: Boolean
		get() {
			val fragment = supportFragmentManager.findFragmentById(R.id.fl_fragment_place_holder)
			return flFragmentPlaceHolder.visibility == View.VISIBLE && fragment is AudioSelectionFragment &&
					fragment.isVisible()
		}
	private val isAudioSelectionProgressFragmentVisible: Boolean
		get() {
			val fragment = supportFragmentManager.findFragmentById(R.id.fl_fragment_place_holder)
			return flFragmentPlaceHolder.visibility == View.VISIBLE && fragment is AudioSelectionProgressFragment &&
					fragment.isVisible()
		}

	private val isMeetingSecurityFragmentVisible: Boolean
		get() {
			val fragment = supportFragmentManager.findFragmentByTag(
					MeetingSecurityFragment::class.java
							.simpleName)
			return fragment is MeetingSecurityFragment && fragment.isVisible()
		}

	private val isNewPrivateChatSelectionFragmentVisible: Boolean
		get() {
			val fragment = supportFragmentManager.findFragmentByTag(
					NewPrivateChatSelectionFragment::class.java
							.simpleName)
			return fragment is NewPrivateChatSelectionFragment && fragment.isVisible()
		}
	
	inner class SimpleOnPageChangeListener : OnPageChangeListener {
		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
			if (POSITION == position && DISMISSAL_POSITION_OFFSET < positionOffset &&
					mMeetingUserViewModel?.contentPresentationActive == true && mMeetingUserViewModel?.screenShareFullScreen == false) {
				mMeetingControlsView.visibility = View.VISIBLE
			}
		}

		override fun onPageSelected(position: Int) {
			when (position) {
				0 -> {
					NewRelic.setInteractionName(Interactions.PARTICIPANT_LIST.interaction)
					mMeetingUserViewModel?.tabValue = PARTICIPANT_LIST_FRAGMENT_INDEX
				}
				1 -> {
					NewRelic.setInteractionName(Interactions.CONTENT_VIEW.interaction)
					mMeetingUserViewModel?.tabValue = SCREEN_SHARE_FRAGMENT_INDEX
				}
				2 -> {
					NewRelic.setInteractionName(Interactions.CHAT.interaction)
					mMeetingUserViewModel?.tabValue = CHAT_FRAGMENT_INDEX
				}
			}
		}

		override fun onPageScrollStateChanged(state: Int) {}
	}

	override fun onKeyboardHide() {
		if (vpWebMeeting.currentItem == SCREEN_SHARE_FRAGMENT_INDEX) {
			if (mMeetingUserViewModel?.contentPresentationActive == false && mMeetingUserViewModel?.screenShareFullScreen == false) {
				mMeetingControlsView.visibility = View.VISIBLE
			}
		} else {
			mMeetingControlsView.visibility = View.VISIBLE
		}
	}

	override fun onKeyboardVisible() {
		if (vpWebMeeting.currentItem == CHAT_FRAGMENT_INDEX || flChatFragmentPlaceHolder.visibility == View.VISIBLE) {
			mMeetingControlsView.visibility = View.GONE
		}
	}

	// Init view and helpers end
	private fun registerViewModelListener() {
		mMeetingUserViewModel = ViewModelProviders.of(this).get(MeetingUserViewModel::class.java)
		mMeetingUserViewModel?.apply {
			userFlowStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { userFlowStatus: UserFlowStatus -> respondToUserStatus(userFlowStatus) })
			audioStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { status: AudioStatus -> setAudioProgressState(status) })
			audioType.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { type: AudioType -> setMicDrawables(type) })
			cameraStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { checked: Boolean -> toggleCamera(checked) })
			muteStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { checked: Boolean -> toggleMic(checked) })
			muteBtnStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { enabled: Boolean -> toggleMicEnableState(enabled) })
			retryStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { retryStatus: RetryStatus -> onServiceRetryFailed(retryStatus) })
			waitingRoomEnabledStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { enabled: Boolean -> toggleWaitingRoomState(enabled) })
			guestWaitingList.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { waitListUsers: List<User> -> if (waitListUsers.isNotEmpty()) showWaitingToast() })
			cameraConnectingStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { checked: Boolean -> toggleCameraEnabledState(checked) })
			privateChatLocked.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { enabled: Boolean -> updateTabs(enabled) })
			foxdenConnectionStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { connected: Boolean ->
				isFoxdenSessionComplete = connected
				if(connected){
					errorSnackbar?.let{
						it.dismiss()
					}
				}
			})
			conversationId.observe(this@WebMeetingActivity, androidx.lifecycle.Observer {
				mConversationID = it
				openChatFragment()
			})
		}
		mMeetingEventViewModel = ViewModelProviders.of(this).get(MeetingEventViewModel::class.java)
		mMeetingEventViewModel?.apply {
			users.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { users: List<User> ->
				mUserList.clear()
				mUserList.addAll(users)
				if(!(this@WebMeetingActivity::mActiveTalker.isInitialized && mActiveTalker.isTalking))
                updateParticipant(mUserList.size)
			})
			activeTalker.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { activeTalker: ActiveTalker ->
				updateTalker(activeTalker)
				mActiveTalker = activeTalker
			})
			userFlowStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { userFlowStatus: UserFlowStatus -> respondToUserStatus(userFlowStatus) })
			audioJoinedStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { joined: Boolean -> if (joined) setMixPanelJoinAudio() })
			meetingStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { meetingStatus: MeetingStatus -> respondToMeetingStatus(meetingStatus) })
			isMusicOnHold.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { isMusicOnHold: Boolean -> updateAfterMusicOnHold(isMusicOnHold) })
			chatReceived.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { chatReceived: Chat -> updateChatTabNotificationCount(chatReceived) })
			meetingRecordStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { status: MeetingRecordStatus -> handleRecording(status) })
			meetingMuteStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { status: MeetingMuteStatus -> handleAllGuestMuteUnmute(status) })
			waitingRoomEnabledStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { enabled: Boolean -> toggleWaitingRoomState(enabled) })
			guestWaitingList.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { waitListUsers: List<User> -> if (waitListUsers.isNotEmpty()) showWaitingToast() })
			retryStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { retryStatus: RetryStatus -> onServiceRetryFailed(retryStatus) })
			privateChatEnableStatus.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { updatePrivateChatStatus(it) })
			conversationId.observe(this@WebMeetingActivity, androidx.lifecycle.Observer {
				mConversationID = it
				openChatFragment()
			})
			chatUnreadCountUpdated.observe(this@WebMeetingActivity, androidx.lifecycle.Observer { conversationId: String -> updateChatUnreadCount(conversationId) })
			isDelegateHost = getCurrentUser()?.delegateRole ?: false
		}
		mMeetingUserViewModel?.onMeetingRoomInfoSuccess = {
			handleMeetingInfoSuccess(it)
		}
	}

	//add chat tab based on private chat flag- if privateChatLocked true then use existing UI else show chat list
	private fun updateTabs(enabled: Boolean) {
		if (enabled) {
			mAdapter?.addFragment(ChatFragment())
		} else {
			mAdapter?.addFragment(ChatListFragment())
		}
		mAdapter?.notifyDataSetChanged()
		setupTab()
	}

	private fun updatePrivateChatStatus(it: Boolean) {
		isPrivateChatEnable = it
	}

	private fun setJoinedValuesAndView(wait: Boolean, status: String) {
		if (mMeetingUserViewModel != null) {
			mMeetingUserViewModel?.userIsInWaitingRoom = wait
			if (mMeetingUserViewModel?.authResponse != null) {
				mIsMeetingHost = mMeetingUserViewModel?.authResponse?.roomRole?.toLowerCase() ==
						AppConstants.HOST.toLowerCase() || isDelegateHost
			}
			val meetingRoomInfoResponse = mMeetingUserViewModel?.getRoomInfoResponse()
			if (meetingRoomInfoResponse != null) {
				mIsWaitingRoomEnabled = meetingRoomInfoResponse.waitingRoom.enabled
				val meetingRoomAudio = meetingRoomInfoResponse.audio
				if (meetingRoomAudio != null) {
					mIsVoipEnabled = meetingRoomAudio.sipURI != null
					mIsDialInEnabled = meetingRoomAudio.primaryAccessNumber != null
					mFirstName = meetingRoomInfoResponse.meetingOwnerGivenName
					mIsOwnRoom = meetingRoomInfoResponse.meetingOwnerClientId == ClientInfoDaoUtils.getInstance().clientId
					updateTopBar(mFirstName.toString())
				}
			} else {
				mIsDialInEnabled = false
				mIsVoipEnabled = false
			}
			if (!mIsVoipEnabled) {
				mMeetingUserViewModel?.stopTurnServerTimer()
			}
			val joinMeetingResponse = mMeetingUserViewModel?.getJoinResponse()
			if (joinMeetingResponse != null) {
				sipIdentifier = SoftphoneConstants.SIP_COLON +
						joinMeetingResponse.sipIdentifier +
						SoftphoneConstants.PGI_COM
			}
			startBandWidthTest()
			startPollAndShowView(wait, status)
			isPrivateChatEnable = mMeetingUserViewModel?.getRoomInfoResponse()?.privateChat?.enabled == true
		}
	}

	private fun respondToUserStatus(userFlowStatus: UserFlowStatus) {
		when (userFlowStatus) {
			UserFlowStatus.JOIN_MEETING_SUCCESS -> setJoinedValuesAndView(false, userFlowStatus.status)
			UserFlowStatus.JOIN_WAIT_ROOM -> setJoinedValuesAndView(true, userFlowStatus.status)
			UserFlowStatus.JOIN_LOCK_MEETING,
			UserFlowStatus.JOIN_MEETING_AT_CAPACITY,
			UserFlowStatus.DISMISSED_ROOM_AT_CAPACITY,
			UserFlowStatus.DISMISSED_LOCK,
			UserFlowStatus.DISMISSED_WAIT_TIMEOUT_HOST,
			UserFlowStatus.DISMISSED_WAIT_TIMEOUT_ADMIT -> {
				showWaitingRoomView(userFlowStatus.status)
			}
			UserFlowStatus.PARTICIPANT_ADMITTED -> {
				val user=mMeetingEventViewModel?.getCurrentUser()
				user?.let { admitParticipantRequest(it) }
				mMeetingUserViewModel?.userInMeeting = true
				mMeetingUserViewModel?.userIsInWaitingRoom = false
				if(!user?.roomRole.equals(AppConstants.HOST) && mMeetingEventViewModel?.isCurrentUserPresenter() != true)
					showAudioSelectionView()
				CoreApplication.mLogger.info(
						TAG, LogEvent.PARTICIPANT_ADMITTED, LogEventValue.PARTICIPANT_ADMITTED, LogEventValue.PARTICIPANT_ADMITTED.value, null,
						null, true, true)
			}
			UserFlowStatus.DIAL_OUT_SUCCESS -> if (mMeetingUserViewModel?.audioConnType !== AudioType.NO_AUDIO) {
				mMeetingUserViewModel?.audioConnState = AudioStatus.CONNECTED
				if (mMeetingUserViewModel?.isMute == true) {
					val userId = mMeetingUserViewModel?.currentUserId
					val user = mMeetingEventViewModel?.getCurrentUser()
					if (userId != null && user != null) {
						val audio = user.audio
						val audioId = audio.id
						if (audioId != null) {
							mMeetingUserViewModel?.muteUnmuteUser(userId, audioId, true)
						}
					}
				} else if (mMeetingUserViewModel?.audioConnType == AudioType.VOIP ||
						mMeetingUserViewModel?.audioConnType == AudioType.DIAL_OUT) {
					updateMicButton(true)
				}
				if (mMeetingUserViewModel?.audioConnType == AudioType.DIAL_OUT) {
					UpdateDialOutPhoneTask().execute()
				}
			}
			UserFlowStatus.DIAL_OUT_FAILED -> setMixPanelJoinAudio()
			UserFlowStatus.DIAl_OUT_DISCONNECTED -> {
				if (!pendingDialOutDisconnect) {
					mMeetingUserViewModel?.audioConnType = AudioType.NO_AUDIO
					mMeetingUserViewModel?.audioConnState = AudioStatus.NOT_CONNECTED
					mlogger.attendeeModel.audiocodec = Mixpanel.AUDIO_NONE.value
					mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_NONE.value
					stopLocalMicSampling()
				}
				if (isSoftPhoneConnected) {  // softphone should not still be connected. If it is, we need to hangup.
					mlogger.error(
							TAG, LogEvent.ERROR, LogEventValue.AUDIOSELECTION,
							"Received UserFlowStatus: DIAL_OUT_DISCONNECTED: Hanging up VOIP call.")
					hangUpSoftPhone()
					releaseAudioFocus()
					isSoftPhoneConnected = false
				} else {  // we don't show no audio toast for VOIP yet.  It will be shown once VOIP actually disconnects
					showNoAudioToast()
				}
				pendingDialOutDisconnect = false
			}
			UserFlowStatus.MUTE_UNMUTE_SUCCESS -> if (mMeetingUserViewModel?.audioConnType !== AudioType.NO_AUDIO) {
				mMeetingUserViewModel?.isMuteBtnEnabled = true
				val user = mMeetingEventViewModel?.getCurrentUser()
				if (user != null) {
					val audio = user.audio
					mMeetingUserViewModel?.isMute = audio.mute
				}
			}
			UserFlowStatus.ORIENTATION_CHANGED -> {
				handleOrientationChange()
			}
			UserFlowStatus.END_MEETING -> {
				mMeetingEventViewModel?.clearWaitingListOnMeetingEnd()
			}
			UserFlowStatus.END_MEETING_SUCCESS,
			UserFlowStatus.LEAVE_MEETING_SUCCESS,
			UserFlowStatus.DISMISSED,
			UserFlowStatus.DISMISS_AUDIO_PARTICIPANT,
			UserFlowStatus.JOIN_MEETING_FAILED,
			UserFlowStatus.ROOM_INFO_FAILED,
			UserFlowStatus.LEAVE_MEETING_FAILED,
			UserFlowStatus.END_MEETING_FAILED,
			UserFlowStatus.DISMISSED_INACTIVE_PARTICIPANT -> {
				onExitActivity()
			}
			UserFlowStatus.DISMISSED_BY_HOST -> {
				if (mMeetingUserViewModel?.userIsInWaitingRoom == true) {
					showWaitingRoomView(UserFlowStatus.DISMISSED_BY_HOST.status)
				} else {
					onExitActivity()
				}
			}

			UserFlowStatus.AUTH_MEETING_FAILED -> {
				showInvalidConferenceAlert(this)
				hideLoadingScreen()
				onExitActivity()
			}
			UserFlowStatus.AUTH_MEETING_FAILED_NETWORK,
			UserFlowStatus.JOIN_MEETING_FAILED_NETWORK,
			UserFlowStatus.ROOM_INFO_FAILED_NETWORK -> {
				showInternetConnectionTimeoutDialog()
				hideLoadingScreen()
				onExitActivity()
			}
			UserFlowStatus.WAITING_ROOM_OFF_FAILURE,
			UserFlowStatus.WAITING_ROOM_ON_FAILURE -> {
				Toast.makeText(this, R.string.waiting_room_toggle_failed, Toast.LENGTH_SHORT).show()
			}
			UserFlowStatus.ADD_CHAT_FAILED -> {
				showChatError()
			}
			UserFlowStatus.ADD_CONVERSATION_FAILED -> {
				removeNewPrivateChatSelectionFragment()
				notifyUser(resources.getString(R.string.add_conversation_error_msg), Snackbar.LENGTH_LONG)
			}
			else -> {
			}
		}
	}

	fun showWaitingRoomView(status: String) {
		val waitingRoomFragment = MiscRoomViewFragment.newInstance(mFirstName, status)
		waitingRoomFragment.let { replaceFragmentsFade(it) }
	}

	fun handleMeetingInfoSuccess(infoResponse: MeetingRoomInfoResponse) {
		mMeetingEventViewModel?.waitingRoomEnabledStatus?.postValue(infoResponse.waitingRoom.enabled)
		mMeetingEventViewModel?.frictionFreeEnabledStatus?.postValue(infoResponse.frictionFree.enabled)
		if (!infoResponse.privateChat.enabled && infoResponse.privateChat.enabledLocked) {
			mMeetingUserViewModel?.isPrivateChatLocked = true
		} else {
			mMeetingUserViewModel?.isPrivateChatLocked = false
		}
	}

	private fun onExitActivity() {
		exitActivity = true
		mlogger.clearMeetingModel()
		mMeetingEventViewModel?.pausePolling()
		mMeetingEventViewModel?.clearWaitingListOnMeetingEnd()
		stopInProgressAudio(AudioType.NO_AUDIO)
		openFeedbackFragment(false)
		mMeetingUserViewModel?.clear()
	}

	private fun startBandWidthTest() {
		connectionQuality.subscribe(object : Observer<ConnectionQuality> {
			override fun onSubscribe(d: Disposable) {
				compositeDisposable.add(d)
			}

			override fun onNext(connectionQuality: ConnectionQuality) {
				onConnectionQualityChange(connectionQuality)
			}

			override fun onError(e: Throwable) {}
			override fun onComplete() {}
		})
		mFurl?.let { testNetworkQualityUsingUrl(it) }
	}

	private var prevQuality = ConnectionQuality.UNKNOWN
	private fun onConnectionQualityChange(quality: ConnectionQuality) {
		runOnUiThread {
			if (quality !== prevQuality) { // only log network quality when it changes.
				when (quality) {
					ConnectionQuality.POOR, ConnectionQuality.NONE -> {
						showMeteredNetworkToast()
						mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.POOR, "Network quality is " + quality.name, null, null, true, true)
						mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value, null, null, false, true)
					}
					ConnectionQuality.MODERATE -> {
						hideMeteredNetworkToast()
						mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.MODERATE, "Network quality is Moderate", null, null, true, true)
					}
					ConnectionQuality.GOOD -> {
						hideMeteredNetworkToast()
						mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.GOOD, "Network quality is Good", null, null, true, true)
					}
					ConnectionQuality.EXCELLENT -> {
						hideMeteredNetworkToast()
						mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.EXCELLENT, "Network quality is Excellent", null, null, true, true)
					}
					else -> mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.UNKNOWN, "Network quality is Unknown", null, null, true, true)
				}
			}
			mlogger.mixpanelTurnOnCameraModel.webcamBandwidth = quality.name
			prevQuality = quality
		}
	}

	private fun respondToMeetingStatus(meetingStatus: MeetingStatus) {
		if (meetingStatus === MeetingStatus.ENDED) {
			mMeetingEventViewModel?.pausePolling()
			stopInProgressAudio(AudioType.NO_AUDIO)
			entryPoint = null
			if (mMeetingUserViewModel?.userIsInWaitingRoom == true) {
				showWaitingRoomView(UserFlowStatus.DISMISSED_MEETING_ENDED.status)
			} else {
				openFeedbackFragment(false)
			}
		} else if (meetingStatus === MeetingStatus.STARTED) {
			mlogger.meetingState.currentCamerasSubcribed = 0
			mlogger.meetingState.currentCamerasPublished = 0
			mlogger.meetingState.maxCamerasPublished = 0
			MixpanelJoinEvent()
		}
	}

	private fun setMixPanelJoinAudio() {
		val meetingRoomInfoResponse = mMeetingUserViewModel?.getRoomInfoResponse()
		if (meetingRoomInfoResponse != null) {
			val meetingRoomAudio = meetingRoomInfoResponse.audio
			if (meetingRoomAudio != null) {
				val sipurl = meetingRoomAudio.sipURI
				if (sipurl != null) {
					if (sipurl.contains(AppConstants.BOB.toLowerCase())) {
						mlogger.attendeeModel.conferenceType = AppConstants.BOB
					} else if (mMeetingUserViewModel?.audioConnType == AudioType.VOIP) {
						mlogger.attendeeModel.conferenceType = AppConstants.DIALOG
					}
				}
				mlogger.attendeeModel.meetingServer = meetingRoomInfoResponse.meetingHost
				mlogger.attendeeModel.muted = mMeetingUserViewModel?.isMute.toString()
			}
		}
		MixpanelJoinAudioConnectionEvent()
	}

	private fun MixpanelJoinEvent() {
		if (entryPoint == null) return
		/* log Join Meeting Event to Mixpanel
         Note: we have to wait unit status = started to
         be sure that we have all the information needed for
         logging this event.
       */mlogger.userModel.joinMeetingEntryPoint = entryPoint
		val msg = MIXPANEL_EVENT + AppConstants.JOIN_MEETING_ENTRY_POINT + " from " + entryPoint
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_MEETING, msg, null,
				null, false, true)
		// clear the entry point otherwise it will show on every log message
		mlogger.userModel.joinMeetingEntryPoint = null
		entryPoint = null
	}

	private fun MixpanelJoinAudioConnectionEvent() {
		val msg = MIXPANEL_EVENT + "Join Audio Connection"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_JOIN_AUDIO_CONNECTION, msg, null,
				null, false, true)
	}

	private fun MixpanelNetworkChangeEvent() {
		val msg = MIXPANEL_EVENT + "Network Change"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_NETWORK_CHANGE, msg, null,
				null, false, true)
	}

	private fun MixpanelLockMeetingEvent() {
		MixpanelSetUserType()
		val msg = MIXPANEL_EVENT + "Lock Meeting"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_LOCK_MEETING, msg, null,
				null, false, true)
	}

	private fun MixpanelUnlockMeetingEvent() {
		MixpanelSetUserType()
		val msg = MIXPANEL_EVENT + "Unlock Meeting"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNLOCK_MEETING, msg, null,
				null, false, true)
	}

	private fun MixpanelEnableWaitingRoomEvent() {
		MixpanelSetUserType()
		val msg = MIXPANEL_EVENT + "Enable Waiting Room "
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_ENABLE_WAITING, msg, null,
				null, false, true)
	}

	private fun MixpanelDisableWaitingRoomEvent() {
		MixpanelSetUserType()
		val msg = MIXPANEL_EVENT + "Disable Waiting Room"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_DISABLE_WAITING, msg, null,
				null, false, true)
	}

	private fun MixpanelMuteAllGuestsEvent(type: Mixpanel) {
		MixpanelSetUserType()
		mlogger.mixpanelItem1 = type
		val msg = MIXPANEL_EVENT + "Mute Guests"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MUTE_GUESTS, msg, null,
				null, false, true)
	}

	private fun MixpanelUnmuteAllGuestsEvent(type: Mixpanel) {
		MixpanelSetUserType()
		mlogger.mixpanelItem1 = type
		val msg = MIXPANEL_EVENT + "Unmute Guests"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_UNMUTE_GUESTS, msg, null,
				null, false, true)
	}

	private fun MixpanelRecordEvent() {
		MixpanelSetUserType()
		val msg = MIXPANEL_EVENT + "Record"
		mlogger.info(
				TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_RECORD, msg, null,
				null, false, true)
	}

	private fun MixpanelSetUserType() {
		if (mIsMeetingHost) {
			mlogger.userModel.role = Mixpanel.MEETING_HOST.value
		} else if (isDelegateHost) {
			mlogger.userModel.role = Mixpanel.MEETING_CO_HOST.value
		}
	}

	private fun startPollAndShowView(wait: Boolean, status: String) {
		val joinMeetingResponse = mMeetingUserViewModel?.getJoinResponse()
		try {
			if (joinMeetingResponse != null) {
				mMeetingEventViewModel?.restartPolling(joinMeetingResponse._links.getEvents?.href)
				mMeetingEventViewModel?.setCurrentUserId(joinMeetingResponse.participantId)
			} else {
				mlogger.error(TAG, LogEvent.ERROR, LogEventValue.AUDIOSELECTION, "Join Meeting Response is null", null, null, false, false)
				// if join meeting response is null, we need to take the user out of the meeting.
				exitActivity = true
				openFeedbackFragment(false)
				mMeetingUserViewModel?.clear()
				return
			}
		} catch (e: Exception) {
			e.message?.let { mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.AUDIOSELECTION, it, e, null, false, false) }
		}
		if (!wait) {
			showAudioSelectionView()
		} else {
			showWaitingRoomView(status)
		}
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_INIT.name)
		mlogger.endMetric(LogEvent.METRIC_JOIN_GM5_MEETING, "GM5 Room join time in seconds")
		mlogger.record(LogEvent.FEATURE_GM5MEETINGS)
	}

	// Handling intents
	private fun handleIntentData() {
		if (intent != null) {
			if (intent.getIntExtra(AppConstants.KEY_INVALID_URL, 0) == AppConstants.INVALID_CONFERENCE_CODE) {
				showInvalidConferenceAlert(this)
			}
			if (intent.hasExtra(AppConstants.MEETING_CONFERENCE_ID)) {
				mConferenceId = intent.getStringExtra(AppConstants.MEETING_CONFERENCE_ID)
			}
			if (intent.hasExtra(AppConstants.FIRST_NAME)) {
				mFirstName = intent.getStringExtra(AppConstants.FIRST_NAME)
			}
			if (intent.hasExtra(AppConstants.KEY_FURL) && intent.getStringExtra(AppConstants.KEY_FURL) != null) {
				mFurl = intent.getStringExtra(AppConstants.KEY_FURL)
				startOrJoinMeeting(intent.getStringExtra(AppConstants.KEY_FURL))
				/* extract and save the entry point for joining the meeting for use with Mixpanel */if (intent.hasExtra(AppConstants.JOIN_MEETING_ENTRY_POINT)) {
					entryPoint = intent.getStringExtra(AppConstants.JOIN_MEETING_ENTRY_POINT)
				}
			} else {
				finishActivity(false, AppConstants.INVALID_MEETING_URL)
			}
			mlogger.endMetric(LogEvent.METRIC_ROOM_VIEW_STARTUP, "GM5 Room View create time in seconds")
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		if (intent.action != null && intent.action == AppConstants.HANGUP_SOFTPHONE) {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_STOP.name)
		} else if (intent.action != null && intent.action == AppConstants.MIC_TOGGLE) {
			btnMicToggle.performClick()
		} else if (intent.action != null && intent.action == AppConstants.SPEAKER_TOGGLE) {
			onSpeakerClicked()
		} else if (intent.action != null && intent.action == AppConstants.OPEN_ACTIVITY) {
			//      getMlogger().debug(TAG, ELKConstants.NOTIFICATION_SERVICE, "Opening Activity");
		} else if (intent.getIntExtra(AppConstants.KEY_INVALID_URL, 0) == AppConstants.INVALID_CONFERENCE_CODE) {
			showInvalidConferenceAlert(this)
		} else if (intent.hasExtra(AppConstants.KEY_FURL)) {
			val furl = intent.getStringExtra(AppConstants.KEY_FURL)
			if ((mMeetingUserViewModel?.userInMeeting == true) && (mMeetingUserViewModel?.meetingFurl != furl)) {
				showAlertToLeaveCurrentConference()
			}
		} else {
			finishActivity(true, AppConstants.INVALID_MEETING_URL)
		}
	}

	// Handling intents end
	// Start/Join Meeting start
	private fun startOrJoinMeeting(furl: String?) {
		if (furl != null) {
			rlMeetingView.visibility = View.GONE
			rlTopBarContainer.visibility = View.GONE
			flFragmentPlaceHolder.visibility = View.VISIBLE
			showAudioLoadingScreen()
			checkMeteredConnection()
			mMeetingUserViewModel?.triggerMeetingLaunch(furl)
			mMeetingUserViewModel?.isCameraConnecting = true
		} else {
			finishActivity(false, AppConstants.INVALID_MEETING_URL)
		}
	}

	private fun checkMeteredConnection() {
		if (InternetConnection.isMetered(this.applicationContext)) {
			mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.UNKNOWN, "User is on metered connection", null, null, true, true)
		}
	}

	override fun sendUserToRejoinMeeting() {
		mlogger.error(
				TAG, LogEvent.NETWORK_CONNECTIVITY, LogEventValue.JOIN,
				"sendUsertoRejoinMeeting after network error", null,
				null, true, false)
		reconnectVoip = false
		mIsMeetingHost = false
		dismissExitLeaveMeetingDialogIfShowing()
		val furl = mMeetingUserViewModel?.meetingFurl
		furl?.let {
			onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, it)
			startOrJoinMeeting(it)
		}
		mMeetingUserViewModel?.clear()
	}

	// Start/Join Meeting end
	// Permissions handle
	override fun onNeedPermission(permission: String) {
		when (permission) {
			Manifest.permission.RECORD_AUDIO -> ActivityCompat.requestPermissions(
					this, arrayOf(
					Manifest.permission.RECORD_AUDIO), AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO)
			Manifest.permission.CALL_PHONE -> ActivityCompat.requestPermissions(
					this, arrayOf(
					Manifest.permission.CALL_PHONE), AppConstants.PERMISSIONS_REQUEST_PHONE_CALL)
			Manifest.permission.CAMERA -> ActivityCompat.requestPermissions(this, arrayOf(
					Manifest.permission.CAMERA), AppConstants.PERMISSIONS_REQUEST_CAMERA)
		}
	}

	override fun onPermissionPreviouslyDenied(permission: String) {
		when (permission) {
			Manifest.permission.RECORD_AUDIO -> ActivityCompat.requestPermissions(
					this, arrayOf(
					Manifest.permission.RECORD_AUDIO), AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO)
			Manifest.permission.CALL_PHONE -> ActivityCompat.requestPermissions(
					this, arrayOf(
					Manifest.permission.CALL_PHONE), AppConstants.PERMISSIONS_REQUEST_PHONE_CALL)
			Manifest.permission.CAMERA -> ActivityCompat.requestPermissions(this, arrayOf(
					Manifest.permission.CAMERA), AppConstants.PERMISSIONS_REQUEST_CAMERA)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults) // call to super is needed for fragments
		when (requestCode) {
			AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO -> {
				if (PermissionUtil.verifyPermissions(grantResults)) {
					startSoftphone()
				} else {
                  notifyUserForNotGettingAudioPermission()
				}
			}
			AppConstants.PERMISSIONS_REQUEST_PHONE_CALL -> {
				if (PermissionUtil.verifyPermissions(grantResults)) {
					 handleDialIn("")
				} else {
					notifyUserForNotGettingAudioPermission()
				}
			}
			AppConstants.PERMISSIONS_REQUEST_CAMERA -> {
				if (PermissionUtil.verifyPermissions(grantResults)) {
					if (mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS.feature)!!) {
							if((mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)!! &&
									InternetConnection.isConnectedWifi(this)) || (mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)!! && mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)!!)) {
						btnCameraToggle.isChecked = true
						isCameraShared = true
						mixpanelCameraOnEvent(AppConstants.PERMISSION_GRANTED)
					} else {
						toggleCamera(false)
						showCameraDisableDialog()
					}
				 }
				} else {
					mixpanelCameraOnEvent(AppConstants.PERMISSION_DENIED)
					btnCameraToggle.isChecked = false
					Snackbar.make(coordinatorLayout , getString(R.string.unable_to_get_camera_permission),Snackbar.LENGTH_SHORT).show()
				}
				mMeetingUserViewModel?.isCameraOn = btnCameraToggle.isChecked
			}
		}
	}

	// End Permissions handle
	private fun stopInProgressAudio(requestedConnectType: AudioType) {
		if (mMeetingUserViewModel?.audioConnState === AudioStatus.CONNECTED
				|| mMeetingUserViewModel?.audioConnState === AudioStatus.CONNECTING) {
			if (mMeetingUserViewModel?.audioConnType !== requestedConnectType) {
				if (mMeetingUserViewModel?.audioConnType === AudioType.DIAL_OUT) {
					pendingDialOutDisconnect = true
					stopDialOut()
				} else if (mMeetingUserViewModel?.audioConnType === AudioType.VOIP) {
					pendingVoipDisconnect = true
					hangUpSoftPhone()
					releaseAudioFocus()
				}
			} else {
				return
			}
		}
		mMeetingUserViewModel?.audioConnType = AudioType.NO_AUDIO
		mMeetingUserViewModel?.audioConnState = AudioStatus.NOT_CONNECTED
		mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_NONE.value
		resetMicLevelView()
		updateMicLevelEnabled(false)
	}

	override fun setAudioBehaviour(isMuteMe: Boolean, isActivateSpeaker: Boolean) {
		mMeetingUserViewModel?.isMute = isMuteMe
		mMeetingUserViewModel?.isSpeakerOn = isActivateSpeaker
		mIsSpeakerOn = isActivateSpeaker
	}

	override fun muteUnmuteChangeConnection(isMuteMe: Boolean) {
		mMeetingUserViewModel?.isMute = isMuteMe
		mMeetingUserViewModel?.isMuteActivated = isMuteMe
		afterChangeConnectionMuteUnmute(isMuteMe)
	}

	private fun afterChangeConnectionMuteUnmute(isMute: Boolean) {
		val user = mMeetingEventViewModel?.getCurrentUser()
		val userId = mMeetingUserViewModel?.currentUserId ?: user?.id
		if (userId != null && user != null) {
			val audio = user.audio
			val audioId = audio.id
			if (audioId != null) {
                mMeetingUserViewModel?.muteStatus?.postValue(isMute)
                mMeetingUserViewModel?.isMute = btnMicToggle.isChecked
				mMeetingUserViewModel?.muteUnmuteUser(userId, audioId, isMute)
			}
		}
	}

	override val isSpeakerEnabled: Boolean
		get() = isSpeakerButtonEnable

	// Button states and actions
	private fun setAudioProgressState(status: AudioStatus) {
		runOnUiThread {
			if (status === AudioStatus.CONNECTING) {
				resetMicLevelView()
				mMeetingUserViewModel?.isMuteBtnEnabled = false
				if (mMeetingUserViewModel?.audioConnType === AudioType.VOIP ||
						mMeetingUserViewModel?.audioConnType === AudioType.DIAL_OUT) {
					progressMicButton.visibility = View.VISIBLE
					updateMicLevelEnabled(true)
				} else {
					progressMicButton.visibility = View.GONE
				}
			} else if (status === AudioStatus.CONNECTED) {
				progressMicButton.visibility = View.GONE
				if (mMeetingUserViewModel?.audioConnType === AudioType.DIAL_IN) {
					mMeetingUserViewModel?.isMuteBtnEnabled = true
				} // else wait for dial_out_success to enable microphone
			} else if (status === AudioStatus.DISCONNECTED || status === AudioStatus.NOT_CONNECTED) {
				progressMicButton.visibility = View.GONE
				mMeetingUserViewModel?.isMuteBtnEnabled = false
				updateMicLevelEnabled(false)
				resetMicLevelView()
			}
		}
	}

	private fun setMicDrawables(type: AudioType) {
		//TODO: Get correct sizing of oval as we are exporting the image only
		runOnUiThread {
			if (type === AudioType.VOIP) {
				resetMicButtonStateOnMusicHold()
				btnMicToggle.setButtonDrawable(R.drawable.mic_btn_selector)
				if(mMeetingUserViewModel?.isMute == true) {
					progressMicButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.color_red,theme))
				}
				else{
					progressMicButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_color_500,theme))
				}
			} else if (type === AudioType.DIAL_OUT) {
				resetMicButtonStateOnMusicHold()
				btnMicToggle.setButtonDrawable(R.drawable.mic_btn_pstn_selector)
				if(mMeetingUserViewModel?.isMute == true) {
					progressMicButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.color_red,theme))
				}
				else{
					progressMicButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.accent_color_400,theme))
				}
			} else if (type === AudioType.NO_AUDIO || type === AudioType.DIAL_IN) {
				btnMicToggle.setButtonDrawable(R.drawable.speaker_btn_audio_not_connected_selector)
			}
		}
	}

	private fun resetMicButtonStateOnMusicHold() {
		if(isMusicOnHold) {
			this.isMusicOnHold=false
			updateMicButton(false)
		}
	}

	fun updateControlPanel(isMicEnabled: Boolean, isSpeakerEnabled: Boolean) {
		mMeetingUserViewModel?.isMute = isMicEnabled
		mMeetingUserViewModel?.isSpeakerOn = isSpeakerEnabled
	}

	private fun toggleCamera(checked: Boolean) {
		isCameraShared = checked
		btnCameraToggle.isChecked = checked
	}

	private fun toggleMic(checked: Boolean) {
			isChecked = checked
			btnMicToggle.isChecked = checked
			if (mMeetingUserViewModel?.audioConnType == AudioType.VOIP) {
				activateMic(checked)
			}
	}

	private fun toggleMicEnableState(enabled: Boolean) {
		updateMicButton(enabled, false)
	}

	var cameraFailedSnackbar: Snackbar? = null
	var timer: TimerTask? = null
	private fun startCameraEnableDisableTimer(isEnablingCamera: Boolean) {
		timer = Timer().schedule(10000){
			runOnUiThread {
				toggleCameraEnabledState(false)
				//Reset Camera button and camera shared status
				isCameraShared = !isCameraShared
				btnCameraToggle.isChecked = !btnCameraToggle.isChecked

				if (cameraFailedSnackbar == null) {
					cameraFailedSnackbar = if(isEnablingCamera)
						Snackbar.make(coordinatorLayout, R.string.failed_camera_start, Snackbar.LENGTH_LONG)
								.setActionTextColor(ContextCompat.getColor(this@WebMeetingActivity, R.color.white))
								.setAction(R.string.failed_camera_btn_retry) {
									btnCameraToggle.performClick()
								}
					else
						Snackbar.make(coordinatorLayout, R.string.failed_camera_stop, Snackbar.LENGTH_LONG)
								.setActionTextColor(ContextCompat.getColor(this@WebMeetingActivity, R.color.white))
								.setAction(R.string.failed_camera_btn_retry) {
									btnCameraToggle.performClick()
								}
				}
				cameraFailedSnackbar?.show()
			}
		}
    }

	private fun toggleCameraEnabledState(connecting: Boolean) {
		if(connecting) {
			progressCameraButton.visibility = View.VISIBLE
			btnCameraToggle.alpha = 0.3f
			btnCameraToggle.isClickable = false
		} else {
			timer?.cancel()
			progressCameraButton.visibility = View.GONE
			btnCameraToggle.alpha = 1.0f
			btnCameraToggle.isClickable = true
		}
	}

	private var mShownToast: Boolean = false
	private fun showWaitingToast() {
		val isPresenter = mMeetingEventViewModel?.isCurrentUserPresenter() ?: false
		val toastPermittedForUser = mIsMeetingHost || isPresenter
		val isWaitingRoomEnabled = mMeetingEventViewModel?.waitingRoomEnabledStatus?.value == true
		val showToast = isWaitingRoomEnabled && (rlMeetingView.visibility == View.VISIBLE || flChatFragmentPlaceHolder.visibility == View.VISIBLE)
		if (toastPermittedForUser && !mShownToast && showToast) {
			mShownToast = true
			showPeopleAreWaitingToast()
		}
	}

	private fun toggleWaitingRoomState(enabled: Boolean) {
		mIsWaitingRoomEnabled = enabled
	}

	fun updateMicButton(isMicEnabled: Boolean, shouldUpdateMicEnabledStatus: Boolean = true) {
		if (isMicEnabled) {
			btnMicToggle.isClickable = true
			btnMicToggle.isEnabled = mMeetingUserViewModel?.audioConnType != AudioType.DIAL_IN
			btnMicToggle.isChecked = mMeetingUserViewModel?.isMute ?: true
		} else {
			btnMicToggle.isClickable = false
			btnMicToggle.isEnabled = false
			btnMicToggle.isChecked = true
		}
		if (shouldUpdateMicEnabledStatus) mMeetingUserViewModel?.isMuteBtnEnabled = btnMicToggle.isEnabled
		if(isMusicOnHold) btnMicToggle.isEnabled = true
		if (mMeetingUserViewModel?.audioConnType == AudioType.NO_AUDIO || mMeetingUserViewModel?.audioConnType == AudioType.DIAL_IN) {
			btnMicToggle.isClickable = true
			btnMicToggle.isEnabled = true
		}
		resetMicLevelView()
	}

	fun updateAfterMusicOnHold(isMusicOnHold: Boolean) {
		try {
			if (mMeetingUserViewModel?.audioConnType !== AudioType.NO_AUDIO && !mIsMeetingHost) {
				if (isMusicOnHold) {
					btnMicToggle.isChecked = true
				}
				this.isMusicOnHold = isMusicOnHold
				btnMicToggle.isClickable = !isMusicOnHold
				btnMicToggle.isEnabled = !isMusicOnHold
				mMeetingUserViewModel?.isMuteBtnEnabled = btnMicToggle.isEnabled
				val isMuteActive = mMeetingUserViewModel?.isMuteActivated?: false
				val user = mMeetingEventViewModel?.getCurrentUser()
				if (user != null) {
					val audio = user.audio
					val audioId = audio.id
					val userId = mMeetingUserViewModel?.currentUserId
					if (userId != null && audioId != null) {
						mMeetingUserViewModel?.muteUnmuteUser(userId, audioId, isMuteActive)
						if (!isMusicOnHold) {
							btnMicToggle.isChecked = isMuteActive
						}
					}
				}
			}
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity updateAfterMusicOnHold() - Error getAudio ", ex, null,
					true, false)
		}
	}
	@OnClick(R.id.btn_close_shimmer)
	override fun handleCloseShimmerClick() {
		clearConferenceOnBackPress()
	}


//	@OnClick(R.id.btn_speaker)
//	fun onSpeakerButtonClick() {
//		mlogger.info(TAG, LogEvent.FEATURE_CHANGEAUDIO, LogEventValue.AUDIOSELECTION, "onSpeakerButtonClick")
//		mMeetingUserViewModel?.isSpeakerOn = btnSpeakerToggle.isChecked
//		mMeetingUserViewModel?.isSpeakerBtnEnabled = true

	@OnClick(R.id.btn_camera)
	fun onCameraButtonClick() {
		val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
		if (isGranted != PackageManager.PERMISSION_GRANTED) {  // we don't have the permission.  Ask for it
			ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), AppConstants.PERMISSIONS_REQUEST_CAMERA)
			btnCameraToggle.isChecked = false
			isCameraPermissonDialogOpen = true
		} else if (isFoxdenSessionComplete) {
			if (mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS.feature)!!) {
					if((mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)!! &&
							InternetConnection.isConnectedWifi(this)) || (mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)!! && mMeetingUserViewModel?.featureManager?.isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)!!)) {
				if (!isCameraShared) {
					progressCameraButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_color_500,theme))
					mixpanelCameraOnEvent(AppConstants.ENABLE_WEBCAM)
					isCameraShared = true
					btnCameraToggle.isChecked = true
					toggleCameraEnabledState(true)
				} else { // camera was sharing.  Stop the camera sharing session
					progressCameraButton.indeterminateTintList = ColorStateList.valueOf(resources.getColor(R.color.white,theme))
					isCameraShared = false
					btnCameraToggle.isChecked = false
					toggleCameraEnabledState(true)
					mixpanelCameraOnEvent(AppConstants.DISABLE_WEBCAM)
				}
				mMeetingUserViewModel?.isCameraOn = btnCameraToggle.isChecked
				startCameraEnableDisableTimer(true)
			} else {
				toggleCamera(false)
				showCameraDisableDialog()
			}
		}
		} else {
		    btnCameraToggle.isChecked = false
		    showFoxdenErrorSnackBar()
		}
	}

	private fun showFoxdenErrorSnackBar() {
		errorSnackbar = Snackbar.make(coordinatorLayout, resources.getString(R.string.foxden_error_msg), Snackbar.LENGTH_INDEFINITE)
		errorSnackbar?.let {snackBar ->
			snackBar.setAction(R.string.close, View.OnClickListener {
			})
			snackBar.setActionTextColor(resources.getColor(R.color.primary_color_500))
			val snackbarView = snackBar.view
			val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
			textView.maxLines = 4
			snackBar.show()
		}
	}

	private fun showCameraDisableDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setMessage(R.string.dialog_wifi_to_mobile_data_switch_message_for_webcam)
		builder.setCancelable(false)
		builder.setPositiveButton(R.string.dialog_dismiss) { dialog: DialogInterface, which: Int ->
			dialog.cancel()
		}
		builder.show()
	}

	@OnClick(R.id.btn_mute_meeting)
	fun onMuteButtonClick() {
		if (mMeetingUserViewModel?.audioConnType == AudioType.NO_AUDIO || mMeetingUserViewModel?.audioConnType == AudioType.DIAL_IN) {
			onChangeAudioClicked()
		} else {
			val user = mMeetingEventViewModel?.getCurrentUser()
			val userId = mMeetingUserViewModel?.currentUserId ?: user?.id
			if (userId != null && user != null) {
				val audio = user.audio
				val audioId = audio.id
				if (audioId != null) {
					mMeetingUserViewModel?.isMuteBtnEnabled = btnMicToggle.isEnabled
					mMeetingUserViewModel?.isMute = btnMicToggle.isChecked
					val isMuteActive = mMeetingUserViewModel?.isMute ?: false
					mMeetingUserViewModel?.muteUnmuteUser(userId, audioId, isMuteActive)
					if (isMuteActive) {
						MixpanelMuteAllGuestsEvent(Mixpanel.INDIVIDUAL)
					} else {
						MixpanelUnmuteAllGuestsEvent(Mixpanel.INDIVIDUAL)
					}
				}
			}
		}
	}

	fun updateChatUnreadCount(conversationId: String)
	{
		var chats= mMeetingEventViewModel?.chatsList
		var showRedBadge = false
		if (chats != null) {
			for ((chatIndex, chatValue) in chats.withIndex()) {
				if (chats[chatIndex].conversationId == conversationId) {
					chats[chatIndex].unReadChatCount = 0
				} else if(chats[chatIndex].unReadChatCount > 0) {
					showRedBadge = true
					break
				}
			}
			if(!showRedBadge) {
				var tab = tlWebMeeting.getTabAt(CHAT_FRAGMENT_INDEX)
				val tv_UnreadChatCount = tab?.customView?.findViewById(R.id.text) as TextView
				tv_UnreadChatCount.visibility = View.GONE
				//Reset unread chat count.
				mUnreadChatCount = 0
			}
		}
	}

	@OnClick(R.id.tv_exit_meeting)
	fun exitMeeting() {
		ibExitButton.isEnabled = false
		mlogger.info(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity exitMeeting() - Showing Meeting exit dialog", null, null,
				true, false)
		mAlertDialog = if (mIsMeetingHost) {
			createHostExitAlertDialog()
		} else {
			createGuestExitDialog()
		}
		mAlertDialog?.setCanceledOnTouchOutside(false)
		mAlertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
			override fun onCancel(dialog: DialogInterface) {
				ibExitButton.isEnabled = true
				dialog.dismiss()
			}
		})
		mAlertDialog?.show()
	}

	@OnClick(R.id.btn_overflow_options)
	fun onOverFlowMenuClicked() {
		val overflowMenuDialogFragment = OverflowMenuDialogFragment.newInstance()
		overflowMenuDialogFragment.context = this
		if (mIsMeetingHost) {
			overflowMenuDialogFragment.setIsHost(true)
			overflowMenuDialogFragment.setIsWaitngRoomEnabled(mIsWaitingRoomEnabled)
			overflowMenuDialogFragment.setIsRecording(mIsRecording)
			overflowMenuDialogFragment.setAreAllMuted(mAreAllMuted)
		} else {
			overflowMenuDialogFragment.setIsHost(false)
		}
		overflowMenuDialogFragment.setIsLowBandwidthOn(mTurnWebCamOff)
		overflowMenuDialogFragment.setIsPSTNAvailable(isPSTNAvailableForMeetingRoom(), isSoftPhoneConnected)
		overflowMenuDialogFragment.setIsSpeakerOn(mIsSpeakerOn)
		overflowMenuDialogFragment.show(supportFragmentManager,
				"overflow_menu_dialog_fragment")
	}

	// Show disconnect audio in overflow menu when PSTN not available based on dialin and dialout values
	private fun isPSTNAvailableForMeetingRoom(): Boolean {
		return mIsDialInEnabled && mMeetingUserViewModel?.isDialOutBlocked()?.let {  mMeetingUserViewModel?.isDialOutBlocked()} == false
	}
	// Button states End

	// Mic Level
	private fun updateMicLevelEnabled(isEnabled: Boolean) {
		var resId = R.drawable.volume_indicator_empty
		if (!isEnabled) {
			resId = R.drawable.volume_indicator_empty
		}
		for (i in 0..9) {
			mMicLevelView[i]?.setImageResource(resId)
		}
	}

	private fun startLocalMicSampling() {
		try {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
					PackageManager.PERMISSION_DENIED) {
				return
			}
			audioRecordMinSize = AudioRecord.getMinBufferSize(
					441000, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT)
			audioRecord = AudioRecord(
					MediaRecorder.AudioSource.MIC, 441000,
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
					audioRecordMinSize)
			try {
				audioRecord?.startRecording()
			} catch (ise: IllegalStateException) {
				mlogger.error(
						TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM,
						"WebMeetingActivity startLocalMicSampling() - Error sampling Mic",
						ise, null,
						true, false)
			}
			micLevelHandler = Handler(Handler.Callback { msg ->
				if (msg.what == SAMPLE_MIC) {
					val buffer = ShortArray(audioRecordMinSize)
					val rc = audioRecord?.read(buffer, 0, audioRecordMinSize) ?: 0
					if (rc > 0) {
						var max = 0
						for (s in buffer) {
							val v = Math.abs(s.toInt())
							if (v > max) {
								max = v
							}
						}
						val value: Int
						value = if (max > 6800) 10 else if (max > 5400) 9 else if (max > 4800) 8 else if (max > 3600) 7 else if (max > 2600) 6 else if (max > 1800) 5 else if (max > 1200) 4 else if (max > 800) 3 else if (max > 400) 2 else if (max > 100) 1 else 0
						//TODO : Get assest so we will not need to tint
						if (value != mLastMicSignalLevel) {
							val green_line = R.drawable.do_green_line
							if (BuildConfig.CONFIG_TYPE == "lumen") {
								context?.let {
									resources.getDrawable(R.drawable.do_green_line).setColorFilter(
											ContextCompat
													.getColor(it, R.color.accent_color_400), PorterDuff.Mode.MULTIPLY)
								}
							}
							updateMicLevelView(value, green_line)
							mLastMicSignalLevel = value
						}
					}
					this.micLevelHandler?.sendEmptyMessageDelayed(SAMPLE_MIC, 1000);
				}
				true
			})
			micLevelHandler?.sendEmptyMessageDelayed(SAMPLE_MIC, 1000)
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity startLocalMicSampling() - Error sampling Mic", ex, null,
					true, false)
		}
	}

	private fun stopLocalMicSampling() {
		try {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
					PackageManager.PERMISSION_GRANTED) {
				audioRecord?.stop()
				audioRecord = null
			}
		} catch (ise: IllegalStateException) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity stopLocalMicSampling() - Error stopping Mic " +
					"sampling", ise, null, true, false)
		}
		micLevelHandler?.removeMessages(SAMPLE_MIC)
	}

	override fun updateSignalLevel(data: String?) {
		if (mMeetingUserViewModel?.audioConnType == AudioType.VOIP) {
			if (data != null && !btnMicToggle.isChecked) {
				var micSigLevelInt = data.toInt()
				if (micSigLevelInt < 4) {
					micSigLevelInt -= 1
				} else {
					micSigLevelInt /= 3
				}
				if (micSigLevelInt != mLastMicSignalLevel) {
					val blueCircle = R.drawable.volume_indicator_blue
					//TODO : Get assest so we will not need to tint
					if (BuildConfig.CONFIG_TYPE == "lumen") {
						resources.getDrawable(R.drawable.volume_indicator_blue).setColorFilter(ContextCompat.getColor(this, R.color.primary_color_500), PorterDuff.Mode.MULTIPLY)
					}
					updateMicLevelView(micSigLevelInt, blueCircle)
					mLastMicSignalLevel = micSigLevelInt
				}
			}
		}
	}

	private fun updateMicLevelView(micSigLevel: Int, active_line_color_resId: Int) {
		val grayCircle = R.drawable.volume_indicator_empty
		for (i in 0..9) {
			if (i + 1 <= micSigLevel) {
				mMicLevelView[i]?.setImageResource(active_line_color_resId)
			} else {
				mMicLevelView[i]?.setImageResource(grayCircle)
			}
		}
	}

	private fun resetMicLevelView() {
		val grayCircle = R.drawable.volume_indicator_empty
		for (i in 0..9) {
			mMicLevelView[i]?.setImageResource(grayCircle)
		}
	}

	//End Mic Level
	// Network states
	override fun onInternetDisconnected() {
		mIsInternetConnectionChange = true
		super.onInternetDisconnected()
		mMeetingUserViewModel?.internetConnected = false
		mMeetingEventViewModel?.pausePolling()
		isMicButtonEnable = btnMicToggle.isEnabled
		updateMicLevelEnabled(false)
		stopLocalMicSampling()
		btnMicToggle.isEnabled = false
		btnCallSetting.isEnabled = false
		ibExitButton.isEnabled = false
		updateInternetAvailabilityForCurrentFragment()
		if (mMeetingUserViewModel?.audioConnType === AudioType.VOIP
				&& mMeetingUserViewModel?.audioConnState === AudioStatus.CONNECTED) {
			reconnectVoip = true
		}
		val fragment = mAdapter?.getItem(SCREEN_SHARE_FRAGMENT_INDEX)
		if(fragment is ContentPresentationFragment) {
			fragment.updateContentID()
		}
		stopBandWidthTest()
	}

	override fun onInternetConnected() {
		super.onInternetConnected()
		if (internetConnectionTimeoutDialogVisible) {
			timeoutAlertDialog?.cancel()
		}
		if (mIsWaitingRoomEnabled) {
			mMeetingEventViewModel?.getCurrentUser()?.let { admitParticipantRequest(it) }
			mMeetingUserViewModel?.userInMeeting = true
			mMeetingUserViewModel?.userIsInWaitingRoom = false
			showAudioSelectionView()

        }
        if (mMeetingUserViewModel?.userInMeeting == true) {
			mMeetingEventViewModel?.restartPolling(null)
			startBandWidthTest()
		}
		when (mMeetingUserViewModel?.audioConnType) {
			AudioType.VOIP -> {
				reconnectVoip = true
				createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_NETWORK_CHANGE.name)
			}
			AudioType.DIAL_OUT -> {
			}
			else -> {
				if (!isAudioSelectionFragmentVisible && mIsInternetConnectionChange &&
						mMeetingUserViewModel?.audioConnState == AudioStatus.CONNECTED) {
					showAudioSelectionPage()
				}
				updateMicLevelEnabled(false)
			}
		}
		btnCallSetting.isEnabled = true
		ibExitButton.isEnabled = true
		if (mIsInternetConnectionChange) {
			btnMicToggle.isEnabled = mMeetingUserViewModel?.isMuteBtnEnabled ?: false
		}
		if (mMeetingUserViewModel?.audioConnState == AudioStatus.DISCONNECTED ||
				mMeetingUserViewModel?.audioConnState == AudioStatus.NOT_CONNECTED) {
			updateControlPanel(false, false)
		}
		mMeetingUserViewModel?.internetConnected = true
		updateInternetAvailabilityForCurrentFragment()
	}

	private fun updateInternetAvailabilityForCurrentFragment() {
		val fragment = mAdapter?.getItem(PARTICIPANT_LIST_FRAGMENT_INDEX)
		if (fragment is ParticipantListFragment) {
			fragment.onUpdateInternetAvailability(InternetConnection.isConnected(this))
		}

		if(isMeetingSecurityFragmentVisible){
			val fragment = supportFragmentManager.findFragmentByTag(
					MeetingSecurityFragment::class.java
							.simpleName)
			val meetingSecurityFragment = fragment as? MeetingSecurityFragment
			meetingSecurityFragment?.onUpdateInternetAvailability(InternetConnection.isConnected(this))
		}
	}

	override fun showNoInternetConnectionBanner() {
		super.showNoInternetConnectionBanner()
		mInternetReconnectingBannerLayout.visibility = View.VISIBLE
	}

	override fun hideNoInternetConnectionBanner() {
		super.hideNoInternetConnectionBanner()
		if (mInternetReconnectingBannerLayout.visibility == View.VISIBLE) {
			mInternetReconnectingBannerLayout.visibility = View.GONE
		}
	}

	private var internetConnectionTimeoutDialogVisible = false
	override fun showInternetConnectionTimeoutDialog() {
		if (internetConnectionTimeoutDialogVisible) {
			return
		}
		val builder = AlertDialog.Builder(this)
		builder.setCancelable(false)
		builder.setMessage(R.string.dialog_internet_connection_time_out)
		builder.setPositiveButton(R.string.dialog_ok) { dialog: DialogInterface, which: Int ->
			//SSMOBILE-4225: The cancel should be called before onExitActivity as on ExitActiviy in one sceanrio we do finish activity.
			dialog.cancel()
			onExitActivity()
			internetConnectionTimeoutDialogVisible = false
		}
		if (!isDestroyed) {
			internetConnectionTimeoutDialogVisible = true
			timeoutAlertDialog = builder.show()
		}
	}

	override fun showNetworkSwitchDialog() {
		super.showNetworkSwitchDialog()
		if (hasNetworkSwitchDialogAlreadyShown) {
			return
		}
		if (isAudioSelectionProgressFragmentVisible || isAudioSelectionFragmentVisible || isFeedbackFragmentVisible)
			return
		if (SharedPreferencesManager.getInstance().dontShowNetworkSwitchDialog) {
			return
		}
		hasNetworkSwitchDialogAlreadyShown = true
		val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
		builder.setMessage(R.string.dialog_wifi_to_mobile_data_switch_message)
		builder.setCancelable(false)
		builder.setNegativeButton(R.string.dialog_dismiss, null)
		builder.setPositiveButton(R.string.dialog_dont_show_again) { dialog: DialogInterface, which: Int ->
			dialog.cancel()
			SharedPreferencesManager.getInstance().dontShowNetworkSwitchDialog = true
		}
		if (!isDestroyed) {
			builder.show()
		}
	}

	override fun setConnectionChangeValues() {
		setConnectionValues()
		MixpanelNetworkChangeEvent();
	}

	override fun showBadNetworkToast() {
		mlogger.info(
				TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.RAG_ALARM_RED, "WebMeetingActivity " +
				"showBadNetworkToast() - Showing bad Network SnackBar", null, null, true, false)
		mlogger.record(LogEvent.METRIC_RAG_ALARAM_RED)
		ragAlarmSnackbar = SimpleCustomSnackbar.make(coordinatorLayout, getString(R.string
				.bad_network_conditions_message), Snackbar.LENGTH_LONG, badNetworkListener, getString(R.string.bad_network_conditions_title), getColor(R.color.logout_dialog_background))
		if (ragAlarmSnackbar?.isShownOrQueued == false) {
			ragAlarmSnackbar?.show()
		}
	}
	var badNetworkListener: View.OnClickListener = View.OnClickListener {
		runOnUiThread {
			ragAlarmSnackbar?.dismiss()
			onChangeAudioClicked()
		}
	}

	override fun showBadNetworkLowBandwidthToast() {
		mlogger.info(
				TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.RAG_ALARM_RED, "WebMeetingActivity " +
				"showBadNetworkLowBandwidthToast() - Showing bad Network Low Bandwidth SnackBar", null, null, true, false)
		mlogger.record(LogEvent.METRIC_RAG_ALARAM_RED)
		lowBandwidthSnackbar = SimpleCustomSnackbar.make(coordinatorLayout, getString(R.string
				.bad_network_low_bandwidth_message), Snackbar.LENGTH_LONG, lowBandwidthListener, getString(R.string.bad_network_low_bandwidth_title), getColor(R.color.logout_dialog_background))
		if (lowBandwidthSnackbar?.isShownOrQueued == false) {
			lowBandwidthSnackbar?.show()
		}
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value, null, null, false, true)
	}
	var lowBandwidthListener: View.OnClickListener = View.OnClickListener {
		runOnUiThread {
			lowBandwidthSnackbar?.dismiss()
			onLowBandwidthClicked()
		}
	}

	fun showMeteredNetworkToast() {
		if ((audioSnackBar?.isShownOrQueued == false) && (ragAlarmSnackbar?.isShownOrQueued == false)) {
			networkSnackbar = Snackbar.make(coordinatorLayout, R.string.metered_network_conditions_message, Snackbar.LENGTH_INDEFINITE)
			mlogger.info(TAG, LogEvent.NETWORK_QUALITY, LogEventValue.NETWORK_CHANGED, "WebMeetingActivity - showBadNetworkToast() - Showing bad Network SnackBar", null, null, true, false)
			networkSnackbar?.setAction(R.string.dialog_dismiss) { v: View? -> networkSnackbar?.dismiss() }
			networkSnackbar?.setActionTextColor(ContextCompat.getColor(this, R.color.primary_color_500))
			if (networkSnackbar?.isShownOrQueued == false) {
				networkSnackbar?.show()
			}
		}
	}

	fun hideMeteredNetworkToast() {
		if (networkSnackbar?.isShown == true) {
			networkSnackbar?.dismiss()
		}
	}

	fun showNoAudioToast() {
		if (mMeetingUserViewModel?.audioConnType == AudioType.NO_AUDIO) {
			mlogger.error(TAG, LogEvent.ERROR, LogEventValue.AUDIOSELECTION, "Showing No Audio Snackbar")
			audioSnackBar = Snackbar.make(coordinatorLayout, R.string.no_audio, Snackbar.LENGTH_LONG)
			audioSnackBar?.setAction(R.string.connect_audio_title) { v: View? -> onChangeAudioClicked() }
			audioSnackBar?.setActionTextColor(ContextCompat.getColor(this, R.color.primary_color_500))
			if (audioSnackBar?.isShownOrQueued == false) {
				audioSnackBar?.show()
			}
		}
	}

	fun showPeopleAreWaitingToast() {
		mlogger.error(TAG, LogEvent.WAITING_ROOM_SNACK_BAR, LogEventValue.WAITING_SNACK_BAR, "Showing People Are Waiting Snackbar")
		waitingRoomSnackBar = SimpleCustomSnackbar.make(coordinatorLayout, getString(R.string
				                                                             .people_are_waiting_message), Snackbar.LENGTH_LONG, listener, getString(R.string.manage_waiting_room), getColor(R.color.logout_dialog_background))
		if (waitingRoomSnackBar?.isShownOrQueued == false) {
			waitingRoomSnackBar?.show()
		}
	}

	var listener: View.OnClickListener = View.OnClickListener {
		runOnUiThread {
			waitingRoomSnackBar?.dismiss()
			onSecurityMeetingOptionClicked()
		}
	}

	//disconnect voip call
	fun onDisconnectAudioClicked() {
		if(InternetConnection.isConnected(context)) {
			hangUpSoftPhone()
			releaseAudioFocus()
			isSoftPhoneConnected = false
		}
		else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
		}
	}

	// Network state end
	fun onChangeAudioClicked() {
		if (audioSnackBar?.isShownOrQueued == true) {
			audioSnackBar?.dismiss()
		}
		mIsChangeAudioFlow = true
		rlMeetingView.visibility = View.GONE
		rlTopBarContainer.visibility = View.GONE
		hideLoadingScreen()
		mlogger.record(LogEvent.FEATURE_CHANGEAUDIO)
		flFragmentPlaceHolder.visibility = View.VISIBLE
		showAudioSelectionView()
	}

	fun onLowBandwidthClicked() {
		mTurnWebCamOff = !mTurnWebCamOff
		mMeetingUserViewModel?.turnWebcamOff = mTurnWebCamOff
	}

	fun onShowChatDetailPage(chat: Chat) {
		if (isPrivateChatEnable) {
			this.mChat = chat
			val participantIDs = arrayOf(chat.webPartId.toString(), mMeetingEventViewModel?.getCurrentUser()?.id.toString())
			mMeetingUserViewModel?.addConversation(participantIDs)
			mMeetingEventViewModel?.chatCount = mMeetingEventViewModel?.chatsList?.size.let { it }
		} else {
			showDisablePrivateChatToast(isPrivateChatEnable)
		}
	}

	private fun removeNewPrivateChatSelectionFragment() {
		val fragment = supportFragmentManager.findFragmentById(R.id.frameLytSelectChat)
		if (fragment is NewPrivateChatSelectionFragment) {
			fragment.removeSelectPrivateChatFragment()
		}
	}

	private fun openChatFragment() {
		if (mMeetingUserViewModel?.isOpenChatFragment?.value == true) {
			removeNewPrivateChatSelectionFragment()
			mChat?.let {
				it.conversationId = mConversationID
				onShowChatFragment(it)
			}
		}
	}

	private fun showChatError() {
		val fragment = supportFragmentManager.findFragmentById(R.id.fl_chatfragment_place_holder)
		if (fragment is ChatFragment) {
			fragment.showChatError()
		}
	}

	fun onShowChatFragment(chat: Chat) {
		val chatFragment = ChatFragment.newInstance()
		chatFragment.setChatObject(chat)
		replaceFragments(chatFragment, chatFragment.javaClass.simpleName)
		mMeetingUserViewModel?.isOpenChatFragment?.postValue(false)
		rlMeetingView.visibility = View.GONE
		rlTopBarContainer.visibility = View.GONE
		flChatFragmentPlaceHolder.visibility = View.VISIBLE
	}

	fun onSecurityMeetingOptionClicked() {
		val meetingSecurityFragment = MeetingSecurityFragment.newInstance()
		replaceFragments(meetingSecurityFragment)
		rlMeetingView.visibility = View.GONE
		rlTopBarContainer.visibility = View.GONE
		flFragmentPlaceHolder.visibility = View.VISIBLE
	}

	private var mIsWaitingRoomEnabled = false
	fun onTurnOffWaitingClicked() {
		mIsWaitingRoomEnabled = !mIsWaitingRoomEnabled
		mMeetingUserViewModel?.toggleWaitingRoom(false)
		if (mIsWaitingRoomEnabled) {
			MixpanelEnableWaitingRoomEvent()
		} else {
			MixpanelDisableWaitingRoomEvent()
		}
		if (waitingRoomSnackBar?.isShownOrQueued == true) {
			waitingRoomSnackBar?.dismiss()
		}
	}

	private var mAreAllMuted = false
	fun onMuteAllClicked() {
		mAreAllMuted = !mAreAllMuted
		mMeetingUserViewModel?.muteUnmuteMeeting(mAreAllMuted)
		if (mAreAllMuted) {
			MixpanelMuteAllGuestsEvent(Mixpanel.ALL)
		} else {
			MixpanelUnmuteAllGuestsEvent(Mixpanel.ALL)
		}
	}

	private var mIsSpeakerOn = false
	fun onSpeakerClicked() {
		mIsSpeakerOn = !mIsSpeakerOn
		activateSpeakerPhone(mIsSpeakerOn)
		mMeetingUserViewModel?.isSpeakerOn = mIsSpeakerOn
	}

	private var mIsRecording = false
	private var mRecordingSnackbarVisible = false
	var recordingSnackbar: Snackbar? = null
	fun onStartRecordingClicked() {
		mIsRecording = true
		val recordingName = "My Meeting"
		val locale = CommonUtils.getLocale(this)
		mMeetingUserViewModel?.startRecording(recordingName, locale)
		if (recordingSnackbar == null) {
			recordingSnackbar = Snackbar.make(coordinatorLayout, R.string.preparingToRecordToast, Snackbar.LENGTH_INDEFINITE)
		}
		recordingSnackbar?.show()
		mRecordingSnackbarVisible = true
		MixpanelRecordEvent()
	}

	fun onStopRecordingClicked() {
		mMeetingUserViewModel?.stopRecording()
		val snackbar = Snackbar.make(coordinatorLayout, R.string.recordingStopped, Snackbar.LENGTH_LONG)
		snackbar.show()
	}

	// Init Voip Call Methods
	override fun startSoftphone() {
		super.initSoftphoneCallBacks()
	}

	override fun startVoipMeeting() {
		mlogger.info(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM,
				"WebMeetingActivity startVoipMeeting() - Connecting to Voip", null, null,
				true, false)
		reconnectVoip = false
		stopInProgressAudio(AudioType.VOIP)
		resumeMeetingRoom()
		var sipurl: String? = null
		var passCode: String? = null
		val meetingRoomInfoResponse = mMeetingUserViewModel?.getRoomInfoResponse()
		if (meetingRoomInfoResponse != null) {
			val meetingRoomAudio = meetingRoomInfoResponse.audio
			if (meetingRoomAudio != null) {
				sipurl = meetingRoomAudio.sipURI
				if (mMeetingUserViewModel?.userIsInWaitingRoom != true
						&& (mMeetingUserViewModel?.authResponse?.roomRole?.toLowerCase() ==
								AppConstants.GUEST.toLowerCase()) && (mMeetingUserViewModel?.audioConnState != AudioStatus.CONNECTED)){
					sipurl += AppConstants.BYPASS_VOIP
				}
				passCode = if (mIsMeetingHost) meetingRoomAudio.hostPasscode else meetingRoomAudio.guestPasscode
			}
		}
		if (sipurl == null) {
			return
		}
		mFormattedSipUri = CommonUtils.createSipUri(sipurl, passCode, this)
		mMeetingUserViewModel?.audioConnType = AudioType.VOIP
		mMeetingUserViewModel?.audioConnState = AudioStatus.CONNECTING
		mFormattedSipUri?.let {
			connectSoftPhone(it, enableDolby = true)
		}
		requestAudioFocus()
		mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_VOIP.value
	}

	override fun softPhoneConnected() {
		mlogger.info(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM,
				"WebMeetingActivity softPhoneConnected()", null, null, true, false)
		mlogger.endMetric(LogEvent.METRIC_VOIP_CONNECT, "Voip Connection times in seconds")
		softPhoneReconnectCount = 0
		isSoftPhoneConnectSuccess = true
		setAudioProgressState(AudioStatus.CONNECTED)
		mMeetingUserViewModel?.let {
			activateSpeakerPhone(it.isSpeakerOn)
		}
		updateMicButton(true)
		if (audioSnackBar?.isShownOrQueued == true) {
			audioSnackBar?.dismiss()
		}
		if (voipFailedDialog?.isShowing == true) {
			voipFailedDialog?.dismiss()
			voipFailedDialog = null
		}
	}

	override fun softPhoneDisconnected() {
		mlogger.info(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
				"softPhoneDisconnected()", null, null, true, false)
		if (!isSoftPhoneConnectSuccess) {
			val builder = AlertDialog.Builder(this)
			builder.setMessage(R.string.voip_connect_failed_try_something_else)
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_ok) { dialog, id ->
						dialog.dismiss()
						voipFailedDialog = null
					}
			voipFailedDialog = builder.create()
			voipFailedDialog?.show()
		}
		createSoftPhoneIntent(PGiSoftPhoneConstants.STOP_SERVICE.name)
		isSoftPhoneConnectSuccess = false
		if (exitActivity) {
			openFeedbackFragment(true)
			return
		}
		if (mMeetingUserViewModel?.userInMeeting == true) {
			onGoingMeetingIntent(NotificationConstants.MEETING_ONGOING.name, mConferenceId)
		}
		if (!pendingVoipDisconnect) {
			mMeetingUserViewModel?.audioConnType = AudioType.NO_AUDIO
			mMeetingUserViewModel?.audioConnState = AudioStatus.DISCONNECTED
			mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_NONE.value
		}
		mMeetingEventViewModel?.updateVoipDisconnectStatus()
		var showToast = true
		if (reconnectVoip) {
			mlogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.RECONNECTING, "WebMeetingActivity Softphone reconnecting", null, null, true, false)
			if (softPhoneReconnectCount < 1) {
				showToast = false
				Handler().postDelayed({
					                      reInitSoftPhone()
					                      startVoipMeeting()
					                      softPhoneReconnectCount++
				                      }, 3000) // We are adding this delay as destroy and init are happening immedialtely and see a freeze
			}
		}
		if (showToast) {
			mlogger.warn(TAG, LogEvent.ERROR, LogEventValue.AUDIOSELECTION, "SoftPhoneDisconnected: Show No Audio Toast")
			showNoAudioToast()
		}
		pendingVoipDisconnect = false
	}

	override fun softPhoneReconnected() {
		updateMicButton(true)
	}

	// End Voip Calls
	//Start Dial in methods
	override fun handleDialInAction() {
		val dialInSelectionFragment = DialInSelectionFragment(mIsMeetingHost, this, true)
		replaceFragments(dialInSelectionFragment,"dialInSelectionFragment")
	}

	override fun handleDialInClick(phoneNumber: String) {
		val msg = resources.getString(R.string.dial_in_not_enabled)
		if (mMeetingUserViewModel == null) {
			notifyUser(msg, Snackbar.LENGTH_LONG)
			mlogger.error(
					TAG, LogEvent.ERROR, LogEventValue.DIALINSCREEN, "MeetingUserViewModel is null", null,
					null, false, false)
			return
		}
		val meetingRoomInfoResponse = mMeetingUserViewModel?.getRoomInfoResponse()
		if (meetingRoomInfoResponse == null) {
			notifyUser(msg, Snackbar.LENGTH_LONG)
			mlogger.error(
					TAG, LogEvent.ERROR, LogEventValue.DIALINSCREEN, "MeetingRoomInfoResponse is null", null,
					null, false, false)
			return
		}
		val meetingRoomAudio = meetingRoomInfoResponse.audio
		if (meetingRoomAudio == null) {
			notifyUser(msg, Snackbar.LENGTH_LONG)
			mlogger.error(
					TAG, LogEvent.ERROR, LogEventValue.DIALINSCREEN, "MeetingRoomAudio is null", null,
					null, false, false)
			return
		}
		var passcode: String? = null
		passcode = if (mIsMeetingHost) {
			meetingRoomAudio.hostPasscode
		} else {
			meetingRoomAudio.guestPasscode
		}
		stopInProgressAudio(AudioType.DIAL_IN)
		resumeMeetingRoom()
		val formattedPhoneNumber = CommonUtils.getPGIFormattedNumber(phoneNumber, passcode)
		mMeetingUserViewModel?.audioConnType = AudioType.DIAL_IN
		mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DIAL_IN.value
		dialPhoneNumber(formattedPhoneNumber)
		setMixPanelJoinAudio()
	}

	private fun notifyUser(message: String, duration: Int) {
		Snackbar.make(coordinatorLayout,message,duration).show()
	}

	override fun showDialInFragment() {
		val dialInFragment = DialInFragment(mIsMeetingHost, this, true,mFirstName?.let { it.toString() }.toString(),"")
		replaceFragments(dialInFragment)
	}

	override fun showDialInSelectionFragment() {
		val dialInSelectionFragment = DialInSelectionFragment(mIsMeetingHost, this, true)
		replaceFragments(dialInSelectionFragment,"dialInSelectionFragment")
	}

	private fun dialPhoneNumber(formattedPhoneStr: String) {
		try {
			val utfFormattedPhoneStr = URLEncoder.encode(formattedPhoneStr, AppConstants.UTF_8)
			runOnUiThread {
				try {
					val dialinIntent = Intent(Intent.ACTION_CALL)
					dialinIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					dialinIntent.data = Uri.parse(AppConstants.TEL_SYMBOL + utfFormattedPhoneStr)
					startActivity(dialinIntent)
					mlogger.record(LogEvent.FEATURE_DIALIN)
					mlogger.startMetric(LogEvent.METRIC_DIALIN_CONNECT)
					mlogger.info(
							TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM,
							"WebMeetingActivity dialPhoneNumber() - Dialing In", null, null,
							true, false)
					notifyUser(AppConstants.CALLING_TEXT + formattedPhoneStr, Snackbar.LENGTH_LONG)
				} catch (e: SecurityException) {
					mlogger.error(
							TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM,
							"WebMeetingActivity dialPhoneNumber() - Failed Dialing In", e, null, true, false)
				}
			}
		} catch (e: SecurityException) {
			showDialInErrorSnackBar()
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
					"dialPhoneNumber() - SecurityException  Dialing In", e,
					null, true, false)
		} catch (e: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity " +
					"dialPhoneNumber() - Exception Dialing In", e,
					null, true, false)
		}
	}

	fun showDialInErrorSnackBar() {
		val layout = findViewById<View>(R.id.ll_home)
		Snackbar.make(
				layout, R.string.permission_call_rationale,
				Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.str_ok) { view: View? -> PermissionUtil.showAppNativeSettings(WeakReference(context)) }.show()
	}

	//End Dial in
	// Start Dial out methods
	override fun handleCallMyPhoneClick() {
		val audioSelectionProgressFragment = AudioSelectionProgressFragment()
		replaceFragments(audioSelectionProgressFragment)
		val phones : List<Phone> = mMeetingUserViewModel?.getDialOutNumbers() ?: emptyList()
	    if(phones.isNotEmpty()) {
			val selectedPhoneNumber: Phone? = phones[0]
			val dialOutFragment = DialOutFragment.newInstance()
			mFirstName?.let { dialOutFragment.initDialoutFragment(this, mIsMeetingHost, it, phones, selectedPhoneNumber, this, true) }
			replaceFragments(dialOutFragment)
		}
		else
		{
			val dialOutSelectionFragment = DialOutSelectionFragment.newInstance()
			dialOutSelectionFragment.initDialoutFragment(this, mIsMeetingHost, phones, this, true)
			replaceFragments(dialOutSelectionFragment)
		}
	}

	override fun handleCallMeClick(selectedPhoneNumber: Phone?) {
		stopInProgressAudio(AudioType.DIAL_OUT)
		resumeMeetingRoom()
		if (mIsChangeAudioFlow) {
			mlogger.info(
					TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM, "WebMeetingActivity handleCallMeClick() - Changing Audio Connection", null,
					null, true, false)
		}
		dialOutPhoneNumber = selectedPhoneNumber
		mMeetingUserViewModel?.audioConnType = AudioType.DIAL_OUT
		mMeetingUserViewModel?.audioConnState = AudioStatus.CONNECTING
		mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DIAL_OUT.value
		startLocalMicSampling()
		mlogger.record(LogEvent.FEATURE_DIALOUT)
		mlogger.startMetric(LogEvent.METRIC_DIALOUT_CONNECT)
		mlogger.info(
				TAG, LogEvent.APP_VIEW, LogEventValue.GM5MEETINGROOM,
				"WebMeetingActivity handleCallMeClick() - Dialing Out", null, null,
				true, false)
		mMeetingUserViewModel?.dialOut(
				selectedPhoneNumber?.countryCode!!, selectedPhoneNumber
				.number, selectedPhoneNumber.extension, CommonUtils.getLocale(this))
	}

	fun stopDialOut() {
		mMeetingUserViewModel?.cancelDialOut()
		val user = mMeetingEventViewModel?.getCurrentUser()
		if (user != null) {
			val audio = user.audio
			val audioUserId = audio.id
			if (audioUserId != null && !audioUserId.isEmpty()) {
				mMeetingUserViewModel?.dismissAudioUser(audioUserId)
			}
		}
		stopLocalMicSampling()
	}

	//End Dial out methods
	// No Audio
	override fun handleDoNotConnectAudio() {
		stopInProgressAudio(AudioType.NO_AUDIO)
		mlogger.attendeeModel.audioConnectionType = Mixpanel.AUDIO_DO_NOT_CONNECT.value
		mlogger.attendeeModel.audiocodec = "NA"
		resumeMeetingRoom()
		setMixPanelJoinAudio()
	}

	override fun handleDialIn(mobile: String) {
		mFirstName?.let { name->
			val dialInFragment = DialInFragment(mIsMeetingHost, this, true, name, mobile)
			replaceFragments(dialInFragment, "dialInFragment")
		}
	}

	override fun handledialinsearch() {
		val serchdialInFragment = DialInSearchFragment(mIsMeetingHost, this, true)
		replaceFragments(serchdialInFragment,"serchdialInFragment")
	}

	// End No Audio
	// Content actions
	private fun handleOrientationChange() {
		val transition: TransitionSet = TransitionSet()
				.addTransition(Fade())
		if (mMeetingUserViewModel?.screenShareFullScreen == true) {
			vpWebMeeting.isPagingEnabled = false
			transition.interpolator = LinearOutSlowInInterpolator()
			TransitionManager.beginDelayedTransition(rlMeetingView, transition)
			mMeetingControlsView.visibility = View.GONE
			tlWebMeeting.visibility = View.GONE
			rlTopBarContainer.visibility = View.GONE
		} else {
			transition.interpolator = FastOutLinearInInterpolator()
			transition.duration = 600
			TransitionManager.beginDelayedTransition(rlMeetingView, transition)
			vpWebMeeting.isPagingEnabled = true
			mMeetingControlsView.visibility = View.VISIBLE
			tlWebMeeting.visibility = View.VISIBLE
			rlTopBarContainer.visibility = View.VISIBLE
		}
	}

	// Content actions end
	// Chat actions
	private fun onChatTabSelected() {
		// making chat unread count invisble when chat tab is  selected
		var tab = tlWebMeeting.getTabAt(CHAT_FRAGMENT_INDEX)
		val tv_UnreadChatCount = tab?.customView?.findViewById(R.id.text) as TextView
		tv_UnreadChatCount.visibility = View.GONE
		//Reset unread chat count.
		mUnreadChatCount = 0
	}

	fun updateChatTabNotificationCount(chatReceived: Chat) {
		if (vpWebMeeting.currentItem != CHAT_FRAGMENT_INDEX && ((vpWebMeeting.currentItem == PARTICIPANT_LIST_FRAGMENT_INDEX && chatReceived.isSelf == false) || vpWebMeeting.currentItem == SCREEN_SHARE_FRAGMENT_INDEX)) {
			//Add chat newChatCount
			val countView = mChatTabView?.findViewById<TextView>(R.id.tv_count)
			//  setting text for Unread chat count
			var tab = tlWebMeeting.getTabAt(CHAT_FRAGMENT_INDEX)
			val tv_UnreadChatCount = tab?.customView?.findViewById(R.id.text) as TextView
			if (mMeetingUserViewModel?.isPrivateChatLocked == false) {
				mUnreadChatCount++
				tv_UnreadChatCount.height = getResources().getDimensionPixelSize(R.dimen.dp_8);
				tv_UnreadChatCount.width = getResources().getDimensionPixelSize(R.dimen.dp_8);
				tv_UnreadChatCount.visibility = View.VISIBLE
			} else if (mMeetingUserViewModel?.isPrivateChatLocked == true && chatReceived.conversationId == AppConstants.CHAT_EVERYONE) {
				mUnreadChatCount++
				tv_UnreadChatCount.width = getResources().getDimensionPixelSize(R.dimen.margin_20_dp);
				val count = if (mUnreadChatCount > AppConstants.CHAT_COUNT_99) AppConstants.CHAT_COUNT_99_PLUS else mUnreadChatCount.toString()
				countView?.text = count
				// setting chat unread count to text ui
				tv_UnreadChatCount.text = count
				tv_UnreadChatCount.visibility = View.VISIBLE
			}
		}
	}

	// Chat actions end
	// Recording states
	private fun handleRecording(status: MeetingRecordStatus) {
		if (status === MeetingRecordStatus.RECORDING_STARTED) {
			if (recordingSnackbar != null && mRecordingSnackbarVisible) {
				recordingSnackbar?.dismiss()
				mRecordingSnackbarVisible = false
			}
			mIsRecording = true
			startRecordingIndicator()
		} else if (status === MeetingRecordStatus.RECORDING_STOPPED) {
			mIsRecording = false
			stopRecordingIndicator()
		} else if (mIsMeetingHost) {
			if (status === MeetingRecordStatus.RECORDING_START_FAILED) {
				val dialogBuilder = AlertDialog.Builder(this)
				dialogBuilder.setMessage(getString(R.string.recording_failed))
						.setCancelable(false)
						.setPositiveButton(R.string.try_again) { dialogInterface, i ->
							onStartRecordingClicked()
							dialogInterface.dismiss()
						}
						.setNegativeButton(R.string.cancel) { dialogInterface, i -> dialogInterface.cancel() }
				val alertDialog = dialogBuilder.create()
				alertDialog.show()
			} else if (status === MeetingRecordStatus.RECORDING_STOP_FAILED) {
				mMeetingUserViewModel?.stopRecording()
			}
		}
	}

	private fun handleAllGuestMuteUnmute(status: MeetingMuteStatus) {
		if (status === MeetingMuteStatus.MUTED) {
			mAreAllMuted = true
		} else if (status === MeetingMuteStatus.UN_MUTED) {
			mAreAllMuted = false
		}
	}

	override fun startRecordingIndicator() {
		recordingFlashAnimation()
	}

	override fun stopRecordingIndicator() {
		ivRecordingIndicator.visibility = View.INVISIBLE
	}

	fun recordingFlashAnimation() {
		try {
			ivRecordingIndicator.visibility = View.VISIBLE
			if (mMeetingUserViewModel?.contentPresentationActive == false) {
				val anim: Animation = AlphaAnimation(AppConstants.ALPHA_RECORDING_0, AppConstants.ALPHA_RECORDING_1)
				anim.duration = AppConstants.RECORDING_FLASH_DURATION.toLong() //You can manage the blinking
				// time with this parameter
				anim.repeatMode = Animation.REVERSE
				anim.repeatCount = AppConstants.RECORDING_FLASH_COUNT
				ivRecordingIndicator.startAnimation(anim)
			}
		} catch (ex: Exception) {
			mlogger.error(
					TAG, LogEvent.EXCEPTION, LogEventValue.GM5MEETINGROOM,
					"WebMeetingActivity recordingFlashAnimation()  - Exception showing record animation",
					null, null, true, false)
		}
	}

	// Recording states end
	override val useHtml5: Boolean
		get() = true

	// Activity, fragment and meeting states
	override fun onBackPressed() {
		//For the time being we have disabled the back key when user is in meeting room...
		if (flFragmentPlaceHolder.visibility == View.VISIBLE) {
			val currentFragment = mFragmentManager?.findFragmentById(R.id.fl_fragment_place_holder)
			if (currentFragment is AudioSelectionFragment) {
				if (mIsChangeAudioFlow) {
					resumeMeetingRoom()
				} else {
					closeAudioPanel()
				}
			} else if (currentFragment is DialInFragment) {
				supportFragmentManager.popBackStack()
			}  else if (currentFragment is DialInSelectionFragment) {
				supportFragmentManager.popBackStack()
			} else if (currentFragment is DialInSearchFragment) {
				supportFragmentManager.popBackStack()
			} else if (currentFragment is FeedbackCommentFragment) {
				supportFragmentManager.popBackStack()
			} else {
				moveTaskToBack(true)
			}
		} else {
			moveTaskToBack(true)
		}
	}

	override fun closeAudioPanel() {
		if (mMeetingUserViewModel?.userInMeeting == true) {
			if (mIsChangeAudioFlow) {
				resumeMeetingRoom()
			} else {
				handleDoNotConnectAudio()
				updateControlPanel(false, false)
				progressMicButton.visibility = View.INVISIBLE
			}
		} else {
			mMeetingUserViewModel?.clear()
			mMeetingEventViewModel?.clear()
			ElkTransactionIDUtils.resetTransactionId()
			openFeedbackFragment(false)
		}
	}

	private fun clearConferenceOnBackPress() {
		stopInProgressAudio(AudioType.NO_AUDIO)
		mMeetingUserViewModel?.leaveMeeting()
		if (isTaskRoot && !mMeetingUserViewModel?.userInMeeting!!) {
			val parentIntent = Intent(this, AppBaseLayoutActivity::class.java)
			startActivity(parentIntent)
		}
		finish()
	}

	private fun admitParticipantRequest(user: User) {
		mMeetingUserViewModel?.updateUserAdmitDeny(user, true)
	}

	private fun denyParticipantRequest(user: User) {
		mMeetingUserViewModel?.updateUserAdmitDeny(user, false)
	}

	override fun resumeActivity() {
		resumeMeetingRoom()
	}

	fun clearConvIdAndResumeAct()
	{
		if(getFragmentRefreshListener()!=null){
			getFragmentRefreshListener()?.onRefresh();
		}
		flChatFragmentPlaceHolder.visibility = View.GONE
		resumeMeetingRoom()
	}
	protected fun resumeMeetingRoom() {
		mIsChangeAudioFlow = false
		flFragmentPlaceHolder.visibility = View.GONE
		rlMeetingView.visibility = View.VISIBLE
		updateTopBarContainerVisibility()
		removeFragment()
	}

	// Alerts and Dialogs
	private fun showAlertToLeaveCurrentConference() {
		val dialog = AlertDialog.Builder(this)
				.setTitle(getString(R.string.leave_current_meeting_prompt_title))
				.setMessage(getString(R.string.leave_current_meeting_prompt_message))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.guest_exit_dialog_positive_btn)) { dialog1: DialogInterface?, which: Int ->
					mIsChangeAudioFlow = true
					mMeetingUserViewModel?.leaveMeeting()
				}
				.setNegativeButton(
						getString(R.string.guest_exit_dialog_negative_btn),
						null).create()
		dialog.show()
		if (dialog.getButton(DialogInterface.BUTTON_NEGATIVE) != null && dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
			dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
					ContextCompat.getColor(
							this,
							R.color.cancel_btn_text_color))
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
					ContextCompat.getColor(
							this,
							R.color.dialog_btn_text_color))
		}
	}

	override fun dismissExitLeaveMeetingDialogIfShowing() {
		mAlertDialog?.dismiss()
		mAlertDialog = null
	}

	fun createGuestExitDialog(): Dialog {
		val alertDialog = Dialog(this)
		alertDialog.setContentView(R.layout.guest_exit_dialog)
		val positiveBtn = alertDialog.findViewById<TextView>(R.id.tv_guest_dialog_yes)
		val negtiveBtn = alertDialog.findViewById<TextView>(R.id.tv_guest_dialog_no)
		positiveBtn.setOnClickListener { v: View? -> onPositiveClicked() }
		negtiveBtn.setOnClickListener { v: View? -> onNeutralClicked() }
		return alertDialog
	}

	fun createHostExitAlertDialog(): Dialog {
		val alertDialog = Dialog(this)
		alertDialog.setContentView(R.layout.host_exit_dialog)
		val positiveBtn = alertDialog.findViewById<TextView>(R.id.tv_host_dialog_exit)
		val negtiveBtn = alertDialog.findViewById<TextView>(R.id.tv_host_dialog_leave)
		val neutralBtn = alertDialog.findViewById<TextView>(R.id.tv_host_dialog_cancel)
		positiveBtn.setOnClickListener { v: View? -> onPositiveClicked() }
		negtiveBtn.setOnClickListener { v: View? -> onNegativeClicked() }
		neutralBtn.setOnClickListener { v: View? -> onNeutralClicked() }
		return alertDialog
	}

	private fun onNegativeClicked() {
		SharedPreferencesManager.getInstance().setHasJoinedUsersRoom(false)
		if (mIsMeetingHost) {
			mMeetingUserViewModel?.leaveMeeting()
		}
		mAlertDialog?.dismiss()
	}

	private fun onNeutralClicked() {
		ibExitButton.isEnabled = true
		mAlertDialog?.dismiss()
	}

	private fun onPositiveClicked() {
		SharedPreferencesManager.getInstance().setHasJoinedUsersRoom(false)
		if (mIsMeetingHost) {
			stopInProgressAudio(AudioType.NO_AUDIO)
			mMeetingUserViewModel?.endMeeting(true)
		} else {
			stopInProgressAudio(AudioType.NO_AUDIO)
			mMeetingUserViewModel?.leaveMeeting()
		}
		mAlertDialog?.dismiss()
	}

	override fun onServiceRetryFailed(retryStatus: RetryStatus) {
		if (retryStatus.value() == RetryStatus.NOCONNECTIVITY) {
			showInternetConnectionTimeoutDialog()
		} else if (retryStatus.value() == RetryStatus.FLAKYCONNECTIVITY) {
			showMeteredNetworkToast()
		}
		super.onServiceRetryFailed(retryStatus)
	}

	override fun finishActivity(showFeedbackFragment: Boolean, resultCode: Int) {
		mMeetingUserViewModel?.audioConnState = AudioStatus.NOT_CONNECTED
		mMeetingUserViewModel?.audioConnType = AudioType.NO_AUDIO
		mlogger.attendeeModel.audioConnectionType = AudioType.NO_AUDIO.toString()
		mMeetingUserViewModel?.isMute = false
		mMeetingUserViewModel?.isSpeakerOn = false
		mIsMeetingHost = false
		if (showFeedbackFragment) {
			if (isFeedbackFragmentVisible) {
				val fragmentManager = supportFragmentManager
				val fragment = fragmentManager.findFragmentByTag(FeedbackFragment::class.java.simpleName)
				val feedbackFragment = fragment as FeedbackFragment
				if (feedbackFragment.mPositiveFeedClicked || feedbackFragment.mNegativeFeedClicked) {
					feedbackFragment.takeClickAction()
				}
			} else {
				val feedbackFragment = FeedbackFragment()
				replaceFragments(feedbackFragment)
				if (feedbackFragment.mPositiveFeedClicked || feedbackFragment.mNegativeFeedClicked) {
					feedbackFragment.takeClickAction()
				}
			}
		} else {
			finish()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		SharedPreferencesManager.getInstance().scale = 0.0f
		compositeDisposable.clear()
		stopBandWidthTest()
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_DESTROY.name)
		mKeyboardOpenCloseListener?.unregisterKeyboardOpenCloseListener(mRootView)
		mlogger.clearMeetingModel()
		mMeetingEventViewModel?.clear()
		mMeetingUserViewModel?.clear()
	}

	private inner class UpdateDialOutPhoneTask : AsyncTask<String?, Void?, String?>() {
		override fun doInBackground(vararg params: String?): String? {
			dialOutPhoneNumber?.let {
				mWebMeetingPresenter?.updateHostLastDialOutPhoneNumber(
						mConferenceId,
						it.countryCode,
						it.number,
						it.extension)
			}
			return null
		}
	}

	private fun notifyUserForNotGettingAudioPermission() {
		val currentFragment = mFragmentManager?.findFragmentById(R.id.fl_fragment_place_holder)
		if (currentFragment is AudioSelectionFragment) {
			Snackbar.make((currentFragment as AudioSelectionFragment).rlContainer, getString(R.string.unable_to_get_audio_permission), Snackbar.LENGTH_SHORT).show()
		}
	}


	companion object {
		private const val PARTICIPANT_LIST_FRAGMENT_INDEX = 0
		private const val SCREEN_SHARE_FRAGMENT_INDEX = 1
		private const val CHAT_FRAGMENT_INDEX = 2
		private const val DISMISSAL_POSITION_OFFSET = 0.333f
		private const val POSITION = 1
		private val TAG = WebMeetingActivity::class.java.simpleName
		private const val MIXPANEL_EVENT = "Mixpanel Event: "
		private const val SAMPLE_MIC = 1
	}
	private fun mixpanelCameraOnEvent(webcamActionValue : String) {
		mlogger.mixpanelTurnOnCameraModel.webcamAction = webcamActionValue
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_WEBCAM_ON, LogEventValue.MIXPANEL_WEBCAM_ON.value,
				null, null, false, true)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	fun onAppResumeObserver() {
		if(mMeetingUserViewModel?.tabValue == SCREEN_SHARE_FRAGMENT_INDEX) {
			mMeetingUserViewModel?.appInForeground = true
		}
		isCameraPermissonDialogOpen = false
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	fun onAppPauseObserver() {
		if(mMeetingUserViewModel?.tabValue == SCREEN_SHARE_FRAGMENT_INDEX) {
			mMeetingUserViewModel?.appInForeground = false
		}
		/**
		 remove the SelectPrivateChatFragment from fragmentManager if visible
		 */
		if (mMeetingUserViewModel?.tabValue == CHAT_FRAGMENT_INDEX) {
			removeNewPrivateChatSelectionFragmentOnPause()
		}
	}

	fun updateTalker(activeTalker: ActiveTalker) {
		if (mMeetingUserViewModel?.contentPresentationActive == true && mMeetingUserViewModel?.screenShareFullScreen == false && activeTalker.isTalking) {
			tvfullname.text = activeTalker.user.name?.let { it }
			tvToatalParticipant.text = resources.getString(R.string.speaking).toLowerCase()
		} else {
			mFirstName?.let { updateTopBar(it) }
			updateParticipant(mUserList.size)
		}
	}

	fun updateTopBar(name: String) {
		val userName = name + "'s " + resources.getString(R.string.meeting).toLowerCase()
		tvfullname.text = userName
		updateTopBarContainerVisibility()
	}

	private fun updateTopBarContainerVisibility() {
		rlTopBarContainer.visibility = if ((mMeetingUserViewModel?.screenSharePortrait == true || mMeetingUserViewModel?.screenShareLandScape == true)
				&& mMeetingUserViewModel?.contentPresentationActive == true && mMeetingUserViewModel?.screenShareFullScreen == true) {
			View.GONE
		} else View.VISIBLE

	}

	fun updateParticipant(participantSize: Int) {
		updateTopBarContainerVisibility()
		tvToatalParticipant.text = when (participantSize) {
			0 -> resources.getString(R.string.no_participants).toLowerCase()
			1 -> resources.getString(R.string.one_participant).toLowerCase()
			else -> participantSize.toString() + AppConstants.BLANK_SPACE + resources.getString(R.string.participants).toLowerCase()
		}
	}

	override fun onResume() {
		super.onResume()
		val msg = AppConstants.MIXPANEL_EVENT + AppConstants.OPEN_APP
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_OPEN_APP, msg,
				null, null, false, true)
		mCurrentFragment?.let { replaceFragments(it,it.javaClass.simpleName) }
	}

	/**
	To add or Remove Select Private Chat Fragment from fragment manager
	 */
	fun addOrRemoveSelectPrivateChat(isAdd: Boolean, privateChatSelectionFragment: NewPrivateChatSelectionFragment) {
		val mFragmentManager = supportFragmentManager
		val transaction = mFragmentManager.beginTransaction()
		if (isAdd) {
			//adding the fragment
			transaction.replace(R.id.frameLytSelectChat, privateChatSelectionFragment, privateChatSelectionFragment.javaClass.simpleName)
			transaction.addToBackStack( privateChatSelectionFragment.javaClass.simpleName)
			transaction.attach(privateChatSelectionFragment)
			transaction.commit()
		} else {
			//removing the fragment
			transaction.remove(privateChatSelectionFragment)
			transaction.commit()
			mFragmentManager.popBackStack()
		}
	}

	/**
	 *  fetch the appropriate msg to display user
	 *  notify the method to show customize toast
	 */
	fun showDisablePrivateChatToast(isChatEnabled: Boolean) {
		val msg = if (isChatEnabled && mMeetingEventViewModel?.users?.value?.size ?: 0 < 2) {//private chat enable, participant is 1
			resources.getString(R.string.no_participant_available_to_chat)
		} else {
			val user = mMeetingEventViewModel?.getCurrentUser()
			if (user?.roomRole == AppConstants.HOST || user?.delegateRole == true) resources.getString(R.string.notification_for_host_on_chat_disable)
			else resources.getString(R.string.notification_host_disable_private_chat)
		}
		notifyUser(msg,Snackbar.LENGTH_LONG)
	}

	interface FragmentRefreshListener {
		fun onRefresh()
	}

	fun removeNewPrivateChatSelectionFragmentOnPause() {
		if(isNewPrivateChatSelectionFragmentVisible) {
			val fragment = supportFragmentManager.fragments.get(CHAT_FRAGMENT_INDEX)
			val chatListFragment = fragment as? ChatListFragment
			if(!isCameraPermissonDialogOpen) {
				chatListFragment?.addOrRemoveFragment(false)
			}
		}
	}

	/**
	 * show toast that private chat have restriction to send/receive below particular app version
	 */
	fun showPrivateChatRestrictionToast() {
		notifyUser(resources.getString(R.string.msg_private_chat_restriction), Snackbar.LENGTH_LONG)
	}

	/**
	 *  show toast for offline user only when in chat fragment
	 */
	fun showOfflineUserToast() {
		if (flChatFragmentPlaceHolder.visibility == View.VISIBLE) {
			notifyUser(resources.getString(R.string.offline_error_msg), Snackbar.LENGTH_LONG)
		}
	}

	fun openFeedbackCommentFragment() {
		val feedbackCommentFragment = FeedbackCommentFragment()
		replaceFragments(feedbackCommentFragment,"feedbackCommentFragment")
	}

	fun openFeedbackFragmentOnBack() {
		CommonUtils.hideKeyboard(this)
		val feedbackFragment = FeedbackFragment()
		replaceFragments(feedbackFragment)
	}
}