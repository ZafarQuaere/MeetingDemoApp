package com.pgi.convergencemeetings.models.enterUrlModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.network.models.SearchResult;

/**
 * This is holding meeting's attribute and details
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "MeetingRoomName",
        "MeetingUrl",
        "ConferenceId",
        "UseHtml5",
        "ClientId"
})
public class Detail {

    @JsonProperty("MeetingRoomName")
    private String meetingRoomName;
    @JsonProperty("MeetingUrl")
    private String meetingUrl;
    @JsonProperty("ConferenceId")
    private int conferenceId;
    @JsonProperty("UseHtml5")
    private boolean useHtml5;
    @JsonProperty("ClientId")
    private String clientId;

    @JsonIgnore
    private SearchResult searchResult;

    /**
     * Gets meeting room name.
     *
     * @return the meeting room name
     */
    @JsonProperty("MeetingRoomName")
    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    /**
     * Sets meeting room name.
     *
     * @param meetingRoomName the meeting room name
     */
    @JsonProperty("MeetingRoomName")
    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    /**
     * Gets meeting url.
     *
     * @return the meeting url
     */
    @JsonProperty("MeetingUrl")
    public String getMeetingUrl() {
        return meetingUrl;
    }

    /**
     * Sets meeting url.
     *
     * @param meetingUrl the meeting url
     */
    @JsonProperty("MeetingUrl")
    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    /**
     * Gets use html 5.
     *
     * @return the use html 5
     */
    @JsonProperty("UseHtml5")
    public boolean getUseHtml5() {
        return useHtml5;
    }

    /**
     * Sets use html 5.
     *
     * @param useHtml5 the use html 5
     */
    @JsonProperty("UseHtml5")
    public void setUseHtml5(boolean useHtml5) {
        this.useHtml5 = useHtml5;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    @JsonProperty("ClientId")
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    @JsonProperty("ClientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets conference id.
     *
     * @return the conference id
     */
    @JsonProperty("ConferenceId")
    public int getConferenceId() {
        return conferenceId;
    }

    /**
     * Sets conference id.
     *
     * @param conferenceId the conference id
     */
    @JsonProperty("ConferenceId")
    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    @JsonIgnore
    public SearchResult getSearchResult() {
        return searchResult;
    }

    @JsonIgnore
    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
}