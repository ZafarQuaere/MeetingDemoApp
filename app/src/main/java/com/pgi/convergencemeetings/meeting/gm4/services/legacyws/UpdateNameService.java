package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.models.settings.ClientUpdateResult;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ashwanikumar on 12/1/2017.
 */

public class UpdateNameService extends BaseService implements NetworkResponseHandler{
    private static final String TAG = UpdateNameService.class.getSimpleName();
    private UpdateNameServiceCallback mUpdateNameServiceCallback;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public UpdateNameService(UpdateNameServiceCallback meetingRoomPresenter) {
        this.mUpdateNameServiceCallback = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void updateClientName(String clienId, String requestBody){
        mNetworkRequestManager.updateClientName(clienId, requestBody);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            String errorMsg = response.raw().message();
            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if(response.body() != null){
                errorCode = response.raw().code();
                ClientUpdateResult clientUpdateResult = DEFAULT_MAPPER.readValue(response.body(), ClientUpdateResult.class);
                mUpdateNameServiceCallback.onNameUpdateSuccess(clientUpdateResult);
            }else{
                errorCode = response.raw().code();
                mUpdateNameServiceCallback.onNameUpdateError(errorCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        mUpdateNameServiceCallback.onNameUpdateError(AppConstants.FAILURE_RESULT_CODE);
    }
}
