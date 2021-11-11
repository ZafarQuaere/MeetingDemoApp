package com.pgi.convergencemeetings.models.enterUrlModel;

/**
 * Created by nnennaiheke on 9/6/17.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "MeetingRoomSearchResult"
})
public class MeetingRoomSearchResult {

    @JsonProperty("MeetingRoomSearchResult")
    private MeetingRoomSearchResult_ meetingRoomSearchResult;

    @JsonProperty("MeetingRoomSearchResult")
    public MeetingRoomSearchResult_ getMeetingRoomSearchResult() {
        return meetingRoomSearchResult;
    }

    @JsonProperty("MeetingRoomSearchResult")
    public void setMeetingRoomSearchResult(MeetingRoomSearchResult_ meetingRoomSearchResult) {
        this.meetingRoomSearchResult = meetingRoomSearchResult;
    }

}
