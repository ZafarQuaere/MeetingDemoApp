
package com.pgi.network.models;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.application.CoreApplication;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Attended",
    "ClientId",
    "DeletedDate",
    "Description",
    "DesktopMeetingId",
    "Favorite",
    "HubConfId",
    "MeetingType",
    "MeetingUrl",
    "ModeratorSecurityCode",
    "ModifiedDate",
    "SecurityCode",
    "UserType",
    "FirstName",
    "LastName",
    "Furl",
    "BrandId",
    "ProfileImageUrl"
})
@Entity
public class SearchResult implements ImeetingRoomInfo, Comparable<SearchResult> {
    @Id(autoincrement = true)
    public Long id;
    @JsonProperty("Attended")
    private Integer attended;
    @JsonProperty("ClientId")
    private Integer clientId;
    @JsonProperty("DeletedDate")
    private String deletedDate;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("DesktopMeetingId")
    private Integer desktopMeetingId;
    @JsonProperty("Favorite")
    private Boolean favorite;
    @JsonProperty("HubConfId")
    private Integer hubConfId;
    @JsonProperty("MeetingType")
    private String meetingType;
    @JsonProperty("MeetingUrl")
    private String meetingUrl;
    @JsonProperty("ModeratorSecurityCode")
    private String moderatorSecurityCode;
    @JsonProperty("ModifiedDate")
    private String modifiedDate;
    @JsonProperty("SecurityCode")
    private String securityCode;
    @JsonProperty("UserType")
    private String userType;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("Furl")
    private String furl;
    @JsonProperty("ConferenceId")
    private int conferenceId;
    @JsonProperty("UseHtml5")
    private boolean UseHtml5;
    @JsonProperty("BrandId")
    private int brandId;
    @JsonProperty("ProfileImageUrl")
    private String profileImageUrl;
    private static final String TAG = SearchResult.class.getSimpleName();


    @Keep
    public SearchResult(Long id, Integer attended, Integer clientId, String deletedDate,
                        String description, Integer desktopMeetingId, Boolean favorite, Integer hubConfId,
                        String meetingType, String meetingUrl, String moderatorSecurityCode, String modifiedDate,
                        String securityCode, String userType, String firstName, String lastName, String furl,
                        int conferenceId, boolean UseHtml5, int brandId, String profileImageUrl) {
        this.id = id;
        this.attended = attended;
        this.clientId = clientId;
        this.deletedDate = deletedDate;
        this.description = description;
        this.desktopMeetingId = desktopMeetingId;
        this.favorite = favorite;
        this.hubConfId = hubConfId;
        this.meetingType = meetingType;
        this.meetingUrl = meetingUrl;
        this.moderatorSecurityCode = moderatorSecurityCode;
        this.modifiedDate = modifiedDate;
        this.securityCode = securityCode;
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.furl = furl;
        this.conferenceId = conferenceId;
        this.UseHtml5 = UseHtml5;
        this.brandId = brandId;
        this.profileImageUrl = profileImageUrl;
    }

    @Keep
    public SearchResult() {
    }

    @JsonProperty("Attended")
    public Integer getAttended() {
        return attended;
    }

    @JsonProperty("Attended")
    public void setAttended(Integer attended) {
        this.attended = attended;
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
    public String getDeletedDate() {
        return deletedDate;
    }

    @JsonProperty("DeletedDate")
    public void setDeletedDate(String deletedDate) {
        this.deletedDate = deletedDate;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("DesktopMeetingId")
    public Integer getDesktopMeetingId() {
        return desktopMeetingId;
    }

    @JsonProperty("DesktopMeetingId")
    public void setDesktopMeetingId(Integer desktopMeetingId) {
        this.desktopMeetingId = desktopMeetingId;
    }

    @JsonProperty("Favorite")
    public Boolean getFavorite() {
        return favorite;
    }

    @JsonProperty("Favorite")
    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @JsonProperty("HubConfId")
    public Integer getHubConfId() {
        return hubConfId;
    }

    @JsonProperty("HubConfId")
    public void setHubConfId(Integer hubConfId) {
        this.hubConfId = hubConfId;
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

    @JsonProperty("ModeratorSecurityCode")
    public String getModeratorSecurityCode() {
        return moderatorSecurityCode;
    }

    @JsonProperty("ModeratorSecurityCode")
    public void setModeratorSecurityCode(String moderatorSecurityCode) {
        this.moderatorSecurityCode = moderatorSecurityCode;
    }

    @JsonProperty("ModifiedDate")
    public String getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty("ModifiedDate")
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty("SecurityCode")
    public String getSecurityCode() {
        return securityCode;
    }

    @JsonProperty("SecurityCode")
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @JsonProperty("UserType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("UserType")
    public void setUserType(String userType) {
        this.userType = userType;
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

    @JsonProperty("Furl")
    public String getFurl() {
        return furl;
    }

    @JsonProperty("Furl")
    public void setFurl(String furl) {
        this.furl = furl;
    }

    @JsonProperty("ConferenceId")
    public int getConferenceId() {
        return conferenceId;
    }

    @JsonProperty("ConferenceId")
    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    @JsonProperty("BrandId")
    public int getBrandId() {
        return brandId;
    }

    @JsonProperty("BrandId")
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    @JsonProperty("ProfileImageUrl")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @JsonProperty("ProfileImageUrl")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getProfileImage() {
        return profileImageUrl;
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
        if (furl != null && furl.contains(AppConstants.SLASH_SYMBOL)) {
            int firstSlash = furl.indexOf(AppConstants.SLASH_SYMBOL);
            int secondSlash = furl.indexOf(AppConstants.SLASH_SYMBOL, firstSlash+1);
            furlEndPoint = furl.substring(secondSlash+1);
        }
        return furlEndPoint;
    }

    @Override
    public String getMeetingConferenceId() {
        return String.valueOf(getConferenceId());
    }

    @Override
    public String getMeetingRoomId() {
        return String.valueOf(hubConfId);
    }

    @Override
    public int compareTo(@NonNull SearchResult searchResult) {
        String date = searchResult.getModifiedDate();
        int result;
        try {
            result = date.compareTo(modifiedDate);
        } catch (Exception ex) {
            CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.JOIN, "SearchResult " +
                "compareTo() ", ex, null, true, false);
            result = 0;
        }
        return result;
    }

    @JsonProperty("UseHtml5")
    public boolean isUseHtml5() {
        return UseHtml5;
    }

    @JsonProperty("UseHtml5")
    public void setUseHtml5(boolean useHtml5) {
        UseHtml5 = useHtml5;
    }

    public boolean getUseHtml5() {
        return this.UseHtml5;
    }
}