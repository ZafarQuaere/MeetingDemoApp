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
 * Service class to clear the conference watch on a specific meeting room.
 */
public class ClearConferenceWatchService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = ClearConferenceWatchService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel conferenceOptionConferenceModel;

    /**
     * Instantiates a new Clear conference watch service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public ClearConferenceWatchService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Clear conference watch.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void clearConferenceWatch(String sessionId, String conferenceId) {
        mNetworkRequestManager.clearConferenceWatch(sessionId, conferenceId);
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
                conferenceOptionConferenceModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (conferenceOptionConferenceModel != null) {
                    if (conferenceOptionConferenceModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onClearConfereceWatchSuccess();
                    } else{
                        mPIAServiceCallbacks.onClearConfereceWatchError(conferenceOptionConferenceModel.getResultText(), errorCode );
                    }
                } else {
                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onClearConfereceWatchError(errorMsg, errorCode );
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mPIAServiceCallbacks.onClearConfereceWatchError(e.getMessage(), errorCode);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
