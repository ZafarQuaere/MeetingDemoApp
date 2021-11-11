
package com.pgi.convergencemeetings.models;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.enums.pia.PIAPartType;
import com.pgi.convergence.enums.pia.PIAWebSocketEventType;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ANI",
        "CallerPIN",
        "Company",
        "ConfID",
        "Connected",
        "DNIS",
        "Email",
        "FirstName",
        "Hold",
        "LastName",
        "LineID",
        "ListenLevel",
        "ListenOnly",
        "Mute",
        "PartID",
        "PartType",
        "Phone",
        "SubConfID",
        "VoiceLevel",
        "MsgType"
})
public class MeetingParticipant implements Comparable{

    @JsonProperty("ANI")
    private String aNI;
    @JsonProperty("CallerPIN")
    private String callerPIN;
    @JsonProperty("Company")
    private String company;
    @JsonProperty("ConfID")
    private String confID;
    @JsonProperty("Connected")
    private Boolean connected;
    @JsonProperty("DNIS")
    private String dNIS;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("Hold")
    private Boolean hold;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("LineID")
    private String lineID;
    @JsonProperty("ListenLevel")
    private Integer listenLevel;
    @JsonProperty("ListenOnly")
    private Boolean listenOnly;
    @JsonProperty("Mute")
    private Boolean mute;
    @JsonProperty("PartID")
    private String partID;
    @JsonProperty("PartType")
    private Integer partType;
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("SubConfID")
    private String subConfID;
    @JsonProperty("VoiceLevel")
    private Integer voiceLevel;
    @JsonProperty("MsgType")
    private String msgType;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("MsgType")
    public String getMsgType() {
        return msgType;
    }

    @JsonProperty("MsgType")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @JsonProperty("ANI")
    public String getANI() {
        return aNI;
    }

    @JsonProperty("ANI")
    public void setANI(String aNI) {
        this.aNI = aNI;
    }

    @JsonProperty("CallerPIN")
    public String getCallerPIN() {
        return callerPIN;
    }

    @JsonProperty("CallerPIN")
    public void setCallerPIN(String callerPIN) {
        this.callerPIN = callerPIN;
    }

    @JsonProperty("Company")
    public String getCompany() {
        return company;
    }

    @JsonProperty("Company")
    public void setCompany(String company) {
        this.company = company;
    }

    @JsonProperty("ConfID")
    public String getConfID() {
        return confID;
    }

    @JsonProperty("ConfID")
    public void setConfID(String confID) {
        this.confID = confID;
    }

    @JsonProperty("Connected")
    public Boolean getConnected() {
        return connected;
    }

    @JsonProperty("Connected")
    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    @JsonProperty("DNIS")
    public String getDNIS() {
        return dNIS;
    }

    @JsonProperty("DNIS")
    public void setDNIS(String dNIS) {
        this.dNIS = dNIS;
    }

    @JsonProperty("Email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("Email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("FirstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("FirstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("Hold")
    public Boolean getHold() {
        return hold;
    }

    @JsonProperty("Hold")
    public void setHold(Boolean hold) {
        this.hold = hold;
    }

    @JsonProperty("LastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("LastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("LineID")
    public String getLineID() {
        return lineID;
    }

    @JsonProperty("LineID")
    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    @JsonProperty("ListenLevel")
    public Integer getListenLevel() {
        return listenLevel;
    }

    @JsonProperty("ListenLevel")
    public void setListenLevel(Integer listenLevel) {
        this.listenLevel = listenLevel;
    }

    @JsonProperty("ListenOnly")
    public Boolean getListenOnly() {
        return listenOnly;
    }

    @JsonProperty("ListenOnly")
    public void setListenOnly(Boolean listenOnly) {
        this.listenOnly = listenOnly;
    }

    @JsonProperty("Mute")
    public Boolean getMute() {
        return mute;
    }

    @JsonProperty("Mute")
    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    @JsonProperty("PartID")
    public String getPartID() {
        return partID;
    }

    @JsonProperty("PartID")
    public void setPartID(String partID) {
        this.partID = partID;
    }

    @JsonProperty("PartType")
    public Integer getPartType() {
        return partType;
    }

    @JsonProperty("PartType")
    public void setPartType(Integer partType) {
        this.partType = partType;
    }

    @JsonProperty("Phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("Phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("SubConfID")
    public String getSubConfID() {
        return subConfID;
    }

    @JsonProperty("SubConfID")
    public void setSubConfID(String subConfID) {
        this.subConfID = subConfID;
    }

    @JsonProperty("VoiceLevel")
    public Integer getVoiceLevel() {
        return voiceLevel;
    }

    @JsonProperty("VoiceLevel")
    public void setVoiceLevel(Integer voiceLevel) {
        this.voiceLevel = voiceLevel;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isParticipantNameAvailable(){
        return (firstName!= null && !firstName.equals(AppConstants.EMPTY_STRING));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() == obj.getClass()) {
            return partID.equals(((MeetingParticipant) obj).getPartID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (partID != null ? getPartID().hashCode() : 0);
    }

    @Override
    public int compareTo(@NonNull Object object) {
        int compare = 0;
        if(object != null && object instanceof MeetingParticipant){
            MeetingParticipant meetingParticipant = (MeetingParticipant) object;
            if(partType == PIAPartType.MODERATOR.getValue()){
                compare = -1;
            }else {
                compare = 1;
            }
        }
        return compare;
    }
}
