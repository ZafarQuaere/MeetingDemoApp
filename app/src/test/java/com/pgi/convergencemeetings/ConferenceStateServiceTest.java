package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.ConferenceStateModel;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.ConferenceStateService;
import com.pgi.network.PiaServiceAPI;
import com.pgi.network.PiaServiceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
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
public class ConferenceStateServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    ConferenceStateService conferenceStateService;
    int errorCode = 401;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        ConvergenceApplication.mLogger = new TestLogger();
        conferenceStateService = new ConferenceStateService(mMeetingRoomPresenter);
    }

    @Test
    public void testSubscribeToAudioMeeting(){
        conferenceStateService.getConferenceState("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599");
        Mockito.verify(mMeetingRoomPresenter).onConferenceStateSuccess(Matchers.any(ConferenceStateModel.class));
    }

    @Test
    public void testSubscribeToAudioMeetingInactive(){
        conferenceStateService.getConferenceState("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "3729599");
        Mockito.verify(mMeetingRoomPresenter).onConferenceStateSuccess(Matchers.any(ConferenceStateModel.class));
    }

    @Test
    public void testSubscribeToAudioMeetingError(){
        conferenceStateService.getConferenceState("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "");
        Mockito.verify(mMeetingRoomPresenter).onConferenceStateError("{\"ResultCode\":8,\"ResultText\":\"INVALID_CONF\",\"PgsErrorCode\":0}", errorCode);
    }
}
