package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

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
@Entity
public class Error {

    @Id(autoincrement = true)
    public Long id;
    public String errorsId;
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

    @Generated(hash = 1462214617)
    public Error(Long id, String errorsId, Integer code, String message,
                 String parameter, String parameterValue, Integer severity,
                 Integer source) {
        this.id = id;
        this.errorsId = errorsId;
        this.code = code;
        this.message = message;
        this.parameter = parameter;
        this.parameterValue = parameterValue;
        this.severity = severity;
        this.source = source;
    }

    @Generated(hash = 1198744959)
    public Error() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorsId() {
        return errorsId;
    }

    public void setErrorsId(String errorsId) {
        this.errorsId = errorsId;
    }

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