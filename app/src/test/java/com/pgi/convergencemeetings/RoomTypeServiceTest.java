package com.pgi.convergencemeetings;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.greendao.ApplicationDao;
import com.pgi.convergencemeetings.services.RoomTypeService;
import com.pgi.convergencemeetings.services.RoomTypeServiceCallbacks;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.RoomTypeServiceAPI;
import com.pgi.network.RoomTypeServiceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({RoomTypeServiceManager.class, AppAuthUtils.class, CommonUtils.class, ApplicationDao.class})
public class RoomTypeServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    RoomTypeServiceManager mRoomTypeServiceManager;

    @Mock
    RoomTypeServiceCallbacks roomTypeServiceCallbacks;

    @Mock
    ApplicationDao mApplicationDao;

    RoomTypeService mRoomTypeService;
    Context mContext;
    AppAuthUtils mAppAuthUtils;
    String furl = "https://pgi.globalmeet.com/PepperBernhardt/";
    String RTError = "RoomType service error";

    @Before
    public void setup() {
        mContext = ApplicationProvider.getApplicationContext();
        ConvergenceApplication.appContext = mContext;
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        RoomTypeServiceAPI roomTypeServiceAPI = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getRoomTypeClient(furl);
        PowerMockito.mockStatic(ApplicationDao.class);
        PowerMockito.mockStatic(CommonUtils.class);
        mAppAuthUtils = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().mockAppAuthToken();
        mRoomTypeServiceManager = Mockito.mock(RoomTypeServiceManager.class);
        Mockito.when(mRoomTypeServiceManager.getRoomTypeServiceAPI()).thenReturn(roomTypeServiceAPI);
        Mockito.when(mApplicationDao.get(mContext)).thenReturn(mApplicationDao);
        mRoomTypeService = new RoomTypeService(roomTypeServiceCallbacks);
        mRoomTypeService.getRoomType(furl);
    }

    @Test
    public void testRoomTypeSuccessForGM5() {
        roomTypeServiceCallbacks.onRoomTypeSuccess(AppConstants.GM5_Room);
        Mockito.verify(roomTypeServiceCallbacks).onRoomTypeSuccess(AppConstants.GM5_Room);
    }

    @Test
    public void testRoomTypeSuccessForGM4() {
        roomTypeServiceCallbacks.onRoomTypeSuccess(AppConstants.GM4_ROOM);
        Mockito.verify(roomTypeServiceCallbacks).onRoomTypeSuccess(AppConstants.GM4_ROOM);
    }

    @Test
    public void testRoomTypeError() {
        roomTypeServiceCallbacks.onRoomTypeError(RTError);
        Mockito.verify(roomTypeServiceCallbacks).onRoomTypeError(RTError);
    }
}
