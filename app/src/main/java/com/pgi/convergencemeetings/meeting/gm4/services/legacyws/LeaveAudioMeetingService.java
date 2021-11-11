package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.models.PIABaseModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to leave the Audio meeting for GM4 meeting rooms.
 */
public class LeaveAudioMeetingService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = LeaveAudioMeetingService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel leaveConferenceModel;

    /**
     * Instantiates a new Leave audio meeting service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public LeaveAudioMeetingService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Leave audio meeting.
     *
     * @param sessionId     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     */
    public void leaveAudioMeeting(String sessionId, String conferenceId, String participantId) {
        mNetworkRequestManager.leaveConference(sessionId, conferenceId, participantId);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            String errorMsg = response.raw().message();
            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if (response.body() != null) {
                errorCode = response.raw().code();
                leaveConferenceModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (leaveConferenceModel != null) {
                    if (leaveConferenceModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onLeaveConferenceSuccess();
                    } else {
                        mPIAServiceCallbacks.onLeaveConferenceError(leaveConferenceModel.getResultText(), errorCode );
                    }
                } else {
                     errorMsg = response.raw().message();

                    mPIAServiceCallbacks.onLeaveConferenceError(errorMsg,errorCode);
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(LeaveAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mPIAServiceCallbacks.onLeaveConferenceError(e.getMessage(), errorCode );
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}

