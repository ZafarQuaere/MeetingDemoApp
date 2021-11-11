package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import PGi.Softphone.SWIG.*
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.pgi.convergence.annotations.OpenForTest
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

@OpenForTest
class PGiSoftPhoneCallBack(context: Context, response: PGiSoftPhone) : SoftphoneCallbacks() {
    private val TAG = PGiSoftPhoneCallBack::class.java.simpleName
    private val mLogger = CoreApplication.mLogger
    private var callBackHandler: Handler? = null
    private val softphoneResponse: PGiSoftPhone = response
    private var UIThreadHandler: Handler = Handler(Looper.getMainLooper())
    private var microphoneSignalProcessingActive: Boolean = false
    private var activeCall: SWIGTYPE_p_void? = null
    private val mContext = context
    private val handlerThread = HandlerThread("com.pgi.gmmeet.voip,callback")
    private var audioLostErrors = 0L
    var streamCreated: Boolean = false
    var totalRAGCount = 0;
    var redRAGCount = 0;


    /* A pjsip status of 70006 means there is no active audio on a SIP session.
       70006 errors occur when we change networks.  It is not unusual to see the error once or
       twice during a reconnect.  But we should not see more than two.  If we see more than
       it probably means we are on a slow network or a really bad connection.
     */
    private val NO_AUDIO = "status=70006"

    var isDolby = false

    fun init() {
      handlerThread.start()
      callBackHandler = Handler(handlerThread.looper)

    }

    var callState: CallState = CallState.INV_STATE_NULL

