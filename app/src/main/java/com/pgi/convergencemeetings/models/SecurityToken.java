package com.pgi.convergencemeetings.models;

/**
 * Created by amit1829 on 9/7/2017.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "aud",
        "sub",
        "email_verified",
        "iss",
        "name",
        "exp",
        "given_name",
        "iat",
        "family_name",
        "email",
        "pgi_user_type",
        "avatar_url_100x100",
        "pgi_id_app_info"
})
public class SecurityToken {

    @JsonProperty("aud")
    private String aud;
    @JsonProperty("sub")
    private String sub;
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    @JsonProperty("iss")
    private String iss;
    @JsonProperty("name")
    private String name;
    @JsonProperty("exp")
    private Integer exp;
    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("iat")
    private Integer iat;
    @JsonProperty("family_name")
    private String familyName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("pgi_user_type")
    private String pgiUserType;
    @JsonProperty("avatar_url_100x100")
    private String profileImageUrl;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("aud")
    public String getAud() {
        return aud;
    }

    @JsonProperty("aud")
    public void setAud(String aud) {
        this.aud = aud;
    }

    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }

    @JsonProperty("sub")
    public void setSub(String sub) {
        this.sub = sub;
    }

    @JsonProperty("email_verified")
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    @JsonProperty("email_verified")
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @JsonProperty("iss")
    public String getIss() {
        return iss;
    }

    @JsonProperty("iss")
    public void setIss(String iss) {
        this.iss = iss;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("exp")
    public Integer getExp() {
        return exp;
    }

    @JsonProperty("exp")
    public void setExp(Integer exp) {
        this.exp = exp;
    }

    @JsonProperty("given_name")
    public String getGivenName() {
        return givenName;
    }

    @JsonProperty("given_name")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty("iat")
    public Integer getIat() {
        return iat;
    }

    @JsonProperty("iat")
    public void setIat(Integer iat) {
        this.iat = iat;
    }

    @JsonProperty("family_name")
    public String getFamilyName() {
        return familyName;
    }

    @JsonProperty("family_name")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("pgi_user_type")
    public String getPgiUserType() {
        return pgiUserType;
    }

    @JsonProperty("pgi_user_type")
    public void setPgiUserType(String pgiUserType) {
        this.pgiUserType = pgiUserType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonProperty("avatar_url_100x100")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    @JsonProperty("avatar_url_100x100")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}