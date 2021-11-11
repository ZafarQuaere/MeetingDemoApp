package com.pgi.convergencemeetings.models.getPhoneNumberModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nnennaiheke on 11/13/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CountryCode",
    "Extension",
    "LastUsed",
    "Number",
    "PhoneLabel"
})
@Entity
public class Phone {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("Extension")
    private String extension;
    @JsonProperty("LastUsed")
    private Boolean lastUsed;
    @JsonProperty("Number")
    private String number;
    @JsonProperty("PhoneLabel")
    private String phoneLabel;

    @Generated(hash = 1179595525)
    public Phone(Long id, String countryCode, String extension, Boolean lastUsed,
                 String number, String phoneLabel) {
        this.id = id;
        this.countryCode = countryCode;
        this.extension = extension;
        this.lastUsed = lastUsed;
        this.number = number;
        this.phoneLabel = phoneLabel;
    }

    @Generated(hash = 429398894)
    public Phone() {
    }

    @JsonProperty("CountryCode")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("CountryCode")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("Extension")
    public String getExtension() {
        return extension;
    }

    @JsonProperty("Extension")
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @JsonProperty("LastUsed")
    public Boolean getLastUsed() {
        return lastUsed;
    }

    @JsonProperty("LastUsed")
    public void setLastUsed(Boolean lastUsed) {
        this.lastUsed = lastUsed;
    }

    @JsonProperty("Number")
    public String getNumber() {
        return number;
    }

    @JsonProperty("Number")
    public void setNumber(String number) {
        this.number = number;
    }

    @JsonProperty("PhoneLabel")
    public String getPhoneLabel() {
        return phoneLabel;
    }

    @JsonProperty("PhoneLabel")
    public void setPhoneLabel(String phoneLabel) {
        this.phoneLabel = phoneLabel;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}