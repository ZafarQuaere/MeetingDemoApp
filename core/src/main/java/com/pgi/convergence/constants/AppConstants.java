package com.pgi.convergence.constants;

import android.net.Uri;
import android.os.Environment;

import java.util.regex.Pattern;

/**
 * Created by amit1829 on 7/24/2017.
 */

public interface AppConstants {
    //Symbols
    String DOT_SYMBOL = ".";
    String SLASH_SYMBOL = "/";
    String UNDER_SCORE_SYMBOL = "_";
    String BLANK_SPACE = " ";
    String COMMA_SYMBOL = ",";
    String DOUBLE_COMMA_SYMBOL = ",,";
    String POUND_SYMBOL = "#";
    String ASTERISK_SYMBOL = "*";
    String EMPTY_STRING = "";
    String EQUALS_SYMBOL = "=";
    String SEMI_COLON = ";";
    String NEW_LINE_CHARACTER = "\n";
    String STRING_PLUS = "+";
    String REGEX_PARENTHESES = "[(][0-9]*[)]";
    String APOSTROPHE_CHARACTERS = "&#39;";
    String APOSTROPHE_SYMBOL = "'";
    String CONFIG_PGI_BASE_URL = "pgiBaseURL";
    String CONFIG_UAPI_BASE_URL = "uapiBaseURL";
    String CONFIG_PIA_BASE_URL = "piaBaseURL";

    String FIRST_NAME = "FIRST_NAME";
    String CLIENT_ID = "CLIENT_ID";

    //App auth related constants
    String AUTHORIZATION_RESPONSE = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
    String AUTH_BY_CLIENT = "auth_by_client";
    String AUTH_BY_CLIENT_VALUE = "true";
    String PARAM_ACCESS_TYPE = "access_type";
    String VALUE_ACCESS_TYPE = "offline";
    String SCOPE_LOGGING = "app.globalmeet:logging";
    String SCOPE_PULSAR = "app.globalmeet:Pulsar";
    String SCOPE_UAPI = "app.globalmeet:uapi";
    String SCOPE_WEBSERVICES = "app.globalmeet:webservices";
    String APP_AUTH_REDIRECT_URI = "androididentity://com.globalmeet.app";
    String SCOPE_OPENID = "openid";
    String SCOPE_PROFILE = "profile";
    String SCOPE_EMAIL = "email";
    String SCOPE_SCREEN_SHARE = "app.globalmeet:screenshare";
    String SCOPE_ELASTIC_SEARCH = "app.globalmeet:gmsearch";
    String BUNDLE_KEY_IMAGE_URLS = "keyImageUrl";
    String BUNDLE_KEY_IMAGE_DIRECTORY = "keyImageDirectory";
    String BUNDLE_KEY_TIMESTAMP = "keyTimeStamp";
    String ELK_EVENT = "elkEvent";
    //Home Greeting Constants
    int MID_NIGHT_THRESHOLD = 0;
    int MORNING_THRESHOLD = 12;
    int AFTERNOON_THRESHOLD = 17;

    String MIC_LEVEL = "M:";
    String COLON = ":";
    String PIPE_SYMBOL = "|";
    String PGI_IDTOKEN_GIVEN_NAME = "given_name";
    String PGI_IDTOKEN_NAME = "name";
    String PGI_IDTOKEN_FAMILY_NAME = "family_name";
    String PGI_IDTOKEN_USER_TYPE = "pgi_user_type";
    String PGI_IDTOKEN_EMAIL = "email";
    int EVENING_THRESHOLD = 24;
    String PROFILE_URL = "avatar_url_100x100";
    String ID_APP_INFO = "pgi_id_app_info";

    //Home Screen Default Weather Constants
    String PREFIX_HOME_IMAGE = "home_weather";
    int MORNING_RANGE = 6;
    int NIGHT_RANGE = 3;
    String ICON_DEF_TYPE_DRAWABLE = "drawable";
    String CLIENT_INFO_TAG = "ClientInfo";
    String DESKTOP_MEETING_SEARCH = "DesktopMeeting/Search";
    String MEETING_ROOM_SEARCH = "EnterpriseDirectory.svc/Meetings";
    String MEETING_SEARCH_TAG = "MeetingRoom/Search";

