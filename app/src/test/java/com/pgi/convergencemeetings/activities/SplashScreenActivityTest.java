package com.pgi.convergencemeetings.activities;

import android.content.Intent;

import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.PermissionUtil;
import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity;
import com.pgi.convergencemeetings.landingpage.ui.SplashScreenActivity;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

/**
 * Created by Sudheer Chilumula on 2019-01-23.
 * PGi
 * sudheer.chilumula@pgi.com
 */
@PrepareForTest({PermissionUtil.class, SharedPreferencesManager.class})
public class SplashScreenActivityTest extends RobolectricTest {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  private SharedPreferencesManager preferencesManager;
  private SplashScreenActivity mSplashScreenActivity;

  @Before
  public void setUp() throws Exception{
    ConvergenceApplication.appContext = RuntimeEnvironment.application.getApplicationContext();
    ConvergenceApplication.mLogger = new TestLogger();
    ActivityController<SplashScreenActivity> controller = Robolectric.buildActivity(SplashScreenActivity.class);
    mSplashScreenActivity = controller.create().get();
    PowerMockito.mockStatic(PermissionUtil.class);
    PowerMockito.when(PermissionUtil.shouldAskPermission()).thenReturn(true);
    PowerMockito.mockStatic(SharedPreferencesManager.class);
    preferencesManager = PowerMockito.mock(SharedPreferencesManager.class);
    PowerMockito.when(SharedPreferencesManager.getInstance()).thenReturn(preferencesManager);
  }

  @Test
  public void shouldNotBeNull() throws Exception{
    Assert.assertNotNull(mSplashScreenActivity);
  }

  @Test
  public void testOnResumeShouldStartOnBoardActivity() {
    mSplashScreenActivity.enablePostAuthorizationFlows();
    Intent expectedIntent = new Intent(mSplashScreenActivity, OnBoardAuthActivity.class);
    ShadowActivity shadowActivity = Shadows.shadowOf(mSplashScreenActivity);
    Intent startedIntent = shadowActivity.getNextStartedActivity();
    ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
    Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
  }

  @Test
  public void testLaunchApplication() {
    mSplashScreenActivity.launchApplicationFlow();
    Intent expectedIntent = new Intent(mSplashScreenActivity, AppBaseLayoutActivity.class);
    ShadowActivity shadowActivity = Shadows.shadowOf(mSplashScreenActivity);
    Intent startedIntent = shadowActivity.getNextStartedActivity();
    ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
    Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
  }
}
