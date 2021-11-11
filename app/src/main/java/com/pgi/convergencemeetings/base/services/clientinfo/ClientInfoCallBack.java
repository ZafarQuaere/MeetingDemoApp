package com.pgi.convergencemeetings.base.services.clientinfo;

public interface ClientInfoCallBack {
	void onClientInfoSuccess(String clientId, String conferenceId);
	void onClientInfoError(String errMsg, int response);
}
