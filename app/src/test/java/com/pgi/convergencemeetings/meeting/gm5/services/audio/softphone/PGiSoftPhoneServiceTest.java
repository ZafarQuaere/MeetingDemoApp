package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity;
import com.pgi.network.models.Cave;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

@PrepareForTest({PGiSoftPhone.class})
public class PGiSoftPhoneServiceTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    private PGiSoftPhoneImpl mPGiSoftPhoneImpl;
    @Mock
    private NotificationManager mockNotificationManager;

    private Context mContext;
    private PGiSoftPhoneService pGiSoftPhoneServiceSpy;

    @InjectMocks
    private PGiSoftPhoneService mPGiSoftPhoneService;

    private Context contextSpy;

    @Before
    public void setUp() throws Exception {
        ConvergenceApplication.mLogger = new TestLogger();
        MockitoAnnotations.initMocks(this);
        PowerMockito.whenNew(PGiSoftPhoneImpl.class).withAnyArguments().thenReturn(mPGiSoftPhoneImpl);
        Mockito.when(mPGiSoftPhoneImpl.isCallConnected()).thenReturn(true);
        mContext = RuntimeEnvironment.application.getApplicationContext();
        ConvergenceApplication.appContext = mContext;
        mPGiSoftPhoneService.Companion.setMPGiSoftPhone(mPGiSoftPhoneImpl);
        Whitebox.setInternalState(mPGiSoftPhoneService, "mNotificationManager",
                mockNotificationManager);
        pGiSoftPhoneServiceSpy = PowerMockito.spy(mPGiSoftPhoneService);
        Notification notification = Mockito.mock(Notification.class);
        contextSpy = PowerMockito.spy(mContext);
        PowerMockito.when(contextSpy.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager);
        PowerMockito.doReturn(notification).when(pGiSoftPhoneServiceSpy).createAndUpdateVoipNotification();
        PowerMockito.doReturn(notification).when(pGiSoftPhoneServiceSpy).createOnGoingMeetingNotification();
    }

    @Test
    public void testServiceIsNotNull() throws Exception {
        Assert.assertNotNull(mPGiSoftPhoneService);
    }

    @Test
    public void testStickyService() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_REQUEST_FOCUS.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onCreate();
        long result = pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 1);
        Assert.assertEquals(Service.START_STICKY, result);
    }

    @Test
    public void testNonStickyService() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_REQUEST_FOCUS.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        long result = pGiSoftPhoneServiceSpy.onStartCommand(null, 0, 1);
        Assert.assertEquals(Service.START_NOT_STICKY, result);
    }

    @Test
    public void testVoipInitAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_INIT.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.Companion.setMPGiSoftPhone(mPGiSoftPhoneImpl);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 0);
        Assert.assertNotNull(pGiSoftPhoneServiceSpy.Companion.getMPGiSoftPhone());
    }

    @Test
    public void testVoipDialoutAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.putExtra("sipFromAddress", "sip:GM.a0rxnnrt66jxm2t9hnucvplya@pgi.com");
        intent.putExtra("formattedSipUrl", "sip:7777009111@bob.mobile.sip.pgiconnect.com:5070;pgilink=GM;lang=en;passcode=729476");
        intent.putExtra("caveInfo", new Cave("wss://edge.cave.pgibob.com:443", "edge.cave.pgibob.com:5061", "https://rope.cave.pgibob.com/turn"));
        intent.setAction(PGiSoftPhoneConstants.VOIP_DIALOUT.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 1);
        Assert.assertEquals(mPGiSoftPhoneService.getMfurl(), null);
    }

    @Test
    public void testActivateSpeakerAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_ACTIVATE_SPEAKER.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 2);
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).activateSpeaker();
        Mockito.verify(mPGiSoftPhoneImpl).activateSpeaker();
    }

    @Test
    public void testHangupAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_STOP.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 3);
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).hangUp();
        Mockito.verify(mPGiSoftPhoneImpl).hangUp();
    }

    @Test
    public void testAudioPauseAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_PAUSE.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 4);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).pauseAudio();
        Mockito.verify(mPGiSoftPhoneImpl).pauseAudio();
    }

    @Test
    public void testAudioResumeAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_RESUME.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 5);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).resumeAudio();
        Mockito.verify(mPGiSoftPhoneImpl).resumeAudio();
    }

    @Test
    public void testRaiseVolumeAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_RAISE_VOLUME.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).raiseVolume(false);
        Mockito.verify(mPGiSoftPhoneImpl).raiseVolume(false);
    }

    @Test
    public void testLowerVolumeAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_LOWER_VOLUME.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).lowerVolume(false);
        Mockito.verify(mPGiSoftPhoneImpl).lowerVolume(false);
    }

    @Test
    public void testMicMute() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_MIC_MUTED.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);
        Assert.assertEquals(true, pGiSoftPhoneServiceSpy.getMute());
    }

    @Test
    public void testMicActive() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_MIC_ACTIVE.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);
        Assert.assertEquals(false, pGiSoftPhoneServiceSpy.getMute());
    }

    @Test
    public void testReleaseFocus() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_RELEASE_FOCUS.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).releaseFocus();
        Mockito.verify(mPGiSoftPhoneImpl).releaseFocus();
    }

    @Test
    public void testRequestFocus() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_REQUEST_FOCUS.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).requestFocus();
        Mockito.verify(mPGiSoftPhoneImpl).requestFocus();
    }

    @Test
    public void testStopService() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.STOP_SERVICE.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).hangUp();
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).destroy();

        Mockito.verify(mPGiSoftPhoneImpl).hangUp();
        Mockito.verify(mPGiSoftPhoneImpl).destroy();
    }


    @Test
    public void testAudioRouteAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_SET_AUDIO_ROUTE.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);
        Mockito.verify(mPGiSoftPhoneImpl).setDefaultAudioRoute();
    }

    @Test
    public void testDestroyAction() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_DESTROY.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 6);

        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).hangUp();
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).destroy();

        Mockito.verify(mPGiSoftPhoneImpl).hangUp();
        Mockito.verify(mPGiSoftPhoneImpl).destroy();
    }

    @Test
    public void testVoIPCallQualityOnHangup() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_STOP.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        Mockito.when(mPGiSoftPhoneImpl.getCallQuality()).thenReturn("Good");
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 3);
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).hangUp();
        Mockito.verify(mPGiSoftPhoneImpl).getCallQuality();
    }

    @Test
    public void testVoIPCallQualityIsEmpty() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.setAction(PGiSoftPhoneConstants.VOIP_STOP.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        Mockito.when(mPGiSoftPhoneImpl.getCallQuality()).thenReturn("");
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 3);
        PowerMockito.doNothing().when(mPGiSoftPhoneImpl).hangUp();
        Mockito.verify(mPGiSoftPhoneImpl).getCallQuality();
    }

    @Test
    public void testVoipDialoutActionCaveNull() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.putExtra("sipFromAddress", "sip:GM.a0rxnnrt66jxm2t9hnucvplya@pgi.com");
        intent.putExtra("formattedSipUrl", "sip:7777009111@bob.mobile.sip.pgiconnect.com:5070;pgilink=GM;lang=en;passcode=729476");
        intent.putExtra("caveInfo", new Cave("wss://edge.cave.pgibob.com:443", null, "https://rope.cave.pgibob.com/turn"));
        intent.setAction(PGiSoftPhoneConstants.VOIP_DIALOUT.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 1);
        Assert.assertEquals(mPGiSoftPhoneService.getMfurl(), null);

        Intent intentNull = new Intent(mContext, PGiSoftPhoneService.class);
        intentNull.putExtra("targetClass", WebMeetingActivity.class.getName());
        intentNull.putExtra("sipFromAddress", "sip:GM.a0rxnnrt66jxm2t9hnucvplya@pgi.com");
        intentNull.putExtra("formattedSipUrl", "sip:7777009111@bob.mobile.sip.pgiconnect.com:5070;pgilink=GM;lang=en;passcode=729476");
        intentNull.putExtra("caveInfo", new Cave());
        intentNull.setAction(PGiSoftPhoneConstants.VOIP_DIALOUT.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intentNull);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 1);
        Assert.assertEquals(mPGiSoftPhoneService.getMfurl(), null);
    }

    @Test
    public void testVoipDialoutActionNullIntent() throws Exception {
        Intent intent = new Intent(mContext, PGiSoftPhoneService.class);
        intent.putExtra("targetClass", WebMeetingActivity.class.getName());
        intent.putExtra("sipFromAddress", "sip:GM.a0rxnnrt66jxm2t9hnucvplya@pgi.com");
        intent.putExtra("formattedSipUrl", "sip:7777009111@bob.mobile.sip.pgiconnect.com:5070;pgilink=GM;lang=en;passcode=729476");
        intent.setAction(PGiSoftPhoneConstants.VOIP_DIALOUT.name());
        PGiSoftPhoneService.Companion.start(contextSpy, intent);
        pGiSoftPhoneServiceSpy.onStartCommand(intent, 0, 1);
        Assert.assertEquals(mPGiSoftPhoneService.getMfurl(), null);
    }

}