    override fun callStateCB(call: SWIGTYPE_p_void?, cs: CallState, context: Any) {
        activeCall = call
        callState = cs
        val msg = "callStateCB state=$cs"
        val err:IntArray = intArrayOf(0)
        val callInfo = PgiSoftphone.getCallInfo(call, err)
        mLogger.meetingState.meetingStateCallId = callInfo.sipCallId
        mLogger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, msg)
        Log.e(TAG, msg)
        if (cs == CallState.INV_STATE_CONFIRMED) {
            startMicrophoneSignalProcessing()
            UIThreadHandler.post {
              PGiSoftPhoneModel.softPhoneConnected.onNext(PGiSoftPhoneConstants.VOIP_CONNECTED)
            }
        } else if (cs == CallState.INV_STATE_DISCONNECTED) {
            if (!softphoneResponse.isAttemptingConnection()) {
                UIThreadHandler.post {
                    PGiSoftPhoneModel.softPhoneConnected.onNext(PGiSoftPhoneConstants.VOIP_DISCONNECTED)
                }
                stopMicrophoneSignalProcessing()
                 activeCall = null
            }
        }
        /*else if (cs == CallState.INV_STATE_NULL && activeCall != null) {
            mLogger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, " call state null on active call, treating like disconnect")
            UIThreadHandler.post {
                PGiSoftPhoneModel.softPhoneConnected.onNext(PGiSoftPhoneConstants.VOIP_DISCONNECTED)
            }
            stopMicrophoneSignalProcessing()
            stopQualityAnalysis()
        } */
    }

    /* leave for documentation
    override fun incomingCallCB(account: SWIGTYPE_p_void?, newCall: SWIGTYPE_p_void?, context: Any) {
        mLogger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "incoming Call")
        if (softphoneResponse != null) {
            Handler(Looper.getMainLooper()).post { softphoneResponse?.onIncomingCall(account, newCall) }
        } else {
            PgiSoftphone.answerCall(newCall, 200)
        }
    } */

    override fun loggingCB(arg0: String, context: Any) {
        if (arg0.contains("pjsua") || arg0.contains("tsx0x") || arg0.contains("tlsrel")) {
            mLogger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, arg0)
        } else {
            Log.d(TAG, arg0)
        }
        if (arg0.contains(NO_AUDIO)) {  // "status=70006" means there is no active audio
            audioLostErrors++
            mLogger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "70006 no active audio")
        } else {
            audioLostErrors = 0
        }
        /*
        if (audioLostErrors > 10L) {
            audioLostErrors = 0L
            mLogger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "too many 70006 errors, reconnecting")
            softphoneResponse.reconnect(true)
        } */
    }

    override fun registrationStateCB(call: SWIGTYPE_p_void?, regActive: Boolean, regCode: Int, context: Any) {
        val msg = "registrationStateCB: regActive=$regActive, code=$regCode"
        mLogger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, msg)
    }

    private fun startMicrophoneSignalProcessing() {
        microphoneSignalProcessingActive = true
        if (activeCall != null) {
            callBackHandler?.postDelayed(object : Runnable {
                private var lastMicData = 0
                override fun run() {
                    if (microphoneSignalProcessingActive && activeCall != null) {
                        val err: IntArray = intArrayOf(0)
                        var data: Int = PgiSoftphone.getLastTxLevel(activeCall, err)
                        // for some reason the values in Dolby rooms are about 4 times
                        // greater than for non-dolby rooms.
                        if (isDolby) {
                            data /= 4
                        }
                        if (data != lastMicData) {
                            val micData: String = data.toString()
                            UIThreadHandler.post {
                                PGiSoftPhoneModel.softPhoneSignalLevel.onNext(micData)
                            }
                            /*  this code is an attempt to fix an issue with
                                low volume in softphone v2.  Audio group
                                is working on a permanent fix.
                             */
                            /* commenting out this code as it seems to raise
                               volume too much.  Leaving it in place in case
                               we decide we want it back.
                            if (data > 30) {
                                softphoneResponse.lowerVolume(silent = true)
                            } else if (data < 5)
                                softphoneResponse.raiseVolume(silent = true)
                             */
                            lastMicData = data
                        }
                        if (err[0] != 0) {
                            val errorDesc = PgiSoftphone.getErrorInfo(err[0])
                            val errStr = "startMicrophoneSignalProcessing:" + errorDesc.errorString
                            mLogger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, errStr)
                        }
                        callBackHandler?.postDelayed(this, 500)
                    }
                }
        }, 500)
    }
}

    override fun trunkQualityAlarmCB(call: SWIGTYPE_p_void?, alarm_type: softphone_trunk_quality_alarm_type?, alarm_severity: softphone_trunk_quality_alarm_severity?, context: Any?) {
        super.trunkQualityAlarmCB(call, alarm_type, alarm_severity, context)
        val msg = "RAG alarm notification: alarmType = $alarm_type, alarmSeverity = $alarm_severity"
        if (alarm_severity.toString() == AppConstants.TRUNK_QUALITY_ALARM_SEVERITY_RED) {
            UIThreadHandler.post {
                PGiSoftPhoneModel.softPhoneBadNetworkToast.onNext(true)
            }
            mLogger.error(TAG, LogEvent.ERROR, LogEventValue.RAG_ALARM_RED, msg)
            redRAGCount += 1
        } else {
            totalRAGCount += 1
        }
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE_CONNECTION_MONITOR, LogEventValue.RAG_ALARM, msg)
    }

     fun getCallQualityValue() : String {
             if(totalRAGCount > 0) {
                 var redRAGCountPercentage = (redRAGCount * 100 / totalRAGCount)
                 return if (redRAGCountPercentage > AppConstants.RED_RAG_CALL_QUALITY_THRESHOLD) {
                     AppConstants.CALL_QUALITY_POOR
                 } else {
                     AppConstants.CALL_QUALITY_GOOD
                 }
             }
         return AppConstants.EMPTY_STRING
     }

     fun resetRAGCount() {
        redRAGCount = 0
        totalRAGCount = 0
     }

    override fun streamCreatedCB(call: SWIGTYPE_p_void?, si: StreamInfo?, context: Any?) {
        streamCreated = true
    }

    fun stopMicrophoneSignalProcessing() {
        microphoneSignalProcessingActive = false
    }

    override fun ipChangeCompletedCB(context: Any?) {
        super.ipChangeCompletedCB(context)
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE_CONNECTION_MONITOR, LogEventValue.IP_CHANGE_COMPLETED, "Received Softphone ipChangeCompletedCB")
    }
}