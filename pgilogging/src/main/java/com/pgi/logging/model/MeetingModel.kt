package com.pgi.logging.model

data class MeetingModel(
    var furl: String? = null,
    var hostPgiClientId: String? = null,
    var hostCompanyId: String? = null,
    var hubConfId: String? = null,
    var server: String? = null,
    var uniqueMeetingId: String? = null,
    var type: String? = null,
    var numGuests: Int = 0,
    var waitingRoomEnabled: Boolean? = null
)