package com.pgi.convergencemeetings.leftnav.settings.ui

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.models.settings.Client
import com.pgi.convergencemeetings.models.settings.ClientNameRequestModel
import com.pgi.convergencemeetings.models.settings.ClientUpdateMessage
import com.pgi.convergencemeetings.models.settings.ClientUpdateResult
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoCallBack
import com.pgi.convergencemeetings.base.services.clientinfo.ClientInfoService
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateNameService
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateNameServiceCallback
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils

/**
 * Created by ashwanikumar on 12/14/2017.
 */
class UpdateNamePresenter(private val mView: UpdateNameContract.view) : UpdateNameContract.presenter, UpdateNameServiceCallback, ClientInfoCallBack {
	private val mContext: Context = mView as Context
	private val mUpdateNameService: UpdateNameService = UpdateNameService(this)
	private var reasonFailed = 0
	private var responseCode = 0

	override fun requestNameUpdate(clientId: String, firstName: String, lastName: String) {
		val clientNameRequestModel = getClientNameRequestModel(firstName, lastName)
		try {
			val requestBody = DEFAULT_MAPPER.writeValueAsString(clientNameRequestModel)
			mUpdateNameService.updateClientName(clientId, requestBody)
		} catch (e: JsonProcessingException) {
			Log.e(TAG, e.message)
		}
	}

	private fun getClientNameRequestModel(firstName: String, lastName: String): ClientNameRequestModel {
		val clientNameRequestModel = ClientNameRequestModel()
		val clientUpdateMessage = ClientUpdateMessage()
		val client = Client()
		client.firstName = firstName
		client.lastName = lastName
		clientUpdateMessage.client = client
		clientNameRequestModel.clientUpdateMessage = clientUpdateMessage
		return clientNameRequestModel
	}

	override fun onNameUpdateSuccess(clientUpdateResult: ClientUpdateResult) {
		mView.onNameUpdateSuccess()
		val clientInfoService = ClientInfoService(mContext, this)
		val accessToken = AppAuthUtils.getInstance().accessToken
		clientInfoService.getClientInfo()
	}

	override fun onNameUpdateError(response: Int) {
		mView.onNameUpdateError()
		reasonFailed = RetryStatus.FAILED
		sendFailureResponse(response)
	}

	override fun onClientInfoSuccess(clientId: String, conferenceId: String) {
		mView.hideProgress()
		ClientInfoDaoUtils.getInstance().refereshClientInfoResultDao()
		mView.sendUpdateNameBroadcast()
	}

	override fun onClientInfoError(errMsg: String, response: Int) {
		mView.hideProgress()
		reasonFailed = RetryStatus.WS_CLIENT_INFO_SELF
		sendFailureResponse(response)
	}

	private fun sendFailureResponse(response: Int) {
		responseCode = response
		val mRetryStatus = RetryStatus(responseCode, reasonFailed)
		if (!responseCode.toString().startsWith(AppConstants.CODE_4) && !responseCode.toString().startsWith(AppConstants.CODE_2)) {
			mView.onServiceRetryFailed(mRetryStatus)
		}
	}

	companion object {
		private val TAG = UpdateNamePresenter::class.java.simpleName
		private val DEFAULT_MAPPER = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	}

}