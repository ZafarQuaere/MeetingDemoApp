package com.pgi.convergencemeetings.models;

/**
 * Created by ashwanikumar on 12/12/2017.
 */

public class DefaultRoom {
    private String mMeetingRoomId;
    private String mTitle;
    private String mSubTitle;
    private boolean mIsDefaultRoom;

    public DefaultRoom(String title, String subTitle, boolean isMultiLabel, String meetingRoomId) {
        this.mTitle = title;
        this.mIsDefaultRoom = isMultiLabel;
        this.mSubTitle = subTitle;
        this.mMeetingRoomId = meetingRoomId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public boolean isDefaultRoom() {
        return mIsDefaultRoom;
    }

    public void setDefaultRoom(boolean isDefaultRoom) {
        mIsDefaultRoom = isDefaultRoom;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String subTitle) {
        this.mSubTitle = subTitle;
    }

    public String getmMeetingRoomId() {
        return mMeetingRoomId;
    }

    public void setmMeetingRoomId(String mMeetingRoomId) {
        this.mMeetingRoomId = mMeetingRoomId;
    }
}
