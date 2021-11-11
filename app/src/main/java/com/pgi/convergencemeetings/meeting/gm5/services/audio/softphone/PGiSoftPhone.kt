package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import PGi.Softphone.SWIG.SWIGTYPE_p_void
import com.pgi.convergence.annotations.OpenForTest

@OpenForTest
interface PGiSoftPhone {
    fun onIncomingCall(account: SWIGTYPE_p_void?, newCall: SWIGTYPE_p_void?)
    fun isSoftPhoneAvailable(): Boolean
    fun dialOut(sipFromAddress: String, mFormattedSipUri: String, enableDolby: Boolean, proxyServer: String)
    fun hangUp()
    fun destroy()
    fun activateSpeaker()
    fun pauseAudio()
    fun resumeAudio()
    fun setDefaultAudioRoute()
    fun isCallConnected(): Boolean
    fun raiseVolume(silent: Boolean = false)
    fun lowerVolume(silent: Boolean = false)
    fun reconnect(newCall: Boolean)
    fun requestFocus()
    fun releaseFocus()
    fun networkChange()
    fun isAttemptingConnection(): Boolean
    fun getCallQuality(): String
    fun resetRAGCount()
}