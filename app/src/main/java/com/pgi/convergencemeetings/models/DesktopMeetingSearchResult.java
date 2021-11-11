
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.network.models.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    "ScheduledInviteResults",
    "SearchResults"
})
public class DesktopMeetingSearchResult {

    @JsonProperty("CorrelationId")
    private String correlationId;
    @JsonProperty("Errors")
    private List<RecentMeetingError> errors = null;
    @JsonProperty("ExecutionTime")
    private Integer executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;
    @JsonProperty("PageNumber")
    private Integer pageNumber;
    @JsonProperty("TotalItems")
    private Integer totalItems;
    @JsonProperty("TotalPages")
    private Integer totalPages;
    @JsonProperty("ScheduledInviteResults")
    private List<Object> scheduledInviteResults = null;
    @JsonProperty("SearchResults")
    private List<SearchResult> searchResults = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CorrelationId")
    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("CorrelationId")
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @JsonProperty("Errors")
    public List<RecentMeetingError> getErrors() {
        return errors;
    }

    @JsonProperty("Errors")
    public void setErrors(List<RecentMeetingError> errors) {
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

    @JsonProperty("PageNumber")
    public Integer getPageNumber() {
        return pageNumber;
    }

    @JsonProperty("PageNumber")
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonProperty("TotalItems")
    public Integer getTotalItems() {
        return totalItems;
    }

    @JsonProperty("TotalItems")
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    @JsonProperty("TotalPages")
    public Integer getTotalPages() {
        return totalPages;
    }

    @JsonProperty("TotalPages")
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @JsonProperty("ScheduledInviteResults")
    public List<Object> getScheduledInviteResults() {
        return scheduledInviteResults;
    }

    @JsonProperty("ScheduledInviteResults")
    public void setScheduledInviteResults(List<Object> scheduledInviteResults) {
        this.scheduledInviteResults = scheduledInviteResults;
    }

    @JsonProperty("SearchResults")
    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    @JsonProperty("SearchResults")
    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
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
