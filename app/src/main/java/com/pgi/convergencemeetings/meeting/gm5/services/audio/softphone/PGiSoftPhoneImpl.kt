package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import PGi.Softphone.SWIG.*
import PGi.Softphone.SWIG.SRTPOption.SRTP_MANDATORY
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.pgi.convergence.annotations.OpenForTest
import com.pgi.convergence.application.CoreApplication.Companion.mLogger
import com.pgi.convergence.common.features.AppConfig
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.SoftphoneConstants
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.TurnServerInfoManager
import com.pgi.network.models.Cave
import com.pgi.network.models.TurnServerInfo
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.text.Typography.tm


@OpenForTest
class PGiSoftPhoneImpl(context: Context, loggerUtil: Logger): PGiSoftPhone, KoinComponent {
    private var mSoftPhoneInit = false
    private var libraryLoaded = false
    private var mSpeakerActivated = false
    private var mAudioRouteManager: PGiAudioRouteManager? = null
    internal var mAudioManager: AudioManager? = null
    private val mContext = context
    private val logger = loggerUtil
    private var dialoutCount: Int = 0
    private var mPGiSoftPhoneCallback: PGiSoftPhoneCallBack? = null
    private val TAG = PGiSoftPhoneImpl::class.java.simpleName
    lateinit var mAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener
    private var call: SWIGTYPE_p_void? = null
    private var softphoneAccount: SWIGTYPE_p_void? = null
    private var callBackSet: Boolean = false
    private var isDolby = false
    private var sipFromAddr: String = ""
    private var sipFormatted: String = ""
    private val handlerThread = HandlerThread("com.pgi.gmmeet.voip")
    private var softPhoneHandler: Handler? = null
    private var reconnecting = false
    private var hangUpProcessing = false
    private var destroyProcessing = false
    private var UIThreadHandler: Handler = Handler(Looper.getMainLooper())
    private val STREAM_BLUETOOTH:Int = 6
    private var turnServerInfo: TurnServerInfo? = null
    private val turnServerInfoManager: TurnServerInfoManager by inject()
    private val featureManager: FeaturesManager by inject()
    private var attemptingToConnect: Boolean = false
    private var turnServer: String? = null
    private var turnUserName: String? = null
    private var turnUserPassword: String? = null
    private var turnTLS: Boolean = false
    private var currentAudioRoute: AudioDeviceRoutes? = null
    private var audioRouteCheckerTimer: Timer? = null
    private var proxyServer: String? = null
    private var caveEnabled = false
    private var ropeEnabled = false
    private var turnFound = false

