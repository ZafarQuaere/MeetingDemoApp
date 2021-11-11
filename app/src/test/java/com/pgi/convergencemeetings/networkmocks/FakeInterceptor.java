package com.pgi.convergencemeetings.networkmocks;

/**
 * Created by ashwanikumar on 8/3/2017.
 */

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.network.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 */
public class FakeInterceptor implements Interceptor {

    private final static String TAG = FakeInterceptor.class.getSimpleName();
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // FAKE RESPONSES.
    private final static String WEATHER_INFO1 = "{\"response\":{\"version\":\"0.1\",\"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\"features\":{\"conditions\":1,\"forecast\":1}},\"current_observation\":{\"image\":{\"url\":\"http://icons.wxug.com/graphics/wu2/logo_130x80.png\",\"title\":\"Weather Underground\",\"link\":\"http://www.wunderground.com\"},\"display_location\":{\"full\":\"Samalkha, India\",\"city\":\"Samalkha\",\"state\":\"DL\",\"state_name\":\"India\",\"country\":\"IN\",\"country_iso3166\":\"IN\",\"zip\":\"00000\",\"magic\":\"23\",\"wmo\":\"42181\",\"latitude\":\"28.490000\",\"longitude\":\"77.070000\",\"elevation\":\"228.9\"},\"observation_location\":{\"full\":\"New Delhi, \",\"city\":\"New Delhi\",\"state\":\"\",\"country\":\"IN\",\"country_iso3166\":\"IN\",\"latitude\":\"28.56999969\",\"longitude\":\"77.12000275\",\"elevation\":\"764 ft\"},\"estimated\":{},\"station_id\":\"VIDP\",\"observation_time\":\"Last Updated on August 3, 12:00 PM IST\",\"observation_time_rfc822\":\"Thu, 03 Aug 2017 12:00:00 +0530\",\"observation_epoch\":\"1501741800\",\"local_time_rfc822\":\"Thu, 03 Aug 2017 12:26:58 +0530\",\"local_epoch\":\"1501743418\",\"local_tz_short\":\"IST\",\"local_tz_long\":\"Asia/Kolkata\",\"local_tz_offset\":\"+0530\",\"weather\":\"Haze\",\"temperature_string\":\"90 F (32 C)\",\"temp_f\":90,\"temp_c\":32,\"relative_humidity\":\"70%\",\"wind_string\":\"From the West at 13 MPH\",\"wind_dir\":\"West\",\"wind_degrees\":260,\"wind_mph\":13,\"wind_gust_mph\":0,\"wind_kph\":20,\"wind_gust_kph\":0,\"pressure_mb\":\"1001\",\"pressure_in\":\"29.56\",\"pressure_trend\":\"0\",\"dewpoint_string\":\"79 F (26 C)\",\"dewpoint_f\":79,\"dewpoint_c\":26,\"heat_index_string\":\"105 F (40 C)\",\"heat_index_f\":105,\"heat_index_c\":40,\"windchill_string\":\"NA\",\"windchill_f\":\"NA\",\"windchill_c\":\"NA\",\"feelslike_string\":\"105 F (40 C)\",\"feelslike_f\":\"105\",\"feelslike_c\":\"40\",\"visibility_mi\":\"2.2\",\"visibility_km\":\"3.5\",\"solarradiation\":\"--\",\"UV\":\"9\",\"precip_1hr_string\":\"-9999.00 in (-9999.00 mm)\",\"precip_1hr_in\":\"-9999.00\",\"precip_1hr_metric\":\"--\",\"precip_today_string\":\"0.00 in (0.0 mm)\",\"precip_today_in\":\"0.00\",\"precip_today_metric\":\"0.0\",\"icon\":\"hazy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/hazy.gif\",\"forecast_url\":\"http://www.wunderground.com/global/stations/42181.html\",\"history_url\":\"http://www.wunderground.com/history/airport/VIDP/2017/8/3/DailyHistory.html\",\"ob_url\":\"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=28.56999969,77.12000275\",\"nowcast\":\"\"},\"forecast\":{\"txt_forecast\":{\"date\":\"11:36 AM IST\",\"forecastday\":[{\"period\":0,\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"title\":\"Thursday\",\"fcttext\":\"Cloudy with occasional rain showers. High 91F. Winds W at 10 to 15 mph. Chance of rain 50%.\",\"fcttext_metric\":\"Considerable cloudiness with occasional rain showers. High 33C. Winds W at 15 to 25 km/h. Chance of rain 50%.\",\"pop\":\"50\"},{\"period\":1,\"icon\":\"nt_partlycloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/nt_partlycloudy.gif\",\"title\":\"Thursday Night\",\"fcttext\":\"Partly to mostly cloudy. Low around 80F. Winds W at 5 to 10 mph.\",\"fcttext_metric\":\"Mainly cloudy. Low 27C. Winds W at 10 to 15 km/h.\",\"pop\":\"10\"},{\"period\":2,\"icon\":\"cloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/cloudy.gif\",\"title\":\"Friday\",\"fcttext\":\"Overcast. High 93F. Winds W at 5 to 10 mph.\",\"fcttext_metric\":\"Cloudy skies. High 34C. Winds W at 10 to 15 km/h.\",\"pop\":\"10\"},{\"period\":3,\"icon\":\"nt_partlycloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/nt_partlycloudy.gif\",\"title\":\"Friday Night\",\"fcttext\":\"Partly cloudy in the evening with more clouds for later at night. Low 81F. Winds WSW at 5 to 10 mph.\",\"fcttext_metric\":\"Partly cloudy early with increasing clouds overnight. Low 27C. Winds WSW at 10 to 15 km/h.\",\"pop\":\"0\"},{\"period\":4,\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"title\":\"Saturday\",\"fcttext\":\"Partly cloudy in the morning. Increasing clouds with periods of showers later in the day. High 88F. Winds W at 5 to 10 mph. Chance of rain 40%.\",\"fcttext_metric\":\"Partly cloudy early followed by increasing clouds with showers developing later in the day. High 31C. Winds W at 10 to 15 km/h. Chance of rain 40%.\",\"pop\":\"40\"},{\"period\":5,\"icon\":\"nt_chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/nt_chancerain.gif\",\"title\":\"Saturday Night\",\"fcttext\":\"Considerable cloudiness with occasional rain showers. Low 81F. Winds SW at 5 to 10 mph. Chance of rain 70%.\",\"fcttext_metric\":\"Rain early...then remaining cloudy with showers late. Low 27C. Winds light and variable. Chance of rain 70%. Rainfall near 6mm.\",\"pop\":\"70\"},{\"period\":6,\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"title\":\"Sunday\",\"fcttext\":\"Rain showers early with overcast skies later in the day. High near 90F. Winds WNW at 5 to 10 mph. Chance of rain 50%.\",\"fcttext_metric\":\"Rain showers early with overcast skies later in the day. High 32C. Winds WNW at 10 to 15 km/h. Chance of rain 50%.\",\"pop\":\"50\"},{\"period\":7,\"icon\":\"nt_chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/nt_chancerain.gif\",\"title\":\"Sunday Night\",\"fcttext\":\"Cloudy with occasional showers late at night. Low 82F. Winds light and variable. Chance of rain 40%.\",\"fcttext_metric\":\"Cloudy in the evening, then off and on rain showers after midnight. Low 27C. Winds light and variable. Chance of rain 40%.\",\"pop\":\"40\"}]},\"simpleforecast\":{\"forecastday\":[{\"date\":{\"epoch\":\"1501767000\",\"pretty\":\"7:00 PM IST on August 03, 2017\",\"day\":3,\"month\":8,\"year\":2017,\"yday\":214,\"hour\":19,\"min\":\"00\",\"sec\":0,\"isdst\":\"0\",\"monthname\":\"August\",\"monthname_short\":\"Aug\",\"weekday_short\":\"Thu\",\"weekday\":\"Thursday\",\"ampm\":\"PM\",\"tz_short\":\"IST\",\"tz_long\":\"Asia/Kolkata\"},\"period\":1,\"high\":{\"fahrenheit\":\"91\",\"celsius\":\"33\"},\"low\":{\"fahrenheit\":\"80\",\"celsius\":\"27\"},\"conditions\":\"Chance of Rain\",\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"skyicon\":\"\",\"pop\":50,\"qpf_allday\":{\"in\":0.22,\"mm\":6},\"qpf_day\":{\"in\":0.24,\"mm\":6},\"qpf_night\":{\"in\":0,\"mm\":0},\"snow_allday\":{\"in\":0,\"cm\":0},\"snow_day\":{\"in\":0,\"cm\":0},\"snow_night\":{\"in\":0,\"cm\":0},\"maxwind\":{\"mph\":15,\"kph\":24,\"dir\":\"W\",\"degrees\":273},\"avewind\":{\"mph\":12,\"kph\":19,\"dir\":\"W\",\"degrees\":273},\"avehumidity\":67,\"maxhumidity\":0,\"minhumidity\":0},{\"date\":{\"epoch\":\"1501853400\",\"pretty\":\"7:00 PM IST on August 04, 2017\",\"day\":4,\"month\":8,\"year\":2017,\"yday\":215,\"hour\":19,\"min\":\"00\",\"sec\":0,\"isdst\":\"0\",\"monthname\":\"August\",\"monthname_short\":\"Aug\",\"weekday_short\":\"Fri\",\"weekday\":\"Friday\",\"ampm\":\"PM\",\"tz_short\":\"IST\",\"tz_long\":\"Asia/Kolkata\"},\"period\":2,\"high\":{\"fahrenheit\":\"93\",\"celsius\":\"34\"},\"low\":{\"fahrenheit\":\"81\",\"celsius\":\"27\"},\"conditions\":\"Overcast\",\"icon\":\"cloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/cloudy.gif\",\"skyicon\":\"\",\"pop\":10,\"qpf_allday\":{\"in\":0,\"mm\":0},\"qpf_day\":{\"in\":0,\"mm\":0},\"qpf_night\":{\"in\":0,\"mm\":0},\"snow_allday\":{\"in\":0,\"cm\":0},\"snow_day\":{\"in\":0,\"cm\":0},\"snow_night\":{\"in\":0,\"cm\":0},\"maxwind\":{\"mph\":10,\"kph\":16,\"dir\":\"W\",\"degrees\":270},\"avewind\":{\"mph\":9,\"kph\":14,\"dir\":\"W\",\"degrees\":270},\"avehumidity\":69,\"maxhumidity\":0,\"minhumidity\":0},{\"date\":{\"epoch\":\"1501939800\",\"pretty\":\"7:00 PM IST on August 05, 2017\",\"day\":5,\"month\":8,\"year\":2017,\"yday\":216,\"hour\":19,\"min\":\"00\",\"sec\":0,\"isdst\":\"0\",\"monthname\":\"August\",\"monthname_short\":\"Aug\",\"weekday_short\":\"Sat\",\"weekday\":\"Saturday\",\"ampm\":\"PM\",\"tz_short\":\"IST\",\"tz_long\":\"Asia/Kolkata\"},\"period\":3,\"high\":{\"fahrenheit\":\"88\",\"celsius\":\"31\"},\"low\":{\"fahrenheit\":\"81\",\"celsius\":\"27\"},\"conditions\":\"Chance of Rain\",\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"skyicon\":\"\",\"pop\":40,\"qpf_allday\":{\"in\":0.26,\"mm\":7},\"qpf_day\":{\"in\":0.04,\"mm\":1},\"qpf_night\":{\"in\":0.21,\"mm\":5},\"snow_allday\":{\"in\":0,\"cm\":0},\"snow_day\":{\"in\":0,\"cm\":0},\"snow_night\":{\"in\":0,\"cm\":0},\"maxwind\":{\"mph\":10,\"kph\":16,\"dir\":\"W\",\"degrees\":267},\"avewind\":{\"mph\":6,\"kph\":10,\"dir\":\"W\",\"degrees\":267},\"avehumidity\":74,\"maxhumidity\":0,\"minhumidity\":0},{\"date\":{\"epoch\":\"1502026200\",\"pretty\":\"7:00 PM IST on August 06, 2017\",\"day\":6,\"month\":8,\"year\":2017,\"yday\":217,\"hour\":19,\"min\":\"00\",\"sec\":0,\"isdst\":\"0\",\"monthname\":\"August\",\"monthname_short\":\"Aug\",\"weekday_short\":\"Sun\",\"weekday\":\"Sunday\",\"ampm\":\"PM\",\"tz_short\":\"IST\",\"tz_long\":\"Asia/Kolkata\"},\"period\":4,\"high\":{\"fahrenheit\":\"90\",\"celsius\":\"32\"},\"low\":{\"fahrenheit\":\"82\",\"celsius\":\"28\"},\"conditions\":\"Chance of Rain\",\"icon\":\"chancerain\",\"icon_url\":\"http://icons.wxug.com/i/c/k/chancerain.gif\",\"skyicon\":\"\",\"pop\":50,\"qpf_allday\":{\"in\":0.14,\"mm\":4},\"qpf_day\":{\"in\":0.12,\"mm\":3},\"qpf_night\":{\"in\":0.03,\"mm\":1},\"snow_allday\":{\"in\":0,\"cm\":0},\"snow_day\":{\"in\":0,\"cm\":0},\"snow_night\":{\"in\":0,\"cm\":0},\"maxwind\":{\"mph\":10,\"kph\":16,\"dir\":\"WNW\",\"degrees\":301},\"avewind\":{\"mph\":6,\"kph\":10,\"dir\":\"WNW\",\"degrees\":301},\"avehumidity\":75,\"maxhumidity\":0,\"minhumidity\":0}]}}}";
    private final static String WEATHER_INFO2 = "{\"response\":{\"version\":\"0.1\",\"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\"features\":{\"conditions\":1,\"forecast\":1},\"error\":{\"type\":\"querynotfound\",\"description\":\"No cities match your search query\"}}}";
    private final static String RECENT_MEETING_RESPONSE = "{\"DesktopMeetingSearchResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":109,\"MessageId\":\"6a01fced-69ff-4646-929e-21229e78d5b6\",\"ServerDateTime\":\"\\/Date(1505282101592)\\/\",\"PageNumber\":1,\"TotalItems\":2,\"TotalPages\":1,\"ScheduledInviteResults\":[],\"SearchResults\":[{\"Attended\":1,\"ClientId\":5078758,\"DeletedDate\":null,\"Description\":\"Convergence Desktop App\",\"DesktopMeetingId\":4221,\"Favorite\":true,\"HubConfId\":1141282,\"MeetingType\":\"Web\",\"MeetingUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141282&pc=43331893&sc=&msc=41508\",\"ModeratorSecurityCode\":\"41508\",\"ModifiedDate\":\"\\/Date(1502921331500-0500)\\/\",\"SecurityCode\":\"\",\"UserType\":\"Client\",\"FirstName\":\"Sudheer\",\"Furl\":\"https:\\/\\/mulesoft.pgilab.net\\/SudheerChilumula4\",\"LastName\":\"Chilumula\"},{\"Attended\":1,\"ClientId\":5078758,\"DeletedDate\":null,\"Description\":\"KevinHolligan\",\"DesktopMeetingId\":4224,\"Favorite\":false,\"HubConfId\":1141295,\"MeetingType\":\"web\",\"MeetingUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141295&pc=&sc=&msc=72520\",\"ModeratorSecurityCode\":\"72520\",\"ModifiedDate\":\"\\/Date(1502949301410-0500)\\/\",\"SecurityCode\":\"\",\"UserType\":\"Client\",\"FirstName\":\"Kevin\",\"Furl\":\"https:\\/\\/mulesoft.pgilab.net\\/KevinHolligan\",\"LastName\":\"Holligan\"}]}}";
    private final static String RECENT_MEETINGS_ERROR_RESPONSE = "{\"DesktopMeetingSearchResult\":{\"CorrelationId\":null,\"Errors\":[{\"Code\":26119,\"Message\":\"Jwt access token validation failure.\",\"Parameter\":null,\"ParameterValue\":null,\"Severity\":1,\"Source\":0}],\"ExecutionTime\":125,\"MessageId\":\"d7b65927-b91b-4268-804f-b0fa2e078e13\",\"ServerDateTime\":\"/Date(1505289004119)/\",\"PageNumber\":null,\"TotalItems\":null,\"TotalPages\":null,\"ScheduledInviteResults\":[],\"SearchResults\":[]}}";
    private final static String CLIENT_INFO_RESPONSE = "{\"ClientInfoResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":703,\"MessageId\":\"32fd5e04-3f02-486d-ad56-ec7c73cfebc0\",\"ServerDateTime\":\"\\/Date(1504869690113)\\/\",\"AudioOnlyConferences\":[],\"ClientDetails\":{\"Active\":true,\"BillToId\":8291321,\"ClientContactDetails\":{\"Address\":{\"Address1\":null,\"Address2\":null,\"Address3\":null,\"City\":null,\"CountryCode\":null,\"PostalCode\":null,\"Province\":null,\"StateCode\":null,\"TimeZone\":null},\"Email\":\"ashwani.kumar@pgi.com\",\"Fax\":\"\",\"FaxIntlPrefix\":\"\",\"FirstName\":\"Kumar\",\"HomePhone\":\"\",\"HomePhoneExt\":\"\",\"HomePhoneIntlPrefix\":\"\",\"HomePhoneIsDefault\":false,\"JobTitle\":\"\",\"LastName\":\"Ashwani\",\"MobilePhone\":\"\",\"MobilePhoneExt\":\"\",\"MobilePhoneIntlPrefix\":\"\",\"MobilePhoneIsDefault\":false,\"Phone\":\"\",\"PhoneExt\":\"\",\"PhoneIntlPrefix\":\"\",\"PhoneIsDefault\":false,\"SecondaryPhone\":null,\"SecondaryPhoneExt\":null,\"SecondaryPhoneIntlPrefix\":null,\"SecondaryPhoneIsDefault\":false},\"ClientId\":\"8358172\",\"CreditCardDetail\":null,\"DefaultLanguage\":\"\",\"Hold\":false,\"OperationComments\":\"\",\"Password\":\"n1SX860m\",\"PasswordCanBeModified\":null,\"PromotionCodes\":[],\"SoftPhoneAutoConnect\":false,\"SoftPhoneEnable\":true,\"SpecialInfo\":\"\",\"SubscriptionId\":\"\",\"TerritoryCode\":\"D0938\"},\"CompanyDetails\":{\"BlockedServices\":[\"Acrobat Connect Professional\",\"Readycast Demo (Internal Use Only)\",\"ReadyCast Protect\",\"Visioncast Meeting Demo (Internal Use Only)\",\"iMeet (Visual Conferencing)\",\"International Moderator Dial Out\",\"LotusLiveMeeting\",\"LotusLiveMeetingDemo (Internal Use Only)\"],\"CompanyId\":635623,\"Name\":\"MuleSoft\"},\"ExcessiveConfs\":[],\"MeetingRooms\":[{\"Availability\":\"Wait For Moderator\",\"ConfId\":\"1000026642\",\"ConferenceName\":\"Kumar's Meeting\",\"Deleted\":false,\"HashWebApiAuth\":\"a10ebc349b51cdbcaf185f99179b0be7\",\"ListenPassCode\":\"4439312\",\"MaxParticipants\":\"125\",\"MeetingRoomId\":\"1141409\",\"MeetingRoomOptions\":[{\"Enabled\":true,\"Name\":\"AllowChat\"},{\"Enabled\":true,\"Name\":\"JoinNoise\"},{\"Enabled\":true,\"Name\":\"WebRecording\"},{\"Enabled\":true,\"Name\":\"File Transfer\"}],\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/KumarAshwani\",\"AttendeeRegisterUrl\":\"\",\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141409&pc=&sc=&msc=32195\",\"EndOfMeetingUrl\":\"\"},\"ModeratorEmail\":\"ashwani.kumar@pgi.com\",\"ModeratorPassCode\":\"4439313\",\"ModeratorSecurityCode\":\"32195\",\"ParticipantPassCode\":\"443931\",\"Ptok\":\"wEao7e5Xvr7CwqSpeYp5mSZb8uEK5M0anly5wlf0pzU=\",\"RoomName\":\"KumarAshwani\",\"SecurityCode\":\"\",\"Status\":\"Completed\",\"TimeZone\":\"GUAM__BASE\",\"WebMeetingServer\":\"usw2a.qab.pgilab.net\",\"bridgeOptions\":[],\"phoneInformation\":[{\"Location\":\"USA\\/Mexico\",\"PhoneNumber\":\"1-719-359-9722\",\"PhoneType\":\"Toll\",\"CustomLocation\":null}],\"reservationOptions\":[],\"ParticipantAnonymity\":false}],\"AudioConferenceCount\":0,\"HasGMWebinar\":false,\"MeetingRoomsCount\":1}}";
    private final static String CLIENT_INFO_ERR_RESPONSE = "Jwt access token validation failure.";
    private final static String SUBSCRIBE_MEETING_RESPONSE = "{\"SessionId\":\"FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41\",\"ResultCode\":0,\"PgsErrorCode\":0}";
    private final static String INVALID_TOKEN_SUBSCRIBE_RESPONSE = "{\"ExceptionType\":\"pgi.pia.webservices.InvalidArgumentException\",\"Message\":\"Invalid argument: pgiiToken expired\"}";
    private final static String INVALID_SESSION_RESPONSE = "{\"ResultCode\":4,\"PgsErrorCode\":0}";
    private final static String VALID_SESSION_RESPONSE = "{\"ResultCode\":0,\"ResultText\":\"\",\"PgsErrorCode\":0}";
    private final static String TIMEOUT_SESSION_RESPONSE = "{\"ResultCode\":4,\"ResultText\":\"TimeOut\",\"PgsErrorCode\":0}";
    private final static String INVALID_CONF_SESSION_RESPONSE = "{\"ResultCode\":8,\"ResultText\":\"INVALID_CONF\",\"PgsErrorCode\":0}";
    private final static String INACTIVE_CONF_STATE_RESPONSE = "{\"ConfState\":{\"ConferenceInfo\":{\"Active\":false,\"ConfID\":\"3729599\",\"LPasscode\":\"9045571\",\"Locked\":false,\"MPasscode\":\"9045575\",\"MusicOn\":false,\"NumActiveParts\":0,\"PPasscode\":\"904557\",\"Recording\":false,\"RecordingStarted\":false,\"TalkerNotify\":false,\"RecFileName\":\"\",\"ConfMute\":false,\"VoiceOnEntry\":false,\"VoiceOnExit\":false,\"ToneOnEntry\":false,\"ToneOnExit\":false},\"Parts\":[]},\"ResultCode\":0,\"ResultText\":\"\",\"PgsErrorCode\":0}";
    private final static String ACTIVE_CONF_STATE_RESPONSE = "{\"ConfState\":{\"ConferenceInfo\":{\"Active\":true,\"ConfID\":\"113729599\",\"LPasscode\":\"9045571\",\"Locked\":false,\"MPasscode\":\"9045575\",\"MusicOn\":false,\"NumActiveParts\":1,\"PPasscode\":\"904557\",\"Recording\":false,\"RecordingStarted\":false,\"TalkerNotify\":false,\"RecFileName\":\"\",\"ConfLink\":\"011372959920171023032427\",\"ConfMute\":false,\"VoiceOnEntry\":false,\"VoiceOnExit\":false,\"ToneOnEntry\":false,\"ToneOnExit\":false},\"Parts\":[{\"ANI\":\"GM.FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41\",\"ConfID\":\"113729599\",\"Connected\":true,\"DNIS\":\"7777100002\",\"Hold\":false,\"ListenLevel\":0,\"ListenOnly\":false,\"Mute\":false,\"PartID\":\"8001-37866308\",\"PartType\":1,\"VoiceLevel\":0}]},\"ResultCode\":0,\"ResultText\":\"\",\"PgsErrorCode\":0}";
    private final static String UPDATE_NAME_RESPONSE = "{\"ClientUpdateResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":312,\"MessageId\":\"0dd7e93e-1b0c-469b-bfeb-63a4851ba897\",\"ServerDateTime\":\"/Date(1512120006772)/\",\"ClientHierarchy\":{\"CompanyId\":635623,\"CompanyName\":\"MuleSoft\",\"CorporateCustomerId\":140432,\"CorporateCustomerName\":\"MuleSoft\",\"EnterpriseId\":4594,\"EnterpriseName\":\"MuleSoft\",\"EnterpriseType\":\"1\",\"HubGroupId\":121171,\"HubGroupName\":\"mulesoft\",\"HubId\":120963,\"HubName\":\"mulesoft\",\"HubUrl\":\"mulesoft.hub.itdev.local\",\"ProviderId\":1,\"ProviderName\":\"Premiere Global Services\",\"ProviderType\":\"1\"},\"ClientId\":2690509}}";
    private final static String GET_PHONE_RESPONSE = "{\"RegisteredUserGetResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":718,\"MessageId\":\"99456601-40ad-4db5-95fb-382d9f843348\",\"ServerDateTime\":\"\\/Date(1512465619444)\\/\",\"AttendeeId\":0,\"ClientId\":1815899,\"DeletedDate\":null,\"LastUpdated\":\"\\/Date(-62135575200000-0600)\\/\",\"Phones\":[{\"CountryCode\":\"\",\"Extension\":\"\",\"LastUsed\":true,\"Number\":\"555\\/555-5555\",\"PhoneLabel\":\"Work\"}],\"RegisteredUser\":{\"AutoConnect\":false,\"Email\":\"Test25Data@PremiereGlobalServices.local\",\"FirstName\":\"testdataFirstName\",\"LastName\":\"testdataLastName\",\"Password\":null},\"RegisteredUserId\":null}}";
    private final static String CREATE_MEETINGS_RESPONSE_SUCCESS = "{\"DesktopMeetingCreateResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":2390,\"MessageId\":\"f236ee58-cc85-4293-bb25-ac36ce61a4f6\",\"ServerDateTime\":\"\\/Date(1514459319065)\\/\",\"DesktopMeetingId\":4606}}";
    private final static String CREATE_MEETINGS_RESPONSE_ERROR = "{\"DesktopMeetingCreateResult\":{\"CorrelationId\":null,\"Errors\":[{\"Code\":26119,\"Message\":\"Jwt access token validation failure.\",\"Parameter\":null,\"ParameterValue\":null,\"Severity\":1,\"Source\":0}],\"ExecutionTime\":328,\"MessageId\":\"3a627ebb-7fd7-4691-8dcf-60f40397dee8\",\"ServerDateTime\":\"/Date(1514464886406)/\",\"DesktopMeetingId\":null}}";
    private final static String UPDATE_MEETINGS_RESPONSE_SUCCESS = "{\"DesktopMeetingUpdateResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":31,\"MessageId\":\"4c37b466-9afb-492d-b435-09e6aea66869\",\"ServerDateTime\":\"\\/Date(1514459569336)\\/\"}}";
    private final static String UPDATE_MEETINGS_RESPONSE_ERROR = "{\"DesktopMeetingUpdateResult\":{\"CorrelationId\":null,\"Errors\":[{\"Code\":26119,\"Message\":\"Jwt access token validation failure.\",\"Parameter\":null,\"ParameterValue\":null,\"Severity\":1,\"Source\":0}],\"ExecutionTime\":374,\"MessageId\":\"94c74218-0574-4d3e-a513-df639fa6c756\",\"ServerDateTime\":\"/Date(1514529984833)/\"}}";
    private final static String FETCH_MEETINGS_ROOM_RESPONSE_SUCCESS = "{\"MeetingRoomGetResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":359,\"MessageId\":\"5d8ebbdb-2b9a-4ca4-9275-0eed0ef0f17f\",\"ServerDateTime\":\"/Date(1514458906913)/\",\"AttendeeStartPermission\":\"Allowed\",\"AttendeeWebPassCode\":null,\"Attendees\":[],\"AudioDetail\":{\"ConfId\":\"1000019727\",\"ConferenceName\":null,\"ListenPassCode\":\"2592458\",\"ModeratorPassCode\":\"2592454\",\"ParticipantPassCode\":\"259245\",\"bridgeOptions\":[\"GlobalMeet2\"],\"phoneInformation\":[{\"Location\":\"Canada, Toronto\",\"PhoneNumber\":\"+1 416 915 3222\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Uruguay\",\"PhoneNumber\":\"000 40 190 522\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Uruguay (toll free)\"},{\"Location\":\"Mexico\",\"PhoneNumber\":\"001 800 514 4665\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Mexico (toll free)\"},{\"Location\":\"Germany, Frankfurt\",\"PhoneNumber\":\"+49 69 2222 10764\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bahrain\",\"PhoneNumber\":\"800 81076\",\"PhoneType\":\"National free phone\",\"CustomLocation\":\"Bahrain (toll free)\"},{\"Location\":\"Hungary, Budapest\",\"PhoneNumber\":\"+36 1 778 9324\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Portugal, Lisbon\",\"PhoneNumber\":\"+351 21 366 5066\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Turkey\",\"PhoneNumber\":\"00 800 448 826 205\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Turkey (toll free)\"},{\"Location\":\"Latvia\",\"PhoneNumber\":\"8000 4272\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Latvia (toll free)\"},{\"Location\":\"Saudi Arabia\",\"PhoneNumber\":\"800 844 6206\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Saudi Arabia (toll free)\"},{\"Location\":\"Austria, Vienna\",\"PhoneNumber\":\"+43 (0) 1 25302 1754\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"BELGIUM\",\"PhoneNumber\":\"0 800 39 259\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"BELGIUM (TOLL FREE)\"},{\"Location\":\"Denmark, Copenhagen\",\"PhoneNumber\":\"+45 32 71 16 53\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"France\",\"PhoneNumber\":\"0800 946 539\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"France (toll free)\"},{\"Location\":\"Netherlands\",\"PhoneNumber\":\"0800 020 2578\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Netherlands (toll free)\"},{\"Location\":\"Portugal\",\"PhoneNumber\":\"800 784 427\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Portugal (toll free)\"},{\"Location\":\"Switzerland, Geneva\",\"PhoneNumber\":\"+41 (0) 22 595 4791\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Switzerland\",\"PhoneNumber\":\"0800 841 673\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Switzerland (toll free)\"},{\"Location\":\"UK\",\"PhoneNumber\":\"0800 358 6382\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"UK (toll free)\"},{\"Location\":\"UK, Edinburgh\",\"PhoneNumber\":\"+44 (0) 13 1460 1131\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"UK\",\"PhoneNumber\":\"0845 351 3029\",\"PhoneType\":\"Lo-call\",\"CustomLocation\":\"UK (toll free)\"},{\"Location\":\"Austria\",\"PhoneNumber\":\"0800 886 63287\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Austria (toll free)\"},{\"Location\":\"Spain\",\"PhoneNumber\":\"900 803 680\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Spain (toll free)\"},{\"Location\":\"Sweden\",\"PhoneNumber\":\"0200 125 449\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Sweden (toll free)\"},{\"Location\":\"Denmark\",\"PhoneNumber\":\"80 703 560\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Denmark (toll free)\"},{\"Location\":\"Finland\",\"PhoneNumber\":\"800 772 206\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Finland (toll free)\"},{\"Location\":\"Finland, Helsinki\",\"PhoneNumber\":\"+358 (0) 9 2310 1627\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Norway\",\"PhoneNumber\":\"800 56073\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Norway (toll free)\"},{\"Location\":\"Norway, Oslo\",\"PhoneNumber\":\"+47 21 50 27 26\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Luxembourg\",\"PhoneNumber\":\"+352 2088 1755\",\"PhoneType\":\"Local\",\"CustomLocation\":\"Luxembourg (toll free)\"},{\"Location\":\"Greece, Athens\",\"PhoneNumber\":\"+30 21 1198 2789\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bulgaria, Sofia\",\"PhoneNumber\":\"+359 (0) 2 491 7884\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Cyprus\",\"PhoneNumber\":\"800 96492\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Cyprus (toll free)\"},{\"Location\":\"Czech Republic\",\"PhoneNumber\":\"800 701 452\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Czech Republic (toll free)\"},{\"Location\":\"Czech Republic, Prague\",\"PhoneNumber\":\"+420 228 882 964\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Estonia, Tallinn\",\"PhoneNumber\":\"+372 622 6500\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Israel, Tel Aviv\",\"PhoneNumber\":\"+972 3 935 0632\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Latvia, Riga\",\"PhoneNumber\":\"+371 6778 2585\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Lithuania, Vilnius\",\"PhoneNumber\":\"+370 5205 5228\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Poland, Warsaw\",\"PhoneNumber\":\"+48 (0) 22 295 3506\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Romania\",\"PhoneNumber\":\"0800 895 861\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Romania (toll free)\"},{\"Location\":\"Romania, Bucharest\",\"PhoneNumber\":\"+40 (0) 21 529 3940\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Slovakia, Bratislava\",\"PhoneNumber\":\"+421 (0) 2 3305 9627\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Slovenia\",\"PhoneNumber\":\"0800 80944\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Slovenia (toll free)\"},{\"Location\":\"Slovenia, Ljubljana\",\"PhoneNumber\":\"+386 1 888 8284\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"South Africa,Johannesberg \",\"PhoneNumber\":\"+27 11 019 7069\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bahrain, Manama\",\"PhoneNumber\":\"+973 1619 8800\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"South Africa\",\"PhoneNumber\":\"800 984 062\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"South Africa (toll free)\"},{\"Location\":\"Ukraine\",\"PhoneNumber\":\"800 500 431\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Ukraine (toll free)\"},{\"Location\":\"Croatia\",\"PhoneNumber\":\"0800 805 913\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Croatia (toll free)\"},{\"Location\":\"Belgium, Brussels\",\"PhoneNumber\":\"+32 (0) 2 404 0661\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"France, Paris\",\"PhoneNumber\":\"+33 (0) 1 70 70 17 49\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Germany, Frankfurt\",\"PhoneNumber\":\"+49 (0) 69 2222 10768\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Germany, Munich\",\"PhoneNumber\":\"+49 (0) 89 2030 31203\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Ireland, Dublin\",\"PhoneNumber\":\"+353 (0) 1 246 5600\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Italy, Milan\",\"PhoneNumber\":\"+39 02 3600 9841\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Italy, Rome\",\"PhoneNumber\":\"+39 06 8750 0873\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Netherlands, Amsterdam\",\"PhoneNumber\":\"+31 (0) 20 716 8294\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Spain, Barcelona\",\"PhoneNumber\":\"+34 93 800 1930\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Spain, Madrid\",\"PhoneNumber\":\"+34 91 114 6580\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bulgaria\",\"PhoneNumber\":\"00800 118 4457\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Bulgaria (toll free)\"},{\"Location\":\"Sweden, Stockholm\",\"PhoneNumber\":\"+46 (0) 8 5033 6519\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Switzerland, Zurich\",\"PhoneNumber\":\"+41 (0) 44 580 7203\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Germany\",\"PhoneNumber\":\"0800 589 1851\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Germany (lo-call)\"},{\"Location\":\"UK, London\",\"PhoneNumber\":\"+44 (0) 20 3364 5727\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"UK, Reading\",\"PhoneNumber\":\"+44 (0) 11 8990 3054\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Luxembourg\",\"PhoneNumber\":\"800 27069\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Luxembourg (toll free)\"},{\"Location\":\"Hungary\",\"PhoneNumber\":\"06 800 193 87\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Hungary (toll free)\"},{\"Location\":\"Slovakia\",\"PhoneNumber\":\"0800 001 822\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Slovakia (toll free)\"},{\"Location\":\"Iceland\",\"PhoneNumber\":\"800 9906\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Iceland (toll free)\"},{\"Location\":\"Poland\",\"PhoneNumber\":\"00800 121 4328\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Poland (toll free)\"},{\"Location\":\"Ireland\",\"PhoneNumber\":\"1800 937 645\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Ireland (toll free)\"},{\"Location\":\"Greece\",\"PhoneNumber\":\"00800 128 568\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Greece (toll free)\"},{\"Location\":\"UK, Belfast\",\"PhoneNumber\":\"+44 (0) 28 9595 0027\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Israel\",\"PhoneNumber\":\"1809 212 926\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Israel (toll free)\"},{\"Location\":\"Italy\",\"PhoneNumber\":\"800 145 993\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Italy (toll free)\"},{\"Location\":\"Lithuania\",\"PhoneNumber\":\"8800 31305\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Lithuania (toll free)\"},{\"Location\":\"Vietnam\",\"PhoneNumber\":\"1800 9248\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Vietnam (toll free)\"},{\"Location\":\"Mexico, Mexico City\",\"PhoneNumber\":\"+52 55 1205 0736\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Canada, Montreal\",\"PhoneNumber\":\"+1-514-669-5882\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Argentina\",\"PhoneNumber\":\"0 800 444 4665\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\" Argentina (toll free)\"},{\"Location\":\"Chile\",\"PhoneNumber\":\"123 002 09252\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Chile (toll free)\"},{\"Location\":\"Panama\",\"PhoneNumber\":\"00 800 226 4665\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Panama (toll free)\"},{\"Location\":\"Venezuela\",\"PhoneNumber\":\"0 800 102 9664\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Venezuela (toll free)\"},{\"Location\":\"Costa Rica\",\"PhoneNumber\":\"0800 012 1751\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Costa Rica (toll free)\"},{\"Location\":\"Peru\",\"PhoneNumber\":\"0800 54 856\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Peru (toll free)\"},{\"Location\":\"New Zealand, Auckland\",\"PhoneNumber\":\"+64 (0) 9 929 1764\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Australia\",\"PhoneNumber\":\"1 800 720 497\",\"PhoneType\":\"International toll free mobile\",\"CustomLocation\":\"Australia (toll free)\"},{\"Location\":\"Hong Kong, Hong Kong\",\"PhoneNumber\":\"800 968 955\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Hong Kong (toll free)\"},{\"Location\":\"Hong Kong, Hong Kong\",\"PhoneNumber\":\"+852 3051 2730\",\"PhoneType\":\"Local\",\"CustomLocation\":\"Hong Kong (toll free)\"},{\"Location\":\"Indonesia\",\"PhoneNumber\":\"001 803 0613 2213\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Indonesia (toll free)\"},{\"Location\":\"Japan\",\"PhoneNumber\":\"0066 3386 1116\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Japan (toll free)\"},{\"Location\":\"Japan Mobile\",\"PhoneNumber\":\"0066 3386 1017\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Japan (mobile)\"},{\"Location\":\"New Zealand\",\"PhoneNumber\":\"0800 459 128\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"New Zealand (toll free)\"},{\"Location\":\"South Korea, Seoul\",\"PhoneNumber\":\"+82 (0) 2 3483 1929\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Singapore\",\"PhoneNumber\":\"800 616 3176\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Singapore (toll free)\"},{\"Location\":\"Australia, Sydney\",\"PhoneNumber\":\"+61 (0) 2 8017 5613\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Thailand\",\"PhoneNumber\":\"001 800 613 62248\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Thailand (toll free)\"},{\"Location\":\"Japan, Tokyo\",\"PhoneNumber\":\"+81 (0) 3 4588 9634\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Singapore\",\"PhoneNumber\":\"+65 6622 1723\",\"PhoneType\":\"Local\",\"CustomLocation\":\"Singapore (toll free)\"},{\"Location\":\"South Korea\",\"PhoneNumber\":\"007 986 517 503\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"South Korea (toll free)\"},{\"Location\":\"Australia\",\"PhoneNumber\":\"1 800 651 017\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Australia (toll free)\"},{\"Location\":\"Japan\",\"PhoneNumber\":\"0120 639 800\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Japan (toll free)\"},{\"Location\":\"Singapore\",\"PhoneNumber\":\"800 186 5015\",\"PhoneType\":\"International Toll Free\",\"CustomLocation\":\"Singapore (toll free)\"},{\"Location\":\"USA, Colorado Springs\",\"PhoneNumber\":\"1-855-782-9302\",\"PhoneType\":\"Toll free\",\"CustomLocation\":\"USA/Mexico\"},{\"Location\":\"USA, Colorado Springs\",\"PhoneNumber\":\"1-719-234-0010\",\"PhoneType\":\"Toll free\",\"CustomLocation\":\"USA/Mexico\"},{\"Location\":\"USA, Colorado Springs\",\"PhoneNumber\":\"1-866-643-3221\",\"PhoneType\":\"Local\",\"CustomLocation\":\"USA/Mexico\"}],\"reservationOptions\":[\"PlayEntryTone\",\"PlayExitTone\",\"RecordName\",\"AllowIntDialOut\",\"OneCallerCheck\",\"NoCallerCount\"],\"PrimaryAccessNumber\":\"+1 416 915 3222\",\"ConferenceType\":\"GMWeb\",\"sipPhoneInformation\":[{\"Location\":\"USA, Colorado Springs\",\"PhoneNumber\":\"7777100003@ff1-sy1.sip.pgilab.com\",\"PhoneType\":\"VOIP SIP\",\"CustomLocation\":\"USA/Mexico\"}]},\"ClientId\":\"1298399\",\"ConferenceOptions\":[{\"Enabled\":true,\"Name\":\"AllowChat\"},{\"Enabled\":true,\"Name\":\"WebRecording\"}],\"HubId\":167363,\"HubName\":\"dell\",\"MeetingRoomDetail\":{\"AnonymousEvaluation\":false,\"AudioConferenceId\":1000019727,\"AudioProviderType\":\"ReadyConferencePlus\",\"AutoAcceptRegistrants\":false,\"Availability\":\"Wait For Moderator\",\"Category\":\"\",\"ConferenceDescription\":\"\",\"ConferenceTitle\":\"Derrick's Meeting\",\"InvitationMessage\":\"\",\"InvitationSubject\":\"\",\"MaxParticipants\":125,\"MaxRegistrants\":125,\"ModeratorEmail\":\"derrick.fitzgerald@pgi.com\",\"ModeratorInvitationMessage\":\"\",\"ModeratorInvitationSubject\":\"\",\"ModeratorName\":\"Derrick Fitzgerald\",\"ModeratorSecurityCode\":\"17505\",\"PONumber\":\"111\",\"RecordAllEvents\":false,\"RegistrationClosedDateTime\":null,\"RoomName\":\"DerricksEverOn123\",\"SecurityCode\":\"7720\",\"SendEmailOnRegistration\":false,\"TimeZone\":\"CNTRL-BASE\",\"WebProviderType\":\"Netspoke\",\"WebConferenceOptions\":[{\"Code\":\"Full Screen\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Grant Control\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Hide Pop-ups\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Email File Link\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"File Transfer\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"File Upload\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Notes\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Present\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Web-based Video\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Chat\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Delegate\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Guest Privacy\",\"Enabled\":false,\"Locked\":false},{\"Code\":\"Poll\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Promote\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Q&A\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Record\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Webcam\",\"Enabled\":false,\"Locked\":false},{\"Code\":\"Whiteboard\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Host Aproval Required\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Participants after host joins\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Participants join immediatley\",\"Enabled\":true,\"Locked\":false},{\"Code\":\"Chat Help\",\"Enabled\":true,\"Locked\":false}],\"Secure\":true,\"DefaultLanguage\":null,\"UseHtml5\":false},\"MeetingRoomId\":1140280,\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https://dell.pgilab.net/DerricksEverOn123\",\"AttendeeRegisterUrl\":\"\",\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https://dell.pgilab.net/utilities/joinLiveConference.aspx?cid=1140280&pc=&sc=7720&msc=17505\",\"EndOfMeetingUrl\":\"\"},\"Packages\":[{\"Description\":\"EMEA. GlobalMeet Softphone Package\",\"Name\":\"EMEA. GlobalMeet Softphone Package\",\"PackageCode\":\"GMWebEMEA-1\",\"ProductData\":{\"Products\":[{\"Description\":\"GMWeb\",\"FeatureData\":{\"Features\":[{\"Description\":\"SoftPhone\",\"FeatureCode\":\"SoftphoneforGMWeb\",\"Name\":\"SoftPhone\"},{\"Description\":\"Max Participants\",\"FeatureCode\":\"MaxParts\",\"Name\":\"Max Participants\"}]},\"Name\":\"GMWeb\",\"ProductCode\":\"GMWeb\",\"HasAudioConf\":null,\"HasWebConf\":null,\"HasWebinar\":null}]}}],\"PresenterStartPermission\":\"Allowed\",\"Presenters\":[],\"Ptok\":\"37fd0f82db7aa14612880e335432bcd4ef0eb99cda6363dab0269d3ee55d2992\",\"Status\":\"Completed\",\"WebMeetingServer\":\"usw2a.qab.pgilab.net\",\"ParticipantAnonymity\":false,\"CustomAudioData\":null,\"HubBrandId\":7290,\"DeleteReason\":\"\",\"DeletedDateTime\":null,\"BrandId\":7290}}";
    private final static String FETCH_MEETINGS_ROOM_RESPONSE_ERROR = "{\"MeetingRoomGetResult\":{\"CorrelationId\":null,\"Errors\":[{\"Code\":26119,\"Message\":\"Jwt access token validation failure.\",\"Parameter\":null,\"ParameterValue\":null,\"Severity\":1,\"Source\":0}],\"ExecutionTime\":390,\"MessageId\":\"350d786f-4f55-415f-b765-7d76049a9e34\",\"ServerDateTime\":\"/Date(1514530907058)/\",\"AttendeeStartPermission\":null,\"AttendeeWebPassCode\":null,\"Attendees\":[],\"AudioDetail\":null,\"ClientId\":null,\"ConferenceOptions\":[],\"HubId\":0,\"HubName\":null,\"MeetingRoomDetail\":null,\"MeetingRoomId\":0,\"MeetingRoomUrls\":null,\"Packages\":[],\"PresenterStartPermission\":null,\"Presenters\":[],\"Ptok\":null,\"Status\":null,\"WebMeetingServer\":null,\"ParticipantAnonymity\":false,\"CustomAudioData\":null,\"HubBrandId\":0,\"DeleteReason\":null,\"DeletedDateTime\":null,\"BrandId\":0}}";
    private final static String UAPI_AUTH_RESPONSE = "{\"responseType\":\"ROOM_SESSION\",\"timestamp\":\"2018-01-03T04:08:04.067Z\",\"email\":\"amit.sapra@nagarro.com\",\"loginType\":\"CLIENT\",\"loginName\":\"amit sapra\",\"roomRole\":\"HOST\",\"authToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ1YXBpLnFhYi5nbG9iYWxtZWV0Lm5ldCIsInN1YiI6IjAzMDNiZWIwLTZiNjgtNGE0Zi1hOGFhLTRiYjJkZTFiZWNiYyIsImF1ZCI6ImUzNzExMmVlLTQyNTAtNDFlOS1hOTZmLWFjMTZlMTczOGFiYyIsImV4cCI6MTUxNDk5NTY4MywiaWF0IjoxNTE0OTUyNDgzLCJsb2dpblR5cGUiOiJDTElFTlQiLCJsb2dpblNlc3Npb25JZCI6IjIyODJmMjQ0LTgwZDktNDFiZS1iNDAxLTI5Y2NlYTJjYjY4NSIsInJvb21JZCI6IjExNDIwNTYiLCJyb29tUm9sZSI6IkhPU1QiLCJwYXJ0SWQiOiJlN2ZyZjMxeHkxOGpiemhqYjg5ZmZuNWNhIn0.e4rX2dVxWfZlB8Dsq-CewR7z2jpEIRe2MNmZiir8dhE\",\"id\":\"9251722\",\"_links\":{\"joinMeeting\":{\"href\":\"https://uapi.qab.globalmeet.net/uapi/v1/room/meeting/participants\"},\"meetingRoomInfo\":{\"href\":\"https://uapi.qab.globalmeet.net/uapi/v1/room\"}}}\n";
    private final static String JOIN_MEETING_RESPONSE = "{\"responseType\":\"ERROR_JOININGMEETING\",\"timestamp\":\"2018-01-03T04:09:52.345Z\",\"joinStatus\":\"JOIN\",\"participantId\":\"e7frf31xy18jbzhjb89ffn5ca\",\"sipIdentifier\":\"GM.e7frf31xy18jbzhjb89ffn5ca\",\"_links\":{\"getEvents\":{\"href\":\"https://uapi.qab.globalmeet.net/uapi/v1/room/meeting/events?since=-1&timeout=50000\"},\"defaultPublicChat\":{\"href\":\"https://uapi.qab.globalmeet.net/uapi/v1/room/meeting/conversations/default/chats\"}}}\n";
    private final static String ROOM_INFO_RESPONSE = "{\"responseType\":\"MEETING_ROOM_INFO\",\"timestamp\":\"2018-01-03T04:09:52.533Z\",\"title\":\"Amit's Meeting\",\"meetingOwnerName\":\"amit sapra\",\"showOldLoginFlow\":false,\"productName\":\"GlobalMeet\",\"meetingHost\":\"usw2a.qab.pgilab.net\",\"staticHost\":\"mts-cdn.qab.pgilab.net\",\"audio\":{\"audioConferenceId\":\"999964117\",\"hostPasscode\":\"7178442115\",\"guestPasscode\":\"6490879930\",\"primaryAccessNumber\":\"+1 416 915 3222\",\"accessPhoneNumbers\":[{\"phoneNumber\":\"+1 416 915 3222\",\"phoneType\":\"Local\",\"description\":\"Canada, Toronto\"},{\"phoneNumber\":\"000 40 190 522\",\"phoneType\":\"International Toll Free\",\"description\":\"Uruguay (toll free)\"},{\"phoneNumber\":\"001 800 514 4665\",\"phoneType\":\"International Toll Free\",\"description\":\"Mexico (toll free)\"},{\"phoneNumber\":\"+49 69 2222 10764\",\"phoneType\":\"Local\",\"description\":\"Germany, Frankfurt\"},{\"phoneNumber\":\"800 81076\",\"phoneType\":\"National free phone\",\"description\":\"Bahrain (toll free)\"},{\"phoneNumber\":\"+36 1 778 9324\",\"phoneType\":\"Local\",\"description\":\"Hungary, Budapest\"},{\"phoneNumber\":\"+351 21 366 5066\",\"phoneType\":\"Local\",\"description\":\"Portugal, Lisbon\"},{\"phoneNumber\":\"00 800 448 826 205\",\"phoneType\":\"International Toll Free\",\"description\":\"Turkey (toll free)\"},{\"phoneNumber\":\"8000 4272\",\"phoneType\":\"International Toll Free\",\"description\":\"Latvia (toll free)\"},{\"phoneNumber\":\"800 844 6206\",\"phoneType\":\"International Toll Free\",\"description\":\"Saudi Arabia (toll free)\"},{\"phoneNumber\":\"+43 (0) 1 25302 1754\",\"phoneType\":\"Local\",\"description\":\"Austria, Vienna\"},{\"phoneNumber\":\"0 800 39 259\",\"phoneType\":\"International Toll Free\",\"description\":\"BELGIUM (TOLL FREE)\"},{\"phoneNumber\":\"+45 32 71 16 53\",\"phoneType\":\"Local\",\"description\":\"Denmark, Copenhagen\"},{\"phoneNumber\":\"0800 946 539\",\"phoneType\":\"International Toll Free\",\"description\":\"France (toll free)\"},{\"phoneNumber\":\"0800 020 2578\",\"phoneType\":\"International Toll Free\",\"description\":\"Netherlands (toll free)\"},{\"phoneNumber\":\"800 784 427\",\"phoneType\":\"International Toll Free\",\"description\":\"Portugal (toll free)\"},{\"phoneNumber\":\"+41 (0) 22 595 4791\",\"phoneType\":\"Local\",\"description\":\"Switzerland, Geneva\"},{\"phoneNumber\":\"0800 841 673\",\"phoneType\":\"International Toll Free\",\"description\":\"Switzerland (toll free)\"},{\"phoneNumber\":\"0800 358 6382\",\"phoneType\":\"International Toll Free\",\"description\":\"UK (toll free)\"},{\"phoneNumber\":\"+44 (0) 13 1460 1131\",\"phoneType\":\"Local\",\"description\":\"UK, Edinburgh\"},{\"phoneNumber\":\"0845 351 3029\",\"phoneType\":\"Lo-call\",\"description\":\"UK (toll free)\"},{\"phoneNumber\":\"0800 886 63287\",\"phoneType\":\"International Toll Free\",\"description\":\"Austria (toll free)\"},{\"phoneNumber\":\"900 803 680\",\"phoneType\":\"International Toll Free\",\"description\":\"Spain (toll free)\"},{\"phoneNumber\":\"0200 125 449\",\"phoneType\":\"International Toll Free\",\"description\":\"Sweden (toll free)\"},{\"phoneNumber\":\"80 703 560\",\"phoneType\":\"International Toll Free\",\"description\":\"Denmark (toll free)\"},{\"phoneNumber\":\"800 772 206\",\"phoneType\":\"International Toll Free\",\"description\":\"Finland (toll free)\"},{\"phoneNumber\":\"+358 (0) 9 2310 1627\",\"phoneType\":\"Local\",\"description\":\"Finland, Helsinki\"},{\"phoneNumber\":\"800 56073\",\"phoneType\":\"International Toll Free\",\"description\":\"Norway (toll free)\"},{\"phoneNumber\":\"+47 21 50 27 26\",\"phoneType\":\"Local\",\"description\":\"Norway, Oslo\"},{\"phoneNumber\":\"+352 2088 1755\",\"phoneType\":\"Local\",\"description\":\"Luxembourg (toll free)\"},{\"phoneNumber\":\"+30 21 1198 2789\",\"phoneType\":\"Local\",\"description\":\"Greece, Athens\"},{\"phoneNumber\":\"+359 (0) 2 491 7884\",\"phoneType\":\"Local\",\"description\":\"Bulgaria, Sofia\"},{\"phoneNumber\":\"800 96492\",\"phoneType\":\"International Toll Free\",\"description\":\"Cyprus (toll free)\"},{\"phoneNumber\":\"800 701 452\",\"phoneType\":\"International Toll Free\",\"description\":\"Czech Republic (toll free)\"},{\"phoneNumber\":\"+420 228 882 964\",\"phoneType\":\"Local\",\"description\":\"Czech Republic, Prague\"},{\"phoneNumber\":\"+372 622 6500\",\"phoneType\":\"Local\",\"description\":\"Estonia, Tallinn\"},{\"phoneNumber\":\"+972 3 935 0632\",\"phoneType\":\"Local\",\"description\":\"Israel, Tel Aviv\"},{\"phoneNumber\":\"+371 6778 2585\",\"phoneType\":\"Local\",\"description\":\"Latvia, Riga\"},{\"phoneNumber\":\"+370 5205 5228\",\"phoneType\":\"Local\",\"description\":\"Lithuania, Vilnius\"},{\"phoneNumber\":\"+48 (0) 22 295 3506\",\"phoneType\":\"Local\",\"description\":\"Poland, Warsaw\"},{\"phoneNumber\":\"0800 895 861\",\"phoneType\":\"International Toll Free\",\"description\":\"Romania (toll free)\"},{\"phoneNumber\":\"+40 (0) 21 529 3940\",\"phoneType\":\"Local\",\"description\":\"Romania, Bucharest\"},{\"phoneNumber\":\"+421 (0) 2 3305 9627\",\"phoneType\":\"Local\",\"description\":\"Slovakia, Bratislava\"},{\"phoneNumber\":\"0800 80944\",\"phoneType\":\"International Toll Free\",\"description\":\"Slovenia (toll free)\"},{\"phoneNumber\":\"+386 1 888 8284\",\"phoneType\":\"Local\",\"description\":\"Slovenia, Ljubljana\"},{\"phoneNumber\":\"+27 11 019 7069\",\"phoneType\":\"Local\",\"description\":\"South Africa,Johannesberg \"},{\"phoneNumber\":\"+973 1619 8800\",\"phoneType\":\"Local\",\"description\":\"Bahrain, Manama\"},{\"phoneNumber\":\"800 984 062\",\"phoneType\":\"International Toll Free\",\"description\":\"South Africa (toll free)\"},{\"phoneNumber\":\"800 500 431\",\"phoneType\":\"International Toll Free\",\"description\":\"Ukraine (toll free)\"},{\"phoneNumber\":\"0800 805 913\",\"phoneType\":\"International Toll Free\",\"description\":\"Croatia (toll free)\"},{\"phoneNumber\":\"+32 (0) 2 404 0661\",\"phoneType\":\"Local\",\"description\":\"Belgium, Brussels\"},{\"phoneNumber\":\"+33 (0) 1 70 70 17 49\",\"phoneType\":\"Local\",\"description\":\"France, Paris\"},{\"phoneNumber\":\"+49 (0) 69 2222 10768\",\"phoneType\":\"Local\",\"description\":\"Germany, Frankfurt\"},{\"phoneNumber\":\"+49 (0) 89 2030 31203\",\"phoneType\":\"Local\",\"description\":\"Germany, Munich\"},{\"phoneNumber\":\"+353 (0) 1 246 5600\",\"phoneType\":\"Local\",\"description\":\"Ireland, Dublin\"},{\"phoneNumber\":\"+39 02 3600 9841\",\"phoneType\":\"Local\",\"description\":\"Italy, Milan\"},{\"phoneNumber\":\"+39 06 8750 0873\",\"phoneType\":\"Local\",\"description\":\"Italy, Rome\"},{\"phoneNumber\":\"+31 (0) 20 716 8294\",\"phoneType\":\"Local\",\"description\":\"Netherlands, Amsterdam\"},{\"phoneNumber\":\"+34 93 800 1930\",\"phoneType\":\"Local\",\"description\":\"Spain, Barcelona\"},{\"phoneNumber\":\"+34 91 114 6580\",\"phoneType\":\"Local\",\"description\":\"Spain, Madrid\"},{\"phoneNumber\":\"00800 118 4457\",\"phoneType\":\"International Toll Free\",\"description\":\"Bulgaria (toll free)\"},{\"phoneNumber\":\"+46 (0) 8 5033 6519\",\"phoneType\":\"Local\",\"description\":\"Sweden, Stockholm\"},{\"phoneNumber\":\"+41 (0) 44 580 7203\",\"phoneType\":\"Local\",\"description\":\"Switzerland, Zurich\"},{\"phoneNumber\":\"0800 589 1851\",\"phoneType\":\"International Toll Free\",\"description\":\"Germany (lo-call)\"},{\"phoneNumber\":\"+44 (0) 20 3364 5727\",\"phoneType\":\"Local\",\"description\":\"UK, London\"},{\"phoneNumber\":\"+44 (0) 11 8990 3054\",\"phoneType\":\"Local\",\"description\":\"UK, Reading\"},{\"phoneNumber\":\"800 27069\",\"phoneType\":\"International Toll Free\",\"description\":\"Luxembourg (toll free)\"},{\"phoneNumber\":\"06 800 193 87\",\"phoneType\":\"International Toll Free\",\"description\":\"Hungary (toll free)\"},{\"phoneNumber\":\"0800 001 822\",\"phoneType\":\"International Toll Free\",\"description\":\"Slovakia (toll free)\"},{\"phoneNumber\":\"800 9906\",\"phoneType\":\"International Toll Free\",\"description\":\"Iceland (toll free)\"},{\"phoneNumber\":\"00800 121 4328\",\"phoneType\":\"International Toll Free\",\"description\":\"Poland (toll free)\"},{\"phoneNumber\":\"1800 937 645\",\"phoneType\":\"International Toll Free\",\"description\":\"Ireland (toll free)\"},{\"phoneNumber\":\"00800 128 568\",\"phoneType\":\"International Toll Free\",\"description\":\"Greece (toll free)\"},{\"phoneNumber\":\"+44 (0) 28 9595 0027\",\"phoneType\":\"Local\",\"description\":\"UK, Belfast\"},{\"phoneNumber\":\"1809 212 926\",\"phoneType\":\"International Toll Free\",\"description\":\"Israel (toll free)\"},{\"phoneNumber\":\"800 145 993\",\"phoneType\":\"International Toll Free\",\"description\":\"Italy (toll free)\"},{\"phoneNumber\":\"8800 31305\",\"phoneType\":\"International Toll Free\",\"description\":\"Lithuania (toll free)\"},{\"phoneNumber\":\"1 800 164 100 22\",\"phoneType\":\"International Toll Free\",\"description\":\"Philippines (toll free)\"},{\"phoneNumber\":\"1800 9248\",\"phoneType\":\"International Toll Free\",\"description\":\"Vietnam (toll free)\"},{\"phoneNumber\":\"+55 21 4063 7853\",\"phoneType\":\"Local\",\"description\":\"Brazil, Rio de Janeiro\"},{\"phoneNumber\":\"+55 11 3163 0502\",\"phoneType\":\"Local\",\"description\":\"Brazil, Sao Paulo\"},{\"phoneNumber\":\"+52 55 1205 0736\",\"phoneType\":\"Local\",\"description\":\"Mexico, Mexico City\"},{\"phoneNumber\":\"+1-514-669-5882\",\"phoneType\":\"Local\",\"description\":\"Canada, Montreal\"},{\"phoneNumber\":\"0 800 444 4665\",\"phoneType\":\"International Toll Free\",\"description\":\" Argentina (toll free)\"},{\"phoneNumber\":\"123 002 09252\",\"phoneType\":\"International Toll Free\",\"description\":\"Chile (toll free)\"},{\"phoneNumber\":\"00 800 226 4665\",\"phoneType\":\"International Toll Free\",\"description\":\"Panama (toll free)\"},{\"phoneNumber\":\"0 800 102 9664\",\"phoneType\":\"International Toll Free\",\"description\":\"Venezuela (toll free)\"},{\"phoneNumber\":\"0800 012 1751\",\"phoneType\":\"International Toll Free\",\"description\":\"Costa Rica (toll free)\"},{\"phoneNumber\":\"0800 54 856\",\"phoneType\":\"International Toll Free\",\"description\":\"Peru (toll free)\"},{\"phoneNumber\":\"+64 (0) 9 929 1764\",\"phoneType\":\"Local\",\"description\":\"New Zealand, Auckland\"},{\"phoneNumber\":\"1 800 720 497\",\"phoneType\":\"International toll free mobile\",\"description\":\"Australia (toll free)\"},{\"phoneNumber\":\"+400 120 0524\",\"phoneType\":\"Lo-call\",\"description\":\"China (lo-call)\"},{\"phoneNumber\":\"800 968 955\",\"phoneType\":\"International Toll Free\",\"description\":\"Hong Kong (toll free)\"},{\"phoneNumber\":\"+852 3051 2730\",\"phoneType\":\"Local\",\"description\":\"Hong Kong (toll free)\"},{\"phoneNumber\":\"001 803 0613 2213\",\"phoneType\":\"International Toll Free\",\"description\":\"Indonesia (toll free)\"},{\"phoneNumber\":\"0066 3386 1116\",\"phoneType\":\"International Toll Free\",\"description\":\"Japan (toll free)\"},{\"phoneNumber\":\"0066 3386 1017\",\"phoneType\":\"International Toll Free\",\"description\":\"Japan (mobile)\"},{\"phoneNumber\":\"+60 (0) 3 7723 7165\",\"phoneType\":\"Local\",\"description\":\"Malaysia,Kuala Lumpur\"},{\"phoneNumber\":\"1 800 815 806\",\"phoneType\":\"International Toll Free\",\"description\":\"Malaysia (toll free)\"},{\"phoneNumber\":\"+91 (0) 22 6150 1741\",\"phoneType\":\"Local\",\"description\":\"India, Mumbai\"},{\"phoneNumber\":\"0800 459 128\",\"phoneType\":\"International Toll Free\",\"description\":\"New Zealand (toll free)\"},{\"phoneNumber\":\"+82 (0) 2 3483 1929\",\"phoneType\":\"Local\",\"description\":\"South Korea, Seoul\"},{\"phoneNumber\":\"800 616 3176\",\"phoneType\":\"International Toll Free\",\"description\":\"Singapore (toll free)\"},{\"phoneNumber\":\"+61 (0) 2 8017 5613\",\"phoneType\":\"Local\",\"description\":\"Australia, Sydney\"},{\"phoneNumber\":\"+886 (0) 2 2656 7247\",\"phoneType\":\"Local\",\"description\":\"Taiwan, Taipei\"},{\"phoneNumber\":\"00 801 615 185\",\"phoneType\":\"International Toll Free\",\"description\":\"Taiwan (toll free)\"},{\"phoneNumber\":\"001 800 613 62248\",\"phoneType\":\"International Toll Free\",\"description\":\"Thailand (toll free)\"},{\"phoneNumber\":\"+86 10 5904 5011\",\"phoneType\":\"Local\",\"description\":\"China, Beijing\"},{\"phoneNumber\":\"+81 (0) 3 4588 9634\",\"phoneType\":\"Local\",\"description\":\"Japan, Tokyo\"},{\"phoneNumber\":\"+65 6622 1723\",\"phoneType\":\"Local\",\"description\":\"Singapore (toll free)\"},{\"phoneNumber\":\"007 986 517 503\",\"phoneType\":\"International Toll Free\",\"description\":\"South Korea (toll free)\"},{\"phoneNumber\":\"00 806 651 935\",\"phoneType\":\"International Toll Free\",\"description\":\"Taiwan (toll free)\"},{\"phoneNumber\":\"1 800 651 017\",\"phoneType\":\"International Toll Free\",\"description\":\"Australia (toll free)\"},{\"phoneNumber\":\"+400 120 0519\",\"phoneType\":\"Lo-call\",\"description\":\"China (lo-call)\"},{\"phoneNumber\":\"+86 10 5904 5002\",\"phoneType\":\"Local\",\"description\":\"China, Beijing\"},{\"phoneNumber\":\"+91 80 6127 5055\",\"phoneType\":\"Local\",\"description\":\"India, Bangalore\"},{\"phoneNumber\":\"0120 639 800\",\"phoneType\":\"International Toll Free\",\"description\":\"Japan (toll free)\"},{\"phoneNumber\":\"800 186 5015\",\"phoneType\":\"International Toll Free\",\"description\":\"Singapore (toll free)\"},{\"phoneNumber\":\"+91 11 6641 1356\",\"phoneType\":\"Local\",\"description\":\"India, Delhi\"},{\"phoneNumber\":\"000 800 1007 702\",\"phoneType\":\"International Toll Free\",\"description\":\"India (toll free)\"},{\"phoneNumber\":\"1-855-782-9302\",\"phoneType\":\"Toll free\",\"description\":\"USA/Mexico\"},{\"phoneNumber\":\"1-719-234-0010\",\"phoneType\":\"Toll free\",\"description\":\"USA/Mexico\"},{\"phoneNumber\":\"1-866-643-3221\",\"phoneType\":\"Local\",\"description\":\"USA/Mexico\"},{\"phoneNumber\":\"+91 44 6688 6114\",\"phoneType\":\"International Toll Free\",\"description\":\"Nigeria\"}],\"sipURI\":\"7777100003@ff1-sy1.sip.pgilab.com\"},\"_links\":{\"companyLogo\":{\"href\":\"https://cobranding-qab.s3.amazonaws.com/7290/Logo1.png\"},\"productLogo\":{\"href\":\"http://static.pgilab.net/branding/logos/globalMeetLogo.png\"},\"friendlyUrl\":{\"href\":\"https://dell.pgilab.net/AmitSapra3\"}}}";
    private static final String END_MEETING_RESPONSE = "{\"responseType\" : \"END_MEETING\", \"timestamp\" : \"2018-01-04T02:52:34.698Z\" }";
    private static final String WEB_DIAL_OUT_RESPONSE = "{\"responseType\":\"DIAL_OUT\",\"timestamp\":\"2018-01-30T06:29:54.914Z\"}";
    private static final String ADD_CHAT_RESPONSE = "{\"responseType\":\"ADD_CHAT\",\"timestamp\":\"2018-01-30T06:29:54.914Z\"}";
    private static final String DISMISS_AUDIO_PARTICIPANT_RESPONSE = "{\"responseType\":\"DISMISS_AUDIO_PARTICIPANT\",\"timestamp\":\"2018-01-30T06:36:28.055Z\"}";

