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
    "Enabled",
    "Name"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ConferenceOption {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String conferenceOptionsId;

    @JsonProperty("Enabled")
    private boolean enabled;
    @JsonProperty("Name")
    private String name;

    @Generated(hash = 1739846934)
    public ConferenceOption(Long id, String conferenceOptionsId, boolean enabled,
                            String name) {
        this.id = id;
        this.conferenceOptionsId = conferenceOptionsId;
        this.enabled = enabled;
        this.name = name;
    }

    @Generated(hash = 197022340)
    public ConferenceOption() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConferenceOptionsId() {
        return conferenceOptionsId;
    }

    public void setConferenceOptionsId(String conferenceOptionsId) {
        this.conferenceOptionsId = conferenceOptionsId;
    }

    @JsonProperty("Enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty("Enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

}
