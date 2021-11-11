package com.pgi.logging.enums

enum class LogEventValue(val value: String) {

  //Error and Exception Values
  //Error and Exception values can be any of the constants defined in this file
  EXCEPTION_ANR("ANR"),
  EXCEPTION_UNHANDLED("UnhandledException"),
  ERROR_USER_TYPE_NULL("UserTypeNull"),

  //APP_VIEW POSSIBLE Values
  SPLASHSCREEN("SplashScreenView"),
  ONBORADING("OnBoardingView"),
  WELCOMETOUR("WelcomeTourView"),
  APPPERMISSIONS("AppPermissionsView"),
  LOGIN("LoginView"),
  HOME("HomeView"),
  FURLLINKHANDLER("FurlLinkHandler"),
  PROFILEIMAGESEARCH("ProfileImangeSearch"),
  GUESTFIRSTTIMEENTERURL("GuestFirstTimeEnterUrlView"),
  AUDIOSHIMMER("AudioShimmerView"),
  AUDIOSELECTION("AudioSelectionView"),
  WAITING_SNACK_BAR("WaitingSnackBar"),
  DIALOUTSCREEN("DialOutView"),
  DIALINSCREEN("DialInView"),
  GM5MEETINGROOM("GM5MeetingRoomView"),
  GM4MEETINGROOM("GM4MeetingRoomView"),
  PARTICIPANTLIST("ParticipantListView"),
  GUESTWAITINGLIST("GuestWaitingListView"),
  ACTIVETALKER("ActiveTalkerView"),
  SCREENSHARE("ScreenShareView"),
  FILEPRESENTATION("FilePresentationView"),
  CHAT("ChatView"),
  THANKYOU("ThankYouView"),
  JOIN("JoinView"),
  ENTERURL("EnterUrlView"),
  ABOUT("AboutView"),
  SETTINGS("SettingsView"),
  UPDATE_NAME("UpdateNameView"),
  TOAST("ToastView"),
  ALERTDIALOG("AlertDialogView"),

  // DataBase values
  LOCAL_DB("LocalDB"),
  SQL("SQL"),
  REALM("Realm"),

  //Permission values
  PERMISSION_DENIED("PermissionDenied"),
  PERMISSION_GRANTED("PermissionGranted"),

  //Connectivity values
  NETWORK_CONNECTED("NetworkConnected"),
  NETWORK_CHANGED("NetworkChanged"),
  NETWORK_DISCONNECTED("NetworkDisconnected"),
  NETWORK("Network layer"),

  POOR("POOR"),
  MODERATE("MODERATE"),
  GOOD("GOOD"),
  EXCELLENT("EXCELLENT"),
  UNKNOWN("UNKNOWN"),

  // PGii
  IDENTITY_SIGNIN("SignIn"),
  IDENTITY_SIGNOUT("SignOut"),
  IDENTITY_TOKENREFRESH("TokenRefresh"),
  IDENTITY_USERPROFILE("UserProfile"),

  // ELK Services
  ELK_LOG("Log"),
  MIXPANEL("Mixpanel"),
  MIXPANEL_JOIN_MEETING("Join Meeting"),
  MIXPANEL_MUTE_GUESTS("Mute Guests"),
  MIXPANEL_LOCK_MEETING("Lock Meeting"),
  MIXPANEL_ENABLE_WAITING("Enable Waiting"),
  MIXPANEL_DISABLE_WAITING("Disable Waiting"),
  MIXPANEL_LOG_IN("Log In"),
  MIXPANEL_RECORD("Record"),
  MIXPANEL_UNLOCK_MEETING("Unlock Meeting"),
  MIXPANEL_UNMUTE_GUESTS("Unmute Guests"),
  MIXPANEL_JOIN_AUDIO_CONNECTION("Join Audio Connection"),
  MIXPANEL_NETWORK_CHANGE("Network Change"),
  MIXPANEL_ENABLE_INTEGRATION("Enable Integration"),
  MIXPANEL_DISMISS_HOME_CARD("Dismiss Home Card"),
  MIXPANEL_WEBCAM_SPOTLIGHT("Spotlight webcam"),
  MIXPANEL_WEBCAM_ON("Manage Webcam Feed"),
  MIXPANEL_SCHEDULE("Schedule Meeting"),
  MIXPANEL_MANAGE_MEETING_SECURITY("Manage Meeting Security"),
  MIXPANEL_END_OF_MEETING_FEEDBACK("End of Meeting Feedback"),
  MIXPANEL_VOIP_CALL_QUALITY("VoIP Call Quality"),

