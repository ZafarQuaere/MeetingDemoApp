package com.pgi.convergencemeetings;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.meeting.gm4.ui.MeetingRoomPresenter;
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.ConferenceWatchService;
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

import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by nnennaiheke on 10/26/17.
 */

@PrepareForTest({PiaServiceManager.class, CommonUtils.class, SharedPreferencesManager.class})
public class ConferenceWatchServiceTest extends RobolectricTest {
    @Mock
    MeetingRoomPresenter mMeetingRoomPresenter;
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    PiaServiceManager piaServiceManager;

    ConferenceWatchService conferenceWatchService;
    int errorCode = 200;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PiaServiceAPI piaServiceApi = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().getPiaClient();
        PowerMockito.mockStatic(PiaServiceManager.class);
        Mockito.when(PiaServiceManager.getInstance()).thenReturn(piaServiceManager);
        Mockito.when(PiaServiceManager.getInstance().getPiaServiceAPI()).thenReturn(piaServiceApi);

        conferenceWatchService = new ConferenceWatchService(mMeetingRoomPresenter);
    }

    @Test
    public void testSetConferenceWatch() {
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        String sessionId = "FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41";
        String conferenceId = "113729599";
        conferenceWatchService.setConferenceWatch(sessionId, conferenceId);
        Mockito.verify(mMeetingRoomPresenter, times(1)).onConferenceWatchSuccess();
    }


    @Test
    public void testSubscribeToAudioMeeting() {
        conferenceWatchService.setConferenceWatch("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "113729599");
        Mockito.verify(mMeetingRoomPresenter).onConferenceWatchSuccess();
    }

    @Test
    public void testSubscribeToAudioMeetingInvalid() {
        conferenceWatchService.setConferenceWatch("FB7E4096-A6ED-4AB0-ADDB-0A1527A46C41", "");
        Mockito.verify(mMeetingRoomPresenter).onConferenceWatchError("ConferenceNotFound", errorCode);
    }
}