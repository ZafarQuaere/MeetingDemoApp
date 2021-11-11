package com.pgi.convergencemeetings.models.enterUrlModel;

/**
 * Created by nnennaiheke on 9/6/17.
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "CorrelationId",
        "Errors",
        "ExecutionTime",
        "MessageId",
        "ServerDateTime",
        "PageNumber",
        "TotalItems",
        "TotalPages",
        "results"
})
public class MeetingRoomSearchResult_ {

    @JsonProperty("CorrelationId")
    private Object correlationId;
    @JsonProperty("Errors")
    private List<Object> errors = null;
    @JsonProperty("ExecutionTime")
    private int executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;
    @JsonProperty("PageNumber")
    private int pageNumber;
    @JsonProperty("TotalItems")
    private int totalItems;
    @JsonProperty("TotalPages")
    private int totalPages;
    @JsonProperty("results")
    private List<Result> results = null;

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
    public int getExecutionTime() {
        return executionTime;
    }

    @JsonProperty("ExecutionTime")
    public void setExecutionTime(int executionTime) {
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

    @JsonProperty("PageNumber")
    public int getPageNumber() {
        return pageNumber;
    }

    @JsonProperty("PageNumber")
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonProperty("TotalItems")
    public int getTotalItems() {
        return totalItems;
    }

    @JsonProperty("TotalItems")
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @JsonProperty("TotalPages")
    public int getTotalPages() {
        return totalPages;
    }

    @JsonProperty("TotalPages")
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
    }

}