package com.pgi.convergencemeetings.mockservers

import com.pgi.network.UAPIEndPoints
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

class MockUAPIServerSessionUsedDispatcher: Dispatcher() {

    override fun dispatch(request: RecordedRequest?): MockResponse {
        if(request?.getHeader("Authorization")?.contains("expired_token", true) == false) {
            val line = request.body?.readUtf8Line()
            when (request.path) {

                UAPIEndPoints.JOINMEETING -> {
                    return MockResponse()
                            .setResponseCode(403)
                            .setBody(getJson("json/uapi/joinalreadyjoined.json"))
                }

            }
        } else {
            return MockResponse()
                    .setResponseCode(401)
                    .setBody(getJson("json/uapi/unautorizederror.json"))
        }
        return MockResponse().setResponseCode(404)
    }


    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getJson(path : String) : String {
        val uri = javaClass.classLoader!!.getResource(path)
        val file = File(uri.file)
        return String(file.readBytes())
    }
}