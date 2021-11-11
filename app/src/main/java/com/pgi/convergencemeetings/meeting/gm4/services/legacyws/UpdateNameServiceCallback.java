package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.settings.ClientUpdateResult;

/**
 * Created by ashwanikumar on 12/1/2017.
 */

public interface UpdateNameServiceCallback {
    void onNameUpdateSuccess(ClientUpdateResult clientUpdateResult);
    void onNameUpdateError(int response);
}
