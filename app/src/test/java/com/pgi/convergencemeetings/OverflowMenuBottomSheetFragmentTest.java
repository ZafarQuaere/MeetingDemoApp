package com.pgi.convergencemeetings;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.pgi.convergence.application.CoreApplication;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity;
import com.pgi.convergencemeetings.meeting.gm5.ui.misc.OverflowMenuDialogFragment;
import com.pgi.convergencemeetings.utils.AppAuthUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import static org.junit.Assert.assertNotNull;

@PrepareForTest({SharedPreferencesManager.class, AppAuthUtils.class})
public class OverflowMenuBottomSheetFragmentTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    BottomSheetDialogFragment bottomSheetDialogFragment;
    @Mock
    private WebMeetingActivity mWebMeetingActivity;
    @Mock
    private OverflowMenuDialogFragment overflowMenuDialogFragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        CoreApplication.appContext = context;
        CoreApplication.mLogger = new TestLogger();
        PowerMockito.mockStatic(SharedPreferencesManager.class);
        PowerMockito.mockStatic(AppAuthUtils.class);
        ActivityController<WebMeetingActivity> controller = Robolectric.buildActivity(WebMeetingActivity.class);
        mWebMeetingActivity = controller.get();

    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(overflowMenuDialogFragment);
        assertNotNull(mWebMeetingActivity);

    }

    @Test
    public void testOnChangeConnectionClicked() {
        overflowMenuDialogFragment.onChangeConnectionClicked();
        Mockito.verify(overflowMenuDialogFragment, Mockito.atLeastOnce()).onChangeConnectionClicked();
//      verify(overflowMenuDialogFragment).dismiss();

    }

    @Test
    public void testOnDisconnectAudioClicked() {
        overflowMenuDialogFragment.onDisconnectAudioClicked();
        Mockito.verify(overflowMenuDialogFragment, Mockito.atLeastOnce()).onDisconnectAudioClicked();
    }
//    @Test
//    public void testOnHelpClicked() {
//        overflowMenuDialogFragment.onHelpClicked();
////      verify(overflowMenuDialogFragment).dismiss();
//        Mockito.verify(overflowMenuDialogFragment, Mockito.atLeastOnce()).onHelpClicked();
//        ShadowActivity shadowActivity = Shadows.shadowOf(mWebMeetingActivity);
//        Intent startedIntent = shadowActivity.getNextStartedActivity();
//        if (startedIntent != null) {
//            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
//            Assert.assertEquals(shadowIntent.getIntentClass().getSimpleName(), HelpMenuActivity.class.getSimpleName());
//        }
//    }

}
