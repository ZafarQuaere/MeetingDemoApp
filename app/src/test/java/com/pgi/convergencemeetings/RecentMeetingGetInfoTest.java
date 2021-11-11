package com.pgi.convergencemeetings;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthPresenter;
import com.pgi.convergencemeetings.search.ui.SearchActivity;
import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PgiWebServiceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/**
 * Created by ashwanikumar on 8/29/17.
 */
@Config(shadows = RecentMeetingsFragmentShadow.class)
@PrepareForTest({PgiWebServiceManager.class, CommonUtils.class, SharedPreferencesManager.class})
public class RecentMeetingGetInfoTest extends RobolectricTest{

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    PgiWebServiceManager pgiWebServiceManager;

    @Mock
    private OnBoardAuthPresenter joinMeetingsPresenter;

    @Mock
    private SearchActivity recentMeetingsActivity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        ActivityController<SearchActivity> controller = Robolectric.buildActivity(SearchActivity.class);
        recentMeetingsActivity = controller.get();
    }

//    @Test
//    public void testGetRecentMeetingInfoReturnsExpected() throws IOException {
//        PgiWebServiceAPI pgiWebServiceAPI = FakeRestClient.getInstance().getClient();
//        PowerMockito.mockStatic(PgiWebServiceManager.class);
//        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(mPgiWebServiceManager);
//        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
//        pgiWebServiceAPI.getRecentMeetingInfo("124124", 50, true, false, "");
//        joinMeetingsPresenter.getRecentMeetingInfo(anyString());
//        verify(joinMeetingsPresenter, times(1)).getRecentMeetingInfo(anyString());
//        Call<String> meetingInfo = mPgiWebServiceManager.getPgiWebServiceAPI().getRecentMeetingInfo("124124", 50, true, false, "");
//        assertTrue(meetingInfo.execute() != null);
//        assertEquals("Request method should be 'GET'", "GET" , meetingInfo.request().method());
//
//    }

    @Test
    public void testGetMeetingInfo(){
        PgiWebServiceAPI pgiWebServiceAPI = FakeRestClient.getInstance().getClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        pgiWebServiceAPI.getRecentMeetingInfo("124124", null, 50, true, false);
    }
}
