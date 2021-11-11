
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pgi.convergence.enums.pia.PIAWebSocketCommand;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class PIAWebSocketCommandMessage {

    @JsonProperty("command")
    private PIAWebSocketCommand command;
    @JsonProperty("PIASID")
    private String PIASID;
    @JsonProperty("ID")
    private Integer ID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("command")
    public PIAWebSocketCommand getCommand() {
        return command;
    }

    @JsonProperty("command")
    public void setCommand(PIAWebSocketCommand command) {
        this.command = command;
    }

    @JsonProperty("PIASID")
    public String getPIASID() {
        return PIASID;
    }

    @JsonProperty("PIASID")
    public void setPIASID(String PIASID) {
        this.PIASID = PIASID;
    }

    @JsonProperty("ID")
    public Integer getID() {
        return ID;
    }

    @JsonProperty("ID")
    public void setID(Integer ID) {
        this.ID = ID;
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
