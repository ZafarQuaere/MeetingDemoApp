package com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback

import android.util.Log
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.utils.ResultWrapper
import com.pgi.convergencemeetings.utils.makeRetrofit
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.repository.BaseRepository
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SalesForceRepository:  BaseRepository() {

    val mlogger = CoreApplication.mLogger

    private object Holder {
        val instance = SalesForceRepository()
    }

    companion object {
        val instance: SalesForceRepository by lazy {
            SalesForceRepository.Holder.instance
        }

        lateinit var service: SalesForceService
        var baseUrl: String? = null
            set(value) {
                value?.let {
                    field = it
                    service = makeRetrofit("$it/").create(SalesForceService::class.java)
                }
            }
    }

    fun createSalesForceTicket(
            email: String,
            clientId: String,
            confId: String,
            name: String,
            userType: String,
            followupEmail: String,
            browser: String="",
            browserVer: String="",
            osInfo: String="Android",
            osLanguage: String,
            productName: String="GlobalMeet - Collaboration",
            productVersion: String,
            webUrl: String,
            requesterName: String,
            meetingLang: String,
            subject: String,
            description: String,
            region: String) {
        try {
             baseUrl?.let {
                service.createSalesForceTicket(
                        it,
                        "00D30000001H6BZ",
                        "http://pgi.com",
                        "GM5 Poor Feedback",
                        "0121B000001hgNa",
                        subject,
                        description,
                        region,
                        email,
                        clientId,
                        confId,
                        name,
                        userType,
                        followupEmail,
                        "",
                        browser,
                        browserVer,
                        osInfo,
                        osLanguage,
                        productName,
                        productVersion,
                        webUrl,
                        requesterName,
                        meetingLang).subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout)).subscribe( {
                            ResultWrapper.Success(it.body())
                        }, {
                            Log.e(SalesForceRepository::class.java.simpleName, it.message)
                        })
            }
        } catch (throwable: Throwable) {
            Log.e(SalesForceRepository::class.java.simpleName, throwable.message)
            if(throwable is HttpException) {
                ResultWrapper.Error(throwable.code(), throwable)
            } else {
                ResultWrapper.NetworkError
            }
        }
    }
}