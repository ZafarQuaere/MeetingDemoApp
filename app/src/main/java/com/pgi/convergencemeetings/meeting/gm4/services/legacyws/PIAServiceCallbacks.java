package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.enterUrlModel.MeetingRoomSearchResult_;

public interface PIAServiceCallbacks<T extends BaseModel> {
    void onStartConferenceSuccess();

    void onStartConferenceError(String errorMsg, int response);

    void onEndConferenceSuccess();

    void onEndConferenceError(String errorMsg, int response);

    void onLeaveConferenceSuccess();

    void onLeaveConferenceError(String resultText, int response);

    void onConferenceWatchSuccess();

    void onConferenceWatchError(String resultText, int response);

    void onConferenceOptionSuccess();

    void onConferenceOptionError(String resultText, int response);

    void onParticipantOptionSuccess();

    void onParticipantOptionError(String resultText, int response);

    void onClearConfereceWatchError(String message, int response);

    void onClearConfereceWatchSuccess();

    void onParticipantInfoSuccess();

    void onParticipantInfoError(String resultText, int response);

    void onHangupError(String errorMsg, int response);

    void onMeetingRoomSearchSuccess(MeetingRoomSearchResult_ meetingRoomSearchResult_);

    void onDialParticipantSuccess(String participantId);

    void onDialParticipantError(String errorMsg, int response);

    void onClearConfereceWatchFailure();
}
