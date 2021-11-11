package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Address1",
    "Address2",
    "Address3",
    "City",
    "CountryCode",
    "PostalCode",
    "Province",
    "StateCode",
    "TimeZone"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Address {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty("Address1")
    private String address1;
    @JsonProperty("Address2")
    private String address2;
    @JsonProperty("Address3")
    private String address3;
    @JsonProperty("City")
    private String city;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("PostalCode")
    private String postalCode;
    @JsonProperty("Province")
    private String province;
    @JsonProperty("StateCode")
    private String stateCode;
    @JsonProperty("TimeZone")
    private String timeZone;

    @Generated(hash = 1250247962)
    public Address(Long id, String address1, String address2, String address3,
                   String city, String countryCode, String postalCode, String province,
                   String stateCode, String timeZone) {
        this.id = id;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.province = province;
        this.stateCode = stateCode;
        this.timeZone = timeZone;
    }

    @Generated(hash = 388317431)
    public Address() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("Address1")
    public String getAddress1() {
        return address1;
    }

    @JsonProperty("Address1")
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @JsonProperty("Address2")
    public String getAddress2() {
        return address2;
    }

    @JsonProperty("Address2")
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @JsonProperty("Address3")
    public String getAddress3() {
        return address3;
    }

    @JsonProperty("Address3")
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    @JsonProperty("City")
    public String getCity() {
        return city;
    }

    @JsonProperty("City")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("CountryCode")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("CountryCode")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("PostalCode")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("PostalCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("Province")
    public String getProvince() {
        return province;
    }

    @JsonProperty("Province")
    public void setProvince(String province) {
        this.province = province;
    }

    @JsonProperty("StateCode")
    public String getStateCode() {
        return stateCode;
    }

    @JsonProperty("StateCode")
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @JsonProperty("TimeZone")
    public String getTimeZone() {
        return timeZone;
    }

    @JsonProperty("TimeZone")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}