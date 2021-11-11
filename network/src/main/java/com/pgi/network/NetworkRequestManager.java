package com.pgi.network;


import android.util.Log;

import com.pgi.network.helper.RetryAfterTimeoutWithDelay;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.Request;
import retrofit2.Call;

/**
 * This class is used by service class to consume web service for network request.
 */
public class NetworkRequestManager {

    public static final int MAX_ITEMS = 500;
    private static final String TAG = NetworkRequestManager.class.getSimpleName();
    public static final boolean INCLUDE_DELETED = false;
    public static final String USER_TYPE_CLIENT = "Client";
    public static final boolean INCLUDE_EXPIRED_INVITES = false;
    public static final String APPLICATION_JSON = "application/json";
    private String nullString = "null";

    private NetworkResponseHandler mNetworkResponseHandler;

    /**
     * Instantiates a new Network request manager.
     *
     * @param networkResponseHandler the network response handler
     */
    public NetworkRequestManager(NetworkResponseHandler networkResponseHandler) {
        this.mNetworkResponseHandler = networkResponseHandler;
    }

    /**
     * Requesting to network to get logedin user information from network. Information will deliver through callback.
     *
     */
    public void getClientInfo() {
         final PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
                    Call<String> call = pgiWebServiceAPI.getClientInfoPGI(false);
                    call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Gets others client info from network.
     *
     * @param conferenceId
     */
    public void getOthersClientInfo(String conferenceId) {
        final PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
                    Call<String> call = pgiWebServiceAPI.getOthersClientInfoPGI(conferenceId, false);
                    call.enqueue(mNetworkResponseHandler);

        }
    }