    init {
        var cSharedLibLoaded = false
        var softphoneLibLoaded = false
        try {
            System.loadLibrary(SoftphoneConstants.C_SHARED_LIBRARY)
            cSharedLibLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE,
                    "PGiSoftPhoneImpl - Failed to load c++ Shared Library", e)
        }
        try {
            System.loadLibrary(SoftphoneConstants.SOFTPHONE_LIBRARY)
            softphoneLibLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE,
                    "PGiSoftPhoneImpl - Failed to load Native Softphone Library", e)
        }
        if (cSharedLibLoaded && softphoneLibLoaded) {
            libraryLoaded = true
            try {
                handlerThread.start()
                softPhoneHandler = Handler(handlerThread.looper)
                mAudioRouteManager = PGiAudioRouteManager(mContext, logger)
                mAudioManager = mAudioRouteManager?.getAudioManager()
                initAudioFocusListener()
                val clientId: String? = logger.userModel.id ?: logger.userModel.email ?: logger.attendeeModel.participantId
                logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "PGiSoftPhoneImpl - clientid = $clientId")
                val userDataContext = Object()
                val lo = LoggingOptions()
                lo.logLevel = SoftphoneLoggingLevel.LOG_LEVEL_STANDARD
                lo.logICE = false
                caveEnabled = featureManager.isFeatureEnabled(Features.CAVE_ENABLED.feature)
                ropeEnabled = featureManager.isFeatureEnabled(Features.ROPE_ENABLED.feature)
                var rc : Int = 0
                setTurnInfo()
                // We need to call addStunSever before callback for now. Softphone team is going to fix it next release
                mPGiSoftPhoneCallback = PGiSoftPhoneCallBack(mContext, this)
                mPGiSoftPhoneCallback?.init()
                PgiSoftphone.setCallbacks(mPGiSoftPhoneCallback)
                callBackSet = true
                rc = PgiSoftphone.initLibrary(mContext.applicationContext, SoftphoneConstants.LOGGING_SERVER_URL, lo, clientId,
                        "SoftphoneAndroid", userDataContext)
                if (rc != 0) {
                    logger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "PGiSoftPhoneImpl - initLibrary failed with rc = $rc")
                } else {
                    val version = PgiSoftphone.getReleaseId()
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "PGiSoftPhoneImpl - Inited softphone lib version $version")
                    mSoftPhoneInit = true
                    logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_CAVE_ENABLED, "caveEnabled - $caveEnabled", null, null, false, true)
                    logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_ROPE_ENABLED, "ropeEnabled - $ropeEnabled", null, null, false, true)
                }
            } catch (e: Exception) {
                logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
            }
        }
        UIThreadHandler.post {
            PGiSoftPhoneModel.softPhoneAvailable.onNext(libraryLoaded)
        }
    }

    private fun initAudioFocusListener() {
        try {
            mAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {
                when (it) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        Log.e(TAG, "AUDIOFOCUS_GAIN")
                        if (mSpeakerActivated) {
                            this.activateSpeaker()
                        } else {
                            this.setDefaultAudioRoute()
                        }
                        this.resumeAudio()
                    }
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        Log.e(TAG, "AUDIOFOCUS_LOSS")
                        logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                            "PGiSoftPhoneImpl - Lost Audio Focus")
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                            "PGiSoftPhoneImpl - Pausing Softphone Audio")
                        this.pauseAudio()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
    }

    override fun activateSpeaker() {
        val route = mAudioRouteManager?.getAudioRoute()
        val msg = "ActivateSpeaker: changing audio route from " + route.toString() + " to speaker"
        logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
//        val beforeVolume = mAudioRouteManager?.getAudioManager()?.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
//            ?: -1
        mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER)

        if (mAudioRouteManager?.getAudioRoute() != AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "Failed to set audio route to speaker")
        } else {
            currentAudioRoute = AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER
        }
