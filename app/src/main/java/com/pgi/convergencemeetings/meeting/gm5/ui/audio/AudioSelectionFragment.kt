package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.ConnectionState
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergence.listeners.PermissionAskListener
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.PermissionUtil
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiAudioRouteManager
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.utils.ConferenceManager
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by ashwanikumar on 11/13/2017.
 */
class AudioSelectionFragment : Fragment(), PermissionAskListener, AudioSelectionFragmentContractor.fragment {
  @BindView(R.id.tv_audio_panel_header_title)
	lateinit var tvAudioPanelHeaderTitle: TextView

  @BindView(R.id.tv_audio_cnfrm_msg)
	lateinit var tvAudioConfirmationMsg: TextView

  @BindView(R.id.chkbx_speaker)
	lateinit var chkActivateSpeaker: Switch

  @BindView(R.id.chkbx_mute_me)
	lateinit var chkMuteMe: Switch

  @BindView(R.id.btn_call_phone)
	lateinit var btnCallMe: Button

  @BindView(R.id.btn_back)
	lateinit var btnBack: ImageView

  @BindView(R.id.connect_voip_btn)
	lateinit var btnStartVoIP: Button

  @BindView(R.id.btn_dial_in)
	lateinit var btnDialIn: Button

  @BindView(R.id.btn_donot_connect)
	lateinit var btnDoNotConnectAudio: Button

	@BindView(R.id.rl_container)
	lateinit var rlContainer: RelativeLayout

