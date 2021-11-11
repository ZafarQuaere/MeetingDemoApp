package com.pgi.convergencemeetings.services;

import android.util.Log;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import retrofit2.Call;
import retrofit2.Response;

public class RoomTypeService extends BaseService implements NetworkResponseHandler {
    private static final String constError = "RoomType service error";
    private static final String TAG = RoomTypeService.class.getSimpleName();
    private NetworkRequestManager mNetworkRequestManager;
    private RoomTypeServiceCallbacks mServiceCallbacks;

    public RoomTypeService(RoomTypeServiceCallbacks serviceCallbacks) {
        this.mServiceCallbacks = serviceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * get room type (gm4 or gm5)
     * @param furl
     */
    public void getRoomType(String furl) {
        String msg = "getRoomType: furl=" + furl;
//        mLogger.info(TAG, null, null, msg);
        mNetworkRequestManager.getRoomType(furl);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            okhttp3.Response rawResponse = response.raw();
            String errorMsg = rawResponse.message();
            int errorCode = rawResponse.code();
            if (errorCode >= 400) {
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "UNKNOWN";
                }
                super.logErrorResponse(logger, TAG, errorCode, errorMsg);
            } else {
                okhttp3.Response networkResponse = rawResponse.networkResponse();
                if (networkResponse != null) {
                    String nr = networkResponse.toString();
                    if (nr != null) {
                        int type;
                        if (nr.contains("boot.html")) {
                            type = AppConstants.GM5_Room;
                        } else {
                            type = AppConstants.GM4_ROOM;
                        }
                        mServiceCallbacks.onRoomTypeSuccess(type);
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getStackTrace()[0].getMethodName() + " " + ex.getMessage();
            Log.e(TAG, msg);
//            mLoggerUtil.error(TAG, msg, ex);
        }
        mServiceCallbacks.onRoomTypeError(constError);
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
//        mLoggerUtil.error(TAG, "RoomTypeService:onFailure called", t);
    }

}
