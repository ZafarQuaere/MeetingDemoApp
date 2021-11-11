package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class UUID {
    @JsonProperty("value")
    private String value;

    @Generated(hash = 1300283622)
    public UUID(String value) {
        this.value = value;
    }

    @Generated(hash = 1707891937)
    public UUID() {
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}