package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.BaseModel;

/**
 * Created by ashwanikumar on 12/6/2017.
 */

public interface ConferenceSubscribeServiceCallbacks<T extends BaseModel> {
    void onSubscribeSuccess(String sessionId);

    void onSubscribeError(String errMsg, int response);

    void onUnSubscribeSuccess();

    void onUnSubscribeError(String errMsg, int response);

    void onUnSubscribeFailure(int response);

    void onSubscribeFailure(int response);
}
