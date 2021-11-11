package com.pgi.network.repository

import com.pgi.network.GMWebServiceManager
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.models.GMMeetingInfoGetResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class GMWebServiceRepository: BaseRepository(), KoinComponent {

    private val gmWebServiceManager: GMWebServiceManager by inject()

    fun getMeetingInformation(roomId: Int): Observable<GMMeetingInfoGetResponse?> {
        return gmWebServiceManager.gmWebServiceAPI.getMeetingInformation(roomId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
    }
}