package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.models.ConferenceStateModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to get the state of conference, conference may be active, locked.
 */
public class ConferenceStateService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = ConferenceStateService.class.getSimpleName();
    private ConferenceStateServiceCallbacks mConferenceStateServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private ConferenceStateModel conferenceStateModel;
    private int retryCount = 0;

    /**
     * Instantiates a new Conference state service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public ConferenceStateService(ConferenceStateServiceCallbacks meetingRoomPresenter) {
        this.mConferenceStateServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Gets conference state.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void getConferenceState(String sessionId, String conferenceId) {
        mNetworkRequestManager.getConferenceState(sessionId, conferenceId);
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
                conferenceStateModel = DEFAULT_MAPPER.readValue(response.body(), ConferenceStateModel.class);
                if (conferenceStateModel != null) {
                    mConferenceStateServiceCallbacks.onConferenceStateSuccess(conferenceStateModel);

                }
            } else {
                errorCode = response.raw().code();
                errorMsg = response.raw().message();
                mConferenceStateServiceCallbacks.onConferenceStateError(errorMsg, errorCode);

            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: " + e.getMessage());

            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mConferenceStateServiceCallbacks.onConferenceStateError(e.getMessage(), errorCode);
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
        } else {
            mConferenceStateServiceCallbacks.onConferenceStateFailure(AppConstants.FAILURE_RESULT_CODE);

        }
    }
}

