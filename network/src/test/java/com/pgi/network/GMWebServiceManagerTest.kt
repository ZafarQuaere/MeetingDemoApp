package com.pgi.network

import android.content.Context
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.Logger
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GMWebServiceManagerTest {

    @Before
    fun setup() {
        val mockContext = mockkClass(Context::class)
        CoreApplication.appContext = mockContext
        val mockLogger = mockkClass(Logger::class)
        CoreApplication.mLogger = mockLogger

        val mockAuthService = mockkClass(PGiIdentityAuthService::class)
        mockkObject(PGiIdentityAuthService)
        every { PGiIdentityAuthService.getInstance(mockContext) } returns mockAuthService
    }

    @Test
    fun `test create`() {
        val gmWebServiceAPI = GMWebServiceManager().create()
        Assert.assertNotNull(gmWebServiceAPI)
    }

}