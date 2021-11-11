package com.pgi.convergencemeetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pgi.auth.PGiIdentityAuthService;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthPresenter;

import net.openid.appauth.TokenResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by ashwanikumar on 8/17/2017.
 */
@PrepareForTest({ApplicationDao.class})
public class OnBoardAuthPresenterTest extends RobolectricTest{

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPrefs;
    OnBoardAuthActivity onBoardAuthActivity;
    OnBoardAuthActivity onBoardAuthActivitySpy;
    OnBoardAuthPresenter onBoardAuthPresenter;
    OnBoardAuthPresenter onBoardAuthPresenterSpy;
    Context context;
    PGiIdentityAuthService spy;

    private String tokenResponse = "{\n" +
                                    "    \"access_token\": \"eyJraWQiOiI3YWZkMmEyNi05NWFhLTQ3NTctODg4YS0yZjE5MjAzNDcwOGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkZGIwMzI0NS0yN2MwLTQ0YjEtODk2ZS0wMDBmNTY5YTMyMjQiLCJzY3AiOlsid2Vic2VydmljZXMiLCJsb2dnaW5nIiwiZW1haWwiLCJnbXNlYXJjaCIsIm9wZW5pZCIsInVhcGkiLCJwcm9maWxlIiwiUHVsc2FyIiwic2NyZWVuc2hhcmUiXSwicGdpX3Nlc3Npb24iOiIzZTYwZWQ1Ny0xZTgwLTQ2MzItYTYxZS1iY2Y5Mzk1OTFlOGIiLCJpc3MiOiJsb2dpbi5wZ2lpZC5jb20iLCJwZ2lfY2xpZW50X3R5cGUiOiJBbmRyb2lkIiwicGdpX2lkX2VtYWlsIjoic3VkaGVlci5jaGlsdW11bGFAcGdpLmNvbSIsImF1ZCI6ImEzMzJjNmFkLWNiOGYtNGJiZi04ZjNiLWJjZDc1MjA1Y2RiYiIsInBnaV9sb2dpbl9zb3VyY2UiOiJHbG9iYWxNZWV0IiwicGdpX2lkX3VzZXJfZ3JvdXAiOiIzMjUyMDQiLCJwZ2lfaWRfZ20iOiI3MTQyMTczIiwicGdpX3VzZXIiOnRydWUsImV4cCI6MTU0Nzg1MTczNiwiaWF0IjoxNTQ3ODQ0NTM2fQ.iMUAGVXDC-xHHAWzCrQriPpYxQDjDFkD8Tks327_1NMU4QqLzY_2REB3YS0e0JlgSz7VOcz2MMe4Gcp3Ak18XZCyBEDu7EffHBjUx9dWXgkm4fY2lveMDy0tM1vAA6Qort7K_rwI83GsRdj-uunuIxf0CqU5f25rD1PV96WWam5cRD8vddgKQdnjM9GWA0LR6C_f7Q_6FirYZ9okYC_Lj2qGdwOvJNVWPKsvhtTHayR0CuopyhZhz8cveyP1OoNUvirA-LWkjKWEhl4x\",\n" +
                                    "    \"accessTokenExpirationTime\": 1547851736436,\n" +
                                    "    \"additionalParameters\": {},\n" +
                                    "    \"id_token\": \"eyJraWQiOiI3YWZkMmEyNi05NWFhLTQ3NTctODg4YS0yZjE5MjAzNDcwOGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkZGIwMzI0NS0yN2MwLTQ0YjEtODk2ZS0wMDBmNTY5YTMyMjQiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZV92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoibG9naW4ucGdpaWQuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1ZGhlZXLDpMOkw6TDtsO2w7zDvCIsImxvY2FsZSI6ImVuIiwiYXVkIjoiYTMzMmM2YWQtY2I4Zi00YmJmLThmM2ItYmNkNzUyMDVjZGJiIiwicGdpX2xvZ2luX3NvdXJjZSI6Ikdsb2JhbE1lZXQiLCJwZ2lfdXNlcl90eXBlIjoiQ2xpZW50IiwibmFtZSI6IlN1ZGhlZXLDpMOkw6TDtsO2w7zDvCBDaGlsdW11bGEiLCJleHAiOjE1NDc4NDgxMzYsImlhdCI6MTU0Nzg0NDUzNiwiZmFtaWx5X25hbWUiOiJDaGlsdW11bGEiLCJqdGkiOiJmZGYwYTAwZi1iOTA3LTRhNTUtODFiNy1lZDkxODk2NjE1YzgiLCJlbWFpbCI6InN1ZGhlZXIuY2hpbHVtdWxhQHBnaS5jb20ifQ.Kcdyo2aRtL0g08VQ_avbcuwFasxqPF0kPh2KetVUQW6ksxhvQjsvEFjccHfypn0lqfsYGsb3pN-5seEXMWQXXxGTLTH8JOmVGzY-0cItAdnnSklUKWnsQBowGGUdA-zlX7no_wxvGi-yiptYK49zXg7XT1BPTG4Vqhj1rsf2DjtGYInrSh7k-ykuqlVuB_WzSdc4-RJB7CqC8Fu6uj9qH_2DOOhAM_MHsR966A4zMbG0s2nvZNnxBSSUXSCQ_1_4vgD3QiEpFmRA7hdpIPp03M-V4tCdGfgTz2mLMnn8WY7paObh\",\n" +
                                    "    \"refresh_token\": \"1kslz59Ip9QkJXQ0dAkKh5MM0QLDkVLX\",\n" +
                                    "    \"scope\": \"webservices logging email gmsearch openid uapi profile Pulsar screenshare\",\n" +
                                    "    \"token_type\": \"Bearer\",\n" +
                                    "    \"request\": {\n" +
                                    "        \"configuration\": {\n" +
                                    "            \"authorizationEndpoint\": \"https://login.globalmeet.com/oauth2/auth\",\n" +
                                    "            \"tokenEndpoint\": \"https://login.globalmeet.com/oauth2/token\"\n" +
                                    "        },\n" +
                                    "        \"clientId\": \"a332c6ad-cb8f-4bbf-8f3b-bcd75205cdbb\",\n" +
                                    "        \"authorizationCode\" : \"JBhsuXMt7Hom24Uz6nmjlITCMHKi5Pjj\",\n" +
                                    "        \"redirectUri\": \"androididentity://com.globalmeet.app\",\n" +
                                    "        \"scope\": \"openid profile email app.globalmeet:logging app.globalmeet:Pulsar app.globalmeet:uapi app.globalmeet:webservices app.globalmeet:screenshare app.globalmeet:gmsearch\",\n" +
                                    "        \"codeVerifierChallengeMethod\": \"S256\",\n" +
                                    "        \"grantType\": \"authorization_code\",\n" +
                                    "        \"refreshToken\": \"1kslz59Ip9QkJXQ0dAkKh5MM0QLDkVLX\",\n" +
                                    "        \"additionalParameters\": {}\n" +
                                    "    }\n" +
                                    "}";
    @Before
    public void setup(){
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        PowerMockito.mockStatic(ApplicationDao.class);
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Whitebox.setInternalState(sharedPreferencesManager, "mSharedPrefs", sharedPrefs);
        Whitebox.setInternalState(sharedPreferencesManager, "mEditor", sharedPrefs.edit());
        onBoardAuthActivity = Robolectric.buildActivity(OnBoardAuthActivity.class).get();
        onBoardAuthActivitySpy = Mockito.spy(onBoardAuthActivity);
        PowerMockito.doNothing().when(onBoardAuthActivitySpy).onClientInfoReceived(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        onBoardAuthPresenter = new OnBoardAuthPresenter(onBoardAuthActivitySpy, onBoardAuthActivitySpy);
        onBoardAuthPresenterSpy = Mockito.spy(onBoardAuthPresenter);
        PGiIdentityAuthService appAuthService = PGiIdentityAuthService.Companion.getInstance(context);
        spy = Mockito.spy(appAuthService);
        Whitebox.setInternalState(onBoardAuthPresenter, "mPGiAuthService", spy);
        PowerMockito.doNothing().when(spy).doAuth(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        PowerMockito.doNothing().when(spy).onAuthResponse(Mockito.any());
    }
    @Test
    public void testStartAppAutherization() throws Exception {
       onBoardAuthPresenter.startAppAuthorization();
       Assert.assertNotNull(spy);
       Mockito.verify(spy).doAuth(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testHandleAuthorizationResponse() throws Exception {
        PGiIdentityAuthService appAuthService = PGiIdentityAuthService.Companion.getInstance(context);
        Intent intent = new Intent();
        onBoardAuthPresenter.handleAuthorizationResponse(intent);
        Assert.assertNotNull(appAuthService);
        Mockito.verify(spy).onAuthResponse(intent);
    }

    @Test
    public void testTokenResponseCalled() throws Exception {
        PGiIdentityAuthService appAuthService = PGiIdentityAuthService.Companion.getInstance(context);
        Intent intent = new Intent();
        onBoardAuthPresenterSpy.handleAuthorizationResponse(intent);
        TokenResponse response = TokenResponse.jsonDeserialize(tokenResponse);
        appAuthService.getTokenSubject().onNext(response);
        Mockito.verify(onBoardAuthPresenterSpy).onTokenResponseSuccess();
    }

    @Test
    public void testOnDestroy() throws Exception {
        onBoardAuthPresenter.destroy();
        Mockito.verify(spy).destroy();
    }

    @Test
    public void testClientInfoSuccess() throws Exception {
        onBoardAuthPresenter.onClientInfoSuccess("12345", "56789");
        Assert.assertEquals(ConvergenceApplication.mLogger.getUserModel().getId(), "12345");
    }
}
