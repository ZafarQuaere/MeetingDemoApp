package com.pgi.network.mockservers

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockGMWebServerDispatcher: Dispatcher() {
    override fun dispatch(request: RecordedRequest?): MockResponse {
        if(request?.getHeader("Authorization")?.contains("expired_token", true) == false) {
            if (request.path == "/REST/V1/Services/GlobalMeet.svc/MeetingRoom/12345") {
                return MockResponse()
                        .setResponseCode(200)
                        .setBody("""
                            {"MeetingRoomGetResult":{"AudioDetail":{"ParticipantPassCode":"7708649931","phoneInformation":[{"Location":"Vietnam, Ho Chi Minh","PhoneNumber":"+84 28 5678 4438","PhoneType":"Local","CustomLocation":"Vietnam (toll free)"},{"Location":"Vietnam","PhoneNumber":"1800 400 370","PhoneType":"International toll free","CustomLocation":"Vietnam (toll free)"}],"PrimaryAccessNumber":"1-605-475-5603"},"MeetingRoomDetail":{"EnableVrc":true},"MeetingRoomId":3122397,"MeetingRoomUrls":{"VRCUrl":"3122397@35.174.177.7"}}}
                        """.trimIndent())
            }
        }
        return MockResponse().setResponseCode(404)
    }
}