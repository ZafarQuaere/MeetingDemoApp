
package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "PgsErrorCode",
        "ResultCode",
        "ResultText",
        "ConfInfo",
        "ConfState",
        "ConferenceId",
        "ConferenceLinked",
        "ConferenceUnlinked",
        "MessageType",
        "PartInfo",
        "TalkingParticipants"
})
public class PendingMessagesArray {

    @JsonProperty("PgsErrorCode")
    private Integer pgsErrorCode;
    @JsonProperty("ResultCode")
    private Integer resultCode;
    @JsonProperty("ResultText")
    private String resultText;
    @JsonProperty("ConfInfo")
    private ConferenceInfo confInfo;
    @JsonProperty("ConfState")
    private ConfState confState;
    @JsonProperty("ConferenceId")
    private String conferenceId;
    @JsonProperty("ConferenceLinked")
    private ConferenceLinked conferenceLinked;
    @JsonProperty("ConferenceUnlinked")
    private ConferenceUnlinked conferenceUnlinked;
    @JsonProperty("MessageType")
    private Integer messageType;
    @JsonProperty("PartInfo")
    private MeetingParticipant partInfo;
    @JsonProperty("TalkingParticipants")
    private List<String> talkingParticipants = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("PgsErrorCode")
    public Integer getPgsErrorCode() {
        return pgsErrorCode;
    }

    @JsonProperty("PgsErrorCode")
    public void setPgsErrorCode(Integer pgsErrorCode) {
        this.pgsErrorCode = pgsErrorCode;
    }

    @JsonProperty("ResultCode")
    public Integer getResultCode() {
        return resultCode;
    }

    @JsonProperty("ResultCode")
    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty("ResultText")
    public String getResultText() {
        return resultText;
    }

    @JsonProperty("ResultText")
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @JsonProperty("ConfInfo")
    public ConferenceInfo getConfInfo() {
        return confInfo;
    }

    @JsonProperty("ConfInfo")
    public void setConfInfo(ConferenceInfo confInfo) {
        this.confInfo = confInfo;
    }

    @JsonProperty("ConfState")
    public ConfState getConfState() {
        return confState;
    }

    @JsonProperty("ConfState")
    public void setConfState(ConfState confState) {
        this.confState = confState;
    }

    @JsonProperty("ConferenceId")
    public String getConferenceId() {
        return conferenceId;
    }

    @JsonProperty("ConferenceId")
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    @JsonProperty("ConferenceLinked")
    public ConferenceLinked getConferenceLinked() {
        return conferenceLinked;
    }

    @JsonProperty("ConferenceLinked")
    public void setConferenceLinked(ConferenceLinked conferenceLinked) {
        this.conferenceLinked = conferenceLinked;
    }

    @JsonProperty("ConferenceUnlinked")
    public ConferenceUnlinked getConferenceUnlinked() {
        return conferenceUnlinked;
    }

    @JsonProperty("ConferenceUnlinked")
    public void setConferenceUnlinked(ConferenceUnlinked conferenceUnlinked) {
        this.conferenceUnlinked = conferenceUnlinked;
    }

    @JsonProperty("MessageType")
    public Integer getMessageType() {
        return messageType;
    }

    @JsonProperty("MessageType")
    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    @JsonProperty("PartInfo")
    public MeetingParticipant getPartInfo() {
        return partInfo;
    }

    @JsonProperty("PartInfo")
    public void setPartInfo(MeetingParticipant partInfo) {
        this.partInfo = partInfo;
    }

    @JsonProperty("TalkingParticipants")
    public List<String> getTalkingParticipants() {
        return talkingParticipants;
    }

    @JsonProperty("TalkingParticipants")
    public void setTalkingParticipants(List<String> talkingParticipants) {
        this.talkingParticipants = talkingParticipants;
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
