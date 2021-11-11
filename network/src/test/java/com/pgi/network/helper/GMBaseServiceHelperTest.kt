package com.pgi.network.helper

import com.pgi.auth.PGiIdentityAuthService
import com.pgi.logging.Logger
import com.pgi.network.interceptors.PGiTokenAuthenticator
import com.pgi.network.interceptors.PGiTokenValidationInterceptor
import io.mockk.mockkClass
import org.junit.Assert
import org.junit.Test

class GMBaseServiceHelperTest {

    @Test
    fun `test okClient`() {
        val httpClient = GMBaseServiceHelper.okClient()
        Assert.assertEquals(httpClient.connectTimeoutMillis(), 60000)
        Assert.assertTrue(httpClient.authenticator() is PGiTokenAuthenticator)
    }

    @Test
    fun `test okClient add interceptor`() {
        val mockPGiIdentityAuthService = mockkClass(PGiIdentityAuthService::class)
        val mockLogger = mockkClass(Logger::class)
        val interceptor = PGiTokenValidationInterceptor(mockPGiIdentityAuthService, mockLogger)
        val client = GMBaseServiceHelper.okClient(listOf(interceptor))
        Assert.assertTrue(client.interceptors().contains(interceptor))
    }
}