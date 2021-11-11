package com.pgi.convergencemeetings;

/**
 * Created by nnennaiheke on 11/17/17.
 */

import android.content.Context;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateLastUsedPhoneService;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
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
import org.robolectric.RuntimeEnvironment;

@PrepareForTest({PgiWebServiceManager.class, CommonUtils.class, SharedPreferencesManager.class, AppAuthUtils.class})
public class UpdateLastUsedNumberServiceTest extends RobolectricTest{
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    MeetingRoomPresenter meetingRoomPresenter;
    @Mock
    PgiWebServiceManager pgiWebServiceManager;

    UpdateLastUsedPhoneService updateLastUsedPhoneService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        ConvergenceApplication.mLogger = new TestLogger();
        PowerMockito.mockStatic(SharedPreferencesManager.class);
        PowerMockito.mockStatic(CommonUtils.class);

        PowerMockito.mockStatic(AppAuthUtils.class);
        AppAuthUtils appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        updateLastUsedPhoneService = new UpdateLastUsedPhoneService(meetingRoomPresenter);
    }

    @Test
    public void testUpdateLastPhoneNumber() {
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        updateLastUsedPhoneService.updateLastUsedPhoneNumber("111111", false, "1", "6788776766", "");
        Mockito.verify(meetingRoomPresenter).onUpdateLastUsedPhoneNumberError("", 401);
    }
}
