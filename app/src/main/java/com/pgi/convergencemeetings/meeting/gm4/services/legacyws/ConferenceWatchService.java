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

/**
 * Service class to set the conference watch on the meeting room, Need to set conference watch to get the meeting events.
 */
public class ConferenceWatchService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = ConferenceWatchService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel conferenceWatchConferenceModel;
    private int retryCount = 0;

    /**
     * Instantiates a new Conference watch service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public ConferenceWatchService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Sets conference watch.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void setConferenceWatch(String sessionId, String conferenceId) {
        mNetworkRequestManager.setConferenceWatch(sessionId, conferenceId);
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
                conferenceWatchConferenceModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (conferenceWatchConferenceModel != null) {
                    if (conferenceWatchConferenceModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onConferenceWatchSuccess();
                    } else if (conferenceWatchConferenceModel.getResultCode().equals("8")) {
                        mPIAServiceCallbacks.onConferenceWatchError(AppConstants.CONF_NOT_FOUND, errorCode);
                    } else {
                        mPIAServiceCallbacks.onConferenceWatchError(conferenceWatchConferenceModel.getResultText(), errorCode);
                    }
                } else {
                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onConferenceWatchError(errorMsg, errorCode);

                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: " + e.getMessage());
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mPIAServiceCallbacks.onConferenceWatchError(e.getMessage(), errorCode);
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
            mPIAServiceCallbacks.onClearConfereceWatchFailure();

        }
    }
}