    private static final String DISMISS_PARTICIPANT_RESPONSE = "{\n" +
            "  \"responseType\" : \"DISMISS_PARTICIPANT\",\n" +
            "  \"timestamp\" : \"2018-01-18T02:31:12.091Z\"\n" +
            "}";

    private static final String UPDATE_PARTICIPANT_RESPONSE = "{\n" +
            "  \"responseType\" : \"UPDATE_PARTICIPANT\",\n" +
            "  \"timestamp\" : \"2018-01-18T02:31:12.505Z\"\n" +
            "}";

    private static final String PARTICIPANT_RESPONSE_FAILURE = "{\n" +
            "  \"responseType\" : \"ERROR\",\n" +
            "  \"timestamp\" : \"2018-01-18T02:31:12.595Z\",\n" +
            "  \"errors\" : [ {\n" +
            "    \"errorCode\" : 403,\n" +
            "    \"errorMessage\" : \"Access Denied\"\n" +
            "  } ]\n" +
            "}";

    private static final String SUGGEST_RESPONSE = "[\n" +
            "  {\n" +
            "    \"id\": \"1026472\",\n" +
            "    \"resourceUri\": \"/meetingrooms/1026472\",\n" +
            "    \"resourceType\": \"MeetingRoom\",\n" +
            "    \"title\": \"Kevin McAdams\",\n" +
            "    \"secondaryTitle\": \"http://pgi.globalmeet.com/kevin\",\n" +
            "    \"externalUrl\": \"http://pgi.globalmeet.com/kevin\",\n" +
            "    \"metadata\": {\n" +
            "      \"meetingRoomType\": \"globalmeet5\",\n" +
            "      \"webMeetingServer\": \"web-na.globalmeet.com\",\n" +
            "      \"conferenceType\": \"Web and Audio\",\n" +
            "      \"conferenceId\": \"7638248\"\n" +
            "    }\n" +
            "  }\n" +
            "]\n";

