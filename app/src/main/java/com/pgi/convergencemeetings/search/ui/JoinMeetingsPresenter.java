package com.pgi.convergencemeetings.search.ui;

import android.content.Context;
import android.widget.Toast;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.R;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.BaseModel;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.network.models.ImeetingRoomInfo;
import com.pgi.network.models.SearchResult;
import com.pgi.convergencemeetings.services.RecentMeetingService;
import com.pgi.convergencemeetings.services.RecentMeetingServiceCallbacks;
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus;
import com.pgi.convergencemeetings.utils.AppAuthUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used by {@link JoinMeetingFragment}
 * Requesting meeting join APIs to network and delivering callback to UI
 * If network request fail then retrying to network
 */

public class JoinMeetingsPresenter implements JoinMeetingContractor.presenter, RecentMeetingServiceCallbacks<BaseModel> {
    private static final String TAG = JoinMeetingsPresenter.class.getName();
    private static final String SORT_ORDER = "Ascending";
    private final Context mContext;
    private RecentMeetingService mRecentMeetingService;
    private JoinMeetingContractor.View mView;
    private AppAuthUtils mAppAuthUtils;
    private int reasonFailed;
    private int responseCode;
    private RetryStatus mRetryStatus;

    /**
     * Instantiating the class and initializing the member variables.
     * @param context
     * @param view
     */
    public JoinMeetingsPresenter(Context context, JoinMeetingContractor.View view) {
        mView = view;
        mContext = context;
        mAppAuthUtils = AppAuthUtils.getInstance();
        mRecentMeetingService = new RecentMeetingService(this);
    }

    // for use with unit tests
    public void setRecentMeetingService(RecentMeetingService recentMeetingService) {
        mRecentMeetingService = recentMeetingService;
    }

    @Override
    public void getRecentMeetingInfo(String clientId) {
        mRecentMeetingService.getRecentMeetingInfo(clientId);
    }


    @Override
    public void loadLocalData() {
        ApplicationDao applicationDao = ApplicationDao.get(ConvergenceApplication.appContext);
        List<SearchResult> searchResultList = applicationDao.getAllRecentMeetings();
        if (searchResultList == null) {
            // this should only occur if there was a problem accessing the DB
            Toast.makeText(mContext, R.string.required_service_failure, Toast.LENGTH_LONG).show();
        } else if (!searchResultList.isEmpty()) {
            List<ImeetingRoomInfo> iMeetingRoomInfos = new ArrayList<>(searchResultList.size());
            if (mAppAuthUtils != null && mAppAuthUtils.isUserTypeGuest()) {
                Collections.sort(searchResultList);
            }
            iMeetingRoomInfos.addAll(searchResultList);
            mView.notifyAdapterOnRecentUpdate(iMeetingRoomInfos);
        } else {
            if (mAppAuthUtils != null && mAppAuthUtils.isUserTypeGuest()) {
                mView.notifyAdapterOnRecentUpdate(null);
            } else {
                mView.showProgress();
            }
        }
    }

    @Override
    public void onClientInfoSuccess(ClientInfoResponse clientInfoResponse) {
        //CommonUtils.launchAudioSelectionActivity(mContext);
    }

    @Override
    public void onRecentMeetingSuccessCallback() {
        ApplicationDao applicationDao = ApplicationDao.get(ConvergenceApplication.appContext);
        List<SearchResult> searchResults = applicationDao.getAllRecentMeetings();
        if (searchResults != null) {
            List<ImeetingRoomInfo> imeetingRoomInfos = new ArrayList<>();
            imeetingRoomInfos.addAll(searchResults);
            mView.notifyAdapterOnRecentUpdate(imeetingRoomInfos);
            mView.hideProgress();
        }
    }



    @Override
    public void onRecentMeetingErrorCallback(String errorMsg, int response) {
        mView.hideProgress();
        mView.onRecentMeetingInfoError(errorMsg, response);
        reasonFailed = RetryStatus.FAILED;
        sendFailureResponse(response);
    }

    @Override
    public void onClientInfoError(int errorMsg, int response) {
        reasonFailed = RetryStatus.WS_CLIENT_INFO_OTHERS;
        sendFailureResponse(response);
    }

    /**
     * If service failed when interacting with network and received failure response then sending it for retry
     * @param response
     */
    public void sendFailureResponse(int response){
        responseCode = response;
        mRetryStatus = new RetryStatus(responseCode, reasonFailed);
        if(!String.valueOf(responseCode).startsWith(AppConstants.CODE_4) && String.valueOf(responseCode).startsWith(AppConstants.CODE_2)){
            mView.onServiceRetryFailedParent(mRetryStatus);
        }
    }
}
