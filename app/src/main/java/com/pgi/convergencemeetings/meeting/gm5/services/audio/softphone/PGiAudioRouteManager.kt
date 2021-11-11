package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import PGi.Softphone.SWIG.AudioDevice
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.VisibleForTesting
import com.pgi.convergence.annotations.OpenForTest
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

enum class AudioDeviceRoutes {
    AUDIO_ROUTE_SPEAKER,
    AUDIO_ROUTE_EARPIECE,
    AUDIO_ROUTE_BLUETOOTH
}

@OpenForTest
class PGiAudioRouteManager(context: Context, loggerUtil: Logger) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var mContext = CoreApplication.appContext
    private val logger = loggerUtil;
    lateinit var  audioDevices: Array<AudioDeviceInfo>
    private var isBTDeviceAvailable: Boolean = false
    private var audioManager: AudioManager = CoreApplication.appContext
        .getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val TAG = PGiAudioRouteManager::class.java.simpleName

    fun getAudioRoute(): AudioDeviceRoutes {
        return if (audioManager.isBluetoothScoOn) {
            AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH
        } else if (audioManager.isSpeakerphoneOn) {
            AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER
        } else {
            AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE
        }
    }


    fun setAudioRoute(inputAudioRoute: AudioDeviceRoutes?) {
        when (inputAudioRoute) {

            AudioDeviceRoutes.AUDIO_ROUTE_SPEAKER -> {
                audioManager.isSpeakerphoneOn = true
                if (isBluetoothAvailable()) {
                    audioManager.isBluetoothScoOn = false
                    audioManager.stopBluetoothSco()
                }
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                    "PGiAudioRouteManager -  Setting Speaker Audio route")
            }

            AudioDeviceRoutes.AUDIO_ROUTE_EARPIECE -> {
                audioManager.isWiredHeadsetOn = true
                audioManager.isSpeakerphoneOn = false
                audioManager.isBluetoothScoOn = false
                audioManager.stopBluetoothSco()
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                    "PGiAudioRouteManager -  Setting EarPiece Audio route")
            }
            AudioDeviceRoutes.AUDIO_ROUTE_BLUETOOTH -> {
                audioManager.isBluetoothScoOn = true
                audioManager.startBluetoothSco()
                audioManager.isSpeakerphoneOn = false
                audioManager.isWiredHeadsetOn = false
                logger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                    "PGiAudioRouteManager -  Setting Bluetooth Audio route")
            }

        }
    }

    fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            if (bluetoothAdapter != null) {
                val btDev = bluetoothAdapter.bondedDevices
                if (btDev.isNotEmpty()) {
                    isBTDeviceAvailable = true
                }
            }
        } catch (ex: Exception) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE,
                "PGiAudioRouteManager isBluetoothAvailable() - Error Getting Bluetooth Adapter")
        }
        return isBTDeviceAvailable
    }

    fun isBluetoothDeviceConnected(): Boolean {
        return try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val connected =  bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) or
                bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) or
                bluetoothAdapter.getProfileConnectionState(BluetoothProfile.SAP)
            connected == BluetoothProfile.STATE_CONNECTED
        } catch (ex: Exception) {
            false
        }
    }

    fun isHeadphonesPlugged(): Boolean {
        //audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL)
        for (deviceInfo in audioDevices) {
            if (deviceInfo.getType() === AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() === AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                return true
            }
        }
        return false
    }

    fun getAudioManager(): AudioManager? {
        return audioManager
    }

    fun setAudioManager(mgr: AudioManager) {
        audioManager = mgr
    }

    @TargetApi(26)
    fun createAudioFocusRequest(listener: AudioManager.OnAudioFocusChangeListener): AudioFocusRequest {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(listener)
                .build()
    }

    fun releaseAudioFocus(listener: AudioManager.OnAudioFocusChangeListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            releaseAudioFocusForTarget26(listener)
        } else {
            releaseAudioFocusForTarget21(listener)
        }
    }

    @TargetApi(21)
    fun releaseAudioFocusForTarget21(listener: AudioManager.OnAudioFocusChangeListener) {
        val result: Int = audioManager.abandonAudioFocus(listener)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            logger.warn(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "PGiAudioRouteManager -" +
                "  Abandon audio focus failed")
        }
    }

    @TargetApi(26)
    fun releaseAudioFocusForTarget26(listener: AudioManager.OnAudioFocusChangeListener) {
        val audioFocusRequest = createAudioFocusRequest(listener)
        val result: Int = audioManager?.abandonAudioFocusRequest(audioFocusRequest)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            logger.warn(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
                "PGiAudioRouteManager -  Abandon audio focus failed")
        }
    }

    fun requestAudioFocus(listener: AudioManager.OnAudioFocusChangeListener): Boolean {
        val result: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = requestAudioFocusForTarget26(listener)
        } else {
            result = requestAudioFocusForTarget21(listener)
        }
        return result != AudioManager.AUDIOFOCUS_REQUEST_FAILED
    }

    @TargetApi(21)
    fun requestAudioFocusForTarget21(listener: AudioManager.OnAudioFocusChangeListener) : Int {
        val result: Int = audioManager.requestAudioFocus(listener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "PGiAudioRouteManager" +
                " -  Audio focus request failed")
        }
        return result
    }

    @TargetApi(26)
    fun requestAudioFocusForTarget26(listener: AudioManager.OnAudioFocusChangeListener) : Int {
        val audioFocusRequest = createAudioFocusRequest(listener)
        val result: Int = audioManager.requestAudioFocus(audioFocusRequest)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            logger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "PGiAudioRouteManager" +
                " -  Audio focus request failed")
        }
        return result
    }
}