    private static final String SEARCH_RESPONSE = "{\n" +
            "  \"totalCount\": 1,\n" +
            "  \"items\": [\n" +
            "    {\n" +
            "      \"id\": \"1026472\",\n" +
            "      \"resourceUri\": \"/meetingrooms/1026472\",\n" +
            "      \"resourceType\": \"MeetingRoom\",\n" +
            "      \"title\": \"Kevin McAdams\",\n" +
            "      \"secondaryTitle\": \"http://pgi.globalmeet.com/kevin\",\n" +
            "      \"externalUrl\": \"http://pgi.globalmeet.com/kevin\",\n" +
            "      \"metadata\": {\n" +
            "        \"meetingRoomType\": \"globalmeet5\",\n" +
            "        \"webMeetingServer\": \"web-na.globalmeet.com\",\n" +
            "        \"conferenceType\": \"Web and Audio\",\n" +
            "        \"conferenceId\": \"7638248\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";


    private static final String MUTEUNMUTE_RESPONSE_FAILURE = "{\n" +
            "  \"responseType\" : \"ERROR\",\n" +
            "  \"timestamp\" : \"2018-01-18T02:31:12.595Z\",\n" +
            "  \"errors\" : [ {\n" +
            "    \"errorCode\" : 403,\n" +
            "    \"errorMessage\" : \"Access Denied\"\n" +
            "  } ]\n" +
            "}";
    private static final String MUTEUNMUTE_RESPONSE =
            "{\"responseType\":\"UPDATE_AUDIO_PARTICIPANT\",\"timestamp\":\"2018-01-18T02:31:12.595Z\"}";


