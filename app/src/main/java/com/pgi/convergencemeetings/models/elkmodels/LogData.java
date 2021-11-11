
package com.pgi.convergencemeetings.models.elkmodels;

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
    "clientId",
    "applicationId",
    "applicationType",
    "os",
    "osVersion",
    "deviceType",
    "deviceModel",
		"deviceId",
    "webConfId",
    "audioConfId",
    "logItems",
})
public class LogData {
    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("applicationId")
    private String applicationId;
    @JsonProperty("applicationType")
    private String applicationType;
    @JsonProperty("os")
    private String os;
    @JsonProperty("osVersion")
    private String osVersion;
    @JsonProperty("deviceType")
    private String deviceType;
    @JsonProperty("deviceModel")
    private String deviceModel;

  @JsonProperty("deviceId")
    private String deviceId;
    @JsonProperty("webConfId")
    private String webConfId;
    @JsonProperty("audioConfId")
    private String audioConfId;
    @JsonProperty("logItems")
    private List<String> logItems = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("clientId")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("clientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("applicationId")
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty("applicationId")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @JsonProperty("applicationType")
    public String getApplicationType() {
        return applicationType;
    }

    @JsonProperty("applicationType")
    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    @JsonProperty("os")
    public String getOs() {
        return os;
    }

    @JsonProperty("os")
    public void setOs(String os) {
        this.os = os;
    }

    @JsonProperty("osVersion")
    public String getOsVersion() {
        return osVersion;
    }

    @JsonProperty("osVersion")
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @JsonProperty("deviceType")
    public String getDeviceType() {
        return deviceType;
    }

    @JsonProperty("deviceType")
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @JsonProperty("deviceModel")
    public String getDeviceModel() {
        return deviceModel;
    }

    @JsonProperty("deviceModel")
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @JsonProperty("deviceId")
    public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
    }
    @JsonProperty("webConfId")
    public String getWebConfId() {
        return webConfId;
    }

    @JsonProperty("webConfId")
    public void setWebConfId(String webConfId) {
        this.webConfId = webConfId;
    }

    @JsonProperty("audioConfId")
    public String getAudioConfId() {
        return audioConfId;
    }

    @JsonProperty("audioConfId")
    public void setAudioConfId(String audioConfId) {
        this.audioConfId = audioConfId;
    }

    @JsonProperty("logItems")
    public List<String> getLogItems() {
        return logItems;
    }

    @JsonProperty("logItems")
    public void setLogItems(List<String> logItems) {
        this.logItems = logItems;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("clientId: ").append(clientId).append("applicationId: ").append(applicationId).append("applicationType: ").append(applicationType).append("os: ").append(os).append("osVersion: ").append(osVersion).append("deviceType: ").append(deviceType).append("deviceModel: ").append(deviceModel).append("deviceId: ").append(deviceId).append("webConfId: ").append(webConfId).append("audioConfId: ").append(audioConfId).append("logItems: ").append(logItems).append("additionalProperties: ").append(additionalProperties).toString();
    }

}
