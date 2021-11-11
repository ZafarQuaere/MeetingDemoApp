package com.pgi.convergencemeetings.models.elkmodels;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ashwanikumar on 21-02-2018.
 */
public class MeetingModel {
    @JsonProperty("furl")
    private String mFurl;
    @JsonProperty("hubconfid")
    private String mHubconfid;
    @JsonProperty("hubconfcdrid")
    private String mHubconfcdrid;
    @JsonProperty("server")
    private String mServer;
    @JsonProperty("ownerpgiclientid")
    private String mOwnerpgiclientid;

    private static MeetingModel mMeetingModel;

    private MeetingModel(){

    }

    public static MeetingModel getInstance(){
        if(mMeetingModel == null){
            mMeetingModel = new MeetingModel();
        }
        return mMeetingModel;
    }

    @JsonProperty("furl")
    public String getFurl() {
        return mFurl;
    }

    @JsonProperty("furl")
    public void setFurl(String furl) {
        this.mFurl = furl;
    }
    @JsonProperty("hubconfid")
    public String getHubconfid() {
        return mHubconfid;
    }
    @JsonProperty("hubconfid")
    public void setHubconfid(String hubconfid) {
        this.mHubconfid = hubconfid;
    }
    @JsonProperty("hubconfcdrid")
    public String getHubconfcdrid() {
        return mHubconfcdrid;
    }
    @JsonProperty("hubconfcdrid")
    public void setHubconfcdrid(String hubconfcdrid) {
        this.mHubconfcdrid = hubconfcdrid;
    }
    @JsonProperty("server")
    public String getServer() {
        return mServer;
    }
    @JsonProperty("server")
    public void setServer(String server) {
        this.mServer = server;
    }
    @JsonProperty("ownerpgiclientid")
    public String getOwnerpgiclientid() {
        return mOwnerpgiclientid;
    }
    @JsonProperty("ownerpgiclientid")
    public void setOwnerpgiclientid(String ownerpgiclientid) {
        this.mOwnerpgiclientid = ownerpgiclientid;
    }

}