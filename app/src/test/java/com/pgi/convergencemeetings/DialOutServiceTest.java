package com.pgi.convergencemeetings;

import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.DialOutService;
import com.pgi.network.PiaServiceAPI;
import com.pgi.network.PiaServiceManager;

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

/**
 * Created by nnennaiheke on 11/17/17.
 */

@PrepareForTest({PiaServiceManager.class})
public class DialOutServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    MeetingRoomPresenter meetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    @Mock
    DialOutService dialOutService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        ConvergenceApplication.mLogger = new TestLogger();
        dialOutService = new DialOutService(meetingRoomPresenter);
    }

    @Test
    public void testDialOutAudioMeeting() {
        dialOutService.dialOutToParticipant("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "121212w", "6788422358", "normal", "inviteParticipant", "Nnenna", "Iheke");
        meetingRoomPresenter.onDialParticipantSuccess(ArgumentMatchers.anyString());
        Mockito.verify(meetingRoomPresenter).onDialParticipantSuccess(ArgumentMatchers.anyString());
    }
}

