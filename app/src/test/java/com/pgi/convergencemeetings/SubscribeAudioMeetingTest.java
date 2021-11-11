package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.SubscribeAudioMeetingService;
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
 * Created by ashwanikumar on 10/23/2017.
 */
@PrepareForTest({PiaServiceManager.class, CommonUtils.class})
public class SubscribeAudioMeetingTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    SubscribeAudioMeetingService subscribeAudioMeetingService;
    String clientId = "7155507";
    int errorCode = 200;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        subscribeAudioMeetingService = new SubscribeAudioMeetingService(mMeetingRoomPresenter);
    }

    @Test
    public void testSubscribeToAudioMeeting(){
        subscribeAudioMeetingService.subscribeToAudioMeeting("asdwercigwnobanstrgfoivbn.kovmaerovnaerivdnaoeivnrao.invoinvameroamvoesvroiG", clientId);
        Mockito.verify(mMeetingRoomPresenter).onSubscribeSuccess("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41");
    }

    @Test
    public void testSubscribeToAudioMeetingTimeout(){
        subscribeAudioMeetingService.subscribeToAudioMeeting("asdwercigwnobanstrgfoivbn.kovmaerovnaerivdnaoeivnrao.timeout", clientId);
        Mockito.verify(mMeetingRoomPresenter).onSubscribeError("TimeOut", errorCode);
    }
}
