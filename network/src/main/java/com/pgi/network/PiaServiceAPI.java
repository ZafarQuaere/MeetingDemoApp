package com.pgi.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * This class is used by {@link PiaServiceManager} to provide instance its implementation.
 */
public interface PiaServiceAPI{
//    "application/json","application/json;charset=utf-8"

    /**
     * Subscribe pia audio call.
     *
     * @param pgiToken the pgi token
     * @param clientId the client id
     * @return the call
     */
    @Headers({
            "Accept: application/json;charset=utf-8",
            "Content-Type: application/json;charset=utf-8"
    })

    @GET("/2.0/PiaRestWebServices.svc/subscribe")
    Call<String> subscribePiaAudio(@Query("pgiToken") String pgiToken, @Header("PGI-XCLIENT") String clientId);

    /**
     * Start polling call.
     *
     * @param sessionid   the sessionid
     * @param accessToken the access token
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/poll")
    Call<String> startPolling(@Query("sessionid") String sessionid, @Header("Authorization") String accessToken);

    /**
     * Sets conference watch.
     *
     * @param sessionid    the sessionid
     * @param conferenceId the conference id
     * @param accessToken  the access token
     * @return the conference watch
     */
    @GET("/2.0/PiaRestWebServices.svc/setconferencewatch")
    Call<String> setConferenceWatch(@Query("sessionid") String sessionid, @Query("conferenceId") String conferenceId, @Header("Authorization") String accessToken);

    /**
     * Sets conference option.
     *
     * @param sessionid    the sessionid
     * @param conferenceId the conference id
     * @param option       the option
     * @return the conference option
     */
    @GET("/2.0/PiaRestWebServices.svc/setconferenceoption")
    Call<String> setConferenceOption(@Query("sessionid") String sessionid, @Query("conferenceId") int conferenceId, @Query("option") String option);


    /**
     * Cancel audio only dial out call.
     *
     * @param sessionid     the sessionid
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @return the call
     */
    @GET("/PiaRestWebServices.svc/canceldialout")
    Call<String> cancelAudioOnlyDialOut(@Query("sessionid") String sessionid, @Query("conferenceId") int conferenceId, @Query("participantId") String participantId);

    /**
     * Un subscribe audio only call.
     *
     * @param sessionID the session id
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/unsubscribe")
    Call<String> unSubscribeAudioOnly(@Query("sessionid") String sessionID);

    /**
     * Gets conference state.
     *
     * @param sessionID    the session id
     * @param conferenceId the conference id
     * @return the conference state
     */
    @GET("/2.0/PiaRestWebServices.svc/getconferencestate")
    Call<String> getConferenceState(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId);

    /**
     * Sets conference watch.
     *
     * @param sessionID    the session id
     * @param conferenceId the conference id
     * @return the conference watch
     */
    @GET("/2.0/PiaRestWebServices.svc/setconferencewatch")
    Call<String> setConferenceWatch(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId);

    /**
     * Start conference call.
     *
     * @param sessionID    the session id
     * @param conferenceId the conference id
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/startconference")
    Call<String> startConference(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId);

    /**
     * Sets conference option.
     *
     * @param sessionID        the session id
     * @param conferenceId     the conference id
     * @param conferenceOption the conference option
     * @return the conference option
     */
    @GET("/2.0/PiaRestWebServices.svc/setconferenceoption")
    Call<String> setConferenceOption(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId, @Query("option") String conferenceOption);

    /**
     * Sets participant option.
     *
     * @param sessionID     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @param option        the option
     * @return the participant option
     */
    @GET("/2.0/PiaRestWebServices.svc/setparticipantoption")
    Call<String> setParticipantOption(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId, @Query("participantid") String participantId, @Query("option") String option);

    /**
     * Leave conference call.
     *
     * @param sessionID     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/hangup")
    Call<String> leaveConference(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId, @Query("participantId") String participantId);

    /**
     * End conference call.
     *
     * @param sessionID    the session id
     * @param conferenceId the conference id
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/endconference")
    Call<String> endConference(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId);

    /**
     * Clear conference watch call.
     *
     * @param sessionID    the session id
     * @param conferenceId the conference id
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/clearconferencewatch")
    Call<String> clearConferenceWatch(@Query("sessionid") String sessionID, @Query("conferenceId") String conferenceId);

    /**
     * Sets participant info.
     *
     * @param sessionID     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @param firstName     the first name
     * @param lastName      the last name
     * @param companyName   the company name
     * @param phoneNumber   the phone number
     * @param emailAddress  the email address
     * @return the participant info
     */
    @GET("/2.0/PiaRestWebServices.svc/setparticipantinfo")
    Call<String> setParticipantInfo(@Query("sessionid")String sessionID, @Query("conferenceId") String conferenceId, @Query("participantId") String participantId, @Query("firstname") String firstName,@Query("lastname") String lastName,@Query("companyname") String companyName,@Query("phonenumber") String phoneNumber,@Query("emailaddress") String emailAddress);

    /**
     * Dial out to participant call.
     *
     * @param sessionid       the sessionid
     * @param conferenceId    the conference id
     * @param phonenumber     the phonenumber
     * @param participanttype the participanttype
     * @param dialouttype     the dialouttype
     * @param firstname       the firstname
     * @param lastname        the lastname
     * @return the call
     */
    @GET("/2.0/PiaRestWebServices.svc/dialout")
    Call<String> dialOutToParticipant(@Query("sessionid") String sessionid, @Query("conferenceId") String conferenceId, @Query("phonenumber") String phonenumber, @Query("participanttype") String participanttype, @Query("dialouttype") String dialouttype, @Query("firstname") String firstname, @Query("lastname") String lastname);

}
