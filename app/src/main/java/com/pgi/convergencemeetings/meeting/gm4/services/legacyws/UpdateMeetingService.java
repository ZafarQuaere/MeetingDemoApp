package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.models.Meeting;
import com.pgi.convergencemeetings.models.UpdateMeetingRequest;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public class UpdateMeetingService extends BaseService implements NetworkResponseHandler {
    private static final String TAG = UpdateMeetingService.class.getSimpleName();
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private NetworkRequestManager mNetworkRequestManager;
    private UpdateMeetingServiceCallbacks mUpdateMeetingServiceCallbacks;

    public UpdateMeetingService(UpdateMeetingServiceCallbacks mUpdateMeetingServiceCallbacks){
        this.mUpdateMeetingServiceCallbacks = mUpdateMeetingServiceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void updateMeeting(String desktopMeetingId, Meeting meeting){
        try {
            UpdateMeetingRequest updateMeetingRequest = new UpdateMeetingRequest();
            updateMeetingRequest.setMeeting(meeting);

            String requestBody = DEFAULT_MAPPER.writeValueAsString(updateMeetingRequest);
            mNetworkRequestManager.updateMeeting(desktopMeetingId, requestBody);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        String errorMsg = response.raw().message();
        if(errorMsg != null) {
            super.logErrorResponse(logger, TAG, response.code(), errorMsg);
        }
        if (response.body() != null) {
            mUpdateMeetingServiceCallbacks.onUpdateMeetingSuccess();
        }
        else {
            errorMsg = response.raw().message();
            mUpdateMeetingServiceCallbacks.onUpdateMeetingError(errorMsg);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        mUpdateMeetingServiceCallbacks.onUpdateMeetingError(t.getMessage());
    }
}
