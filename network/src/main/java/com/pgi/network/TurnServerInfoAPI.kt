package com.pgi.network

import com.pgi.network.models.TurnServerInfo
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface TurnServerInfoAPI {
  /**
   * Gets Turn Server Info.
   *
   * @return Observable<Response<String>>
   */
  @GET(".")
  @Headers("Cache-Control: no-cache")
  fun getTurnServerInfo(): Observable<TurnServerInfo>
}