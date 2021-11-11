
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.enums.pia.PIAWebSocketEventType;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "MsgType",
        "ConfID"
})
public class WatchLost {

    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("ConfID")
    private String confID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ConfID")
    public String getConfID() {
        return confID;
    }

    @JsonProperty("ConfID")
    public void setConfID(String confID) {
        this.confID = confID;
    }

    @JsonProperty("MsgType")
    public String getMsgType() {
        return msgType;
    }

    @JsonProperty("MsgType")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
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