    //Screen Share
    String SCREEN_SHARE_HTML = "file:///android_asset/jslibs/foxden/index.html";
    String START_SCREEN_SHARE_AUTH = "javascript:startSessionWithAuth()";
    String EXIT_FULL_SCREEN_FOXDEN = "javascript:exitFullScreenMode()";
    String DISCONNECT_FOXDEN_SESSION = "javascript:disconnect()";
    String SET_METHOD = "SetMethod";
    int SCREEN_SIZE = 2560;


    //File Share
    String QUEUE_RENDERED_PAGE = "javascript:queueRenderPage(num)";
    String FILE_NAME = "/PGIDownload.pdf";
    String ROOT = Environment.getExternalStorageDirectory().toString();
    String LOCAL_FILE_NAME = ROOT + FILE_NAME;
    Uri PATH = Uri.parse(LOCAL_FILE_NAME);
    String FILE_SHARE_HTML = "file:///android_asset/jslibs/pdfjs/index.html#disableAutoFetch=true&disableStream=false?" + PATH;
    String VIDEO_SHARE_HTML = "file:///android_asset/jslibs/videojs/index.html";
    //Uapi

    String RESOLUTION_100X100 = "100x100";
    String RESOLUTION_200X200 = "200x200";
    String _LINKS = "_links";
    String PROFILE_IMAGE = "profileImage";
    String HREF = "href";
    String ACTION_START = "START";
    String ACTION_JOIN = "JOIN";
    String ACTION_JOIN_DURATION = "duration_to_join_meeting_milliseconds";
    String ACTION_START_DURATION = "duration_to_start_meeting_milliseconds";
    String ACTION_VOIP_DURATION = "duration_to_start_voip_milliseconds";
    String ACTION_SHIMMER_DURATION = "duration_of_shimmer_milliseconds";
    String WIFI_CONNECTION_TYPE_MESSAGE = " milliseconds using connection_type wifi";
    String DATA_CONNECTION_TYPE_MESSAGE = " milliseconds using connection_type data";
    String ACTION_STOP = "STOP";
    String SCREENSHARE_NAME = "name";
    String SCREENSHARE_EMAIL = "email";
    String SCREENSHARE_PHONE_NUMBER = "phoneNumber";
    String SCREENSHARE_ACTION = "action";
    String SCREENSHARE_PART_ID = "partId";
    String SCREENSHARE_JOINER_ID = "webId";
    String SCREENSHARE_OBJECT = "screenPresenter";
    String SCREENSHARE_SESSION_ID = "streamSessionId";
    String SCREEN_SHARE = "SCREENSHARE";
    String WEBCAM = "WEBCAM";
    String WHITEBOARD = "WHITEBOARD";
    String FILE_PRESENTATION = "FILE_PRESENTATION";
    String CONTENT_DELETED = "CONTENT_DELETED";
    String START_RECORDING_INITIATED = "START_RECORDING_INITIATED";
    String START_RECORDING_FAILED = "START_RECORDING_FAILED";
    String RECORDING_STARTED = "RECORDING_STARTED";
    String STOP_RECORDING_INITIATED = "STOP_RECORDING_INITIATED";
    String STOP_RECORDING_FAILED = "STOP_RECORDING_FAILED";
    String RECORDING_STOPPED = "RECORDING_STOPPED";
    String RECORDING_INFORMATION_UPDATED = "RECORDING_INFORMATION_UPDATED";
    String RECORDING_FAILED = "RECORDING_FAILED";
    String IS_RECORDING = "IS_RECORDING";
    String JWT_ACCESS_TOKEN_EXPIRED = "The access token expired.";
    String JWT_ACCESS_TOKEN_VALIDATION_FAILURE = "Jwt access token validation failure.";
    String RECENT_MEETING_SEARCH_ISSUE = "Unknown error occurred during DesktopMeetingSearch.";

