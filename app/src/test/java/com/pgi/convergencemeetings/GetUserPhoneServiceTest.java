package com.pgi.convergencemeetings;

import android.content.Context;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneNumberResult;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.services.GetPhoneNumberService;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PgiWebServiceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by nnennaiheke on 11/17/17.
 */

@PrepareForTest({PgiWebServiceManager.class, CommonUtils.class, AppAuthUtils.class, SharedPreferencesManager.class})

public class GetUserPhoneServiceTest extends RobolectricTest{
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    MeetingRoomPresenter audioSelectionPresenter;
    @Mock
    PgiWebServiceManager pgiWebServiceManager;
    @Mock
    GetPhoneNumberService getPhoneNumberService;
    Context context;
    private AppAuthUtils appAuthUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        PowerMockito.mockStatic(SharedPreferencesManager.class);
        PowerMockito.mockStatic(AppAuthUtils.class);
        appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        getPhoneNumberService = new GetPhoneNumberService(audioSelectionPresenter);
    }

    @Test
    public void testUpdateLastPhoneNumber() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        PowerMockito.mockStatic(AppAuthUtils.class);
        AppAuthUtils appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(pgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        getPhoneNumberService.getPhoneNumber("111111");
        Mockito.verify(audioSelectionPresenter).onGetPhoneNumberSuccess(ArgumentMatchers.any(PhoneNumberResult.class));
    }
}