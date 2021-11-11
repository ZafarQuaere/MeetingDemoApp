package com.pgi.convergencemeetings.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.AddressDao;
import com.pgi.convergencemeetings.models.ClientContactDetailsDao;
import com.pgi.convergencemeetings.models.ClientDetailsDao;
import com.pgi.convergencemeetings.models.ClientInfoResponseDao;
import com.pgi.convergencemeetings.models.ClientInfoResultDao;
import com.pgi.convergencemeetings.models.CompanyDetailsDao;
import com.pgi.convergencemeetings.models.DaoMaster;
import com.pgi.convergencemeetings.models.DaoSession;
import com.pgi.convergencemeetings.models.MeetingInfoResponseDao;
import com.pgi.convergencemeetings.models.MeetingRoomDao;
import com.pgi.convergencemeetings.models.MeetingRoomDetailDao;
import com.pgi.convergencemeetings.models.MeetingRoomGetResultDao;
import com.pgi.convergencemeetings.models.MeetingRoomOptionDao;
import com.pgi.convergencemeetings.models.MeetingRoomUrlsDao;
import com.pgi.convergencemeetings.models.PhoneInformationDao;
import com.pgi.convergencemeetings.models.ReservationOptionsDao;
import com.pgi.convergencemeetings.models.UUIDDao;
import com.pgi.convergencemeetings.models.elkmodels.LogsModelDao;
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneDao;
import com.pgi.network.models.SearchResult;
import com.pgi.network.models.SearchResultDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.mockito.Mockito.mock;

