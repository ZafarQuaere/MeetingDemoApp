package com.pgi.convergencemeetings.utils;

import android.text.TextUtils;

import com.google.common.base.Strings;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.Address;
import com.pgi.convergencemeetings.models.AddressDao;
import com.pgi.convergencemeetings.models.AudioDetail;
import com.pgi.convergencemeetings.models.ClientContactDetails;
import com.pgi.convergencemeetings.models.ClientContactDetailsDao;
import com.pgi.convergencemeetings.models.ClientDetails;
import com.pgi.convergencemeetings.models.ClientDetailsDao;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.convergencemeetings.models.ClientInfoResult;
import com.pgi.convergencemeetings.models.ClientInfoResultDao;
import com.pgi.convergencemeetings.models.CompanyDetails;
import com.pgi.convergencemeetings.models.CompanyDetailsDao;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.models.MeetingRoomDao;
import com.pgi.convergencemeetings.models.MeetingRoomOption;
import com.pgi.convergencemeetings.models.MeetingRoomOptionDao;
import com.pgi.convergencemeetings.models.MeetingRoomUrls;
import com.pgi.convergencemeetings.models.MeetingRoomUrlsDao;
import com.pgi.convergencemeetings.models.PhoneInformation;
import com.pgi.convergencemeetings.models.PhoneInformationDao;
import com.pgi.convergencemeetings.models.ReservationOptionsDao;
import com.pgi.convergencemeetings.models.elkmodels.AttendeeModel;
import com.pgi.logging.Logger;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ashwanikumar on 9/6/2017.
 */

public class ClientInfoDaoUtils extends ClientInfoDataSet {
    private static final String TAG = ClientInfoDaoUtils.class.getSimpleName();
    private final Logger mlogger = ConvergenceApplication.mLogger;

    private static ClientInfoDaoUtils clientInfoDaoUtils;
    private ClientInfoResult mClientInfoResult;
    private ClientContactDetailsDao clientContactDetailsDao = null;
    private ClientDetailsDao clientDetailsDao = null;
    private MeetingRoomDao meetingRoomDao = null;
    private MeetingRoomUrlsDao meetingRoomUrlsDao = null;
    private MeetingRoomOptionDao meetingRoomOptionDao = null;
    private PhoneInformationDao phoneInformationDao = null;
    private ReservationOptionsDao reservationOptionsDao = null;
    private ClientInfoResultDao clientInfoResultDao = null;
    private AddressDao addressDao = null;
    private CompanyDetailsDao companyDetailsDao = null;
    private String primaryAccessNumber;

    private ClientInfoDaoUtils() {

    }

    public static ClientInfoDaoUtils getInstance() {
        if (clientInfoDaoUtils == null) {
            clientInfoDaoUtils = new ClientInfoDaoUtils();
        }
        return clientInfoDaoUtils;
    }


    public String getEmailId() {
        String email = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {
            email = clientContactDetails.getEmail();
        }
        return email;

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

    public boolean getSoftphoneEnabled() {
        boolean softphoneEnabled = false;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            softphoneEnabled = clientDetails.getSoftPhoneEnable();
        }
        return softphoneEnabled;
    }

