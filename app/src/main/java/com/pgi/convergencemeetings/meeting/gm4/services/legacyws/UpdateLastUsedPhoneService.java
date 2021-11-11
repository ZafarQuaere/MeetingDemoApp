package com.pgi.convergencemeetings.meeting.gm4.services.legacyws;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.models.SetLastUsedPhoneResponse;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to update the last phone number used in Dial-out service.
 */
public class UpdateLastUsedPhoneService extends BaseService implements NetworkResponseHandler {

    private static final String TAG = UpdateLastUsedPhoneService.class.getSimpleName();

    private UpdateLastUserPhoneServiceCallbacks mServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Instantiates a new Update last used phone service.
     *
     * @param serviceCallbacks the service callbacks
     */
    public UpdateLastUsedPhoneService(UpdateLastUserPhoneServiceCallbacks serviceCallbacks) {
        this.mServiceCallbacks = serviceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Update last used phone number.
     *
     * @param registeredUserId the registered user id
     * @param isRegisteredUser the is registered user
     * @param countryCode      the country code
     * @param phoneNumber      the phone number
     * @param extension        the extension
     */
    public void updateLastUsedPhoneNumber(String registeredUserId, boolean isRegisteredUser, String countryCode, String phoneNumber, String extension ) {
        mNetworkRequestManager.updateLastUsedPhoneNumber(registeredUserId, isRegisteredUser, countryCode, phoneNumber, extension);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode = -1;
        try {
            String errorMsg = response.raw().message();
            errorCode = response.raw().code();

            if(errorMsg != null) {
                super.logErrorResponse(logger, TAG, errorCode, errorMsg);
            }
            String body = response.body();
            if (body != null) {
                SetLastUsedPhoneResponse setLastUsedPhoneResponse = DEFAULT_MAPPER.readValue(body, SetLastUsedPhoneResponse.class);
                if (setLastUsedPhoneResponse != null) {
                    mServiceCallbacks.onUpdateLastUsedPhoneNumberSuccess();

                }
            } else {
                mServiceCallbacks.onUpdateLastUsedPhoneNumberError(errorMsg, errorCode);
            }
        } catch (IOException e) {
            mServiceCallbacks.onUpdateLastUsedPhoneNumberError(e.getMessage(), errorCode);
        }
    }


    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}