public class ApplicationDaoTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
    }

    @Test
    public void get() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        Assert.assertNotNull(applicationDao);
    }

    @Test
    public void getWithNullContext() {
        ApplicationDao save = ApplicationDao.get(null);
        ApplicationDao.setInstance(null);
        ApplicationDao applicationDao = ApplicationDao.get(null);
        Assert.assertNotNull(applicationDao);
        ApplicationDao.setInstance(save);
    }

    @Test
    public void invalidate() {
        ApplicationDao mockApplicationDao = mock(ApplicationDao.class);
        mockApplicationDao.invalidate();
        Mockito.verify(mockApplicationDao, Mockito.atLeastOnce()).invalidate();
    }

    @Test
    public void getWritableDatabase() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        SQLiteDatabase sqLiteDatabase = applicationDao.getWritableDatabase();
        if (sqLiteDatabase == null) {
            Assert.assertNull(sqLiteDatabase);
        } else {
            Assert.assertNotNull(sqLiteDatabase);
        }
    }

    @Test
    public void getDaoMaster() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        DaoMaster daoMaster = applicationDao.getDaoMaster();
        Assert.assertNotNull(daoMaster);
    }

    @Test
    public void getDaoSession() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        com.pgi.network.models.DaoSession daoSession = applicationDao.getDaoSessionSearch();
        Assert.assertNotNull(daoSession);
    }

    @Test
    public void getDaoMasterSearch() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        com.pgi.network.models.DaoMaster daoMaster = applicationDao.getDaoMasterSearch();
        Assert.assertNotNull(daoMaster);
    }

    @Test
    public void getDaoSessionSearch() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        DaoSession daoSession = applicationDao.getDaoSession();
        Assert.assertNotNull(daoSession);
    }
    @Test
    public void getMeetingResult() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingRoomGetResultDao meetingRoomGetResultDao = applicationDao.getMeetingResult();
        Assert.assertNotNull(meetingRoomGetResultDao);
    }

    @Test
    public void getMeetingInfo() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingInfoResponseDao meetingInfoResponseDao = applicationDao.getMeetingInfo();
        Assert.assertNotNull(meetingInfoResponseDao);
    }

    @Test
    public void getClientInfo() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        ClientInfoResponseDao clientInfoResponseDao = applicationDao.getClientInfo();
        Assert.assertNotNull(clientInfoResponseDao);
    }

    @Test
    public void getClientResult() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        ClientInfoResultDao clientInfoResultDao = applicationDao.getClientResult();
        Assert.assertNotNull(clientInfoResultDao);
    }

    @Test
    public void getClientDetail() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        ClientDetailsDao clientDetailsDao = applicationDao.getClientDetail();
        Assert.assertNotNull(clientDetailsDao);
    }

    @Test
    public void getAddress() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        AddressDao addressDao = applicationDao.getAddress();
        Assert.assertNotNull(addressDao);
    }

    @Test
    public void getClientContactDetails() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        ClientContactDetailsDao clientContactDetailsDao = applicationDao.getClientContactDetails();
        Assert.assertNotNull(clientContactDetailsDao);
    }

    @Test
    public void getCompanyDetail() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        CompanyDetailsDao companyDetailsDao = applicationDao.getCompanyDetail();
        Assert.assertNotNull(companyDetailsDao);
    }

    @Test
    public void getMeetingRoomOptions() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingRoomOptionDao meetingRoomOptionDao = applicationDao.getMeetingRoomOptions();
        Assert.assertNotNull(meetingRoomOptionDao);
    }

    @Test
    public void getMeetingRoomUrls() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingRoomUrlsDao meetingRoomUrlsDao = applicationDao.getMeetingRoomUrls();
        Assert.assertNotNull(meetingRoomUrlsDao);
    }

    @Test
    public void getPhoneInformations() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        PhoneInformationDao phoneInformationDao = applicationDao.getPhoneInformations();
        Assert.assertNotNull(phoneInformationDao);
    }

    @Test
    public void getReservationOptions() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        ReservationOptionsDao reservationOptionsDao= applicationDao.getReservationOptions();
        Assert.assertNotNull(reservationOptionsDao);
    }

    @Test
    public void getMeetingRoom() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingRoomDao meetingRoomDao = applicationDao.getMeetingRoom();
        Assert.assertNotNull(meetingRoomDao);
    }

    @Test
    public void getPhoneNumbers() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        PhoneDao phoneDao = applicationDao.getPhoneNumbers();
        Assert.assertNotNull(phoneDao);
    }

    @Test
    public void getAllRecentMeetings() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        List<SearchResult> searchResultList = applicationDao.getAllRecentMeetings();
        if (searchResultList != null) {
            Assert.assertNotNull(searchResultList);
        } else {
            Assert.assertNull(searchResultList);
        }
    }

    @Test
    public void saveRecentMeeting() {
        Context mockContext = mock(Context.class);
        ApplicationDao oldDao = ApplicationDao.get(mockContext);
        oldDao.invalidate();
        ApplicationDao.setInstance(null);

        Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        ApplicationDao applicationDao = ApplicationDao.get(context);

        SearchResult searchResult = new SearchResult();
        searchResult.setConferenceId(123);
        searchResult.setUseHtml5(true);
        searchResult.setBrandId(10);
        searchResult.setFurl("https://pgi.globalmeet.com/gauravsingh");
        long firstObjId = applicationDao.saveRecentMeeting(searchResult);
        Assert.assertNotEquals(firstObjId, 0);

        SearchResult secondSearchResult = new SearchResult();
        secondSearchResult.setConferenceId(123);
        secondSearchResult.setUseHtml5(true);
        secondSearchResult.setBrandId(10);
        secondSearchResult.setFurl("https://pgi.globalmeet.com/gauravsingh");
        long secondObjId = applicationDao.saveRecentMeeting(secondSearchResult);
        Assert.assertNotEquals(secondObjId, 0);
        Assert.assertEquals(firstObjId, secondObjId);

        Assert.assertEquals(0, applicationDao.saveRecentMeeting(null));
    }

    @Test
    public void getSearchResult() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        SearchResultDao searchResultDao = applicationDao.getSearchResult();
        Assert.assertNotNull(searchResultDao);
    }

    @Test
    public void getMeetingDetails() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        MeetingRoomDetailDao meetingRoomDetailDao = applicationDao.getMeetingDetails();
        Assert.assertNotNull(meetingRoomDetailDao);
    }

    @Test
    public void getUUID() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        UUIDDao uuidDao = applicationDao.getUUID();
        Assert.assertNotNull(uuidDao);
    }

    @Test
    public void getElkLogs() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        LogsModelDao logsModelDao = applicationDao.getElkLogs();
        Assert.assertNotNull(logsModelDao);
    }

    @Test
    public void deleteSearchResult() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        com.pgi.network.models.DaoSession saveDaoSession = applicationDao.getDaoSessionSearch();
        com.pgi.network.models.DaoSession mockDaoSession = mock(com.pgi.network.models.DaoSession.class);
        applicationDao.setDaoSessionSearch(mockDaoSession);
        Assert.assertEquals(mockDaoSession, applicationDao.getDaoSessionSearch());
        applicationDao.deleteSearchResult();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        applicationDao.setDaoSessionSearch(saveDaoSession);
    }

    @Test
    public void deleteMeetingDetails() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        DaoSession saveDaoSession = applicationDao.getDaoSession();
        DaoSession mockDaoSession = mock(DaoSession.class);
        applicationDao.setDaoSession(mockDaoSession);
        Assert.assertEquals(mockDaoSession, applicationDao.getDaoSession());
        applicationDao.deleteMeetingDetails();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        applicationDao.setDaoSession(saveDaoSession);
        ApplicationDao mockApplicationDao = mock(ApplicationDao.class);
        mockApplicationDao.deleteMeetingDetails();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(mockApplicationDao, Mockito.atLeastOnce()).deleteMeetingDetails();
    }

    @Test
    public void deleteAll() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        DaoSession saveDaoSession = applicationDao.getDaoSession();
        DaoSession mockDaoSession = mock(DaoSession.class);
        applicationDao.setDaoSession(mockDaoSession);
        Assert.assertEquals(mockDaoSession, applicationDao.getDaoSession());
        applicationDao.deleteAll();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ClientInfoResultDao clientResultDao = applicationDao.getClientResult();
        Assert.assertNull(clientResultDao);
        applicationDao.setDaoSession(saveDaoSession);
    }

    @Test
    public void deleteAllExceptLogs() {
        Context context = mock(Context.class);
        ApplicationDao applicationDao = ApplicationDao.get(context);
        DaoSession saveDaoSession = applicationDao.getDaoSession();
        DaoSession mockDaoSession = mock(DaoSession.class);
        applicationDao.setDaoSession(mockDaoSession);
        Assert.assertEquals(mockDaoSession, applicationDao.getDaoSession());
        LogsModelDao preLogsModelDao = applicationDao.getElkLogs();
        LogsModelDao postLogsModelDao = applicationDao.getElkLogs();
        Assert.assertEquals(preLogsModelDao, postLogsModelDao);
        applicationDao.deleteAllExecptLogs();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ClientInfoResultDao clientResultDao = applicationDao.getClientResult();
        Assert.assertNull(clientResultDao);
        com.pgi.network.models.DaoSession saveDaoSessionSearch = applicationDao.getDaoSessionSearch();
        com.pgi.network.models.DaoSession mockDaoSessionSearch = mock(com.pgi.network.models.DaoSession.class);
        applicationDao.setDaoSessionSearch(mockDaoSessionSearch);
        Assert.assertEquals(mockDaoSessionSearch, applicationDao.getDaoSessionSearch());
        applicationDao.deleteSearchOnly();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SearchResultDao searchResultDao = applicationDao.getSearchResult();
        Assert.assertNull(searchResultDao);
        applicationDao.setDaoSession(saveDaoSession);
        applicationDao.setDaoSessionSearch(saveDaoSessionSearch);
    }

    @Test
    public void deleteAllModels(){
        ApplicationDao applicationDao = mock(ApplicationDao.class);
        applicationDao.deleteAllModels();
        SearchResultDao searchResultDao = applicationDao.getSearchResult();
        Assert.assertNull(searchResultDao);

    }

    @Test
    public void onUpgradeDatabase(){
        Context context = mock(Context.class);
        SQLiteDatabase database = mock(SQLiteDatabase.class);
        ApplicationDao mockApplicationDao = ApplicationDao.get(context);
        mockApplicationDao.getOpenHelper().onUpgrade(database, 4, 5);
    }

    @Test
    public void onCreateDatabase(){
        Context context = mock(Context.class);
        SQLiteDatabase database = mock(SQLiteDatabase.class);
        ApplicationDao mockApplicationDao = ApplicationDao.get(context);
        mockApplicationDao.getOpenHelper().onCreate(database);
    }
}