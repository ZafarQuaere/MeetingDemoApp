package com.pgi.convergencemeetings;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.leftnav.settings.ui.UpdateNameActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.TokenRefreshModel;
import com.pgi.convergencemeetings.leftnav.settings.ui.UpdateNamePresenter;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils;
import com.pgi.network.PgiWebServiceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowInputMethodManager;

import java.io.IOException;

/**
 * Created by ashwanikumar on 12/4/2017.
 */
@PrepareForTest({AppAuthUtils.class, PgiWebServiceManager.class, ClientInfoDaoUtils.class, CommonUtils.class})
public class UpdateNameActivityTest extends RobolectricTest{

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private UpdateNameActivity updateNameActivity;

    String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
    String firstName = "Abhishek";
    String lastName = "Gupta";
    String clientId = "2690509";
    private Context context;
    private AppAuthUtils appAuthUtils;
    private ClientInfoDaoUtils mClientInfoDoaUtils;
    private InputMethodManager manager;
    private ShadowInputMethodManager shadow;

    @Before
    public void setup() throws IOException {
        context = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = context;
        ConvergenceApplication.mLogger = new TestLogger();
        PowerMockito.mockStatic(AppAuthUtils.class);
        appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        Mockito.when(appAuthUtils.getFirstName()).thenReturn(firstName);
        Mockito.when(appAuthUtils.getLastName()).thenReturn(lastName);
        PowerMockito.mockStatic(ClientInfoDaoUtils.class);
        mClientInfoDoaUtils = Mockito.mock(ClientInfoDaoUtils.class);
        Mockito.when(ClientInfoDaoUtils.getInstance()).thenReturn(mClientInfoDoaUtils);
        Mockito.when(mClientInfoDoaUtils.getClientId()).thenReturn("2690509");

        updateNameActivity = Robolectric.buildActivity(UpdateNameActivity.class).create().resume().get();
        manager = (InputMethodManager) RuntimeEnvironment.application.getSystemService(Activity.INPUT_METHOD_SERVICE);
        shadow = Shadows.shadowOf(manager);
        TokenRefreshModel.INSTANCE.setRefreshing(true);
    }

    @Test
    public void testNotNull(){
        Assert.assertNotNull(updateNameActivity);
    }

    @Test
    public void testPopulateData(){
        EditText etFirstName = (EditText) updateNameActivity.findViewById(R.id.et_first_name);
        EditText etLastName = (EditText) updateNameActivity.findViewById(R.id.et_last_name);
        Assert.assertEquals("Abhishek",etFirstName.getText().toString());
        Assert.assertEquals("Gupta",etLastName.getText().toString());
    }

    @Test
    public void testUpdateNameButtonClick(){
        UpdateNamePresenter presenter = Mockito.mock(UpdateNamePresenter.class);
        updateNameActivity.setPresenter(presenter);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.hideKeyboard(updateNameActivity)).thenReturn(true);
        updateNameActivity.onUpdateNameClicked();
        Mockito.verify(presenter).requestNameUpdate(clientId, firstName, lastName);
    }
}
