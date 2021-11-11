package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.constants.PiaResultCodes;
import com.pgi.convergencemeetings.models.SubscribeAudioMeetingModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to subscribe for a meeting room, before joining the meeting.
 */
public class SubscribeAudioMeetingService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = SubscribeAudioMeetingService.class.getSimpleName();
    private ConferenceSubscribeServiceCallbacks mConferenceSubscribeServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private String sessionId;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private SubscribeAudioMeetingModel subscribeAudioMeetingModel;
    private int retryCount = 0;

    /**
     * Instantiates a new Subscribe audio meeting service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public SubscribeAudioMeetingService(ConferenceSubscribeServiceCallbacks meetingRoomPresenter) {
        this.mConferenceSubscribeServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Subscribe to audio meeting.
     *
     * @param accessToken the access token
     * @param clientId    the client id
     */
    public void subscribeToAudioMeeting(String accessToken, String clientId) {
        mNetworkRequestManager.subscribeToAudioMeeting(accessToken, clientId);
    }

    /**
     * Get session id string.
     *
     * @return the string
     */
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            String errorMsg = response.raw().message();
            if (errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if (response.body() != null) {
                errorCode = response.raw().code();
                subscribeAudioMeetingModel = DEFAULT_MAPPER.readValue(response.body(), SubscribeAudioMeetingModel.class);
                if (subscribeAudioMeetingModel != null) {
                    sessionId = subscribeAudioMeetingModel.getSessionId();
                    if (sessionId != null) {
                        mConferenceSubscribeServiceCallbacks.onSubscribeSuccess(sessionId);
                    } else if (!subscribeAudioMeetingModel.getResultCode().equals(PiaResultCodes.RESPONSE_SUCCESS)) {
                        mConferenceSubscribeServiceCallbacks.onSubscribeError(subscribeAudioMeetingModel.getResultText(), errorCode);
                    }
                } else {
                    errorMsg = response.raw().message();
                    mConferenceSubscribeServiceCallbacks.onSubscribeError(errorMsg, errorCode);

                }
            } else {
                errorCode = response.raw().code();
                mConferenceSubscribeServiceCallbacks.onSubscribeError(AppConstants.JWT_ACCESS_TOKEN_EXPIRED, errorCode);
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(SubscribeAudioMeetingService.class.getName(), "Error: " + e.getMessage());
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mConferenceSubscribeServiceCallbacks.onSubscribeError(e.getMessage(), errorCode);
            }
        }
    }

    @Override
    public void onFailure(final Call<String> call, Throwable t) {
        Log.e(SubscribeAudioMeetingService.class.getName(), "Error: " + t.getMessage());
        retryCount++;
        if (retryCount <= BuildConfig.DEFAULT_SERVICE_RETRY) {
            int expDelay = 0;
            if (retryCount == AppConstants.FIRST_SERVICE_RETRY) {
                expDelay = AppConstants.RETRY_1000_MS;
            } else if (retryCount == AppConstants.SECOND_SERVICE_RETRY) {
                expDelay = AppConstants.RETRY_3000_MS;
            } else {
                expDelay = AppConstants.RETRY_5000_MS;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    retry(call);
                }
            }, expDelay);
        } else {
            mConferenceSubscribeServiceCallbacks.onSubscribeFailure(AppConstants.FAILURE_RESULT_CODE);

        }
    }
}
