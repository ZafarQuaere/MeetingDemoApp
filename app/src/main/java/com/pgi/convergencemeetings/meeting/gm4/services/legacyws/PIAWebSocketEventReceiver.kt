package com.pgi.convergencemeetings.meeting.gm4.services.legacyws

import com.pgi.convergencemeetings.models.*

/**
 *
 * Created by surbhidhingra on 26-10-17.
 */
interface PIAWebSocketEventReceiver {
	fun onParticipantInfoReceived(part: MeetingParticipant)
	fun onConferenceInfoReceived(conferenceInfo: ConferenceInfo)
	fun onConferenceStateReceived(conferenceState: ConferenceState)
	fun onTalkerStateReceived(talker: Talker)
	fun onWatchLost(watchLost: WatchLost)
	fun onCommandReceived(piaWebSocketCommandMessageACK: PIAWebSocketCommandMessageACK)
}