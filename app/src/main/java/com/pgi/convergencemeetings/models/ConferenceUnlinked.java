
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
        "Result",
        "Route",
        "BridgeID",
        "ConfID",
        "ConfLink",
        "EndPoint",
        "EndTime",
        "LinkLineID",
        "LinkType",
        "MsgType",
        "StartTime"
})
public class ConferenceUnlinked {

    @JsonProperty("Result")
    private Result result;
    @JsonProperty("Route")
    private String route;
    @JsonProperty("BridgeID")
    private String bridgeID;
    @JsonProperty("ConfID")
    private String confID;
    @JsonProperty("ConfLink")
    private String confLink;
    @JsonProperty("EndPoint")
    private String endPoint;
    @JsonProperty("EndTime")
    private String endTime;
    @JsonProperty("LinkLineID")
    private String linkLineID;
    @JsonProperty("LinkType")
    private String linkType;
    @JsonProperty("StartTime")
    private String startTime;
    @JsonProperty("MsgType")
    private String msgType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("MsgType")
    public String getMsgType() {
        return msgType;
    }

    @JsonProperty("MsgType")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @JsonProperty("Result")
    public Result getResult() {
        return result;
    }

    @JsonProperty("Result")
    public void setResult(Result result) {
        this.result = result;
    }

    @JsonProperty("Route")
    public String getRoute() {
        return route;
    }

    @JsonProperty("Route")
    public void setRoute(String route) {
        this.route = route;
    }

    @JsonProperty("BridgeID")
    public String getBridgeID() {
        return bridgeID;
    }

    @JsonProperty("BridgeID")
    public void setBridgeID(String bridgeID) {
        this.bridgeID = bridgeID;
    }

    @JsonProperty("ConfID")
    public String getConfID() {
        return confID;
    }

    @JsonProperty("ConfID")
    public void setConfID(String confID) {
        this.confID = confID;
    }

    @JsonProperty("ConfLink")
    public String getConfLink() {
        return confLink;
    }

    @JsonProperty("ConfLink")
    public void setConfLink(String confLink) {
        this.confLink = confLink;
    }

    @JsonProperty("EndPoint")
    public String getEndPoint() {
        return endPoint;
    }

    @JsonProperty("EndPoint")
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    @JsonProperty("EndTime")
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty("EndTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("LinkLineID")
    public String getLinkLineID() {
        return linkLineID;
    }

    @JsonProperty("LinkLineID")
    public void setLinkLineID(String linkLineID) {
        this.linkLineID = linkLineID;
    }

    @JsonProperty("LinkType")
    public String getLinkType() {
        return linkType;
    }

    @JsonProperty("LinkType")
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    @JsonProperty("StartTime")
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty("StartTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
