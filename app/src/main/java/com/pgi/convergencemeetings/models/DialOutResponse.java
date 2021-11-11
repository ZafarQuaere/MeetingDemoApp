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
 * Created by nnennaiheke on 11/14/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ParticipantId",
        "ResultCode",
        "ResultText",
        "PgsErrorCode"
})
public class DialOutResponse {

    @JsonProperty("ParticipantId")
    private String participantId;
    @JsonProperty("ResultCode")
    private Integer resultCode;
    @JsonProperty("ResultText")
    private String resultText;
    @JsonProperty("PgsErrorCode")
    private Integer pgsErrorCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ParticipantId")
    public String getParticipantId() {
        return participantId;
    }

    @JsonProperty("ParticipantId")
    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    @JsonProperty("ResultCode")
    public Integer getResultCode() {
        return resultCode;
    }

    @JsonProperty("ResultCode")
    public void setResultCode(Integer resultCode) {
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

    @JsonProperty("PgsErrorCode")
    public Integer getPgsErrorCode() {
        return pgsErrorCode;
    }

    @JsonProperty("PgsErrorCode")
    public void setPgsErrorCode(Integer pgsErrorCode) {
        this.pgsErrorCode = pgsErrorCode;
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
