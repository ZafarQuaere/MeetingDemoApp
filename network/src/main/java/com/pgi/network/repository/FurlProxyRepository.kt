package com.pgi.network.repository

import android.util.Log
import com.pgi.network.FurlProxyServiceManager
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


/**
 * Created by Sudheer Chilumula on 9/28/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class FurlProxyRepository private constructor() : BaseRepository() {

  private object Holder {
    val instance = FurlProxyRepository()
  }

  companion object {
    val instance: FurlProxyRepository by lazy {
      Holder.instance
    }
  }

  public var furlProxyServiceManager = FurlProxyServiceManager()

  fun resolveFurl(furl: String): Observable<Response<String>> {
    try {
      if (furlProxyServiceManager != null) {
        return furlProxyServiceManager.create(furl).getResolvedUrl()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout))
      }
    } catch (e:Exception) {
      Log.e("FurlProxyRepository.kt", e.message.toString())
    }
    val furlResponse = Response.success("success")
    return Observable.just(furlResponse)
  }

  fun getMeetingServer(furl: String): Observable<Response<String>> {
    return furlProxyServiceManager.create(furl).getServer()
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .retryWhen(RetryAfterTimeoutWithDelay(0, retryTimeout))
  }
}