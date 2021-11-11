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
 * Service class to end the Audio meeting for GM4 meeting rooms.
 */
public class EndAudioMeetingService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = EndAudioMeetingService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel endConferenceModel;

    /**
     * Instantiates a new End audio meeting service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public EndAudioMeetingService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * End audio meeting.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void endAudioMeeting(String sessionId, String conferenceId) {
        mNetworkRequestManager.endConference(sessionId, conferenceId);
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
                endConferenceModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (endConferenceModel != null) {
                    if (endConferenceModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onEndConferenceSuccess();
                    } else {
                        mPIAServiceCallbacks.onEndConferenceError(endConferenceModel.getResultText(),errorCode );
                    }
                } else {
                     errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onEndConferenceError(errorMsg,errorCode );
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mPIAServiceCallbacks.onEndConferenceError(e.getMessage(),errorCode );
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
