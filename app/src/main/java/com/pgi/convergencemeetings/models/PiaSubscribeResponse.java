package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by nnennaiheke on 9/13/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SessionId",
        "ResultCode",
        "PgsErrorCode",
})
public class PiaSubscribeResponse {

    @JsonProperty("SessionId")
    String sessionid;
    @JsonProperty("ResultCode")
    int resultCode;
    @JsonProperty("PgsErrorCode")
    int pgsErrorCode;

    @JsonProperty("SessionId")
    public String getSessionid() {
        return sessionid;
    }

    @JsonProperty("SessionId")
    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    @JsonProperty("ResultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("ResultCode")
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty("PgsErrorCode")
    public int getPgsErrorCode() {
        return pgsErrorCode;
    }

    @JsonProperty("PgsErrorCode")
    public void setPgsErrorCode(int pgsErrorCode) {
        this.pgsErrorCode = pgsErrorCode;
    }
}
