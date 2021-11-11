package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by nnennaiheke on 6/6/18.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "sub",
        "email",
        "email_verified",
        "name",
        "given_name",
        "family_name",
        "avatar_url_350x350",
        "avatar_url_100x100",
        "attributes",
        "user_type"

})
public class UserInfoIdentityModel {

    @JsonProperty("sub")
    private String sub;
    @JsonProperty("email")
    private String email;
    @JsonProperty("email_verified")
    private String email_verified;
    @JsonProperty("name")
    private String name;
    @JsonProperty("given_name")
    private String given_name;
    @JsonProperty("family_name")
    private String family_name;
    @JsonProperty("avatar_url_350x350")
    private String avatar_url_350x350;
    @JsonProperty("avatar_url_100x100")
    private String avatar_url_100x100;
    @JsonProperty("attributes")
    private String[] attributes;
    @JsonProperty("user_type")
    private String user_type;
    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }
    @JsonProperty("sub")
    public void setSub(String sub) {
        this.sub = sub;
    }
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }
    @JsonProperty("email_verified")
    public String getEmail_verified() {
        return email_verified;
    }
    @JsonProperty("email_verified")
    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("given_name")
    public String getGiven_name() {
        return given_name;
    }
    @JsonProperty("given_name")
    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }
    @JsonProperty("family_name")
    public String getFamily_name() {
        return family_name;
    }
    @JsonProperty("family_name")
    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }
    @JsonProperty("avatar_url_350x350")
    public String getAvatar_url_350x350() {
        return avatar_url_350x350;
    }
    @JsonProperty("avatar_url_350x350")
    public void setAvatar_url_350x350(String avatar_url_350x350) {
        this.avatar_url_350x350 = avatar_url_350x350;
    }
    @JsonProperty("avatar_url_100x100")
    public String getAvatar_url_100x100() {
        return avatar_url_100x100;
    }
    @JsonProperty("avatar_url_100x100")
    public void setAvatar_url_100x100(String avatar_url_100x100) {
        this.avatar_url_100x100 = avatar_url_100x100;
    }
    @JsonProperty("attributes")
    public String[] getAttributes() {
        return attributes;
    }
    @JsonProperty("attributes")
    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }
    @JsonProperty("user_type")
    public String getUser_type() {
        return user_type;
    }
    @JsonProperty("user_type")
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
