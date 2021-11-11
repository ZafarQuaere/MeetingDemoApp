//package com.pgi.convergencemeetings;
//
//import android.app.Dialog;
//import android.widget.TextView;
//
//import com.pgi.convergencemeetings.meeting.gm4.ui.GuestMeetingRoomActivity;
//import com.pgi.convergencemeetings.meeting.gm4.ui.HostMeetingRoomActivity;
//import com.pgi.convergencemeetings.base.ConvergenceApplication;
//import com.pgi.convergencemeetings.utils.AppAuthUtils;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.mockito.MockitoAnnotations;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.robolectric.Robolectric;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowDialog;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * Created by ashwanikumar on 9/26/2017.
// */
//@Config(shadows = {ShadowShimmerlayout.class, ShadowDialog.class})
//@PrepareForTest({AppAuthUtils.class})
//public class MeetingRoomActivityTest extends RobolectricTest {
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//    private AppAuthUtils mAppAuthUtils;
//
//    private GuestMeetingRoomActivity mGuestMeetingRoomActivity;
//    private HostMeetingRoomActivity mHostMeetingRoomActivity;
//
//    @Before
//    public void setUp() throws Exception{
//        MockitoAnnotations.initMocks(this);
//        ConvergenceApplication.mLogger = new TestLogger();
//        mAppAuthUtils = com.pgi.convergencemeetings.networkmocks.FakeRestClient.getInstance().mockAppAuthToken();
//        PowerMockito.mockStatic(AppAuthUtils.class);
//        PowerMockito.when(AppAuthUtils.getInstance()).thenReturn(mAppAuthUtils);
//        mGuestMeetingRoomActivity = Robolectric.buildActivity(GuestMeetingRoomActivity.class )
//            .create()
//            .resume()
//            .get();
//        mHostMeetingRoomActivity = Robolectric.buildActivity(HostMeetingRoomActivity.class )
//            .create()
//            .resume()
//            .get();
//    }
//
//    @Test
//    public void shouldNotBeNull() throws Exception{
//        assertNotNull(mGuestMeetingRoomActivity);
//    }
//
//    @Test
//    public void testGuestExitDialog() throws Exception{
//        Dialog dialog = mGuestMeetingRoomActivity.createGuestExitDialog();
//        dialog.show();
//        Dialog alert =
//            ShadowDialog.getLatestDialog();
//        TextView textView = (TextView)alert.findViewById(R.id.tv_meeting_room_guest_cnfrm_msg);
//
//        assertEquals(textView.getText().toString(), mGuestMeetingRoomActivity.getString(R.string.guest_exit_leave_meeting_confirmation_msg));
//    }
//
//    @Test
//    public void testHostExitDialog() throws Exception{
//        Dialog dialog = mHostMeetingRoomActivity.createHostExitAlertDialog();
//        dialog.show();
//        Dialog alert =
//            ShadowDialog.getLatestDialog();
//        TextView textView = (TextView)alert.findViewById(R.id.tv_host_dialog_cnfrm_msg);
//
//        assertEquals(textView.getText().toString(), mGuestMeetingRoomActivity.getString(R.string.host_exit_leave_meeting_confirmation_msg));
//    }
//}
//
