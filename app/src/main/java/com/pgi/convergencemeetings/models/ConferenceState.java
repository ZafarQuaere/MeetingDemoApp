
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.enums.pia.PIAWebSocketEventType;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Parts",
        "Active",
        "LPasscode",
        "Locked",
        "MPasscode",
        "MusicOn",
        "NumActiveParts",
        "PPasscode",
        "Recording",
        "RecordingStarted",
        "TalkerNotify",
        "RecFileName",
        "ConfLink",
        "ConfMute",
        "MsgType",
        "ConfID"
})
public class ConferenceState {

    @JsonProperty("Parts")
    private MeetingParticipant[] parts;
    @JsonProperty("Active")
    private Boolean active;
    @JsonProperty("LPasscode")
    private String lPasscode;
    @JsonProperty("Locked")
    private Boolean locked;
    @JsonProperty("MPasscode")
    private String mPasscode;
    @JsonProperty("MusicOn")
    private Boolean musicOn;
    @JsonProperty("NumActiveParts")
    private Integer numActiveParts;
    @JsonProperty("PPasscode")
    private String pPasscode;
    @JsonProperty("Recording")
    private Boolean recording;
    @JsonProperty("RecordingStarted")
    private Boolean recordingStarted;
    @JsonProperty("TalkerNotify")
    private Boolean talkerNotify;
    @JsonProperty("RecFileName")
    private String recFileName;
    @JsonProperty("ConfLink")
    private String confLink;
    @JsonProperty("ConfMute")
    private Boolean confMute;
    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("ConfID")
    private String confID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Parts")
    public MeetingParticipant[] getMeetingParticipants() {
        return parts;
    }

    @JsonProperty("Parts")
    public void setParts(MeetingParticipant[] parts) {
        this.parts = parts;
    }

    @JsonProperty("Active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("Active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("LPasscode")
    public String getLPasscode() {
        return lPasscode;
    }

    @JsonProperty("LPasscode")
    public void setLPasscode(String lPasscode) {
        this.lPasscode = lPasscode;
    }

    @JsonProperty("Locked")
    public Boolean getLocked() {
        return locked;
    }

    @JsonProperty("Locked")
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @JsonProperty("MPasscode")
    public String getMPasscode() {
        return mPasscode;
    }

    @JsonProperty("MPasscode")
    public void setMPasscode(String mPasscode) {
        this.mPasscode = mPasscode;
    }

    @JsonProperty("MusicOn")
    public Boolean getMusicOn() {
        return musicOn;
    }

    @JsonProperty("MusicOn")
    public void setMusicOn(Boolean musicOn) {
        this.musicOn = musicOn;
    }

    @JsonProperty("NumActiveParts")
    public Integer getNumActiveParts() {
        return numActiveParts;
    }

    @JsonProperty("NumActiveParts")
    public void setNumActiveParts(Integer numActiveParts) {
        this.numActiveParts = numActiveParts;
    }

    @JsonProperty("PPasscode")
    public String getPPasscode() {
        return pPasscode;
    }

    @JsonProperty("PPasscode")
    public void setPPasscode(String pPasscode) {
        this.pPasscode = pPasscode;
    }

    @JsonProperty("Recording")
    public Boolean getRecording() {
        return recording;
    }

    @JsonProperty("Recording")
    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    @JsonProperty("RecordingStarted")
    public Boolean getRecordingStarted() {
        return recordingStarted;
    }

    @JsonProperty("RecordingStarted")
    public void setRecordingStarted(Boolean recordingStarted) {
        this.recordingStarted = recordingStarted;
    }

    @JsonProperty("TalkerNotify")
    public Boolean getTalkerNotify() {
        return talkerNotify;
    }

    @JsonProperty("TalkerNotify")
    public void setTalkerNotify(Boolean talkerNotify) {
        this.talkerNotify = talkerNotify;
    }

    @JsonProperty("RecFileName")
    public String getRecFileName() {
        return recFileName;
    }

    @JsonProperty("RecFileName")
    public void setRecFileName(String recFileName) {
        this.recFileName = recFileName;
    }

    @JsonProperty("ConfLink")
    public String getConfLink() {
        return confLink;
    }

    @JsonProperty("ConfLink")
    public void setConfLink(String confLink) {
        this.confLink = confLink;
    }

    @JsonProperty("ConfMute")
    public Boolean getConfMute() {
        return confMute;
    }

    @JsonProperty("ConfMute")
    public void setConfMute(Boolean confMute) {
        this.confMute = confMute;
    }

    @JsonProperty("MsgType")
    public String getMsgType() {
        return msgType;
    }

    @JsonProperty("MsgType")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @JsonProperty("ConfID")
    public String getConfID() {
        return confID;
    }

    @JsonProperty("ConfID")
    public void setConfID(String confID) {
        this.confID = confID;
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
