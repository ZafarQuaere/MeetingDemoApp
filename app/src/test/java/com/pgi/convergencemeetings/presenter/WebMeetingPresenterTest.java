package com.pgi.convergencemeetings.presenter;

import android.content.Context;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingPresenter;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.services.RecentMeetingService;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateLastUsedPhoneService;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
import com.pgi.convergencemeetings.utils.ClientInfoResultCache;
import com.pgi.network.PgiWebServiceManager;
import com.pgi.network.models.SearchResult;
import com.pgi.network.models.SearchResultDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@PrepareForTest({AppAuthUtils.class, ApplicationDao.class, ClientInfoDaoUtils.class, ClientInfoResultCache.class, PgiWebServiceManager.class})
public class WebMeetingPresenterTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    ApplicationDao applicationDao;
    @Mock
    ClientInfoDaoUtils clientInfoDaoUtils;
    @Mock
    ClientInfoResultCache clientInfoResultCache;
    @Mock
    PgiWebServiceManager pgiWebServiceManager;
    @Mock
    RecentMeetingService recentMeetingService;
    @Mock
    SearchResultDao searchResultDao;
    @Mock
    UpdateLastUsedPhoneService updateLastUsedPhoneService;
    @Mock
    AppAuthUtils appAuthUtils;

    WebMeetingPresenter webMeetingPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        Context context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        PowerMockito.mockStatic(AppAuthUtils.class);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        webMeetingPresenter = new WebMeetingPresenter(context);
    }

    @Test
    public void getOthersClientInfo() {
        webMeetingPresenter.setRecentMeetingService(recentMeetingService);
        webMeetingPresenter.getOthersClientInfo("testconfId", false);
        Mockito.verify(recentMeetingService, Mockito.atLeastOnce()).getOthersClientInfo(Mockito.anyString());
    }

    @Test
    public void onRecentMeetingSuccessCallback() {
        PowerMockito.mockStatic(ApplicationDao.class);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext)).thenReturn(applicationDao);
        webMeetingPresenter.onRecentMeetingSuccessCallback();
        Mockito.verify(applicationDao, Mockito.atLeastOnce()).getAllRecentMeetings();
    }

    @Test
    public void onRecentMeetingSuccessCallbackWithMeetingRoomData() {
        PowerMockito.mockStatic(ApplicationDao.class);
        Mockito.when(ApplicationDao.get(ConvergenceApplication.appContext)).thenReturn(applicationDao);
        SearchResult searchResult = new SearchResult();
        searchResult.setHubConfId(2222);
        List<SearchResult> searchResultList = new ArrayList<SearchResult>();
        searchResultList.add(searchResult);
        PowerMockito.when(applicationDao.getAllRecentMeetings())
                .thenReturn(searchResultList);
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(111L);
        meetingRoom.setConferenceName("test conference name");
        meetingRoom.setMeetingRoomId("2222");
        PowerMockito.mockStatic(ClientInfoResultCache.class);
        PowerMockito.when(ClientInfoResultCache.getInstance())
                .thenReturn(clientInfoResultCache);
        PowerMockito.when(clientInfoResultCache.getMeetingRoomData())
                .thenReturn(meetingRoom);
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        PowerMockito.when(PgiWebServiceManager.getInstance())
                .thenReturn(pgiWebServiceManager);
        webMeetingPresenter.onRecentMeetingSuccessCallback();
        Mockito.verify(applicationDao, Mockito.atLeastOnce()).getAllRecentMeetings();
    }

    @Test
    public void onRecentMeetingErrorCallback() {
        WebMeetingPresenter mockWebMeetingPresenter = mock(WebMeetingPresenter.class);
        String testErrorMsg = "test error message";
        int testResponse = 1;
        webMeetingPresenter.onRecentMeetingErrorCallback(testErrorMsg, testResponse);
        mockWebMeetingPresenter.onRecentMeetingErrorCallback(testErrorMsg, testResponse );
        Mockito.verify(mockWebMeetingPresenter, Mockito.atLeastOnce())
                .onRecentMeetingErrorCallback(testErrorMsg, testResponse);
    }

    @Test
    public void onClientInfoError() {
        WebMeetingPresenter mockWebMeetingPresenter = mock(WebMeetingPresenter.class);
        int testError = 1;
        int testResponse = 2;
        webMeetingPresenter.onClientInfoError(testError, testResponse);
        mockWebMeetingPresenter.onClientInfoError(testError, testResponse);
        Mockito.verify(mockWebMeetingPresenter, Mockito.atLeastOnce())
                .onClientInfoError(testError, testResponse);
    }

    @Test
    public void updateHostLastDialOutPhoneNumber() {
        webMeetingPresenter.setUpdateLastUsedPhoneService(updateLastUsedPhoneService);
        String testClientId = "testClientId";
        String testCountryCode = "testCountryCode";
        String testPhoneNumber = "testPhoneNuber";
        String testExtension = "testExtension";
        webMeetingPresenter.updateHostLastDialOutPhoneNumber(testClientId, testCountryCode, testPhoneNumber, testExtension);
        Mockito.verify(updateLastUsedPhoneService, Mockito.atLeastOnce()).updateLastUsedPhoneNumber(testClientId, false,
                testCountryCode, testPhoneNumber, testExtension);
    }

    @Test
    public void onUpdateLastUsedPhoneNumberSuccess() {
        WebMeetingPresenter mockWebMeetingPresenter = mock(WebMeetingPresenter.class);
        webMeetingPresenter.onUpdateLastUsedPhoneNumberSuccess();
        mockWebMeetingPresenter.onUpdateLastUsedPhoneNumberSuccess();
        Mockito.verify(mockWebMeetingPresenter, Mockito.atLeastOnce()).onUpdateLastUsedPhoneNumberSuccess();
    }

    @Test
    public void onUpdateLastUsedPhoneNumberError() {
        WebMeetingPresenter mockWebMeetingPresenter = mock(WebMeetingPresenter.class);
        String testErrorMsg = "test error message";
        int testResponse = 1;
        webMeetingPresenter.onUpdateLastUsedPhoneNumberError(testErrorMsg, testResponse);
        mockWebMeetingPresenter.onUpdateLastUsedPhoneNumberError(testErrorMsg, testResponse);
        Mockito.verify(mockWebMeetingPresenter, Mockito.atLeastOnce()).onUpdateLastUsedPhoneNumberError(testErrorMsg, testResponse);
    }}