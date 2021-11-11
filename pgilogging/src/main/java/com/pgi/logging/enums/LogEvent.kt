package com.pgi.logging.enums

enum class LogEvent(val eventName: String) {

  // Generic errors and exceptions. All errors will fall under this and if needed add more values
  EXCEPTION("Exception"),
  ERROR("Error"),
  ERROR_USER_TYPE_NULL("Error_UserType_Null"),

  // Defining View in App
  APP_VIEW("App_View"),

  //Database
  APP_DATABASE("App_DataBase"),

  //Permissions
  PERMISSION_CALENDAR("Permission_Calendar"),
  PERMISSION_LOCATION("Permission_Location"),
  PERMISSION_MICROPHONE("Permission_Microphone"),
  PERMISSION_CELLULAR("Permission_CellularData"),

  // Connectivity
  NETWORK_CONNECTIVITY("Network_Connectivity"),
  NETWORK_QUALITY("NETWORK_QUALITY"),

  BLUETOOTH("BLUETOOTH"),

  // API Calls. These should be logged to only Kibana. NewRelic automatically traces the network
  // calls
  API_IDENTITY("Api_Identity"),
  API_ELKLOG("Api_ELKLog"),
  API_WSSERVICE("Api_WebService"),
  API_FURLPROXY("Api_FurlProxy"),
  API_GMSEARCH("Api_GMSearch"),
  API_UAPI("Api_UAPI"),
  API_FOXDEN("Api_Foxden"),
  API_KAZOO("Api_Kazoo"),

  //Other Services
  SERVICE_SOFTPHONE("Service_Softphone"),
  SERVICE_NOTIFICATION("Service_Notification"),
  SERVICE_SOFTPHONE_CONNECTION_MONITOR("Service_SoftPhoneConnectionMonitor"),

  //Feature Tracking - The value will be True
  FEATURE_FURLLINKHANDLER("Feature_FurlLinkHandler"),
  FEATURE_FIRSTTIMEGUESTENTERURLSEARCH("Feature_FirstTimeGuestEnterUrlSearch"),
  FEATURE_ENTERURLSEARCH("Feature_EnterUrlSearch"),
  FEATURE_CONTACTSEARCH("Feature_ContactSearch"),
  FEATURE_SHARE("Feature_Share"),
  FEATURE_HELP("Feature_Help"),
  FEATURE_VOIP("Feature_Voip"),
  FEATURE_DIALOUT("Feature_Dialout"),
  FEATURE_DIALIN("Feature_Dialin"),
  FEATURE_JOINFROMRECENTS("Feature_JoinFromRecents"),
  FEATURE_JOINFROMSEARCH("Feature_JoinFromSearch"), // This covers both url and contact search joins
  FEATURE_GM5MEETINGS("Feature_GM5Meetings"),
  FEATURE_GM4MEETINGS("Feature_GM4Meetings"),
  FEATURE_SENDCHAT("Feature_SendChat"),
  FEATURE_SCREENSHARE("Feature_ScreenShare"),
  FEATURE_FILE("Feature_File"),
  FEATURE_NAMECHANGE("Feature_NameChange"),
  FEATURE_ROOMURLCHANGE("Feature_RoomUrlChange"),
  FEATURE_DEFAULTROOMCHANGE("Feature_DefaultRoomChange"),
  FEATURE_MUTEUNMUTE("Feature_MuteUnMute"),
  FEATURE_MUTEUNMUTEOTHERS("Feature_MuteUnMuteOthers"),
  FEATURE_DEMOTEPROMOTE("Feature_DemotePromote"),
  FEATURE_CHANGEAUDIO("Feature_ChangeAudio"),
  FEATURE_NATIVE_SIP("FEATURE_NATIVE_SIP"),

  //Metrics - The value will be the metric we want to pass
  METRIC_COLD_START("Metric_ColdStart"),
  METRIC_ROOM_VIEW_STARTUP("Metric_RoomViewStartup"),
  METRIC_JOIN_GM5_MEETING("Metric_JoinGM5Room"),
  METRIC_JOIN_GM4_MEETING("Metric_JoinGM4Room"),
  METRIC_SIP_CONNECT("Metric_SipConnect"),
  METRIC_VOIP_CONNECT("Metric_VoipConnect"),
  METRIC_DIALIN_CONNECT("Metric_DialinConnect"),
  METRIC_DIALOUT_CONNECT("Metric_DialoutConnect"),

  //
  METRIC_RAG_ALARAM_RED("Mteric_Rag_Alarm_Red"),
  METRIC_FAILED_MEETING_JOINS("Metric_FailedMeetingJoins"),
  METRIC_FAILED_MEETING_STARTS("Metric_FailedMeetingStarts"),
  METRIC_NETWORK_IO_CALLS("Metric_NetworkIOCalls"),
  METRIC_CHATS_SENT("Metric_ChatsSent"),

  //Mixpanel events
  MIXPANEL_EVENT("eventName"),
  JOIN_LOCK_MEETING("JOIN_LOCK_MEETING"),
  JOIN_MEETING_AT_CAPACITY("JOIN_MEETING_AT_CAPACITY"),
  JOIN_WAIT_ROOM("JOIN_WAIT_ROOM"),
  DISMISSED_BY_HOST("BY_HOST"),
  PARTICIPANT_ADMITTED("PARTICIPANT_ADMITTED"),
  AUDIO_PARTICIPANT_ADMITTED("AUDIO_PARTICIPANT_ADMITTED"),
  WAITING_ROOM_SNACK_BAR("WAITING_ROOM_SNACK_BAR"),
  MIXPANEL_METRICS_EVENT_NAME("Event Name")
}