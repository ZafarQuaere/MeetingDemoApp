package com.pgi.convergencemeetings.utils;

import android.util.Log;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.AudioDetail;
import com.pgi.convergencemeetings.models.ClientContactDetails;
import com.pgi.convergencemeetings.models.ClientDetails;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.convergencemeetings.models.ClientInfoResult;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.models.MeetingRoomUrls;
import com.pgi.convergencemeetings.models.PhoneInformation;
import com.pgi.network.models.SearchResult;
import com.pgi.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by amit1829 on 10/26/2017.
 */

public class ClientInfoResultCache extends ClientInfoDataSet {

    private static final String TAG = ClientInfoResultCache.class.getSimpleName();
   private Logger mlogger = ConvergenceApplication.mLogger;
    public static final String PHONE_INFORMATION_NOT_AVAILABLE = "phone information not available";
    private static ClientInfoResultCache sClientInfoResultCache;
    private String mSelectedMeetingRoomId;
    String primaryAccessNumber;

    private ClientInfoResultCache() {

    }

    public static ClientInfoResultCache getInstance() {
        if (sClientInfoResultCache == null) {
            sClientInfoResultCache = new ClientInfoResultCache();
        }
        return sClientInfoResultCache;
    }

    // Only for testing
    public static void setInstance(ClientInfoResultCache cache) { sClientInfoResultCache = cache; }

    private final Set<ClientInfoResponse> clientInfoResponses = new HashSet<>();

    private ClientInfoResponse getValueFromCache() {
        ClientInfoResponse clientInfoResponse = null;
        if (clientInfoResponses.iterator().hasNext()) {
            clientInfoResponse = clientInfoResponses.iterator().next();
        }
        return clientInfoResponse;
    }

    public void setValueInCache(ClientInfoResponse value) {
        removeValueFromCache();
        clientInfoResponses.add(value);
    }

    private void removeValueFromCache() {
        clientInfoResponses.clear();
    }

