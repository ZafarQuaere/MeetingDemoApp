package com.pgi.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.spyk
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Sudheer Chilumula on 2019-01-17.
 * PGi
 * sudheer.chilumula@pgi.com
 */
@RunWith(AndroidJUnit4::class)
class PGiAuthConfigurationTest {

  private lateinit var authConfiguration: PGiAuthConfiguration
  private lateinit var mContext: Context
  private lateinit var authConfigurationSpy: PGiAuthConfiguration

  private val mockErrorConfig = "{\n" +
      "    \"client_id\": \"1fd17d84-a877-4b26-ba16-11a8e290feeb\",\n" +
      "    \"redirect_uri\": \"testIdentity://com.globalmeet.app\",\n" +
      "    \"authorization_scope\": \"openid profile email app.globalmeet:logging app.globalmeet:Pulsar app.globalmeet:uapi app.globalmeet:webservices app.globalmeet:screenshare app.globalmeet:gmsearch\",\n" +
      "    \"discovery_uri\": \"\",\n" +
      "    \"authorization_endpoint_uri\": \"https://identity1.dev.globalmeet.net/oauth2/auth\",\n" +
      "    \"token_endpoint_uri\": \"https://identity1.dev.globalmeet.net/oauth2/token\",\n" +
      "    \"registration_endpoint_uri\": \"\",\n" +
      "    \"user_info_endpoint_uri\": \"https://identity1.dev.globalmeet.net/oauth2/userinfo\",\n" +
      "    \"logout_endpoint_uri\": \"https://identity1.dev.globalmeet.net/users/logout\",\n" +
      "    \"revoke_endpoint_uri\": \"https://identity1.dev.globalmeet.net/oauth2/revoke?token=\",\n" +
      "    \"https_required\": true\n" +
      "  }"

  @Before
  fun `set up`() {
    mContext = ApplicationProvider.getApplicationContext<Context>()
    authConfiguration = PGiAuthConfiguration.getInstance(mContext)
    authConfigurationSpy = spyk(authConfiguration, "aothConfigSpy", recordPrivateCalls = true)
  }

  @Test
  fun `test authconfigurator is not null`() {
//    assertThat(authConfiguration).isNotNull
  }

  @Test
  fun `test configurationchange returns true`() {
    assertThat(authConfiguration.hasConfigurationChanged()).isTrue()
  }

  @Test
  fun `test getConnectionBuilder`() {
    assertThat(authConfiguration.getConnectionBuilder()).isEqualTo(DefaultConnectionBuilder.INSTANCE)
  }

  @Test
  fun `test config values`() {
    if(BuildConfig.FLAVOR == "prod") {
      assertThat(authConfiguration.mClientId).isEqualTo("a332c6ad-cb8f-4bbf-8f3b-bcd75205cdbb")
      assertThat(authConfiguration.mAuthEndpointUri.toString()).isEqualTo("https://login.globalmeet.com/oauth2/auth")
      assertThat(authConfiguration.mRedirectUri.toString()).isEqualTo("androididentity://com.globalmeet.app")
      assertThat(authConfiguration.mUserInfoEndpointUri.toString()).isEqualTo("https://login.globalmeet.com/oauth2/userinfo")
      assertThat(authConfiguration.mTokenEndpointUri.toString()).isEqualTo("https://login.globalmeet.com/oauth2/token")
      assertThat(authConfiguration.mRegistrationEndpointUri).isNull()
      assertThat(authConfiguration.mHttpsRequired).isEqualTo(true)
      assertThat(authConfiguration.mScope).isEqualTo("openid profile email app.globalmeet:logging " +
          "app.globalmeet:Pulsar app.globalmeet:uapi app.globalmeet:webservices app.globalmeet:screenshare app.globalmeet:gmsearch app.globalmeet:gmservices.core")
      assertThat(authConfiguration.mDiscoveryUri).isNull()
    }
  }
}