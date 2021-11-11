package com.pgi.convergencemeetings.utils;

import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone;

/**
 * Created by ashwanikumar on 11/15/2017.
 */

public interface DialOutNumberListener {
    void onPhoneNumberSelected(Phone phone);
}
