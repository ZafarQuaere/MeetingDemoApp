package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by visheshchandra on 12/21/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "desktopMeetingUpdateMessage"
})
public class UpdateMeetingRequest {
    @JsonProperty("desktopMeetingUpdateMessage")
    private Meeting meeting;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("desktopMeetingUpdateMessage")
    public Meeting getMeeting() {
        return meeting;
    }

    @JsonProperty("desktopMeetingUpdateMessage")
    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
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