//        if (beforeVolume != -1) {
//            mAudioRouteManager?.getAudioManager()?.setStreamVolume(AudioManager.STREAM_VOICE_CALL, beforeVolume, 0)
//        }
        mSpeakerActivated = true
    }

    private fun createAccount(acctName: String) {
        try {
            val err: IntArray = intArrayOf(0)
            val accountConfig = AccountConfig()
            accountConfig.accountUserName = acctName
            accountConfig.accountUserPassword = ""
            accountConfig.proxyServer = if (caveEnabled) proxyServer else null
            accountConfig.registrarServer = null
            accountConfig.ipChangeConfig.hangUpCalls = false

            val turnConfig = AccountTurnConfig()
            if (ropeEnabled) {
                if (turnFound) {
                    turnConfig.turnServer = turnServer
                    turnConfig.turnUserName = turnUserName
                    turnConfig.turnUserPassword = turnUserPassword
                    turnConfig.turnTLS = turnTLS
                }
                val turnDetails = "turnConfig.turnServer=${turnConfig.turnServer} " + "turnConfig.turnUserName=${turnConfig.turnUserName} " + "turnConfig.turnTLS=${turnConfig.turnTLS}"
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.SOFTPHONE_TURN_CONFIG, "Turn Details set to softphone = ${turnDetails}", null, null, false, true)
                Log.e(TAG, "turnDetails= ${turnDetails}")
                Log.e(TAG, "turnConfig.turnUserPassword=${turnConfig.turnUserPassword}")

                accountConfig.turnConfig = turnConfig
            }
            val transportConfig = AccountTransportConfig()
            if (caveEnabled) {
                val mediaConfig = AccountMediaConfig()
                mediaConfig.srtpOption = SRTP_MANDATORY
                mediaConfig.rtcpMux = true
                accountConfig.mediaConfig = mediaConfig
                transportConfig.sipTLS = true
                val caveDetails = "transportConfig.sipTLS=${transportConfig.sipTLS} ,srtpOption =${accountConfig.mediaConfig.srtpOption}"
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.SOFTPHONE_CAVE_ENABLED, "Cave details = ${caveDetails}", null, null, false, true)
            }
            accountConfig.transportConfig = transportConfig
            softphoneAccount = PgiSoftphone.createAccount(accountConfig, err)
            if (err[0] != 0) {
                val errorDesc = PgiSoftphone.getErrorInfo(err[0])
                val errStr = errorDesc.errorString
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, errStr)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override fun dialOut(sipFromAddress: String, formattedSipUri: String, enableDolby: Boolean, proxyServer: String) {
        this.proxyServer = proxyServer
        if (sipFromAddress.isEmpty()) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "SIP Address is empty")
            return
        }
        if (formattedSipUri.isEmpty()) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "SIP string is empty")
            return
        }
        if (attemptingToConnect) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "There is already a call trying to connect.")
            return
        }
        if (mSoftPhoneInit) {
            softPhoneHandler?.post {
                Log.e(TAG, "dialOut calling doDialOut")
                doDialOut(sipFromAddress, formattedSipUri)
            }
        }
    }

    private fun doDialOut(sipFromAddress: String, formattedSipUri: String) {
        var msg = "PGiSoftPhoneImpl - doDailout $sipFromAddress $formattedSipUri $tm DialoutCount: $dialoutCount"
        logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
        Log.e(TAG, msg)
        if (!callBackSet) {
            PgiSoftphone.setCallbacks(mPGiSoftPhoneCallback)
            callBackSet = true
        }
        if(call != null) {
            if (isCallConnected()) {
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                        "doDialOut: another call is connected,  calling doHangUp")
                doHangUp()
            } else if (mPGiSoftPhoneCallback?.callState == CallState.INV_STATE_CALLING ||
                    mPGiSoftPhoneCallback?.callState == CallState.INV_STATE_CONNECTING) {
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                        "doDialOut: another call is trying to connect.  Calling doHangUp")
                doHangUp()
            }
        }
        isDolby = formattedSipUri.contains("bob")
        mPGiSoftPhoneCallback?.isDolby = isDolby
        dialoutCount++
        if (sipFromAddress.startsWith("sip:")) {
            val editedSipAddress = sipFromAddress.substring(4)
            createAccount(editedSipAddress)
        } else {
            createAccount(sipFromAddress)
        }
        val retryLimit = 2
        var callAttempts = 0
        attemptingToConnect = true
        while (callAttempts++ < retryLimit) {
            if (call == null) {
                try {
                    var createAttempts = 0
                    val err: IntArray = intArrayOf(0)
                    while (createAttempts++ < 5 && call == null) {
                        call = PgiSoftphone.createCall(softphoneAccount, err)
                        if (err[0] != 0) {
                            val errorDesc = PgiSoftphone.getErrorInfo(err[0])
                            val errStr = "createCall failed with " + errorDesc.errorString
                            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, errStr)
                            if (call != null) {
                                val rc = PgiSoftphone.destroyCall(call)
                                if (rc != 0) {
                                    msg = "destroyCall returned $rc"
                                    Log.e(TAG, msg)
                                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                                }
                                call = null
                            }
                            Thread.sleep(200)
                        } else {
                            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "createCall worked")
                            Log.e(TAG, "createCall worked")
                        }
                    }
                    sipFromAddr = sipFromAddress
                    sipFormatted = formattedSipUri
                } catch (e: Exception) {
                    logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
                }
            }
            if (call != null) {
                try {
                    var rc = PgiSoftphone.makeCall(call, formattedSipUri)
                    msg = "makeCall returned $rc, attempt=$callAttempts"
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                    Log.e(TAG, msg)
                    if (rc == 70004) {
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "makeCall failed because call object is now invalid")
                        rc = PgiSoftphone.destroyCall(call)
                        if (rc != 0) {
                            msg = "destroyCall returned $rc"
                            Log.e(TAG, msg)
                            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                        }
                        call = null
                        return
                    } else if (rc == 70010) {
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "makeCall failed with 70010")
                        rc = PgiSoftphone.destroyCall(call)
                        if (rc != 0) {
                            msg = "destroyCall returned $rc"
                            Log.e(TAG, msg)
                            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                        }
                        call = null
                        return
                    } else if (rc != 0) {
                        Thread.sleep(500)
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "retrying failed makeCall")
                        continue
                    }
                    // make sure we get connected
                    // in testing we often saw 10 seconds for connection when on cellular
                    // that is why the 15L below
                    // but when on WIFI it often connects in one second or less
                    // that is why 500L below
                    var waits = 0L
                    val waitLimit = 15L * callAttempts
                    while (!isCallConnected() && waits++ < waitLimit) {
                        val delay = waits * 500L
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "waiting for makecall to connect $waits")
                        Thread.sleep(delay)
                        if (mPGiSoftPhoneCallback?.callState == CallState.INV_STATE_DISCONNECTED) {
                            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "call disconnected, exiting wait")
                            break
                        }
                    }
                    if (isCallConnected()) {
                        break
                    }
                    if (mPGiSoftPhoneCallback?.callState == CallState.INV_STATE_DISCONNECTED) {
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "call disconnected prior to completing connection")
                        doHangUp()
                    }
                    if (waits >= waitLimit) {
                        logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "do not see connection after makeCall")
                        rc = PgiSoftphone.destroyCall(call)
                        if (rc != 0) {
                            msg = "destroyCall returned $rc"
                            Log.e(TAG, msg)
                            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                        }
                        call = null
                    }
                } catch (e: Exception) {
                    logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
                }
            }
        }
        attemptingToConnect = false
        if (callAttempts >= retryLimit && !isCallConnected()) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "VOIP dialout failed.")
            UIThreadHandler.post {
                PGiSoftPhoneModel.softPhoneConnected.onNext(PGiSoftPhoneConstants.VOIP_DISCONNECTED)
            }
        } else {
            startAudioRouteChecker()
        }
        Log.e(TAG, "------ doDialOut finished -----------")
    }

    override fun destroy() {
        mLogger.meetingState.meetingStateCallId = ""
        Log.e(TAG, "destroy")
        if (reconnecting || destroyProcessing) {
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "ignoring destroy request")
            return
        }
        if (softphoneAccount != null) {
            softPhoneHandler?.post {
                doDestroy()
            }
        }
    }

    private fun doDestroy() {
        destroyProcessing = true
        Log.e(TAG, "doDestroy")
        try {
            if (callBackSet) {
                PgiSoftphone.setCallbacks(null)
                callBackSet = false
            }
        } catch (e: java.lang.Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
        try {
            if (call != null) {
                if (isCallConnected()) {
                    doHangUp()
                }
                val rc = PgiSoftphone.destroyCall(call)
                if (rc != 0) {
                    val msg = "destroyCall returned $rc"
                    Log.e(TAG, msg)
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                }
                call = null
            }
        } catch (e: java.lang.Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
        try {
            if (softphoneAccount != null) {
                val rc = PgiSoftphone.destroyAccount(softphoneAccount)
                if (rc != 0) {
                    val msg = "destroyAccount returned $rc"
                    Log.e(TAG, msg)
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                }
                softphoneAccount = null
            }
        } catch (e: java.lang.Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
        dialoutCount = 0
        mSpeakerActivated = false
        destroyProcessing = false
        mPGiSoftPhoneCallback?.callState = CallState.INV_STATE_NULL
        turnServerInfoManager.stopCredentialExpiryChecker()
    }

    private fun setTurnInfo() {
        if (caveEnabled && ropeEnabled && TurnServerInfoManager.turnRestSuccess) {
            turnFound = false
            logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_TURNREST_API, turnFound.toString())
            TurnServerInfoManager.turnServerInfo?.let {
                parseStunTurnInfo(it)
            } ?: run {
                logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_TURNREST_API, "turnRest URL null or empty", null, null, false, true)
            }
        }
        else {
            logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_TURNREST_API, "turnRest URL null or empty", null, null, false, true)
            logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.SOFTPHONE_TURNREST_API, "caveEnabled - $caveEnabled", null, null, false, true)
            val rc = PgiSoftphone.addStunServer(AppConstants.STUNSERVER_URL)
            if (rc != 0) {
                logger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "addStunServers failed")
            } else {
                Log.e(TAG, "added Stun Server")
                logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "addedStunServers -" + AppConstants.STUNSERVER_URL, null, null, false, true)
            }
        }
    }

    override fun hangUp() {
        Log.e(TAG, "hangUp")
        if (reconnecting || hangUpProcessing) {
            val msg = "ignoring hangup"
            Log.e(TAG, msg)
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
            return
        }
        if (call != null && isCallConnected()) {
            softPhoneHandler?.post {
                doHangUp()
            }
        }
    }

    private fun doHangUp() {
        hangUpProcessing = true
        Log.e(TAG, "doHangUp")
        try {
            mPGiSoftPhoneCallback?.stopMicrophoneSignalProcessing()
            val rc = PgiSoftphone.hangUpCall(call)
            if (rc != 0) {
                val msg: String
                when (rc) {
                    70009 -> msg = "hangUpCall: operation timed out"
                    171140 -> msg = "hangUpCall: session already terminated"
                    else -> msg = "hangUpCall returned $rc"
                }
                Log.e(TAG, msg)
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
            }
        } catch (e: Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
        stopAudioRouteChecker()
        mPGiSoftPhoneCallback?.callState = CallState.INV_STATE_NULL
        hangUpProcessing = false
    }

    override fun isAttemptingConnection(): Boolean {
        return attemptingToConnect
    }

    override fun getCallQuality() : String {
       return mPGiSoftPhoneCallback?.getCallQualityValue().toString()
    }

    override fun resetRAGCount() {
        mPGiSoftPhoneCallback?.resetRAGCount()
    }

    override fun isCallConnected(): Boolean {
        try {
            if (call != null) {
                val callState = mPGiSoftPhoneCallback?.callState
                if (callState != null) {
                    Log.e(TAG, "isCallConnected: call state=$callState")
                    if (callState == CallState.INV_STATE_CONFIRMED)
                        return true
                }
            }
        } catch (e: Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE, e.message.toString(), e)
        }
        return false
    }

    override fun isSoftPhoneAvailable(): Boolean {
        UIThreadHandler.post {
            PGiSoftPhoneModel.softPhoneAvailable.onNext(libraryLoaded)
        }
        return libraryLoaded
    }

    override fun lowerVolume(silent: Boolean) {
        var flags = AudioManager.FLAG_SHOW_UI
        if (silent) {
            flags = 0
        }
        var stream: Int = AudioManager.STREAM_VOICE_CALL
        if (mAudioManager?.isBluetoothScoOn == true) {
            stream = STREAM_BLUETOOTH
        }
        mAudioManager?.adjustStreamVolume(stream, AudioManager.ADJUST_LOWER, flags)
    }

    override fun networkChange() {
        Log.e(TAG, "networkChange")
        if (attemptingToConnect) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "call not connected, ignoring network change")
            return
        }
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected) {
            softPhoneHandler?.post {
                doNetworkChange()
            }
        } else {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "network not connected, ignoring network change")
        }
    }

    private fun doNetworkChange() {
        Log.e(TAG, "networkChange handler")
        if (mPGiSoftPhoneCallback?.callState != CallState.INV_STATE_CONFIRMED) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                    "doNetworkChange: call not connected. Can't call handleIpChange")
            return
        }
        reconnecting = true
        Log.e(TAG, "networkChange: handleIpChange")
        var rc = PgiSoftphone.handleIpChange()
        mPGiSoftPhoneCallback?.streamCreated = false
        if (rc != 0) {
            val msg = "handleIpChange returned $rc"
            Log.e(TAG, msg)
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
        }
        rc = PgiSoftphone.enableAudio()
        if (rc != 0) {
            val msg = "enableAudio returned $rc"
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
        }
        if (mSpeakerActivated) {  // sometimes after a network change the speaker gets turned off
            activateSpeaker()
        }
        reconnecting = false
    }

    private fun parseStunTurnInfo(turnServerInfo: TurnServerInfo) {
        if (turnServerInfo != null) {
            turnServerInfo.iceServers.forEach {
                var url = it.urls[0]
                if (url.contains("turns:") && !turnFound) {
                    url = url.substringAfter("turns:")
                    url = url.substringBefore("?")
                    turnServer = url
                    turnUserName = it.username
                    turnUserPassword = it.credential
                    turnTLS = true
                    turnFound = true // we can only set one turn
                } else if (url.contains("stun:")) {
                    url = url.substringAfter("stun:")
                    val rc = PgiSoftphone.addStunServer(url)
                    if (rc != 0) {
                        logger.error(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "addStunServers failed")
                    } else {
                        Log.e(TAG, "added Stun Server=$url")
                        logger.info(TAG, LogEvent.FEATURE_VOIP, LogEventValue.VOIP_SERVICE, "addedRopeStunServers - $url", null, null, false, true)
                    }
                }
            }
        }
    }

    override fun pauseAudio() {
        softPhoneHandler?.post {
            if (this.isCallConnected()) {
                val rc = PgiSoftphone.disableAudio()
                if (rc != 0) {
                    val msg = "disableAudio returned $rc"
                    Log.e(TAG, msg)
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                }
            }
        }
    }

    override fun raiseVolume(silent: Boolean) {
        var flags = AudioManager.FLAG_SHOW_UI
        if (silent) {
            flags = 0
        }
        var stream: Int = AudioManager.STREAM_VOICE_CALL
        if (mAudioManager?.isBluetoothScoOn == true) {
            stream = STREAM_BLUETOOTH
        }
        mAudioManager?.adjustStreamVolume(stream, AudioManager.ADJUST_RAISE, flags)
    }

    override fun reconnect(newCall: Boolean) {
        if (newCall) {
            doDestroy()
            call = null
            Log.e(TAG, "setting call to null")
        }
        Log.e(TAG, "reconnecting softphone")
        if (sipFromAddr.isNotEmpty() && sipFormatted.isNotEmpty()) {
            var formattedSIP = sipFormatted
            if (!formattedSIP.contains("pgijoin")) {
                formattedSIP += ";pgijoin=silent"
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "Adding silent join to SIP string")
                Log.e(TAG, "Adding silent join to SIP string")
            }
            reconnecting = true
            var retries = 3
            while (retries-- > 0) {
                doDialOut(sipFromAddr, formattedSIP)
                if (isCallConnected()) {
                    Log.e(TAG, "see connected, stopping reconnect processing")
                    break
                }
            }
            UIThreadHandler.post {
                PGiSoftPhoneModel.softPhoneConnected.onNext(PGiSoftPhoneConstants.VOIP_RECONNECTED)
            }
        } else {
            Log.e(TAG, "can not process reconnect")
        }
        reconnecting = false
    }

    override fun releaseFocus() {
        mAudioRouteManager?.releaseAudioFocus(mAudioFocusChangeListener)
    }

    override fun requestFocus() {
        mAudioRouteManager?.requestAudioFocus(mAudioFocusChangeListener)
    }

    override fun resumeAudio() {
        softPhoneHandler?.post {
            if (this.isCallConnected()) {
                val rc = PgiSoftphone.enableAudio()
                if (rc != 0) {
                    val msg = "enableAudio returned $rc"
                    Log.e(TAG, msg)
                    logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
                }
            }
        }
    }

    override fun setDefaultAudioRoute() {
        val audioRoute = mAudioRouteManager?.getAudioRoute()
        if (audioRoute ==  AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE) {
            val msg = "setEerpiceAudioRoute: changing audio route from " + audioRoute.toString() + " to Earpiece"
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
            mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE)
            if (audioRoute != AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE) {
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "failed to set audio route to Earpiece")
            } else {
                currentAudioRoute = AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE
            }
            mSpeakerActivated = false
        } else if (audioRoute ==  AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH ){
            val route = mAudioRouteManager?.getAudioRoute()
            val msg = "setBlueToothAudioRoute: changing audio route from " + route.toString() + " to Bluetooth"
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
            mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH)
            if (audioRoute != AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH) {
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "failed to set audio route to Bluetooth")
            } else {
                currentAudioRoute = AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH
            }
            mSpeakerActivated = false
        } else {
            val msg = "setEerpiceAudioRoute: changing audio route from " + audioRoute.toString() + " to Earpiece"
            logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, msg)
            mAudioRouteManager?.setAudioRoute(AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE)
            currentAudioRoute = AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE
            mSpeakerActivated = false
        }
    }

    override fun onIncomingCall(account: SWIGTYPE_p_void?, newCall: SWIGTYPE_p_void?) {
        call = newCall
        val err: IntArray = intArrayOf(0)
        val callInfo = PgiSoftphone.getCallInfo(newCall, err)
        if (callInfo == null) {
            Log.e(TAG, "unable to get call info")
            PgiSoftphone.answerCall(newCall, 603)
            return
        }
    }

    // functins for starting and stopping the audio route checker
    private fun startAudioRouteChecker() {
        val audioRoutingChecker = AudioRoutingChecker()
        audioRouteCheckerTimer = Timer()
        audioRouteCheckerTimer?.scheduleAtFixedRate(audioRoutingChecker, 0, 3000)
    }

    private fun stopAudioRouteChecker() {
        audioRouteCheckerTimer?.cancel()
        audioRouteCheckerTimer?.purge()
    }

    //  It would be more efficient to use AudioRouting:OnRoutingChangeListener
    //  but that is not supported on older API levels
    //  therefore we are using a timer task to check the audio routing.
    //  There was no measurable difference in APP CPU utilization
    //  with this task running at once every 3 seconds
    /// only during a VOIP call.
    //
    // inner class for running an audio route checker
    inner class AudioRoutingChecker : TimerTask() {
        override fun run() {
            val audioRoute = mAudioRouteManager?.getAudioRoute()
            if (audioRoute != currentAudioRoute) {
                mAudioRouteManager?.setAudioRoute(currentAudioRoute)
                logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "Detected audio route change.  Resetting route.")
            }
        }

    }
}