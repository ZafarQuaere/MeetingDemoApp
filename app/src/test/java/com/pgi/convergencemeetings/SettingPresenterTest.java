package com.pgi.convergencemeetings;

import android.content.Context;

import com.pgi.convergencemeetings.leftnav.settings.ui.UpdateNameActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.leftnav.settings.ui.UpdateNamePresenter;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.IdentityServiceManager;
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

/**
 * Created by ashwanikumar on 12/4/2017.
 */
@PrepareForTest({PgiWebServiceManager.class, AppAuthUtils.class, IdentityServiceManager.class})
public class SettingPresenterTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    UpdateNameActivity mUpdatenameActivity;
    @Mock
    PgiWebServiceManager mPgiWebServiceManager;

    UpdateNamePresenter mUpdatenamePresenter;

    String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
    private Context context;
    private AppAuthUtils appAuthUtils;
    String firstName = "Abhishek";
    String lastName = "Gupta";
    String clientId = "2690509";


    @Before
    public void setup(){
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        mUpdatenamePresenter = new UpdateNamePresenter(mUpdatenameActivity);
        PowerMockito.mockStatic(AppAuthUtils.class);
        appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
    }

    @Test
    public void testUpdateName(){
        PgiWebServiceAPI pgiWebServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPGiClient();
        PowerMockito.mockStatic(PgiWebServiceManager.class);
        Mockito.when(PgiWebServiceManager.getInstance()).thenReturn(mPgiWebServiceManager);
        Mockito.when(PgiWebServiceManager.getInstance().getPgiWebServiceAPI()).thenReturn(pgiWebServiceAPI);
        mUpdatenamePresenter.requestNameUpdate(clientId, firstName, lastName);
        mUpdatenameActivity.onNameUpdateSuccess();
        Mockito.verify(mUpdatenameActivity).onNameUpdateSuccess();
    }

}
