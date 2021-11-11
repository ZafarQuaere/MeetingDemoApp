package com.pgi.convergencemeetings.utils;

import com.pgi.convergence.enums.ConnectionState;
import com.pgi.convergence.enums.ConnectionType;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;

/**
 * Created by ashwanikumar on 11/2/2017.
 */

public class ConferenceManager {
    private static ConnectionType mConnectionType = ConnectionType.NO_AUDIO;
    private static ConnectionState mConnectionState = ConnectionState.DISCONNECTED;
    private static MeetingRoomPresenter mMeetingRoomHook;
    private static boolean mIsMeetingActive;
    private static boolean mIsMeetingHost;
    private static String mClientId;
    private static String mConferenceId;
    private static boolean mInitiatedFromURI;

    public static void updateAudioConnectionState(ConnectionType connectionType, ConnectionState connectionState) {
        mConnectionType = connectionType;
        mConnectionState = connectionState;
    }

    public static ConnectionType getConnectionType() {
        return mConnectionType;
    }

    public static ConnectionState getConnectionState() {
        return mConnectionState;
    }

    public static void setMeetingHook(MeetingRoomPresenter meetingRoomPresenter) {
        mMeetingRoomHook = meetingRoomPresenter;
    }

    public static MeetingRoomPresenter getMeetingHook() {
        return mMeetingRoomHook;
    }

    public static void disconnectVoipConnection() {
        if (mMeetingRoomHook != null) {
            mMeetingRoomHook.disconnectVoipAudio();
        }
    }

    public static void setIsMeetinActive(boolean isMeetingActive) {
        mIsMeetingActive = isMeetingActive;
    }

    public static boolean isMeetingActive() {
        return mIsMeetingActive;
    }

    public static void resetConnectionState(boolean resetConferenceInfo) {
        if (resetConferenceInfo) {
            mConnectionType = ConnectionType.NO_AUDIO;
            mConnectionState = ConnectionState.DISCONNECTED;
            mIsMeetingActive = false;
            resetConferenceInformation();
        }
    }

    public static void resetConferenceInformation() {
        mConferenceId = null;
        mInitiatedFromURI = false;
    }

    public static void setIsMeetingHost(boolean isMeetingHost) {
        mIsMeetingHost = isMeetingHost;
    }

    public static boolean isMeetingHost() {
        return mIsMeetingHost;
    }

    public static void setClientId(String clientId) {
        mClientId = clientId;
    }

    public static String getClientId() {
        return mClientId;
    }

    public static String getConferenceId() {
        return mConferenceId;
    }

    public static void setConferenceId(String conferenceId) {
        mConferenceId = conferenceId;
    }

    public static void setIsInitiatedFromURI(boolean isInitiatedFromURI) {
        mInitiatedFromURI = isInitiatedFromURI;
    }

    public static boolean isInitiatedFromURI() {
        return mInitiatedFromURI;
    }
}
