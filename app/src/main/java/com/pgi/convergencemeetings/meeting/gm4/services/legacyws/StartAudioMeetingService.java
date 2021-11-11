package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.models.PIABaseModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class StartAudioMeetingService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = StartAudioMeetingService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel startConferenceModel;
    private int retryCount = 0;

    public StartAudioMeetingService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void startAudioMeeting(String sessionId, String conferenceId) {
        mNetworkRequestManager.startConference(sessionId, conferenceId);
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
                startConferenceModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (startConferenceModel != null) {
                    if (startConferenceModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onStartConferenceSuccess();
                    } else {
                        mPIAServiceCallbacks.onStartConferenceError(startConferenceModel.getResultText(), response.code());
                    }
                } else {
                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onStartConferenceError(errorMsg, errorCode);
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(StartAudioMeetingService.class.getName(), "Error: " + e.getMessage());
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mPIAServiceCallbacks.onStartConferenceError(e.getMessage(), errorCode);
            }
        }
    }

    @Override
    public void onFailure(final Call<String> call, Throwable t) {
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
        }
    }
}
