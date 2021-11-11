package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.BaseModel;

/**
 * Created by visheshchandra on 12/20/2017.
 */

public interface UpdateMeetingServiceCallbacks<T extends BaseModel> {
    void onUpdateMeetingSuccess();
    void onUpdateMeetingError(String errorMsg);
}
