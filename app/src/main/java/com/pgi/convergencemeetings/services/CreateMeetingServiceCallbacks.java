package com.pgi.convergencemeetings.services;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.CreateMeetingResult;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public interface CreateMeetingServiceCallbacks<T extends BaseModel> {
    void onCreateMeetingSuccess(CreateMeetingResult createMeetingResult);
    void onCreateMeetingError(String errorMsg);
}
