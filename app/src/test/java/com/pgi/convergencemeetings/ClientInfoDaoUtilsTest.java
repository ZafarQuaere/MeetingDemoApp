package com.pgi.convergencemeetings;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.AddressDao;
import com.pgi.convergencemeetings.models.ClientContactDetailsDao;
import com.pgi.convergencemeetings.models.ClientDetails;
import com.pgi.convergencemeetings.models.ClientDetailsDao;
import com.pgi.convergencemeetings.models.ClientInfoResponse;
import com.pgi.convergencemeetings.models.ClientInfoResult;
import com.pgi.convergencemeetings.models.ClientInfoResultDao;
import com.pgi.convergencemeetings.models.CompanyDetails;
import com.pgi.convergencemeetings.models.CompanyDetailsDao;
import com.pgi.convergencemeetings.models.DaoSession;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.models.MeetingRoomDao;
import com.pgi.convergencemeetings.models.MeetingRoomOptionDao;
import com.pgi.convergencemeetings.models.MeetingRoomUrlsDao;
import com.pgi.convergencemeetings.models.PhoneInformation;
import com.pgi.convergencemeetings.models.PhoneInformationDao;
import com.pgi.convergencemeetings.models.ReservationOptionsDao;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwanikumar on 9/11/2017.
 */
