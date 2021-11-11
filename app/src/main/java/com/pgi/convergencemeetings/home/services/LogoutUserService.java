package com.pgi.convergencemeetings.home.services;


import com.pgi.convergencemeetings.base.services.BaseService;
import com.pgi.network.NetworkRequestManager;

import io.reactivex.Observable;

public class LogoutUserService extends BaseService {

    private NetworkRequestManager mNetworkRequestManager;

    public LogoutUserService() {
        mNetworkRequestManager = new NetworkRequestManager(this);
    }

    public Observable<String> logoutUser(String refreshToken) {
        return mNetworkRequestManager.logoutUser(true);
    }

    public Observable<String> revokeTokenLogoutUser(String refreshToken) {
        return mNetworkRequestManager.revokeUserToken(refreshToken);
    }
}
