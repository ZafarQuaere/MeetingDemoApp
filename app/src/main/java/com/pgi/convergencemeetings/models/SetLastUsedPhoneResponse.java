package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nnennaiheke on 11/14/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "RegisteredUserSetLastUsedPhoneResult"
})
public class SetLastUsedPhoneResponse {

    @JsonProperty("RegisteredUserSetLastUsedPhoneResult")
    private RegisteredUserSetLastUsedPhoneResult registeredUserSetLastUsedPhoneResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("RegisteredUserSetLastUsedPhoneResult")
    public RegisteredUserSetLastUsedPhoneResult getRegisteredUserSetLastUsedPhoneResult() {
        return registeredUserSetLastUsedPhoneResult;
    }

    @JsonProperty("RegisteredUserSetLastUsedPhoneResult")
    public void setRegisteredUserSetLastUsedPhoneResult(RegisteredUserSetLastUsedPhoneResult registeredUserSetLastUsedPhoneResult) {
        this.registeredUserSetLastUsedPhoneResult = registeredUserSetLastUsedPhoneResult;
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
