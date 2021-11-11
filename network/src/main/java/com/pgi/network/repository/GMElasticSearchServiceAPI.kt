package com.pgi.network.repository

import com.pgi.network.models.MeetingRoomInfo
import com.pgi.network.models.SearchResponse
import com.pgi.network.models.SuggestResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GMElasticSearchServiceAPI {

    @GET("/suggest")
    fun getSuggestResults(@Query(value="q", encoded=true) searchText: String, @Query("nameOrder") nameOrder: String, @Query("size") size: Int, @Query("hl") highlight: Boolean): Observable<List<SuggestResponse>>

    @GET("/search")
    fun getSearchResults(@Query("q") searchText: String, @Query("nameOrder") nameOrder: String, @Query("size") size: Int, @Query("from") from: Int, @Query("idsOnly") idsOnly: Boolean): Observable<SearchResponse>


    @GET("/meetingrooms/{meetingRoomId}")
    fun getMeetingRoomInfoFromRoomID(@Path("meetingRoomId") roomId: Int, @Query("idsOnly") idsOnly: Boolean): Observable<MeetingRoomInfo>


    @GET("/meetingrooms/getByUrl")
    fun getMeetingRoomInfoFromFURL(@Query(value="url", encoded=true) furl: String): Observable<MeetingRoomInfo>
}