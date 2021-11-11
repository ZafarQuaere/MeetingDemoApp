package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.models.PIABaseModel;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to set the participant options. participant options we need to set canMute and unmute events.
 * Following are the options available
 * "Mute", "UnMute".
 */
public class ParticipantOptionService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = ParticipantOptionService.class.getSimpleName();
    private PIAServiceCallbacks mPIAServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PIABaseModel participantConferenceOptionModel;

    /**
     * Instantiates a new Participant option service.
     *
     * @param meetingRoomPresenter the meeting room presenter
     */
    public ParticipantOptionService(PIAServiceCallbacks meetingRoomPresenter) {
        this.mPIAServiceCallbacks = meetingRoomPresenter;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Sets participant option.
     *
     * @param sessionId     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @param option        the option
     */
    public void setParticipantOption(String sessionId, String conferenceId, String participantId, String option) {
        mNetworkRequestManager.setParticipantOption(sessionId, conferenceId, participantId, option);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            String errorMsg = response.raw().message();
            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, response.code(), errorMsg);
            }
            if (response.body() != null) {
                errorCode = response.raw().code();
                participantConferenceOptionModel = DEFAULT_MAPPER.readValue(response.body(), PIABaseModel.class);
                if (participantConferenceOptionModel != null) {
                    if (participantConferenceOptionModel.getResultCode().equals("0")) {
                        mPIAServiceCallbacks.onParticipantOptionSuccess();
                    } else{
                        mPIAServiceCallbacks.onParticipantOptionError(participantConferenceOptionModel.getResultText(), errorCode );
                    }
                } else {
                    errorMsg = response.raw().message();
                    mPIAServiceCallbacks.onParticipantOptionError(errorMsg, errorCode );
                }
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(EndAudioMeetingService.class.getName(), "Error: "+e.getMessage());
            mPIAServiceCallbacks.onParticipantOptionError(e.getMessage(), errorCode);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
