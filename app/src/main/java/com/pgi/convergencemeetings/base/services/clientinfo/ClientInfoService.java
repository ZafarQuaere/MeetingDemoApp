package com.pgi.convergencemeetings.base.services.clientinfo;

import android.content.Context;
import android.os.Handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.R;
import com.pgi.convergencemeetings.models.ClientDetails;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.convergencemeetings.models.ClientInfoResult;
import com.pgi.convergencemeetings.models.CompanyDetails;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;
import com.pgi.network.BuildConfig;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ashwanikumar on 12/18/2017.
 */

public class ClientInfoService extends BaseService implements NetworkResponseHandler {
    private static final String TAG = ClientInfoService.class.getSimpleName();
    private final Context mContext;
    private final ClientInfoCallBack mClientInfoCallBack;
    private final NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private int retryCount = 0;

    public ClientInfoService(Context context, ClientInfoCallBack clientInfoCallBack) {
        mClientInfoCallBack = clientInfoCallBack;
        mContext = context;
        mNetworkRequestManager = new NetworkRequestManager(this );

    }

    public void getClientInfo() {
        mNetworkRequestManager.getClientInfo();
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        int errorCode;
        try {
            okhttp3.Response rawResponse = response.raw();
            String errorMsg = rawResponse.message();
            errorCode = rawResponse.code();
            if (errorCode >= 400) {
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "UNKNOWN";
                }
                super.logErrorResponse(logger, TAG, errorCode, errorMsg);
            }
            String body = response.body();
            if (body != null) {
                if (rawResponse.request().url().toString().contains(AppConstants.CLIENT_INFO_TAG)) {
                    ClientInfoResponse clientInfoResponse = DEFAULT_MAPPER.readValue(body, ClientInfoResponse.class);
                    if (clientInfoResponse != null) {
                        ClientInfoDaoUtils clientInfoDaoUtils = ClientInfoDaoUtils.getInstance();
                        clientInfoDaoUtils.insertClientInfoInDb(clientInfoResponse);
                        ClientInfoResult clientInfoResult = clientInfoResponse.getClientInfoResult();
                        if (clientInfoResult != null) {
                            CompanyDetails companyDetails = clientInfoResult.getCompanyDetails();
                            String companyId = String.valueOf(companyDetails.getCompanyId());
                            if (companyId != null && !companyId.isEmpty()) {
                                logger.getUserModel().setCompanyId(companyId);
                            }
                        }
                        String clientId = clientInfoDaoUtils.getClientId();
                        if ((clientId == null || clientId.isEmpty()) && clientInfoResult != null) {
                            ClientDetails clientDetails = clientInfoResult.getClientDetails();
                            clientId = clientDetails.getClientId();
                        }
                        String conferenceId = clientInfoDaoUtils.getConferenceId();
                        if ((conferenceId == null || conferenceId.isEmpty()) && clientInfoResult != null) {
                            List<MeetingRoom> meetingRooms = clientInfoResult.getMeetingRooms();
                            if (!meetingRooms.isEmpty()) {
                                conferenceId = meetingRooms.get(0).getConfId();
                            }
                        }
                        mClientInfoCallBack.onClientInfoSuccess(clientId, conferenceId);
                    } else {
                        mClientInfoCallBack.onClientInfoError(mContext.getString(R.string.parsing_erro_client_info), errorCode);
                    }
                }
            } else {
                mClientInfoCallBack.onClientInfoError(errorMsg, errorCode);
            }
        } catch (IOException e) {
            errorCode = response.raw().code();
            String msg = e.getMessage();
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_CLIENTINFO,
                "ClientInfoService - Got Error on Clientinfo Service", e, null, true, false);
            if(getRetryCount() <= BuildConfig.DEFAULT_SERVICE_RETRY) {
                retryMethod(call);
            }else {
                mClientInfoCallBack.onClientInfoError(msg, errorCode);
            }
        } catch (Exception ex) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_CLIENTINFO,
                "ClientInfoService - Got Error on Clientinfo Service", ex, null, true, false);

        }
    }

    @Override
    public void onFailure(final Call<String> call, Throwable t) {
        retryCount++;
        if (retryCount <= BuildConfig.DEFAULT_SERVICE_RETRY) {
            int expDelay;
            switch (retryCount) {
                case AppConstants.FIRST_SERVICE_RETRY:
                    expDelay = AppConstants.RETRY_1000_MS;
                    break;
                case AppConstants.SECOND_SERVICE_RETRY:
                    expDelay = AppConstants.RETRY_3000_MS;
                    break;
                default:
                    expDelay = AppConstants.RETRY_5000_MS;
                    break;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    retry(call);
                }
            }, expDelay);
        } else {
            mClientInfoCallBack.onClientInfoError(t.getMessage(), AppConstants.FAILURE_RESULT_CODE);
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_CLIENTINFO,
                "ClientInfoService - Failed to get Client Info", t, null, true, false);

        }
    }
}

