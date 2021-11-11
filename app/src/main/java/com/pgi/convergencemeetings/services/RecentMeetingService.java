package com.pgi.convergencemeetings.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.R;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.convergencemeetings.models.DesktopMeetingSearchResult;
import com.pgi.convergencemeetings.models.RecentMeetingsModel;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
import com.pgi.convergencemeetings.utils.ClientInfoResultCache;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;
import com.pgi.network.NetworkRequestManager;
import com.pgi.network.NetworkResponseHandler;
import com.pgi.network.models.SearchResult;
import com.pgi.network.models.SearchResultDao;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Service class to create recent meetings which user has joined. We need to create an entry for a meeting
 * which is recently joined. If there is already an entry we need to update the timestamp for that meeting.
 */
public class RecentMeetingService extends BaseService implements NetworkResponseHandler {
    private static final String TAG = RecentMeetingService.class.getSimpleName();
    private RecentMeetingServiceCallbacks mServiceCallbacks;
    private NetworkRequestManager mNetworkRequestManager;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private RecentMeetingsModel recentMeetingModel;

    /**
     * Instantiates a new Recent meeting service.
     *
     * @param recentMeetingServiceCallbacks the recent meeting service callbacks
     */
    public RecentMeetingService(RecentMeetingServiceCallbacks recentMeetingServiceCallbacks) {
        this.mServiceCallbacks = recentMeetingServiceCallbacks;
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    /**
     * Gets recent meeting info.
     *  @param clientId    the client id
     *
     */
    public void getRecentMeetingInfo(String clientId) {
        mNetworkRequestManager.getRecentMeetingInfo(clientId);
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
                if (response.raw().request().url().toString().contains(AppConstants.DESKTOP_MEETING_SEARCH)) {
                    recentMeetingModel = DEFAULT_MAPPER.readValue(response.body(), RecentMeetingsModel.class);
                    if (recentMeetingModel != null) {
                        DesktopMeetingSearchResult desktopMeetingResult = recentMeetingModel.getDesktopMeetingSearchResult();
                        if (desktopMeetingResult != null) {
                            List<SearchResult> searchResults = desktopMeetingResult.getSearchResults();
                            if (searchResults != null) {
                                ApplicationDao applicationDao = ApplicationDao.get(ConvergenceApplication.appContext);
                                SearchResultDao searchResultDao = applicationDao.getSearchResult();
                                //Remove all the entries from DB before inserting
                                searchResultDao.deleteAll();
                                Collections.reverse(searchResults);
                                ClientInfoDaoUtils clientInfoDaoUtils = ClientInfoDaoUtils.getInstance();
                                for (SearchResult searchResult : searchResults) {
                                    if (clientInfoDaoUtils != null && clientInfoDaoUtils.getConferenceIdSet() != null &&
                                            clientInfoDaoUtils.getConferenceIdSet().contains(String.valueOf(searchResult.getConferenceId()))) {
                                        //We should not display recent meeting of self....
                                    } else {
                                        searchResultDao.insertOrReplace(searchResult);
                                    }
                                }
                                mServiceCallbacks.onRecentMeetingSuccessCallback();
                            }
                        }
                    }
                } else if (response.raw().request().url().toString().contains(AppConstants.CLIENT_INFO_TAG)) {

                    ClientInfoResponse clientInfoResponse = DEFAULT_MAPPER.readValue(response.body(), ClientInfoResponse.class);
                    if (clientInfoResponse != null) {
                        ClientInfoResultCache.getInstance().setValueInCache(clientInfoResponse);
                        mServiceCallbacks.onClientInfoSuccess(clientInfoResponse);
                    } else {
                        logger.error(TAG, LogEvent.ERROR, LogEventValue.WS_RECENTS,
                            "RecentMeetingService - Failed to get Recent meetings", null, null, true, false);
                        mServiceCallbacks.onClientInfoError(R.string.parsing_erro_client_info, errorCode);
                    }
                }
            } else {
                errorCode = response.raw().code();
                errorMsg = response.raw().message();
                mServiceCallbacks.onRecentMeetingErrorCallback(errorMsg, errorCode);
            }
        } catch (IOException e) {
            logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_RECENTS,
                "RecentMeetingService - Failed to get Recent meetings", e, null, true, false);
            errorCode = response.raw().code();
            mServiceCallbacks.onRecentMeetingErrorCallback(e.getMessage(),errorCode);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.WS_RECENTS,
            "RecentMeetingService - Failed to get Recent meetings", t, null, true, false);
    }

    /**
     * Gets others client info.
     *
     * @param conferenceId the conference id
     */
    public void getOthersClientInfo(String conferenceId) {
        mNetworkRequestManager.getOthersClientInfo(conferenceId);
    }
}
