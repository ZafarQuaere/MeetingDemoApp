package com.pgi.convergencemeetings.services;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.ClientInfoResponse;

/**
 * Created by visheshchandra on 12/26/2017.
 */

public interface RecentMeetingServiceCallbacks<T extends BaseModel> {
    void onRecentMeetingSuccessCallback();
    void onRecentMeetingErrorCallback(String errorMsg, int response);
    void onClientInfoError(int errorMsg, int response);
    void onClientInfoSuccess(ClientInfoResponse clientInfoResponse);
}