  // Web Services - Add more values if needed
  WS_ERROR("WebSerivceError"), // This is invalid needs to be updated
  WS_CLIENTINFO("ClientInfo"),
  WS_RECENTS("Recents"),


  //GMSearch
  GMSEARCH_SUGGEST("Suggest"),
  GMSEARCH_SEARCH("Search"),
  GMSEARCH_FETCH("MeetingRoomFetch"),

  //FURL Proxy Valus
  FURL_GETURL("GetUrl"),
  FURL_GETBOOTNOAUTH("GetBootNoAuth"),

  // UAPIs - These are the only events which we are concerned as of now
  UAPI_SESSION("RoomSession"),
  UAPI_MEETINGROOMINFO("MeetingRoomInfo"),
  UAPI_JOINMEETING("JoinMeeting"),
  UAPI_MEETINGEVENT("MeetingEvent"),
  UAPI_UPDATEPARTICIPANT("UpdateParticipant"),
  UAPI_DISMISSPARTICIPANT("DismissParticipant"),
  UAPI_DISMISSAUDIOPARTICIPANT("DismissAudioParticipant"),
  UAPI_DIALIN("DialIn"),
  UAPI_DIALOUT("DialOut"),
  UAPI_MUTE("Mute"),
  UAPI_UNMUTE("UnMute"),
  UAPI_MUTEALL("MuteAll"),
  UAPI_UNMUTEALL("UnMuteAll"),
  UAPI_CHAT("Chat"),
  UAPI_SCREENSHARE("ScreenShare"),
  UAPI_LEAVEMEETING("LeaveMeeting"),
  UAPI_ENDMEETING("EndMeeting"),
  UAPI_LOCKMEETING("LockMeeting"),
  UAPI_WAITINGOFF("WaitingOff"),
  UAPI_WAITINGON("WaitingOn"),
  UAPI_UNLOCKMEETING("UnlockMeeting"),
  UAPI_STARTRECORDING("StartRecording"),
  UAPI_STOPRECORDING("StopRecording"),
  UAPI_FRICTIONFREEON("FrictionFreeOn"),
  UAPI_FRICTIONFREEOFF("FrictionFreeOff"),
  // Meeting event
  UAPI_PARTICIPANTDISMISSED("ParticipantDismissed"),
  UAPI_PARTICIPANTADMITTED("ParticipantAdmitted"),
  UAPI_AUDIO_PARTICIPANTADMITTED("AudioParticipantAdmitted"),
  UAPI_MUTEAUDIOPARTICIPANT("MuteParticipant"),
  UAPI_UNMUTEAUDIOPARTICIPANT("UnmuteParticipant"),
  UAPI_PARTICIPANTPROMOTED("ParticipantPromoted"),
  UAPI_PARTICIPANTDEMOTED("ParticipantDemoted"),
  UPDATE_MEETING_OPTION("UpdateMeetingOption"),

  //Files
  FILES_SESSION("FilesSession"),

