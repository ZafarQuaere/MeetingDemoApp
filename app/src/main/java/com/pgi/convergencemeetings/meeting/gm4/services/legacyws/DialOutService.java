package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.os.Handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.models.DialOutResponse;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to make a Dial-out service for to join a conference via Call me option.
 */
public class DialOutService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = DialOutService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private DialOutResponse dialOutResponse;
    private int retryCount = 0;

    /**
     * Instantiates a new Dial out service.
     *
     * @param piaServiceCallbacks the pia service callbacks
     */
    public DialOutService(PIAServiceCallbacks piaServiceCallbacks) {
        this.mPIAServiceCallbacks = piaServiceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Dial out to participant.
     *
     * @param sessionId       the session id
     * @param conferenceId    the conference id
     * @param phoneNumber     the phone number
     * @param participantType the participant type
     * @param dialOutType     the dial out type
     * @param firstName       the first name
     * @param lastName        the last name
     */
    public void dialOutToParticipant(String sessionId, String conferenceId, String phoneNumber, String participantType, String dialOutType, String firstName, String lastName) {
        mNetworkRequestManager.dialOutToParticipant(sessionId, conferenceId, phoneNumber, participantType, dialOutType, firstName, lastName);
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
                dialOutResponse = DEFAULT_MAPPER.readValue(response.body(), DialOutResponse.class);
                if (dialOutResponse != null) {
                    if (dialOutResponse.getResultCode() == 0) {
                        mPIAServiceCallbacks.onDialParticipantSuccess(dialOutResponse.getParticipantId());
                    } else {
                        mPIAServiceCallbacks.onDialParticipantError(dialOutResponse.getResultText(), errorCode);
                    }
                } else {

                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onDialParticipantError(errorMsg, errorCode);

                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.UAPI_DIALOUT,
              "Got Error on DialOutService", e, null, true, false);
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }
            else {
                mPIAServiceCallbacks.onDialParticipantError(e.getMessage(), errorCode);
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
          logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.UAPI_DIALOUT,
              "Got Error on DialOutService", null, null, true, false);
        }
    }
}
