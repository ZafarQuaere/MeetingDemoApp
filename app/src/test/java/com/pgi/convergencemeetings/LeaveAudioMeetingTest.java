package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.LeaveAudioMeetingService;
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
@PrepareForTest({PiaServiceManager.class, CommonUtils.class})
public class LeaveAudioMeetingTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    LeaveAudioMeetingService leaveAudioMeetingService;
    int errorCode = 200;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        leaveAudioMeetingService = new LeaveAudioMeetingService(mMeetingRoomPresenter);
    }

    @Test
    public void testSubscribeToAudioMeeting(){
        leaveAudioMeetingService.leaveAudioMeeting("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599", "8001-37866308");
        Mockito.verify(mMeetingRoomPresenter).onLeaveConferenceSuccess();
    }
    @Test
    public void testSubscribeToAudioMeetingInvalid(){
        leaveAudioMeetingService.leaveAudioMeeting("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "", "8001-37866308");
        Mockito.verify(mMeetingRoomPresenter).onLeaveConferenceError("INVALID_CONF", errorCode);
    }
}
