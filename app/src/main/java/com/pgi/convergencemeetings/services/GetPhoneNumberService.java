package com.pgi.convergencemeetings.services;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneNumberResult;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to get the last phone number used in Dial-out service.
 */
public class GetPhoneNumberService extends BaseService implements NetworkResponseHandler{

    private static final String TAG = GetPhoneNumberService.class.getSimpleName();
    private PhoneNumberServiceCallbacks mServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PhoneNumberResult phoneNumberResult;

    /**
     * Instantiates a new Get phone number service.
     *
     * @param serviceCallbacks the service callbacks
     */
    public GetPhoneNumberService(PhoneNumberServiceCallbacks serviceCallbacks) {
        this.mServiceCallbacks = serviceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Gets phone number.
     *
     * @param clientId the client id
     */
    public void getPhoneNumber(String clientId) {
        mNetworkRequestManager.getPhoneNumber(clientId);
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
                phoneNumberResult = DEFAULT_MAPPER.readValue(response.body(), PhoneNumberResult.class);
                if (phoneNumberResult != null) {
                    mServiceCallbacks.onGetPhoneNumberSuccess(phoneNumberResult);

                }
            } else {
                errorCode = response.raw().code();
                errorMsg = response.raw().message();
                mServiceCallbacks.onGetPhoneNumberError(errorMsg,errorCode );
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            Log.e(GetPhoneNumberService.class.getName(), "Error: "+e.getMessage());
            mServiceCallbacks.onGetPhoneNumberError(e.getMessage(), errorCode);
        }
    }


    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
