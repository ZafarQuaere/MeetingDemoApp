package com.pgi.convergencemeetings.models.enterUrlModel;

/**
 * Created by nnennaiheke on 9/6/17.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Detail",
        "MeetingRoomId"
})
public class Result {

    @JsonProperty("Detail")
    private Detail detail;
    @JsonProperty("MeetingRoomId")
    private int meetingRoomId;

    @JsonProperty("Detail")
    public Detail getDetail() {
        return detail;
    }

    @JsonProperty("Detail")
    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    @JsonProperty("MeetingRoomId")
    public int getMeetingRoomId() {
        return meetingRoomId;
    }

    @JsonProperty("MeetingRoomId")
    public void setMeetingRoomId(int meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

}