package com.pgi.logging.model

data class ElkLogMessage  (
    var clientId: String = "NA",
    var applicationId: String = "NA",
    var applicationVersion: String = "NA",
    var applicationName: String = "NA",
    var applicationType: String = "NA",
    var os: String = "NA",
    var osVersion: String = "NA",
    var deviceType: String = "NA",
    var deviceModel: String = "NA",
    var deviceId: String = "NA",
    var webConfId: String = "NA",
    var audioConfId: String = "NA",
    var attendee_roomRole: String = "NA",
    var logItems: MutableList<ElkLogItem> = mutableListOf()
)

