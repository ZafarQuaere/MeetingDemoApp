package com.pgi.convergencemeetings.models.getPhoneNumberModel;

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
 * Created by nnennaiheke on 11/13/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "CorrelationId",
        "Errors",
        "ExecutionTime",
        "MessageId",
        "ServerDateTime",
        "AttendeeId",
        "ClientId",
        "DeletedDate",
        "LastUpdated",
        "Phones",
        "RegisteredUser",
        "RegisteredUserId"
})
public class RegisteredUserGetResult {

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
    @JsonProperty("AttendeeId")
    private Integer attendeeId;
    @JsonProperty("ClientId")
    private Integer clientId;
    @JsonProperty("DeletedDate")
    private Object deletedDate;
    @JsonProperty("LastUpdated")
    private String lastUpdated;
    @JsonProperty("Phones")
    private List<Phone> phones = null;
    @JsonProperty("RegisteredUser")
    private RegisteredUser registeredUser;
    @JsonProperty("RegisteredUserId")
    private Object registeredUserId;
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

    @JsonProperty("AttendeeId")
    public Integer getAttendeeId() {
        return attendeeId;
    }

    @JsonProperty("AttendeeId")
    public void setAttendeeId(Integer attendeeId) {
        this.attendeeId = attendeeId;
    }

    @JsonProperty("ClientId")
    public Integer getClientId() {
        return clientId;
    }

    @JsonProperty("ClientId")
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("DeletedDate")
    public Object getDeletedDate() {
        return deletedDate;
    }

    @JsonProperty("DeletedDate")
    public void setDeletedDate(Object deletedDate) {
        this.deletedDate = deletedDate;
    }

    @JsonProperty("LastUpdated")
    public String getLastUpdated() {
        return lastUpdated;
    }

    @JsonProperty("LastUpdated")
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty("Phones")
    public List<Phone> getPhones() {
        return phones;
    }

    @JsonProperty("Phones")
    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    @JsonProperty("RegisteredUser")
    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    @JsonProperty("RegisteredUser")
    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    @JsonProperty("RegisteredUserId")
    public Object getRegisteredUserId() {
        return registeredUserId;
    }

    @JsonProperty("RegisteredUserId")
    public void setRegisteredUserId(Object registeredUserId) {
        this.registeredUserId = registeredUserId;
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
