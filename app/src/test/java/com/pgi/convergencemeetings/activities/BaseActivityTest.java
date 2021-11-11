package com.pgi.convergencemeetings.activities;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.pgi.auth.PGiIdentityAuthService;
import com.pgi.convergence.application.CoreApplication;
import com.pgi.convergence.enums.JoinMeetingEntryPoint;
import com.pgi.convergence.utils.InternetConnection;
import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.ShadowRecentMeetingService;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ui.BaseActivity;
import com.pgi.convergencemeetings.meeting.gm4.ui.GuestMeetingRoomActivity;
import com.pgi.convergencemeetings.meeting.gm4.ui.HostMeetingRoomActivity;
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity;
import com.pgi.convergencemeetings.models.About;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;
import com.pgi.convergencemeetings.utils.ClientInfoResultCache;

import net.openid.appauth.AuthorizationException;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;

import java.lang.ref.WeakReference;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by nnennaiheke on 6/22/18.
 */
@Config(shadows = {ShadowRecentMeetingService.class})
public class BaseActivityTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private BaseActivity mBaseActivity;
    private OnBoardAuthActivity mOnBoardAuthActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CoreApplication.mLogger = new TestLogger();
        CoreApplication.appContext = ApplicationProvider.getApplicationContext();
        ActivityController<BaseActivity> baseActivityController = Robolectric.buildActivity(BaseActivity.class);
        mBaseActivity = baseActivityController.get();
        ActivityController<OnBoardAuthActivity> onBoardController = Robolectric.buildActivity(OnBoardAuthActivity.class);
        mOnBoardAuthActivity = onBoardController.get();
    }

    @Test
    public void redirectToLogin() {
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        mBaseActivity.redirectToLoginPage();
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getIntentClass().getSimpleName(), IsEqual.<String>equalTo(mOnBoardAuthActivity.getClass().getSimpleName()));
    }

    @Test
    public void testOnErrorRdirectToLoginPage() {
        mBaseActivity.onCreate(null);
        PGiIdentityAuthService.Companion.getInstance(mBaseActivity).getAuthExceptionSubject().onNext(AuthorizationException.fromTemplate(
            AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW,
            null));
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        mBaseActivity.redirectToLoginPage();
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getIntentClass().getSimpleName(), IsEqual.<String>equalTo(mOnBoardAuthActivity.getClass().getSimpleName()));
    }

    @Test
    public void testLaunchAudioSelectionActivity() {
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        mBaseActivity.launchAudioSelectionActivity(mBaseActivity, "1223456", "https://pgi.globalmeet.com/test",
            "test", true, 1313123123, JoinMeetingEntryPoint.ENTERED_URL, false, null);
        Intent expectedIntent = new Intent(mBaseActivity, WebMeetingActivity.class);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
    }

    @Test
    public void testLaunchAudioSelectionActivityGM4Host() {
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        mBaseActivity.launchAudioSelectionActivity(mBaseActivity, "1223456", "https://pgi.globalmeet.com/test",
            "test", false, 1313123123, JoinMeetingEntryPoint.ENTERED_URL, true, null);
        Intent expectedIntent = new Intent(mBaseActivity, HostMeetingRoomActivity.class);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
    }

    @Test
    public void testLaunchAudioSelectionActivityGM4Guest() {
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        mBaseActivity.launchAudioSelectionActivity(mBaseActivity, "1223456", "https://pgi.globalmeet.com/test",
            "test", false, 1313123123, JoinMeetingEntryPoint.ENTERED_URL, false, null);
        Intent expectedIntent = new Intent(mBaseActivity, GuestMeetingRoomActivity.class);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
    }

    @Test
    public void testopenEmailApp() {
        ShadowActivity shadowActivity = shadowOf(mBaseActivity);
        String[] organizers= {"pgi@support.com"};
        mBaseActivity.openEmailApp(new WeakReference<>(mBaseActivity), organizers, "Test", "Message");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getAction(), IsEqual.<String>equalTo(Intent.ACTION_SENDTO));
    }

    @Test
    public void testClearAppData() {
        mBaseActivity.clearAppData(mBaseActivity);
        assertFalse(PGiIdentityAuthService.Companion.getInstance(mBaseActivity.getApplicationContext()).authState().getCurrent().isAuthorized());
    }

    @Test
    public void testGetAboutItems() {
        List<About> items = mBaseActivity.getAboutItems(mBaseActivity);
        assertEquals(items.size(), 1);
    }

    @Test
    public void testShowInvalidConferenceAlert() {
        mBaseActivity.showInvalidConferenceAlert(mBaseActivity);
        assertTrue(ShadowAlertDialog.getShownDialogs().get(0).isShowing());
    }

    @Test
    public void testLaunchRoomWithNullFurl() {
        mBaseActivity.launchRoom(null, JoinMeetingEntryPoint.EXTERNAL_LINK, true,null);
        String id = ClientInfoResultCache.getInstance().getSelectedMeetingRoomId();
        assertNull(id);
    }

    @Test
    public void testLaunchRoomWithValidFurl() {
        mBaseActivity.launchRoom("https://pgi.globalmeet.com/PepperBernhardt", JoinMeetingEntryPoint.EXTERNAL_LINK, true , null);
        String id = ClientInfoResultCache.getInstance().getSelectedMeetingRoomId();
        assertNull(id);
    }

    @Test public void testConnectionValuesFalse(){
        mBaseActivity.setTest(false);
        mBaseActivity.setConnectionValues();
    }

    @Test public void testConnectionValues(){
        mBaseActivity.setTest(true);
        mBaseActivity.setConnectionValues();
    }

    @Test
    public void testLogOpenApp() {
        mBaseActivity.logOpenApp();
    }

}