    public String getPrimaryAccessNumber() {
        return primaryAccessNumber;
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
                List<PhoneInformation> dailInList = prepareDialInList(primaryAccessNumber, phoneInformationList);
                if (dailInList != null && !dailInList.isEmpty()) {
                    phoneNumberList = dailInList;
                }
            }
        } else {
            if (mClientInfoResult != null) {
                List<AudioDetail> audioDetailList = mClientInfoResult.getAudioOnlyConferences();
                if (audioDetailList != null && !audioDetailList.isEmpty()) {
                    AudioDetail audioDetail = audioDetailList.get(0);
                    if (audioDetail != null) {
                        List<PhoneInformation> phoneInfoList = audioDetail.getPhoneInformation();
                        if (phoneInfoList != null && !phoneInfoList.isEmpty()) {
                            phoneNumberList = prepareDialInList("", audioDetail.getPhoneInformation());
                        }
                    }
                }
            }
        }
        return phoneNumberList;
    }

    public List<PhoneInformation> prepareDialInList(String primaryAccessNumber, List<PhoneInformation> phoneInformation) {
        List<PhoneInformation> phoneInformationWithoutVoip = new ArrayList<>();
        for (PhoneInformation phoneInfo : phoneInformation) {
            String phoneType = phoneInfo.getPhoneType();
            String phoneNumber = phoneInfo.getPhoneNumber();
            if (primaryAccessNumber.equalsIgnoreCase(phoneNumber)) {
                phoneInfo.setLocation(AppConstants.PRIMARY_ACCESS_NUM);
            }
            if (!phoneType.equalsIgnoreCase(AppConstants.TYPE_VOIP_SIP)) {
                phoneInformationWithoutVoip.add(phoneInfo);
            }

        }
            Collections.sort(phoneInformationWithoutVoip, (Comparator<PhoneInformation>) PhoneInformation::compareTo);
        return phoneInformationWithoutVoip;
    }

    public boolean getPhoneNumberAvailable() {
        boolean isPhoneNumber = false;
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
            if (mClientInfoResult != null) {
                List<AudioDetail> audioDetailList = mClientInfoResult.getAudioOnlyConferences();
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
        return isPhoneNumber;
    }

    public String getProfileImageUrl() {
        String profileImageUrl = null;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            profileImageUrl = clientDetails.getProfileImageUrl();
        }
        return profileImageUrl;
    }

    public String getFullName() {
        String fullName = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {

            fullName = clientContactDetails.getFirstName()
                    + AppConstants.EMPTY_SPACE_SYMBOL + clientContactDetails.getLastName();
        }
        return fullName;
    }

    @Override
    public String getFirstName() {

        String firstName = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {

            firstName = clientContactDetails.getFirstName();
        }
        return firstName;
    }

    public String getLastName() {

        String lastName = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {

            lastName = clientContactDetails.getLastName();
        }
        return lastName;
    }

    public Long getCompanyId() {
        long id = -1L;
        CompanyDetails companyDetails = getClientCompanyDetails();
        if (companyDetails != null) {

            id = (long) companyDetails.getCompanyId();
        }
        return id;
    }

    public String getCompany() {

        String comapanyName = null;
        CompanyDetails companyDetails = getClientCompanyDetails();
        if (companyDetails != null) {

            comapanyName = companyDetails.getName();
        }
        return comapanyName;
    }

    public String getEmail() {

        String emailId = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {

            emailId = clientContactDetails.getEmail();
        }
        return emailId;
    }

    public String getClientPhone() {

        String phoneNumber = null;
        ClientContactDetails clientContactDetails = getClientContactDetails();
        if (clientContactDetails != null) {

            phoneNumber = clientContactDetails.getPhone();
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
                    passcode = meetingRoom.getModeratorPassCode();
                }
            }
        } else {
            if (mClientInfoResult != null) {
                List<AudioDetail> audioDetailList = mClientInfoResult.getAudioOnlyConferences();
                if (audioDetailList != null && !audioDetailList.isEmpty()) {
                    AudioDetail audioDetail = audioDetailList.get(0);
                    if (audioDetail != null) {
                        List<PhoneInformation> phoneInformationList = audioDetail.getPhoneInformation();
                        if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                            PhoneInformation phoneInformation = phoneInformationList.get(0);
                            if (phoneInformation != null) {
                                phoneNumber = phoneInformation.getPhoneNumber();
                            }
                        }
                        passcode = audioDetail.getModeratorPassCode();
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


    public String getConferenceId() {
        String conferenceId = null;
        List<MeetingRoom> meetingRoomList = getMeetingRooms();
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
        if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
            for (MeetingRoom meetingRoom : meetingRoomList) {
                conferenceId = meetingRoom.getConfId();
                if (!conferenceId.equals("0")) {
                    String meetingRoomId = meetingRoom.getMeetingRoomId();
                    //Matching the conference Id with Default room setting for user....
                    String defaultMeetingRoomId = sharedPreferencesManager.getPrefDefaultMeetingRoom();
                    if (TextUtils.isEmpty(defaultMeetingRoomId) || defaultMeetingRoomId.equals(meetingRoomId)) {
                        break;
                    }
                }
            }
        }
        return conferenceId;
    }

    public Set<String> getConferenceIdSet() {
        Set<String> conferenceId = new HashSet<>();
        List<MeetingRoom> meetingRoomList = getMeetingRooms();
        if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
            for (MeetingRoom meetingRoom : meetingRoomList) {
                conferenceId.add(meetingRoom.getConfId());
            }
        }
        return conferenceId;
    }

    private ClientDetails getClientDetails() {
        ClientDetails clientDetails = null;
        if (mClientInfoResult != null) {
            clientDetails = mClientInfoResult.getClientDetails();
        }
        return clientDetails;
    }

    private CompanyDetails getClientCompanyDetails() {
        CompanyDetails companyDetails = null;
        if (mClientInfoResult != null) {
            companyDetails = mClientInfoResult.getCompanyDetails();
        }
        return companyDetails;
    }

    private ClientContactDetails getClientContactDetails() {
        ClientContactDetails clientContactDetails = null;
        ClientDetails clientDetails = getClientDetails();
        if (clientDetails != null) {
            clientContactDetails = clientDetails.getClientContactDetails();
        }
        return clientContactDetails;
    }

    @Override
    public MeetingRoom getMeetingRoomData() {
        MeetingRoom meetingRoom = null;
        List<MeetingRoom> meetingRoomList = getMeetingRooms();
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
        if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
            for (MeetingRoom mtngRoom : meetingRoomList) {
                String conferenceId = mtngRoom.getConfId();
                if (!conferenceId.equals("0")) {
                    String meetingRoomId = mtngRoom.getMeetingRoomId();
                    //Matching the conference Id with Default room setting for user....
                    String defaultMeetingRoomId = sharedPreferencesManager.getPrefDefaultMeetingRoom();
                    if (TextUtils.isEmpty(defaultMeetingRoomId) || defaultMeetingRoomId.equals(meetingRoomId)) {
                        meetingRoom = mtngRoom;
                        break;
                    }
                }
            }
        }
        return meetingRoom;
    }

    public List<MeetingRoom> getMeetingRooms() {
        List<MeetingRoom> meetingRoom = null;
        if (mClientInfoResult != null && mClientInfoResult.getMeetingRooms() != null
                && mClientInfoResult.getMeetingRooms().size() > 0) {
            meetingRoom = mClientInfoResult.getMeetingRooms();
        }
        return meetingRoom;
    }

    public void refereshClientInfoResultDao() {
        mClientInfoResult = loadClientInfo();
    }

    public ClientInfoResult getClientInfoResult() {
        return mClientInfoResult;
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

    public String getModeratorPasscode() {
        String passcode = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            passcode = meetingRoom.getModeratorPassCode();
        }
        return passcode;
    }

    public String getParticipantPasscode() {
        String passcode = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            passcode = meetingRoom.getParticipantPassCode();
        }
        return passcode;
    }

    public String getPasscode() {
        String passcode = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (meetingRoom != null) {
            passcode = meetingRoom.getModeratorPassCode();
        }
        return passcode;
    }

    private ClientInfoResult loadClientInfo() {
        ClientInfoResult clientInfoResult = null;
        ClientInfoResultDao clientInfoResultDao = ApplicationDao.get((ConvergenceApplication.appContext)).getClientResult();
        if (clientInfoResultDao != null) {
            List<ClientInfoResult> clientInfoResultList;
            try {
                clientInfoResultList = clientInfoResultDao.loadAll();
            } catch (Exception ex) {
                clientInfoResultList = null;
                mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, "ClientInfoDaoUtils " +
                    "loadClientInfo() - Exception loading clientInfo", ex, null, true, false);
            }
            if (clientInfoResultList != null && !clientInfoResultList.isEmpty()) {
                initializeClientInfoDao();
                clientInfoResult = clientInfoResultList.get(0);
                List<MeetingRoom> meetingRooms = null;
                if (meetingRoomDao != null && meetingRoomDao.loadAll().size() > 0) {
                    meetingRooms = meetingRoomDao.loadAll();
                    for (MeetingRoom meetingRoom : meetingRooms) {
                        List<MeetingRoomOption> meetingRoomOptions = null;
                        if (meetingRoomOptionDao != null && meetingRoomOptionDao.loadAll().size() > 0) {
                            meetingRoomOptions = meetingRoomOptionDao.loadAll();
                        }
                        List<PhoneInformation> phoneInformations = null;
                        if (phoneInformationDao != null && phoneInformationDao.loadAll().size() > 0) {
                            phoneInformations = phoneInformationDao.loadAll();
                        }
                        List<MeetingRoomUrls> meetingRoomUrls = null;
                        if (meetingRoomUrlsDao != null && meetingRoomUrlsDao.loadAll().size() > 0) {
                            meetingRoomUrls = meetingRoomUrlsDao.loadAll();
                        }
                        if (null != phoneInformations) {
                            List<PhoneInformation> phoneInformationList = new ArrayList<>();
                            for (PhoneInformation phoneInformation : phoneInformations) {
                                if (meetingRoom.getId().toString().equals(phoneInformation.getPhoneInformationId())) {
                                    phoneInformationList.add(phoneInformation);
                                }
                            }
                            meetingRoom.setPhoneInformation(phoneInformationList);
                        }
                        if (null != meetingRoomOptions) {
                            List<MeetingRoomOption> meetingRoomOptionsList = new ArrayList<>();
                            for (MeetingRoomOption meetingRoomOption : meetingRoomOptions) {
                                if (meetingRoom.getId().toString().equals(meetingRoomOption.getMeetingRoomOptionsId())) {
                                    meetingRoomOptionsList.add(meetingRoomOption);
                                }
                            }
                            meetingRoom.setMeetingRoomOptions(meetingRoomOptionsList);
                        }
                        if (null != meetingRoomUrls) {
                            MeetingRoomUrls meetingRoomUrl = null;
                            for (MeetingRoomUrls mtngRoomUrl : meetingRoomUrls) {
                                if (meetingRoom.getMeetingRoomUrlsId().equals(mtngRoomUrl.getId().toString())) {
                                    meetingRoomUrl = mtngRoomUrl;
                                }
                            }
                            meetingRoom.setMeetingRoomUrls(meetingRoomUrl);
                        }
                    }
                }


                ClientDetails clientDetails = null;
                if (null != clientDetailsDao && clientInfoResult != null) {
                    String idStr = clientInfoResult.getClientDetailsId();
                    Long id = CommonUtils.convertStringToLong(idStr);
                    clientDetails = clientDetailsDao.loadDeep(id);
                }

                ClientContactDetails clientContactDetails = null;
                if (clientContactDetailsDao != null && clientDetails != null) {
                    String idStr = clientDetails.getClientContactDetailsId();
                    Long id = CommonUtils.convertStringToLong(idStr);
                    clientContactDetails = clientContactDetailsDao.loadDeep(id);
                }

                CompanyDetails companyDetails = null;
                if (companyDetailsDao != null && clientInfoResult != null) {
                    String idStr = clientInfoResult.getCompanyDetailsId();
                    Long id = CommonUtils.convertStringToLong(idStr);
                    companyDetails = companyDetailsDao.load(id);
                    if (companyDetails != null) {
                        clientInfoResult.setCompanyDetails(companyDetails);
                    }
                }

                if (null != clientInfoResult && null != meetingRooms) {
                    clientInfoResult.setMeetingRooms(meetingRooms);
                }
                if (null != clientInfoResult && null != clientContactDetails) {
                    clientDetails.setClientContactDetails(clientContactDetails);
                }
                if (null != clientInfoResult && null != clientDetails) {
                    clientInfoResult.setClientDetails(clientDetails);
                }
            }
        }
        return clientInfoResult;
    }

    @Override
    public boolean isUseHtml5() {
        boolean result = false;
        List<MeetingRoom> meetingRoomList = getMeetingRooms();
        if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
            MeetingRoom meetingRoom = meetingRoomList.get(0);
            result = meetingRoom.isUseHtml5();
        }
        return result;
    }

    @Override
    public String getMeetingRoomId() {
        String meetingRoomId = null;
        MeetingRoom meetingRoom = getMeetingRoomData();
        if (null != meetingRoom && !Strings.isNullOrEmpty(meetingRoom.getMeetingRoomId())) {
            meetingRoomId = meetingRoom.getMeetingRoomId();
        }
        return meetingRoomId;
    }

    private void initializeClientInfoDao() {
        ApplicationDao applicationDao = ApplicationDao.get(ConvergenceApplication.appContext);
        if (applicationDao != null) {
            clientInfoResultDao = applicationDao.getClientResult();
            addressDao = applicationDao.getAddress();
            companyDetailsDao = applicationDao.getCompanyDetail();
            clientContactDetailsDao = applicationDao.getClientContactDetails();
            clientDetailsDao = applicationDao.getClientDetail();
            meetingRoomDao = applicationDao.getMeetingRoom();
            meetingRoomUrlsDao = applicationDao.getMeetingRoomUrls();
            meetingRoomOptionDao = applicationDao.getMeetingRoomOptions();
            phoneInformationDao = applicationDao.getPhoneInformations();
            reservationOptionsDao = applicationDao.getReservationOptions();
        }
    }

    public void insertClientInfoInDb(ClientInfoResponse clientInfoResponse) {
        try {
            //Data saved in db
            initializeClientInfoDao();

            //Remove previous data if present, this can be modified in future
            cleanOldClientInfoinDB();
            ClientInfoResult clientInfoResult = clientInfoResponse.getClientInfoResult();
            CompanyDetails companyDetails = clientInfoResult.getCompanyDetails();
            AttendeeModel.getInstance().setCompanyId(String.valueOf(companyDetails.getCompanyId()));
            ClientDetails clientDetails = clientInfoResult.getClientDetails();
            ClientContactDetails clientContactDetails = clientDetails.getClientContactDetails();
            Address address = clientContactDetails.getAddress();
            List<MeetingRoom> meetingRooms = clientInfoResult.getMeetingRooms();

            MeetingRoomUrls meetingRoomUrls;
            List<MeetingRoomOption> meetingRoomOptions;
            List<PhoneInformation> phoneInformations;

            long addressId = addressDao.insertOrReplace(address);
            clientContactDetails.setAddressId("" + addressId);

            long clientContactDetailsId = clientContactDetailsDao.insertOrReplace(clientContactDetails);
            clientDetails.setClientContactDetailsId("" + clientContactDetailsId);

            long clientDetailsId = clientDetailsDao.insertOrReplace(clientDetails);
            clientInfoResult.setClientDetailsId("" + clientDetailsId);

            long companyDetailsId = companyDetailsDao.insertOrReplace(companyDetails);
            clientInfoResult.setCompanyDetailsId("" + companyDetailsId);

            long clientInfoResultId = clientInfoResultDao.insertOrReplace(clientInfoResult);

            for (int i = 0; i < meetingRooms.size(); i++) {
                MeetingRoom meetingRoom = meetingRooms.get(i);
                //set first room as default room when inserting into database for the first time
                meetingRoomUrls = meetingRoom.getMeetingRoomUrls();
                meetingRoomOptions = meetingRoom.getMeetingRoomOptions();
                phoneInformations = meetingRoom.getPhoneInformation();

                long meetingRoomUrlId = meetingRoomUrlsDao.insertOrReplace(meetingRoomUrls);
                meetingRoom.setMeetingRoomUrlsId("" + meetingRoomUrlId);
                meetingRoom.setMeetingRoomsId("" + clientInfoResultId);
                long meetingRoomId = meetingRoomDao.insertOrReplace(meetingRoom);
                for (MeetingRoomOption meetingRoomOption : meetingRoomOptions) {
                    meetingRoomOption.setMeetingRoomOptionsId("" + meetingRoomId);
                    meetingRoomOptionDao.insertOrReplace(meetingRoomOption);
                }

                for (PhoneInformation phoneInformation : phoneInformations) {
                    phoneInformation.setPhoneInformationId("" + meetingRoomId);
                    phoneInformationDao.insertOrReplace(phoneInformation);
                }
            }
        } catch (Exception ex) {

        }
    }

    private void cleanOldClientInfoinDB() {
        if (clientInfoResultDao != null) {
            clientInfoResultDao.deleteAll();
        }
        if (addressDao != null) {
            addressDao.deleteAll();
        }
        if (clientContactDetailsDao != null) {
            clientContactDetailsDao.deleteAll();
        }
        if (clientDetailsDao != null) {
            clientDetailsDao.deleteAll();
        }
        if (companyDetailsDao != null) {
            companyDetailsDao.deleteAll();
        }
        if (meetingRoomDao != null) {
            meetingRoomDao.deleteAll();
        }
        if (meetingRoomUrlsDao != null) {
            meetingRoomUrlsDao.deleteAll();
        }
        if (meetingRoomOptionDao != null) {
            meetingRoomOptionDao.deleteAll();
        }
        if (phoneInformationDao != null) {
            phoneInformationDao.deleteAll();
        }
        if (reservationOptionsDao != null) {
            reservationOptionsDao.deleteAll();
        }
    }
}
