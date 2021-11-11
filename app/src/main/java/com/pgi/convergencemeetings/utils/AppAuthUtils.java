package com.pgi.convergencemeetings.utils;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.pgi.auth.PGiIdentityAuthService;
import com.pgi.auth.models.AccessTokenKazooProfile;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.enums.PgiUserTypes;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.SecurityToken;
import com.pgi.logging.Logger;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;

import net.openid.appauth.AuthState;

public class AppAuthUtils {

    private static AppAuthUtils mAppAuthUtils;
    private static SecurityToken mSecurityToken;
    private Logger mlogger = ConvergenceApplication.mLogger;
    private String TAG = AppAuthUtils.class.getSimpleName();

    private AppAuthUtils() {
        //Private constructor
    }

    public static AppAuthUtils getInstance() {
        if (mAppAuthUtils == null) {
            mAppAuthUtils = new AppAuthUtils();
            mSecurityToken = mAppAuthUtils.populateSecurityToken();
        }
        return mAppAuthUtils;
    }

    public void setLogger(Logger value ) {mlogger = value;}

    public AuthState getAuthState() {
        return PGiIdentityAuthService.Companion.getInstance(ConvergenceApplication.appContext).authState().getCurrent();
    }

    public String getAccessToken() {
        return getAuthState().getAccessToken();
    }

    public String getRefreshToken() {
        return getAuthState().getRefreshToken();
    }

    private SecurityToken populateSecurityToken() {
        mSecurityToken = null;
        if (getAuthState().getIdToken() != null) {
            mSecurityToken = parseJWTToken(getAuthState().getIdToken());
        }
        else {
            mlogger.error(TAG, LogEvent.API_IDENTITY, LogEventValue.IDENTITY_USERPROFILE, "security token null", null, null , true, true);
        }
        return mSecurityToken;
    }

    public String getFirstName() {
        String firstName = null;
        if(populateSecurityToken() != null) {
            firstName = mSecurityToken.getGivenName();
        }
        return firstName;
    }

    public String getProfileImage() {
        String profileImage = null;
        if(populateSecurityToken() != null) {
            profileImage = mSecurityToken.getProfileImageUrl();
        }
        return profileImage;
    }

    public String getLastName() {
        String lastName = null;
        if(populateSecurityToken() != null) {
            lastName = mSecurityToken.getFamilyName();
        }
        return lastName;
    }

    public String getEmailId() {
        String email = null;
        if(populateSecurityToken() != null) {
            email = mSecurityToken.getEmail();
        }
        return email;
    }

    public String getPgiUserType() {
        String userType = null;
        if(populateSecurityToken() != null) {
            userType = mSecurityToken.getPgiUserType();
        }
        return userType;
    }

    public AccessTokenKazooProfile getKazooInfo() {
        AccessTokenKazooProfile profile = null;
        if(getAccessToken() != null) {
            JWT jwt = new JWT(getAccessToken());
            profile = new Gson().fromJson(jwt.getClaim(AppConstants.ID_APP_INFO).asString(), AccessTokenKazooProfile.class);
        }
        return profile;
    }

    public boolean isUserTypeGuest() {
        return PgiUserTypes.USER_TYPE_GUEST.toString().equalsIgnoreCase(getPgiUserType());
    }

    private SecurityToken parseJWTToken(String idToken) {
        SecurityToken securityToken = new SecurityToken();
        JWT jwt = new JWT(idToken);
        securityToken.setGivenName(jwt.getClaim(AppConstants.PGI_IDTOKEN_GIVEN_NAME).asString());
        securityToken.setEmail(jwt.getClaim(AppConstants.PGI_IDTOKEN_EMAIL).asString());
        securityToken.setName(jwt.getClaim(AppConstants.PGI_IDTOKEN_NAME).asString());
        securityToken.setFamilyName(jwt.getClaim(AppConstants.PGI_IDTOKEN_FAMILY_NAME).asString());
        securityToken.setPgiUserType(jwt.getClaim(AppConstants.PGI_IDTOKEN_USER_TYPE).asString());
        securityToken.setProfileImageUrl(jwt.getClaim(AppConstants.PROFILE_URL).asString());
        return securityToken;
    }
}
