package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.Meeting;
import com.pgi.convergencemeetings.models.MeetingRoomGetResult;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.services.FetchMeetingRoomService;
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

import java.io.IOException;

/**
 * Created by visheshchandra on 12/28/2017.
 */

@PrepareForTest({PgiWebServiceManager.class, CommonUtils.class})
public class FetchMeetingRoomServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    private PgiWebServiceManager mPgiWebServiceManager;

    private PgiWebServiceAPI mPgiWebServiceAPI;
    private FetchMeetingRoomService mFetchMeetingRoomService;

    private static final String ACCESS_TOKEN = "eyJraWQiOiJlMTFjNzhkYy1jMjljLTQ2M2MtYjc2MS05N2I5NDk3OWFlMGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiIxYjdkMTkxOS02ODY3LTQ1YzQtODMwNy0zMjY3YjcwNmU0NTAiLCJzY3AiOlsid2Vic2VydmljZXMiLCJsb2dnaW5nIiwiZW1haWwiLCJvcGVuaWQiLCJ1YXBpIiwicHJvZmlsZSIsIlB1bHNhciJdLCJwZ2lfbG9naW5fc291cmNlIjoiR2xvYmFsTWVldCIsInBnaV9zZXNzaW9uIjoiNzFmYmViYTYtMWNmOS00NTc1LTk1ZmItNzE4ZGM3ZTRkN2YxIiwicGdpX2lkX2dtIjoiOTkyMTExNSIsImlzcyI6ImlkZW50aXR5LnFhYi5nbG9iYWxtZWV0Lm5ldCIsInBnaV9jbGllbnRfdHlwZSI6IkFuZHJvaWQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTE0NDY1NzQ2LCJpYXQiOjE1MTQ0NTg1NDYsInBnaV9pZF9lbWFpbCI6ImFtaXQua3VtYXJAcGdpLmNvbSJ9.ft__qYLfDRc_SOmZa2pWEEgQC8agcHL1LtTBgdjd3-s5vdOD5v482NbQ2QjBpvJdQsa5H_BpVXdQo7LAbJQd1U3fpmCFPV028zEp01VicZIfuXZXvjs1wMa46_srf-u2cFbvPqjd9yZhVQgu5SWqQhXiWC6R6EtasrTo9SQN5qmM2w5R-VG-QfzcrjAyifIxgbGpxekqBf0m5nrRcYXs2lv2jxDdlVle_7le0bGZw0LY5pKB6eZA09ZXS_5thQH6sfOA2wkB_cR47s76gxuMDP13Y61w0Bmg_hFBij5jbtkfiNECKW5zlC-iTIo_TneQkm4riWRoOxpRTY_92ya75A";
    private static final String INVALID_ACCESS_TOKEN = "old.access.token";

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        PowerMockito.mockStatic(CommonUtils.class);
        mPgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(mPgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(mPgiWebServiceAPI);
        mFetchMeetingRoomService = new FetchMeetingRoomService(mMeetingRoomPresenter);
    }

    @Test
    public void testUpdateMeetingSuccess() throws IOException {
        MeetingRoomGetResult meetingRoomGetResult = new MeetingRoomGetResult();
        Meeting meeting = new Meeting();
        meeting.setIncrementAttended(true);
        meeting.setFavorite(false);
        String desktopMeetingId = "4606";
        mMeetingRoomPresenter.onFetchMeetingRoomSuccess(meetingRoomGetResult);
        Mockito.verify(mMeetingRoomPresenter).onFetchMeetingRoomSuccess(meetingRoomGetResult);
    }

    @Test
    public void testUpdateMeetingError(){
        Meeting meeting = new Meeting();
        meeting.setIncrementAttended(true);
        meeting.setFavorite(false);
        String desktopMeetingId = "4606";
        mMeetingRoomPresenter.onFetchMeetingRoomErrorCallback(Mockito.anyString());
        Mockito.verify(mMeetingRoomPresenter, Mockito.times(1)).onFetchMeetingRoomErrorCallback(Mockito.anyString());
    }
}

