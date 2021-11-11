package com.pgi.convergencemeetings.mockservers

import com.pgi.network.UAPIEndPoints
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockUAPIServerRequestFailDispatcher: Dispatcher() {
    override fun dispatch(request: RecordedRequest?): MockResponse {
        if(request?.getHeader("Authorization")?.contains("expired_token", true) == false) {
            val line = request.body?.readUtf8Line()
            when (request.path) {
                UAPIEndPoints.UPDATE_FRICTION_FREE -> {
                    return MockResponse()
                            .setResponseCode(401)
                }
            }
        }
        return MockResponse().setResponseCode(404)
    }
}