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
 * Created by visheshchandra on 12/22/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "MessageId",
        "DesktopMeetingId"
})
public class CreateMeetingResult {
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("DesktopMeetingId")
    private int desktopMeetingId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("MessageId")
    public String getMessageId() {
        return messageId;
    }

    @JsonProperty("MessageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("DesktopMeetingId")
    public int getDesktopMeetingId() {
        return desktopMeetingId;
    }

    @JsonProperty("DesktopMeetingId")
    public void setDesktopMeetingId(int desktopMeetingId) {
        this.desktopMeetingId = desktopMeetingId;
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
