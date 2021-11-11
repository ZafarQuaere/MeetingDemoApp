package com.pgi.convergencemeetings.models.getPhoneNumberModel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nnennaiheke on 11/13/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "RegisteredUserGetResult"
})
public class PhoneNumberResult {

    @JsonProperty("RegisteredUserGetResult")
    private RegisteredUserGetResult registeredUserGetResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("RegisteredUserGetResult")
    public RegisteredUserGetResult getRegisteredUserGetResult() {
        return registeredUserGetResult;
    }

    @JsonProperty("RegisteredUserGetResult")
    public void setRegisteredUserGetResult(RegisteredUserGetResult registeredUserGetResult) {
        this.registeredUserGetResult = registeredUserGetResult;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
