
package com.pgi.convergencemeetings.models.elkmodels;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergencemeetings.models.CustomFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "logLevel",
    "loggerName",
    "timestamp",
    "transactionId",
    "subTransactionId",
    "message",
    "exceptionType",
    "exceptionMessage",
    "exceptionTrace",
    "customFields",
    "tags"
})
public class LogItem {

    @JsonProperty("logLevel")
    private String logLevel;
    @JsonProperty("loggerName")
    private String loggerName;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("subTransactionId")
    private String subTransactionId;
    @JsonProperty("message")
    private String message;
    @JsonProperty("exceptionType")
    private String exceptionType;
    @JsonProperty("exceptionMessage")
    private String exceptionMessage;
    @JsonProperty("exceptionTrace")
    private String exceptionTrace;
    @JsonProperty("customFields")
    private CustomFields customFields;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("logLevel")
    public String getLogLevel() {
        return logLevel;
    }

    @JsonProperty("logLevel")
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @JsonProperty("loggerName")
    public String getLoggerName() {
        return loggerName;
    }

    @JsonProperty("loggerName")
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("subTransactionId")
    public String getSubTransactionId() {
        return subTransactionId;
    }

    @JsonProperty("subTransactionId")
    public void setSubTransactionId(String subTransactionId) {
        this.subTransactionId = subTransactionId;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("exceptionType")
    public String getExceptionType() {
        return exceptionType;
    }

    @JsonProperty("exceptionType")
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    @JsonProperty("exceptionMessage")
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @JsonProperty("exceptionMessage")
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @JsonProperty("exceptionTrace")
    public String getExceptionTrace() {
        return exceptionTrace;
    }

    @JsonProperty("exceptionTrace")
    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }

    @JsonProperty("customFields")
    public CustomFields getCustomFields() {
        return customFields;
    }

    @JsonProperty("customFields")
    public void setCustomFields(CustomFields customFields) {
        this.customFields = customFields;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
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
        return new StringBuilder().append("logLevel: ").append(logLevel).append("loggerName: ").append(loggerName).append("timestamp: ").append(timestamp).append("transactionId: ").append(transactionId).append("subTransactionId: ").append(subTransactionId).append("message: ").append(message).append("exceptionType: ").append(exceptionType).append("exceptionMessage: ").append(exceptionMessage).append("exceptionTrace: ").append(exceptionTrace).append("customFields: ").append(customFields).append("tags: ").append(tags).append("additionalProperties: ").append(additionalProperties).toString();
    }

}
