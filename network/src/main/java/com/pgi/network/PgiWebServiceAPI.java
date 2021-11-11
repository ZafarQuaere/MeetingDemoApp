package com.pgi.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This class is exposing APIs network web service request.
 */
public interface PgiWebServiceAPI {
    /**
     * Provides meeting room configuration information required to start a meeting in that room.
     *
     * @param roomId      The id for the room.
     * @return the meeting information
     */

    @Headers({
            "Accept: application/vnd.pgi.enterprisedirectory.meetings.v1+json",
            "Content-Type: application/vnd.pgi.enterprisedirectory.meetings.v1+json"
    })
    //Premiere Global Web Services
    @GET("/REST/V1/Services/GlobalMeet.svc/MeetingRoom/{roomId}")
    Call<String> getMeetingInformation(@Path("roomId") int roomId );

    /**
     * Gets meeting info.
     *
     * @param clientId       the client id
     * @param clientPassword the client password
     * @return the meeting info
     */
    @GET("/Rest/V1/Services/Security.svc/Token/New")
    Call<String> getMeetingInfo(@Query("clientId") String clientId, @Query("clientPassword") String clientPassword);

    /**
     * Gets client info pgi.
     *
     * @param includeDeleted the include deleted
     * @return the client info pgi
     */
    @GET("/REST/V1/Services/ApplicationServices.svc/ClientInfo/token=null")
    Call<String> getClientInfoPGI(@Query("includeDeleted") boolean includeDeleted);

    /**
     * Gets others client info pgi.
     *
     * @param conferenceId   the conference id
     * @param includeDeleted the include deleted
     * @return the others client info pgi
     */
    @GET("/REST/V1/Services/ApplicationServices.svc/ClientInfo/token=null")
    Call<String> getOthersClientInfoPGI(@Query("confId") String conferenceId, @Query("includeDeleted") boolean includeDeleted);

    /**
     * Gets recent meeting info.
     *
     * @param userType              the user type
     * @param clientId              the client id
     * @param maxItems              the max items
     * @param includeDeleted        the include deleted
     * @param includeExpiredInvites the include expired invites
     * @return the recent meeting info
     */
    @GET("/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/Search")
    Call<String> getRecentMeetingInfo(@Query("type") String userType, @Query("clientId") String clientId, @Query("maxItems") int maxItems, @Query("includeDeleted") boolean includeDeleted, @Query("includeExpiredInvites") boolean includeExpiredInvites);

    /**
     * Search meeting rooms call.
     *
     * @param PageSize         the page size
     * @param SortDirection    the sort direction
     * @param includeAudioOnly the include audio only
     * @return the call
     */
    @GET("/Rest/V1/Services/EnterpriseDirectory.svc/Meetings")
    Call<String> searchMeetingRooms(@Query("FirstName") String FirstName, @Query("LastName") String LastName, @Query("PageSize") int PageSize, @Query("SortDirection") String SortDirection,  @Query("IncludeAudioOnly") boolean includeAudioOnly);

    /**
     * Gets meeting info search.
     *
     * @param furl        the furl
     * @return the meeting info search
     */
    @GET("REST/V1/Services/GlobalMeet.svc/MeetingRoom/Search")
    Call<String> getMeetingInfoSearch( @Query( value = "furl", encoded = true) String furl );

    /**
     * Gets phone number.
     *
     * @param clientId    the client id
     * @return the phone number
     */
    @GET("Rest/V1/Services/GlobalMeet.svc/RegisteredUser/Get")
    Call<String> getPhoneNumber(@Query("clientId") String clientId);

    /**
     * Update last used phone call.
     *
     * @param registeredUserId the registered user id
     * @param isRegisteredUser the is registered user
     * @param countryCode      the country code
     * @param phoneNumber      the phone number
     * @param extension        the extension
     * @return the call
     */
    @PUT("Rest/V1/Services/GlobalMeet.svc/RegisteredUser/LastUsedPhone")
    Call<String> updateLastUsedPhone(@Query("registeredUserId") String registeredUserId, @Query("isRegisteredUser") boolean isRegisteredUser, @Query("countryCode") String countryCode, @Query("phoneNumber") String phoneNumber, @Query("extension") String extension);

    /**
     * Update client name call.
     *
     * @param contentType the content type
     * @param clientId    the client id
     * @param requestBody the request body
     * @return the call
     */
    @POST("REST/V1/Services/Client.svc/Client")
    Call<String> updateClientName(@Header("Content-Type") String contentType, @Query("clientId") String clientId, @Body String requestBody);

    /**
     * Update meeting call.
     *
     * @param contentType      the content type
     * @param desktopMeetingId the desktop meeting id
     * @param requestBody      the request body
     * @return the call
     */
    @POST("/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/{desktopMeetingId}")
    Call<String> updateMeeting(@Header("Content-Type") String contentType, @Path("desktopMeetingId") String desktopMeetingId, @Body String requestBody);

    /**
     * Create meeting call.
     *
     * @param contentType the content type
     * @param requestBody the request body
     * @return the call
     */
    @POST("/REST/V1/Services/GlobalMeet.svc/DesktopMeeting/New")
    Call<String> createMeeting(@Header("Content-Type") String contentType, @Body String requestBody);

    /**
     * Fetch meeting room call.
     *
     * @param contentType   the content type
     * @param meetingRoomId the meeting room id
     * @return the call
     */
    @GET("/REST/V1/Services/GlobalMeet.svc/MeetingRoom/{meetingRoomId}")
    Call<String> fetchMeetingRoom(@Header("Content-Type") String contentType, @Path("meetingRoomId") String meetingRoomId);
}
