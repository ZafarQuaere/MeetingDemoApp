package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.ConferenceStateModel;

/**
 * Created by ashwanikumar on 12/6/2017.
 */

public interface ConferenceStateServiceCallbacks<T extends BaseModel> {
    void onConferenceStateError(String message, int response);

    void onConferenceStateSuccess(ConferenceStateModel conferenceStateModel);

    void onConferenceStateFailure(int response);
}
