package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nnennaiheke on 8/3/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AttendeeEvaluateUrl",
    "AttendeeJoinUrl",
    "AttendeeRegisterUrl",
    "FileDownloadUrl",
    "PresenterJoinUrl",
    "EndOfMeetingUrl"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingRoomUrls {

    @Id(autoincrement = true)
    private Long id;

    private String meetingRoomUrlsId;

    @JsonProperty("AttendeeEvaluateUrl")
    private String attendeeEvaluateUrl;
    @JsonProperty("AttendeeJoinUrl")
    private String attendeeJoinUrl;
    @JsonProperty("AttendeeRegisterUrl")
    private String attendeeRegisterUrl;
    @JsonProperty("FileDownloadUrl")
    private String fileDownloadUrl;
    @JsonProperty("PresenterJoinUrl")
    private String presenterJoinUrl;
    @JsonProperty("EndOfMeetingUrl")
    private String endOfMeetingUrl;

    @Generated(hash = 242292531)
    public MeetingRoomUrls(Long id, String meetingRoomUrlsId,
                           String attendeeEvaluateUrl, String attendeeJoinUrl,
                           String attendeeRegisterUrl, String fileDownloadUrl,
                           String presenterJoinUrl, String endOfMeetingUrl) {
        this.id = id;
        this.meetingRoomUrlsId = meetingRoomUrlsId;
        this.attendeeEvaluateUrl = attendeeEvaluateUrl;
        this.attendeeJoinUrl = attendeeJoinUrl;
        this.attendeeRegisterUrl = attendeeRegisterUrl;
        this.fileDownloadUrl = fileDownloadUrl;
        this.presenterJoinUrl = presenterJoinUrl;
        this.endOfMeetingUrl = endOfMeetingUrl;
    }

    @Generated(hash = 211936108)
    public MeetingRoomUrls() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingRoomUrlsId() {
        return meetingRoomUrlsId;
    }

    public void setMeetingRoomUrlsId(String meetingRoomUrlsId) {
        this.meetingRoomUrlsId = meetingRoomUrlsId;
    }

    @JsonProperty("AttendeeEvaluateUrl")
    public String getAttendeeEvaluateUrl() {
        return attendeeEvaluateUrl;
    }

    @JsonProperty("AttendeeEvaluateUrl")
    public void setAttendeeEvaluateUrl(String attendeeEvaluateUrl) {
        this.attendeeEvaluateUrl = attendeeEvaluateUrl;
    }

    @JsonProperty("AttendeeJoinUrl")
    public String getAttendeeJoinUrl() {
        return attendeeJoinUrl;
    }

    @JsonProperty("AttendeeJoinUrl")
    public void setAttendeeJoinUrl(String attendeeJoinUrl) {
        this.attendeeJoinUrl = attendeeJoinUrl;
    }

    @JsonProperty("AttendeeRegisterUrl")
    public String getAttendeeRegisterUrl() {
        return attendeeRegisterUrl;
    }

    @JsonProperty("AttendeeRegisterUrl")
    public void setAttendeeRegisterUrl(String attendeeRegisterUrl) {
        this.attendeeRegisterUrl = attendeeRegisterUrl;
    }

    @JsonProperty("FileDownloadUrl")
    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    @JsonProperty("FileDownloadUrl")
    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    @JsonProperty("PresenterJoinUrl")
    public String getPresenterJoinUrl() {
        return presenterJoinUrl;
    }

    @JsonProperty("PresenterJoinUrl")
    public void setPresenterJoinUrl(String presenterJoinUrl) {
        this.presenterJoinUrl = presenterJoinUrl;
    }

    @JsonProperty("EndOfMeetingUrl")
    public String getEndOfMeetingUrl() {
        return endOfMeetingUrl;
    }

    @JsonProperty("EndOfMeetingUrl")
    public void setEndOfMeetingUrl(String endOfMeetingUrl) {
        this.endOfMeetingUrl = endOfMeetingUrl;
    }

}