    //Main Menu Share this app constants
    String SHARE_TEXT_PLAIN = "text/plain";
    String EMPTY_SPACE_SYMBOL = " ";
    String IS_MEETING_HOST = "isMeetingHost";
    String CUSTOM_URI_CONNECTION = "customUriConnection";
    String MEETING_CONFERENCE_ID = "meetingConferenceId";

    int RECENT_MEETINGS_MAX_LIMIT = 1000;
    String NAVIGATION_ITEM_SETTINGS = "Settings";
    String NAVIGATION_ITEM_ABOUT = "About";
    String NAVIGATION_ITEM_SHARE = "Share";
    String NAVIGATION_ITEM_HELP = "Help";
    String CONF_NOT_FOUND = "ConferenceNotFound";

    int ABOUT_ITEM_SOFTWARE_CREDIT = 0;
    int SETTING_DEFAULT_ROOM = 0;
    int SETTING_DISPLAY_NAME = 1;
    int SETTING_MEETING_SETTING = 2;
    float ALPHA_VOIP_BTN_DISABLED = .4f;
    int FIVE_MINUTES_IN_MILLIS = (5 * 60 * 1000);

    String MESSAGE_MAIL_TO = "mailto:";
    String MESSAGE_SUBJECT = "subject=";
    int EMAIL_REQUEST_CODE = 1;

    String HELP_APP_TO_EMAIL = "support@globalmeet.com";
    String HELP_APP_TO_EMAIL_JA = "clientsupport.jp@pgi.com";
    String HELP_APP_SUBJECT = "GlobalMeet Feedback";
    String APP_VERSION = "App Version";
    String OS_VERSION = "OS version";
    String DEVICE_MODEL = "Device model";
    String SALES_FORCE_TICKET_SUBJECT_LUMEN = "Meeting Issue-Mobile";
    String SALES_FORCE_TICKET_SUBJECT_GM = "GMC: Meeting Issue-Mobile";
    String MEETING_SERVER_REGION_NA = "NA";
    String MEETING_SERVER_REGION_EMEA = "EMEA";
    String MEETING_SERVER_REGION_APAC = "APAC";
    String USER_TYPE_INTERNAL_EMPLOYEE = "Internal Employee";
    String USER_TYPE_PARTICIPANT = "Participant/Customer";








    String KEY_ABOUT_US_WEB_VIEW_TYPE = "key_about_us_web_view_type";
    String WEBVIEW_MAIL_TO_LINK = "mailto:";
    String WEBVIEW_TEL_LINK = "tel:";
    String WEBVIEW_WEBPAGE_LINK_HTTP = "http://";
    String WEBVIEW_WEBPAGE_LINK_HTTPS = "https://";
    String DIAL_OUT_TYPE_INVITE_PART = "inviteParticipant";
    String VOIP_CONSTANT = "voip";

    int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    int PERMISSIONS_REQUEST_PHONE_CALL = 2;
    int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    int PERMISSIONS_REQUEST_CAMERA = 4;

    String BEARER_PREFIX = "Bearer %s";
    String KEY_ENTER_URL_FOR_NEW_GUEST = "enter_url_for_new_guest";

    //Color codes to be used programatically
    int COLOR_CODE_ACTION_BLUE = 0xFF00B9E6;
    int COLOR_CODE_ACTION_GREY = 0xFF666666;
    String DIAL_OUT_PHONE_NUMBER = "dial_out";
    String DIAL_OUT_CNTRY_CODE = "cntry_code";

    //HTTP response codes
    int HTTP_204 = 204;
    String TALKER_NOTIFY_ON = "TalkerNotifyOn";
    String TALKER_NOTIFY_OFF = "TalkerNotifyOff";
    String CONF_MUTE_ON = "ConfMute";
    String CONF_MUTE_OFF = "ConfUnMute";
    String PART_MUTE_ON = "Mute";
    String PART_MUTE_OFF = "UnMute";
    String INVALID_SESSION_ID = "InvalidSessionId";
    String TYPE_VOIP_SIP = "VOIP SIP";

