package com.pgi.convergencemeetings.activities;

import android.content.Intent;
import android.net.Uri;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.landingpage.ui.CustomURIHandleActivity;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;


/**
 * Created by Sudheer Chilumula on 2019-01-30.
 * PGi
 * sudheer.chilumula@pgi.com
 */
public class CustomURIHandleActivityTest extends RobolectricTest {
  private CustomURIHandleActivity mCustomURIHandleActivity;
  private CustomURIHandleActivity mCustomURIHandleActivitySpy;

  @Before
  public void setUp() throws Exception{
    ConvergenceApplication.mLogger = new TestLogger();
    ActivityController<CustomURIHandleActivity> controller = Robolectric.buildActivity(CustomURIHandleActivity.class);
    mCustomURIHandleActivity = controller.create().get();
    mCustomURIHandleActivitySpy = Mockito.spy(mCustomURIHandleActivity);
  }

  @Test
  public void shouldNotBeNull() throws Exception{
    Assert.assertNotNull(mCustomURIHandleActivity);
  }

  @Test
  public void testOnNewIntent() throws Exception{
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Intent expectedIntent = new Intent(mCustomURIHandleActivitySpy, OnBoardAuthActivity.class);
    ShadowActivity shadowActivity = Shadows.shadowOf(mCustomURIHandleActivitySpy);
    intent.setData(Uri.parse("gmmeet://launchRoom?furl=https://pgi.globalmeet.com/test"));
    Mockito.when(mCustomURIHandleActivitySpy.getIntent()).thenReturn(intent);
    mCustomURIHandleActivitySpy.onNewIntent(intent);
    Intent startedIntent = shadowActivity.getNextStartedActivity();
    ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
    Assert.assertTrue(expectedIntent.filterEquals(startedIntent));
  }
}
