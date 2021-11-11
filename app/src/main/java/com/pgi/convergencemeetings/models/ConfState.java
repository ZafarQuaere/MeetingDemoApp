
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
    "ConferenceInfo",
    "Parts"
})
public class ConfState {

    @JsonProperty("ConferenceInfo")
    private ConferenceInfo conferenceInfo;
    @JsonProperty("Parts")
    private List<MeetingParticipant> meetingParticipants = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ConferenceInfo")
    public ConferenceInfo getConferenceInfo() {
        return conferenceInfo;
    }

    @JsonProperty("ConferenceInfo")
    public void setConferenceInfo(ConferenceInfo conferenceInfo) {
        this.conferenceInfo = conferenceInfo;
    }

    @JsonProperty("Parts")
    public List<MeetingParticipant> getMeetingParticipants() {
        return meetingParticipants;
    }

    @JsonProperty("Parts")
    public void setMeetingParticipants(List<MeetingParticipant> meetingParticipants) {
        this.meetingParticipants = meetingParticipants;
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
