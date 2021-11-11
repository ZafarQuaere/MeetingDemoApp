package com.pgi.logging.model

data class MixpanelBaseModel(
    var clientId: String? = null,
    var companyId: String? = null,
    var locale: String? = null,
    var screenResolution: String? = null,
    var numGuests: String? = null,
    val userType: String? = null,
    var networkType: String? = null,
    var mobileCountryCode: Int? = null,
    var mobileNetworkCode: Int? = null,
    val appVersion: String? = null,
    val platform: String = "Android",
    val product: String = "Collaboration",
    val productsUsed: String = "Collaboration",
    var waitingRoomEnabled: Boolean? = null
    )
