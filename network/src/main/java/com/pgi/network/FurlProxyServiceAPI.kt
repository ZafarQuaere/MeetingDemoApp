package com.pgi.network

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers


/**
 * Created by Sudheer Chilumula on 9/28/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
interface FurlProxyServiceAPI {
  /**
   * Gets resolved url.
   *
   * @return Observable<Response<String>>
   */
  @GET(".")
  @Headers("Cache-Control: no-cache")
  fun getResolvedUrl(): Observable<Response<String>>
  /**
   * Gets server.
   *
   * @return Observable<Response<String>>
   */
  @GET("/locator")
  fun getServer(): Observable<Response<String>>
}