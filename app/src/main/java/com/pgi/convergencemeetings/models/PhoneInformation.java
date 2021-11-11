package com.pgi.convergencemeetings.models;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.DialInNumbers;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Location",
    "PhoneNumber",
    "PhoneType",
    "CustomLocation"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class PhoneInformation implements Comparable<PhoneInformation>, DialInNumbers {

    @Id(autoincrement = true)
    private Long id;

    private String phoneInformationId;

    @JsonProperty("Location")
    private String location;
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    @JsonProperty("PhoneType")
    private String phoneType;
    @JsonProperty("CustomLocation")
    private String customLocation;

    @Generated(hash = 1867438476)
    public PhoneInformation(Long id, String phoneInformationId, String location, String phoneNumber,
                            String phoneType, String customLocation) {
        this.id = id;
        this.phoneInformationId = phoneInformationId;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.customLocation = customLocation;
    }

    @Generated(hash = 1298363857)
    public PhoneInformation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneInformationId() {
        return phoneInformationId;
    }

    public void setPhoneInformationId(String phoneInformationId) {
        this.phoneInformationId = phoneInformationId;
    }

    @JsonProperty("Location")
    @Override
    public String getLocation() {
        return location;
    }

    @JsonProperty("Location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("PhoneNumber")
    @Override
    public String getPhoneNumber() {
        return phoneNumber.replaceAll(AppConstants.REGEX_PARENTHESES, AppConstants.EMPTY_STRING);
    }

    @JsonProperty("PhoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("PhoneType")
    @Override
    public String getPhoneType() {
        return phoneType;
    }

    @JsonProperty("PhoneType")
    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @JsonProperty("CustomLocation")
    public String getCustomLocation() {
        return customLocation;
    }

    @JsonProperty("CustomLocation")
    public void setCustomLocation(String customLocation) {
        this.customLocation = customLocation;
    }

    @Override
    public int compareTo(@NonNull PhoneInformation phoneInformation) {
        if(location != null && location.equalsIgnoreCase(AppConstants.PRIMARY_ACCESS_NUM)){
            return -1;
        }else {
            return 1;
        }
    }
}