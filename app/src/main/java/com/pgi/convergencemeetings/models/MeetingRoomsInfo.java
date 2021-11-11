
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
    "Meetings",
    "PageNumber",
    "PageSize",
    "TotalItems",
    "TotalPages"
})
public class MeetingRoomsInfo {

    @JsonProperty("Meetings")
    private List<Meeting> meetings = null;
    @JsonProperty("PageNumber")
    private Integer pageNumber;
    @JsonProperty("PageSize")
    private Integer pageSize;
    @JsonProperty("TotalItems")
    private Integer totalItems;
    @JsonProperty("TotalPages")
    private Integer totalPages;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Meetings")
    public List<Meeting> getMeetings() {
        return meetings;
    }

    @JsonProperty("Meetings")
    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    @JsonProperty("PageNumber")
    public Integer getPageNumber() {
        return pageNumber;
    }

    @JsonProperty("PageNumber")
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonProperty("PageSize")
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("PageSize")
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
