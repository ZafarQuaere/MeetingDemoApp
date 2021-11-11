package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by ashwanikumar on 9/7/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Code",
        "Message",
        "Parameter",
        "ParameterValue",
        "Severity",
        "Source"
})
public class RecentMeetingError {

    @JsonProperty("Code")
    private Integer code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Parameter")
    private String parameter;
    @JsonProperty("ParameterValue")
    private String parameterValue;
    @JsonProperty("Severity")
    private Integer severity;
    @JsonProperty("Source")
    private Integer source;

    @JsonProperty("Code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(Integer code) {
        this.code = code;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("Message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("Parameter")
    public String getParameter() {
        return parameter;
    }

    @JsonProperty("Parameter")
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @JsonProperty("ParameterValue")
    public String getParameterValue() {
        return parameterValue;
    }

    @JsonProperty("ParameterValue")
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @JsonProperty("Severity")
    public Integer getSeverity() {
        return severity;
    }

    @JsonProperty("Severity")
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    @JsonProperty("Source")
    public Integer getSource() {
        return source;
    }

    @JsonProperty("Source")
    public void setSource(Integer source) {
        this.source = source;
    }

}
