package com.pgi.convergencemeetings.utils;

import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.models.PhoneInformation;

import java.util.List;

/**
 * Created by ashwanikumar on 11/10/2017.
 */

public abstract class ClientInfoDataSet {
    public abstract String getSipUriFromClientInfo();
    public abstract String getPasscode();
    public abstract String getConferenceId();
    public abstract String getPhoneNumber();
    public abstract boolean getSoftphoneEnabled();
    public abstract List<PhoneInformation> getPhoneNumbersList();
    public abstract boolean getPhoneNumberAvailable();
    public abstract String getClientId();
    public abstract boolean isUseHtml5();
    public abstract String getMeetingRoomId();
    public abstract String getPrimaryAccessNumber();
    public abstract String getFirstName();
    public abstract MeetingRoom getMeetingRoomData();
}
