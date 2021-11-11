package com.pgi.convergencemeetings.meeting.gm4.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Strings
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.SoftphoneConstants
import com.pgi.convergence.enums.ConnectionState
import com.pgi.convergence.enums.ConnectionType
import com.pgi.convergence.enums.pia.PIAPartType
import com.pgi.convergence.enums.pia.PIAWebSocketCommand
import com.pgi.convergence.enums.pia.PIAWebSocketStatus
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergence.utils.PermissionUtil
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.models.*
import com.pgi.convergencemeetings.models.elkmodels.AttendeeModel
import com.pgi.convergencemeetings.models.enterUrlModel.MeetingRoomSearchResult_
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneNumberResult
import com.pgi.convergencemeetings.meeting.BaseMeetingPresenter
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.*
import com.pgi.convergencemeetings.services.*
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.*
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.SearchResult
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Presenter for MeetingRoomActivity, Presenter class communicates with services gets the data and pass to view to display the data.
 * Class retrieves the data from the Model, applies the UI logic and manages the state of the View,
 * decides what to display and reacts to user input notifications from the View.
 *
 * Presenter class handles all logic of getting meeting room events from web socket and pass to UI to display.
 *
 */
@ObsoleteCoroutinesApi
@UnstableDefault
class MeetingRoomPresenter(private val mView: MeetingRoomContract.view, private val mContext: Context) : BaseMeetingPresenter(mContext),
																																																				 MeetingRoomContract.presenter,
																																																				 PIAServiceCallbacks<BaseModel>,
																																																				 PIAWebSocketEventReceiver,
																																																				 UpdateLastUserPhoneServiceCallbacks<BaseModel>,
																																																				 ConferenceStateServiceCallbacks<BaseModel>,
																																																				 ConferenceSubscribeServiceCallbacks<BaseModel>,
																																																				 RecentMeetingServiceCallbacks<BaseModel>,
																																																				 PhoneNumberServiceCallbacks<BaseModel> {
	private val mlogger: Logger = CoreApplication.mLogger
	private val mConferenceStateCache: ConferenceStateCache
	private val mSharedPreferencesManager: SharedPreferencesManager
	private val mAttendeeModel: AttendeeModel
	private val mLeaveAudioMeetingService: LeaveAudioMeetingService = LeaveAudioMeetingService(this)
	private val mSubscribeAudioMeetingService: SubscribeAudioMeetingService = SubscribeAudioMeetingService(this)
	private val mUnSubscribeAudioMeetingService: UnSubscribeAudioMeetingService = UnSubscribeAudioMeetingService(this)
	private val mConferenceStateService: ConferenceStateService = ConferenceStateService(this)
	private val mEndAudioMeetingService: EndAudioMeetingService = EndAudioMeetingService(this)
	private val mConferenceWatchService: ConferenceWatchService = ConferenceWatchService(this)
	private val mConferenceOptionService: ConferenceOptionService = ConferenceOptionService(this)
	private val mParticipantOptionService: ParticipantOptionService = ParticipantOptionService(this)
	private val mClearConferenceWatchService: ClearConferenceWatchService = ClearConferenceWatchService(this)
	private val mSetParticipantInfoService: SetParticipantInfoService = SetParticipantInfoService(this)
	private val mRecentMeetingService: RecentMeetingService = RecentMeetingService(this)
	private val mGetPhoneNumberService: GetPhoneNumberService = GetPhoneNumberService(this)
	private val mStartAudioMeetingService: StartAudioMeetingService = StartAudioMeetingService(this)
	private val mDialOutService: DialOutService = DialOutService(this)
	private val mUpdateLastUsedPhoneService: UpdateLastUsedPhoneService = UpdateLastUsedPhoneService(this)
	private val mPiaWebSocketWrapper: PIAWebSocketWrapper = PIAWebSocketWrapper(this)
	private val mKeepAliveHandler: Handler = Handler()
	private val mDialInTimeoutHandler: Handler = Handler()
	private var mConferenceId: String? = null
	private var mIsLeaveOrEndMeeting = false
	private var mIsFirstSetPartCall = true
	private var mIsMuteAllFlow = false
	private var mKeepAliveRunnable: Runnable? = null
	private var mSessionId: String? = null
	private var mFinalizer: Runnable? = null
	private var mParticipantId: String? = null
	private var mIsFirstCall = true
	private val mAppAuthUtils: AppAuthUtils
	private var mDataSet: ClientInfoDataSet? = null
	private var mFormattedSipUri: String? = null
	private var mPhoneNumberList: ArrayList<Phone> = ArrayList()
	private var mRegisteredUserId: String? = null
	private var mIsStartMeetingFlow = false
	private var mCountryCode: String? = null
	private var mPhoneNumber: String? = null
	private var mExtension: String? = null
	private var mReasonFailed = 0
	private var mIsMeetingHost = false
	
	override fun onSubscribeSuccess(sessionId: String) {
		Log.d("MEETINGROOM", sessionId)
		mSessionId = sessionId
		CommonUtils.setPrefSessionId(mContext, sessionId)
		if (mConferenceId != null) {
			mConferenceStateService.getConferenceState(sessionId, mConferenceId)
		}
	}

	override fun onSubscribeError(errMsg: String, response: Int) {
		mReasonFailed = RetryStatus.PIA_SUBSCRIBE
		sendFailureResponse(response)
	}

	override fun subscribeToMeeting(confId: String) {
		if (mDataSet != null) {
			mAttendeeModel.userid = mDataSet?.clientId
		}
		mAttendeeModel.firstname = mAppAuthUtils.firstName
		mAttendeeModel.lastname = mAppAuthUtils.lastName
		mAttendeeModel.email = mAppAuthUtils.emailId
		mConferenceId = confId
		if (mIsMeetingHost) {
			mAttendeeModel.role = AppConstants.HOST
			startAuthorizationFlow()
		} else {
			mAttendeeModel.role = AppConstants.GUEST
			othersClientInfo
		}
	}

	//Starts the Authorization service to subscribe the user to join a GM4 meeting rooms.
	private fun startAuthorizationFlow() {
		mIsStartMeetingFlow = true
		val sessionID = CommonUtils.getPrefSessionId(mContext)
		if (sessionID != null) {
			//unsubscribe
			mUnSubscribeAudioMeetingService.unSubscribeToAudioMeeting(sessionID)
		} else {
			//subscribe
			initiateSubcriptionFlow()
		}
	}

	//Getting the client info of other's user to get the meeting room information.
	private val othersClientInfo: Unit
		get() {
			mRecentMeetingService.getOthersClientInfo(mConferenceId)
		}

	//Starts the Authorization service to subscribe the user to join a GM4 meeting rooms.
	private fun initiateSubcriptionFlow() {
		val accessToken = AppAuthUtils.getInstance().accessToken
		if (mDataSet != null) {
			val clientId = mDataSet?.clientId
			mSubscribeAudioMeetingService.subscribeToAudioMeeting(accessToken, clientId)
		}
	}

	override fun onConferenceStateSuccess(conferenceStateModel: ConferenceStateModel) {
		if (!getMeetingLockedStatus(conferenceStateModel)) {
			if (mConferenceId != null) {
				//enable disable voip button
				updateVoIPState()
				//enable disable dial=in button
				updateDialInState()
				mSessionId?.let { bindWebSocket(it) }
			}
		} else {
			mView.showMeetingLockedScreen()
		}
	}

	override fun onConferenceStateFailure(response: Int) {
		mReasonFailed = RetryStatus.PIA_GET_CONFERENCE_STATE
		sendFailureResponse(response)
	}

	override fun onConferenceStateError(message: String, response: Int) {
		Log.d(MeetingRoomPresenter::class.java.simpleName, message)
		mReasonFailed = RetryStatus.PIA_GET_CONFERENCE_STATE
		sendFailureResponse(response)
	}

	//Enable or disable the VOIP option on Audio selection screen.
	private fun updateVoIPState() {
		val sipUri: String? = sipUri
		if (sipUri != null) {
			//VoIP available
			mView.enableVoIPButton()
		} else {
			//VoIP not available
			mView.disableVoIPButton()
		}
	}

	//Enable or disable the Dial-In option on Audio selection screen.
	private fun updateDialInState() {
		var phoneNumberAvailable = false
		if (mDataSet != null) {
			phoneNumberAvailable = mDataSet?.phoneNumberAvailable ?: false
		}
		if (phoneNumberAvailable) {
			//Phone number available
			mView.enableDialInButton()
		} else {
			//Phone number is not available
			mView.disableDialInButton()
		}
	}

	private val sipUri: String
		get() = mDataSet?.sipUriFromClientInfo ?: ""

	override fun onConferenceWatchSuccess() {
		CoreApplication.mLogger.endMetric(LogEvent.METRIC_JOIN_GM4_MEETING, "GM4 Room join time " +
				"in seconds")
		CoreApplication.mLogger.record(LogEvent.FEATURE_GM4MEETINGS)
		mView.showAudioSelectionView()
		updateRecentlyJoinedMeeting()
		mView.showActiveMeetingNotification()
	}

	override fun onConferenceWatchError(resultText: String?, response: Int) {
		clearSocket()
		mReasonFailed = RetryStatus.PIA_CLEAR_CONFERENCE_WATCH
		sendFailureResponse(response)
	}

	override fun startVoipMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean) {
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP
				&& ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			return
		}
		mAttendeeModel.audioconnectiontype = ConnectionType.VOIP.toString()
		ConferenceManager.setIsMeetinActive(false)
		val sipUri: String? = sipUri
		val passcode: String? = passcode
		val confId: String? = confId
		if (sipUri == null || passcode == null || confId == null) {
			Toast.makeText(mContext, R.string.voip_not_enabled, Toast.LENGTH_LONG).show()
			return
		}
		mFormattedSipUri = CommonUtils.createSipUri(sipUri, passcode, mContext)
		ConferenceManager.updateAudioConnectionState(ConnectionType.VOIP, ConnectionState.DISCONNECTED)
		mFormattedSipUri?.let {
			mView.connectSoftPhone(it)
		}
	}

	override fun cleanUpMeetingState() {
		clearConferenceWatch()
	}

	private val passcode: String
		get() = mDataSet?.passcode ?: ""


	private val confId: String
		get() = mDataSet?.conferenceId ?: ""

	override fun onParticipantOptionSuccess() {
		mView.enableMuteButton()
	}

	override fun onParticipantOptionError(resultText: String?, response: Int) {
		mView.hideLoadingScreen()
		mView.enableMuteButton()
		mReasonFailed = RetryStatus.PIA_SET_PARTICIPANT_OPTION
		sendFailureResponse(response)
	}

	override fun onClearConfereceWatchError(message: String, response: Int) {
		mView.hideLoadingScreen()
		mIsStartMeetingFlow = false
		cleanUpMeetingSubscription()
		mReasonFailed = RetryStatus.PIA_CLEAR_CONFERENCE_WATCH
		sendFailureResponse(response)
	}

	override fun onClearConfereceWatchSuccess() {
		mIsStartMeetingFlow = false
		cleanUpMeetingSubscription()
	}

	override fun onParticipantInfoSuccess() {
		mIsFirstSetPartCall = false
	}

	override fun onParticipantInfoError(resultText: String?, response: Int) {
		mReasonFailed = RetryStatus.PIA_SET_PARTICIPANT_INFO
		sendFailureResponse(response)
	}

	override fun onHangupError(errorMsg: String, response: Int) {
		mReasonFailed = RetryStatus.PIA_HANGUP
		sendFailureResponse(response)
	}

	override fun onUnSubscribeSuccess() {
		if (mIsStartMeetingFlow) {
			initiateSubcriptionFlow()
		} else {
			ConferenceManager.setIsMeetinActive(false)
			ConferenceManager.resetConnectionState(true)
			mSharedPreferencesManager.removePrefSessionId()
			mView.hideLoadingScreen()
			ConferenceManager.setMeetingHook(null)
			mIsStartMeetingFlow = false
			stopDialInTimeout()
			mView.finishActivity()
		}
	}

	override fun onUnSubscribeError(errMsg: String, response: Int) {
		if (mIsStartMeetingFlow && errMsg.equals(AppConstants.INVALID_SESSION_ID, ignoreCase = true)) {
			mSharedPreferencesManager.removePrefSessionId()
			initiateSubcriptionFlow()
		} else {
			ConferenceManager.setIsMeetinActive(false)
			ConferenceManager.resetConnectionState(true)
			mIsStartMeetingFlow = false
			mView.hideLoadingScreen()
			mView.finishActivity()
		}
		mReasonFailed = RetryStatus.PIA_UNSUSCRIBE
		sendFailureResponse(response)
	}

	override fun onUnSubscribeFailure(response: Int) {
		if (mIsStartMeetingFlow) {
			mIsStartMeetingFlow = false
			mView.finishActivity()
		}
		mReasonFailed = RetryStatus.PIA_UNSUSCRIBE
		sendFailureResponse(response)
	}

	override fun onSubscribeFailure(response: Int) {
		mView.finishActivity()
		mReasonFailed = RetryStatus.PIA_SUBSCRIBE
		sendFailureResponse(response)
	}

	override fun onStartConferenceSuccess() {
		ConferenceManager.setIsMeetinActive(true)
		dialOutPhoneNumber(mCountryCode, mPhoneNumber, mExtension)
	}

	override fun onStartConferenceError(errorMsg: String, response: Int) {
		mReasonFailed = RetryStatus.PIA_START_CONFERENCE
		sendFailureResponse(response)
	}

	override fun onEndConferenceSuccess() {
		mConferenceStateCache.clearConferenceState()
		mClearConferenceWatchService.clearConferenceWatch(mSessionId, mConferenceId)
	}

	override fun onClientInfoSuccess(clientInfoResponse: ClientInfoResponse) {
		try {
			mDataSet?.firstName?.let { mView.setFirstName(it) }
			startAuthorizationFlow()
		} catch (ex: Exception) {
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_CLIENTINFO, "MeetingRoomPresenter Got " +
					"Exception on ClinetInfo Success", ex, null, true, false)
		}
	}

	override fun onRecentMeetingErrorCallback(errorMsg: String, response: Int) {
		//This is background task, No need to update UI in any case, Added Log only for debugging purpose.
		mReasonFailed = RetryStatus.FAILED
		sendFailureResponse(response)
	}

	override fun onClientInfoError(errorMsg: Int, response: Int) {
		mReasonFailed = RetryStatus.WS_CLIENT_INFO_OTHERS
		sendFailureResponse(response)
	}

	override fun onRecentMeetingSuccessCallback() {
		var tempVal = java.lang.Boolean.FALSE
		val applicationDao = ApplicationDao.get(CoreApplication.appContext)
		val searchResults = applicationDao.allRecentMeetings
		val meetingRoomData = ClientInfoResultCache.getInstance().meetingRoomData
		var desktopId: Int? = null
		if (meetingRoomData != null) {
			for (searchResult in searchResults) {
				if (searchResult.hubConfId != null && searchResult.hubConfId.toString() == meetingRoomData.meetingRoomId) {
					desktopId = searchResult.desktopMeetingId
					tempVal = java.lang.Boolean.TRUE
					break
				}
			}
			if (tempVal) {
				//update
				updateMeeting(desktopId.toString())
			} else {
				//fetch, create and update
				fetchMeetingRoom(meetingRoomData.meetingRoomId)
			}
		}
	}

	override fun onMeetingRoomSearchSuccess(meetingRoomSearchResult_: MeetingRoomSearchResult_) {
		val resultList = meetingRoomSearchResult_.results
		if (resultList.isNotEmpty()) {
			val result = resultList[0]
			if (result != null) {
				val detail = result.detail
				if (detail != null) {
					mConferenceId = detail.conferenceId.toString()
				}
			}
		}
	}

	override fun onDialParticipantSuccess(participantId: String) {
		//        mlogger.info(TAG, ELKConstants.ROOMEVENT_DIALOUT_SUCCESS, ELKConstants.GM4_VALUE);
		val extension = mExtension
		val countryCode = mCountryCode
		val phoneNumber = mPhoneNumber
		val clientId = ConferenceManager.getClientId()
		if (mAppAuthUtils.isUserTypeGuest && phoneNumber != null) {
			updateLastUsedPhoneInDb(countryCode, phoneNumber)
		} else if (clientId != null && phoneNumber != null) {
			mUpdateLastUsedPhoneService.updateLastUsedPhoneNumber(clientId, false, countryCode, phoneNumber, extension)
		}
		mParticipantId = participantId
	}

	//get from database
	override val dialOutPhoneNumbers:
	//verify is this code is working fine
			Unit
		get() {
			val clientId = ClientInfoDaoUtils.getInstance().clientId
			ConferenceManager.setClientId(clientId)
			if (mAppAuthUtils.isUserTypeGuest) {
				//get from database
				val applicationDao = ApplicationDao.get(CoreApplication.appContext)
				val phoneDao = applicationDao.phoneNumbers
				//verify is this code is working fine
				val phones = phoneDao.loadAll()
				phones.reverse()
				mView.phoneNumbersReceivedSuccessfully(phones)
			} else {
				mGetPhoneNumberService.getPhoneNumber(clientId)
			}
		}

	//Update the last Dial-In phone number in the database
	private fun updateLastUsedPhoneInDb(countryCode: String?, phoneNumber: String) {
		//Store into database
		val numberToBeAdded = countryCode + phoneNumber
		val applicationDao = ApplicationDao.get(CoreApplication.appContext)
		val phoneDao = applicationDao.phoneNumbers
		val phoneList = phoneDao.loadAll()
		if (phoneList.isNotEmpty()) {
			for (phoneInfo in phoneList) {
				val cntryCode = phoneInfo.countryCode
				val phnNumber = phoneInfo.number
				val existingNumber = cntryCode + phnNumber
				if (existingNumber.equals(numberToBeAdded, ignoreCase = true)) {
					phoneDao.deleteByKey(phoneInfo.id)
					break
				}
			}
		}
		val phone = Phone()
		phone.countryCode = countryCode
		phone.number = phoneNumber
		//insert
		phoneDao.insert(phone)
	}

	override fun onDialParticipantError(errorMsg: String, response: Int) {
		//        mlogger.info(TAG, ELKConstants.ROOMEVENT_DIALOUT_FAILURE, ELKConstants.GM4_VALUE);
		Toast.makeText(mContext, "Dial out error, finishing meeting", Toast.LENGTH_SHORT).show()
		clearConferenceWatch()
		mReasonFailed = RetryStatus.FAILED
		sendFailureResponse(response)
	}

	override fun onClearConfereceWatchFailure() {}
	override fun onEndConferenceError(errorMsg: String, response: Int) {
		mConferenceStateCache.clearConferenceState()
		ConferenceManager.setIsMeetinActive(false)
		mView.finishActivity()
		mReasonFailed = RetryStatus.PIA_END_CONFERENCE
		sendFailureResponse(response)
	}

	override fun onLeaveConferenceSuccess() {
		mConferenceStateCache.clearConferenceState()
		mClearConferenceWatchService.clearConferenceWatch(mSessionId, mConferenceId)
		updateRecentlyJoinedMeeting()
	}

	override fun onLeaveConferenceError(resultText: String?, response: Int) {
		mConferenceStateCache.clearConferenceState()
		mClearConferenceWatchService.clearConferenceWatch(mSessionId, mConferenceId)
		ConferenceManager.setIsMeetinActive(false)
		mView.finishActivity()
		mReasonFailed = RetryStatus.FAILED
		sendFailureResponse(response)
	}

	override fun onConferenceOptionSuccess() {
		if (mIsMuteAllFlow) {
			mView.enableMuteAllButton()
		}
	}

	override fun onConferenceOptionError(resultText: String?, response: Int) {
		if (mIsMuteAllFlow) {
			mView.enableMuteAllButton()
		}
		mReasonFailed = RetryStatus.PIA_SET_CONFERENCE_OPTION
		sendFailureResponse(response)
		Toast.makeText(mContext, R.string.failed_to_mute_all, Toast.LENGTH_LONG).show()
	}

	override fun startConference(conferenceId: String) {
		mConferenceId = conferenceId
		mSessionId = CommonUtils.getPrefSessionId(mContext)
		mSessionId?.let { sessionId ->
			mConferenceId?.let { confId ->
				startConferenceWatch(sessionId, confId)
			}
		}
	}

	override fun startConferenceWatch(sessionId: String, conferenceId: String) {
		mConferenceWatchService.setConferenceWatch(sessionId, conferenceId)
	}

	override fun endConference() {
		mView.openFeedbackFragment()
		mIsLeaveOrEndMeeting = true
		mIsStartMeetingFlow = false
		mAttendeeModel.resetAttendeeInfo()
		ElkTransactionIDUtils.resetTransactionId()
		mEndAudioMeetingService.endAudioMeeting(mSessionId, mConferenceId)
	}

	override fun leaveConference(participantId: String) {
		mView.openFeedbackFragment()
		mIsLeaveOrEndMeeting = true
		mIsStartMeetingFlow = false
		mAttendeeModel.resetAttendeeInfo()
		ElkTransactionIDUtils.resetTransactionId()
		mLeaveAudioMeetingService.leaveAudioMeeting(mSessionId, mConferenceId, participantId)
	}

	override fun startDialInMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, phoneNumber: String) {
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP && ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			ConferenceManager.disconnectVoipConnection()
		}
		mAttendeeModel.audioconnectiontype = ConnectionType.DIAL_IN.toString()
		ConferenceManager.setIsMeetinActive(false)
		val passcode: String? = passcode
		val confId: String? = confId
		if (passcode == null || confId == null) {
			Toast.makeText(mContext, R.string.dial_in_not_enabled, Toast.LENGTH_LONG).show()
			return
		}
		val formattedPhoneNumber = CommonUtils.getPGIFormattedNumber(phoneNumber, passcode)
		dialPhoneNumber(formattedPhoneNumber)
	}

	//Append the meeting passcode with phone number in seprated by comma and * and launch phone dialler to dial the number.
	private fun dialPhoneNumber(formattedPhoneStr: String) {
		try {
			val utfFormattedPhoneStr = URLEncoder.encode(AppConstants.PLUS_SYMBOL + formattedPhoneStr, AppConstants.UTF_8)
			(mContext as MeetingRoomActivity).runOnUiThread {
				startDialInTimeout()
				val intent = Intent(Intent.ACTION_CALL)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				intent.data = Uri.parse(AppConstants.TEL_SYMBOL + utfFormattedPhoneStr)
				mContext.startActivity(intent)
				Toast.makeText(mContext, AppConstants.CALLING_TEXT + formattedPhoneStr, Toast.LENGTH_LONG).show()
			}
		} catch (e: SecurityException) {
			val layout = (mContext as Activity).findViewById<View>(R.id.ll_home)
			Snackbar.make(layout, R.string.permission_call_rationale,
					Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.str_ok) { PermissionUtil.showAppNativeSettings(WeakReference(mContext)) }.show()
		} catch (e: Exception) {
			Log.e(MeetingRoomPresenter::class.java.name, AppConstants.ERROR_TXT + e.message)
		}
	}

	//Make a request for Dial-out passing the phone number with country code and extention if exists.
	private fun dialOutPhoneNumber(countryCode: String?, phoneNumber: String?, extension: String?) {
		var phoneNumberToDial: String? = countryCode + phoneNumber
		if (!Strings.isNullOrEmpty(extension)) {
			phoneNumberToDial += extension
		}
		mAttendeeModel.audioconnectiontype = ConnectionType.DIAL_OUT.toString()
		val firstName = mAppAuthUtils.firstName
		val lastName = mAppAuthUtils.lastName
		val isMeetingHost = ConferenceManager.isMeetingHost()
		val partType: String
		partType = if (isMeetingHost) {
			PIAPartType.MODERATOR.toString()
		} else {
			PIAPartType.NORMAL.toString()
		}
		if (ConferenceManager.isMeetingActive()) {
			mDialOutService.dialOutToParticipant(mSessionId, mConferenceId, phoneNumberToDial, partType, AppConstants.DIAL_OUT_TYPE_INVITE_PART, firstName, lastName)
		} else {
			mStartAudioMeetingService.startAudioMeeting(mSessionId, mConferenceId)
		}
	}

	private fun startDialInTimeout() {
		mFinalizer = Runnable { cleanUpMeetingSubscription() }
		mDialInTimeoutHandler.postDelayed(mFinalizer, WS_KEEP_ALIVE_TIME_INTERVAL + 10000L)
	}

	private fun cleanUpMeetingSubscription() {
		clearSocket()
		mConferenceStateCache.clearConferenceState()
		mUnSubscribeAudioMeetingService.unSubscribeToAudioMeeting(mSessionId)
	}

	private fun stopDialInTimeout() {
		if (mFinalizer != null) {
			mDialInTimeoutHandler.removeCallbacks(mFinalizer)
		}
	}

	override fun clearConferenceWatch() {
		mAttendeeModel.resetAttendeeInfo()
		ElkTransactionIDUtils.resetTransactionId()
		if (mSessionId != null && mConferenceId != null) {
			mClearConferenceWatchService.clearConferenceWatch(mSessionId, mConferenceId)
		} else {
			ConferenceManager.setIsMeetinActive(false)
			mView.finishActivity()
		}
	}

	override fun createSipFromAddress(): String {
		var sipFromAddress = ""
		if (mSessionId != null) {
			sipFromAddress = (SoftphoneConstants.SIP_COLON + SoftphoneConstants.GM
					+ SoftphoneConstants.DOT + mSessionId + SoftphoneConstants.PGI_COM)
		}
		return sipFromAddress
	}

	private fun setConferenceOptionTalker(optionState: Boolean) {
		mIsMuteAllFlow = false
		if (optionState) { //switch on talker
			mConferenceOptionService.setConferenceOption(mSessionId, mConferenceId, AppConstants.TALKER_NOTIFY_ON)
		} else { //switch off talker
			mConferenceOptionService.setConferenceOption(mSessionId, mConferenceId, AppConstants.TALKER_NOTIFY_OFF)
		}
	}

	private fun setConferenceOptionMute(optionState: Boolean) {
		mIsMuteAllFlow = true
		mView.disableMuteAllButton()
		if (optionState) { //switch canMute all on
			mConferenceOptionService.setConferenceOption(mSessionId, mConferenceId, AppConstants.CONF_MUTE_ON)
		} else { //switch canMute all off
			mConferenceOptionService.setConferenceOption(mSessionId, mConferenceId, AppConstants.CONF_MUTE_OFF)
		}
	}

	private fun setParticipantOptionMute(optionState: Boolean, participantId: String) {
		mView.disableMuteButton()
		if (optionState) { //switch canMute all on
			mParticipantOptionService.setParticipantOption(mSessionId, mConferenceId, participantId, AppConstants.PART_MUTE_ON)
		} else { //switch canMute all off
			mParticipantOptionService.setParticipantOption(mSessionId, mConferenceId, participantId, AppConstants.PART_MUTE_OFF)
		}
	}

	override fun prepareParticipantsData(meetingParticipantList: List<MeetingParticipant>, meetingParticipant: MeetingParticipant): List<MeetingParticipant> {
		val tempMeetingParticipantList: MutableList<MeetingParticipant> = ArrayList()
		var newParticipantAdded = false
		if (!meetingParticipantList.isEmpty()) {
			for (participant in meetingParticipantList) {
				if (participant.partID != null && participant.partID == meetingParticipant.partID) {
					if (meetingParticipant.connected) {
						newParticipantAdded = true
						tempMeetingParticipantList.add(meetingParticipant)
					}
				} else {
					tempMeetingParticipantList.add(participant)
				}
			}
			if (meetingParticipant.connected && !newParticipantAdded) {
				tempMeetingParticipantList.add(meetingParticipant)
			}
		} else {
			tempMeetingParticipantList.add(meetingParticipant)
		}
		Collections.sort(tempMeetingParticipantList)
		return tempMeetingParticipantList
	}

	override fun getActiveTalkerParticipant(mMeetingParticipants: List<MeetingParticipant>, s: String): MeetingParticipant? {
		var meetingParticipant: MeetingParticipant? = null
		if (mMeetingParticipants.isNotEmpty()) {
			for (participant in mMeetingParticipants) {
				if (participant.partID != null && participant.partID == s) {
					meetingParticipant = participant
					break
				}
			}
		}
		return meetingParticipant
	}

	override fun handleMuteButtonClick(isMuted: Boolean) {
		val myPartInfo = mConferenceStateCache.myPartInfo
		if (myPartInfo != null) {
			val participantId = myPartInfo.partID
			participantId?.let { setParticipantOptionMute(isMuted, it) }
		}
	}

	override fun handleMuteAllClick(isMuteAll: Boolean) {
		setConferenceOptionMute(isMuteAll)
	}

	override fun onParticipantInfoReceived(part: MeetingParticipant) {
		//part options
		if (mSessionId != null) {
			Log.d(MeetingRoomPresenter::class.java.name, "" + part.ani)
			handleParticipantInfoMessage(part)
			if (mIsFirstSetPartCall) {
				mView.updateMuteOnEntry()
			}
		}
	}

	override fun onConferenceInfoReceived(conferenceInfo: ConferenceInfo) {
		//active inactive conf state
		Log.d(MeetingRoomPresenter::class.java.name, conferenceInfo.confID)
		val meetingActiveState = conferenceInfo.active
		ConferenceManager.setIsMeetinActive(meetingActiveState)
		val participantList = mView.meetingParticipants
		if (meetingActiveState && !conferenceInfo.talkerNotify) {
			//set active talker here
			setConferenceOptionTalker(true)
		} else if (!meetingActiveState && !mIsLeaveOrEndMeeting) {
			mIsLeaveOrEndMeeting = false
			mView.stopSoftPhone()
			if (!participantList.isEmpty()) {
				val participant = mConferenceStateCache.myPartInfo
				if (participant != null) {
					leaveConference(participant.partID)
				}
			} else {
				mClearConferenceWatchService.clearConferenceWatch(mSessionId, mConferenceId)
			}
		}
	}

	override fun onConferenceStateReceived(conferenceState: ConferenceState) {
		Log.d(MeetingRoomPresenter::class.java.name, conferenceState.confID)
		//update part list
		mConferenceStateCache.conferenceState = conferenceState
		val meetingParticipants = conferenceState.meetingParticipants
		val meetinActiveState = conferenceState.active
		if (meetinActiveState && !conferenceState.talkerNotify) {
			//set active talker here
			setConferenceOptionTalker(true)
		}
		if (meetingParticipants != null && meetingParticipants.isNotEmpty()) {
			for (participant in meetingParticipants) {
				if (participant != null && participant.firstName != null && participant.lastName != null) {
					if (participant.firstName != AppConstants.EMPTY_STRING && participant.lastName != AppConstants.EMPTY_STRING) {
						mView.updateParticipants(participant)
					}
				}
			}
		}
	}

	override fun onTalkerStateReceived(talker: Talker) {
		Log.d(MeetingRoomPresenter::class.java.name, talker.confID)
		mView.updateActiveTalker(talker)
	}

	override fun onWatchLost(watchLost: WatchLost) {
		Log.d(MeetingRoomPresenter::class.java.name, watchLost.confID)
		//set watch again
	}

	override fun onCommandReceived(piaWebSocketCommandMessageACK: PIAWebSocketCommandMessageACK) {
		Log.d(MeetingRoomPresenter::class.java.name, piaWebSocketCommandMessageACK.piasid)
		validateAndStartConference(piaWebSocketCommandMessageACK)
	}

	private fun bindWebSocket(sessionId: String) {
		val piaWebSocketCommandMessage = PIAWebSocketCommandMessage()
		piaWebSocketCommandMessage.command = PIAWebSocketCommand.BIND
		piaWebSocketCommandMessage.id = 0
		piaWebSocketCommandMessage.piasid = sessionId
		mPiaWebSocketWrapper.invokeSocket(piaWebSocketCommandMessage)
		invokeSocketKeepAlive(sessionId)
	}

	private fun unbindWebSocket(sessionId: String) {
		val piaWebSocketCommandMessage = PIAWebSocketCommandMessage()
		piaWebSocketCommandMessage.command = PIAWebSocketCommand.UNBIND
		piaWebSocketCommandMessage.id = 0
		piaWebSocketCommandMessage.piasid = sessionId
		mPiaWebSocketWrapper.unbindSocket(piaWebSocketCommandMessage)
	}

	private fun keepAliveWebSocket(sessionId: String) {
		val piaWebSocketCommandMessage = PIAWebSocketCommandMessage()
		piaWebSocketCommandMessage.command = PIAWebSocketCommand.KEEPALIVE
		piaWebSocketCommandMessage.id = 0
		piaWebSocketCommandMessage.piasid = sessionId
		mPiaWebSocketWrapper.keepAlive(piaWebSocketCommandMessage)
	}

	private fun invokeSocketKeepAlive(sessionId: String) {
		mKeepAliveRunnable = Runnable {
			keepAliveWebSocket(sessionId)
			mKeepAliveHandler.postDelayed(mKeepAliveRunnable, WS_KEEP_ALIVE_TIME_INTERVAL.toLong())
		}
		mKeepAliveHandler.post(mKeepAliveRunnable)
	}

	override fun clearSocket() {
		mKeepAliveHandler.removeCallbacks(mKeepAliveRunnable)
		mSessionId?.let {
			unbindWebSocket(it)
		}
	}

	private fun handleParticipantInfoMessage(participant: MeetingParticipant) {
		stopDialInTimeout()
		if (checkMyIsPartInfo(participant, mContext, mParticipantId)) {
			mAttendeeModel.dnis = participant.dnis
			mAttendeeModel.participantid = participant.partID
			mView.setUserMuted(participant.mute)
			mView.hideLoadingScreen()
			mView.showActiveMeetingNotification()
			if (participant.isParticipantNameAvailable) {
				if (mIsFirstCall) {
					mView.updateMuteOnEntry()
					mIsFirstCall = false
				}
				updateMyParticipantInfoCache(participant)
				mView.updateParticipants(participant)
			} else {
				validateAndSetParticipantInfo(participant)
			}
		} else {
			if (participant.isParticipantNameAvailable) {
				mView.updateParticipants(participant)
				mView.setAllMuted(participant.mute)
			} else {
				if (participant.dnis != null && !participant.ani.contains(SoftphoneConstants.GM)) {
					if (participant.connected) {
						ConferenceManager.updateAudioConnectionState(ConnectionType.DIAL_IN, ConnectionState.CONNECTED)
						ConferenceManager.setIsMeetinActive(true)
					}
					mView.hideLoadingScreen()
					mView.showActiveMeetingNotification()
					participant.firstName = participant.ani
					mView.updateParticipants(participant)
					mView.setUserMuted(participant.mute)
				}
			}
		}
	}

	private fun validateAndSetParticipantInfo(participant: MeetingParticipant) {
		if (mIsFirstSetPartCall) {
			mView.hideLoadingScreen()
			val firstName = mAppAuthUtils.firstName
			val lastName = mAppAuthUtils.lastName
			if (firstName != null && lastName != null) {
				val company = participant.company
				val phone = participant.phone
				val email = participant.email
				Log.d("new message received", "settingPArtInfo $firstName $lastName")
				mSetParticipantInfoService.setParticipantInfo(mSessionId, participant.confID,
						participant.partID, firstName, lastName, company, phone, email)
			}
		}
	}

	private fun updateMyParticipantInfoCache(part: MeetingParticipant) {
		mConferenceStateCache.myPartInfo = part
	}

	private fun validateAndStartConference(piaWebSocketCommandMessageACK: PIAWebSocketCommandMessageACK?) {
		if (piaWebSocketCommandMessageACK != null) {
			if (piaWebSocketCommandMessageACK.command == PIAWebSocketCommand.BIND
					&& piaWebSocketCommandMessageACK.status == PIAWebSocketStatus.SUCCESS.value) {
				mConferenceId?.let {
					startConference(it)
				}
			}
		}
	}

	/**
	 * Disconnect voip audio.
	 */
	fun disconnectVoipAudio() {
		mView.stopSoftPhone()
	}

	override fun onUpdateLastUsedPhoneNumberSuccess() {}
	override fun onUpdateLastUsedPhoneNumberError(errorMsg: String, response: Int) {
		mReasonFailed = RetryStatus.WS_LAST_USED_PHONE
		sendFailureResponse(response)
	}

	//Need to update the last joined meeting, For Guest we need to make a call to PGI web services
	// to update it on PGI server, But for guest user need to update it in the local database.
	private fun updateRecentlyJoinedMeeting() {
		val clientInfoResultCache = ClientInfoResultCache.getInstance()
		val clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
		if (!mView.isMeetingHost && clientInfoResultCache.clientId != null && clientInfoResultCache.clientId != clientInfoDaoUtils.clientId) {
			if (mAppAuthUtils.isUserTypeGuest) {
				//If user type is guest save the recently joined meeting in local database...
				val searchResult = clientInfoResultCache.searchResult
				val applicationDao = ApplicationDao.get(CoreApplication.appContext)
				if (searchResult != null) {
					val existingSearchResults = prepareSearchResultsList(applicationDao.allRecentMeetings, searchResult)
					val searchResultDao = applicationDao.searchResult
					//Remove all the entries from DB before inserting
					searchResultDao.deleteAll()
					searchResultDao.insertOrReplaceInTx(existingSearchResults)
				}
			} else {
				mRecentMeetingService.getRecentMeetingInfo(ClientInfoDaoUtils.getInstance().clientId)
			}
		}
	}

	private fun prepareSearchResultsList(searchResults: List<SearchResult>, newResult: SearchResult): List<SearchResult> {
		val tempSearchResults: MutableList<SearchResult> = ArrayList()
		//Remove the least recently used meeting logs if the previous recent meeting list size reached to 50.
		if (searchResults.size >= AppConstants.RECENT_MEETINGS_MAX_LIMIT) {
			searchResults.sorted()
			searchResults.dropLast(AppConstants.RECENT_MEETINGS_MAX_LIMIT - 1)
		}
		for (searchResult in searchResults) {
			if (searchResult.hubConfId != null && searchResult.hubConfId != newResult.hubConfId) {
				tempSearchResults.add(searchResult)
			}
		}
		tempSearchResults.add(newResult)
		return tempSearchResults
	}

	override fun setMeetingHostStatus(isMeetingHost: Boolean) {
		mIsMeetingHost = isMeetingHost
		mDataSet = if (isMeetingHost) {
			initClientInfoDaoCache()
			ClientInfoDaoUtils.getInstance()
		} else {
			ClientInfoResultCache.getInstance()
		}
	}

	override fun startDialOutMeeting(isMuteMe: Boolean, isActivateSpeaker: Boolean, isMeetingHost: Boolean, selectedPhoneNumber: Phone) {
		if (ConferenceManager.getConnectionType() == ConnectionType.VOIP && ConferenceManager.getConnectionState() == ConnectionState.CONNECTED) {
			ConferenceManager.disconnectVoipConnection()
		}
		val passcode: String? = passcode
		val confId: String? = confId
		if (passcode == null || confId == null) {
			Toast.makeText(mContext, R.string.dial_out_not_enabled, Toast.LENGTH_LONG).show()
			return
		}
		ConferenceManager.setIsMeetingHost(isMeetingHost)
		val countryCode = selectedPhoneNumber.countryCode
		val phoneNumber = selectedPhoneNumber.number
		val extension = selectedPhoneNumber.extension
		ConferenceManager.updateAudioConnectionState(ConnectionType.DIAL_OUT, ConnectionState.DISCONNECTED)
		mCountryCode = countryCode
		mPhoneNumber = phoneNumber
		mExtension = extension ?: AppConstants.EMPTY_STRING
		dialOutPhoneNumber(countryCode, phoneNumber, extension)
	}

	private fun initClientInfoDaoCache() {
		ClientInfoDaoUtils.getInstance().refereshClientInfoResultDao()
	}

	override fun onGetPhoneNumberSuccess(phoneNumberResult: PhoneNumberResult) {
		//using start conference call
		val registeredUserResult = phoneNumberResult.registeredUserGetResult
		if (registeredUserResult != null) {
			val phoneList = registeredUserResult.phones
			if (phoneList != null && !phoneList.isEmpty()) {
				phoneList.reverse()
				mPhoneNumberList = ArrayList(phoneList.size)
				mPhoneNumberList.addAll(phoneList)
			}
			val registeredUserId = registeredUserResult.registeredUserId
			if (registeredUserId != null) {
				mRegisteredUserId = registeredUserId.toString()
			} else if (mDataSet != null) {
				mRegisteredUserId = mDataSet?.clientId
			}
		}
		mView.phoneNumbersReceivedSuccessfully(mPhoneNumberList)
		ConferenceManager.setClientId(mRegisteredUserId)
	}

	override fun onGetPhoneNumberError(errorMsg: String, response: Int) {
		mReasonFailed = RetryStatus.WS_GET_PHONE_NUMBERS
		sendFailureResponse(response)
	}

	private fun sendFailureResponse(response: Int) {
		val mRetryStatus = RetryStatus(response, mReasonFailed)
		if (!response.toString().startsWith(AppConstants.CODE_4) && !response.toString().startsWith(AppConstants.CODE_2)) {
			mView.onServiceRetryFailed(mRetryStatus)
		}
	}

	companion object {
		private val TAG = MeetingRoomPresenter::class.java.simpleName
		private const val WS_KEEP_ALIVE_TIME_INTERVAL = 60000
		fun getMeetingLockedStatus(conferenceStateModel: ConferenceStateModel): Boolean {
			val conferenceState = conferenceStateModel.confState
			if (conferenceState != null) {
				val conferenceInfo = conferenceState.conferenceInfo
				if (conferenceInfo != null) {
					return conferenceInfo.locked
				}
			}
			return false
		}

		fun checkMyIsPartInfo(part: MeetingParticipant, context: Context?, participantId: String?): Boolean {
			//part options
			var result = false
			if (ConferenceManager.getConnectionType() == ConnectionType.DIAL_OUT) {
				if (participantId != null) {
					result = participantId.equals(part.partID, ignoreCase = true)
					if (part.connected) {
						ConferenceManager.updateAudioConnectionState(ConnectionType.DIAL_OUT, ConnectionState.CONNECTED)
					}
				} else {
					result = false
				}
			} else {
				val ani = part.ani
				if (ani != null && ani.contains(SoftphoneConstants.GM)) {
					val sessionId = CommonUtils.getPrefSessionId(context)
					val extractedSessId = ani.substring(ani.indexOf(SoftphoneConstants.DOT) + 1)
					if (extractedSessId.equals(sessionId, ignoreCase = true)) {
						result = true
					}
				}
			}
			return result
		}
	}

	/**
	 * Instantiates a new Meeting room presenter.
	 *
	 * @param view    the view
	 * @param context the context
	 */
	init {
		ConferenceManager.setMeetingHook(this)
		mSharedPreferencesManager = SharedPreferencesManager.getInstance()
		mConferenceStateCache = ConferenceStateCache.getInstance()
		mAppAuthUtils = AppAuthUtils.getInstance()
		mAttendeeModel = AttendeeModel.getInstance()
	}
}