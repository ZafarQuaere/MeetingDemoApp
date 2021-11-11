package com.pgi.network

import org.junit.Assert
import org.junit.Test

class GMWebUAPIServiceManagerTest {

    @Test
    fun `test onCreate`() {
        GMWebUAPIServiceManager.baseUrl = "https://pgi.globalmeet.com"
        val api = GMWebUAPIServiceManager.create()
        Assert.assertNotNull(api)
    }
}