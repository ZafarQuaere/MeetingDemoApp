package com.pgi.convergencemeetings.utils;

import androidx.test.core.app.ApplicationProvider;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergence.application.CoreApplication;
import com.pgi.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AppAuthUtilsTest extends RobolectricTest {

  AppAuthUtils mAppAuthUtils;
  @Mock
  Logger mLogger;
//  AppAuthUtils mAppAuthUtilsSpy;
//
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    CoreApplication.appContext = ApplicationProvider.getApplicationContext();
    mAppAuthUtils = AppAuthUtils.getInstance();
//    JSONObject coderes = new JSONObject(this.getJson("appauth/coderesponse.json"));
//    JSONObject tokenres = new JSONObject(getJson("appauth/tokenresponse.json").toString());
//    AuthorizationResponse authResponse = AuthorizationResponse.jsonDeserialize(coderes);
//    TokenResponse tokReponse = TokenResponse.jsonDeserialize(tokenres);
//    AuthState mAuthState = new AuthState(authResponse, tokReponse,null);
//    mAppAuthUtils = AppAuthUtils.getInstance();
//    mAppAuthUtilsSpy = Mockito.spy(mAppAuthUtils);
//    Mockito.when(mAppAuthUtilsSpy.getAuthState()).thenReturn(mAuthState);
  }
  @Test
  public void testSetLogger() {
    mAppAuthUtils.setLogger(mLogger);
    Assert.assertNotNull(mAppAuthUtils);
  }
//
//  @Test
//  public void testAccessToken() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getAccessToken(), "eyJraWQiOiI3YWZkMmEyNi05NWFhLTQ3NTctODg4YS0yZjE5MjAzNDcwOGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkZGIwMzI0NS0yN2MwLTQ0YjEtODk2ZS0wMDBmNTY5YTMyMjQiLCJzY3AiOlsid2Vic2VydmljZXMiLCJsb2dnaW5nIiwiZW1haWwiLCJnbXNlYXJjaCIsIm9wZW5pZCIsInVhcGkiLCJwcm9maWxlIiwiUHVsc2FyIiwic2NyZWVuc2hhcmUiXSwicGdpX3Nlc3Npb24iOiIzZTYwZWQ1Ny0xZTgwLTQ2MzItYTYxZS1iY2Y5Mzk1OTFlOGIiLCJpc3MiOiJsb2dpbi5wZ2lpZC5jb20iLCJwZ2lfY2xpZW50X3R5cGUiOiJBbmRyb2lkIiwicGdpX2lkX2VtYWlsIjoic3VkaGVlci5jaGlsdW11bGFAcGdpLmNvbSIsImF1ZCI6ImEzMzJjNmFkLWNiOGYtNGJiZi04ZjNiLWJjZDc1MjA1Y2RiYiIsInBnaV9sb2dpbl9zb3VyY2UiOiJHbG9iYWxNZWV0IiwicGdpX2lkX3VzZXJfZ3JvdXAiOiIzMjUyMDQiLCJwZ2lfaWRfZ20iOiI3MTQyMTczIiwicGdpX3VzZXIiOnRydWUsImV4cCI6MTU0Nzg1MTczNiwiaWF0IjoxNTQ3ODQ0NTM2fQ.iMUAGVXDC-xHHAWzCrQriPpYxQDjDFkD8Tks327_1NMU4QqLzY_2REB3YS0e0JlgSz7VOcz2MMe4Gcp3Ak18XZCyBEDu7EffHBjUx9dWXgkm4fY2lveMDy0tM1vAA6Qort7K_rwI83GsRdj-uunuIxf0CqU5f25rD1PV96WWam5cRD8vddgKQdnjM9GWA0LR6C_f7Q_6FirYZ9okYC_Lj2qGdwOvJNVWPKsvhtTHayR0CuopyhZhz8cveyP1OoNUvirA-LWkjKWEhl4x");
//  }
//
//  @Test
//  public void tesRefreshToken() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getRefreshToken(), "1kslz59Ip9QkJXQ0dAkKh5MM0QLDkVLX");
//  }
//
//  @Test
//  public void testFirstName() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getFirstName(), "Sudheeräääööüü");
//  }
//
//  @Test
//  public void testLastName() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getLastName(), "Chilumula");
//  }
//
//  @Test
//  public void tesEmailId() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getEmailId(), "sudheer.chilumula@pgi.com");
//  }
//
//  @Test
//  public void testUserType() {
//    Assert.assertEquals(mAppAuthUtilsSpy.getPgiUserType(), "Client");
//  }
//
//  @Test
//  public void tesyGuestUserType() {
//    Assert.assertEquals(mAppAuthUtilsSpy.isUserTypeGuest(), false);
//  }
//
//  private String getJson(String path) throws IOException {
//    URL uri = this.getClass().getClassLoader().getResource(path);
//    File file = new File(uri.getFile());
//    return new String(Files.toByteArray(file));
//  }
//
//
}