    float ALPHA_VOIP_BTN_ENABLED = 1f;
    String PRIMARY_ACCESS_NUM = "Primary Access Number";
    String TEL_SYMBOL = "tel: ";
    String CALLING_TEXT = "Calling : ";
    String PLUS_SYMBOL = "+";
    String UTF_8 = "UTF-8";
    String ERROR_TXT = "Error  : ";

    String UPDATE_NAME_BROADCAST = "update_name_broadcast";
    String UPDATE_CLIENT_INFO_BROADCAST = "update_client_info_broadcast";
    String HTTP_REGEX = "(?i)^https:\\/\\/|https:\\/\\/";
    Pattern HTTP_PATTERN = Pattern.compile(HTTP_REGEX);
    String KEY_SIGN_OUT_INTENT = "isSignOut";
    String KEY_JOIN_RETRY_FAILED = "isRetryFailed";

    String MEETING_ID = "MEETING_ID";
    String PRESENTER_NAME = "PRESENTER_NAME";
    String ACTION_SCREEN_START = "START";
    String ACTION_SCREEN_STOP = "STOP";

    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS";
    String SHORT_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    String JSON_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSSX";
    String JSON_SHORT_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ssX";
    String TIME_ZONE_GMT = "GMT";
    String CHAT_TIME_FORMAT = "hh:mm a";
    String CHAT_EVERYONE = "default";
    String PRIVATE_CHAT = "Individual";
    String PRIVATE_CHAT_FROM_CHAT = "Chat tab";
    String PRIVATE_CHAT_FROM_PARTICIPANT_LIST = "Participants list";
    String CHAT_TYPE_GROUP = "Group";
    String CHAT_TYPE_INDIVIDUAL = "Individual";
    String CHAT_CONVID = "convId";
    String CHAT_COUNT_99_PLUS = "99+";
    int CHAT_COUNT_99 = 99;
    int PARTICIPANT_TAB_INDEX = 0;

    String PARTICIPANT_ROLE_KEY = "role";

    String CONFIG_TYPE_PROD = "prod";
    String CONFIG_TYPE_STAGE = "stage";
    String CONFIG_TYPE_QAB = "qab";
    String CONFIG_TYPE_DEV = "dev";
    String ATTENDEE_DNIS = "attendee.dnis";

    int INVALID_CONFERENCE_CODE = 101;
    int INVALID_MEETING_URL = 102;
    int RESULT_MEETING_ROOM_CANCELLED = 0;
    String KEY_INVALID_URL = "invalidUrl";
    String KEY_FURL = "furl";
    String KEY_FROM_URI = "from_uri";
    int MAX_RETRY_OPTIONS = 2;
    String YES = "Y";
    String NO = "N";
    String GUEST = "GUEST";
    String HOST = "HOST";
    String PRESENTER = "PRESENTER";
    String CO_HOST = "CO-HOST";
    String CODE_4 = "4";
    String CODE_2 = "2";
    String ON_KEY_UP_FAILURE = "Failed to retrieve streamType on Key_VOLUME_UP";
    String ON_KEY_DOWN_FAILURE = "Failed to retrieve streamType on Key_VOLUME_DOWN";
    int FAILURE_RESULT_CODE = 0;
    int RETRY_1000_MS = 1000;
    int RETRY_3000_MS = 3000;
    int RETRY_5000_MS = 5000;
    int FIRST_SERVICE_RETRY = 1;
    int SECOND_SERVICE_RETRY = 2;
    float ALPHA_RECORDING_1 = 1.0f;
    float ALPHA_RECORDING_0 = 0.0f;
    int RECORDING_FLASH_DURATION = 500;
    int RECORDING_FLASH_COUNT = 4;
    float ALPHA_WAITINGROOM_ENABLED = 1f;
    float ALPHA_WAITINGROOM_DISABLED = .54f;

    //Local
    String LOCALE = "locale";

