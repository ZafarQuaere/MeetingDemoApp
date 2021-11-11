//package com.pgi.convergencemeetings;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.AudioManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Handler;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pgi.convergencemeetings.meetings.gm5.ui.WebMeetingActivity;
//import ConvergenceApplication;
//import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhone;
//import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.softphone.PGiSoftPhoneCallback;
//import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneModel;
//import com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone.PGiSoftPhoneService;
//import AppConstants;
//import ConnectionState;
//import ConnectionType;
//import com.pgi.convergencemeetings.meetings.audio.ui.AudioSelectionFragment;
//import com.pgi.convergencemeetings.meetings.audio.ui.AudioSelectionProgressFragment;
//import com.pgi.convergencemeetings.greendao.ApplicationDao;
//import SharedPreferencesManager;
//import com.pgi.convergencemeetings.models.ClientInfoResult;
//import com.pgi.convergencemeetings.models.ClientInfoResultDao;
//import com.pgi.convergencemeetings.models.MeetingRoom;
//import com.pgi.convergencemeetings.models.uapi.Event;
//import com.pgi.convergencemeetings.models.uapi.MeetingEvents;
//import com.pgi.convergencemeetings.networkmocks.FakeRestClient;
//import com.pgi.convergencemeetings.uapieventsmanager.UAPIEventsManager;
//import com.pgi.convergencemeetings.utils.AppAuthUtils;
//import com.pgi.convergence.managers.AudioRouteManager;
//import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
//import com.pgi.convergencemeetings.utils.ClientInfoResultCache;
//import com.pgi.convergencemeetings.utils.ConferenceManager;
//import com.pgi.convergencemeetings.utils.LoggerUtil;
//import com.pgi.convergence.listeners.PermissionAskListener;
//import com.pgi.convergence.utils.PermissionUtil;
//import com.pgi.network.IdentityServiceAPI;
//import com.pgi.network.IdentityServiceManager;
//import com.pgi.network.PgiWebServiceAPI;
//import com.pgi.network.PgiWebServiceManager;
//import com.pgi.network.ServerManager;
//import com.pgi.network.UAPIServiceAPI;
//import com.pgi.network.UAPIServiceManager;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.powermock.reflect.Whitebox;
//import org.robolectric.Robolectric;
//import org.robolectric.RuntimeEnvironment;
//import org.robolectric.Shadows;
//import org.robolectric.android.controller.ActivityController;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowApplication;
//import org.robolectric.shadows.ShadowConnectivityManager;
//import org.robolectric.shadows.ShadowDrawable;
//import org.robolectric.shadows.ShadowNetworkInfo;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by sudheer chilumula on 07/30/2018.
// */
//
//@Config(shadows = {ShadowShimmerlayout.class})
//@PrepareForTest({ClientInfoDaoUtils.class, ContextCompat.class, ActivityCompat.class,
//        LoggerUtil.class, ApplicationDao.class, AppAuthUtils.class, UAPIServiceManager.class,
//        PgiWebServiceManager.class, IdentityServiceManager.class, ServerManager.class,
//        UAPIEventsManager.class, SharedPreferencesManager.class, PGiSoftPhoneCallback.class, PGiSoftPhoneService.class})
//public class WebMeetingActivityTest extends RobolectricTest {
//
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//
//    @InjectMocks
//    private WebMeetingActivity mWebMeetingActivity;
//
//    @Mock
//    private ClientInfoDaoUtils mClientInfoDaoUtils;
//    @Mock
//    private LoggerUtil mLoggerUtil;
//    @Mock
//    private ApplicationDao mApplicationDao;
//    @Mock
//    private ClientInfoResultDao mClientInfoResultDao;
//    @Mock
//    private AppAuthUtils mAppAuthUtils;
//    @Mock
//    private UAPIServiceManager mUAPIServiceManager;
//    @Mock
//    private PgiWebServiceManager mPgiWebServiceManager;
//    @Mock
//    private IdentityServiceManager mIdentityServiceManager;
//    @Mock
//    private ServerManager mServerManager;
//    @Mock
//    private UAPIEventsManager mUAPIEventsManager;
//    @Mock
//    private AudioRouteManager mAudioRouteManager;
//    @Mock
//    private AudioManager mAudioManager;
//    @Mock
//    private SharedPreferencesManager mSharedPreferencesManager;
//    @Mock
//    private ContextCompat mContextCompat;
//    @Mock
//    private PGiSoftPhoneService mPGiSoftPhoneService;
//    @Mock
//    private PGiSoftPhone mPGiSoftPhone;
//    @Mock
//    private PGiSoftPhoneCallback mSoftphoneCallbacks;
//
//    private Context mContext;
//    private ActivityController<WebMeetingActivity> controller;
//    private UAPIServiceAPI mUAPIServiceAPI;
//    private PgiWebServiceAPI mPgiWebServiceAPI;
//    private IdentityServiceAPI mIdentityServiceAPI;
//    private ConnectivityManager mConnectivityManager;
//    private ShadowConnectivityManager mShadowConnectivityManager;
//    private ShadowNetworkInfo mShadowNetworkInfo;
//    private FrameLayout mFlFragmentHolder;
//    private RelativeLayout mWebMeetingView;
//    private ViewGroup mShimmerGroup;
//    // private Handler mHandler = new Handler();
//
//    private static final ObjectMapper mapper = new ObjectMapper().configure
//            (DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//    private String clientInfoResultStr = "{\"ExecutionTime\":328," +
//            "\"MessageId\":\"13120412-5781-438d-9d4a-9de9c11fafa5\",\"ServerDateTime\":\"/Date" +
//            "(1532752851530)/\",\"AudioOnlyConferences\":[],\"ClientDetails\":{\"Active\":true," +
//            "\"BillToId\":5598473,\"ClientContactDetails\":{\"Address\":{\"Address1\":\"\"," +
//            "\"Address2\":\"\",\"Address3\":\"\",\"City\":\"\",\"CountryCode\":\"USA\"," +
//            "\"PostalCode\":\"-\",\"Province\":\"\",\"StateCode\":\"\",\"TimeZone\":null}," +
//            "\"Email\":\"sudheer.chilumula@pgi.com\",\"Fax\":\"\",\"FaxIntlPrefix\":\"\"," +
//            "\"FirstName\":\"Sudheer\",\"HomePhone\":\"\",\"HomePhoneExt\":\"\"," +
//            "\"HomePhoneIntlPrefix\":\"\",\"HomePhoneIsDefault\":false,\"JobTitle\":\"\"," +
//            "\"LastName\":\"Chilumula\",\"MobilePhone\":\"\",\"MobilePhoneExt\":\"\"," +
//            "\"MobilePhoneIntlPrefix\":\"\",\"MobilePhoneIsDefault\":false," +
//            "\"Phone\":\"000/000-0000\",\"PhoneExt\":\"\",\"PhoneIntlPrefix\":\"\"," +
//            "\"PhoneIsDefault\":false,\"Salutation\":\"\",\"SecondaryPhone\":null," +
//            "\"SecondaryPhoneExt\":null,\"SecondaryPhoneIntlPrefix\":null," +
//            "\"SecondaryPhoneIsDefault\":false},\"ClientId\":\"123456\"," +
//            "\"CreditCardDetail\":null,\"DefaultLanguage\":\"EN\",\"Hold\":false," +
//            "\"OperationComments\":\"\",\"Password\":\"******\"," +
//            "\"PasswordCanBeModified\":true,\"PromotionCodes\":[],\"SoftPhoneAutoConnect\":false," +
//            "\"SoftPhoneEnable\":true,\"SpecialInfo\":\"\",\"SubscriptionId\":\"\"," +
//            "\"TerritoryCode\":\"IC\",\"BrandId\":617,\"ProfileImageUrl\":null}," +
//            "\"CompanyDetails\":{\"BlockedServices\":[\"ReadyCast Protect\",\"ReadyConference " +
//            "Plus (Web)\",\"iMeet (Visual Conferencing)\",\"LotusLiveMeeting\"]," +
//            "\"CompanyId\":7975,\"Name\":\"Premiere Global Services, Inc.\"," +
//            "\"DefaultGlobalMeetVersion\":\"GM5\",\"DisplayGlobalMeet5Toggle\":true}," +
//            "\"ExcessiveConfs\":[],\"MeetingRooms\":[{\"Availability\":\"Wait For Moderator\"," +
//            "\"ConfId\":\"147305988\",\"ConferenceName\":\"Sudheer's Meeting\",\"Deleted\":false," +
//            "\"HashWebApiAuth\":\"1e6701f8feb93663626b1e20fe71e8af\"," +
//            "\"ListenPassCode\":\"4353213\",\"MaxParticipants\":\"125\"," +
//            "\"MeetingRoomId\":\"2988345\",\"MeetingRoomOptions\":[{\"Enabled\":true," +
//            "\"Name\":\"AllowChat\"},{\"Enabled\":true,\"Name\":\"WebRecording\"}," +
//            "{\"Enabled\":true,\"Name\":\"File Transfer\"}]," +
//            "\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https://pgi" +
//            ".globalmeet.com/SudheerChilumula\",\"AttendeeRegisterUrl\":\"\"," +
//            "\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https://pgi.globalmeet" +
//            ".com/utilities/joinLiveConference.aspx?cid=2988345&pc=59793395&sc=8786&msc=60126\"," +
//            "\"EndOfMeetingUrl\":\"\"},\"ModeratorEmail\":\"sudheer.chilumula@pgi.com\"," +
//            "\"ModeratorPassCode\":\"4353212\",\"ModeratorSecurityCode\":\"60126\"," +
//            "\"ParticipantPassCode\":\"435321\"," +
//            "\"Ptok\":\"Ki7cNftO5JVFcrE0s8Gemn5yIOGd85g1xdF+7uNWTOw=\"," +
//            "\"RoomName\":\"SudheerChilumula\",\"SecurityCode\":\"8786\"," +
//            "\"Status\":\"Completed\",\"TimeZone\":\"ESTRN-BASE\",\"WebMeetingServer\":\"web-na" +
//            ".globalmeet.com\",\"bridgeOptions\":[\"GlobalMeet2\"]," +
//            "\"phoneInformation\":[{\"Location\":\"USA\",\"PhoneNumber\":\"1-605-475-5619\"," +
//            "\"PhoneType\":\"Toll\",\"CustomLocation\":null},{\"Location\":\"USA\"," +
//            "\"PhoneNumber\":\"1-719-234-7872\",\"PhoneType\":\"Toll\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Canada, Calgary\",\"PhoneNumber\":\"+1 403 407 5801\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Canada, Montreal\"," +
//            "\"PhoneNumber\":\"+1 514 669 5926\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Canada, Toronto\",\"PhoneNumber\":\"+1 647 " +
//            "426 9175\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Canada, " +
//            "Vancouver\",\"PhoneNumber\":\"+1 604 224 6903\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Argentina, Buenos Aires\"," +
//            "\"PhoneNumber\":\"+54 (0) 11 5172 6020\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Argentina (toll free)\"," +
//            "\"PhoneNumber\":\"0800 800 1252\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Australia, Sydney\",\"PhoneNumber\":\"+61 " +
//            "(0) 2 8017 5796\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Australia, Melbourne\",\"PhoneNumber\":\"+61 (0) 3 8687 0554\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Australia, " +
//            "Brisbane\",\"PhoneNumber\":\"+61 (0) 7 3015 0544\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Australia (toll free)\",\"PhoneNumber\":\"1" +
//            " 800 448 693\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Austria (toll free)\",\"PhoneNumber\":\"0800 070 879\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Bahrain, Manama\",\"PhoneNumber\":\"+973 1619 8750\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bahrain (toll free)" +
//            "\",\"PhoneNumber\":\"800 81231\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Belarus (toll free)\",\"PhoneNumber\":\"8 " +
//            "820 0011 0342\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Belgium, Brussels\",\"PhoneNumber\":\"+32 (0) 2 400 6929\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Belgium (toll free)" +
//            "\",\"PhoneNumber\":\"0800 39280\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Bosnia and Herzegovina\"," +
//            "\"PhoneNumber\":\"+387 7031 1469\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Bulgaria, Sofia\",\"PhoneNumber\":\"+359 (0) 2 491 6054\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Bulgaria (toll " +
//            "free)\",\"PhoneNumber\":\"00800 111 4951\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Cambodia, Phnom Penh\"," +
//            "\"PhoneNumber\":\"+855 23 962 580\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Canada (toll free)\",\"PhoneNumber\":\"1 " +
//            "855 950 3725\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Chile, Santiago\",\"PhoneNumber\":\"+56 (0) 2 2666 0712\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Chile (toll free)" +
//            "\",\"PhoneNumber\":\"171 800 835 945\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Colombia, Bogota\",\"PhoneNumber\":\"+57 1 " +
//            "508 8125\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Colombia" +
//            " (toll free)\",\"PhoneNumber\":\"01 800 755 0054\",\"PhoneType\":\"International " +
//            "toll free\",\"CustomLocation\":null},{\"Location\":\"Costa Rica (toll free)\"," +
//            "\"PhoneNumber\":\"800 542 5351\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Croatia (toll free)\"," +
//            "\"PhoneNumber\":\"0800 222 836\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Cyprus (toll free)\",\"PhoneNumber\":\"800 " +
//            "97429\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Czech Republic, Prague\",\"PhoneNumber\":\"+420 225 986 566\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Czech  Republic " +
//            "(toll free)\",\"PhoneNumber\":\"800 701 533\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Denmark, Copenhagen\"," +
//            "\"PhoneNumber\":\"+45 32 72 78 11\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Denmark (toll free)\",\"PhoneNumber\":\"80 " +
//            "70 35 87\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Egypt (toll free)\",\"PhoneNumber\":\"0800 000 0541\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Estonia, Tallinn\",\"PhoneNumber\":\"+372 622 6569\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Estonia (toll free)" +
//            "\",\"PhoneNumber\":\"800 011 1577\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Finland, Helsinki\",\"PhoneNumber\":\"+358 " +
//            "(0) 9 2310 1676\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Finland (toll free)\",\"PhoneNumber\":\"0800 772 237\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"France, Paris\",\"PhoneNumber\":\"+33 (0) 1 70 37 16 57\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"France (toll free)" +
//            "\",\"PhoneNumber\":\"0800 946 532\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"France (national)\",\"PhoneNumber\":\"0811 " +
//            "655 102\",\"PhoneType\":\"Lo-call\",\"CustomLocation\":null},{\"Location\":\"France " +
//            "(national)\",\"PhoneNumber\":\"0821 231 688\",\"PhoneType\":\"Lo-call\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Georgia, Tbilisi\",\"PhoneNumber\":\"+995 " +
//            "32 2 053 083\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Germany, Frankfurt\",\"PhoneNumber\":\"+49 (0) 69 2222 10613\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Germany, Munich\"," +
//            "\"PhoneNumber\":\"+49 (0) 89 2030 31208\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Germany (national)\"," +
//            "\"PhoneNumber\":\"01801 000 891\",\"PhoneType\":\"Lo-call\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Germany (toll free)\"," +
//            "\"PhoneNumber\":\"0800 588 9171\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Greece, Athens\",\"PhoneNumber\":\"+30 211 " +
//            "181 3825\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Greece " +
//            "(toll free)\",\"PhoneNumber\":\"00800 128 906\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Hong Kong\",\"PhoneNumber\":\"+852 " +
//            "3018 9119\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Hong " +
//            "Kong (toll free)\",\"PhoneNumber\":\"800 901 914\",\"PhoneType\":\"International " +
//            "toll free\",\"CustomLocation\":null},{\"Location\":\"Hungary, Budapest\"," +
//            "\"PhoneNumber\":\"+36 1408 8968\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Hungary (toll free)\",\"PhoneNumber\":\"068 001 9675\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Iceland (toll free)\",\"PhoneNumber\":\"800 9004\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Indonesia, Jakarta\",\"PhoneNumber\":\"+62 21 2188 9094\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Indonesia (toll " +
//            "free)\",\"PhoneNumber\":\"007 803 321 8918\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Ireland, Dublin\"," +
//            "\"PhoneNumber\":\"+353 (0) 1 526 9422\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Ireland (national)\",\"PhoneNumber\":\"0818" +
//            " 270 288\",\"PhoneType\":\"Lo-call\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Ireland (toll free)\",\"PhoneNumber\":\"1800 937 650\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Ireland (national)\",\"PhoneNumber\":\"1890 907 631\"," +
//            "\"PhoneType\":\"Lo-call\",\"CustomLocation\":null},{\"Location\":\"Israel, Tel " +
//            "Aviv\",\"PhoneNumber\":\"+972 (0)3 721 7954\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Israel (toll free)\",\"PhoneNumber\":\"1809" +
//            " 213 173\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Italy, Milan\",\"PhoneNumber\":\"+39 02 3600 8007\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Italy, Rome\"," +
//            "\"PhoneNumber\":\"+39 06 8750 0677\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Italy (toll free)\",\"PhoneNumber\":\"800 " +
//            "146 095\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Japan, Tokyo\",\"PhoneNumber\":\"+81 3 4588 9606\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Japan, Osaka\"," +
//            "\"PhoneNumber\":\"+81 6 4560 2417\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Japan (mobile)\",\"PhoneNumber\":\"0120 538" +
//            " 611\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Japan (toll free)\",\"PhoneNumber\":\"0120 759 200\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Japan (national)\",\"PhoneNumber\":\"0570 063 744\"," +
//            "\"PhoneType\":\"Lo-call\",\"CustomLocation\":null},{\"Location\":\"Jordan (toll " +
//            "free)\",\"PhoneNumber\":\"0800 22157\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Kazakhstan (toll free)\"," +
//            "\"PhoneNumber\":\"8800 333 7556\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Kenya, Nairobi\",\"PhoneNumber\":\"+254 (0)" +
//            "207 643 597\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"South" +
//            " Korea, Seoul\",\"PhoneNumber\":\"+82 (0) 2 6007 0087\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"South Korea (toll free)\"," +
//            "\"PhoneNumber\":\"00798 6136 1526\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Kuwait (national)\",\"PhoneNumber\":\"+965 " +
//            "2206 3086\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Latvia," +
//            " Riga\",\"PhoneNumber\":\"+371 6601 3641\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Latvia (toll free)\",\"PhoneNumber\":\"8000" +
//            " 4412\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Lithuania, Vilnius\",\"PhoneNumber\":\"+370 5205 5486\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Lithuania (toll " +
//            "free)\",\"PhoneNumber\":\"8800 31443\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Luxembourg\",\"PhoneNumber\":\"+352 2088 " +
//            "1910\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Luxembourg " +
//            "(toll free)\",\"PhoneNumber\":\"800 28437\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Macau (national)\"," +
//            "\"PhoneNumber\":\"+853 6262 1684\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Mexico, Guadalajara\",\"PhoneNumber\":\"+52 33 4162 3995\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Mexico, Mexico " +
//            "City\",\"PhoneNumber\":\"+52 55 8421 0858\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Mexico, Monterrey\",\"PhoneNumber\":\"+52 " +
//            "81 4162 3851\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Mexico (toll free)\",\"PhoneNumber\":\"01 800 283 3131\"," +
//            "\"PhoneType\":\"National toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Morocco, Casablanca\",\"PhoneNumber\":\"+212 (0) 520 48 0023\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Morocco (toll free)" +
//            "\",\"PhoneNumber\":\"0800 091 297\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Netherlands, Amsterdam\"," +
//            "\"PhoneNumber\":\"+31 (0) 20 716 8346\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Netherlands (toll free)\"," +
//            "\"PhoneNumber\":\"0800 020 2598\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"New Zealand, Christchurch\"," +
//            "\"PhoneNumber\":\"+64 (0) 3 974 2592\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"New Zealand, Wellington\"," +
//            "\"PhoneNumber\":\"+64 (0) 4 909 4679\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"New Zealand, Auckland\"," +
//            "\"PhoneNumber\":\"+64 (0) 9 929 1832\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"New Zealand (toll free)\"," +
//            "\"PhoneNumber\":\"0800 440 096\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Nigeria, Lagos\",\"PhoneNumber\":\"+234 1 " +
//            "277 3948\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Norway, " +
//            "Oslo\",\"PhoneNumber\":\"+47 21 00 48 16\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Norway (toll free)\",\"PhoneNumber\":\"800 " +
//            "56082\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Oman (toll free)\",\"PhoneNumber\":\"800 77243\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Panama, Panama City\",\"PhoneNumber\":\"+507 8 339 783\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Panama (toll free)" +
//            "\",\"PhoneNumber\":\"00800 223 1431\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Peru, Lima\",\"PhoneNumber\":\"+51 (0) 1 " +
//            "700 9588\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Peru " +
//            "(toll free)\",\"PhoneNumber\":\"0800 78127\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Poland, Warsaw\"," +
//            "\"PhoneNumber\":\"+48 22 295 3584\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Poland (toll free)\",\"PhoneNumber\":\"00 " +
//            "800 121 4357\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Portugal, Lisbon\",\"PhoneNumber\":\"+351 21 424 5134\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Portugal (toll " +
//            "free)\",\"PhoneNumber\":\"800 784 443\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Qatar (freephone)\",\"PhoneNumber\":\"00 " +
//            "800 100 501\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Romania, Bucharest\",\"PhoneNumber\":\"+40 (0) 21 529 1307\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Romania (toll free)" +
//            "\",\"PhoneNumber\":\"0800 801 036\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Russia, Moscow\",\"PhoneNumber\":\"+7 495 " +
//            "705 9452\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Russia " +
//            "(toll free)\",\"PhoneNumber\":\"8 800 500 9272\",\"PhoneType\":\"International toll " +
//            "free\",\"CustomLocation\":null},{\"Location\":\"Saudi Arabia (toll free)\"," +
//            "\"PhoneNumber\":\"800 844 9140\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Saudi Arabia (toll free)\"," +
//            "\"PhoneNumber\":\"800 850 0228\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Serbia (toll free)\",\"PhoneNumber\":\"0800" +
//            " 190 700\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Singapore\",\"PhoneNumber\":\"+65 6654 9115\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Singapore (toll " +
//            "free)\",\"PhoneNumber\":\"800 616 3193\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Slovakia, Bratislava\"," +
//            "\"PhoneNumber\":\"+421 (0)2 3321 5508\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Slovakia (toll free)\"," +
//            "\"PhoneNumber\":\"0800 002 003\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Slovenia (toll free)\"," +
//            "\"PhoneNumber\":\"0800 80594\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Slovenia, Ljubljana\",\"PhoneNumber\":\"1 " +
//            "888 8200\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"South " +
//            "Africa, Johannesburg\",\"PhoneNumber\":\"+27 (0)11 589 8334\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"South Africa (toll " +
//            "free)\",\"PhoneNumber\":\"0800 999 598\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Spain, Madrid\",\"PhoneNumber\":\"+34 91 " +
//            "114 6654\",\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Spain, " +
//            "Barcelona\",\"PhoneNumber\":\"+34 93 800 1947\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Spain (toll free)\",\"PhoneNumber\":\"900 " +
//            "828 036\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Sri Lanka, Sri Lanka\",\"PhoneNumber\":\"+94 720 910 349\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Sweden, " +
//            "Stockholm\",\"PhoneNumber\":\"+46 (0) 8 5065 3957\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Sweden (toll free)\",\"PhoneNumber\":\"0200" +
//            " 125 589\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Switzerland, Geneva\",\"PhoneNumber\":\"+41 (0) 22 595 4796\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Switzerland, " +
//            "Zurich\",\"PhoneNumber\":\"+41 (0) 44 580 7208\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Switzerland (toll free)\"," +
//            "\"PhoneNumber\":\"0800 740 361\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Thailand, Bangkok\",\"PhoneNumber\":\"+66 " +
//            "(0) 2104 0766\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Thailand (toll free)\",\"PhoneNumber\":\"001 800 6136 1530\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Tunisia, Tunis\",\"PhoneNumber\":\"+216 31 378 110\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Turkey, Istanbul\"," +
//            "\"PhoneNumber\":\"+90 212 375 5032\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Turkey (toll free)\",\"PhoneNumber\":\"00 " +
//            "800 448829138\",\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Ukraine (toll free)\",\"PhoneNumber\":\"0800 500 923\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"UAE (toll free)\",\"PhoneNumber\":\"8000 3570 2658\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"UK (03)\",\"PhoneNumber\":\"+44 (0)330 336 6011\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"UK (toll free)\"," +
//            "\"PhoneNumber\":\"0800 358 6387\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"UK (national)\",\"PhoneNumber\":\"0844 473 " +
//            "5018\",\"PhoneType\":\"Lo-call\",\"CustomLocation\":null},{\"Location\":\"UK " +
//            "(national)\",\"PhoneNumber\":\"0845 545 0016\",\"PhoneType\":\"Lo-call\"," +
//            "\"CustomLocation\":null},{\"Location\":\"USA /Canada (toll free)\"," +
//            "\"PhoneNumber\":\"1-866-546-3377\",\"PhoneType\":\"Toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Uruguay (toll free)\"," +
//            "\"PhoneNumber\":\"0004 019 0305\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Venezuela, Caracas\",\"PhoneNumber\":\"+58 " +
//            "212 720 2580\",\"PhoneType\":\"Local\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Venezuela (toll free)\",\"PhoneNumber\":\"0 800 100 8373\"," +
//            "\"PhoneType\":\"International toll free\",\"CustomLocation\":null}," +
//            "{\"Location\":\"Vietnam, Hanoi\",\"PhoneNumber\":\"+84 24 4458 3324\"," +
//            "\"PhoneType\":\"Local\",\"CustomLocation\":null},{\"Location\":\"Vietnam, Ho Chi " +
//            "Minh\",\"PhoneNumber\":\"+84 28 4458 1334\",\"PhoneType\":\"Local\"," +
//            "\"CustomLocation\":null},{\"Location\":\"Vietnam (toll free)\"," +
//            "\"PhoneNumber\":\"1201 1444\",\"PhoneType\":\"International toll free\"," +
//            "\"CustomLocation\":null},{\"Location\":\"USA\"," +
//            "\"PhoneNumber\":\"7777001044@at5-ln4-sy1.sip.pgiconnect.com\",\"PhoneType\":\"VOIP " +
//            "SIP\",\"CustomLocation\":null}],\"reservationOptions\":[\"PlayEntryTone\"," +
//            "\"PlayExitTone\",\"HubDownload\",\"AllowIntDialOut\",\"PlaySubconfEntryExit\"]," +
//            "\"ParticipantAnonymity\":false,\"PrimaryAccessNumber\":\"1-605-475-5619\"," +
//            "\"UseHtml5\":true,\"AudioSecurityCode\":\"\"}],\"AudioConferenceCount\":0," +
//            "\"HasGMWebinar\":true,\"MeetingRoomsCount\":1}";
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        mContext = RuntimeEnvironment.application.getApplicationContext();
//        ConvergenceApplication.setAppContext(mContext);
//
//        // LoggerUtil Mock
//        PowerMockito.mockStatic(LoggerUtil.class);
//        PowerMockito.when(LoggerUtil.getInstance()).thenReturn(mLoggerUtil);
//
//        // ClientInfoDaoUtils Mock
//        PowerMockito.mockStatic(ClientInfoDaoUtils.class);
//        PowerMockito.when(ClientInfoDaoUtils.getInstance()).thenReturn(mClientInfoDaoUtils);
//        PowerMockito.when(mClientInfoDaoUtils.isUseHtml5()).thenReturn(true);
//
//        // ApplicationDao Mock
//        PowerMockito.mockStatic(ApplicationDao.class);
//        PowerMockito.when(ApplicationDao.get(Mockito.any())).thenReturn(mApplicationDao);
//
//        // ClientInfoResultDao Mock
//        mClientInfoResultDao = PowerMockito.mock(ClientInfoResultDao.class);
//        List<ClientInfoResult> list = prepareMockClientInfoData();
//        PowerMockito.when(mClientInfoResultDao.loadAll()).thenReturn(list);
//        PowerMockito.when(ApplicationDao.get(Mockito.any()).getClientResult()).thenReturn
//                (mClientInfoResultDao);
//
//        // MeetingRoom Mock
//        List<MeetingRoom> meetingRooms = list.get(0).getMeetingRooms();
//        PowerMockito.when(mClientInfoDaoUtils.getMeetingRoomData()).thenReturn(meetingRooms.get(0));
//
//        // AppAuthUtils Mock
//        PowerMockito.mockStatic(AppAuthUtils.class);
//        PowerMockito.when(AppAuthUtils.getInstance()).thenReturn(mAppAuthUtils);
//
//        //SoftPhone Mock
//        PowerMockito.whenNew(PGiSoftPhoneCallback.class).withNoArguments().thenReturn
//                (mSoftphoneCallbacks);
//        // PowerMockito.whenNew(PGiSoftPhoneService.class).withNoArguments().thenReturn(mPGiSoftPhoneService);
//
//
//        //SharedPrefs Mock
//        PowerMockito.mockStatic(SharedPreferencesManager.class);
//        PowerMockito.when(SharedPreferencesManager.getInstance()).thenReturn
//                (mSharedPreferencesManager);
//        PowerMockito.when(SharedPreferencesManager.getInstance().isFirstTimeAskingPermission
//                (Manifest.permission.RECORD_AUDIO)).thenReturn(true);
//
//        // Connectivity Manager Shadow
//        mConnectivityManager = (ConnectivityManager) RuntimeEnvironment.application
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        mShadowConnectivityManager = Shadows.shadowOf(mConnectivityManager);
//        mShadowNetworkInfo = Shadows.shadowOf(mConnectivityManager.getActiveNetworkInfo());
//    }
//
//    private void unMock() throws Exception {
//        Mockito.reset(mClientInfoDaoUtils);
//        Mockito.reset(mLoggerUtil);
//        Mockito.reset(mApplicationDao);
//        Mockito.reset(mAppAuthUtils);
//        Mockito.reset(mUAPIServiceManager);
//        Mockito.reset(mServerManager);
//        Mockito.reset(mUAPIEventsManager);
//        Mockito.reset(mSharedPreferencesManager);
//        Mockito.reset(mContextCompat);
//    }
//
//    private void createActivity() {
//        // Intent
//        Intent audioSelectionIntent = null;
//        audioSelectionIntent = new Intent(mContext, WebMeetingActivity.class);
//        audioSelectionIntent.putExtra(AppConstants.FIRST_NAME, "Sudheer");
//        audioSelectionIntent.putExtra(AppConstants.IS_MEETING_HOST, true);
//        audioSelectionIntent.putExtra(AppConstants.MEETING_CONFERENCE_ID, "123456");
//        audioSelectionIntent.putExtra(AppConstants.BUNDLE_KEY_TIMESTAMP, System.currentTimeMillis
//                ());
//        audioSelectionIntent.putExtra(AppConstants.ELK_EVENT, AppConstants.ACTION_SHIMMER_DURATION);
//
//        // Activity Mock
//        controller = Robolectric.buildActivity(WebMeetingActivity.class, audioSelectionIntent);
//        mWebMeetingActivity = controller.create().resume().get();
//        mFlFragmentHolder = mWebMeetingActivity.findViewById(R.id.fl_fragment_place_holder);
//        mWebMeetingView = mWebMeetingActivity.findViewById(R.id.rl_web_meeting_view);
//        mShimmerGroup = mWebMeetingActivity.findViewById(R.id.shimmer_view_group);
//        PowerMockito.when(mAudioRouteManager.getAudioManager()).thenReturn(mAudioManager);
//        PowerMockito.when(mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)).thenReturn(5);
//        PowerMockito.doNothing().when(mAudioManager).setSpeakerphoneOn(Mockito.anyBoolean());
//    }
//
//    public List<ClientInfoResult> prepareMockClientInfoData() throws IOException {
//        ClientInfoResult clientInfoResult = mapper.readValue(clientInfoResultStr,
//                                                             ClientInfoResult.class);
//        List<ClientInfoResult> clientInfoResults = new ArrayList<>();
//        clientInfoResults.add(clientInfoResult);
//        return clientInfoResults;
//    }
//
//    private void dispatchInternetConnectivity() {
//        NetworkInfo mNetworkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState
//                                                                         .CONNECTED,
//                                                                 ConnectivityManager.TYPE_WIFI,
//                                                                 0, true, true);
//        mShadowConnectivityManager.setActiveNetworkInfo(mNetworkInfo);
//        RuntimeEnvironment.application.sendBroadcast(new Intent("android.net.conn" +
//                                                                        ".CONNECTIVITY_CHANGE"));
//    }
//
//    private void dispatchNoInternetConnectivity() {
//        NetworkInfo mNetworkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState
//                                                                         .DISCONNECTED,
//                                                                 ConnectivityManager.TYPE_WIFI,
//                                                                 0, false, false);
//        mShadowConnectivityManager.setActiveNetworkInfo(mNetworkInfo);
//        RuntimeEnvironment.application.sendBroadcast(new Intent("android.net.conn" +
//                                                                        ".CONNECTIVITY_CHANGE"));
//    }
//
//    private void simulatePermissionGranted() {
//        PowerMockito.mockStatic(ContextCompat.class);
//        PowerMockito.when(ContextCompat.checkSelfPermission(mWebMeetingActivity, Manifest.permission
//                .RECORD_AUDIO))
//                .thenReturn(PackageManager.PERMISSION_GRANTED);
//    }
//
//    private void simulatePermissionDenied() {
//        PowerMockito.mockStatic(ContextCompat.class);
//        PowerMockito.when(ContextCompat.checkSelfPermission(mWebMeetingActivity, Manifest.permission
//                .RECORD_AUDIO))
//                .thenReturn(PackageManager.PERMISSION_DENIED);
//    }
//
//    private void mockNetworkCalls() {
//        PowerMockito.mockStatic(UAPIServiceManager.class);
//        PowerMockito.when(UAPIServiceManager.getInstance()).thenReturn(mUAPIServiceManager);
//        mUAPIServiceAPI = FakeRestClient.getInstance().getUAPIClient();
//        PowerMockito.when(UAPIServiceManager.getInstance().getUAPIServiceAPI()).thenReturn
//                (mUAPIServiceAPI);
//
//        PowerMockito.mockStatic(ServerManager.class);
//        PowerMockito.when(ServerManager.getInstance(Mockito.anyString(), Mockito.any()))
//                .thenReturn(mServerManager);
//        PowerMockito.when(ServerManager.getInstance(Mockito.anyString(), Mockito.any())
//                                  .getUAPIServiceAPI()).thenReturn(mUAPIServiceAPI);
//
//        PowerMockito.mockStatic(PgiWebServiceManager.class);
//        PowerMockito.when(PgiWebServiceManager.getInstance()).thenReturn(mPgiWebServiceManager);
//        mPgiWebServiceAPI = FakeRestClient.getInstance().getPGiClient();
//        PowerMockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn
//                (mPgiWebServiceAPI);
//
//        PowerMockito.mockStatic(IdentityServiceManager.class);
//        PowerMockito.when(IdentityServiceManager.getInstance()).thenReturn(mIdentityServiceManager);
//        mIdentityServiceAPI = FakeRestClient.getInstance().getIdentityClient();
//        PowerMockito.when(IdentityServiceManager.getInstance().getIdentityServiceAPI())
//                .thenReturn(mIdentityServiceAPI);
//    }
//
//    private void simulateSoftPhoneConnectCallback() {
//        PGiSoftPhoneModel.INSTANCE.getSoftPhoneConnected().onNext(true);
//    }
//
//    private void simulateSoftPhoneDisconnectCallback() {
//        PGiSoftPhoneModel.INSTANCE.getSoftPhoneConnected().onNext(false);
//    }
//
//    private void simulateSoftPhoneRagAlarmCallback() {
//        PGiSoftPhoneModel.INSTANCE.getSoftPhoneBadNetworkToast().onNext(true);
//    }
//
//    private void simulateSoftPhoneSignalLevelCallback(String data) {
//        PGiSoftPhoneModel.INSTANCE.getSoftPhoneSignalLevel().onNext(data);
//    }
//
//    // TODO:: Create a Mock UAPI EventManager
//    private void simulateUAPISIPDialOutSuccessEvent() {
//        MeetingEvents sipDialOutEvent = new MeetingEvents();
//        List<Event> events = new ArrayList<Event>();
//        Event sipEvent = new Event();
//        sipEvent.setType("SIP_DIAL_OUT_SUCCEEDED");
//        sipEvent.setEventType("SIP_DIAL_OUT_SUCCEEDED");
//        sipEvent.setWebParticipantId("e7frf31xy18jbzhjb89ffn5ca");
//        events.add(sipEvent);
//        sipDialOutEvent.setEvents(events);
//        UAPIEventsManager.getInstance().onEventReceivedSuccess(sipDialOutEvent);
//    }
//
//    private void simulateUAPIParticipatJoinEvent() {
//        MeetingEvents sipDialOutEvent = new MeetingEvents();
//        List<Event> events = new ArrayList<Event>();
//        Event joinEvent = new Event();
//        joinEvent.setType("MEETING_JOINED");
//        joinEvent.setEventType("MEETING_JOINED");
//        joinEvent.setParticipantId("e7frf31xy18jbzhjb89ffn5ca");
//        events.add(joinEvent);
//        sipDialOutEvent.setEvents(events);
//        UAPIEventsManager.getInstance().onEventReceivedSuccess(sipDialOutEvent);
//    }
//
//    private TextView findSnackbarTextView(View rootView) {
//        final Snackbar.SnackbarLayout snackbarLayout = findSnackbarLayout(rootView);
//        return snackbarLayout == null ? null : (TextView) snackbarLayout.getChildAt(0)
//                .findViewById(android.support.design.R.id.snackbar_text);
//    }
//
//    private Snackbar.SnackbarLayout findSnackbarLayout(View rootView) {
//        if (rootView instanceof Snackbar.SnackbarLayout) {
//            return (Snackbar.SnackbarLayout) rootView;
//        } else if (rootView instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
//                if (findSnackbarLayout(((ViewGroup) rootView).getChildAt(i)) != null) {
//                    return findSnackbarLayout(((ViewGroup) rootView).getChildAt(i));
//                }
//            }
//            return null;
//        } else {
//            return null;
//        }
//    }
//
//    /**************************************Start Test cases****************************************/
//
//    @Test
//    public void testActivityIsNotNull() throws Exception {
//        createActivity();
//        Assert.assertNotNull(mWebMeetingActivity);
//        mWebMeetingActivity.finishActivity(false, 1);
//    }
//
//    @Test
//    public void testViewStatesOnCreate() throws Exception {
//        createActivity();
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Assert.assertEquals(View.GONE, mWebMeetingView.getVisibility());
//        Assert.assertEquals(View.GONE, mShimmerGroup.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//    }
//
//    /***********************************************************************************************
//     *                                                                                             *
//     *                              AUDIO TESTS                                                    *
//     *                                                                                             *
//     **********************************************************************************************/
//
//    @Test
//    public void testAudioSelectionProgressFragmentShownOnInitAndSubscribedToMeetings() throws
//                                                                                       Exception {
//        createActivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertNotNull(fragment);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionProgressFragment);
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//        // TODO:: Cover subscribe method
//    }
//
//    @Test
//    public void testAudioSelectionProgressFragmentShownAfterInternetConnectivity() throws
//                                                                                   Exception {
//        createActivity();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertNotNull(fragment);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionProgressFragment);
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Assert.assertFalse(mWebMeetingActivity.getIsVoipEnabled());
//        mWebMeetingActivity.finishActivity(false, 1);
//    }
//
//    @Test
//    public void testAudioSelectionFragmentShownAfterJoinAndRoomInfoSuccess() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertNotNull(fragment);
//        Assert.assertEquals(false, fragment instanceof AudioSelectionProgressFragment);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Assert.assertTrue(mWebMeetingActivity.getIsVoipEnabled());
//        mWebMeetingActivity.finishActivity(false, 1);
//    }
//
//    /*----------SOFTPHONE TEST START---------*/
//    @Test
//    public void testSoftPhoneInitializedOnAudioSelectionFragmentAndAllowRecordPermission() throws
//                                                                                           Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertNotNull(fragment);
//        Assert.assertEquals(false, fragment instanceof AudioSelectionProgressFragment);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Assert.assertTrue(mWebMeetingActivity.getIsVoipEnabled());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testSoftPhoneNotInitializedOnAudioSelectionFragmentAndDenyRecordPermission()
//            throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        simulatePermissionDenied();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertNotNull(fragment);
//        Assert.assertEquals(false, fragment instanceof AudioSelectionProgressFragment);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Mockito.verifyZeroInteractions(mPGiSoftPhone);
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testAudioSelectionFragmentNotVisibleOnClickOfConnectVoip() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        Assert.assertNotNull(voipConnectBtn);
//        Assert.assertTrue(voipConnectBtn.isEnabled());
//        Assert.assertTrue(voipConnectBtn.isClickable());
//        Assert.assertEquals(AppConstants.ALPHA_VOIP_BTN_ENABLED, voipConnectBtn.getAlpha(), 0);
//        voipConnectBtn.performClick();
//        Assert.assertEquals(View.GONE, mFlFragmentHolder.getVisibility());
//        Assert.assertEquals(View.VISIBLE, mWebMeetingView.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testSoftPhoneConnectedOnConnectVoipBtnClick() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        Assert.assertNotNull(voipConnectBtn);
//        Assert.assertTrue(voipConnectBtn.isEnabled());
//        Assert.assertTrue(voipConnectBtn.isClickable());
//        Assert.assertEquals(AppConstants.ALPHA_VOIP_BTN_ENABLED, voipConnectBtn.getAlpha(), 0);
//        voipConnectBtn.performClick();
//        Assert.assertEquals(View.GONE, mFlFragmentHolder.getVisibility());
//        Mockito.verify(mPGiSoftPhone).dialOut(Mockito.anyString(), Mockito.anyString());
//        simulateSoftPhoneConnectCallback();
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testSoftPhoneDisconnectsOnNoInternet() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        Assert.assertNotNull(voipConnectBtn);
//        Assert.assertTrue(voipConnectBtn.isEnabled());
//        Assert.assertTrue(voipConnectBtn.isClickable());
//        Assert.assertEquals(AppConstants.ALPHA_VOIP_BTN_ENABLED, voipConnectBtn.getAlpha(), 0);
//        voipConnectBtn.performClick();
//        Mockito.verify(mPGiSoftPhone).dialOut(Mockito.anyString(), Mockito.anyString());
//        simulateSoftPhoneConnectCallback();
//        dispatchNoInternetConnectivity();
//        Assert.assertTrue(mWebMeetingActivity.getReconnectVoip());
//        simulateSoftPhoneDisconnectCallback();
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testSoftPhoneReconnectsOnInternetReconnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Assert.assertEquals(true, fragment instanceof AudioSelectionFragment);
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        Assert.assertNotNull(voipConnectBtn);
//        Assert.assertTrue(voipConnectBtn.isEnabled());
//        Assert.assertTrue(voipConnectBtn.isClickable());
//        Assert.assertEquals(AppConstants.ALPHA_VOIP_BTN_ENABLED, voipConnectBtn.getAlpha(), 0);
//        voipConnectBtn.performClick();
//        Mockito.verify(mPGiSoftPhone).dialOut(Mockito.anyString(), Mockito.anyString());
//        simulateSoftPhoneConnectCallback();
//        dispatchNoInternetConnectivity();
//        Assert.assertTrue(mWebMeetingActivity.getReconnectVoip());
//        simulateSoftPhoneDisconnectCallback();
//        dispatchInternetConnectivity();
//        Assert.assertFalse(mWebMeetingActivity.getReconnectVoip());
//        Mockito.verify(mPGiSoftPhone, Mockito.times(2)).dialOut(Mockito.anyString(), Mockito
//                .anyString());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testMultipleSoftPhoneReconnectsOnMultipleInternetReconnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        Mockito.verify(mPGiSoftPhone).dialOut(Mockito.anyString(), Mockito.anyString());
//        simulateSoftPhoneConnectCallback();
//        dispatchNoInternetConnectivity();
//        simulateSoftPhoneDisconnectCallback();
//        // 2nd attempt
//        dispatchInternetConnectivity();
//        simulateSoftPhoneConnectCallback();
//        dispatchNoInternetConnectivity();
//        simulateSoftPhoneDisconnectCallback();
//        // 3rd attempt
//        dispatchInternetConnectivity();
//        simulateSoftPhoneConnectCallback();
//        dispatchNoInternetConnectivity();
//        simulateSoftPhoneDisconnectCallback();
//        // 4th attempt
//        dispatchInternetConnectivity();
//        simulateSoftPhoneConnectCallback();
//        Mockito.verify(mPGiSoftPhone, Mockito.times(4)).dialOut(Mockito.anyString(), Mockito
//                .anyString());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testMeetingControlsPanelDisabledBeforeSoftPhoneConnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        ToggleButton micBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_mute_meeting);
//        ToggleButton speakerBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_speaker);
//        ProgressBar micProgressBtn = (ProgressBar) mWebMeetingActivity.findViewById(R.id.progress_mic_disabled);
//        Assert.assertNotNull(micBtn);
//        Assert.assertFalse(micBtn.isEnabled());
//        Assert.assertFalse(micBtn.isClickable());
//        Assert.assertNotNull(speakerBtn);
//        Assert.assertFalse(speakerBtn.isEnabled());
//        Assert.assertFalse(speakerBtn.isClickable());
//        Assert.assertNotNull(micProgressBtn);
//        Assert.assertEquals(View.VISIBLE, micProgressBtn.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testMeetingControlsPanelEnabledAfterSoftPhoneConnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        simulateUAPISIPDialOutSuccessEvent();
//        ToggleButton micBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_mute_meeting);
//        ToggleButton speakerBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_speaker);
//        ProgressBar micProgressBtn = (ProgressBar) mWebMeetingActivity.findViewById(R.id.progress_mic_disabled);
//        Assert.assertNotNull(micBtn);
//        Assert.assertTrue(micBtn.isEnabled());
//        Assert.assertTrue(micBtn.isClickable());
//        Assert.assertNotNull(speakerBtn);
//        Assert.assertTrue(speakerBtn.isEnabled());
//        Assert.assertTrue(speakerBtn.isClickable());
//        Assert.assertNotNull(micProgressBtn);
//        Assert.assertEquals(View.GONE, micProgressBtn.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testMicProgressVisibleIfNoSIPUAPiEventAfterSoftPhoneConnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        ToggleButton micBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_mute_meeting);
//        ToggleButton speakerBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_speaker);
//        ProgressBar micProgressBtn = (ProgressBar) mWebMeetingActivity.findViewById(R.id.progress_mic_disabled);
//        Assert.assertNotNull(micBtn);
//        Assert.assertFalse(micBtn.isEnabled());
//        Assert.assertFalse(micBtn.isClickable());
//        Assert.assertNotNull(speakerBtn);
//        Assert.assertTrue(speakerBtn.isEnabled());
//        Assert.assertNotNull(micProgressBtn);
//        Assert.assertEquals(View.VISIBLE, micProgressBtn.getVisibility());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testActivateSpeakerOnVoipNonDolby() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        simulateUAPISIPDialOutSuccessEvent();
//        ToggleButton speakerBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id.btn_speaker);
//        Assert.assertNotNull(speakerBtn);
//        Assert.assertTrue(speakerBtn.isEnabled());
//        speakerBtn.performClick();
//        Assert.assertTrue(speakerBtn.isChecked());
//        Assert.assertFalse((mPGiSoftPhone.isDolby()));
//        Mockito.verify(mAudioRouteManager, Mockito.times(2)).SetAudioRoute(Mockito.any());
//        Mockito.verify(mPGiSoftPhone, Mockito.times(0)).setOutputAudioRoute();
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    // TODO:: Mock a Dolby room and enable this test
//    //    @Test
//    //    @PrepareForTest({ClientInfoDaoUtils.class, ContextCompat.class, ActivityCompat.class,
//    //        LoggerUtil.class, ApplicationDao.class, AppAuthUtils.class, UAPIServiceManager.class,
//    //        PgiWebServiceManager.class, IdentityServiceManager.class, ServerManager.class,
//    //        UAPIEventsManager.class, SharedPreferencesManager.class})
//    //    public void testActivateSpeakerOnVoipDolby() throws Exception {
//    //        mockNetworkCalls();
//    //        createActivity();
//    //        mWebMeetingActivity.setPGiSoftPhone(mPGiSoftPhone);
//    //        mWebMeetingActivity.setSoftphoneCallBacks(mSoftphoneCallbacks);
//    //        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//    //        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString()))
//    // .thenReturn(true);
//    //        simulatePermissionGranted();
//    //        dispatchInternetConnectivity();
//    //        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager()
//    // .findFragmentById(R.id.fl_fragment_place_holder);
//    //        simulateUAPIParticipatJoinEvent();
//    //        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id
//    // .connect_voip_btn);
//    //        voipConnectBtn.performClick();
//    //        simulateSoftPhoneConnectCallback();
//    //        simulateUAPISIPDialOutSuccessEvent();
//    //        ToggleButton speakerBtn = (ToggleButton) mWebMeetingActivity.findViewById(R.id
//    // .btn_speaker);
//    //        Assert.assertNotNull(speakerBtn);
//    //        Assert.assertTrue(speakerBtn.isEnabled());
//    //        speakerBtn.performClick();
//    //        Assert.assertTrue(speakerBtn.isChecked());
//    //        Assert.assertTrue((mPGiSoftPhone.isDolby()));
//    //        Mockito.verify(mPGiSoftPhone).setOutputAudioRoute(Mockito.any());
//    //    }
//
//
//    @Test
//    public void testSoftPhoneHangupCalledOnDoNotConnectClick() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        Button doNotConnectBtn = (Button) fragment.getView().findViewById(R.id.btn_donot_connect);
//        Assert.assertNotNull(doNotConnectBtn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        simulateUAPISIPDialOutSuccessEvent();
//        ImageButton imgSettingsBtn = (ImageButton) mWebMeetingActivity.findViewById(R.id.btn_setting);
//        Assert.assertNotNull(imgSettingsBtn);
//        imgSettingsBtn.performClick();
//        Assert.assertEquals(View.VISIBLE, mFlFragmentHolder.getVisibility());
//        Assert.assertEquals(View.GONE, mWebMeetingView.getVisibility());
//        doNotConnectBtn.performClick();
//        Mockito.verify(mPGiSoftPhone).hangUp();
//        Assert.assertFalse(mWebMeetingActivity.getReconnectVoip());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testToastShownOnSoftPhoneRagAlarmsAfter30Secs() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        simulateUAPISIPDialOutSuccessEvent();
//        simulateSoftPhoneRagAlarmCallback();
//        Snackbar.SnackbarLayout snackbarLayout = findSnackbarLayout(mWebMeetingView);
//        Assert.assertNotNull(snackbarLayout);
//        TextView textView = findSnackbarTextView(mWebMeetingView);
//        Assert.assertNotNull(textView);
//        Assert.assertEquals("Poor audio connection", textView.getText());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//
//    @Test
//    public void testMicSamplingOnSoftPhoneConnect() throws Exception {
//        mockNetworkCalls();
//        createActivity();
//        Mockito.when(mPGiSoftPhone.initialize()).thenReturn(true);
//        Mockito.when(mPGiSoftPhone.dialOut(Mockito.anyString(), Mockito.anyString())).thenReturn
//                (true);
//        simulatePermissionGranted();
//        dispatchInternetConnectivity();
//        Fragment fragment = mWebMeetingActivity.getSupportFragmentManager().findFragmentById(R.id.fl_fragment_place_holder);
//        simulateUAPIParticipatJoinEvent();
//        Button voipConnectBtn = (Button) fragment.getView().findViewById(R.id.connect_voip_btn);
//        voipConnectBtn.performClick();
//        simulateSoftPhoneConnectCallback();
//        simulateUAPISIPDialOutSuccessEvent();
//        simulateSoftPhoneSignalLevelCallback("M:11|S:0|MP:22|SP:0");
//        ImageView[] micLevelView = Whitebox.getInternalState(mWebMeetingActivity, "mMicLevelView");
//        ShadowDrawable shadowDrawable = Shadows.shadowOf(micLevelView[0].getDrawable());
//        ShadowDrawable shadowDrawable2 = Shadows.shadowOf(micLevelView[1].getDrawable());
//        ShadowDrawable shadowDrawable3 = Shadows.shadowOf(micLevelView[2].getDrawable());
//        ShadowDrawable shadowDrawable4 = Shadows.shadowOf(micLevelView[3].getDrawable());
//        ShadowDrawable shadowDrawable5 = Shadows.shadowOf(micLevelView[4].getDrawable());
//        ShadowDrawable shadowDrawable6 = Shadows.shadowOf(micLevelView[5].getDrawable());
//        ShadowDrawable shadowDrawable7 = Shadows.shadowOf(micLevelView[6].getDrawable());
//        ShadowDrawable shadowDrawable8 = Shadows.shadowOf(micLevelView[7].getDrawable());
//        ShadowDrawable shadowDrawable9 = Shadows.shadowOf(micLevelView[8].getDrawable());
//
//        Assert.assertEquals(R.drawable.line_2_copy_3, shadowDrawable.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_3, shadowDrawable2.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_3, shadowDrawable3.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable4.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable5.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable6.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable7.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable8.getCreatedFromResId());
//        Assert.assertEquals(R.drawable.line_2_copy_9, shadowDrawable9.getCreatedFromResId());
//        mWebMeetingActivity.finishActivity(false, 1);
//        unMock();
//    }
//    /*-------------SOFTPHONE TESTS END------------------*/
//
//    /*-------------DIAL OUT TESTS START-----------------*/
//}
