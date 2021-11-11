package com.pgi.logging.model

data class AttendeeModel(
    var role: String? = null,
    var roomRole: String? = null,
    var meetingJoinStatus: String? = null,
    var participantId: String? = null,
    var audioConnectionType: String? = "No Audio",
    var audioConnectionState: String? = "NOT_CONNECTED",
    var audioParticipantId: String? = null,
    var sipIdentifier: String? = null,
    var countryCode: String? = null,
    var phoneNumber: String? = null,
    var phoneExtension: String? = null,
    var dnis: String? = null,
    var audiocodec: String? = null,
    var companyId: String? = null,
    var muted: String? = null,
    var meetingServer: String? = null,
    var conferenceType: String? = null
)