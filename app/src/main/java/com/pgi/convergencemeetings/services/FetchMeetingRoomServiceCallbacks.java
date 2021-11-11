package com.pgi.convergencemeetings.services;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.MeetingRoomGetResult;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public interface FetchMeetingRoomServiceCallbacks<T extends BaseModel> {
    void onFetchMeetingRoomSuccess(MeetingRoomGetResult result);
    void onFetchMeetingRoomErrorCallback(String errorMsg);
}
