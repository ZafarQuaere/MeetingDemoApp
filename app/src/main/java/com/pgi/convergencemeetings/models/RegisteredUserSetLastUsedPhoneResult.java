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

/**
 * Created by nnennaiheke on 11/14/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "CorrelationId",
        "Errors",
        "ExecutionTime",
        "MessageId",
        "ServerDateTime"
})
public class RegisteredUserSetLastUsedPhoneResult {

    @JsonProperty("CorrelationId")
    private Object correlationId;
    @JsonProperty("Errors")
    private List<Object> errors = null;
    @JsonProperty("ExecutionTime")
    private Integer executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CorrelationId")
    public Object getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("CorrelationId")
    public void setCorrelationId(Object correlationId) {
        this.correlationId = correlationId;
    }

    @JsonProperty("Errors")
    public List<Object> getErrors() {
        return errors;
    }

    @JsonProperty("Errors")
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @JsonProperty("ExecutionTime")
    public Integer getExecutionTime() {
        return executionTime;
    }

    @JsonProperty("ExecutionTime")
    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    @JsonProperty("MessageId")
    public String getMessageId() {
        return messageId;
    }

    @JsonProperty("MessageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("ServerDateTime")
    public String getServerDateTime() {
        return serverDateTime;
    }

    @JsonProperty("ServerDateTime")
    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
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
