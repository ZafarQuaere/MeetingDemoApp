package com.pgi.convergencemeetings.meeting.gm4.ui

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ConnectionState
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.enums.NotificationConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.PermissionUtil
import com.pgi.convergence.utils.SimpleCustomSnackbar
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiAudioRouteManager
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneConstants
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.AudioSelectionFragment.Companion.newInstance
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.DialInFragmentContractor.DialInActivityContract
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.FeedbackFragment
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.*
import com.pgi.convergencemeetings.models.MeetingParticipant
import com.pgi.convergencemeetings.models.Talker
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.MiscRoomViewFragment
import com.pgi.convergencemeetings.utils.*
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * Abstract class for GM4 meeting rooms. GuestMeetingRoomActivity and HostMeetingRoomActivity
 * extends this class to implement the specific logic for guest or host.
 *
 *
 * Class generates the GM4 meeting room view and implements the click listeners for meeting controls.
 * Created by ashwanikumar on 9/18/2017.
 */
@UnstableDefault
@ObsoleteCoroutinesApi
abstract class MeetingRoomActivity : BaseMeetingActivity(),
																		 MeetingRoomContract.view,
																		 AudioSelectionFragmentContractor.activity,
																		 DialInActivityContract, DialOutFragmentContrator.activity,
																		 AudioSelectionShimmerContractor.activity {

	private val TAG = MeetingRoomActivity::class.java.simpleName

	private var mActiveTalker: MeetingParticipant? = null

	/**
	 * The M alert dialog.
	 */
	protected var mAlertDialog: Dialog? = null

	/**
	 * The M conf id.
	 */
	var mConfId: String? = null

	/**
	 * The M meeting room presenter.
	 */
	var mMeetingRoomPresenter: MeetingRoomContract.presenter? = null

	/**
	 * The Btn call setting.
	 */
	@BindView(R.id.btn_overflow_options)
	lateinit var btnCallSetting: ImageButton

	/**
	 * The Btn exit leave meeting.
	 */
	@BindView(R.id.btn_exit_meeting)
	lateinit var btnExitLeaveMeeting: ImageButton

	/**
	 * The Btn mic toggle.
	 */
	@BindView(R.id.btn_mute_meeting)
	lateinit var btnMicToggle: ToggleButton

    @BindView(R.id.progress_mic_disabled)
	lateinit var progressMicButton: ProgressBar
	/**
	 * The Btn speaker toggle.
	 */
	@BindView(R.id.btn_speaker)
	lateinit var btnSpeakerToggle: ToggleButton

	/**
	 * The Rv participants.
	 */
	@BindView(R.id.rv_participants_list)
	lateinit var rvParticipants: RecyclerView

	/**
	 * The Tv active talker initials.
	 */
	@BindView(R.id.tv_name_initials)
	lateinit var tvActiveTalkerInitials: TextView

	/**
	 * The Tv active talker name.
	 */
	@BindView(R.id.tv_full_name)
	lateinit var tvActiveTalkerName: TextView

	/**
	 * The Tv no one has spoken.
	 */
	@BindView(R.id.tv_no_one_spoken)
	lateinit var tvNoOneHasSpoken: TextView

	/**
	 * The Tv active talker state.
	 */
	@BindView(R.id.tv_talking_state)
	lateinit var tvActiveTalkerState: TextView

	/**
	 * The Tv canMute all.
	 */
	@BindView(R.id.tv_mute_all)
	lateinit var tvMuteAll: TextView

	/**
	 * The Ll active talker.
	 */
	@BindView(R.id.ll_active_talker)
	lateinit var llActiveTalker: LinearLayout

	/**
	 * The Iv mic level 1.
	 */
	@BindView(R.id.imgvw_mic_level_1)
	lateinit var ivMicLevel_1: ImageView

	/**
	 * The Iv mic level 2.
	 */
	@BindView(R.id.imgvw_mic_level_2)
	lateinit var ivMicLevel_2: ImageView

	/**
	 * The Iv mic level 3.
	 */
	@BindView(R.id.imgvw_mic_level_3)
	lateinit var ivMicLevel_3: ImageView

	/**
	 * The Iv mic level 4.
	 */
	@BindView(R.id.imgvw_mic_level_4)
	lateinit var ivMicLevel_4: ImageView

	/**
	 * The Iv mic level 5.
	 */
	@BindView(R.id.imgvw_mic_level_5)
	lateinit var ivMicLevel_5: ImageView

	/**
	 * The Iv mic level 6.
	 */
	@BindView(R.id.imgvw_mic_level_6)
	lateinit var ivMicLevel_6: ImageView

	/**
	 * The Iv mic level 7.
	 */
	@BindView(R.id.imgvw_mic_level_7)
	lateinit var ivMicLevel_7: ImageView

	/**
	 * The Iv mic level 8.
	 */
	@BindView(R.id.imgvw_mic_level_8)
	lateinit var ivMicLevel_8: ImageView

	/**
	 * The Iv mic level 9.
	 */
	@BindView(R.id.imgvw_mic_level_9)
	lateinit var ivMicLevel_9: ImageView

	/**
	 * The Iv mic level 10.
	 */
	@BindView(R.id.imgvw_mic_level_10)
	lateinit var ivMicLevel_10: ImageView

	/**
	 * The M shimmer view group.
	 */
	@BindView(R.id.shimmer_view_group)
	lateinit var mShimmerViewGroup: ViewGroup

	private var mShimmerLayout: ShimmerLayout? = null

	/**
	 * The Rl meeting view.
	 */
	@BindView(R.id.rl_meeting_view)
	lateinit var rlMeetingView: RelativeLayout

	/**
	 * The Fl fragment place holder.
	 */
	@BindView(R.id.fragment_place_holder)
	lateinit var flFragmentPlaceHolder: FrameLayout

	/**
	 * The Btn close shimmer.
	 */
	@BindView(R.id.btn_close_shimmer)
	lateinit var btnCloseShimmer: ImageView

	/**
	 * Recording Indicator
	 */
	@BindView(R.id.recordingIndicator)
	lateinit var ivRecordingIndicator: ImageView

	/**
	 * The M mic level view.
	 */
	var mMicLevelView: Array<ImageView> = emptyArray()

	private var mLastMicSignalLevel = 0

	@BindView(R.id.floatingSnackBar)
	lateinit var coordinatorLayout: CoordinatorLayout

	private var mIsDialInEnabled = false
	private var mIsVoipEnabled = false
	private var mIsMuteMe = false
	private var mIsActivateSpeaker = false
	private var mFirstName: String? = null
	override var isMeetingHost = false
	private var mFragmentManager: FragmentManager? = null
	private var mCurrentFragment: Fragment? = null
	private var mDataSet: ClientInfoDataSet? = null
	private var isMicButtonEnable: Boolean = false
	private var isSpeakerButtonEnable: Boolean = false
	private var isSwitchAudioButtonEnable: Boolean = false
	private var isExitLeaveButtonEnable: Boolean = true
	private var lowBandwidthSnackbar: SimpleCustomSnackbar? = null
	private var ragAlarmSnackbar: SimpleCustomSnackbar? = null

	/**
	 * Init views.
	 */
	abstract fun initViews()

	/**
	 * On positive clicked.
	 */
	abstract fun onPositiveClicked()

	/**
	 * On negative clicked.
	 */
	abstract fun onNegativeClicked()

	/**
	 * On neutral clicked.
	 */
	abstract fun onNeutralClicked()

	/**
	 * Handle exit leave meeting.
	 */
	@OnClick(R.id.btn_exit_meeting)
	abstract fun handleExitLeaveMeeting()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.initRetryCallBacks()
		setContentView(R.layout.activity_meeting_room)
		mMeetingRoomPresenter = MeetingRoomPresenter(this, this)
		ButterKnife.bind(this)
		val micLevelView = arrayOf(ivMicLevel_1, ivMicLevel_2, ivMicLevel_3, ivMicLevel_4, ivMicLevel_5, ivMicLevel_6, ivMicLevel_7, ivMicLevel_8, ivMicLevel_9, ivMicLevel_10)
		mMicLevelView = micLevelView
		addShimmerView()
		initViews()
		handleIntentData()
	}

	override fun onDestroy() {
		super.onDestroy()
		createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_DESTROY.name)
	}

	/**
	 * Adding Shimmer view on meeting room UI. It basically adding an extra view on meeting room
	 * UI and rotate it from left to right.
	 */
	private fun addShimmerView() {
		mShimmerLayout = ShimmerLayout(this)
		mShimmerLayout?.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT)
		mShimmerLayout?.setGhostImage(R.drawable.activity_meeting_room_ghost)
		mShimmerViewGroup.addView(mShimmerLayout)
	}

	/**
	 * Reads the intent data which was set when activity was launched and set the data into class variables.
	 */
	private fun handleIntentData() {
		if (intent?.hasExtra(AppConstants.IS_MEETING_HOST) == true) {
			isMeetingHost = intent.getBooleanExtra(AppConstants.IS_MEETING_HOST, false)
			mDataSet = if (isMeetingHost) {
				ClientInfoDaoUtils.getInstance()
			} else {
				ClientInfoResultCache.getInstance()
			}
			mMeetingRoomPresenter?.setMeetingHostStatus(isMeetingHost)
		}
		if (intent?.hasExtra(AppConstants.FIRST_NAME) == true) {
			mFirstName = intent.getStringExtra(AppConstants.FIRST_NAME)
		}
		if (intent?.hasExtra(AppConstants.MEETING_CONFERENCE_ID) == true) {
			mConfId = intent.getStringExtra(AppConstants.MEETING_CONFERENCE_ID)
			if (mConfId != null) {
				rlMeetingView.visibility = View.GONE
				showAudioLoadingScreen()
				mConfId?.let {
					mMeetingRoomPresenter?.subscribeToMeeting(it)
				}
			} else {
				finishActivity()
				Toast.makeText(this, "Invalid Conf id cannot start meeting.", Toast.LENGTH_LONG).show()
			}
		} else {
			finishActivity()
			Toast.makeText(this, "Invalid Conf id cannot start meeting.", Toast.LENGTH_LONG).show()
		}
	}

	/**
	 * Method to initialize and start softphone library for VOIP calls.
	 */
	override fun startSoftphone() {
		super.initSoftphoneCallBacks()
	}

	/**
	 * Replace the AudioSelection screen fragment by meeting room UI and show loading spinner on Meeting room UI
	 */
	protected fun showMeetingRoom() {
		resumeMeetingRoom()
		showLoadingScreen()
	}

	/**
	 * Replace the AudioSelection screen fragment by meeting room UI
	 */
	protected fun resumeMeetingRoom() {
		removeFragment()
		flFragmentPlaceHolder.visibility = View.GONE
		rlMeetingView.visibility = View.VISIBLE
	}

	/**
	 * Show Meeting Full screen.
	 */
	override fun showMeetingFullScreen() {
		val meetingFullFragment = MiscRoomViewFragment.newInstance(mFirstName, "LOCK")
		replaceFragmentsFade(meetingFullFragment)
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		if (intent.getIntExtra(AppConstants.KEY_INVALID_URL, 0) == AppConstants.INVALID_CONFERENCE_CODE) {
			showInvalidConferenceAlert(this)
		} else if (intent.hasExtra(AppConstants.MEETING_CONFERENCE_ID)) {
		} else {
			finishActivity(true, AppConstants.INVALID_CONFERENCE_CODE)
		}
		if (intent.action != null && intent.action == AppConstants.HANGUP_SOFTPHONE) {
			createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_STOP.name)
		} else if (intent.action != null && intent.action == AppConstants.MIC_TOGGLE) {
			btnMicToggle.performClick()
		} else if (intent.action != null && intent.action == AppConstants.SPEAKER_TOGGLE) {
			btnSpeakerToggle.performClick()
		} else if (intent.action != null && intent.action == AppConstants.OPEN_ACTIVITY) {
		}
	}

	/**
	 * Show Meeting Locked screen.
	 */
	override fun showMeetingLockedScreen() {
		val meetingLockedFragment = MiscRoomViewFragment.newInstance(mFirstName, "ROOM_AT_CAPACITY")
		replaceFragmentsFade(meetingLockedFragment)
	}

	override fun showAudioLoadingScreen() {
		val audioSelectionProgressFragment = AudioSelectionProgressFragment()
		replaceFragments(audioSelectionProgressFragment)
		intent.putExtra(AppConstants.BUNDLE_KEY_TIMESTAMP, System.currentTimeMillis())
		intent.putExtra(AppConstants.ELK_EVENT, AppConstants.ACTION_START_DURATION)
	}

	/**
	 * Show AudioSelection screen after shimmer screen.
	 */
	override fun showAudioSelectionView() {
		val useHtml5 = useHtml5
		if (!useHtml5) {
			val audioSelectionFragment = newInstance(mIsVoipEnabled, mIsDialInEnabled, mIsActivateSpeaker, mIsMuteMe, mFirstName, useHtml5, false, false)
			replaceFragments(audioSelectionFragment)
		} else {
			//            getMlogger().error(TAG, null, null, "Trying to start a GM5 room as GM4");
			val confId = mDataSet?.conferenceId
			val furl = mDataSet?.meetingRoomData?.meetingRoomUrls?.attendeeJoinUrl
			val firstName = mDataSet?.firstName
			launchAudioSelectionActivity(context, confId, furl, firstName, true,
					System.currentTimeMillis(), JoinMeetingEntryPoint.UNKNOWN, false, null)
		}
	}

	fun getDateDiff(previous: Long): Long {
		val current = System.currentTimeMillis()
		return current - previous
	}

	val isOnWifi: Boolean
		get() {
			val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
			return mWifi.isConnected
		}

	override fun showLoadingScreen() {
		if (flFragmentPlaceHolder.visibility != View.VISIBLE) {
			mShimmerViewGroup.visibility = View.VISIBLE
			mShimmerLayout?.startShimmerAnimation()
		} else {
			mShimmerViewGroup.visibility = View.GONE
		}
	}

	override fun hideLoadingScreen() {
		mShimmerLayout?.stopShimmerAnimation()
		mShimmerViewGroup.visibility = View.GONE
	}

	override fun enableVoIPButton() {
		//update DialInFragmentContract from here
		mIsVoipEnabled = true
	}

	override fun disableVoIPButton() {
		//update DialInFragmentContract from here
		mIsVoipEnabled = false
	}

	override fun enableDialInButton() {
		mIsDialInEnabled = true
	}

	override fun disableDialInButton() {
		mIsDialInEnabled = true
	}

	/**
	 * Updating Active talker UI based on the response of long polling.
	 *
	 * @param talker Active talker in meeting.
	 */
	override fun updateActiveTalker(talker: Talker) {
		val participants = talker.parts
		if (CommonUtils.isNonEmptyArray(participants)) {
			mActiveTalker = mMeetingRoomPresenter?.getActiveTalkerParticipant(meetingParticipants, talker.parts[0])
			if (mActiveTalker != null) {
				updateActiveTalkerUI()
			}
		} else {
			updateActiveTalkerToWasTalking()
		}
	}

	//Updating talker state to past user which was talking, If didn't get any update on active
	// talker for more then 4 sec.
	private fun updateActiveTalkerToWasTalking() {
		if (mActiveTalker != null) {
			val talkingState: String = if (isMyTalkerUpdate) {
				getString(R.string.were_taking)
			} else {
				getString(R.string.no_one)+AppConstants.BLANK_SPACE+getString(R.string.speaking)
			}
			tvActiveTalkerState.text = talkingState
			tvActiveTalkerName.visibility = View.GONE
			tvActiveTalkerState.visibility = View.VISIBLE
			tvNoOneHasSpoken.visibility = View.GONE
		}
	}

	/**
	 * Updating canMute for the user if user selected canMute option on AudioSelection screen.
	 */
	override fun updateMuteOnEntry() {
		if (mIsMuteMe) {
			updateMuteOnEntry(mIsMuteMe)
		}
	}

	/**
	 * Updating participants in participant list, Getting the participant events via
	 * web socket and update the list on UI.
	 *
	 * @param participant meeting participant
	 */
	override fun updateParticipants(participant: MeetingParticipant) {
		meetingParticipants = mMeetingRoomPresenter?.prepareParticipantsData(meetingParticipants, participant)
				?: emptyList()
		if (meetingParticipants.isNotEmpty()) {
			val linearLayoutManager = LinearLayoutManager(this@MeetingRoomActivity)
			rvParticipants.layoutManager = linearLayoutManager
			val meetingParticipantsAdapter = MeetingParticipantsAdapter(this@MeetingRoomActivity, meetingParticipants)
			rvParticipants.adapter = meetingParticipantsAdapter
			meetingParticipantsAdapter.notifyDataSetChanged()
		}
	}

	//Update Active talker UI, If the currently talker is self make it "You are talking",
	// Otherwise display the name of active talker.
	private fun updateActiveTalkerUI() {
		val isLocaleJapan = CommonUtils.isUsersLocaleJapan()
		val isMyTalkerUpdate = isMyTalkerUpdate
		val talkingState: String
		val fullName: String
		if (isMyTalkerUpdate) {
			talkingState = getString(R.string.currently_talking)
			fullName = getString(R.string.you)
		} else {
			talkingState = getString(R.string.is_talking)
			fullName = if (isLocaleJapan) {
				CommonUtils.getFullName(mActiveTalker?.lastName, mActiveTalker?.firstName)
			} else {
				CommonUtils.getFullName(mActiveTalker?.firstName, mActiveTalker?.lastName)
			}
		}
		val initials = CommonUtils.getNameInitials(mActiveTalker?.firstName, mActiveTalker?.lastName)
		tvActiveTalkerInitials.visibility = View.VISIBLE
		tvActiveTalkerInitials.background = resources.getDrawable(R.drawable.active_talker_circle_drawable)
		if (isLocaleJapan) {
			tvActiveTalkerInitials.text = CommonUtils.formatJapaneseInitials(initials)
		} else {
			tvActiveTalkerInitials.text = initials
		}
		tvActiveTalkerName.text = CommonUtils.formatCamelCase(fullName)
		tvActiveTalkerState.text = talkingState
		tvActiveTalkerName.visibility = View.VISIBLE
		tvActiveTalkerState.visibility = View.VISIBLE
		tvNoOneHasSpoken.visibility = View.GONE
	}

	private val isMyTalkerUpdate: Boolean
		get() {
			var isMyTalkerUpdate = false
			val meetingParticipant = ConferenceStateCache.getInstance().myPartInfo
			return meetingParticipant.partID == mActiveTalker?.partID
		}

	/**
	 * Update canMute on entry.
	 *
	 * @param isUserMuted the is user muted
	 */
	fun updateMuteOnEntry(isUserMuted: Boolean) {
		mMeetingRoomPresenter?.handleMuteButtonClick(isUserMuted)
	}

	override fun onBackPressed() {
		//For the time being we have disabled the back key when user is in meeting room...
		if (flFragmentPlaceHolder.visibility == View.VISIBLE) {
			val currentFragment = mFragmentManager?.findFragmentById(R.id.fragment_place_holder)
			if (currentFragment is AudioSelectionFragment) {
				closeAudioPanel()
			} else if (currentFragment is DialInSelectionFragment) {
				showAudioFragment()
			} else if (currentFragment is DialOutSelectionFragment) {
				showAudioFragment()
			} else {
				moveTaskToBack(true)
			}
		} else {
			moveTaskToBack(true)
		}
	}

	/**
	 * Handling back press event on meeting room screen.
	 */
	private fun clearConferenceOnBackPress() {
		mMeetingRoomPresenter?.clearSocket()
		if (ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			if (ConferenceManager.getConnectionType() == ConnectionType.VOIP) {
				stopSoftPhone()
			}
		}
		val participantId = currentParticipantId
		if (participantId != null) {
			mMeetingRoomPresenter?.leaveConference(participantId)
		} else {
			mMeetingRoomPresenter?.clearConferenceWatch()
		}
		finish()
	}

	/**
	 * Gets current participant id.
	 *
	 * @return the current participant id
	 */
	protected val currentParticipantId: String?
		get() {
			val conferenceStateCache = ConferenceStateCache.getInstance()
			val participant = conferenceStateCache.myPartInfo
			return participant?.partID
		}

	/**
	 * Mute button click event handle for meeting controls.
	 */
	@OnClick(R.id.btn_mute_meeting)
	fun onMuteButtonClick() {
		val isMuted = btnMicToggle.isChecked
		mMeetingRoomPresenter?.handleMuteButtonClick(isMuted)
	}


	/**
	 * Speaker button click event handle for meeting controls.
	 */
	@OnClick(R.id.btn_speaker)
	fun onSpeakerButtonClick() {
		val isSpeakerChecked = btnSpeakerToggle.isChecked
		val audioRouteManager = context?.let { PGiAudioRouteManager(it, mlogger) }
		val isBTDeviceAvailable = audioRouteManager?.isBluetoothAvailable()
		activateSpeakerPhone(isSpeakerChecked)
	}

	/**
	 * On active talker view clicked.
	 */
	@OnClick(R.id.ll_active_talker)
	fun onActiveTalkerViewClicked() {
	}

	/**
	 * On change audio clicked.
	 */
	@OnClick(R.id.btn_overflow_options)
	fun onChangeAudioClicked() {
	}
	private var mTurnWebCamOff = true
	fun onLowBandwidthClicked() {
		mTurnWebCamOff = !mTurnWebCamOff
		mMeetingUserViewModel?.turnWebcamOff = mTurnWebCamOff
	}

	override fun disableMuteButton() {
		if (btnMicToggle.isEnabled) {
			btnMicToggle.isEnabled = false
		}
	}

	override fun enableMuteButton() {
		if (btnMicToggle.isEnabled) {
			btnMicToggle.isEnabled = true
		}
	}

	override val useHtml5: Boolean
		get() = mDataSet?.isUseHtml5 ?: false

	/**
	 * Updating the mic signal level for VOIP. Getting mic signals from softphone and updating the UI.
	 *
	 * @param data mic signal level
	 */
	override fun updateSignalLevel(data: String?) {
		if (data != null && !btnMicToggle.isChecked) {
				val micSigLevelInt = data.toInt()
				if (micSigLevelInt != mLastMicSignalLevel) {
					updateMicLevelView(micSigLevelInt)
					mLastMicSignalLevel = micSigLevelInt
				}
		}
	}

	/**
	 * Updating the mic signal level for VOIP. Getting mic signals from softphone and updating the UI.
	 *
	 * @param micSigLevel signal level
	 */
	private fun updateMicLevelView(micSigLevel: Int) {
		val blue_line = R.drawable.line_2_copy_3
		val gray_line = R.drawable.line_2_copy_9
		val micSigValue = micSigLevel / 3
		for (i in 0..9) {
			if (i + 1 <= micSigValue) {
				mMicLevelView[i].setImageResource(blue_line)
			} else {
				mMicLevelView[i].setImageResource(gray_line)
			}
		}
	}

	private fun updateMicLevelEnabled(isEnabled: Boolean) {
		var resId = R.drawable.line_2_copy_9
		if (!isEnabled) {
			resId = R.drawable.disabled_mic_level_line
		}
		for (i in 0..9) {
			mMicLevelView[i].setImageResource(resId)
		}
	}

	override fun setUserMuted(isUserMuted: Boolean) {
		btnMicToggle.isChecked = isUserMuted
	}

	override val phoneNumber: String?
		get() = null

	override val countryCode: String?
		get() = null

	override val extension: String
		get() = ""

	override fun stopSoftPhone() {
		openFeedbackFragment()
		super.destroySoftphone()
		ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO, ConnectionState.DISCONNECTED)
		releaseAudioFocus()
	}

	/**
	 * Handling the cleaning of meeting state and displaying Thank you screen for it. When user calls
	 * leave meeting of end meeting this method is getting called clean the meeting state.
	 */
	override fun finishActivity() {
		ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO, ConnectionState.DISCONNECTED)
		mIsMuteMe = false
		mIsActivateSpeaker = false
		isMeetingHost = false
		if (isFeedbackFragmentVisible) {
			val fragmentManager = supportFragmentManager
			val fragment = fragmentManager.findFragmentByTag(FeedbackFragment::class.java.simpleName)
			val feedbackFragment = fragment as FeedbackFragment
			if (feedbackFragment.mPositiveFeedClicked || feedbackFragment.mNegativeFeedClicked) {
				feedbackFragment.takeClickAction()
			}
		} else {
			finish()
		}
	}

	/**
	 * Generate and show meeting notification in the status bar, while meeting is active. This notification
	 * will remain in the notification tray till the meeting will be active. User can open the meeting
	 * page again by clicking on this icon if the app is in background.
	 */
	override fun showActiveMeetingNotification() {
		onGoingMeetingIntent(NotificationConstants.MEETING_ONGOING.name, mConferenceId)
	}

	/**
	 * Generic method to replace the existing fragment with the given fragment.
	 *
	 * @param fragment the fragment
	 */
	fun replaceFragments(fragment: Fragment) {
		if (!isFinishing) {
			mCurrentFragment = fragment
			mFragmentManager = supportFragmentManager
			val transaction = mFragmentManager?.beginTransaction()
			transaction?.replace(R.id.fragment_place_holder, fragment, fragment.javaClass.simpleName)
			transaction?.commitAllowingStateLoss()
		}
	}

	fun replaceFragmentsFade(fragment: Fragment) {
		if (!isFinishing) {
			mCurrentFragment = fragment
			mFragmentManager = supportFragmentManager
			val transaction = mFragmentManager?.beginTransaction()
			transaction?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in)
			transaction?.replace(R.id.fragment_place_holder, fragment, fragment.javaClass.simpleName)
			transaction?.commitAllowingStateLoss()
		}
	}

	/**
	 * Removes the existing fragment on activity.
	 */
	private fun removeFragment() {
		val transaction = mFragmentManager?.beginTransaction()
		mCurrentFragment?.let { transaction?.remove(it) }
	}

	/**
	 * Handles VOIP button click event for Audio selection screen, User can choose the Voip option
	 * to connect to the meeting room. App is using Softphone library to connect via VOIP.
	 */
	override fun startVoipMeeting() {
		updateControlPanel(true, true)
		showMeetingRoom()
		btnMicToggle.isChecked = mIsMuteMe
		updateMicLevelEnabled(true)
		btnSpeakerToggle.isChecked = mIsActivateSpeaker
		sipIdentifier = mMeetingRoomPresenter?.createSipFromAddress();
		mMeetingRoomPresenter?.startVoipMeeting(mIsMuteMe, mIsActivateSpeaker, isMeetingHost)
		intent.putExtra(AppConstants.BUNDLE_KEY_TIMESTAMP, System.currentTimeMillis())
		intent.putExtra(AppConstants.ELK_EVENT, AppConstants.ACTION_VOIP_DURATION)
		requestAudioFocus()
	}

	override fun connectSoftPhone(mFormattedSipUri: String) {
		super.connectSoftPhone(mFormattedSipUri, false)
		onSpeakerButtonClick()
		progressMicButton.visibility = View.VISIBLE;
	}

	/**
	 * Handles Dial-in button click event for Audio selection screen. Opens the DialInSelectionFragment
	 * to choose a number form the list of numbers available for that conference.
	 */
	override fun handleDialInAction() {
		//show dial in DialInFragmentContract
		val dialInSelectionFragment = DialInSelectionFragment(isMeetingHost, this, false)
		replaceFragments(dialInSelectionFragment)
	}

	override fun showAudioFragment() {
		val useHtml5 = useHtml5
		val audioSelectionFragment = newInstance(mIsVoipEnabled, mIsDialInEnabled, mIsActivateSpeaker, mIsMuteMe, mFirstName, useHtml5, false, false)
		replaceFragments(audioSelectionFragment)
	}

	override fun handleDoNotConnectAudio() {}
	override fun showDialOutSelectionFragment() {}
	override fun showDialOutFragment(selectedPhoneNumber: Phone) {}

	/**
	 * Handles the Dial-In options for AudioSelection Screen. Updates the metting controls,
	 * like disable Mic and Speaker icons, and open phone dialler and dial the meeting room number with passcode.
	 *
	 * @param phoneNumber phone number with passcode
	 */
	override fun handleDialInClick(phoneNumber: String) {
		updateControlPanel(false, false)
		showMeetingRoom()
		btnMicToggle.isChecked = mIsMuteMe
		mMeetingRoomPresenter?.startDialInMeeting(mIsMuteMe, mIsActivateSpeaker, isMeetingHost, phoneNumber)
	}

	override fun setAudioBehaviour(isMuteMe: Boolean, isActivateSpeaker: Boolean) {
		mIsMuteMe = isMuteMe
		mIsActivateSpeaker = isActivateSpeaker
	}

	override fun closeAudioPanel() {
		if (ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			removeFragment()
			flFragmentPlaceHolder.visibility = View.GONE
			rlMeetingView.visibility = View.VISIBLE
		} else {
			mMeetingRoomPresenter?.clearSocket()
			mMeetingRoomPresenter?.cleanUpMeetingState()
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO -> {

				// If request is cancelled, the result arrays are empty.
				if (PermissionUtil.verifyPermissions(grantResults)) {
					// permission was granted
					startSoftphone()
				} else {
					// permission denied, boo! Disable the functionality that depends on this permission.
					notifyUserForNotGettingPermission()
				}
			}
			AppConstants.PERMISSIONS_REQUEST_PHONE_CALL -> {

				// If request is cancelled, the result arrays are empty.
				if (PermissionUtil.verifyPermissions(grantResults)) {
					// permission was granted
					 handleDialIn("")
				} else {
					// permission denied, boo! Disable the functionality that depends on this permission.
					notifyUserForNotGettingPermission()
				}
			}
		}
	}

	private fun notifyUserForNotGettingPermission() {
		val currentFragment = mFragmentManager?.findFragmentById(R.id.fragment_place_holder)
		if (currentFragment is AudioSelectionFragment) {
			Snackbar.make((currentFragment as AudioSelectionFragment).rlContainer, getString(R.string.unable_to_get_audio_permission), Snackbar.LENGTH_SHORT).show()
		}
	}

	override fun onServiceRetryFailed(retryStatus: RetryStatus) {
		super.onServiceRetryFailed(retryStatus)
	}

	override fun muteUnmuteChangeConnection(isMuteMe: Boolean) {
		mIsMuteMe = isMuteMe
	}

	override fun handleCallMyPhoneClick() {
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP &&
				ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			releaseAudioFocus()
		}
		//Need to change the mic button color if it's a dial-out.
		btnMicToggle.setButtonDrawable(R.drawable.mic_btn_pstn_selector)
		val audioSelectionProgressFragment = AudioSelectionProgressFragment()
		replaceFragments(audioSelectionProgressFragment)
		mMeetingRoomPresenter?.dialOutPhoneNumbers
	}

	override fun handleCallMeClick(selectedPhoneNumber: Phone?) {
		updateControlPanel(true, false)
		showMeetingRoom()
		btnMicToggle.isChecked = mIsMuteMe
		mMeetingRoomPresenter?.startDialOutMeeting(mIsMuteMe, mIsActivateSpeaker, isMeetingHost, selectedPhoneNumber!!)
	}

	override fun phoneNumbersReceivedSuccessfully(phoneNumberList: List<Phone>) {
		val dialOutSelectionFragment = DialOutSelectionFragment.newInstance()
		dialOutSelectionFragment.initDialoutFragment(this, isMeetingHost, phoneNumberList, this, false)
		replaceFragments(dialOutSelectionFragment)
	}

	override fun openFeedbackFragment() {
		if (ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			showFeedbackFragment()
		}
	}

	//This method is to handle special case when Host ends the meeting and Guest
	// needs to be removed forcefully, Also in this case VOIP get disconnected before calling
	// leaveMeeting() and disconnectSoftphone() methods. Hence openFeedbackFragment never display
	// Thank you screen
	override fun showFeedbackFragment() {
		dismissExitLeaveMeetingDialogIfShowing()
		onGoingMeetingIntent(NotificationConstants.REMOVE_NOTIFICATION.name, mConferenceId)
		flFragmentPlaceHolder.visibility = View.VISIBLE
		val fragment: Fragment = FeedbackFragment()
		replaceFragments(fragment)
	}

	private val isFeedbackFragmentVisible: Boolean
		private get() {
			val fragment = supportFragmentManager.findFragmentByTag(FeedbackFragment::class.java.simpleName)
			return fragment != null && fragment is FeedbackFragment
		}

	private fun updateControlPanel(isMicEnabled: Boolean, isSpeakerEnabled: Boolean) {
		updateMicButton(isMicEnabled)
		updateSpeakerButton(isSpeakerEnabled)
	}

	private fun updateMicButton(isMicEnabled: Boolean) {
		if (isMicEnabled) {
			btnMicToggle.isClickable = true
			btnMicToggle.isEnabled = true
		} else {
			btnMicToggle.isClickable = false
			btnMicToggle.isEnabled = false
		}
	}

	private fun updateSpeakerButton(isSpeakerEnabled: Boolean) {
		if (isSpeakerEnabled) {
			btnSpeakerToggle.isClickable = true
			btnSpeakerToggle.isEnabled = true
		} else {
			btnSpeakerToggle.isClickable = false
			btnSpeakerToggle.isEnabled = false
		}
	}

	override fun onNeedPermission(permission: String) {
		when (permission) {
			Manifest.permission.RECORD_AUDIO -> ActivityCompat.requestPermissions(this, arrayOf(
					Manifest.permission.RECORD_AUDIO), AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO)
			Manifest.permission.CALL_PHONE -> ActivityCompat.requestPermissions(this, arrayOf(
					Manifest.permission.CALL_PHONE), AppConstants.PERMISSIONS_REQUEST_PHONE_CALL)
		}
	}

	override fun onPermissionPreviouslyDenied(permission: String) {
		run {
			when (permission) {
				Manifest.permission.RECORD_AUDIO -> ActivityCompat.requestPermissions(this, arrayOf(
						Manifest.permission.RECORD_AUDIO), AppConstants.PERMISSIONS_REQUEST_RECORD_AUDIO)
				Manifest.permission.CALL_PHONE -> ActivityCompat.requestPermissions(this, arrayOf(
						Manifest.permission.CALL_PHONE), AppConstants.PERMISSIONS_REQUEST_PHONE_CALL)
			}
		}
	}

	override fun resumeActivity() {
		resumeMeetingRoom()
	}

	override fun softPhoneConnected() {
		updateSpeakerButton(true)
		progressMicButton.visibility = View.GONE
	}

	override fun softPhoneReconnected() {
		updateMicButton(true)
	}

	override fun setFirstName(firstName: String) {
		mFirstName = firstName
	}

	override fun onInternetConnected() {
		super.onInternetConnected()
		val connectionType = ConferenceManager.getConnectionType()
		when (connectionType) {
			ConnectionType.VOIP -> {
				reconnectVoip = true
				createSoftPhoneIntent(PGiSoftPhoneConstants.VOIP_NETWORK_CHANGE.name)
				if (ConferenceManager.getConnectionState() == ConnectionState.DISCONNECTED) {
					ConferenceManager.resetConnectionState(false)
				}
			}
			else -> {
			}
		}
		btnMicToggle.isEnabled = isMicButtonEnable
		btnSpeakerToggle.isEnabled = isSpeakerButtonEnable
		btnCallSetting.isEnabled = false
		btnExitLeaveMeeting.isEnabled = isExitLeaveButtonEnable
	}

	override fun onInternetDisconnected() {
		isMicButtonEnable = btnMicToggle.isEnabled
		isSpeakerButtonEnable = btnSpeakerToggle.isEnabled
		isSwitchAudioButtonEnable = btnCallSetting.isEnabled
		isExitLeaveButtonEnable = btnExitLeaveMeeting.isEnabled

		//Disable all meeting controls
		btnMicToggle.isEnabled = false
		updateMicLevelEnabled(false)
		btnSpeakerToggle.isEnabled = false
		btnCallSetting.isEnabled = false
		btnExitLeaveMeeting.isEnabled = false
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP && ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			reconnectVoip = true
		}
	}

	override fun softPhoneDisconnected() {
		createSoftPhoneIntent(PGiSoftPhoneConstants.STOP_SERVICE.name)
		if (ConferenceManager.isMeetingActive()) {
			onGoingMeetingIntent(NotificationConstants.MEETING_ONGOING.name, mConferenceId)
		}
		updateControlPanel(false, false)
		if (reconnectVoip) {
			val i = Intent("com.pgi.convergencemeetings.activities.SOFTPHONERECONNECT")
			sendBroadcast(i)
		} else {
			ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO, ConnectionState.DISCONNECTED)
		}
		//        logger.info(TAG, ELKConstants.ROOMEVENT_VOIP_FAILURE, ELKConstants.GM5_VALUE);
	}

	override fun finishActivity(showFeedbackFragment: Boolean, resultCode: Int) {}

	@OnClick(R.id.btn_close_shimmer)
	override fun handleCloseShimmerClick() {
		clearConferenceOnBackPress()
	}

	override fun sendUserToRejoinMeeting() {
		handleIntentData()
	}

	override fun showBadNetworkToast() {
		val coordinator = findViewById<View>(R.id.floatingSnackBar) as CoordinatorLayout
		ragAlarmSnackbar = SimpleCustomSnackbar.make(coordinator, getString(R.string
				.bad_network_conditions_message), Snackbar.LENGTH_LONG, badNetworkListener, getString(R.string.bad_network_conditions_title), getColor(R.color.logout_dialog_background))
		if (ragAlarmSnackbar?.isShownOrQueued == false) {
			ragAlarmSnackbar?.show()
		}
		mlogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION, LogEventValue.MIXPANEL_POOR_NETWORK_NOTIFICATION.value, null, null, false, true)
	}
	var badNetworkListener: View.OnClickListener = View.OnClickListener {
		runOnUiThread {
			ragAlarmSnackbar?.dismiss()
			onChangeAudioClicked()
		}
	}

	override fun showBadNetworkLowBandwidthToast() {
		val coordinator = findViewById<View>(R.id.floatingSnackBar) as CoordinatorLayout
		lowBandwidthSnackbar = SimpleCustomSnackbar.make(coordinator, getString(R.string
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

	/*
  Adding the recording icon when a user is is a GM4 room and recording has started
   */
	override fun startRecordingIndicator() {
		recordingFlashAnimation()
	}

	/*
   Removing the recording icon when a user is is a GM4 room and recording has ended
    */
	override fun stopRecordingIndicator() {
		ivRecordingIndicator.visibility = View.INVISIBLE
	}

	/*
    Setting the flashing animation when a user joins a GM4 room and recording has started
     */
	fun recordingFlashAnimation() {
		val anim: Animation = AlphaAnimation(AppConstants.ALPHA_RECORDING_0, AppConstants.ALPHA_RECORDING_1)
		ivRecordingIndicator.visibility = View.VISIBLE
		anim.duration = AppConstants.RECORDING_FLASH_DURATION.toLong() //You can manage the blinking time with this parameter
		anim.repeatMode = Animation.REVERSE
		anim.repeatCount = AppConstants.RECORDING_FLASH_COUNT
		ivRecordingIndicator.startAnimation(anim)
	}
}