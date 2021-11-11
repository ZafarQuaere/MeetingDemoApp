package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nnennaiheke on 8/3/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Code",
    "Enabled",
    "Locked"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class WebConferenceOption {

    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String webConferenceOptionsId;

    @JsonProperty("Code")
    private String code;
    @JsonProperty("Enabled")
    private boolean enabled;
    @JsonProperty("Locked")
    private boolean locked;

    @Generated(hash = 1421895731)
    public WebConferenceOption(Long id, String webConferenceOptionsId, String code,
                               boolean enabled, boolean locked) {
        this.id = id;
        this.webConferenceOptionsId = webConferenceOptionsId;
        this.code = code;
        this.enabled = enabled;
        this.locked = locked;
    }

    @Generated(hash = 1714806323)
    public WebConferenceOption() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebConferenceOptionsId() {
        return webConferenceOptionsId;
    }

    public void setWebConferenceOptionsId(String webConferenceOptionsId) {
        this.webConferenceOptionsId = webConferenceOptionsId;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty("Enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("Locked")
    public boolean isLocked() {
        return locked;
    }

    @JsonProperty("Locked")
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public boolean getLocked() {
        return this.locked;
    }

}