    //SoftPhoneCallBacks
    String GREEN = "Green";
    String AMBER = "Amber";
    String RED = "red";
    String MIC_CLIPPING = "Mic Clipping";
    String NOISE_LEVEL = "Noise Level";
    String ECHO_LEVEL = "Echo Level";
    String NETWORK_UPLINK = "Network Uplink";
    String NETWORK_DOWN_LINK = "Network Downlink";
    String TUNNEL_REASON_FORCED = "Connected:Tunneled Reason Forced";
    String TUNNEL_REASON_RTP_FAILED = "Connected:Tunneled Reason RTP Failed";
    String TUNNEL_REASON_PING_FAILED = "Connected:Tunneled Reason Ping Failed";
    String TUNNEL_REASON_BYE_FAILED = "Connected:Tunneled Reason Bye Failed";
    String DISCONNECTED_TUNNEL = "Disconnected:Will Try Tunnel";
    String DISCONNECTED_5060 = "Disconnected:Will Try 5060";
    String DISCONNECTED = "Disconnected";
    String CONNECTED = "Connected";
    String CONNECTING = "Connecting";
    String CALL_ID = "Call-ID:";
    String VIA = "Via:";
    String CALL_STATE = "SoftPhone.CallState";
    int SNACK_BAR_DURATION = 5000;

    //misc logging messages
    String START_OR_JOIN_IN_AIRPLANE_MODE = "User tried to start/join a meeting while in airplane mode";
    // room type
    int GM4_ROOM = 4;
    int GM5_Room = 5;

    String ANDROID_CHANNEL_ID = "ANDROID_CHANNEL_ID";
    String SPEAKER_TOGGLE = "SPEAKER_TOGGLE";
    String MIC_TOGGLE = "MIC_TOGGLE";
    String OPEN_ACTIVITY= "OPEN_ACTIVITY";
    String HANGUP_SOFTPHONE = "HANGUP_SOFTPHONE";
    String LOCK = "LOCK";
    String CAPACITY = "CAPACITY";
    String DELETED_ROOM = "DELETED_ROOM";
    String SESSION_USED = "SESSION_USED";
    String WAIT = "WAIT";
    String WAIT_TIMEOUT = "WAIT_TIMEOUT";
    String JOIN_MEETING_ENTRY_POINT = "JOIN_MEETING_ENTRY_POINT";
    String LUMEN_APP = "lumen";

    //URL Constants
    String PRIVACY_POLICY_URL = "https://www.pgi.com/privacy-policy?inapp";
    String PRIVACY_POLICY_URL_LOCALE = "https://#.pgi.com/privacy-policy?inapp";
    String TERMS_OF_SERVICE_URL = "https://www.pgi.com/terms-of-service/imeet-globalmeet?inapp";
    String TERMS_OF_SERVICE_URL_LOCALE = "https://#.pgi.com/terms-of-service?inapp";
    String SOFTWARE_CREDIT_URL = "https://www.pgi.com/software-credits?inapp";
    String SOFTWARE_CREDIT_URL_LOCALE = " https://#.pgi.com/software-credits?inapp";
    String BOB = "Bob";
    String DIALOG = "Dialog";
    String RECORDER = "RECORDER";
    String VRC = "VRC";
    String WAITING_GUEST = "WAITING_GUEST";
    String WAITING_VRC = "WAITING_VRC";
    String GMDPREFIX = "GM.";
    int COPY_URL = 1;
    int COPY_URL_UNIVERSAL = 2;
    String CONTENT_TYPE_SCREEN = "screen";
    String CONTENT_TYPE_CAMERA = "camera";
    String CONTENT_TYPE_LOCAL_CAMERA = "localcamera";
    String STUNSERVER_URL="stun.globalmeet.com:3478";
    String OUTLOOK_APP_PACKAGE_NAME = "com.microsoft.office.outlook";
    String FOXDEN_SESSION_CONNECTION_SUCCESS = "Foxden session is successfully connected.";
    String DISABLE_WEBCAM = "Disable Webcam";
    String ENABLE_WEBCAM = "Enable Webcam";
    String PERMISSION_DENIED = "Permission denied";
    String SPOTLIGHT_ENABLE = "Spotlight enabled";
    String PERMISSION_GRANTED = "Permission granted";
    String SPOTLIGHT_DISABLE ="Spotlight disabled";
    long RETRY_8000_MS = 8000;
    int ACTUAL_EXPECTED = 1;
    float ALPHA_CAMERA_TOGGLE = 0.3f;
    String BYPASS_VOIP = ";BypassWaitingRoom=y";
    int RETURN_0 = 0;

