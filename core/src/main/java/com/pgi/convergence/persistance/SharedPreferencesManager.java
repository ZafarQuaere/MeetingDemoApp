package com.pgi.convergence.persistance;

import android.content.Context;
import android.content.SharedPreferences;

import com.pgi.convergence.utils.CommonUtils;


/**
 *
 * Created by ashwanikumar on 8/1/2017.
 */

public class SharedPreferencesManager {
    private static final String SHARED_PREFS = "brandx_prefs";

    // no current references

    public String getPrefLastSessionTimeStamp() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_LOG_SESSION_TIMESTAMP, null);
        } else {
            return null;
        }
    }

    public void setPrefLastSessionTimeStamp(String logTimeStamp) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_LOG_SESSION_TIMESTAMP, logTimeStamp);
            mEditor.apply();
        }
    }

    public String getPrefLogSessionCookie() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_LOG_SESSION_COOKIE, null);
        } else {
            return null;
        }
    }
    public void setPrefLogSessionCookie(String cookie) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_LOG_SESSION_COOKIE, cookie);
            mEditor.apply();
        }
    }

    public void setHasJoinedUsersRoom(boolean hasJoined) {
        if (mSharedPrefs != null) {
            mSharedPrefs.edit()
                    .putBoolean(Keys.KEY_HAS_JOINED_USERS_ROOM, hasJoined)
                    .apply();
        }
    }

    public boolean hasJoinedUsersRoom() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_HAS_JOINED_USERS_ROOM, false);
        } else {
            return false;
        }
    }

    private interface Keys {
        String AUTH_STATE = "AUTH_STATE";
        String KEY_SESSION_ID = "session_id";
        String KEY_PERMISSIONS_ACCESSED = "permission_accessed";
        String KEY_FIRST_TIME_GUEST_USER = "first_time_guest";
        String KEY_DEFAULT_MEETING_ROOM = "default_meeting_room";
        String KEY_FIRST_TIME_GUEST_USER_FOR_THANK_YOU = "first_time_guest_thankyou";
        String KEY_HAS_SHOWN_WELCOME_SLIDES  = "KEY_HAS_SHOWN_WELCOME_SLIDES";
        String KEY_DONT_SHOW_NETWORK_SWITCH_DIALOG  = "KEY_DONT_SHOW_NETWORK_SWITCH_DIALOG";
        String KEY_HAS_CLICKED_JOIN  = "KEY_HAS_CLICKED_JOIN";
        String KEY_HAS_CLICKED_START  = "KEY_HAS_CLICKED_START";
        String KEY_LOG_SESSION_TIMESTAMP = "log_session_timestamp";
        String KEY_LOG_SESSION_COOKIE = "log_session_cookie";
        String KEY_CLOUD_REGION  = "KEY_CLOUD_REGION";
        String KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN";
        String KEY_HAS_JOINED_USERS_ROOM = "KEY_HAS_JOINED_USERS_ROOM";
        String MSAL_FIRST_TIME_USER = "MSAL_FIRST_TIME_USER";
        String KEY_FIRST_NAME = "KEY_FIRST_NAME";
        String KEY_LAST_NAME = "KEY_LAST_NAME";
        String KEY_JOIN_URL = "KEY_JOIN_URL";
        String KEY_PROFILE_IMAGE = "KEY_PROFILE_IMAGE";
        String KEY_GUEST_USER = "KEY_GUEST_USER";
        String KEY_DROID_NUM_SUPPORTED_VERSIONS = "KEY_DROID_NUM_SUPPORTED_VERSIONS";
        String KEY_PLAY_STORE_VERSION = "KEY_PLAY_STORE_VERSION";
        String KEY_SUPPORT_EMAIL = "KEY_SUPPORT_EMAIL";
        String KEY_CONFIG_JSON = "KEY_CONFIG_JSON";
        String KEY_SCALE = "KEY_SCALE";
        String KEY_CONF_ID = "KEY_CONF_ID";
    }

    private final SharedPreferences mSharedPrefs;
    private static SharedPreferencesManager sInstance;
    private final SharedPreferences.Editor mEditor;

    public static SharedPreferencesManager getInstance() {
        if (sInstance == null) {
            sInstance = new SharedPreferencesManager();
        }

        return sInstance;
    }

    private SharedPreferencesManager() {
        Context ctx = CommonUtils.getCtx();
        if (ctx != null) {
            mSharedPrefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            if (mSharedPrefs != null) {
                mEditor = mSharedPrefs.edit();
                mEditor.apply();
            } else {
                mEditor = null;
            }
        } else {
            mEditor = null;
            mSharedPrefs = null;
        }
    }

    public float getScale() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getFloat(Keys.KEY_SCALE, 0.0f);
        } else {
            return 0.0f;
        }

    }

    public void setScale(float scale) {
        if (mEditor != null) {
            mEditor.putFloat(Keys.KEY_SCALE, scale);
            mEditor.apply();
        }
    }

    public String getAuthToken() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_AUTH_TOKEN, null);
        } else {
            return null;
        }

    }

    public void setAuthToken(String authToken) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_AUTH_TOKEN, authToken);
            mEditor.apply();
        }
    }

    public void setPrefAuthState(String authState) {
        if (mEditor != null) {
            mEditor.putString(Keys.AUTH_STATE, authState);
            mEditor.apply();
        }
    }

    public String getPrefAuthState() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.AUTH_STATE, null);
        } else {
            return null;
        }
    }

    public void removePrefAuthState() {
        if (mEditor != null) {
            mEditor.remove(Keys.AUTH_STATE);
            mEditor.apply();
        }
    }

    public void setSessionId(String sessionId) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_SESSION_ID, sessionId);
            mEditor.apply();
        }
    }

    public String getPrefSessionId() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_SESSION_ID, null);
        } else {
            return null;
        }
    }

    public void removePrefSessionId() {
        if (mEditor != null) {
            mEditor.remove(Keys.KEY_SESSION_ID);
            mEditor.apply();
        }
    }

    public void commit() {
        if (mEditor != null) {
            mEditor.apply();
        }
    }

    public void firstTimeAskingPermission(String permission, boolean isFirstTime) {
        if (mEditor != null) {
            mEditor.putBoolean(permission, isFirstTime);
            mEditor.apply();
        }
    }

    public boolean isFirstTimeAskingPermission(String permission) {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(permission, true);
        } else {
            return true;
        }
    }

    public void permissionsAccessed(boolean permissionAccessed) {
        if (mEditor != null) {
            mEditor.putBoolean(Keys.KEY_PERMISSIONS_ACCESSED, permissionAccessed);
            mEditor.apply();
        }
    }

    public void firstTimeGuestUser(boolean firstTimeGuest) {
        if (mEditor != null) {
            mEditor.putBoolean(Keys.KEY_FIRST_TIME_GUEST_USER, firstTimeGuest);
            mEditor.apply();
        }
    }

    public boolean isFirstTimeGuestUser() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_FIRST_TIME_GUEST_USER, true);
        } else {
            return true;
        }
    }

    public void firstTimeGuestUserForThankYou(boolean firstTimeGuest) {
        if (mEditor != null) {
            mEditor.putBoolean(Keys.KEY_FIRST_TIME_GUEST_USER_FOR_THANK_YOU, firstTimeGuest);
            mEditor.apply();
        }
    }

    public boolean isFirstTimeGuestUserForThankYou() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_FIRST_TIME_GUEST_USER_FOR_THANK_YOU, false);
        } else {
            return false;
        }
    }

    public int getDroidNumSupportedVersions(){
        if (mSharedPrefs != null) {
            return mSharedPrefs.getInt(Keys.KEY_DROID_NUM_SUPPORTED_VERSIONS, 0);
        } else {
            return 0;
        }
    }

    public void setDroidNumSupportedVersions(int numSupportedVersions) {
        if (mEditor != null) {
            mEditor.putInt(Keys.KEY_DROID_NUM_SUPPORTED_VERSIONS, numSupportedVersions);
            mEditor.apply();
        }
    }

    public void setPrefDefaultMeetingRoom(String meetingRoomId) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_DEFAULT_MEETING_ROOM, meetingRoomId);
            mEditor.apply();
        }
    }

    public String getPrefDefaultMeetingRoom() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_DEFAULT_MEETING_ROOM, "");
        } else {
            return "";
        }
    }
    public String getPlayStoreVersion(){
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_PLAY_STORE_VERSION, "");
        } else {
            return "";
        }
    }

    public void setPlayStoreVersion(String version) {
        if (mSharedPrefs != null) {
            mSharedPrefs.edit()
                    .putString(Keys.KEY_PLAY_STORE_VERSION, version)
            .apply();
        }
    }

    public void setHasShownWelcomeSlides(boolean hasShown) {
        if (mSharedPrefs != null) {
            mSharedPrefs.edit()
                    .putBoolean(Keys.KEY_HAS_SHOWN_WELCOME_SLIDES, hasShown)
                    .apply();
        }
    }

    public boolean hasShownWelcomeSlides() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_HAS_SHOWN_WELCOME_SLIDES, false);
        } else {
            return false;
        }
    }

    public void setHasClickedJoin(boolean hasShown) {
        if (mSharedPrefs != null) {
            mSharedPrefs.edit()
                    .putBoolean(Keys.KEY_HAS_CLICKED_JOIN, hasShown)
                    .apply();
        }
    }

    public boolean hasClickedJoin() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_HAS_CLICKED_JOIN, false);
        } else {
            return false;
        }
    }

    public void setHasClickedStart(boolean hasShown) {
        if (mSharedPrefs != null) {
            mSharedPrefs.edit()
                    .putBoolean(Keys.KEY_HAS_CLICKED_START, hasShown)
                    .apply();
        }
    }

    public boolean hasClickedStart() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_HAS_CLICKED_START, false);
        } else {
            return false;
        }
    }

    public void firstTimeMsalUser(boolean firstTimeMsal) {
        if (mEditor != null) {
            mEditor.putBoolean(Keys.MSAL_FIRST_TIME_USER, firstTimeMsal);
            mEditor.apply();
        }
    }

    public boolean isfirstTimeMsalUser() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.MSAL_FIRST_TIME_USER, true);
        } else {
            return true;
        }
    }


    public void clearPrefOnSignOut() {
        if (mEditor != null) {
            mEditor.remove(Keys.AUTH_STATE);
            mEditor.remove(Keys.KEY_DEFAULT_MEETING_ROOM);
            mEditor.remove(Keys.KEY_SESSION_ID);
            mEditor.remove(Keys.KEY_FIRST_TIME_GUEST_USER);
            mEditor.remove(Keys.KEY_DONT_SHOW_NETWORK_SWITCH_DIALOG);
            mEditor.remove(Keys.KEY_HAS_CLICKED_JOIN);
            mEditor.remove(Keys.KEY_HAS_JOINED_USERS_ROOM);
            mEditor.remove(Keys.KEY_HAS_CLICKED_START);
            mEditor.remove(Keys.KEY_CLOUD_REGION);
            mEditor.remove(Keys.KEY_DROID_NUM_SUPPORTED_VERSIONS);
            mEditor.remove(Keys.KEY_CONFIG_JSON);
            mEditor.remove(Keys.KEY_SCALE);
            mEditor.remove(Keys.KEY_CONF_ID);
            mEditor.apply();
        }
    }

    public void setDontShowNetworkSwitchDialog(boolean shouldDontShow){
        if (mEditor != null) {
            mEditor.putBoolean(Keys.KEY_DONT_SHOW_NETWORK_SWITCH_DIALOG, shouldDontShow);
            mEditor.apply();
        }
    }

    public boolean getDontShowNetworkSwitchDialog(){
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_DONT_SHOW_NETWORK_SWITCH_DIALOG, false);
        } else {
            return false;
        }
    }

    public void setCloudRegion(String cloudRegion){
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_CLOUD_REGION, cloudRegion);
            mEditor.apply();
        }
    }

    public String getCloudRegion(){
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_CLOUD_REGION, null);
        } else {
            return null;
        }
    }

    public String getFirstName() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_FIRST_NAME, null);
        } else {
            return null;
        }

    }

    public void setFirstName(String firstName) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_FIRST_NAME, firstName);
            mEditor.apply();
        }
    }

    public String getLastName() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_LAST_NAME, null);
        } else {
            return null;
        }

    }

    public void setLastName(String lastName) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_LAST_NAME, lastName);
            mEditor.apply();
        }
    }

    public String getJoinUrl() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_JOIN_URL, null);
        } else {
            return null;
        }

    }

    public void setJoinUrl(String joinUrl) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_JOIN_URL, joinUrl);
            mEditor.apply();
        }
    }

    public String getProfileImage() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_PROFILE_IMAGE, null);
        } else {
            return null;
        }

    }

    public void setProfileImage(String profileImage) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_PROFILE_IMAGE, profileImage);
            mEditor.apply();
        }
    }

    public void guestUser(boolean guestUser) {
        if (mEditor != null) {
            mEditor.putBoolean(Keys.KEY_GUEST_USER, guestUser);
            mEditor.apply();
        }
    }

    public boolean isGuestUser() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getBoolean(Keys.KEY_GUEST_USER, true);
        } else {
            return true;
        }
    }

    public String getSupportEmail() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_SUPPORT_EMAIL, null);
        } else {
            return null;
        }

    }

    public void setSupportEmail(String supportEmail) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_SUPPORT_EMAIL, supportEmail);
            mEditor.apply();
        }
    }

    public String getAppConfig() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_CONFIG_JSON, null);
        } else {
            return null;
        }

    }

    public void setAppConfig(String config) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_CONFIG_JSON, config);
            mEditor.apply();
        }
    }

    public String getConfId() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getString(Keys.KEY_CONF_ID, null);
        } else {
            return "";
        }
    }

    public void setConfId(String confId) {
        if (mEditor != null) {
            mEditor.putString(Keys.KEY_CONF_ID, confId);
            mEditor.apply();
        }
    }
}