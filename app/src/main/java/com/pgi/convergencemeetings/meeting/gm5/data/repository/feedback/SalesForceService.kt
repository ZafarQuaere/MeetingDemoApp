package com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface SalesForceService {

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST()
    fun createSalesForceTicket(
            @Url url: String,
            @Field("orgid") orgid: String,
            @Field("retURL") retURL: String,
            @Field("origin") origin: String,
            @Field("recordType") recordType: String,
            @Field("subject") subject: String,
            @Field("description") description: String,
            @Field("00N1300000BD48x") region: String,
            @Field("00N1300000BCzNw") email: String,
            @Field("00N1B00000AxTvx") clientid: String,
            @Field("00N1B00000AxTw2") confId: String,
            @Field("00N1B00000AxQCc") name: String,
            @Field("00N1B00000AxU3C") userType: String,
            @Field("00N1B00000AxQD3") followupEmail: String,
            @Field("00N1B00000AxQD4") userPhone: String,
            @Field("00N1B00000AxQCb") browser: String,
            @Field("00N1B00000AxVFo") browserVer: String,
            @Field("00N1B00000AxQCs") osInfo: String,
            @Field("00N1B00000AxQCq") osLanguage: String,
            @Field("00N1B00000AxQDi") productName: String,
            @Field("00N1B00000AxQEM") productVersion: String,
            @Field("00N1B00000AxRAt") webUrl: String,
            @Field("00N1B00000AxU3X") requesterName: String,
            @Field("00N1B00000AxQER") meetingLang: String
    ): Observable<Response<Unit>>
}
