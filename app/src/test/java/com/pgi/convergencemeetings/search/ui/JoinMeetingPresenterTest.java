package com.pgi.convergencemeetings.search.ui;

import android.content.Context;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.ShadowRecentMeetingService;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.services.RecentMeetingService;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.tour.ui.WelcomeFragment;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.models.DaoSession;
import com.pgi.convergencemeetings.models.DesktopMeetingSearchResult;
import com.pgi.convergencemeetings.models.RecentMeetingsModel;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthPresenter;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PgiWebServiceManager;
import com.pgi.network.models.ImeetingRoomInfo;
import com.pgi.network.models.SearchResult;
import com.pgi.network.models.SearchResultDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwanikumar on 9/12/2017.
 */
@Config(shadows = {ShadowRecentMeetingService.class})
@PrepareForTest({JoinMeetingsPresenter.class, PgiWebServiceManager.class, CommonUtils.class, ApplicationDao.class, AppAuthUtils.class, SharedPreferencesManager.class})
public class JoinMeetingPresenterTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private JoinMeetingFragment mJoinMeetingsFragment;
    private JoinMeetingsPresenter mJoinMeetingsPresenter;
    private SearchActivity mJoinMeetingsActivity;
    private OnBoardAuthPresenter mOnBoardAuthPresenter;
    private OnBoardAuthActivity mOnBoardAuthActivity;
    private WelcomeFragment mWelcomeFragment;

    @Mock
    JoinMeetingFragment recentMeetingsFragment;
    @Mock
    PgiWebServiceManager pgiWebServiceManager;
    @Mock
    ProgressBar progressBar;

    @Mock
    SharedPreferencesManager sharedPreferencesManager;
    Context context;
    AppAuthUtils appAuthUtils;
    String accessToken;
    private ApplicationDao applicationDao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        ConvergenceApplication.mLogger = new TestLogger();
        ActivityController<SearchActivity> controller = Robolectric.buildActivity(SearchActivity.class);
        mJoinMeetingsActivity = controller.get();
        mJoinMeetingsFragment = Mockito.mock(JoinMeetingFragment.class);

        PowerMockito.mockStatic(SharedPreferencesManager.class);
        Mockito.when(SharedPreferencesManager.getInstance()).thenReturn(sharedPreferencesManager);

        ActivityController<OnBoardAuthActivity> onBoardController = Robolectric.buildActivity(OnBoardAuthActivity.class);
        mOnBoardAuthActivity = onBoardController.get();
        mWelcomeFragment = Mockito.mock(WelcomeFragment.class);

        //SupportFragmentTestUtil.startFragment(mJoinMeetingsFragment);
        PowerMockito.mockStatic(ApplicationDao.class);
        applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        mJoinMeetingsPresenter = new JoinMeetingsPresenter(mJoinMeetingsActivity, mJoinMeetingsFragment);
        mOnBoardAuthPresenter = new OnBoardAuthPresenter(mOnBoardAuthActivity, context);

        PowerMockito.mockStatic(AppAuthUtils.class);
        appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";

    }

    @Test
    public void testGetRecentMeetingInfo() {
        Whitebox.setInternalState(mJoinMeetingsFragment, "pbRecentMeeting", progressBar);
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        Mockito.mock(DaoSession.class);
        SearchResultDao searchResultDao = Mockito.mock(SearchResultDao.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        Mockito.when(applicationDao.getSearchResult()).thenReturn(searchResultDao);
        mJoinMeetingsPresenter.getRecentMeetingInfo("5078758");
        List<ImeetingRoomInfo> searchResults = new ArrayList<>();
    }

    @Test
    public void testGetRecentMeetingInfoError() {
        Whitebox.setInternalState(mJoinMeetingsFragment, "pbRecentMeeting", progressBar);
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        String accessToken1 = "old.access.token";
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken1);
        Mockito.mock(DaoSession.class);
        SearchResultDao searchResultDao = Mockito.mock(SearchResultDao.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ConvergenceApplication.appContext = context;
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        Mockito.when(applicationDao.getSearchResult()).thenReturn(searchResultDao);
        String errorMsg = "{\"DesktopMeetingSearchResult\":{\"CorrelationId\":null,\"Errors\":[{\"Code\":26119,\"Message\":\"Jwt access token validation failure.\",\"Parameter\":null,\"ParameterValue\":null,\"Severity\":1,\"Source\":0}],\"ExecutionTime\":125,\"MessageId\":\"d7b65927-b91b-4268-804f-b0fa2e078e13\",\"ServerDateTime\":\"/Date(1505289004119)/\",\"PageNumber\":null,\"TotalItems\":null,\"TotalPages\":null,\"ScheduledInviteResults\":[],\"SearchResults\":[]}}";
        //Mockito.verify(mJoinMeetingsFragment).onRecentMeetingInfoError("");
    }

    @Test
    public void testLoadLocalData() throws IOException {
        String recentMeetingResponse = "{\"DesktopMeetingSearchResult\":{\"CorrelationId\":null,\"Errors\":[],\"ExecutionTime\":109,\"MessageId\":\"6a01fced-69ff-4646-929e-21229e78d5b6\",\"ServerDateTime\":\"\\/Date(1505282101592)\\/\",\"PageNumber\":1,\"TotalItems\":2,\"TotalPages\":1,\"ScheduledInviteResults\":[],\"SearchResults\":[{\"Attended\":1,\"ClientId\":5078758,\"DeletedDate\":null,\"Description\":\"Convergence Desktop App\",\"DesktopMeetingId\":4221,\"Favorite\":true,\"HubConfId\":1141282,\"MeetingType\":\"Web\",\"MeetingUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141282&pc=43331893&sc=&msc=41508\",\"ModeratorSecurityCode\":\"41508\",\"ModifiedDate\":\"\\/Date(1502921331500-0500)\\/\",\"SecurityCode\":\"\",\"UserType\":\"Client\",\"FirstName\":\"Sudheer\",\"Furl\":\"https:\\/\\/mulesoft.pgilab.net\\/SudheerChilumula4\",\"LastName\":\"Chilumula\"},{\"Attended\":1,\"ClientId\":5078758,\"DeletedDate\":null,\"Description\":\"KevinHolligan\",\"DesktopMeetingId\":4224,\"Favorite\":false,\"HubConfId\":1141295,\"MeetingType\":\"web\",\"MeetingUrl\":\"https:\\/\\/mulesoft.pgilab.net\\/utilities\\/joinLiveConference.aspx?cid=1141295&pc=&sc=&msc=72520\",\"ModeratorSecurityCode\":\"72520\",\"ModifiedDate\":\"\\/Date(1502949301410-0500)\\/\",\"SecurityCode\":\"\",\"UserType\":\"Client\",\"FirstName\":\"Kevin\",\"Furl\":\"https:\\/\\/mulesoft.pgilab.net\\/KevinHolligan\",\"LastName\":\"Holligan\"}]}}";
        ObjectMapper objectMapper = new ObjectMapper();
        RecentMeetingsModel recentMeetingsModel = objectMapper.readValue(recentMeetingResponse, RecentMeetingsModel.class);
        DesktopMeetingSearchResult desktopMeetingSearchResult = recentMeetingsModel.getDesktopMeetingSearchResult();
        List<SearchResult> searchResults = desktopMeetingSearchResult.getSearchResults();
        List<ImeetingRoomInfo> imeetingRoomInfos = new ArrayList<>();
        imeetingRoomInfos.addAll(searchResults);
        Mockito.mock(DaoSession.class);
        Mockito.when(applicationDao.getAllRecentMeetings()).thenReturn(searchResults);
        mJoinMeetingsPresenter.loadLocalData();
        Mockito.verify(mJoinMeetingsFragment).notifyAdapterOnRecentUpdate(imeetingRoomInfos);
    }

    @Test
    public void testLoadLocalDataWithEmptyList() {
        List<SearchResult> searchResults = new ArrayList<>();
        List<ImeetingRoomInfo> imeetingRoomInfos = new ArrayList<>();
        imeetingRoomInfos.addAll(searchResults);
        Mockito.mock(DaoSession.class);
        Mockito.when(applicationDao.getAllRecentMeetings()).thenReturn(searchResults);
        mJoinMeetingsPresenter.loadLocalData();
        Mockito.verify(mJoinMeetingsFragment).showProgress();
    }

    @Test
    public void testSearchMeetingRooms() {
        Whitebox.setInternalState(mJoinMeetingsFragment, "pbRecentMeeting", progressBar);
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        Mockito.mock(DaoSession.class);
        SearchResultDao searchResultDao = Mockito.mock(SearchResultDao.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ConvergenceApplication.appContext = context;
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        Mockito.when(applicationDao.getSearchResult()).thenReturn(searchResultDao);
        // mJoinMeetingsPresenter.searchMeetingRooms("PGI");
        List<SearchResult> searchResults = new ArrayList<>();

        if (searchResults.size() > 0) {
            List<ImeetingRoomInfo> imeetingRoomInfos = new ArrayList<>();
            imeetingRoomInfos.addAll(searchResults);
            Mockito.verify(mJoinMeetingsFragment).notifyAdapterOnRecentUpdate(imeetingRoomInfos);
        }
    }

    @Test
    public void testSetRecentMeetingService() {
        RecentMeetingService recentMeetingServiceMock = Mockito.mock(RecentMeetingService.class);
        mJoinMeetingsPresenter.setRecentMeetingService(recentMeetingServiceMock);
//        RecentMeetingService returnedVal = mJoinMeetingsPresenter.getRecentMeetingService();
//        Assert.assertNotNull(returnedVal);
    }

    @Test
    public void testSendFailureResponse404() {
        mJoinMeetingsPresenter.sendFailureResponse(404);
//        Assert.assertEquals(404, mJoinMeetingsPresenter.getResponseCode());
    }

    @Test
    public void testSendFailureResponse200() {
        mJoinMeetingsPresenter.sendFailureResponse(200);
//        Assert.assertEquals(200, mJoinMeetingsPresenter.getResponseCode());
    }

    @Test
    public void testSendFailureResponse500() {
        mJoinMeetingsPresenter.sendFailureResponse(500);
//        int result = mJoinMeetingsPresenter.getResponseCode();
//        Assert.assertEquals(500, result);
    }
}
