package com.pgi.convergencemeetings.base.services;

import android.os.Handler;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.logging.Logger;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkResponseHandler;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by amit1829 on 7/24/2017.
 */

public class BaseService implements NetworkResponseHandler {

    private static final String SERVICE_ERROR = "Service.Error";
    private static final String RETURNED = " returned ";
    private int retryCount;
    protected Logger logger = ConvergenceApplication.mLogger;


  public void logErrorResponse(Logger logger, String tag, int errorCode, String message) {
        if (errorCode >= 400) {
            logger.error(tag, LogEvent.ERROR, LogEventValue.WS_ERROR, errorCode + " : "  + message,
                null, null, false, false);
        }
    }

    protected void retryMethod(Call<String> call) {
        retryCount++;
        if (retryCount <= BuildConfig.DEFAULT_SERVICE_RETRY) {
            int expDelay = 0;
            if (retryCount == AppConstants.FIRST_SERVICE_RETRY) {
                expDelay = AppConstants.RETRY_1000_MS;
            } else if (retryCount == AppConstants.SECOND_SERVICE_RETRY) {
                expDelay = AppConstants.RETRY_3000_MS;
            } else {
                expDelay = AppConstants.RETRY_5000_MS;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    retry(call);
                }
            }, expDelay);
        }
    }

    protected int getRetryCount() {
        return retryCount;
    }

    protected void retry(Call<String> call) {
        call.clone().enqueue(this);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
