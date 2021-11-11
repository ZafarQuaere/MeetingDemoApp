package com.pgi.convergencemeetings.activities;

import android.app.Activity;
import android.content.Intent;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.MeetingRoom;
import com.pgi.convergencemeetings.models.MeetingRoomUrls;
import com.pgi.convergencemeetings.models.enterUrlModel.Detail;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthPresenter;
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity;
import com.pgi.convergencemeetings.utils.ClientInfoResultCache;
import com.pgi.network.models.SearchResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;


/**
 * Created by Sudheer Chilumula on 2019-01-30.
 * PGi
 * sudheer.chilumula@pgi.com
 */
public class OnBoardAuthActivityTest extends RobolectricTest {

  private OnBoardAuthActivity mOnBoardAuthActivity;
  @Mock
  private OnBoardAuthPresenter mOnBoardAuthPresenter;

  @Before
  public void setUp() throws Exception{
    ConvergenceApplication.mLogger = new TestLogger();
    MockitoAnnotations.initMocks(this);
    ActivityController<OnBoardAuthActivity> controller = Robolectric.buildActivity(OnBoardAuthActivity.class);
    mOnBoardAuthActivity = controller.create().get();
    mOnBoardAuthActivity.setPresenter(mOnBoardAuthPresenter);
  }

  @Test
  public void shouldNotBeNull() throws Exception{
    Assert.assertNotNull(mOnBoardAuthActivity);
  }

  @Test
  public void testOnResume() throws Exception{
    ActivityController<OnBoardAuthActivity> controller = Robolectric.buildActivity(OnBoardAuthActivity.class);
    mOnBoardAuthActivity = controller.create().resume().get();
    mOnBoardAuthActivity.setPresenter(mOnBoardAuthPresenter);
    Assert.assertNotNull(mOnBoardAuthActivity);

  }

  @Test
  public void testCheckFirstTimeFromGuestBackground() throws Exception{
    FieldSetter.setField(mOnBoardAuthActivity, mOnBoardAuthActivity.getClass().getDeclaredField("firstTimeGuestFromBackground"), true);
    mOnBoardAuthActivity.checkAndLoadEnterUrlFragment();
    Assert.assertEquals(mOnBoardAuthActivity.getViewMode(),5);
  }


  @Test
  public void testOnNewIntent() throws Exception{
    Intent intent = new Intent();
    intent.setAction(AppConstants.AUTHORIZATION_RESPONSE);
    mOnBoardAuthActivity.onNewIntent(intent);
    Mockito.verify(mOnBoardAuthPresenter).handleAuthorizationResponse(Mockito.any());
  }

  @Test
  public void testShouldShowWelcomeView() {
    Boolean should = mOnBoardAuthActivity.shouldShowWelcomeView();
    Assert.assertTrue(should);
  }

  @Test
  public void testCheckForFirstTimeGuest() {
    mOnBoardAuthActivity.checkForFirstTimeGuest(true);
    Assert.assertNotNull(mOnBoardAuthActivity.getViewMode());
  }

  @Test
  public void testStartMeeting() {
    mOnBoardAuthActivity.startMeeting();
    Assert.assertNotNull(mOnBoardAuthActivity.getViewMode());
  }

  @Test
  public void testJoinRoom() {
    mOnBoardAuthActivity.joinRoom();
    Assert.assertNotNull(mOnBoardAuthActivity.getViewMode());
  }

  @Test
  public void testIsAppEnterForeground() {
    Assert.assertNotNull(mOnBoardAuthActivity.isAppEnterForeground());
  }

  @Test
  public void testLaunchApplicationFlow() {
    mOnBoardAuthActivity.launchApplicationFlow(true);
    Assert.assertNotNull(mOnBoardAuthActivity.getViewMode());
  }

  @Test
  public void testOnJoinMeetingCalled() {
    Detail detail = new Detail();
    detail.setConferenceId(12345);
    detail.setUseHtml5(true);
    detail.setMeetingUrl("https://pgi.globalmeet.com/gauravsingh");

    SearchResult result = new SearchResult();
    result.setUseHtml5(true);
    result.setFurl(detail.getMeetingUrl());
    result.setConferenceId(12345);
    detail.setSearchResult(result);

    mOnBoardAuthActivity.onJoinMeetingCalled(detail);

  }

  @Test
  public void testJoinRoomMeeting() {
    ClientInfoResultCache mockCache = PowerMockito.mock(ClientInfoResultCache.class);
    ClientInfoResultCache.setInstance(mockCache);
    MeetingRoomUrls urls = new MeetingRoomUrls();
    urls.setAttendeeJoinUrl("https://pgi.globalmeet.com/gauravsingh");
    MeetingRoom meetingRoom = new MeetingRoom();
    meetingRoom.setMeetingRoomUrls(urls);
    Mockito.doReturn(meetingRoom).when(mockCache).getMeetingRoomData();
    mOnBoardAuthActivity.onClientInfoReceived(null, "12345", "test@pgi.com");
    mOnBoardAuthActivity.joinRoom();
    Assert.assertNotNull(mOnBoardAuthActivity.getViewMode());
    mOnBoardAuthActivity.onClientInfoReceived(null, null, null);
    ClientInfoResultCache.setInstance(null);
  }

  @Test
  public void testOnResumeForGuestFromBackgroundFirstTime() {
    mOnBoardAuthActivity.onResume();
    Assert.assertFalse(mOnBoardAuthActivity.getFirstTimeGuestFromBackground());
  }

  @Test
  public void testOnResumeNotForGuestFromBackgroundFirstTime() {
    mOnBoardAuthActivity.setFirstTimeGuestFromBackground(true);
    mOnBoardAuthActivity.onResume();
    Assert.assertTrue(mOnBoardAuthActivity.getFirstTimeGuestFromBackground());
  }
}
