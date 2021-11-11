
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
    "clientUpdateMessage"
})
public class ClientNameRequestModel {

    @JsonProperty("clientUpdateMessage")
    private ClientUpdateMessage clientUpdateMessage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("clientUpdateMessage")
    public ClientUpdateMessage getClientUpdateMessage() {
        return clientUpdateMessage;
    }

    @JsonProperty("clientUpdateMessage")
    public void setClientUpdateMessage(ClientUpdateMessage clientUpdateMessage) {
        this.clientUpdateMessage = clientUpdateMessage;
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
