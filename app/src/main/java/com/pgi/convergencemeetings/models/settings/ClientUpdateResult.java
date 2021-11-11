
package com.pgi.convergencemeetings.models.settings;

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
    "ClientUpdateResult"
})
public class ClientUpdateResult {

    @JsonProperty("ClientUpdateResult")
    private ClientUpdateResult_ clientUpdateResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ClientUpdateResult")
    public ClientUpdateResult_ getClientUpdateResult() {
        return clientUpdateResult;
    }

    @JsonProperty("ClientUpdateResult")
    public void setClientUpdateResult(ClientUpdateResult_ clientUpdateResult) {
        this.clientUpdateResult = clientUpdateResult;
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
