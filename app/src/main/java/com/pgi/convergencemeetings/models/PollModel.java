
package com.pgi.convergencemeetings.models;

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
    "PgsErrorCode",
    "ResultCode",
    "ResultText",
    "PendingMessagesArray"
})
public class PollModel {

    @JsonProperty("PgsErrorCode")
    private Integer pgsErrorCode;
    @JsonProperty("ResultCode")
    private Integer resultCode;
    @JsonProperty("ResultText")
    private String resultText;
    @JsonProperty("PendingMessagesArray")
    private List<PendingMessagesArray> pendingMessagesArray = null;
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

    @JsonProperty("PendingMessagesArray")
    public List<PendingMessagesArray> getPendingMessagesArray() {
        return pendingMessagesArray;
    }

    @JsonProperty("PendingMessagesArray")
    public void setPendingMessagesArray(List<PendingMessagesArray> pendingMessagesArray) {
        this.pendingMessagesArray = pendingMessagesArray;
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