    public String getSipUriFromClientInfo() {
        String phoneNumber = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            List<PhoneInformation> phoneInformations = meetingRoom.getPhoneInformation();
            if (phoneInformations != null) {
                for (PhoneInformation phoneInformation : phoneInformations) {
                    String phoneType = phoneInformation.getPhoneType();
                    if (phoneType.equalsIgnoreCase(AppConstants.TYPE_VOIP_SIP)) {
                        phoneNumber = phoneInformation.getPhoneNumber();
                    }
                }
            }
        }
        return phoneNumber;
    }

    public String getPhoneNumber() {
        String phoneNumber = null;
        String passcode = null;

        MeetingRoom meetingRoom = getMeetingRoomData();
        //TODO: currently picking 1st number from phone info, this can be changed
        if (meetingRoom != null) {
            List<PhoneInformation> phoneInformationList = meetingRoom.getPhoneInformation();
            if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                PhoneInformation phoneInformation = phoneInformationList.get(0);
                if (phoneInformation != null) {
                    phoneNumber = phoneInformation.getPhoneNumber();
                    passcode = meetingRoom.getParticipantPassCode();
                }
            }
        } else {
            ClientInfoResult clientInfoResult = getClientInfoResult();
            if (clientInfoResult != null) {
                AudioDetail audioDetail = clientInfoResult.getAudioOnlyConferences().get(0);
                if (audioDetail != null) {
                    List<PhoneInformation> phoneInformationList = audioDetail.getPhoneInformation();
                    if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                        PhoneInformation phoneInformation = phoneInformationList.get(0);
                        if (phoneInformation != null) {
                            phoneNumber = phoneInformation.getPhoneNumber();
                            if (phoneNumber != null) {
                                passcode = audioDetail.getParticipantPassCode();
                            }
                        }
                    }
                }
            }
        }

        if (phoneNumber == null) {
            return null;
        } else {
            return CommonUtils.getPGIFormattedNumber(phoneNumber, passcode);
        }
    }

    public String getPasscode() {
        String passcode = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            passcode = meetingRoom.getParticipantPassCode();
        }
        return passcode;
    }

    @Override
    public MeetingRoom getMeetingRoomData() {
        MeetingRoom meetingRoom = null;
        ClientInfoResult clientInfoResult = getClientInfoResult();
        if (clientInfoResult != null && clientInfoResult.getMeetingRooms() != null
                && clientInfoResult.getMeetingRooms().size() > 0) {
            List<MeetingRoom> meetingRooms = clientInfoResult.getMeetingRooms();
            for (MeetingRoom mtngRoom : meetingRooms) {
                if (mtngRoom.getMeetingRoomId().equals(mSelectedMeetingRoomId)) {
                    meetingRoom = mtngRoom;
                    break;
                }
            }
        }
        return meetingRoom;
    }

    public ClientInfoResult getClientInfoResult() {
        ClientInfoResult clientInfoResult = null;
        ClientInfoResponse clientInfoResponse = getValueFromCache();
        if (clientInfoResponse != null) {
            clientInfoResult = clientInfoResponse.getClientInfoResult();
        }
        return clientInfoResult;
    }

    public ClientDetails getClientDetails() {
        ClientDetails clientDetails = null;
        ClientInfoResult clientInfoResult = getClientInfoResult();
        if (clientInfoResult != null) {
            clientDetails = clientInfoResult.getClientDetails();
        }
        return clientDetails;
    }

    public ClientContactDetails getClientContactDetails() {
        ClientContactDetails clientContactDetail = null;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            clientContactDetail = clientDetails.getClientContactDetails();
        }
        return clientContactDetail;
    }

    @Override
    public String getFirstName() {
        String firstName = null;
        ClientContactDetails clientContactDetail = getClientContactDetails();
        if (clientContactDetail != null) {
            firstName = clientContactDetail.getFirstName();
        }
        return firstName;
    }


    public String getProfileUrl() {
        String profileImage = null;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            profileImage = clientDetails.getProfileImageUrl();
        }
        return profileImage;
    }

    public String getLastName() {
        String lastName = null;
        ClientContactDetails clientContactDetail = getClientContactDetails();
        if (clientContactDetail != null) {
            lastName = clientContactDetail.getLastName();
        }
        return lastName;
    }


    public boolean getSoftphoneEnabled() {
        boolean softphoneEnabled = false;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            softphoneEnabled = clientDetails.getSoftPhoneEnable();
        }
        return softphoneEnabled;
    }

    @Override
    public List<PhoneInformation> getPhoneNumbersList() {
        List<PhoneInformation> phoneNumberList = new ArrayList<>();
        MeetingRoom meetingRoom = getMeetingRoomData();
        //TODO: currently picking 1st number from phone info, this can be changed
        if (meetingRoom != null) {
            primaryAccessNumber = meetingRoom.getPrimaryAccessNumber();
            List<PhoneInformation> phoneInformationList = meetingRoom.getPhoneInformation();
            if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                List<PhoneInformation> dialInList = prepareDialInList(primaryAccessNumber, phoneInformationList);
                if (dialInList != null && !dialInList.isEmpty()) {
                    phoneNumberList = dialInList;
                }
            }
        } else {
            ClientInfoResult clientInfoResult = getClientInfoResult();
            if (clientInfoResult != null) {
                List<AudioDetail> audioDetailList = clientInfoResult.getAudioOnlyConferences();
                if (audioDetailList != null && !audioDetailList.isEmpty()) {
                    AudioDetail audioDetail = audioDetailList.get(0);
                    if (audioDetail != null) {
                        List<PhoneInformation> phoneInformationList = audioDetail.getPhoneInformation();
                        if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                            phoneNumberList = prepareDialInList("", audioDetail.getPhoneInformation());
                        }
                    }
                }
            }
        }
        return phoneNumberList;
    }

    private List<PhoneInformation> prepareDialInList(String
                                                             primaryAccessNumber, List<PhoneInformation> phoneInformation) {
        List<PhoneInformation> phoneInformationWithoutVoip = new ArrayList<>();
        for (PhoneInformation phoneInfo : phoneInformation) {
            if (phoneInfo != null) {
                String phoneType = phoneInfo.getPhoneType();
                String phoneNumber = phoneInfo.getPhoneNumber();
                if (primaryAccessNumber.equalsIgnoreCase(phoneNumber)) {
                    phoneInfo.setLocation(AppConstants.PRIMARY_ACCESS_NUM);
                }
                if (!phoneType.equalsIgnoreCase(AppConstants.TYPE_VOIP_SIP)) {
                    phoneInformationWithoutVoip.add(phoneInfo);
                }
            } else {
                Log.d(TAG, PHONE_INFORMATION_NOT_AVAILABLE);
            }
        }
        Collections.sort(phoneInformationWithoutVoip);
        return phoneInformationWithoutVoip;
    }

    public boolean getPhoneNumberAvailable() {
        boolean isPhoneNumber = false;
        try {
            MeetingRoom meetingRoom = getMeetingRoomData();
            if (meetingRoom != null) {
                List<PhoneInformation> phoneInformationList = meetingRoom.getPhoneInformation();
                if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                    for (PhoneInformation phoneInformation : phoneInformationList) {
                        if (phoneInformation != null && phoneInformation.getPhoneNumber() != null) {
                            String phoneType = phoneInformation.getPhoneType();
                            if (phoneType != null && !phoneType.toLowerCase().contains(AppConstants.VOIP_CONSTANT)) {
                                isPhoneNumber = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                ClientInfoResult clientInfoResult = getClientInfoResult();
                if (clientInfoResult != null) {
                    List<AudioDetail> audioDetailList = clientInfoResult.getAudioOnlyConferences();
                    if (audioDetailList != null && !audioDetailList.isEmpty()) {
                        AudioDetail audioDetail = audioDetailList.get(0);
                        if (audioDetail != null) {
                            List<PhoneInformation> phoneInformationList = audioDetail.getPhoneInformation();
                            if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                                PhoneInformation phoneInformation = phoneInformationList.get(0);
                                if (phoneInformation != null && phoneInformation.getPhoneNumber() != null) {
                                    isPhoneNumber = true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getStackTrace()[0].getMethodName() + " " + ex.getMessage();
            Log.e(TAG, msg);
//            mlogger.error(TAG, msg, ex);
        }
        return isPhoneNumber;
    }

    public String getConferenceId() {
        String confId = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        AudioDetail audioDetail = getAudioOnlyConference();
        if (meetingRoom != null) {
            confId = meetingRoom.getConfId();
        } else if (audioDetail != null) {
            confId = audioDetail.getConfId();
        }
        return confId;
    }

    public String getFurl() {
        String fURL = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            MeetingRoomUrls meetingRoomUrls = meetingRoom.getMeetingRoomUrls();
            if (meetingRoomUrls != null) {
                fURL = meetingRoomUrls.getAttendeeJoinUrl();
            }
        }
        return fURL;
    }

    public AudioDetail getAudioOnlyConference() {
        AudioDetail audioDetail = null;
        ClientInfoResult clientInfoResult = getClientInfoResult();
        if (clientInfoResult != null) {
            List<AudioDetail> audioDetailList = clientInfoResult.getAudioOnlyConferences();
            if (audioDetailList != null && !audioDetailList.isEmpty()) {
                audioDetail = audioDetailList.get(0);
            }
        }
        return audioDetail;
    }

    public SearchResult getSearchResult() {
        SearchResult searchResult = new SearchResult();
        try {
            searchResult.setFurl(getFurl());
            searchResult.setFirstName(getFirstName());
            searchResult.setLastName(getLastName());
            searchResult.setProfileImageUrl(getProfileUrl());
            searchResult.setFavorite(false);
            MeetingRoom meetingRoom = getMeetingRoomData();
            if (meetingRoom != null) {
                String meetingRoomId = meetingRoom.getMeetingRoomId();
                if (meetingRoomId != null) {
                    searchResult.setHubConfId(Integer.parseInt(meetingRoomId));
                }
            }
            String conferenceId = getConferenceId();
            if (conferenceId != null) {
                searchResult.setConferenceId(Integer.parseInt(conferenceId));
            }
            searchResult.setModifiedDate(Long.toString(System.currentTimeMillis()));
            searchResult.setUseHtml5(isUseHtml5());
        } catch (Exception ex) {
            String msg = ex.getStackTrace()[0].getMethodName() + " " + ex.getMessage();
//            mlogger.error(TAG, msg, ex);
        }
        return searchResult;
    }

    @Override
    public String getClientId() {
        String clientId = null;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            clientId = clientDetails.getClientId();
        }
        return clientId;
    }

    @Override
    public boolean isUseHtml5() {
        boolean result = false;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            result = meetingRoom.isUseHtml5();
        }
        return result;
    }

    @Override
    public String getMeetingRoomId() {
        String meetingRoomId = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (null != meetingRoom && null != meetingRoom.getMeetingRoomId() && !AppConstants.EMPTY_STRING.equals(meetingRoom.getMeetingRoomId())) {
            meetingRoomId = meetingRoom.getMeetingRoomId();
        }
        return meetingRoomId;
    }

    public String getPrimaryAccessNumber() {
        return primaryAccessNumber;
    }

    public String getSelectedMeetingRoomId() {
        return mSelectedMeetingRoomId;
    }

    public void setSelectedMeetingRoomId(String selectedMeetingRoomId) {
        this.mSelectedMeetingRoomId = selectedMeetingRoomId;
    }
}
