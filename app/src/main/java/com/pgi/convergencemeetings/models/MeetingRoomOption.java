package com.pgi.convergencemeetings.models;

/**
 * Created by nnennaiheke on 8/9/17.
 */


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Enabled",
    "Name"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingRoomOption {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;
    private String meetingRoomOptionsId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingRoomOptionsId() {
        return meetingRoomOptionsId;
    }

    public void setMeetingRoomOptionsId(String meetingRoomOptionsId) {
        this.meetingRoomOptionsId = meetingRoomOptionsId;
    }

    @JsonProperty("Enabled")
    private boolean enabled;
    @JsonProperty("Name")
    private String name;

    @Generated(hash = 961104168)
    public MeetingRoomOption(Long id, String meetingRoomOptionsId, boolean enabled,
                             String name) {
        this.id = id;
        this.meetingRoomOptionsId = meetingRoomOptionsId;
        this.enabled = enabled;
        this.name = name;
    }

    @Generated(hash = 1402495315)
    public MeetingRoomOption() {
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