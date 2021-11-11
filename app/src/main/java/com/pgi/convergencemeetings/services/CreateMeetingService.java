package com.pgi.convergencemeetings.services;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.convergencemeetings.models.CreateMeetingRequest;
import com.pgi.convergencemeetings.models.CreateMeetingResponse;
import com.pgi.convergencemeetings.models.Meeting;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public class CreateMeetingService extends BaseService implements NetworkResponseHandler {
    private static final String TAG = CreateMeetingService.class.getSimpleName();

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private NetworkRequestManager mNetworkRequestManager;
    private CreateMeetingServiceCallbacks mCreateMeetingServiceCallbacks;

    public CreateMeetingService(CreateMeetingServiceCallbacks createMeetingServiceCallbacks){
        this.mCreateMeetingServiceCallbacks = createMeetingServiceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void createMeeting(Meeting meeting){
        try {
            CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest();
            createMeetingRequest.setMeeting(meeting);

            String requestBody = DEFAULT_MAPPER.writeValueAsString(createMeetingRequest);
            mNetworkRequestManager.createMeeting(requestBody);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            String errorMsg = response.raw().message();
            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if (response.body() != null) {
                CreateMeetingResponse result = DEFAULT_MAPPER.readValue(response.body(), CreateMeetingResponse.class);
                if(result != null){
                    mCreateMeetingServiceCallbacks.onCreateMeetingSuccess(result.getCreateMeetingResult());
                }
            }
            else {
                mCreateMeetingServiceCallbacks.onCreateMeetingError(errorMsg);
            }
        } catch (IOException e) {
            mCreateMeetingServiceCallbacks.onCreateMeetingError(e.getMessage());
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        mCreateMeetingServiceCallbacks.onCreateMeetingError(t.getMessage());
    }
}