@PrepareForTest({CommonUtils.class, ApplicationDao.class, SharedPreferencesManager.class})
public class ClientInfoDaoUtilsTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private Context context;
    private ClientInfoDaoUtils clientInfoDaoUtils;
    private ClientInfoResultDao clientInfoResultDao;
    private AddressDao addressDao;
    private CompanyDetailsDao companyDetailsDao;
    private ClientContactDetailsDao clientContactDetailsDao;
    private ClientDetailsDao clientDetailsDao;
    private MeetingRoomDao meetingRoomDao;
    private MeetingRoomUrlsDao meetingRoomUrlsDao;
    private MeetingRoomOptionDao meetingRoomOptionDao;
    private PhoneInformationDao phoneInformationDao;
    private ReservationOptionsDao reservationOptionsDao;
    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    private CompanyDetails mockCompanyDetail = null;


    private String clientInfoResultStr = "{\"ExecutionTime\":500,\"MessageId\":\"ea66ec05-0abf-4ba5-86de-d8c13ad162d8\",\"ServerDateTime\":\"/Date(1507536609671)/\",\"AudioOnlyConferences\":[],\"ClientDetails\":{\"Active\":true,\"BillToId\":8291321,\"ClientContactDetails\":{\"Address\":{\"id\":1},\"Email\":\"Abhishek.Gupta@pgi.com\",\"Fax\":\"\",\"FaxIntlPrefix\":\"\",\"FirstName\":\"Abhishek\",\"HomePhone\":\"\",\"HomePhoneExt\":\"\",\"HomePhoneIntlPrefix\":\"\",\"HomePhoneIsDefault\":false,\"JobTitle\":\"\",\"LastName\":\"Gupta\",\"MobilePhone\":\"\",\"MobilePhoneExt\":\"\",\"MobilePhoneIntlPrefix\":\"\",\"MobilePhoneIsDefault\":false,\"Phone\":\"\",\"PhoneExt\":\"\",\"PhoneIntlPrefix\":\"\",\"PhoneIsDefault\":false,\"SecondaryPhoneIsDefault\":false,\"id\":1,\"addressId\":\"1\"},\"ClientId\":\"2690509\",\"DefaultLanguage\":\"\",\"Hold\":false,\"OperationComments\":\"\",\"Password\":\"jeM0659Q\",\"PasswordCanBeModified\":false,\"PromotionCodes\":[\"\"],\"SoftPhoneAutoConnect\":false,\"SoftPhoneEnable\":true,\"SpecialInfo\":\"\",\"SubscriptionId\":\"\",\"TerritoryCode\":\"D0938\",\"id\":1,\"clientContactDetailsId\":\"1\"},\"CompanyDetails\":{\"BlockedServices\":[\"Acrobat Connect Professional\",\"Readycast Demo (Internal Use Only)\",\"ReadyCast Protect\",\"Visioncast Meeting Demo (Internal Use Only)\",\"iMeet (Visual Conferencing)\",\"International Moderator Dial Out\",\"LotusLiveMeeting\",\"LotusLiveMeetingDemo (Internal Use Only)\"],\"CompanyId\":635623,\"Name\":\"MuleSoft\",\"id\":1},\"ExcessiveConfs\":[\"\"],\"MeetingRooms\":[{\"Availability\":\"Wait For Moderator\",\"ConfId\":\"999965210\",\"ConferenceName\":\"Abhishek's Meeting\",\"Deleted\":false,\"HashWebApiAuth\":\"23f788f3a6e6f0c12a98fc950d4be7a7\",\"ListenPassCode\":\"1220230\",\"MaxParticipants\":\"125\",\"MeetingRoomId\":\"1141405\",\"MeetingRoomOptions\":[{\"Enabled\":true,\"Name\":\"AllowChat\",\"id\":1,\"meetingRoomOptionsId\":\"1\"},{\"Enabled\":true,\"Name\":\"JoinNoise\",\"id\":2,\"meetingRoomOptionsId\":\"1\"},{\"Enabled\":true,\"Name\":\"WebRecording\",\"id\":3,\"meetingRoomOptionsId\":\"1\"},{\"Enabled\":true,\"Name\":\"File Transfer\",\"id\":4,\"meetingRoomOptionsId\":\"1\"}],\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https://mulesoft.pgilab.net/AbhishekGupta\",\"AttendeeRegisterUrl\":\"\",\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https://mulesoft.pgilab.net/utilities/joinLiveConference.aspx?cid=1141405&pc=&sc=&msc=68864\",\"EndOfMeetingUrl\":\"\",\"id\":1},\"ModeratorEmail\":\"Abhishek.Gupta@pgi.com\",\"ModeratorPassCode\":\"1220238\",\"ModeratorSecurityCode\":\"68864\",\"ParticipantPassCode\":\"122023\",\"Ptok\":\"AVrWzn+lYyIY6sFkiODBk7k64pzNzXC0Po260ENEXE4=\",\"RoomName\":\"AbhishekGupta\",\"SecurityCode\":\"\",\"Status\":\"Completed\",\"TimeZone\":\"GUAM__BASE\",\"WebMeetingServer\":\"usw2a.qab.pgilab.net\",\"bridgeOptions\":[\"\"],\"phoneInformation\":[{\"Location\":\"USA/Mexico\",\"PhoneNumber\":\"1-719-359-9722\",\"PhoneType\":\"Toll\",\"id\":1,\"phoneInformationId\":\"1\"}],\"ParticipantAnonymity\":false,\"UseHtml5\":false,\"id\":1,\"meetingRoomsId\":\"1\",\"meetingRoomUrlsId\":\"1\"}],\"AudioConferenceCount\":0,\"HasGMWebinar\":false,\"MeetingRoomsCount\":1,\"id\":1,\"clientDetailsId\":\"1\",\"companyDetailsId\":\"1\"}";
    private String clientInfoResponseStr = "{\"ClientInfoResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":500,\"MessageId\":\"ea66ec05-0abf-4ba5-86de-d8c13ad162d8\",\"ServerDateTime\":\"\\/Date(1507536609671)\\/\",\"AudioOnlyConferences\":[],\"ClientDetails\":{\"Active\":true,\"BillToId\":8291321,\"ClientContactDetails\":{\"Address\":{\"Address1\":null,\"Address2\":null,\"Address3\":null,\"City\":null,\"CountryCode\":null,\"PostalCode\":null,\"Province\":null,\"StateCode\":null,\"TimeZone\":null},\"Email\":\"Abhishek.Gupta@pgi.com\",\"Fax\":\"\",\"FaxIntlPrefix\":\"\",\"FirstName\":\"Abhishek\",\"HomePhone\":\"\",\"HomePhoneExt\":\"\",\"HomePhoneIntlPrefix\":\"\",\"HomePhoneIsDefault\":false,\"JobTitle\":\"\",\"LastName\":\"Gupta\",\"MobilePhone\":\"\",\"MobilePhoneExt\":\"\",\"MobilePhoneIntlPrefix\":\"\",\"MobilePhoneIsDefault\":false,\"Phone\":\"\",\"PhoneExt\":\"\",\"PhoneIntlPrefix\":\"\",\"PhoneIsDefault\":false,\"SecondaryPhone\":null,\"SecondaryPhoneExt\":null,\"SecondaryPhoneIntlPrefix\":null,\"SecondaryPhoneIsDefault\":false},\"ClientId\":\"2690509\",\"CreditCardDetail\":null,\"DefaultLanguage\":\"\",\"Hold\":false,\"OperationComments\":\"\",\"Password\":\"jeM0659Q\",\"PasswordCanBeModified\":null,\"PromotionCodes\":[],\"SoftPhoneAutoConnect\":false,\"SoftPhoneEnable\":true,\"SpecialInfo\":\"\",\"SubscriptionId\":\"\",\"TerritoryCode\":\"D0938\"},\"CompanyDetails\":{\"BlockedServices\":[\"Acrobat Connect Professional\",\"Readycast Demo (Internal Use Only)\",\"ReadyCast Protect\",\"Visioncast Meeting Demo (Internal Use Only)\",\"iMeet (Visual Conferencing)\",\"International Moderator Dial Out\",\"LotusLiveMeeting\",\"LotusLiveMeetingDemo (Internal Use Only)\"],\"CompanyId\":635623,\"Name\":\"MuleSoft\"},\"ExcessiveConfs\":[],\"MeetingRooms\":[{\"Availability\":\"Wait For Moderator\",\"ConfId\":\"999965210\",\"ConferenceName\":\"Abhishek's Meeting\",\"Deleted\":false,\"HashWebApiAuth\":\"23f788f3a6e6f0c12a98fc950d4be7a7\",\"ListenPassCode\":\"1220230\",\"MaxParticipants\":\"125\",\"MeetingRoomId\":\"1141405\",\"MeetingRoomOptions\":[{\"Enabled\":true,\"Name\":\"AllowChat\"},{\"Enabled\":true,\"Name\":\"JoinNoise\"},{\"Enabled\":true,\"Name\":\"WebRecording\"},{\"Enabled\":true,\"Name\":\"File Transfer\"}],\"MeetingRoomUrls\":{\"AttendeeEvaluateUrl\":\"\",\"AttendeeJoinUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/AbhishekGupta\",\"AttendeeRegisterUrl\":\"\",\"FileDownloadUrl\":\"\",\"PresenterJoinUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141405&pc=&sc=&msc=68864\",\"EndOfMeetingUrl\":\"\"},\"ModeratorEmail\":\"Abhishek.Gupta@pgi.com\",\"ModeratorPassCode\":\"1220238\",\"ModeratorSecurityCode\":\"68864\",\"ParticipantPassCode\":\"122023\",\"Ptok\":\"AVrWzn+lYyIY6sFkiODBk7k64pzNzXC0Po260ENEXE4=\",\"RoomName\":\"AbhishekGupta\",\"SecurityCode\":\"\",\"Status\":\"Completed\",\"TimeZone\":\"GUAM__BASE\",\"WebMeetingServer\":\"usw2a.qab.pgilab.net\",\"bridgeOptions\":[],\"phoneInformation\":[{\"Location\":\"USA\\/Mexico\",\"PhoneNumber\":\"1-719-359-9722\",\"PhoneType\":\"Toll\",\"CustomLocation\":null}],\"reservationOptions\":[],\"ParticipantAnonymity\":false}],\"AudioConferenceCount\":0,\"HasGMWebinar\":false,\"MeetingRoomsCount\":1}}";

    @Before
    public void before() throws IOException {
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        ConvergenceApplication.mLogger = new TestLogger();
        CommonUtils.checkSharedPrefManagerInstance(context);
        SharedPreferencesManager sharedPreferencesManager = Mockito.mock(SharedPreferencesManager.class);
        PowerMockito.mockStatic(SharedPreferencesManager.class);
        Mockito.when(SharedPreferencesManager.getInstance()).thenReturn(sharedPreferencesManager);
        clientInfoDaoUtils = ClientInfoDaoUtils.getInstance();

        Mockito.mock(DaoSession.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        mockClientInfoDaos();
        injectMockInTestClass();

        List<ClientInfoResult> list = prepareMockClientInfoData();
        PowerMockito.when(clientInfoResultDao.loadAll()).thenReturn(list);
        List<MeetingRoom> meetingRooms = list.get(0).getMeetingRooms();
        PowerMockito.when(meetingRoomDao.loadAll()).thenReturn(meetingRooms);
        PowerMockito.when(meetingRoomOptionDao.loadAll()).thenReturn(meetingRooms.get(0).getMeetingRoomOptions());
        PowerMockito.when(phoneInformationDao.loadAll()).thenReturn(meetingRooms.get(0).getPhoneInformation());
        PowerMockito.when(meetingRoomUrlsDao.load(1L)).thenReturn(meetingRooms.get(0).getMeetingRoomUrls());
        List<String> reservationOptions = meetingRooms.get(0).getReservationOptions();
        if (reservationOptions == null) {
            reservationOptions = new ArrayList<>();
        }
        //PowerMockito.when(reservationOptionsDao.loadAll()).thenReturn(reservationOptions);
        ClientDetails clientDetails = list.get(0).getClientDetails();
        PowerMockito.when(clientContactDetailsDao.loadDeep(1L)).thenReturn(clientDetails.getClientContactDetails());
        PowerMockito.when(clientDetailsDao.loadDeep(1L)).thenReturn(clientDetails);
        PowerMockito.when(clientInfoResultDao.loadDeep(1L)).thenReturn(list.get(0));
        mockCompanyDetail = list.get(0).getCompanyDetails();
        clientInfoDaoUtils.refereshClientInfoResultDao();
    }

    private void injectMockInTestClass() {
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getAddress()).thenReturn(addressDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getCompanyDetail()).thenReturn(companyDetailsDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getClientContactDetails()).thenReturn(clientContactDetailsDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getClientDetail()).thenReturn(clientDetailsDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getMeetingRoom()).thenReturn(meetingRoomDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getMeetingRoomUrls()).thenReturn(meetingRoomUrlsDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getMeetingRoomOptions()).thenReturn(meetingRoomOptionDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getPhoneInformations()).thenReturn(phoneInformationDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getReservationOptions()).thenReturn(reservationOptionsDao);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext).getClientResult()).thenReturn(clientInfoResultDao);
    }

    private void mockClientInfoDaos() {
        clientInfoResultDao = PowerMockito.mock(ClientInfoResultDao.class);
        addressDao = PowerMockito.mock(AddressDao.class);
        companyDetailsDao = PowerMockito.mock(CompanyDetailsDao.class);
        clientContactDetailsDao = PowerMockito.mock(ClientContactDetailsDao.class);
        clientDetailsDao = PowerMockito.mock(ClientDetailsDao.class);
        meetingRoomDao = PowerMockito.mock(MeetingRoomDao.class);
        meetingRoomUrlsDao = PowerMockito.mock(MeetingRoomUrlsDao.class);
        meetingRoomOptionDao = PowerMockito.mock(MeetingRoomOptionDao.class);
        phoneInformationDao = PowerMockito.mock(PhoneInformationDao.class);
        reservationOptionsDao = PowerMockito.mock(ReservationOptionsDao.class);
    }

    @Test
    public void testGetEmailId() {
        String emailId = clientInfoDaoUtils.getEmailId();
        Assert.assertEquals("Abhishek.Gupta@pgi.com", emailId);
    }

    @Test
    public void testGetFullname() {
        String fullName = clientInfoDaoUtils.getFullName();
        Assert.assertEquals("Abhishek Gupta", fullName);
    }

    @Test
    public void testGetPhoneNumber() {
        String fullName = clientInfoDaoUtils.getPhoneNumber();
        Assert.assertEquals("1-719-359-9722,*,,1220238#", fullName);
    }

    @Test
    public void testGetConferenceId() {
        String fullName = clientInfoDaoUtils.getConferenceId();
        Assert.assertEquals("999965210", fullName);
    }

    @Test
    public void testGetFirstname() {
        String firstName = clientInfoDaoUtils.getFirstName();
        Assert.assertEquals("Abhishek", firstName);
    }

    @Test
    public void testGetCompanyId() {
        Long id = clientInfoDaoUtils.getCompanyId();
        Assert.assertEquals(635623, (long) id);
    }

    @Test
    public void testGetClientId() {
        String clientId = clientInfoDaoUtils.getClientId();
        Assert.assertEquals("2690509", clientId);
    }

    @Test
    public void testLoadClientInfo() throws IOException {
        clientInfoDaoUtils.refereshClientInfoResultDao();
        ClientInfoResult clientInfo = clientInfoDaoUtils.getClientInfoResult();
        try {
            String jsonInString = mapper.writeValueAsString(clientInfo);
            Assert.assertEquals(clientInfoResultStr, jsonInString);
            Log.d("ERROR_CLIENTINFO", jsonInString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadClientInfoWithException() {
        BDDMockito.willAnswer(invocation -> { throw new Exception("abc msg"); }).given(clientInfoResultDao).loadAll();
        clientInfoDaoUtils.refereshClientInfoResultDao();
        ClientInfoResult clientInfoResult = clientInfoDaoUtils.getClientInfoResult();
        Assert.assertNull(clientInfoResult);
    }

    @Test
    public void testInsertClientInfo() throws IOException {
        ClientInfoResponse clientInfoResponse = mapper.readValue(clientInfoResponseStr, ClientInfoResponse.class);
        CommonUtils.checkSharedPrefManagerInstance(context);
        clientInfoDaoUtils.insertClientInfoInDb(clientInfoResponse);
        clientInfoDaoUtils.refereshClientInfoResultDao();
        ClientInfoResult clientInfo = clientInfoDaoUtils.getClientInfoResult();
        try {
            String jsonInString = mapper.writeValueAsString(clientInfo);
            Assert.assertEquals(clientInfoResultStr, jsonInString);
            Log.d("ERROR_CLIENTINFO", jsonInString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrepareDialIn() {
        List<PhoneInformation> phoneInformationList = clientInfoDaoUtils.getMeetingRoomData().getPhoneInformation();
        PhoneInformation phoneInformation = new PhoneInformation();
        phoneInformation.setPhoneNumber("1-719-359-9722");
        phoneInformation.setPhoneType("TOLL");
        phoneInformationList.add(phoneInformation);
        clientInfoDaoUtils.prepareDialInList("1-719-359-9722", phoneInformationList);
        Assert.assertEquals(phoneInformationList.get(0).getPhoneNumber() , "1-719-359-9722");
        Assert.assertEquals(phoneInformationList.get(0).compareTo(phoneInformationList.get(1)) , -1);
    }

    @Test
    public void testGetCompanyDetails() {
        PowerMockito.when(companyDetailsDao.load(1L)).thenReturn(mockCompanyDetail);
        clientInfoDaoUtils.refereshClientInfoResultDao();
        Assert.assertNotNull(clientInfoDaoUtils.getCompany());
    }
    
    public List<ClientInfoResult> prepareMockClientInfoData() throws IOException {
        ClientInfoResult clientInfoResult = mapper.readValue(clientInfoResultStr, ClientInfoResult.class);
        List<ClientInfoResult> clientInfoResults = new ArrayList<>();
        clientInfoResults.add(clientInfoResult);
        return clientInfoResults;
    }

}
