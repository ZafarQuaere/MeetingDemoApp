package com.pgi.logging.model

import com.google.gson.annotations.SerializedName

data class MixpanelDismissHomeCardModel(
    var dismissAction : String? = null
)

data class MixpanelEnableIntegrationModel(
    val connectCalendar : String = "TRUE",
    val integrationName : String = "Office365",
    val integrationEnabled : String = "Office365"
)

data class MixpanelJoinAudioConnectionModel(
    var audioConnection : String? = null,
    var phoneConnection : String? = null,
    var conferenceType : String? = null,
    var attendeeAudioCodec : String? = null,
    var muted : String? = null,
    var meetingServer: String? = null
)

data class MixpanelJoinMeetingModel(
    var entryPoint: String?= null,
    var meetingId: String? = null,
    var lastJoinedMeeting: String? = null
)

data class MixpanelLockModel(
    var meetingId: String? = null
)

data class MixpanelLogInModel(
    var lastLogin: String? = null,
    val authenticationMethod: String = "PGii"
)

data class MixpanelMuteGuestsModel(
    var meetingId: String? = null,
    var muteType: String? = null,
    var muted: String? = "TRUE"
)

data class MixpanelRecordModel(
    var meetingId: String? = null
)

data class MixpanelSpotlightModel(
    var meetingId: String? = null,
    var numGuests: String? = null,
    var userType: String? = null
)

data class MixpanelNetworkChangeModel(
        var networkType: String? = null,
        var mobileCountryCode: Int? = null,
        var mobileNetworkCode: Int? = null
)

data class MixpanelMeetingSecurityModel(
        var meetingId: String? = null,
        var waitingRoomAction : String? = null,
        var numGuestsWaiting : Number? =null,
        var numGuestsAdmitted : Number?=null,
        var numGuestsDenied : Number?=null,
        var guestsAdmitted : MutableList<String>?=null,
        var guestsDenied : MutableList<String>?= null,
        var meetingServer: String? = null
)

data class MixpanelTurnOnCameraModel(
        var meetingId: String? = null,
        var numGuests: String? = null,
        var userType: String? = null,
        var webcamAction: String? = null,
        var webcamResolution: String? = null,
        var webcamBandwidth: String? = null,
        var webcamError: String? = null,
        var waitingRoomEnabled: String? = null
)

data class MixPanelEndOfMeetingFeedback(
        var meetingId: String? = null,
        @SerializedName("Call Quality")
        var callQuality: String? = null,
        @SerializedName("Rating")
        var rating: String? = null,
        @SerializedName("Issues Experienced")
        var issuesExperienced: MutableList<String>? = null,
        @SerializedName("Issue Categories Experienced")
        var issueCategoriesExperienced: MutableList<String>? = null,
        @SerializedName("Issue Description")
        var issueDescription: String? = null,
        @SerializedName("Email Address Included")
        var emailAddressIncluded: Boolean? = null
)

data class MixPanelVoIPCallQuality(
        var meetingId: String? = null,
        var meetingServer: String? = null,
        @SerializedName("Call Quality")
        var callQuality: String? = null
)

data class MixpanelPoorNetworkNotification(
        var meetingId: String? = null
)

data class MixpanelSendChat(
        var meetingId: String? = null,
        var messageType: String? = null,
        var chatType: String? = null,
        var recipientUserType: String? = null
)

data class MixpanelManagePrivateChat(
        @SerializedName("Private Chat Action")
        var privateChatAction: String? = null
)

data class MixpanelNewPrivateChat(
    var chatType: String? = null,
    var recipientUserType: String? = null,
    var actionSource: String? = null
)

data class MixpanelShareMeetingInformation(
        @SerializedName("Share Option Selected")
        var shareOptionSelected: String? = null,
        @SerializedName("Action Source")
        var actionSource: String? = null
)