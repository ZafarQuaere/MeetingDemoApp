package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.BaseModel;

/**
 * Created by ashwanikumar on 12/6/2017.
 */

public interface UpdateLastUserPhoneServiceCallbacks<T extends BaseModel> {
    void onUpdateLastUsedPhoneNumberSuccess();

    void onUpdateLastUsedPhoneNumberError(String errorMsg, int response);
}
