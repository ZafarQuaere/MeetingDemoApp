package com.pgi.convergencemeetings;

import android.content.Context;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.AddressDao;
import com.pgi.convergencemeetings.models.ClientContactDetailsDao;
import com.pgi.convergencemeetings.models.ClientDetailsDao;
import com.pgi.convergencemeetings.models.ClientInfoResultDao;
import com.pgi.convergencemeetings.models.CompanyDetailsDao;
import com.pgi.convergencemeetings.models.MeetingRoomDao;
import com.pgi.convergencemeetings.models.MeetingRoomOptionDao;
import com.pgi.convergencemeetings.models.MeetingRoomUrlsDao;
import com.pgi.convergencemeetings.models.TokenRefreshModel;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthPresenter;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PgiWebServiceManager;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by ashwanikumar on 8/17/2017.
 */
@PrepareForTest({OnBoardAuthPresenter.class, PgiWebServiceManager.class, ApplicationDao.class, CommonUtils.class,
        ClientInfoDaoUtils.class, AppAuthUtils.class, AuthorizationException.class, AuthorizationService.class, SharedPreferencesManager.class})
public class OnBoardPresenterMockTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    OnBoardAuthActivity onBoardAuthActivity;
    OnBoardAuthPresenter onBoardAuthPresenter;

    @Mock
    PgiWebServiceManager pgiWebServiceManager;
    @Mock
    SharedPreferencesManager sharedPreferencesManager;
    @Mock
    AuthorizationException authorizationException;
    @Mock
    AuthorizationService mAuthorizationService;

    String authStateJson = "{\"refreshToken\":\"7d10942d6e9d404f92bb5d51c577c5cf\",\"scope\":\"app.globalmeet app.globalmeet:webservices openid profile " +
            "email\",\"lastAuthorizationResponse\":{\"request\":{\"configuration\":{\"authorizationEndpoint\":\"https:\\/\\/identity-qab.dev.globalmeet.net\\" +
            "/oauth2\\/auth\",\"tokenEndpoint\":\"https:\\/\\/identity-qab.dev.globalmeet.net\\/oauth2\\/token\"},\"clientId\":\"e37112ee-4250-41e9-a96f-ac16" +
            "e1738abc\",\"responseType\":\"code\",\"redirectUri\":\"androididentity:\\/\\/com.globalmeet.app\",\"scope\":\"app.globalmeet app.globalmeet:webs" +
            "ervices openid profile email\",\"state\":\"Xa2X6kl1WxX9g71jbKXxsw\",\"codeVerifier\":\"g9DNmXnUrKhi8yfQsIc9cgv_hsXzWWgzlsY4JXT5y5zHFnwo-rkMk1Yto" +
            "T6yk90vOoqkl9mCzV9nSdgJm-FlIg\",\"codeVerifierChallenge\":\"ri9RqafHA1AEpz1zJ1AFzFYrR0SO3AXmkB_GHK24rwQ\",\"codeVerifierChallengeMethod\":\"S256\"" +
            ",\"additionalParameters\":{\"gm.enterprise_id\":\"4594\",\"gm.hub_id\":\"120963\",\"client_type\":\"GlobalMeet\",\"gm.default_company_id\":\"6356" +
            "23\",\"gm.time_zone_code\":\"GUAM__BASE\"}},\"state\":\"Xa2X6kl1WxX9g71jbKXxsw\",\"code\":\"7c00765f15f14b8a8ce7e623a90b91fb\",\"additional_para" +
            "meters\":{}},\"mLastTokenResponse\":{\"request\":{\"configuration\":{\"authorizationEndpoint\":\"https:\\/\\/identity-qab.dev.globalmeet.net\\/o" +
            "auth2\\/auth\",\"tokenEndpoint\":\"https:\\/\\/identity-qab.dev.globalmeet.net\\/oauth2\\/token\"},\"clientId\":\"e37112ee-4250-41e9-a96f-ac16e17" +
            "38abc\",\"grantType\":\"authorization_code\",\"redirectUri\":\"androididentity:\\/\\/com.globalmeet.app\",\"scope\":\"app.globalmeet app.globalm" +
            "eet:webservices openid profile email\",\"authorizationCode\":\"7c00765f15f14b8a8ce7e623a90b91fb\",\"additionalParameters\":{\"client_type\":\"Gl" +
            "obalMeet\",\"gm.enterprise_id\":\"4594\",\"gm.hub_id\":\"120963\",\"gm.time_zone_code\":\"GUAM__BASE\",\"gm.default_company_id\":\"635623\"}},\"t" +
            "oken_type\":\"Bearer\",\"access_token\":\"eyJraWQiOiIxMWZiMDA4NC04ZTBkLTQ3NDItYjg1MC1iNmM0MGZlNWJkNzQiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdW" +
            "QiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsid2Vic2VydmljZXMiLCJ" +
            "lbWFpbCIsImdsb2JhbG1lZXQiLCJvcGVuaWQiLCJwcm9maWxlIl0sInBnaV9zZXNzaW9uIjoiZDY1NjRiNTYtNTA2ZS00YzM4LTlmN2UtMjU0ZDE5ODNiMDhjIiwicGdpX2lkX2dtIjoiNTA" +
            "3ODc1OCIsImlzcyI6ImlkZW50aXR5LXFhLmRldi5nbG9iYWxtZWV0Lm5ldCIsInBnaV91c2VyIjp0cnVlLCJleHAiOjE1MDQ3NzM3ODQsImlhdCI6MTUwNDc3MDE4NH0.jddY8U5BCx0IhjN" +
            "tHeorn682NEdZWL_nb8c-JWKOiHp2VtUpKdVF4PypOY6cxSFZAjNO1TSUKuiGdKXgOg9biQ3nxl5U8cm5Od05MameTG8ywT8pWWtF8nz-49j0MeO9LTeeem-Htpo6h4cESB-zut-xnwR5o0-" +
            "mL_JYljXmdBQiCQeVmhQQDVThTi8sRcbhfSDoGaUNQJFAJmJX4gF4ppuPNt0PgDmLPmOZWrEpSgEiSx7XYbHaxZGepbUY1Ix3-1VaALwgg7ZBXogBljJ8xeskf4IOkgAGurypso8f-75NbVC" +
            "YiXRKV_M6xl_zt0z2c4lvxg6ung2RJUZZnjMqVg\",\"expires_at\":1504773783276,\"id_token\":\"eyJraWQiOiIxMWZiMDA4NC04ZTBkLTQ3NDItYjg1MC1iNmM0MGZlNWJkNz" +
            "QiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM" +
            "2RkZGZjNDExZTMiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaWRlbnRpdHktcWEuZGV2Lmdsb2JhbG1lZXQubmV0IiwibmFtZSI6IlN1ZGhlZXIgQ2hpbHVtdWxhIiwiZXhwIjoxN" +
            "TA0NzczNzg0LCJnaXZlbl9uYW1lIjoiU3VkaGVlciIsImlhdCI6MTUwNDc3MDE4NCwiZmFtaWx5X25hbWUiOiJDaGlsdW11bGEiLCJlbWFpbCI6IlN1ZGhlZXIuQ2hpbHVtdWxhQHBnaS5jb" +
            "20ifQ.eX-P2aH9EM-bHxenkEB_H7pvFB8Rw5CC2O7jskPWMOcpjJROvjD2LifFL-yVfO5pXfSw_ZTFokeFts_fu2ySa9fdEuNy7eWjTVErwDMr7o3M7gcl-ow3ovY8TWfTeTGRpI9S2Jkyj5" +
            "6_w4aoWv1STmNT2YlUbfmoy3QG2j0iEGVwtR2v4VTXYUsz6teubdi4l8PlyPrfaiBcE4-xegDF0sAI3qi0KQiT3mrq6bHoz6kKt9hdz1a5mb3wd62b0V3cdyBPW9W8KrmdAP-Fee34_9TsyT" +
            "JwHBg36JJAHKlpitOXz7nU7bbfDx45_gy7XmtzBKlzDn1szRkNlqD5bR3mOA\",\"refresh_token\":\"7d10942d6e9d404f92bb5d51c577c5cf\",\"additionalParameters\":{}}}";
    private Context context;

    private AuthState mAuthState;

    @Before
    public void setup() throws JSONException {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        TokenRefreshModel.INSTANCE.setRefreshing(true);
        PowerMockito.mockStatic(AuthorizationService.class);
        PowerMockito.mockStatic(SharedPreferencesManager.class);
        Mockito.when(SharedPreferencesManager.getInstance()).thenReturn(sharedPreferencesManager);
        onBoardAuthActivity = Robolectric.buildActivity(OnBoardAuthActivity.class).create().get();
        onBoardAuthPresenter = new OnBoardAuthPresenter(onBoardAuthActivity, onBoardAuthActivity);
        mAuthState = AuthState.jsonDeserialize(authStateJson);
        PowerMockito.mockStatic(AppAuthUtils.class);
        AppAuthUtils appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        String firstName = "Amit";
        String lastName = "Kumar";
        String userType = "Host";
        String email = "amit.kumar@pgi.com";
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        Mockito.when(appAuthUtils.getFirstName()).thenReturn(firstName);
        Mockito.when(appAuthUtils.getLastName()).thenReturn(lastName);
        Mockito.when(appAuthUtils.getPgiUserType()).thenReturn(userType);
        Mockito.when(appAuthUtils.getEmailId()).thenReturn(email);

        String idToken = "eyJraWQiOiIxMWZiMDA4NC04ZTBkLTQ3NDItYjg1MC1iNmM0MGZlNWJkNzQiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktY" +
                "Tk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaWRlbnRpdHktcWEuZGV2" +
                "Lmdsb2JhbG1lZXQubmV0IiwibmFtZSI6IlN1ZGhlZXIgQ2hpbHVtdWxhIiwiZXhwIjoxNTA0NzczNzg0LCJnaXZlbl9uYW1lIjoiU3VkaGVlciIsImlhdCI6MTUwNDc3MDE4NCwiZmFtaWx" +
                "5X25hbWUiOiJDaGlsdW11bGEiLCJlbWFpbCI6IlN1ZGhlZXIuQ2hpbHVtdWxhQHBnaS5jb20ifQ.eX-P2aH9EM-bHxenkEB_H7pvFB8Rw5CC2O7jskPWMOcpjJROvjD2LifFL-yVfO5pXfS" +
                "w_ZTFokeFts_fu2ySa9fdEuNy7eWjTVErwDMr7o3M7gcl-ow3ovY8TWfTeTGRpI9S2Jkyj56_w4aoWv1STmNT2YlUbfmoy3QG2j0iEGVwtR2v4VTXYUsz6teubdi4l8PlyPrfaiBcE4-xeg" +
                "DF0sAI3qi0KQiT3mrq6bHoz6kKt9hdz1a5mb3wd62b0V3cdyBPW9W8KrmdAP-Fee34_9TsyTJwHBg36JJAHKlpitOXz7nU7bbfDx45_gy7XmtzBKlzDn1szRkNlqD5bR3mOA";

        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
    }

    @Test
    public void testTokenResponseSuccess() {
        ConvergenceApplication.appContext = context;
        CommonUtils.setContext(context);
        ClientInfoResultDao clientInfoResultDao = Mockito.mock(ClientInfoResultDao.class);
        AddressDao addressDao = Mockito.mock(AddressDao.class);
        ClientContactDetailsDao clientContactDetailsDao = Mockito.mock(ClientContactDetailsDao.class);
        ClientDetailsDao clientDetailsDao = Mockito.mock(ClientDetailsDao.class);
        CompanyDetailsDao companyDetailsDao = Mockito.mock(CompanyDetailsDao.class);
        MeetingRoomDao meetingRoomDao = Mockito.mock(MeetingRoomDao.class);
        MeetingRoomUrlsDao meetingRoomUrlsDao = Mockito.mock(MeetingRoomUrlsDao.class);
        MeetingRoomOptionDao meetingRoomOptionDao = Mockito.mock(MeetingRoomOptionDao.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        PowerMockito.when(ApplicationDao.get(onBoardAuthActivity)).thenReturn(applicationDao);
        Mockito.when(applicationDao.getClientResult()).thenReturn(clientInfoResultDao);
        Mockito.when(applicationDao.getAddress()).thenReturn(addressDao);
        Mockito.when(applicationDao.getClientContactDetails()).thenReturn(clientContactDetailsDao);
        Mockito.when(applicationDao.getClientDetail()).thenReturn(clientDetailsDao);
        Mockito.when(applicationDao.getCompanyDetail()).thenReturn(companyDetailsDao);
        Mockito.when(applicationDao.getMeetingRoom()).thenReturn(meetingRoomDao);
        Mockito.when(applicationDao.getMeetingRoomUrls()).thenReturn(meetingRoomUrlsDao);
        Mockito.when(applicationDao.getMeetingRoomOptions()).thenReturn(meetingRoomOptionDao);
        ClientInfoDaoUtils clientInfoDaoUtils = Mockito.mock(ClientInfoDaoUtils.class);
        PowerMockito.mockStatic(ClientInfoDaoUtils.class);
        Mockito.when(ClientInfoDaoUtils.getInstance()).thenReturn(clientInfoDaoUtils);
        // PowerMockito.verifyStatic(Mockito.times(1));
    }
}