    private static final String FETCH_MEETINGS_ROOM_RESQUEST_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/GlobalMeet.svc/MeetingRoom/1140280";
    private static final String UPDATE_MEETINGS_RESQUEST_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/4606";
    private static final String CREATE_MEETINGS_RESQUEST_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/New";
    private static final String RECENT_MEETINGS_RESQUEST_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/Search?type=Client&clientId=5078758&maxItems=50&includeDeleted=false&includeExpiredInvites=false";
    private static final String CLIENT_INFO_REQ_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/ApplicationServices.svc/ClientInfo/token=null";
    private static final String MEETING_INFO_REQ_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/GlobalMeet.svc/MeetingRoom/1141409";
    private static final String SETTING_UPDATE_NAME_REQ_URL = BuildConfig.PGI_BASE_URL + "/REST/V1/Services/Client.svc/Client?clientId=2690509&token=eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
    private static final String GET_PHONE_REQ_URL = BuildConfig.PGI_BASE_URL + "/Rest/V1/Services/GlobalMeet.svc/RegisteredUser/Get?clientId=111111";
    private static final String SUBSCRIBE_AUDIO_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/subscribe?pgiToken=asdwercigwnobanstrgfoivbn.kovmaerovnaerivdnaoeivnrao.invoinvameroamvoesvroiG";
    private static final String SUBSCRIBE_AUDIO_MEETING_ERR = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/subscribe?pgiToken=";
    private static final String SUBSCRIBE_AUDIO_MEETING_TIMEOUT = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/subscribe?pgiToken=asdwercigwnobanstrgfoivbn.kovmaerovnaerivdnaoeivnrao.timeout";
    private static final String UNSUBSCRIBE_AUDIO_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/unsubscribe?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41";
    private static final String UNSUBSCRIBE_AUDIO_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/unsubscribe?sessionid=";
    private static final String CONF_STATE_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/getconferencestate?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599";
    private static final String CONF_STATE_MEETING_URL_INVALID_CONF_ID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/getconferencestate?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=";
    private static final String CONF_STATE_MEETING_URL_INACTIVE = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/getconferencestate?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=3729599";
    private static final String START_CONF_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/startconference?sessionId=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599";
    private static final String CONF_WATCH_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferencewatch?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=";
    private static final String CONF_WATCH_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferencewatch?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599";
    private static final String CONF_OPTIONS_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferenceoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599&option=TalkerNotifyOn";
    private static final String CONF_OPTIONS_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferenceoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=&option=TalkerNotifyOn";
    private static final String CONF_OPTIONS_MEETING_URL_MUTE = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferenceoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599&option=ConfMute";
    private static final String CONF_OPTIONS_MEETING_URL_MUTE_FAILED = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setconferenceoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599&option=ConfMuteFailed";
    private static final String CLEAR_CONF_WATCH_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/clearconferencewatch?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599";
    private static final String CLEAR_CONF_WATCH_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/clearconferencewatch?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=";
    private static final String END_CONF_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/endconference?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599";
    private static final String END_CONF_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/endconference?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=";
    private static final String LEAVE_CONF_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/hangup?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599&participantId=8001-37866308";
    private static final String LEAVE_CONF_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/hangup?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=&participantId=8001-37866308";
    private static final String PART_OPTIONS_MEETING_URL = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setparticipantoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=113729599&participantid=8001-37866308&option=MuteOn";
    private static final String PART_OPTIONS_MEETING_URL_INVALID = BuildConfig.PIA_BASE_URL + "/2.0/PiaRestWebServices.svc/setparticipantoption?sessionid=FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41&conferenceId=&participantid=8001-37866308&option=MuteOff";
    private static final String FORGOT_PSW_SERVICE_REQ = BuildConfig.IDENTITY_BASE_URL + "/users/abhishek.gupta@pgi.com/forgotPasswordEmail?auth_by_client=true";

