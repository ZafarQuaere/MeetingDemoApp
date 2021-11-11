package com.pgi.convergencemeetings.services;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.convergencemeetings.models.MeetingInfoResponse;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public class FetchMeetingRoomService extends BaseService implements NetworkResponseHandler {
    private static final String TAG = FetchMeetingRoomService.class.getSimpleName();

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private NetworkRequestManager mNetworkRequestManager;
    private FetchMeetingRoomServiceCallbacks mFetchMeetingRoomServiceCallbacks;

    public FetchMeetingRoomService(FetchMeetingRoomServiceCallbacks fetchMeetingRoomServiceCallbacks){
        this.mFetchMeetingRoomServiceCallbacks = fetchMeetingRoomServiceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public void fetchMeetingRoom(String meetingRoomId){
        mNetworkRequestManager.fetchMeetingRoom(meetingRoomId);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            String errorMsg = response.raw().message();
            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if (response.body() != null) {
                MeetingInfoResponse result = DEFAULT_MAPPER.readValue(response.body(), MeetingInfoResponse.class);
                if(result != null){
                    mFetchMeetingRoomServiceCallbacks.onFetchMeetingRoomSuccess(result.getMeetingRoomGetResult());
                }
            }
            else {
                mFetchMeetingRoomServiceCallbacks.onFetchMeetingRoomErrorCallback(errorMsg);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error: "+e.getMessage());
            mFetchMeetingRoomServiceCallbacks.onFetchMeetingRoomErrorCallback(e.getMessage());
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Log.e(TAG, "onFailure: "+t.getMessage());
    }
}
