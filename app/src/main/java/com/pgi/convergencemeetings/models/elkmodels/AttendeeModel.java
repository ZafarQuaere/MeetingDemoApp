package com.pgi.convergencemeetings.models.elkmodels;

import android.os.Build;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pgi.convergence.utils.CommonUtils;

/**
 * Created by ashwanikumar on 21-02-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "userid",
        "firstname",
        "lastname",
        "email",
        "remoteip",
        "operatingsystem",
        "osversion",
        "apptype",
        "device",
        "relaytype",
        "connectionprotocol",
        "audioconnectiontype",
        "dnis",
        "carrier",
        "audiocodec",
        "role",
        "participantid",
        "companyId",
        "callid",
})
public class AttendeeModel {
    String osVersion = Build.VERSION.RELEASE;
    String deviceModel = CommonUtils.getDeviceModelName();

    @JsonProperty("userid")
    private String mUserid;
    @JsonProperty("firstname")
    private String mFirstname;
    @JsonProperty("lastname")
    private String mLastname;
    @JsonProperty("email")
    private String mEmail;
    @JsonProperty("remoteip")
    private String mRemoteip;
    @JsonProperty("operatingsystem")
    private String mOperatingsystem = "Android";
    @JsonProperty("osversion")
    private String mOsVersion = osVersion;
    @JsonProperty("apptype")
    private String mAppType = "Android Client";
    @JsonProperty("device")
    private String mDevice = deviceModel;
    @JsonProperty("relaytype")
    private String mRelaytype;
    @JsonProperty("connectionprotocol")
    private String mConnectionprotocol;
    @JsonProperty("audioconnectiontype")
    private String mAudioconnectiontype;
    @JsonProperty("dnis")
    private String mDnis;
    @JsonProperty("carrier")
    private String mCarrier;
    @JsonProperty("audiocodec")
    private String mAudiocodec;
    @JsonProperty("role")
    private String mRole;
    @JsonProperty("particiantid")
    private String mParticipantid;
    @JsonProperty("companyId")
    private String mCompanyId;
    @JsonProperty("callId")
    private String mCallId;

    private static AttendeeModel mAttendeeModel;

    private AttendeeModel(){

    }

    public static AttendeeModel getInstance(){
        if(mAttendeeModel == null){
            mAttendeeModel = new AttendeeModel();
        }
        return mAttendeeModel;
    }

    @JsonProperty("userid")
    public String getUserid() {
        return mUserid;
    }

    @JsonProperty("userid")
    public void setUserid(String userid) {
        this.mUserid = userid;
    }
    @JsonProperty("firstname")
    public String getFirstname() {
        return mFirstname;
    }
    @JsonProperty("firstname")
    public void setFirstname(String firstname) {
        this.mFirstname = firstname;
    }
    @JsonProperty("lastname")
    public String getLastname() {
        return mLastname;
    }
    @JsonProperty("lastname")
    public void setLastname(String lastname) {
        this.mLastname = lastname;
    }
    @JsonProperty("email")
    public String getEmail() {
        return mEmail;
    }
    @JsonProperty("email")
    public void setEmail(String email) {
        this.mEmail = email;
    }
    @JsonProperty("remoteip")
    public String getRemoteip() {
        return mRemoteip;
    }
    @JsonProperty("remoteip")
    public void setRemoteip(String remoteip) {
        this.mRemoteip = remoteip;
    }
    @JsonProperty("operatingsystem")
    public String getOperatingsystem() {
        return mOperatingsystem;
    }
    @JsonProperty("operatingsystem")
    public void setOperatingsystem(String operatingsystem) {
        this.mOperatingsystem = operatingsystem;
    }
    @JsonProperty("osVersion")
    public String getOsVersion() {
        return mOsVersion;
    }
    @JsonProperty("osVersion")
    public void setOsVersion(String osVersion) {
        this.mOsVersion = osVersion;
    }
    @JsonProperty("appType")
    public String getAppType() {
        return mAppType;
    }
    @JsonProperty("appType")
    public void setAppType(String appType) {
        this.mAppType = appType;
    }
    @JsonProperty("device")
    public String getDevice() {
        return mDevice;
    }
    @JsonProperty("device")
    public void setDevice(String device) {
        this.mDevice = device;
    }
    @JsonProperty("relaytype")
    public String getRelaytype() {
        return mRelaytype;
    }
    @JsonProperty("relaytype")
    public void setRelaytype(String relaytype) {
        this.mRelaytype = relaytype;
    }
    @JsonProperty("connectionprotocol")
    public String getConnectionprotocol() {
        return mConnectionprotocol;
    }
    @JsonProperty("connectionprotocol")
    public void setConnectionprotocol(String connectionprotocol) {
        this.mConnectionprotocol = connectionprotocol;
    }
    @JsonProperty("audioconnectiontype")
    public String getAudioconnectiontype() {
        return mAudioconnectiontype;
    }
    @JsonProperty("audioconnectiontype")
    public void setAudioconnectiontype(String audioconnectiontype) {
        this.mAudioconnectiontype = audioconnectiontype;
    }
    @JsonProperty("userid")
    public String getDnis() {
        return mDnis;
    }
    @JsonProperty("dnis")
    public void setDnis(String dnis) {
        this.mDnis = dnis;
    }
    @JsonProperty("carrier")
    public String getCarrier() {
        return mCarrier;
    }
    @JsonProperty("carrier")
    public void setCarrier(String carrier) {
        this.mCarrier = carrier;
    }
    @JsonProperty("audiocodec")
    public String getAudiocodec() {
        return mAudiocodec;
    }
    @JsonProperty("audiocodec")
    public void setAudiocodec(String audiocodec) {
        this.mAudiocodec = audiocodec;
    }
    @JsonProperty("role")
    public String getRole() {
        return mRole;
    }
    @JsonProperty("role")
    public void setRole(String role) {
        this.mRole = role;
    }
    @JsonProperty("participantid")
    public String getParticipantid() {
        return mParticipantid;
    }
    @JsonProperty("participantid")
    public void setParticipantid(String participantId) {
        this.mParticipantid = participantId;
    }
    @JsonProperty("companyId")
    public String getCompanyId() {
        return mCompanyId;
    }
    @JsonProperty("companyId")
    public void setCompanyId(String companyId) {
        this.mCompanyId = companyId;
    }
    @JsonProperty("callId")
    public String getCallId() {return mCallId;}
    @JsonProperty("callId")
    public void setCallId(String callId) {this.mCallId = callId;}

    public void resetAttendeeInfo() {
        this.setUserid("");
        this.setFirstname("");
        this.setLastname("");
        this.setEmail("");
        this.setDnis("");
        this.setRole("");
        this.setAudioconnectiontype("");
    }
}
