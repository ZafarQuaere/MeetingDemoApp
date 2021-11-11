package com.pgi.network.models;

/**
 * Created by amit1829 on 10/6/2017.
 */

public interface ImeetingRoomInfo {

    String getHostFirstName();
    String getMeetingHostName();
    String getMeetingNameInitials();
    String getMeetingRoomFurl();
    String getMeetingConferenceId();
    String getMeetingRoomId();
    Integer getDesktopMeetingId();
    String getProfileImage();
    String getFurl();
    boolean isUseHtml5();
}
