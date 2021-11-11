package com.pgi.convergencemeetings.services;

import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneNumberResult;

/**
 * Created by ashwanikumar on 11/21/2017.
 */

public interface PhoneNumberServiceCallbacks <T extends BaseModel> {

    void onGetPhoneNumberSuccess(PhoneNumberResult phoneNumberResult);

    void onGetPhoneNumberError(String errorMsg, int response);
}
