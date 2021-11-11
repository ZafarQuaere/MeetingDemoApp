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
    "Applied",
    "Attended",
    "Email",
    "Invited",
    "Moderator",
    "Registered"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Presenter {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String presentersId;

    @JsonProperty("Applied")
    private boolean applied;
    @JsonProperty("Attended")
    private boolean attended;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Invited")
    private boolean invited;
    @JsonProperty("Moderator")
    private boolean moderator;
    @JsonProperty("Registered")
    private boolean registered;

    @Generated(hash = 409150492)
    public Presenter(Long id, String presentersId, boolean applied, boolean attended,
                     String email, boolean invited, boolean moderator, boolean registered) {
        this.id = id;
        this.presentersId = presentersId;
        this.applied = applied;
        this.attended = attended;
        this.email = email;
        this.invited = invited;
        this.moderator = moderator;
        this.registered = registered;
    }

    @Generated(hash = 1526620793)
    public Presenter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPresentersId() {
        return presentersId;
    }

    public void setPresentersId(String presentersId) {
        this.presentersId = presentersId;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean getApplied() {
        return this.applied;
    }

    public boolean getAttended() {
        return this.attended;
    }

    public boolean getInvited() {
        return this.invited;
    }

    public boolean getModerator() {
        return this.moderator;
    }

    public boolean getRegistered() {
        return this.registered;
    }

}