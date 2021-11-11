package com.pgi.convergencemeetings.meeting.gm4.services.legacyws

import android.util.Log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.pgi.convergence.enums.pia.PIAWebSocketEventType
import com.pgi.convergencemeetings.models.*
import com.pgi.network.PIAWebSocketManager
import com.pgi.network.PIAWebSocketResponseHandler
import java.io.IOException


/**
 * Created by surbhidhingra on 25-10-17.
 */
class PIAWebSocketWrapper(private val mPiaWebSocketEventReceiver: PIAWebSocketEventReceiver) : PIAWebSocketResponseHandler {

	private val mPiaWebSocketManager: PIAWebSocketManager = PIAWebSocketManager(this)

	fun invokeSocket(piaWebSocketCommandMessage: PIAWebSocketCommandMessage?) {
		try {
			mPiaWebSocketManager.invokePIASocket(DEFAULT_MAPPER.writeValueAsString(piaWebSocketCommandMessage))
		} catch (e: JsonProcessingException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	fun bindSocket(piaWebSocketCommandMessage: PIAWebSocketCommandMessage?) {
		try {
			mPiaWebSocketManager.sendBindCommand(DEFAULT_MAPPER.writeValueAsString(piaWebSocketCommandMessage))
		} catch (e: JsonProcessingException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	fun unbindSocket(piaWebSocketCommandMessage: PIAWebSocketCommandMessage?) {
		try {
			mPiaWebSocketManager.sendUnBindCommand(DEFAULT_MAPPER.writeValueAsString(piaWebSocketCommandMessage))
		} catch (e: JsonProcessingException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	fun keepAlive(piaWebSocketCommandMessage: PIAWebSocketCommandMessage?) {
		try {
			mPiaWebSocketManager.sendKeepAliveCommand(DEFAULT_MAPPER.writeValueAsString(piaWebSocketCommandMessage))
		} catch (e: JsonProcessingException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	override fun onEventMessageReceived(event: String, data: String) {
		try {
			when (event) {
				PIAWebSocketEventType.PARTINFO.value -> mPiaWebSocketEventReceiver.onParticipantInfoReceived(DEFAULT_MAPPER.readValue(data, MeetingParticipant::class.java))
				PIAWebSocketEventType.CONFINFO.value -> mPiaWebSocketEventReceiver.onConferenceInfoReceived(DEFAULT_MAPPER.readValue(data, ConferenceInfo::class.java))
				PIAWebSocketEventType.CONFSTATE.value -> mPiaWebSocketEventReceiver.onConferenceStateReceived(DEFAULT_MAPPER.readValue(data, ConferenceState::class.java))
				PIAWebSocketEventType.TALKER.value -> mPiaWebSocketEventReceiver.onTalkerStateReceived(DEFAULT_MAPPER.readValue(data, Talker::class.java))
				PIAWebSocketEventType.WATCHLOST.value -> mPiaWebSocketEventReceiver.onWatchLost(DEFAULT_MAPPER.readValue(data, WatchLost::class.java))
				else -> {
				}
			}
		} catch (e: IOException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	override fun onCommandMessageReceived(data: String) {
		try {
			mPiaWebSocketEventReceiver.onCommandReceived(DEFAULT_MAPPER.readValue(data, PIAWebSocketCommandMessageACK::class.java))
		} catch (e: IOException) {
			Log.e(TAG, "Error: " + e.message)
		}
	}

	companion object {
		private val TAG = PIAWebSocketWrapper::class.java.name
		private val DEFAULT_MAPPER = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	}

}