package com.pgi.network

import com.pgi.network.models.GMMeetingInfoGetResponse
import com.pgi.network.models.MeetingRoomInfoResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GMWebServiceAPI {

    /**
     * Provides meeting room configuration information required to start a meeting in that room.
     *
     * @param roomId      The id for the room.
     * @return the meeting information
     */
    @Headers("Accept: application/vnd.pgi.enterprisedirectory.meetings.v1+json", "Content-Type: application/vnd.pgi.enterprisedirectory.meetings.v1+json") //Premiere Global Web Services
    @GET("/REST/V1/Services/GlobalMeet.svc/MeetingRoom/{roomId}")
    fun getMeetingInformation(@Path("roomId") roomId: Int): Observable<GMMeetingInfoGetResponse?>

}