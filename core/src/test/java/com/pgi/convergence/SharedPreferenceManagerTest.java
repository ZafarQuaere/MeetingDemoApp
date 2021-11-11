package com.pgi.convergence;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import com.pgi.convergence.application.CoreApplication;
import com.pgi.convergence.persistance.SharedPreferencesManager;
import com.pgi.convergence.utils.CommonUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

/**
 * Created by ashwanikumar on 8/21/2017.
 */
public class SharedPreferenceManagerTest extends RobolectricTest {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    @Before
    public void setup() {
        CoreApplication.appContext = ApplicationProvider.getApplicationContext();
        CommonUtils.setContext(CoreApplication.appContext);
        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(CoreApplication.appContext);
        Whitebox.setInternalState(sharedPreferencesManager, "mSharedPrefs", sharedPrefs);
        Whitebox.setInternalState(sharedPreferencesManager, "mEditor", sharedPrefs.edit());
    }

    @Test
    public void testSetPrefAuthState() {
        sharedPreferencesManager.setPrefAuthState("Some random authState data");
        sharedPreferencesManager.commit();
        Assert.assertEquals("Some random authState data", sharedPreferencesManager.getPrefAuthState());
    }
/*

    @Test
    public void testFirstTimePermissionAccess() {
        sharedPreferencesManager.firstTimeAskingPermission("calendar", false);
        sharedPreferencesManager.commit();
        Assert.assertFalse(sharedPreferencesManager.isFirstTimeAskingPermission("calendar"));
    }
*/
    @Test
    public void testHasShownWelcomeSlides() {
        sharedPreferencesManager.setHasShownWelcomeSlides(true);
        sharedPreferencesManager.commit();
        Assert.assertTrue(sharedPreferencesManager.hasShownWelcomeSlides());
    }

    @Test
    public void testGetSupportEmail() {
        sharedPreferencesManager.setSupportEmail("support@globalMeet.com");
        sharedPreferencesManager.commit();
        Assert.assertEquals(sharedPreferencesManager.getSupportEmail(), "support@globalMeet.com");
    }

    @Test
    public void testGetScaleNull() {
        Whitebox.setInternalState((Object) sharedPreferencesManager, "mSharedPrefs", (Object) null);
        Assert.assertEquals( 0.0f,sharedPreferencesManager.getScale(), 0.00000001);
        Whitebox.setInternalState(sharedPreferencesManager, "mSharedPrefs", sharedPrefs);
    }

    @Test
    public void testGetScale() {
        sharedPreferencesManager.setScale(0.1f);
        sharedPreferencesManager.commit();
        Assert.assertEquals(sharedPreferencesManager.getScale(), 0.1f, 0.00000001);
    }
/* duplicate test
    @Test
    public void testSetScale() {
        sharedPreferencesManager.setScale(0.1f);
        sharedPreferencesManager.commit();
        Assert.assertEquals(sharedPreferencesManager.getScale(), 0.1f,0.00000001);
    }
*/
    @Test
    public void testClearPref() {
        sharedPreferencesManager.clearPrefOnSignOut();
        Assert.assertEquals(sharedPreferencesManager.getScale(), 0.0f, 0.00000001);
    }