    /**
     * Gets recent meeting info from network and response will deliver throufh callback.
     *  @param clientId    the client id
     *
     */
    public void getRecentMeetingInfo(String clientId) {
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.getRecentMeetingInfo(USER_TYPE_CLIENT, clientId, MAX_ITEMS, INCLUDE_DELETED, INCLUDE_EXPIRED_INVITES);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Search meeting rooms request sending to server and response will deliver through callback.
     *  @param firstName the first name
     * @param lastName optional last name
     * @param maxResults  the max results
     * @param sortOrder   the sort order
     */
    public void searchMeetingRooms(String firstName, String lastName, int maxResults, String sortOrder) {
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.searchMeetingRooms(firstName, lastName, maxResults, sortOrder,false);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Gets phone number request sending to server to get the phone number of user.
     *  @param clientId    the client id
     *
     */
    public void getPhoneNumber(String clientId) {
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.getPhoneNumber(clientId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Update last used phone number into server.
     *  @param registeredUserId the registered user id
     * @param isRegisteredUser the is registered user
     * @param countryCode      the country code
     * @param phoneNumber      the phone number
     * @param extension        the extension
     */
    public void updateLastUsedPhoneNumber(String registeredUserId, boolean isRegisteredUser, String countryCode, String phoneNumber, String extension) {
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.updateLastUsedPhone(registeredUserId, isRegisteredUser, countryCode, phoneNumber, extension);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Update client name into server.
     *  @param clientId    the client id
     * @param requestBody the request body
     */
    public void updateClientName(String clientId, String requestBody) {
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.updateClientName(APPLICATION_JSON, clientId, requestBody);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Getting updated meeting details from server to show it to client.
     *  @param desktopMeetingId the desktop meeting id
     * @param requestBody      the request body
     */
    public void updateMeeting(String desktopMeetingId, String requestBody){
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.updateMeeting(APPLICATION_JSON, desktopMeetingId, requestBody);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Create meeting for user when user start the meeting.
     *
     * @param requestBody the request body
     */
    public void createMeeting(String requestBody){
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.createMeeting(APPLICATION_JSON, requestBody);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Fetch meeting room from server and details show to client.
     *
     * @param meetingRoomId the meeting room id
     */
    public void fetchMeetingRoom(String meetingRoomId){
        PgiWebServiceAPI pgiWebServiceAPI = PgiWebServiceManager.getInstance().getPgiWebServiceAPI();
        if (pgiWebServiceAPI != null) {
            Call<String> call = pgiWebServiceAPI.fetchMeetingRoom(APPLICATION_JSON, meetingRoomId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Audio meeting Subscribe sending to server and getting the details of meeting.
     *
     * @param pgiToken the pgi token
     * @param clientId the client id
     */
// PIA Service
    public void subscribeToAudioMeeting(String pgiToken, String clientId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.subscribePiaAudio(pgiToken, clientId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Gets conference state from server of specific meeting.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void getConferenceState(String sessionId, String conferenceId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.getConferenceState(sessionId, conferenceId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Send logs to network.
     *
     * @param logSessionType the log session type
     * @param cookie         the cookie
     * @param logs           the logs
     */
    public void sendLogs(String logSessionType, String cookie, String logs) {
        try {
            LoggerServiceAPI loggerServiceAPI = LoggerServiceManager.getInstance().getLoggerServiceAPI();
            if (loggerServiceAPI != null) {
                Call<String> call = loggerServiceAPI.sendLogs("application/json", logSessionType, cookie, logs);
                call.enqueue(mNetworkResponseHandler);
                Request request = call.request();
                if (request != null) {
                    Headers headers = request.headers();
                    if (headers != null) {
                        String header = headers.toString();
                        String msg = "sendLogs: hdr=" + header;
                        Log.e(TAG, msg);
                    }
                }
            }
        } catch (Exception ex)
        {
            Log.e(TAG, "sendLogs");
        }
    }

    /**
     * Sets conference watch.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void setConferenceWatch(String sessionId, String conferenceId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.setConferenceWatch(sessionId, conferenceId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Un subscribe pia session.
     *
     * @param sessionId the session id
     */
    public void unSubscribePIASession(String sessionId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.unSubscribeAudioOnly(sessionId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Sets conference option.
     *
     * @param sessionId        the session id
     * @param conferenceId     the conference id
     * @param conferenceOption the conference option
     */
    public void setConferenceOption(String sessionId, String conferenceId, String conferenceOption) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.setConferenceOption(sessionId, conferenceId, conferenceOption);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Start conference.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void startConference(String sessionId, String conferenceId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.startConference(sessionId, conferenceId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * End conference.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void endConference(String sessionId, String conferenceId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.endConference(sessionId, conferenceId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Leave conference.
     *
     * @param sessionId      the session id
     * @param conferenceId   the conference id
     * @param participantsId the participants id
     */
    public void leaveConference(String sessionId, String conferenceId, String participantsId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.leaveConference(sessionId, conferenceId, participantsId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Sets participant option.
     *
     * @param sessionId     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @param option        the option
     */
    public void setParticipantOption(String sessionId, String conferenceId, String participantId, String option) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.setParticipantOption(sessionId, conferenceId, participantId, option);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * request room type
     * @param furl the full url for the meeting room
     */
    public void getRoomType(String furl) {
        RoomTypeServiceManager roomTypeServiceManager = new RoomTypeServiceManager(furl);
        RoomTypeServiceAPI roomTypeServiceAPI = roomTypeServiceManager.getRoomTypeServiceAPI();
        if (roomTypeServiceAPI != null) {
            Call<String> call = roomTypeServiceAPI.getRoomType(furl);
            call.enqueue(mNetworkResponseHandler);
        }

    }

    /**
     * Clear conference watch.
     *
     * @param sessionId    the session id
     * @param conferenceId the conference id
     */
    public void clearConferenceWatch(String sessionId, String conferenceId) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.clearConferenceWatch(sessionId, conferenceId);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Sets participant info.
     *
     * @param sessionId     the session id
     * @param conferenceId  the conference id
     * @param participantId the participant id
     * @param firstName     the first name
     * @param lastName      the last name
     * @param companyName   the company name
     * @param phoneNumber   the phone number
     * @param emailAddress  the email address
     */
    public void setParticipantInfo(String sessionId, String conferenceId, String participantId, String firstName, String lastName, String companyName, String phoneNumber, String emailAddress) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.setParticipantInfo(sessionId, conferenceId, participantId, firstName, lastName, companyName, phoneNumber, emailAddress);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Dial out to participant.
     *
     * @param sessionId       the session id
     * @param conferenceId    the conference id
     * @param phoneNumber     the phone number
     * @param participantType the participant type
     * @param dialOutType     the dial out type
     * @param firstName       the first name
     * @param lastName        the last name
     */
    public void dialOutToParticipant(String sessionId, String conferenceId, String phoneNumber, String participantType, String dialOutType, String firstName, String lastName) {
        PiaServiceAPI piaServiceAPI = PiaServiceManager.getInstance().getPiaServiceAPI();
        if (piaServiceAPI != null) {
            Call<String> call = piaServiceAPI.dialOutToParticipant(sessionId, conferenceId, phoneNumber, participantType, dialOutType, firstName, lastName);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Send forgot password request.
     *
     * @param email        the email
     * @param authByClient the auth by client
     */
//Identity Services
    public void sendForgotPasswordRequest(String email, String authByClient) {
        IdentityServiceAPI identityServiceAPI = IdentityServiceManager.getInstance().getIdentityServiceAPI();
        if (identityServiceAPI != null) {
            Call<String> call = identityServiceAPI.forgotPasswordRequest(email, authByClient);
            call.enqueue(mNetworkResponseHandler);
        }
    }

    /**
     * Logout user.
     *
     * @param authByClient the auth by client
     */
    public Observable<String> logoutUser(final boolean authByClient) {
         final IdentityServiceAPI identityServiceAPI = IdentityServiceManager.getInstance().getIdentityServiceAPI();
        return identityServiceAPI.logoutUser(authByClient)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(new RetryAfterTimeoutWithDelay(3, 1000, 1000));
    }

    /**
     * Revoke user token.
     *
     * @param refreshToken the refresh token
     */
    public Observable<String> revokeUserToken(String refreshToken) {
        final IdentityServiceAPI identityServiceAPI = IdentityServiceManager.getInstance().getIdentityServiceAPI();
        return identityServiceAPI.revokeUserToken(refreshToken)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .retryWhen(new RetryAfterTimeoutWithDelay(3, 1000, 1000));
    }

    /**
     * Get user profileImage.
     *
     */
    public void getUserProfileImage() {
        final IdentityServiceAPI identityServiceAPI = IdentityServiceManager.getInstance().getIdentityServiceAPI();
        if (identityServiceAPI != null) {
                    Call<String> call = identityServiceAPI.getProfileImage();
                    call.enqueue(mNetworkResponseHandler);

        }
    }
}
