
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
        "PgsErrorCode",
        "ResultCode",
        "ResultText",
        "ConfState"
})
public class ConferenceStateModel {

    @JsonProperty("PgsErrorCode")
    private Integer pgsErrorCode;
    @JsonProperty("ResultCode")
    private String resultCode;
    @JsonProperty("ResultText")
    private String resultText;
    @JsonProperty("ConfState")
    private ConfState confState;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("PgsErrorCode")
    public Integer getPgsErrorCode() {
        return pgsErrorCode;
    }

    @JsonProperty("PgsErrorCode")
    public void setPgsErrorCode(Integer pgsErrorCode) {
        this.pgsErrorCode = pgsErrorCode;
    }

    @JsonProperty("ResultCode")
    public String getResultCode() {
        return resultCode;
    }

    @JsonProperty("ResultCode")
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty("ResultText")
    public String getResultText() {
        return resultText;
    }

    @JsonProperty("ResultText")
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @JsonProperty("ConfState")
    public ConfState getConfState() {
        return confState;
    }

    @JsonProperty("ConfState")
    public void setConfState(ConfState confState) {
        this.confState = confState;
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