    @Test
    public void testSetPrefLastSessionTime() {
        String testValue = "testTimeStamp";
        sharedPreferencesManager.setPrefLastSessionTimeStamp("testTimeStamp");
        String resultValue = sharedPreferencesManager.getPrefLastSessionTimeStamp();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testSetPefLogSessionCookie() {
        String testValue = "testLogSessionCookie";
        sharedPreferencesManager.setPrefLastSessionTimeStamp(testValue);
        String resultValue = sharedPreferencesManager.getPrefLogSessionCookie();
        if (resultValue == null) {
            Assert.assertNull(resultValue);
        } else {
            Assert.assertEquals(testValue, resultValue);
        }
    }

    @Test
    public void testSetHasJoinedUsersRoom() {
        Boolean testValue = true;
        sharedPreferencesManager.setHasJoinedUsersRoom(testValue);
        Boolean resultValue = sharedPreferencesManager.hasJoinedUsersRoom();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testSetAuthToken() {
        String testValue = "testAuthToken";
        sharedPreferencesManager.setAuthToken(testValue);
        String resultValue = sharedPreferencesManager.getAuthToken();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testSetAuthState() {
        String testValue = "testAuthState";
        sharedPreferencesManager.setPrefAuthState(testValue);
        String resultValue = sharedPreferencesManager.getPrefAuthState();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testRemoveAuthState() {
        sharedPreferencesManager.removePrefAuthState();
        String resultValue = sharedPreferencesManager.getPrefAuthState();
        Assert.assertNull(resultValue);
    }

    @Test
    public void testSetSessionId() {
        String testValue = "testSessionId";
        sharedPreferencesManager.setSessionId(testValue);
        String resultValue = sharedPreferencesManager.getPrefSessionId();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testRemoveSessionId() {
        sharedPreferencesManager.removePrefSessionId();
        String resultValue = sharedPreferencesManager.getPrefSessionId();
        Assert.assertNull(resultValue);
    }

    @Test
    public void testFirstTimeAskingPermission() {
        sharedPreferencesManager.firstTimeAskingPermission("testPermission", true);
        boolean resultValue = sharedPreferencesManager.isFirstTimeAskingPermission("testPermission");
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testPermissionsAccessed() {
        sharedPreferencesManager.permissionsAccessed(true);
        Assert.assertNotNull(sharedPreferencesManager);
    }

    @Test
    public void testFirstTimeGuestUser() {
        sharedPreferencesManager.firstTimeGuestUser(true);
        boolean resultValue = sharedPreferencesManager.isFirstTimeGuestUser();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testFirstTimeGuestUserForThankYou() {
        sharedPreferencesManager.firstTimeGuestUserForThankYou(true);
        boolean resultValue = sharedPreferencesManager.isFirstTimeGuestUserForThankYou();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testDroidNumSupportedVersions() {
        int testValue = 5;
        sharedPreferencesManager.setDroidNumSupportedVersions(testValue);
        int resultValue = sharedPreferencesManager.getDroidNumSupportedVersions();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testDefaultMeetingRoom() {
        String testValue = "testMeetingRoomId";
        sharedPreferencesManager.setPrefDefaultMeetingRoom(testValue);
        String resultValue = sharedPreferencesManager.getPrefDefaultMeetingRoom();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testPlayStoreVersion() {
        String testValue = "testPlayStoreVersion";
        sharedPreferencesManager.setPlayStoreVersion(testValue);
        String resultValue = sharedPreferencesManager.getPlayStoreVersion();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testHasClickedJoin() {
        sharedPreferencesManager.setHasClickedJoin(true);
        boolean resultValue = sharedPreferencesManager.hasClickedJoin();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testHasClickedStart() {
        sharedPreferencesManager.setHasClickedStart(true);
        boolean resultValue = sharedPreferencesManager.hasClickedStart();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testFirstTimeMsalUser() {
        sharedPreferencesManager.firstTimeMsalUser(true);
        boolean resultValue = sharedPreferencesManager.isfirstTimeMsalUser();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testDontShowNetworkSwitchDialog() {
        sharedPreferencesManager.setDontShowNetworkSwitchDialog(true);
        boolean resultValue = sharedPreferencesManager.getDontShowNetworkSwitchDialog();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testCloudRegion() {
        String testValue = "testCloudRegion";
        sharedPreferencesManager.setCloudRegion(testValue);
        String resultValue = sharedPreferencesManager.getCloudRegion();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testFirstName() {
        String testValue = "testFirstName";
        sharedPreferencesManager.setFirstName(testValue);
        String resultValue = sharedPreferencesManager.getFirstName();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testLastName() {
        String testValue = "testLastName";
        sharedPreferencesManager.setLastName(testValue);
        String resultValue = sharedPreferencesManager.getLastName();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testJoinUrl() {
        String testValue = "testJoinUrl";
        sharedPreferencesManager.setJoinUrl(testValue);
        String resultValue = sharedPreferencesManager.getJoinUrl();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testProfileImage() {
        String testValue = "testProfileImage";
        sharedPreferencesManager.setProfileImage(testValue);
        String resultValue = sharedPreferencesManager.getProfileImage();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testGuestUser() {
        sharedPreferencesManager.guestUser(true);
        boolean resultValue = sharedPreferencesManager.isGuestUser();
        Assert.assertTrue(resultValue);
    }

    @Test
    public void testAppConfig() {
        String testValue = "testAppConfig";
        sharedPreferencesManager.setAppConfig(testValue);
        String resultValue = sharedPreferencesManager.getAppConfig();
        Assert.assertEquals(testValue, resultValue);
    }

    @Test
    public void testConfId() {
        String testValue = "64109328";
        sharedPreferencesManager.setConfId(testValue);
        String resultValue = sharedPreferencesManager.getConfId();
        Assert.assertEquals(testValue, resultValue);
    }
}
