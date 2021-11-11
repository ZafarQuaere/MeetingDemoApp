package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.constants.PiaResultCodes;
import com.pgi.convergencemeetings.models.UnsubscribeAudioMeetingModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to unsubscribe for a meeting room, before joining the meeting.
 */
public class UnSubscribeAudioMeetingService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = UnSubscribeAudioMeetingService.class.getSimpleName();
    private ConferenceSubscribeServiceCallbacks mConferenceSubscribeServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private UnsubscribeAudioMeetingModel unSubscribeAudioMeetingModel;

    /**
     * Instantiates a new Un subscribe audio meeting service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public UnSubscribeAudioMeetingService(ConferenceSubscribeServiceCallbacks meetingRoomPresenter) {
        this.mConferenceSubscribeServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Un subscribe to audio meeting.
     *
     * @param sessionID the session id
     */
    public void unSubscribeToAudioMeeting(String sessionID) {
        mNetworkRequestManager.unSubscribePIASession(sessionID);
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
                unSubscribeAudioMeetingModel = DEFAULT_MAPPER.readValue(response.body(), UnsubscribeAudioMeetingModel.class);
                if (unSubscribeAudioMeetingModel != null) {
                    if (unSubscribeAudioMeetingModel.getResultCode().equals(PiaResultCodes.RESPONSE_SUCCESS)) {
                        mConferenceSubscribeServiceCallbacks.onUnSubscribeSuccess();
                    } else if(unSubscribeAudioMeetingModel.getResultCode().equals(PiaResultCodes.REPONSE_INVALID_SESSIONID)){
                        mConferenceSubscribeServiceCallbacks.onUnSubscribeError(AppConstants.INVALID_SESSION_ID, errorCode);
                    }
                } else {
                    errorMsg = response.raw().message();
                    mConferenceSubscribeServiceCallbacks.onUnSubscribeError(errorMsg, errorCode );
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(UnSubscribeAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mConferenceSubscribeServiceCallbacks.onUnSubscribeError(e.getMessage(),errorCode );
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        mConferenceSubscribeServiceCallbacks.onUnSubscribeFailure(AppConstants.FAILURE_RESULT_CODE);

    }
}
