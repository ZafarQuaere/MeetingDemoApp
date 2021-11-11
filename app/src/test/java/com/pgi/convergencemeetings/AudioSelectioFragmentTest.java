//package com.pgi.convergencemeetings;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.widget.CheckBox;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pgi.convergence.constants.AppConstants;
//import com.pgi.convergence.enums.ConnectionState;
//import com.pgi.convergence.enums.ConnectionType;
//import com.pgi.convergence.utils.CommonUtils;
//import com.pgi.convergence.utils.PermissionUtil;
//import com.pgi.convergencemeetings.meeting.gm4.ui.GuestMeetingRoomActivity;
//import com.pgi.convergencemeetings.meeting.gm4.ui.HostMeetingRoomActivity;
//import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomActivity;
//import com.pgi.convergencemeetings.base.ConvergenceApplication;
//import com.pgi.convergencemeetings.meeting.gm5.ui.audio.AudioSelectionFragment;
//import com.pgi.convergencemeetings.models.ClientInfoResponse;
//import com.pgi.convergencemeetings.utils.AppAuthUtils;
//import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
//import com.pgi.convergencemeetings.utils.ClientInfoResultCache;
//import com.pgi.convergencemeetings.utils.ConferenceManager;
//import com.pgi.logging.PGiLogger;
//import com.pgi.network.PiaServiceAPI;
//import com.pgi.network.PiaServiceManager;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.powermock.reflect.Whitebox;
//import org.robolectric.Robolectric;
//import org.robolectric.RuntimeEnvironment;
//import org.robolectric.Shadows;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowActivity;
//import org.robolectric.shadows.ShadowIntent;
//import org.robolectric.shadows.ShadowLog;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//
///**
// * Created by ashwanikumar on 12/8/2017.
// */
//@Config(shadows = {ShadowShimmerlayout.class})
//@PrepareForTest({CommonUtils.class, PermissionUtil.class, AppAuthUtils.class, ClientInfoDaoUtils.class,
//        PiaServiceManager.class})
//public class AudioSelectioFragmentTest extends RobolectricTest{
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//    @Mock
//    PGiLogger mLoggerUtil;
//
//    @Mock
//    PiaServiceManager piaServiceManager;
//    private MeetingRoomActivity mMeetingRoomActivity;
//    private WeakReference<Activity> mMeetingRoomActivityWeak;
//    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//    String clientInfoResponse = "{\"ClientInfoResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":812,\"MessageId\":\"09b6150c-c6dd-4237-9dae-63ff4c53e8cf\",\"ServerDateTime\":\"\\/Date(1509770564571)\\/\",\"AudioOnlyConferences\":[],\"ClientDetails\":{\"Active\":true,\"BillToId\":8291321,\"ClientContactDetails\":{\"Address\":{\"Address1\":null,\"Address2\":null,\"Address3\":null,\"City\":null,\"CountryCode\":null,\"PostalCode\":null,\"Province\":null,\"StateCode\":null,\"TimeZone\":null},\"Email\":\"Abhishek.Gupta@pgi.com\",\"Fax\":\"\",\"FaxIntlPrefix\":\"\",\"FirstName\":\"Abhishek\",\"HomePhone\":\"\",\"HomePhoneExt\":\"\",\"HomePhoneIntlPrefix\":\"\",\"HomePhoneIsDefault\":false,\"JobTitle\":\"\",\"LastName\":\"Gupta\",\"MobilePhone\":\"\",\"MobilePhoneExt\":\"\",\"MobilePhoneIntlPrefix\":\"\",\"MobilePhoneIsDefault\":false,\"Phone\":\"719\\/555-5555\",\"PhoneExt\":\"\",\"PhoneIntlPrefix\":\"\",\"PhoneIsDefault\":false,\"SecondaryPhone\":null,\"SecondaryPhoneExt\":null,\"SecondaryPhoneIntlPrefix\":null,\"SecondaryPhoneIsDefault\":false},\"ClientId\":\"1578208\",\"CreditCardDetail\":null,\"DefaultLanguage\":\"\",\"Hold\":false,\"OperationComments\":\"\",\"Password\":\"a0F872Mz\",\"PasswordCanBeModified\":null,\"PromotionCodes\":[],\"SoftPhoneAutoConnect\":false,\"SoftPhoneEnable\":false,\"SpecialInfo\":\"\",\"SubscriptionId\":\"\",\"TerritoryCode\":\"D0938\"},\"CompanyDetails\":{\"BlockedServices\":[\"Acrobat Connect Professional\",\"Readycast Demo (Internal Use Only)\",\"ReadyCast Protect\",\"ReadyConference Plus (Web)\",\"Visioncast Meeting Demo (Internal Use Only)\",\"iMeet (Visual Conferencing)\",\"International Moderator Dial Out\",\"LotusLiveMeeting\",\"LotusLiveMeetingDemo (Internal Use Only)\"],\"CompanyId\":635623,\"Name\":\"MuleSoft\"},\"ExcessiveConfs\":[],\"MeetingRooms\":[{\"Availability\":\"Wait For Moderator\",\"ConfId\":\"129127066\",\"ConferenceName\":\"Abhishek's Meeting\",\"Deleted\":false,\"HashWebApiAuth\":\"eeb518cff47150b8cb8e58d8bbb96826\",\"ListenPassCode\":\"8431086\",\"MaxParticipants\":\"125\",\"MeetingRoomId\":\"1429497\",\"MeetingRoomOptions\":[{\"Enabled\":true,\"Name\":\"AllowChat\"},{\"Enabled\":true,\"Name\":\"JoinNoise\"},{\"Enabled\":true,\"Name\":\"WebRecording\"},{\"Enabled\":true,\"Name\":\"File Transfer\"}],\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https:\\/\\/mulesoft.itdev.local\\/AbhishekGupta\",\"AttendeeRegisterUrl\":\"\",\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https:\\/\\/mulesoft.itdev.local\\/utilities\\/joinLiveConference.aspx?cid=1429497&pc=&sc=&msc=21665\",\"EndOfMeetingUrl\":\"\"},\"ModeratorEmail\":\"Abhishek.Gupta@pgi.com\",\"ModeratorPassCode\":\"8431085\",\"ModeratorSecurityCode\":\"21665\",\"ParticipantPassCode\":\"843108\",\"Ptok\":\"LUqS\\/dYGGxtrXUtv0r6i+OKTMeWpOlSQ7K4HM8R40uw=\",\"RoomName\":\"AbhishekGupta\",\"SecurityCode\":\"\",\"Status\":\"Completed\",\"TimeZone\":\"GUAM__BASE\",\"WebMeetingServer\":\"securemeetingdev.pgilab.net\",\"bridgeOptions\":[\"GlobalMeet2\"],\"phoneInformation\":[{\"Location\":\"USA\",\"PhoneNumber\":\"1-719-955-1360\",\"PhoneType\":\"Toll\",\"CustomLocation\":null},{\"Location\":\"USA, Colorado, Colorado Springs\",\"PhoneNumber\":\"1-800-491-1547\",\"PhoneType\":\"Toll free\",\"CustomLocation\":null},{\"Location\":\"USA, Colorado, Colorado Springs\",\"PhoneNumber\":\"1-855-782-9300 (2nd bridge)\",\"PhoneType\":\"Toll free\",\"CustomLocation\":null},{\"Location\":\"USA, Colorado, Colorado Springs\",\"PhoneNumber\":\"7777100002@dn1.sip.pgilab.com\",\"PhoneType\":\"VOIP SIP\",\"CustomLocation\":null}],\"reservationOptions\":[],\"ParticipantAnonymity\":false,\"PrimaryAccessNumber\":\"1-719-955-1360\"}],\"AudioConferenceCount\":0,\"HasGMWebinar\":false,\"MeetingRoomsCount\":1}}";
//    AudioSelectionFragment audioSelectionFragment;
//    @Before
//    public void setup() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        ShadowLog.stream = System.out;
//        Context context = RuntimeEnvironment.application.getApplicationContext();
//        ConvergenceApplication.appContext = context;
//        ConvergenceApplication.mLogger = new TestLogger();
//        ClientInfoDaoUtils mClientInfoResultCache = Mockito.mock(ClientInfoDaoUtils.class);
//        PowerMockito.mockStatic(ClientInfoDaoUtils.class);
//        Mockito.when(ClientInfoDaoUtils.getInstance()).thenReturn(mClientInfoResultCache);
//
//        PowerMockito.mockStatic(AppAuthUtils.class);
//        AppAuthUtils appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
//        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
//        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
//        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.mockStatic(PermissionUtil.class);
//
//        PiaServiceAPI piaServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
//        PowerMockito.mockStatic(PiaServiceManager.class);
//        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
//        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceAPI);
//
//        Intent audioSelectionIntent = null;
//        audioSelectionIntent = new Intent(context, HostMeetingRoomActivity.class);
//        audioSelectionIntent.putExtra(AppConstants.FIRST_NAME,"Ashwani");
//        audioSelectionIntent.putExtra(AppConstants.IS_MEETING_HOST, true);
//        audioSelectionIntent.putExtra(AppConstants.MEETING_CONFERENCE_ID, "123456789");
//        mMeetingRoomActivity = Robolectric.buildActivity(HostMeetingRoomActivity.class, audioSelectionIntent)
//                .create()
//                .get();
//
//        mMeetingRoomActivityWeak = new WeakReference<Activity>(Robolectric.buildActivity(HostMeetingRoomActivity.class, audioSelectionIntent)
//                .create()
//                .get());
//
//        Whitebox.setInternalState(mMeetingRoomActivity, "mFinished", false);
//        audioSelectionFragment = AudioSelectionFragment.newInstance(true, true, false,
//                false, "Ashwani", false, false, false);
//        mMeetingRoomActivity.replaceFragments(audioSelectionFragment);
//        CheckBox checkBox = Mockito.mock(CheckBox.class);
//        Whitebox.setInternalState(audioSelectionFragment, "chkActivateSpeaker", checkBox);
//        Whitebox.setInternalState(audioSelectionFragment, "chkMuteMe", checkBox);
//        Whitebox.setInternalState(audioSelectionFragment, "mAudioSelectionCallbacks", mMeetingRoomActivity);
//        ConferenceManager.updateAudioConnectionState(ConnectionType.NO_AUDIO,
//                ConnectionState.DISCONNECTED);
//    }
//
//    @Test
//    public void testDialogVoipAction() throws IOException {
//        PowerMockito.when(CommonUtils.isConnectionAvailable(ConvergenceApplication.appContext)).thenReturn(true);
//        ClientInfoResponse clientInfoParsedResp = mapper.readValue(clientInfoResponse, ClientInfoResponse.class);
//        ClientInfoResultCache.getInstance().setValueInCache(clientInfoParsedResp);
////        audioSelectionFragment.onVoipAudioClicked();
//        ShadowActivity shadowActivity = Shadows.shadowOf(mMeetingRoomActivity);
//        Intent startedIntent = shadowActivity.getNextStartedActivity();
//        if (startedIntent != null) {
//            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
//            Assert.assertEquals(shadowIntent.getIntentClass().getSimpleName(), GuestMeetingRoomActivity.class.getSimpleName());
//        }
//    }
//
//   /* @Test
//    public void testDialOutAction() throws IOException {
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.mockStatic(PermissionUtil.class);
//        PermissionUtil.checkPermission(mMeetingRoomActivityWeak, Manifest.permission.RECORD_AUDIO, audioSelectionFragment);
//        PowerMockito.when(CommonUtils.isConnectionAvailable(ConvergenceApplication.getAppContext())).thenReturn(true);
//        ClientInfoResponse clientInfoParsedResp = mapper.readValue(clientInfoResponse, ClientInfoResponse.class);
//        ClientInfoResultCache.getInstance().setValueInCache(clientInfoParsedResp);
//        audioSelectionFragment.newInstance(true, true, true, true, "Ashwani", false);
//        audioSelectionFragment.onCallMeClicked();
//        ShadowActivity shadowActivity = Shadows.shadowOf(mMeetingRoomActivity);
//        Intent startedIntent = shadowActivity.getNextStartedActivity();
//        if (startedIntent != null) {
//            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
//            Assert.assertEquals(shadowIntent.getIntentClass().getSimpleName(), GuestMeetingRoomActivity.class.getSimpleName());
//        }
//    }*/
//}
