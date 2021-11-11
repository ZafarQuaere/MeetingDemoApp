
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.network.models.ImeetingRoomInfo;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ClientId",
        "CompanyId",
        "EnterpriseId",
        "FirstName",
        "LastName",
        "Email",
        "ReservationId",
        "ReservationName",
        "MeetingRoomId",
        "MeetingRoomName",
        "MeetingRoomUrl",
        "HubId",
        "HubUrl",
        "ProfileImageUrl"
})
public class Meeting implements ImeetingRoomInfo {

    @JsonProperty("ClientId")
    private String clientId;
    @JsonProperty("CompanyId")
    private String companyId;
    @JsonProperty("EnterpriseId")
    private String enterpriseId;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("ReservationId")
    private String reservationId;
    @JsonProperty("ReservationName")
    private String reservationName;
    @JsonProperty("MeetingRoomId")
    private String meetingRoomId;
    @JsonProperty("MeetingRoomName")
    private String meetingRoomName;
    @JsonProperty("MeetingRoomUrl")
    private String meetingRoomUrl;
    @JsonProperty("HubId")
    private String hubId;
    @JsonProperty("HubUrl")
    private String hubUrl;

    @JsonProperty("Favorite")
    private boolean favorite;
    @JsonProperty("IncrementAttended")
    private boolean incrementAttended;
    @JsonProperty("MeetingType")
    private String meetingType;
    @JsonProperty("MeetingUrl")
    private String meetingUrl;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("UseHtml5")
    private boolean UseHtml5;
    @JsonProperty("ProfileImageUrl")
    private String profileImageUrl;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ClientId")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("ClientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("CompanyId")
    public String getCompanyId() {
        return companyId;
    }

    @JsonProperty("CompanyId")
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @JsonProperty("EnterpriseId")
    public String getEnterpriseId() {
        return enterpriseId;
    }

    @JsonProperty("EnterpriseId")
    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    @JsonProperty("FirstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("FirstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("LastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("LastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("Email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("Email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("ReservationId")
    public String getReservationId() {
        return reservationId;
    }

    @JsonProperty("ReservationId")
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    @JsonProperty("ReservationName")
    public String getReservationName() {
        return reservationName;
    }

    @JsonProperty("ReservationName")
    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }

    @JsonProperty("MeetingRoomId")
    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    @Override
    public Integer getDesktopMeetingId() {
        return meetingRoomId == null ? null : Integer.parseInt(meetingRoomId);
    }

    @JsonProperty("ProfileImageUrl")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @JsonProperty("ProfileImageUrl")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String getProfileImage() {
        return profileImageUrl;
    }

    @JsonProperty("MeetingRoomId")
    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    @JsonProperty("MeetingRoomName")
    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    @JsonProperty("MeetingRoomName")
    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    @JsonProperty("MeetingRoomUrl")
    public String getMeetingRoomUrl() {
        return meetingRoomUrl;
    }

    @JsonProperty("MeetingRoomUrl")
    public void setMeetingRoomUrl(String meetingRoomUrl) {
        this.meetingRoomUrl = meetingRoomUrl;
    }

    @JsonProperty("HubId")
    public String getHubId() {
        return hubId;
    }

    @JsonProperty("HubId")
    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    @JsonProperty("HubUrl")
    public String getHubUrl() {
        return hubUrl;
    }

    @JsonProperty("HubUrl")
    public void setHubUrl(String hubUrl) {
        this.hubUrl = hubUrl;
    }

    @JsonProperty("Favorite")
    public boolean isFavorite() {
        return favorite;
    }

    @JsonProperty("Favorite")
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @JsonProperty("IncrementAttended")
    public boolean isIncrementAttended() {
        return incrementAttended;
    }

    @JsonProperty("IncrementAttended")
    public void setIncrementAttended(boolean incrementAttended) {
        this.incrementAttended = incrementAttended;
    }

    @JsonProperty("MeetingType")
    public String getMeetingType() {
        return meetingType;
    }

    @JsonProperty("MeetingType")
    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    @JsonProperty("MeetingUrl")
    public String getMeetingUrl() {
        return meetingUrl;
    }

    @JsonProperty("MeetingUrl")
    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
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
    public String getHostFirstName() {
        return firstName;
    }

    @Override
    public String getMeetingHostName() {
        return CommonUtils.getFullName(firstName, lastName);
    }

    @Override
    public String getMeetingNameInitials() {
        return CommonUtils.getNameInitials(firstName, lastName);
    }

    @Override
    public String getMeetingRoomFurl() {
        String furlEndPoint = null;
        if (meetingRoomUrl != null && meetingRoomUrl.contains(AppConstants.SLASH_SYMBOL)) {
            int firstSlash = meetingRoomUrl.indexOf(AppConstants.SLASH_SYMBOL);
            int secondSlash = meetingRoomUrl.indexOf(AppConstants.SLASH_SYMBOL, firstSlash+1);
            furlEndPoint = meetingRoomUrl.substring(secondSlash+1);
        }
        return furlEndPoint;
    }

    @Override
    public String getFurl() {
        return meetingRoomUrl;
    }

    @Override
    public String getMeetingConferenceId() {
        return reservationId;
    }

    @JsonProperty("UseHtml5")
    public boolean isUseHtml5() {
        return UseHtml5;
    }

    @JsonProperty("UseHtml5")
    public void setUseHtml5(boolean useHtml5) {
        UseHtml5 = useHtml5;
    }
}