	private var mAudioSelectionCallbacks: AudioSelectionFragmentContractor.activity? = null
	private var mFirstName: String? = null
	private var mIsOwnRoom = false
	private var mIsVoipEnabled = false
	private var mIsMuted = false
	private var mIsActivateSpeaker = false
	private var mIsDialInEnabled = false
	private var mIsUseHtml5 = false
	private val mLogger: Logger = CoreApplication.mLogger
	private var mIsFreemiumEnabled = false
	private var mIsDialOutBlocked = false
	private var recordAudioPermissionGranted = false
	private var mMeetingUserViewModel: MeetingUserViewModel? = null
	private var mLinearViewSpeakerLevel: LinearLayout? = null
    private var mAudioRouteManager: PGiAudioRouteManager? = null
	protected var mlogger: Logger = CoreApplication.mLogger

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.AUDIO_SELECTION.interaction)
		mFirstName = arguments?.getString("mFirstName")
		mIsOwnRoom = arguments?.getBoolean("mIsOwnRoom") ?: false
		mIsVoipEnabled = arguments?.getBoolean("mIsVoipEnabled") ?: false
		mIsDialInEnabled = arguments?.getBoolean("mIsDialInEnabled") ?: false
		mIsMuted = arguments?.getBoolean("mIsMuted") ?: false
		mIsActivateSpeaker = arguments?.getBoolean("mIsActivateSpeaker") ?: false
		mIsUseHtml5 = arguments?.getBoolean("mIsUseHtml5") ?: false
		mIsFreemiumEnabled = arguments?.getBoolean(IS_FREEMIUM_ENABLED) ?: false
		mIsDialOutBlocked = arguments?.getBoolean(IS_DIALOUT_BLOCKED) ?: false
		mLinearViewSpeakerLevel = activity?.findViewById<LinearLayout>(R.id.view_speaker_level)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.audio_selection_fragment, container, false)
		ButterKnife.bind(this, view)
		getActivity()?.setRequestedOrientation((ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
		mAudioRouteManager = context?.let { PGiAudioRouteManager(it, mlogger) }
		initAudioSelectionFragment()
		initAudioSelectionTitle()
		initAudioControls()
		registerViewModels()
		checkConnectedDevice()
		return view
	}

    private fun checkConnectedDevice() {
        val isBluetoothAvailable = mAudioRouteManager?.isBluetoothAvailable()
		val checkHeadphonesPlugged = mAudioRouteManager?.isHeadphonesPlugged()
		if(isBluetoothAvailable == true || checkHeadphonesPlugged == true) {
			chkActivateSpeaker.setChecked(false);
        } else {
			chkActivateSpeaker.setChecked(true);
		}
    }

	override fun onStart() {
		super.onStart()
		getActivity()?.setRequestedOrientation((ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
	}

	private fun registerViewModels() {
		if (mIsUseHtml5) {
			mMeetingUserViewModel = activity?.let { ViewModelProviders.of(it).get(MeetingUserViewModel::class.java) }
		}
	}

	private fun initAudioSelectionTitle() {
		if (mFirstName != null) {
			val res = resources
			@SuppressLint("StringFormatInvalid", "LocalSuppress")
			tvAudioPanelHeaderTitle.text = CommonUtils.formatCamelCase(mFirstName) + "'s "+ res.getString(R.string.meeting).toLowerCase()
			if(mIsOwnRoom)
				tvAudioConfirmationMsg.text = res.getString(R.string.audio_selection_msg_own_room)
			else {
				val audioSelectionMsg = String.format(res.getString(R.string.audio_selection_msg), CommonUtils.formatCamelCase(mFirstName) + "'s")
				tvAudioConfirmationMsg.text = Html.fromHtml(audioSelectionMsg)
			}
		} else {
			val res = resources
			tvAudioConfirmationMsg.text = res.getString(R.string.audio_selection_msg_own_room)
		}
	}

	private fun initAudioSelectionFragment() {
		val activity: Activity? = activity
		if (activity != null) {
			mAudioSelectionCallbacks = getActivity() as AudioSelectionFragmentContractor.activity
		}
	}

	private fun initAudioControls() {
		chkMuteMe.isChecked = mIsMuted
		//Enable disable Voip Button
		if (mIsVoipEnabled) {
			enableVoIPButton()
			setAdditionalAudioBehaviour()
			mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION,
					"AudioSelectionFragment initAudioControls() - Checking Record Audio Permission", null, null, true, false)
			PermissionUtil.checkPermission(WeakReference(activity), Manifest.permission.RECORD_AUDIO, this)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				PermissionUtil.checkPermission(WeakReference(activity), Manifest.permission.FOREGROUND_SERVICE, this)
			}
		} else {
			disableVoIPButton()
		}

		//Enable disable Dial-In Button.
		if (mIsDialInEnabled) {
			enableDialInButton()
		} else {
			disableDialInButton()
		}
		//Enable disable Connect audio connection option
		if (mIsUseHtml5) {
			btnDoNotConnectAudio.visibility = View.VISIBLE
		} else {
			btnDoNotConnectAudio.visibility = View.GONE
		}
		//show or hide Dial-Out Button.
		if (mIsFreemiumEnabled || mIsDialOutBlocked) {
			btnCallMe.visibility = View.GONE
		} else {
			btnCallMe.visibility = View.VISIBLE
		}
	}

	@OnClick(R.id.connect_voip_btn)
	fun onVoipAudioClicked() {
		setAdditionalAudioBehaviour()
        val isMuteMe = chkMuteMe.isChecked
		mAudioSelectionCallbacks?.muteUnmuteChangeConnection(isMuteMe)
		mLinearViewSpeakerLevel?.visibility = View.VISIBLE
		if (ActivityCompat.checkSelfPermission(CoreApplication.appContext, Manifest.permission.RECORD_AUDIO)
				== PackageManager.PERMISSION_DENIED) {
			mAudioSelectionCallbacks?.onPermissionPreviouslyDenied(Manifest.permission.RECORD_AUDIO)
			return
		}
		mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION,
				"AudioSelectionFragment onVoipAudioClicked() - User clicked voip connect", null, null,
				true, false)
		if (CommonUtils.isConnectionAvailable(CoreApplication.appContext)) {
			if (mIsUseHtml5) {
				handleGM5VoipClick()
			} else {
				handleGM4VoipClick()
			}
		} else {
			Snackbar.make(rlContainer, getString(R.string.internet_not_connected), Snackbar.LENGTH_LONG).show()
		}
	}

	private fun handleGM5VoipClick() {
		if (mMeetingUserViewModel?.audioConnType === AudioType.VOIP
				&& mMeetingUserViewModel?.audioConnState !== AudioStatus.DISCONNECTED) {
			//do nothing just go back to meeting room
			mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION, "AudioSelectionFragment handleGM5VoipClick() - Skipping Voip as already connected",
					null, null, true, false)
			mAudioSelectionCallbacks?.resumeActivity()
		} else if (mMeetingUserViewModel?.audioConnType !== AudioType.VOIP) {
			startVoipMeeting()
		}
	}

	private fun handleGM4VoipClick() {
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP
				&& ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			//do nothing just go back to meeting room
			mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION, "AudioSelectionFragment handleGM5VoipClick() - Skipping Voip as already connected",
					null, null, true, false)
			mAudioSelectionCallbacks?.resumeActivity()
		} else if (ConferenceManager.getConnectionType() == ConnectionType.NO_AUDIO ||
				(ConferenceManager.getConnectionType() == ConnectionType.VOIP
						&& ConferenceManager.getConnectionState() == ConnectionState.DISCONNECTED)) {
			startVoipMeeting()
		}
	}

	@OnClick(R.id.btn_call_phone)
	fun onCallMeClicked() {
		mLinearViewSpeakerLevel?.visibility = View.INVISIBLE
		mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION, "AudioSelectionFragment onCallMeClicked() - User clicked Dial out", null, null,
				true, false)
		if (mIsUseHtml5) {
			if (mMeetingUserViewModel?.audioConnType === AudioType.DIAL_OUT
					&& mMeetingUserViewModel?.audioConnState !== AudioStatus.DISCONNECTED) {
				mAudioSelectionCallbacks?.resumeActivity()
			} else if (mMeetingUserViewModel?.audioConnType !== AudioType.DIAL_OUT) {
				setAdditionalAudioBehaviour()
				mAudioSelectionCallbacks?.handleCallMyPhoneClick()
			}
		} else {
			if (ConferenceManager.getConnectionType() == ConnectionType.DIAL_OUT
					&& ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
				//do nothing just go back to meeting room
				mAudioSelectionCallbacks?.resumeActivity()
			} else {
				setAdditionalAudioBehaviour()
				mAudioSelectionCallbacks?.handleCallMyPhoneClick()
			}
		}
	}

	fun startVoipMeeting() {
		mLogger.startMetric(LogEvent.METRIC_VOIP_CONNECT)
		mLogger.startMetric(LogEvent.METRIC_SIP_CONNECT)
		mLogger.record(LogEvent.FEATURE_VOIP)
		setAdditionalAudioBehaviour()
		mAudioSelectionCallbacks?.startVoipMeeting()
	}

	private fun setAdditionalAudioBehaviour() {
        val isMuteMe = chkMuteMe.isChecked
		val isActivateSpeaker = chkActivateSpeaker.isChecked();
        mAudioSelectionCallbacks?.setAudioBehaviour(isMuteMe, isActivateSpeaker)
    }

	@OnClick(R.id.btn_dial_in)
	fun onDialInClicked() {
		mLinearViewSpeakerLevel?.visibility = View.INVISIBLE
		mLogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.AUDIOSELECTION, "AudioSelectionFragment onDialInClicked() - User clicked Dial in", null, null,
				true, false)
		setAdditionalAudioBehaviour()
		checkDialInPermission()
	}

	@OnClick(R.id.btn_back)
	fun onCloseButtonClicked() {
		getActivity()?.setRequestedOrientation((ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ));
		mAudioSelectionCallbacks?.closeAudioPanel()
	}

	@OnClick(R.id.btn_donot_connect)
	fun onDoNotConnectAudioClicked() {
		mAudioSelectionCallbacks?.handleDoNotConnectAudio()
	}

	fun enableVoIPButton() {
		btnStartVoIP.alpha = AppConstants.ALPHA_VOIP_BTN_ENABLED
		btnStartVoIP.isEnabled = true
	}

	fun disableVoIPButton() {
		btnStartVoIP.alpha = AppConstants.ALPHA_VOIP_BTN_DISABLED
		btnStartVoIP.isEnabled = false
	}

	fun enableDialInButton() {
		btnDialIn.visibility = View.VISIBLE
		btnDialIn.alpha = AppConstants.ALPHA_VOIP_BTN_ENABLED
		btnDialIn.isEnabled = true
	}

	fun disableDialInButton() {
		// Hide dial-in button in Connect Audio Panel if dial-in is not available
		btnDialIn.visibility = View.GONE
	}

	override fun onNeedPermission(permission: String) {
		mAudioSelectionCallbacks?.onNeedPermission(permission)
	}

	override fun onPermissionPreviouslyDenied(permission: String) {
		mAudioSelectionCallbacks?.onPermissionPreviouslyDenied(permission)
	}

	override fun onPermissionDisabled(permission: String) {
		// Currently re-requesting permission
	}

	override fun onPermissionGranted(permission: String) {
		when (permission) {
			Manifest.permission.RECORD_AUDIO -> {
				mLogger.info(TAG, LogEvent.PERMISSION_MICROPHONE, LogEventValue.PERMISSION_GRANTED,
						"AudioSelectionFragment onPermissionGranted() - USer Granted Permission", null, null,
						true, false)
				recordAudioPermissionGranted = true
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
					mAudioSelectionCallbacks?.startSoftphone()
				}
			}
			Manifest.permission.FOREGROUND_SERVICE -> if (recordAudioPermissionGranted) {
				mAudioSelectionCallbacks?.startSoftphone()
			}
			Manifest.permission.CALL_PHONE ->  mAudioSelectionCallbacks?.handleDialIn("")
			else -> {
			}
		}
	}

	override fun onPermissionAllowedFromRationale(permission: String) {
		// Currently No implementation
	}

	override fun onPermissionDeniedFromRationale(permission: String) {
		// Currently No implementation
	}

	private fun checkDialInPermission() {
		PermissionUtil.checkPermission(WeakReference(activity), Manifest.permission.CALL_PHONE,
				this)
	}

	companion object {
		private val TAG = AudioSelectionFragment::class.java.simpleName
		private const val IS_FREEMIUM_ENABLED = "mIsFreemiumEnabled"
		private const val IS_DIALOUT_BLOCKED = "mIsDialOutBlocked"
		@JvmStatic
    fun newInstance(isVoipEnabled: Boolean = false,
										isDialInEnabled: Boolean = false,
										isActivatedSpeaker: Boolean = false,
										isMuted: Boolean = false,
                                        firstName: String?,
					                    mIsOwnRoom: Boolean,
						 				useHtml5: Boolean = false,
										isFreemiumEnabled: Boolean = false,
										isDialOutBlocked: Boolean = false): AudioSelectionFragment {
			val audioSelectionFragment = AudioSelectionFragment()
			val args = Bundle()
			args.putString("mFirstName", firstName)
			args.putBoolean("mIsOwnRoom", mIsOwnRoom)
			args.putBoolean("mIsVoipEnabled", isVoipEnabled)
			args.putBoolean("mIsDialInEnabled", isDialInEnabled)
			args.putBoolean("mIsMuted", isMuted)
			args.putBoolean("mIsActivateSpeaker", isActivatedSpeaker)
			args.putBoolean("mIsUseHtml5", useHtml5)
			args.putBoolean(IS_FREEMIUM_ENABLED, isFreemiumEnabled)
			args.putBoolean(IS_DIALOUT_BLOCKED, isDialOutBlocked)
			audioSelectionFragment.arguments = args
			return audioSelectionFragment
		}
	}
}