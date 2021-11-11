package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.os.Handler;

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
 * Service class to set the conference options. conference options we need to set for getting the
 * talker and canMute events. Following are the options available
 * "TalkerNotifyOn", "TalkerNotifyOff", "ConfMute", "ConfUnMute".
 */
public class ConferenceOptionService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = ConferenceOptionService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel conferenceOptionConferenceModel;
    private int retryCount = 0;

    public ConferenceOptionService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void setConferenceOption(String sessionId, String conferenceId, String option) {
        mNetworkRequestManager.setConferenceOption(sessionId, conferenceId, option);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            okhttp3.Response rawResponse = response.raw();
            String errorMsg = rawResponse.message();
            errorCode = rawResponse.code();
            if (errorCode >= 400) {
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "UNKNOWN";
                }
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
                mPIAServiceCallbacks.onConferenceOptionError(errorMsg, errorCode);
            } else {
                String body = response.body();
                if (body != null && body.length() > 0) {
                    conferenceOptionConferenceModel = DEFAULT_MAPPER.readValue(body, PIABaseModel.class);
                    if (conferenceOptionConferenceModel != null) {
                        if (conferenceOptionConferenceModel.getResultCode().equals("0")) {
                            mPIAServiceCallbacks.onConferenceOptionSuccess();
                        } else {
                            mPIAServiceCallbacks.onConferenceOptionError(conferenceOptionConferenceModel.getResultText(), errorCode);
                        }
                    } else {
                        mPIAServiceCallbacks.onConferenceOptionError(errorMsg, errorCode);
                    }
                } else {
                    mPIAServiceCallbacks.onConferenceOptionError(errorMsg, errorCode);
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mPIAServiceCallbacks.onConferenceOptionError(e.getMessage(), errorCode);
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