  //FoxDen
  STATUS_CHANGED("StatusChanged"),
  STATUS_CHANGED_CONNECTING("StatusChangedConnecting"),
  STATUS_CHANGED_CONNECTED("StatusChangedConnected"),
  STATUS_CHANGED_DISCONNECTED("StatusChangedDisconnected"),
  DISCONNECTED("Disconnected"),
  DISCONNECTED_RECONNECTING("DisconnectedReconnecting"),
  DISCONNECTED_UNEXPECTED("DisconnectedUnexpected"),
  DISCONNECTED_EXPECTED("DisconnectedExpected"),
  STREAM_ADDED("StreamAdded"),
  STREAM_REMOVED("StreamRemoved"),
  STREAM_ACTIVE("StreamActive"),
  STREAM_INACTIVE("StreamInactive"),
  CONNECTION_STATUS_CHANGED("ConnectionStatusChanged"),
  ENDPOINT_MESSAGE("EndpointMessage"),


  // SoftPhone Lib Values
  VOIP_SERVICE("Voip"),
  RAG_ALARM_RED("RagAlarmRed"),
  RAG_ALARM("RagAlarm"),
  IP_CHANGE_COMPLETED("ipChangeCompletedCB"),
  SOFTPHONE_CAVE_ENABLED("CaveEnabled"),
  SOFTPHONE_ROPE_ENABLED("RopeEnabled"),
  SOFTPHONE_TURN_CONFIG("TurnConfig"),
  SOFTPHONE_TURNREST_API("TurnRestAPI"),

  // Audio
  RECONNECTING("Reconnecting"),

  // Service Values
  VOIP_NOTIFICATION("VoipNotification"),
  ONGOING_MEETING_NOTIFICATION("OngoingMeetingNotification"),

  // Msal values
  MSAL_AUTH("MsalAuth"),
  MSAL_GETEVENTS("MsalGetEvents"),

  // Crossbar values
  KAZOO_AUTH("KazooAuth"),
  KAZOO_CALLS("KazooCalls"),

  // Config service values
  LOCAL_CONFIG("LocalConfig"),
  REMOTE_CONFIG("RemoteConfig"),

  //Feature Tracking values
  TRUE("True"),
  APPUPGRADE_LOAD("AppUpgradeLoadingStoreInfo"),
  APPUPGRADE_VERSION_CHECK("AppUpgradeCheckingVersions"),
  ENVIRONMENT_CONFIG_PARSE("EnvironmentConfigParse"),

  //Native Sip Registration
  NATIVE_SIP_REGISTERING("RegisteringWithSipServer"),
  NATIVE_SIP_REGISTER_SUCCESS("RegisterSipServerSuccess"),
  NATIVE_SIP_REGISTER_FAILED("RegisterSipServerFailed"),
  NATIVE_SIP_CLOSE_PROFILE("FailedClosingLocalProfile."),
  NATIVE_SIP_CLOSE_MANAGER("FailedClosingSipManager"),
  NATIVE_SIP_END_CALL("FailedEndCall"),
  NATIVE_SIP_CALL_STATE("CallState"),

  ORIENTATION_HANDLER("OrientationHandler"),
  JOIN_LOCK_MEETING("JOIN_LOCK_MEETING"),
  JOIN_MEETING_AT_CAPACITY("JOIN_MEETING_AT_CAPACITY"),
  JOIN_WAIT_ROOM("JOIN_WAIT_ROOM"),
  DISMISSED_BY_HOST("BY_HOST"),
  PARTICIPANT_ADMITTED("PARTICIPANT_ADMITTED"),
  MEETING_OPTION_WS_UPDATED("MEETING_OPTION_WS_UPDATED"),
  MEETING_ENDED_WHILE_WAITING("MEETING_ENDED_WHILE_WAITING"),
  MIXPANEL_POOR_NETWORK_NOTIFICATION("Poor Network Notification"),
  MIXPANEL_OPEN_APP("Open App"),
  MIXPANEL_SEND_CHAT("Send Chat"),
  MIXPANEL_MANAGE_PRIVATE_CHAT("Manage Private Chat"),
  MIXPANEL_NEW_PRIVATE_CHAT("New Private Chat"),
  MIXPANEL_SHARE_MEETING_INFORMATION("Share Meeting Information");

  companion object {
    private val values = values()
    fun getByValue(value: String) = values.firstOrNull { it.value == value }
  }
}