    private static final String UPDATE_PARTICIPANT_URL = BuildConfig.UAPI_BASE_URL + "/uapi/v1/room/meeting/participants/8qm6nrwx4kqowavefo2owrnmx";
    private static final String DISMISS_PARTICIPANT_URL = BuildConfig.UAPI_BASE_URL + "/uapi/v1/room/meeting/participants/8qm6nrwx4kqowavefo2owrnmx";
    private static final String UAPI_AUTHORIZATION_URL = BuildConfig.UAPI_BASE_URL +"/uapi/v1/session";
    private static final String JOIN_MEETING_URL = BuildConfig.UAPI_BASE_URL +"/uapi/v1/room/meeting/participants";
    private static final String ROOM_INFO_URL = BuildConfig.UAPI_BASE_URL +"/uapi/v1/room";
    private static final String END_MEETING_URL = BuildConfig.UAPI_BASE_URL+"/uapi/v1/room/meeting?audio=true";
    private static final String WEB_DIAL_OUT_URL = BuildConfig.UAPI_BASE_URL+"/uapi/v1/room/meeting/participant/dialout";
    private static final String DISMISS_AUDIO_PARTICIPANT_URL = BuildConfig.UAPI_BASE_URL + "/uapi/v1/room/meeting/audio/participants/PART2d9e5f62d27d960afe68508d1dd9a4ad00775048839911";
    private static final String ADD_CHAT_URL = BuildConfig.UAPI_BASE_URL+"/uapi/v1/room/meeting/conversations/default/chats";
    private static final String SUGGEST_URL = BuildConfig.GMSEARCH_URL+"/suggest";
    private static final String SEARCH_URL = BuildConfig.GMSEARCH_URL+"/search";
    private static final String MUTEUNMITE_PARTICIPANT_URL = BuildConfig.UAPI_BASE_URL + "/uapi/v1/room/meeting/audio/participants/8qm6nrwx4kqowavefo2owrnmx";
    private static final String MUTEUNMITE_PARTICIPANT_URL_FAILURE = BuildConfig.UAPI_BASE_URL + "/uapi/v1/room/meeting/audio/participants/testfailure";


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        String responseString;
        int responseCode;
        // Get Request URI.
        final HttpUrl uri = chain.request().url();
        // Get Query String.
        final String query = uri.toString();

