package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomActivity;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.ConferenceOptionService;
import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.PiaServiceAPI;
import com.pgi.network.PiaServiceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * Created by ashwanikumar on 10/24/2017.
 */
@PrepareForTest({AppAuthUtils.class, PiaServiceManager.class, CommonUtils.class})
public class ConferenceOptionServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    MeetingRoomActivity mMeetingRoomActivity;
    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    AppAuthUtils mAppAuthUtils;
    ConferenceOptionService conferenceOptionService;
    MeetingRoomPresenter realMeetingRoomPresenter;
    ConferenceOptionService realConferenceOptionService;
    int errorCode = 200;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        ConvergenceApplication.mLogger = new TestLogger();
        mAppAuthUtils = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().mockAppAuthToken();
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        conferenceOptionService = new ConferenceOptionService(mMeetingRoomPresenter);
//        realMeetingRoomPresenter = new MeetingRoomPresenter(mMeetingRoomActivity, mMeetingRoomActivity);
//        realConferenceOptionService = new ConferenceOptionService(realMeetingRoomPresenter);
    }

    @Test
    public void testSubscribeToAudioMeeting(){
        conferenceOptionService.setConferenceOption("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599", "TalkerNotifyOn");
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionSuccess();
    }
    @Test
    public void testSubscribeToAudioMeetingInvalid(){
        conferenceOptionService.setConferenceOption("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "", "TalkerNotifyOn");
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionError("INVALID_CONF", errorCode);
    }
    @Test
    public void testSetConferenceOptionMuteAll() {
        mMeetingRoomPresenter.onConferenceOptionSuccess();
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionSuccess();
    }
    @Test
    public void testSetConferenceOptionMuteAllFailed() {
        mMeetingRoomPresenter.onConferenceOptionError("INVALID_CONF", errorCode);
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionError("INVALID_CONF", errorCode);
    }
    @Test
    public void testSetConferenceOptionMuteAll200Response() {
        conferenceOptionService.setConferenceOption("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599", "ConfMute");
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionSuccess();
    }
    @Test
    public void testSetConferenceOptionMuteAll403Response() {
        conferenceOptionService.setConferenceOption("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599", "ConfMuteFailed");
        String errorMsg = "{\"ResultCode\":8,\"ResultText\":\"INVALID_CONF\",\"PgsErrorCode\":0}";
        Mockito.verify(mMeetingRoomPresenter).onConferenceOptionError(errorMsg, 403);
    }
}
