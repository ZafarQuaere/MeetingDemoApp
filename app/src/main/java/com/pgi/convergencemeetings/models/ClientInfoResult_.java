
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CorrelationId",
    "Errors",
    "ExecutionTime",
    "MessageId",
    "ServerDateTime",
    "AudioOnlyConferences",
    "ClientDetails",
    "CompanyDetails",
    "ExcessiveConfs",
    "MeetingRooms",
    "AudioConferenceCount",
    "HasGMWebinar",
    "MeetingRoomsCount"
})
public class ClientInfoResult_ {

    @JsonProperty("CorrelationId")
    private Object correlationId;
    @JsonProperty("Errors")
    private List<Object> errors = null;
    @JsonProperty("ExecutionTime")
    private Integer executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;
    @JsonProperty("AudioOnlyConferences")
    private List<Object> audioOnlyConferences = null;
    @JsonProperty("ClientDetails")
    private ClientDetails clientDetails;
    @JsonProperty("CompanyDetails")
    private CompanyDetails companyDetails;
    @JsonProperty("ExcessiveConfs")
    private List<Object> excessiveConfs = null;
    @JsonProperty("MeetingRooms")
    private List<MeetingRoom> meetingRooms = null;
    @JsonProperty("AudioConferenceCount")
    private Integer audioConferenceCount;
    @JsonProperty("HasGMWebinar")
    private Boolean hasGMWebinar;
    @JsonProperty("MeetingRoomsCount")
    private Integer meetingRoomsCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CorrelationId")
    public Object getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("CorrelationId")
    public void setCorrelationId(Object correlationId) {
        this.correlationId = correlationId;
    }

    @JsonProperty("Errors")
    public List<Object> getErrors() {
        return errors;
    }

    @JsonProperty("Errors")
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @JsonProperty("ExecutionTime")
    public Integer getExecutionTime() {
        return executionTime;
    }

    @JsonProperty("ExecutionTime")
    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    @JsonProperty("MessageId")
    public String getMessageId() {
        return messageId;
    }

    @JsonProperty("MessageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("ServerDateTime")
    public String getServerDateTime() {
        return serverDateTime;
    }

    @JsonProperty("ServerDateTime")
    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
    }

    @JsonProperty("AudioOnlyConferences")
    public List<Object> getAudioOnlyConferences() {
        return audioOnlyConferences;
    }

    @JsonProperty("AudioOnlyConferences")
    public void setAudioOnlyConferences(List<Object> audioOnlyConferences) {
        this.audioOnlyConferences = audioOnlyConferences;
    }

    @JsonProperty("ClientDetails")
    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    @JsonProperty("ClientDetails")
    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    @JsonProperty("CompanyDetails")
    public CompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    @JsonProperty("CompanyDetails")
    public void setCompanyDetails(CompanyDetails companyDetails) {
        this.companyDetails = companyDetails;
    }

    @JsonProperty("ExcessiveConfs")
    public List<Object> getExcessiveConfs() {
        return excessiveConfs;
    }

    @JsonProperty("ExcessiveConfs")
    public void setExcessiveConfs(List<Object> excessiveConfs) {
        this.excessiveConfs = excessiveConfs;
    }

    @JsonProperty("MeetingRooms")
    public List<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    @JsonProperty("MeetingRooms")
    public void setMeetingRooms(List<MeetingRoom> meetingRooms) {
        this.meetingRooms = meetingRooms;
    }

    @JsonProperty("AudioConferenceCount")
    public Integer getAudioConferenceCount() {
        return audioConferenceCount;
    }

    @JsonProperty("AudioConferenceCount")
    public void setAudioConferenceCount(Integer audioConferenceCount) {
        this.audioConferenceCount = audioConferenceCount;
    }

    @JsonProperty("HasGMWebinar")
    public Boolean getHasGMWebinar() {
        return hasGMWebinar;
    }

    @JsonProperty("HasGMWebinar")
    public void setHasGMWebinar(Boolean hasGMWebinar) {
        this.hasGMWebinar = hasGMWebinar;
    }

    @JsonProperty("MeetingRoomsCount")
    public Integer getMeetingRoomsCount() {
        return meetingRoomsCount;
    }

    @JsonProperty("MeetingRoomsCount")
    public void setMeetingRoomsCount(Integer meetingRoomsCount) {
        this.meetingRoomsCount = meetingRoomsCount;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