        if (query.equals(FETCH_MEETINGS_ROOM_RESQUEST_URL)) {
            String header = chain.request().header("Authorization");
            if (header != null && header.contains("old.access.token")) {
                responseString = FETCH_MEETINGS_ROOM_RESPONSE_ERROR;
                responseCode = 401;
            } else {
                responseString = FETCH_MEETINGS_ROOM_RESPONSE_SUCCESS;
                responseCode = 200;
            }
        } else if (query.equals(UPDATE_MEETINGS_RESQUEST_URL)) {
            String header = chain.request().header("Authorization");
            if (header != null && header.contains("old.access.token")) {
                responseString = UPDATE_MEETINGS_RESPONSE_ERROR;
                responseCode = 401;
            } else {
                responseString = UPDATE_MEETINGS_RESPONSE_SUCCESS;
                responseCode = 200;
            }
        }  else if (query.equals(RECENT_MEETINGS_RESQUEST_URL)) {
            String header = chain.request().header("Authorization");
            if (header != null && header.contains("old.access.token")) {
                responseString = RECENT_MEETINGS_ERROR_RESPONSE;
                responseCode = 401;
            } else {
                responseString = RECENT_MEETING_RESPONSE;
                responseCode = 200;
            }
        } else if (query.equals(CLIENT_INFO_REQ_URL)) {
            String header = chain.request().header("Authorization");
            if (header != null && header.contains("old.access.token")) {
                responseString = CLIENT_INFO_ERR_RESPONSE;
                responseCode = 401;
            } else {
                responseString = CLIENT_INFO_RESPONSE;
                responseCode = 200;
            }
        } else if (query.equals(MEETING_INFO_REQ_URL)) {
            String header = chain.request().header("Authorization");
            if (header != null && header.contains("old.access.token")) {
                responseString = CLIENT_INFO_ERR_RESPONSE;
                responseCode = 401;
            } else {
                responseString = CLIENT_INFO_RESPONSE;
                responseCode = 200;
            }
        }
//        else if (query.equalsIgnoreCase(SUBSCRIBE_AUDIO_MEETING_URL)) {
//            String header = chain.request().header("Authorization");
//            if (header.contains("old.access.token")) {
//                responseString = RECENT_MEETINGS_ERROR_RESPONSE;
//                responseCode = 401;
//            } else if(header.equalsIgnoreCase("asdwercigwnobanstrgfoivbn.kovmaerovnaerivdnaoeivnrao.timeout")){
//                responseString = TIMEOUT_SESSION_RESPONSE;
//                responseCode = 200;
//            }else{
//                responseString = SUBSCRIBE_MEETING_RESPONSE;
//                responseCode = 200;
//            }
//
//        }
        else if (query.equalsIgnoreCase(SUBSCRIBE_AUDIO_MEETING_URL)) {
            responseString = SUBSCRIBE_MEETING_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(SUBSCRIBE_AUDIO_MEETING_ERR)) {
            responseString = INVALID_TOKEN_SUBSCRIBE_RESPONSE;
            responseCode = 401;
        } else if (query.equalsIgnoreCase(SUBSCRIBE_AUDIO_MEETING_TIMEOUT)) {
            responseString = TIMEOUT_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_STATE_MEETING_URL)) {
            responseString = ACTIVE_CONF_STATE_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_STATE_MEETING_URL_INACTIVE)) {
            responseString = INACTIVE_CONF_STATE_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_STATE_MEETING_URL_INVALID_CONF_ID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 401;
        } else if (query.equalsIgnoreCase(UNSUBSCRIBE_AUDIO_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(UNSUBSCRIBE_AUDIO_MEETING_URL_INVALID)) {
            responseString = INVALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_WATCH_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_WATCH_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CLEAR_CONF_WATCH_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CLEAR_CONF_WATCH_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_OPTIONS_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_OPTIONS_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_OPTIONS_MEETING_URL_MUTE)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(CONF_OPTIONS_MEETING_URL_MUTE_FAILED)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 403;
        } else if (query.equalsIgnoreCase(END_CONF_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(END_CONF_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(LEAVE_CONF_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(LEAVE_CONF_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(PART_OPTIONS_MEETING_URL)) {
            responseString = VALID_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(PART_OPTIONS_MEETING_URL_INVALID)) {
            responseString = INVALID_CONF_SESSION_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(SETTING_UPDATE_NAME_REQ_URL)) {
            responseString = UPDATE_NAME_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(FORGOT_PSW_SERVICE_REQ)) {
            responseString = "";
            responseCode = 204;
        } else if (query.equalsIgnoreCase(GET_PHONE_REQ_URL)) {
            responseString = GET_PHONE_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(UAPI_AUTHORIZATION_URL)) {
            responseString = UAPI_AUTH_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(JOIN_MEETING_URL)) {
            responseString = JOIN_MEETING_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(ROOM_INFO_URL)) {
            responseString = ROOM_INFO_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(END_MEETING_URL)) {
            responseString = END_MEETING_RESPONSE;
            responseCode = 200;
        }

//        else if(query.equalsIgnoreCase(WEB_DIAL_OUT_URL)){
//            Buffer buffer = new Buffer();
//            RequestBody body = chain.request().body();
//            if (body != null) {
//                body.writeTo(buffer);
//            }
////            WebDialOutRequestModel webMeetingResponse = DEFAULT_MAPPER.readValue(buffer.readUtf8(), WebDialOutRequestModel.class);
//            if(null!=webMeetingResponse && webMeetingResponse.getPhoneNumber().equals("1233456787")) {
//                responseString = WEB_DIAL_OUT_RESPONSE;
//                responseCode = 200;
//            } else {
//                responseString = "Invalid phone number";
//                responseCode = 400;
//            }
//        } else if(query.equalsIgnoreCase(ADD_CHAT_URL)){
//            Buffer buffer = new Buffer();
//            RequestBody body = chain.request().body();
//            if (body != null) {
//                body.writeTo(buffer);
//            }
//            AddChatRequestModel addChatResponse = DEFAULT_MAPPER.readValue(buffer.readUtf8(), AddChatRequestModel.class);
//            if(addChatResponse.getChatMessage().equalsIgnoreCase("Hello world")) {
//                responseString = ADD_CHAT_RESPONSE;
//                responseCode = 200;
//            } else {
//                responseString = "Invalid String";
//                responseCode = 400;
//            }
//        }
        else if(query.equalsIgnoreCase(DISMISS_AUDIO_PARTICIPANT_URL)){
            responseString = DISMISS_AUDIO_PARTICIPANT_RESPONSE;
            responseCode = 200;
        } else if (query.equalsIgnoreCase(DISMISS_PARTICIPANT_URL)) {

            if (response != null && response.code() != 200) {
                responseString = PARTICIPANT_RESPONSE_FAILURE;
                responseCode = 200;
            } else {
                responseString = DISMISS_PARTICIPANT_RESPONSE;
                responseCode = 200;
            }
        } else if (query.equalsIgnoreCase(UPDATE_PARTICIPANT_URL)) {
            if (response != null && response.code() != 200) {
                responseString = PARTICIPANT_RESPONSE_FAILURE;
                responseCode = 200;
            } else {
                responseString = UPDATE_PARTICIPANT_RESPONSE;
                responseCode = 200;
            }
        } else if (query.equalsIgnoreCase(SUGGEST_URL)) {
            if (response != null && response.code() != 200) {
                responseString = SUGGEST_RESPONSE;
                responseCode = 200;
            } else {
                responseString = UPDATE_PARTICIPANT_RESPONSE;
                responseCode = 200;
            }
        } else if (query.equalsIgnoreCase(SEARCH_URL)) {
            if (response != null && response.code() != 200) {
                responseString = SEARCH_RESPONSE;
                responseCode = 200;
            } else {
                responseString = UPDATE_PARTICIPANT_RESPONSE;
                responseCode = 200;
            }

        } else if (query.equalsIgnoreCase(MUTEUNMITE_PARTICIPANT_URL)) {
            responseString = MUTEUNMUTE_RESPONSE;
            responseCode = 202;
        } else if (query.equalsIgnoreCase(MUTEUNMITE_PARTICIPANT_URL_FAILURE)) {
            responseString = MUTEUNMUTE_RESPONSE_FAILURE;
            responseCode = 403;

        } else {
            if (query.contains("28.49,77.07.json")) {
                responseString = WEATHER_INFO1;
                responseCode = 200;
            } else if (query.contains("0.0,0.0.json")) {
                responseString = WEATHER_INFO2;
                responseCode = 200;
            } else {
                responseString = "";
                responseCode = 401;
            }
        }

        response = new Response.Builder()
                .code(responseCode)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("firstName-type", "application/json")
                .build();

        return response;
    }
}