package com.pgi.logging.model

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var type: String? = null,
    var carrier: String? = null,
    var locale: String? = null,
    var furl: String? = null,
    var networkType: String? = null,
    var logCount: Int = 0,
    var joinMeetingEntryPoint: String? = null,
    var companyId: String? = null,
    var role: String? = null
)