      //Alarm Severity
    String TRUNK_QUALITY_ALARM_SEVERITY_GREEN = "TRUNK_QUALITY_ALARM_SEVERITY_GREEN";
    String TRUNK_QUALITY_ALARM_SEVERITY_AMBER = "TRUNK_QUALITY_ALARM_SEVERITY_AMBER";
    String TRUNK_QUALITY_ALARM_SEVERITY_RED = "TRUNK_QUALITY_ALARM_SEVERITY_RED";
    String TRUNK_QUALITY_ALARM_SEVERITY_UNKNOWN = "TRUNK_QUALITY_ALARM_SEVERITY_UNKNOWN";
    int RED_RAG_CALL_QUALITY_THRESHOLD = 10;
    String CALL_QUALITY_GOOD = "Good";
    String CALL_QUALITY_POOR = "Poor";
    String MENU_MUTE = "Mute";
    String MENU_UNMUTE = "Unmute";
    String MENU_DEMOTE = "Demote";
    String MENU_PROMOTE = "Promote";
    String MENU_DISMISS = "Dismiss";
    String MENU_PRIVATE_CHAT_WITH = "Private Chat with";

    int ZERO = 0;
    int ONE = 1;
    int TWO = 2;
    int THREE = 3;
    int FOUR = 4;
    int FIVE = 5;

    int COUNTRYCODE_LENGTH = 4;
    int PHONENUMBER_LENGTH = 6;

    //End of meeting Feedback
    String POSITIVE = "Positive";
    String NEGATIVE = "Negative";

    int SYSTEM_UI_VISIBILITY_DEFAULT_VALUE = 0;
    String OPEN_APP = "Open App";
    String MIXPANEL_EVENT = "Mixpanel Event: ";
    String URL_MORE_REGEX = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";
    Pattern URL_MORE_PATTERN = Pattern.compile(URL_MORE_REGEX);
    String TEXT_ONLY = "Text-Only";
    String URL_ONLY = "URL-Only";
    String EMOJI_ONLY = "Emoji-Only";
    String URL_AND_MORE = "URL and More";
    String TEST_TEXT_ONLY = "Hi";
    String TEST_URL_ONLY = "https://pgi.globalmeet.com";
    String TEST_EMOJI_ONLY = "\uD83D\uDE0A";
    String TEST_URL_AND_MORE = "https://pgi.globalmeet.com Hi PGI";
    String TEST_USER = "Test User";
    String TEST_USER_ID = "123456";

//    Guest List
    String IS_SELF_USER = "isSelfUser";
    String OTHER_USER = "otherUser";
    String IS_CONNECTED = "isConnected";
    String NOT_CONNECTED = "notConnected";

    String ENABLE_PRIVATE_CHAT = "Enable Private Chat";
    String DISABLE_PRIVATE_CHAT = "Disable Private Chat";
    String USER_TIMESTAP_DATE_FORMATE= "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // private chat conversation
    String CONVERSATION_EXITS = "CONVERSATION_ALREADY_EXIST";
    int DIGIT_4 = 4;
    int DIGIT_3 = 3;
    String CHAT_FAILED = "ADD_CHAT_FAILED";
    // Margin Constent
    int DP_0=10;
    int DP_30=30;
    int DP_10=10;

    String IN_MEETING_SHARE = "In Meeting Share";
    String OUT_MEETING_SHARE = "Out of Meeting Share";
    String TEST_PACKAGE_NAME = "test.com";
    String MEETING = "meeting";
}
