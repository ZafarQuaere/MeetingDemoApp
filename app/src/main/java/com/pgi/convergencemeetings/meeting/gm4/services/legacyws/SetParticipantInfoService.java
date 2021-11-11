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
 * Created by ashwanikumar on 10/27/2017.
 */

public class SetParticipantInfoService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = SetParticipantInfoService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel participantInfoModel;

    public SetParticipantInfoService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void setParticipantInfo(String sessionId, String conferenceId, String participantId, String firstName, String lastName, String companyName, String phoneNumber, String emailAddress) {
        mNetworkRequestManager.setParticipantInfo(sessionId, conferenceId, participantId, firstName, lastName, companyName, phoneNumber, emailAddress);
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
                participantInfoModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (participantInfoModel != null) {
                    if (participantInfoModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onParticipantInfoSuccess();
                    } else{
                        mPIAServiceCallbacks.onParticipantInfoError(participantInfoModel.getResultText(), errorCode );
                    }
                } else {
                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onParticipantInfoError(errorMsg, errorCode);
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mPIAServiceCallbacks.onParticipantInfoError(e.getMessage(), errorCode );
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
