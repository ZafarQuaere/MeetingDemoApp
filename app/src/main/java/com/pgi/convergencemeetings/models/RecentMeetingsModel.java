
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DesktopMeetingSearchResult"
})
public class RecentMeetingsModel {

    @JsonProperty("DesktopMeetingSearchResult")
    private DesktopMeetingSearchResult desktopMeetingSearchResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("DesktopMeetingSearchResult")
    public DesktopMeetingSearchResult getDesktopMeetingSearchResult() {
        return desktopMeetingSearchResult;
    }

    @JsonProperty("DesktopMeetingSearchResult")
    public void setDesktopMeetingSearchResult(DesktopMeetingSearchResult desktopMeetingSearchResult) {
        this.desktopMeetingSearchResult = desktopMeetingSearchResult;
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
