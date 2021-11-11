package com.pgi.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.spyk
import io.mockk.verify
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


/**
 * Created by Sudheer Chilumula on 2019-01-23.
 * PGi
 * sudheer.chilumula@pgi.com
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PGiAuthStateManagerTest {

  private lateinit var authStateManager: PGiAuthStateManager
  private lateinit var mContext: Context
  private lateinit var authStateManagerSpy: PGiAuthStateManager
  private val codeResponse = "{\n" +
      "  \"request\": {\n" +
      "    \"configuration\": {\n" +
      "      \"authorizationEndpoint\": \"https://login.globalmeet.com/oauth2/auth\",\n" +
      "      \"tokenEndpoint\": \"https://login.globalmeet.com/oauth2/token\"\n" +
      "    },\n" +
      "    \"clientId\": \"a332c6ad-cb8f-4bbf-8f3b-bcd75205cdbb\",\n" +
      "    \"responseType\": \"code\",\n" +
      "    \"redirectUri\": \"androididentity://com.globalmeet.app\",\n" +
      "    \"scope\": \"openid profile email app.globalmeet:logging app.globalmeet:Pulsar app.globalmeet:uapi app.globalmeet:webservices app.globalmeet:screenshare app.globalmeet:gmsearch\",\n" +
      "    \"state\": \"FAdtH6zTecuVm9fM0z9NZw\",\n" +
      "    \"codeVerifier\": \"wiJl8m_MMjGfj5Ptnza4ahC0-Ln1R7Rd_gMX1r7S-5z4ckJsckHJZJ8o053qC2JtFT9bxsqZfQVBK5GQmbb1pg\",\n" +
      "    \"codeVerifierChallenge\": \"Sh5TwUrvhHZ-IRxIw7tY2aEJIEDGs2Aw4TYfxVe4RxM\",\n" +
      "    \"codeVerifierChallengeMethod\": \"S256\",\n" +
      "    \"additionalParameters\": {\n" +
      "      \"access_type\": \"offline\",\n" +
      "      \"auth_by_client\": \"true\",\n" +
      "      \"locale\": \"en\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"state\": \"FAdtH6zTecuVm9fM0z9NZw\",\n" +
      "  \"code\": \"WRz4yc9HfKhhLzfEDwmjc5Lf4S3roQfh\",\n" +
      "  \"additional_parameters\": {}\n" +
      "}\n"



  private val tokenResponse = "{\n" +
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
      "}"

  @Before
  fun `set up`() {
    mContext = ApplicationProvider.getApplicationContext<Context>()
    authStateManager = PGiAuthStateManager.getInstance(mContext)
    authStateManagerSpy = spyk(authStateManager, "stateManagerSpy", recordPrivateCalls = true)
  }

  @Test
  fun `1 - test authconfigurator is not null`() {
    Assertions.assertThat(authStateManager).isNotNull
  }

  @Test
  fun `2 - test get Current`() {
    authStateManagerSpy.replace(AuthState())
    authStateManagerSpy.getCurrent()
    verify(exactly = 0) {
      authStateManagerSpy.readState()
    }
  }

  @Test
  fun `3 - test updateAfterAuthorization`() {
    val codeResponse = AuthorizationResponse.jsonDeserialize(codeResponse)
    authStateManagerSpy.updateAfterAuthorization(codeResponse, null)
    verify {
      authStateManagerSpy.writeState(any())
    }
  }

  @Test
  fun `4 - test updateAfterTokenResponse`() {
    val tokenResponse = TokenResponse.jsonDeserialize(tokenResponse)
    authStateManagerSpy.updateAfterTokenResponse(tokenResponse, null)
    verify {
      authStateManagerSpy.writeState(any())
    }
  }

}