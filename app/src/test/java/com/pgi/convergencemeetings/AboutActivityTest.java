package com.pgi.convergencemeetings;

import android.content.Context;
import android.content.Intent;

import com.pgi.convergencemeetings.leftnav.about.ui.AboutActivity;
import com.pgi.convergencemeetings.leftnav.about.ui.AboutWebViewActivity;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by amit1829 on 11/16/2017.
 */
public class AboutActivityTest extends RobolectricTest {


    @Mock
    Context mMockContext;
    private AboutActivity mAboutActivity;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        ActivityController<AboutActivity> controller = Robolectric.buildActivity(AboutActivity.class);
        mAboutActivity = controller.create().get();
    }

    @Test
    public void shouldNotBeNull() throws Exception{
        assertNotNull(mAboutActivity);
    }

    @Test
    public void testDialogVoipAction() throws IOException {

        mAboutActivity.aboutItemClicked(0);
        ShadowActivity shadowActivity = Shadows.shadowOf(mAboutActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
        Assert.assertEquals(shadowIntent.getIntentClass().getSimpleName(), AboutWebViewActivity.class.getSimpleName());
    }

    @Test
    public void testShowSoftwareCredits(){
        Mockito.when(mMockContext.getString(R.string.software_credit_url)).thenReturn("software credits available ");
        mAboutActivity.initAboutItems(true);
        Assertions.assertThat(mAboutActivity.rvAboutView.getAdapter() != null);
    }
    @Test
    public void close(){
        mAboutActivity.onAboutClose();
        assertTrue(mAboutActivity.isFinishing());
    }
    @After
    public void tearDown(){
        mAboutActivity = null;
    }
}