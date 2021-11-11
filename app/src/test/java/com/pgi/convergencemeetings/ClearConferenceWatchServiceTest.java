package com.pgi.convergencemeetings;

import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.ClearConferenceWatchService;
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
public class ClearConferenceWatchServiceTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Mock
    PiaServiceManager piaServiceManager;
    ClearConferenceWatchService clearConferenceWatchService;
    int errorCode = 200;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);
        clearConferenceWatchService = new ClearConferenceWatchService(mMeetingRoomPresenter);
    }

    @Test
    public void testSubscribeToAudioMeeting(){
        clearConferenceWatchService.clearConferenceWatch("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599");
        Mockito.verify(mMeetingRoomPresenter).onClearConfereceWatchSuccess();
    }
    @Test
    public void testSubscribeToAudioMeetingInvalid(){
        clearConferenceWatchService.clearConferenceWatch("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "");
        Mockito.verify(mMeetingRoomPresenter).onClearConfereceWatchError("INVALID_CONF", errorCode  );
    }
}
