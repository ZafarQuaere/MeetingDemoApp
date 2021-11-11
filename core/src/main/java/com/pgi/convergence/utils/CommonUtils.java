package com.pgi.convergence.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.convergence.core.R;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.constants.SoftphoneConstants;
import com.pgi.convergence.enums.PGiLanguages;
import com.pgi.convergence.persistance.SharedPreferencesManager;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Common class for {@link CommonUtils} methods.
 * Created by amit1829 on 8/14/2017.
 */

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getName();

    private static SharedPreferencesManager mSharedPreferencesManager;
    private static long mMeetingStartTime;
    private static String mLang_Locale = null;
    private static Context ctx;
    private static Resources res;
    private static boolean isMeetingLocked;
    private static boolean isMeetingActive;
    private static boolean isUsersOwnMeeting;
    private static boolean showEndMeetingThankYouSnackbar;

    public static boolean isConnectionAvailable(Context context) {
        boolean result = false;
        try {
            if (context != null) {
                ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    result = netInfo != null && netInfo.isConnected();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
//            LoggerUtil logger = LoggerUtil.getInstance();
//            if (logger != null) {
//                logger.error(TAG, "isConnectionAvailable", ex);
//            }
        }
        return result;
    }

    public static String getDeviceResolution(Context context) {

        String deviceResolution = null;
        if (context != null) {
            int deviceDensity = context.getResources().getDisplayMetrics().densityDpi;
            if (deviceDensity <= DisplayMetrics.DENSITY_LOW) {
                deviceResolution = "ldpi";
            } else if (deviceDensity <= DisplayMetrics.DENSITY_MEDIUM) {
                deviceResolution = "mdpi";
            } else if (deviceDensity <= DisplayMetrics.DENSITY_HIGH) {
                deviceResolution = "hdpi";
            } else if (deviceDensity <= DisplayMetrics.DENSITY_XHIGH) {
                deviceResolution = "xhdpi";
            } else if (deviceDensity <= DisplayMetrics.DENSITY_XXHIGH) {
                deviceResolution = "xxhdpi";
            } else if (deviceDensity <= DisplayMetrics.DENSITY_XXXHIGH) {
                deviceResolution = "xxxhdpi";
            } else {
                deviceResolution = "xhdpi";
            }
        }
        return deviceResolution;
    }

    /**
     * Method calculate the time difference between current times and last saved time
     * convert into hours and returns the time diff into Hours.
     *
     * @param lastSavedTime
     * @return
     */
    public static int getTimeDifferenceinMinutes(long lastSavedTime) {
        Date currentTime = new Date();
        long difference = currentTime.getTime() - lastSavedTime;
        return (int) difference / (1000 * 60);
    }

    /**
     * Returns true if the current time is between 0AM to 5PM.
     *
     * @return boolean
     */

    public static boolean isMorningTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) >= AppConstants.MID_NIGHT_THRESHOLD
            && cal.get(Calendar.HOUR_OF_DAY) < AppConstants.AFTERNOON_THRESHOLD;
    }

    /**
     * Returns true if the current time is between 5pm to 23Am.
     *
     * @return boolean
     */
    public static boolean isNightTime() {
        Calendar cal = Calendar.getInstance();
        return (cal.get(Calendar.HOUR_OF_DAY) >= AppConstants.AFTERNOON_THRESHOLD
            && cal.get(Calendar.HOUR_OF_DAY) < AppConstants.EVENING_THRESHOLD);
    }

    public static void checkSharedPrefManagerInstance(Context context) {
        if (mSharedPreferencesManager == null) {
            mSharedPreferencesManager = SharedPreferencesManager.getInstance();
        }
    }

    public static String getPGIFormattedNumber(String phoneNumber, String passcode) {
        StringBuilder formattedPhone = new StringBuilder(phoneNumber.replaceAll("\\s", ""));
        if (passcode != null && !passcode.isEmpty()) {
            formattedPhone.append(AppConstants.COMMA_SYMBOL).append(AppConstants.ASTERISK_SYMBOL)
                .append(AppConstants.DOUBLE_COMMA_SYMBOL);
            formattedPhone.append(passcode);
            formattedPhone.append(AppConstants.POUND_SYMBOL);
        }
        return formattedPhone.toString();
    }

    public static String getFullName(String firstName, String lastName) {
        String fullName = null;
        //Append first name and last name to create full name...
        if (firstName != null && firstName.trim().length() > 0) {
            fullName = firstName.trim();
            if (lastName != null && lastName.trim().length() > 0) {
                fullName = fullName + AppConstants.BLANK_SPACE + lastName;
            }
        }
        return fullName;
    }

    public static String getNameInitials(String firstName, String lastName) {
        String nameInitials = "";
        //Append first name and last name initials to create name initials...
        if (firstName != null && firstName.trim().length() > 0) {
            if (TextUtils.isDigitsOnly(firstName)) {
                nameInitials = AppConstants.POUND_SYMBOL;
            } else {
                if (firstName.trim().contains(AppConstants.BLANK_SPACE)) {
                    String[] arrOfStr = firstName.split(AppConstants.BLANK_SPACE);
                    nameInitials = arrOfStr[0].trim().charAt(0) + AppConstants.EMPTY_STRING;
                    if (arrOfStr.length > 1) {
                        nameInitials = nameInitials + arrOfStr[1].trim().charAt(0);
                    }
                } else {
                    nameInitials = firstName.trim().charAt(0) + AppConstants.EMPTY_STRING;
                    if (lastName != null && lastName.trim().length() > 0) {
                        nameInitials = nameInitials + lastName.trim().charAt(0);
                    }
                }
            }
            nameInitials = nameInitials.toUpperCase();
        }
        return nameInitials;
    }

    private static String getSupportedLanguage(Context context) {
        String lang = null;
        String[] supportLocales = PGiLanguages.toStringArray();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList ll = context.getResources().getConfiguration().getLocales();
                Locale locale = ll.getFirstMatch(supportLocales);
                if (locale != null) {
                    lang = locale.getLanguage();
                }
            } else {
                String curLang = context.getResources().getConfiguration().locale.getLanguage();
                for (String language : supportLocales) {
                    if (curLang.equals(language)) {
                        lang = curLang;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getStackTrace()[0].getMethodName() + " " + ex.getMessage();
            Log.e(TAG, msg);
//            LoggerUtil logger = LoggerUtil.getInstance();
//

        }
        if (lang == null) {
            lang = "en";
        }
        return lang;
    }

    public static String getLocale(Context context) {
        if (context != null) {
            mLang_Locale = getSupportedLanguage(context);
        } else {
            mLang_Locale = "en";
        }
        if(mLang_Locale.equalsIgnoreCase("ja")){
            mLang_Locale = "ja";
        }
        Log.v("LOCALE", mLang_Locale);
        return mLang_Locale;
    }

    public static String createSipUri(String sipUri, String passCode, Context context) {
        // NOTE: do not use sip_port with softphone v2.  It will default to correct value.
        // plus v1 would do retries on multiple ports.  V2 doesn't do that.  It will just fail
        // if you use the wrong one.
        //sip:7777100002@dn1.mobile.sip.pgilab.com:5070;pgilink=GM;lang=en_US;Overridelock=Y;passcode=1266423
        if (sipUri != null && passCode != null) {
            StringBuilder sipUrlBuilder = new StringBuilder();
            sipUrlBuilder.append(SoftphoneConstants.SIP_COLON).append(sipUri);
            if (sipUrlBuilder.indexOf(SoftphoneConstants.DOT) != -1 && sipUrlBuilder.lastIndexOf(SoftphoneConstants.DOT) != -1) {
                sipUrlBuilder.insert(sipUrlBuilder.indexOf(SoftphoneConstants.DOT) + 1, SoftphoneConstants.MOBILE_DOMAIN);
                sipUrlBuilder.insert(sipUrlBuilder.indexOf(SoftphoneConstants.MOBILE_DOMAIN) + 6, SoftphoneConstants.DOT);
                sipUrlBuilder.append(SoftphoneConstants.SEMICOLON);
                sipUrlBuilder.append(SoftphoneConstants.PGI_LINK).append(SoftphoneConstants.EUQALSTO).append(SoftphoneConstants.GM).append(SoftphoneConstants.SEMICOLON);
                sipUrlBuilder.append(SoftphoneConstants.LANG).append(getLocale(context)).append(SoftphoneConstants.SEMICOLON);
                sipUrlBuilder.append(SoftphoneConstants.KEY_PASSCODE).append(SoftphoneConstants.EUQALSTO).append(passCode).append(SoftphoneConstants.SEMICOLON);
                if(isMeetingLocked() && isMeetingActive() && !isOwnMeeting()) {
                    sipUrlBuilder.append(SoftphoneConstants.OVERRIDE_LOCK).append(SoftphoneConstants.EUQALSTO).append(SoftphoneConstants.YES);
                }
                else {
                    sipUrlBuilder.append(SoftphoneConstants.OVERRIDE_LOCK).append(SoftphoneConstants.EUQALSTO).append(SoftphoneConstants.NO);
                }
                return sipUrlBuilder.toString();
            }
        }
        return null;
    }

    public static boolean setIsOwnMeeting(boolean isOwnMeeting) {
        isUsersOwnMeeting = isOwnMeeting;
        return isOwnMeeting;
    }
    public static boolean isOwnMeeting() {
        return isUsersOwnMeeting;
    }

    public static void setIsMeetingLocked(boolean meetingLocked) {
        isMeetingLocked = meetingLocked;

    }
    private static boolean isMeetingLocked(){
        return isMeetingLocked;
    }


    @NonNull
    public static Long convertStringToLong(String idStr) {
        Long id = -1L;
        if (idStr != null) {
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException nfe) {
                Log.e(TAG, nfe.getMessage());
            }
        }
        return id;
    }

    @NonNull
    public static Integer convertStringToInt(String number) {
        Integer id = -1;
        if (number != null) {
            try {
                id = Integer.parseInt(number);
            } catch (NumberFormatException nfe) {
                Log.e(TAG, nfe.getMessage());
            }
        }
        return id;
    }

    public static void setPrefSessionId(Context context, String sessionId) {
        checkSharedPrefManagerInstance(context);
        mSharedPreferencesManager.setSessionId(sessionId);
    }

    public static String getPrefSessionId(Context context) {
        checkSharedPrefManagerInstance(context);
        return mSharedPreferencesManager.getPrefSessionId();
    }


    /**
     * Getting the application version from package manager.
     *
     * @param context the context
     * @return String the app version
     */
    public static String getAppVersion(WeakReference<Context> context) {
        String appVersion = null;
        try {
            PackageManager manager = context.get().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.get().getPackageName(), 0);
            if (info != null) {
                appVersion = info.versionName + AppConstants.DOT_SYMBOL + info.versionCode;
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.d(TAG, nnfe.getMessage());
        }
        return appVersion;
    }

    /**
     * Getting the application version from package manager.
     *
     * @param context the context
     * @return String the app version
     */
    public static int getAppVersionName(WeakReference<Context> context) {
        String appVersion = null;
        Matcher m;
        try {
            PackageManager manager = context.get().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.get().getPackageName(), 0);
            if (info != null) {
                appVersion = info.versionName;
                m = Pattern.compile("^(.*?[.].*?)[.].*")
                    .matcher(appVersion);
                if (m.matches()) {
                    appVersion = m.group(1);
                    appVersion = appVersion.replaceAll("\\.", "");
                    return Integer.parseInt(appVersion);
                }
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.d(TAG, nnfe.getMessage());
        }
        return Integer.parseInt(appVersion);
    }

    /**
     * Returns device model name
     *
     * @return String the device model name
     */
    public static String getDeviceModelName() {
        String manufacturer = Build.UNKNOWN.equals(Build.MANUFACTURER) ? AppConstants.EMPTY_STRING : Build.MANUFACTURER;
        String model = Build.UNKNOWN.equals(Build.MODEL) ? AppConstants.EMPTY_STRING : Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + AppConstants.BLANK_SPACE + model;
        }
    }

    /**
     * Capitalize the first letter of the string
     *
     * @param s String whose first letter to be capitalize
     * @return String the capitalized string
     */
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return AppConstants.EMPTY_STRING;
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String formatDateToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSz");
        return format.format(date);
    }

    // this function always returns true it is needed to work with some existing Mockito tests
    @SuppressWarnings("SameReturnValue")
    public static boolean hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return true;
    }

    public static void hideNotificationBar(Activity mWebActivity) {
        mWebActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mWebActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWebActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        View decorView = mWebActivity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void showNotificationBar(Activity mWebActivity) {
        View decorView = mWebActivity.getWindow().getDecorView();
        if (decorView.getSystemUiVisibility() != AppConstants.SYSTEM_UI_VISIBILITY_DEFAULT_VALUE) {
            mWebActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mWebActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            decorView.setSystemUiVisibility(0);
        }
    }

    public static HashMap<String, Object> getUrlParameters(String url) {
        Uri uri = Uri.parse(url);
        Set<String> args = uri.getQueryParameterNames();
        HashMap<String, Object> urlParameters = new HashMap<>();

        for (String arg : args) {
            Object value = uri.getQueryParameter(arg);
            urlParameters.put(arg, value);
        }

        return urlParameters;
    }

    public static void setDefaultMeetingRoom(String meetingRoomId) {
        mSharedPreferencesManager.setPrefDefaultMeetingRoom(meetingRoomId);
    }

    public static String getDefaultMeetingRoom(Context context) {
        checkSharedPrefManagerInstance(context);
        return mSharedPreferencesManager.getPrefDefaultMeetingRoom();
    }

    public static String replaceMultipleOccurrenceHttp(String url) {
        String newUrl = AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS;
        if (isMultipleHttpOccurrence(url)) {
            Matcher matcher = AppConstants.HTTP_PATTERN.matcher(url);
            newUrl = newUrl + matcher.replaceAll(AppConstants.EMPTY_STRING);
        } else {
            newUrl = url;
        }
        return newUrl;
    }

    public static boolean isMultipleHttpOccurrence(String url) {
        int count = 0;
        if (url != null && url.trim().length() > AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS.length()) {
            Matcher matcher = AppConstants.HTTP_PATTERN.matcher(url);
            while (matcher.find()) {
                count++;
            }
        }
        return count > 1;
    }

    public static boolean isNonEmptyArray(String[] objects) {
        return (objects != null && objects.length > 0);
    }


    public static String formatCamelCase(String name) {
        //We have commented the code for camel case for the time being..
        /*String result = "";
        if (null != name && !TextUtils.isEmpty(name.trim())) {
            for (String iter : name.split(AppConstants.EMPTY_SQUARE_BRACKET)) {
                if (null != iter && !iter.isEmpty()) {
                    if (iter.length() > 1) {
                        result += iter.substring(0, 1).toUpperCase() + iter.substring(1, iter.length()) + AppConstants.BLANK_SPACE;
                    } else {
                        result += iter.substring(0, 1).toUpperCase() + AppConstants.BLANK_SPACE;
                    }
                }
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;*/
        return name;
    }

    public static boolean isNotEmptyOrNull(JSONObject staticMetaData) {
        return staticMetaData != null && staticMetaData.length() > 0;
    }

    public static boolean isUsersLocaleJapan() {
        return Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage());
    }

    public static String formatJapaneseName(String fullName) {
        String fullNameView = null;
        String firstName;
        String lastName;
        if(fullName != null) {
            if (fullName.trim().contains(" ")) {
                firstName = fullName.substring(0, fullName.indexOf(' '));
                lastName = fullName.substring(fullName.indexOf(' ') + 1);
                fullNameView = String.format("%s %s", lastName, firstName);
            } else {
                fullNameView = String.format("%s %s", fullName, "");
            }

        }
        return fullNameView;
    }

    public static String formatJapaneseFirstName(String firstName) {
        if(firstName != null) {
            if (firstName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(' '));

            } else {
                firstName = String.format("%s", firstName);
            }

        }
        return firstName;
    }

    public static String formatJapaneseLastName(String lastName) {
        if(lastName != null) {
            if (lastName.contains(" ")) {
                lastName = lastName.substring(lastName.indexOf(' ') + 1);
            } else {
                lastName = String.format("%s", lastName);
            }

        }
        return lastName;
    }
    public static StringBuilder formatJapaneseInitials(String initials)  {
        return new StringBuilder(initials.toUpperCase()).reverse();
    }

    public static Context setContext(Context context){
        ctx = context;
        return ctx;
    }

    public static Resources setResources(Resources resources) {
        res = resources;
        return res;
    }

    public static Resources getResources() {
        return res;
    }

    public static Context getCtx() {
        return ctx;
    }

    //commenting this out as we don't need it anymore, but leaving it in
        //case we need it in the future
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                   || Build.FINGERPRINT.startsWith("unknown")
                   || Build.MODEL.contains("google_sdk")
                   || Build.MODEL.contains("Emulator")
                   || Build.MODEL.contains("Android SDK built for x86")
                   || Build.MANUFACTURER.contains("Genymotion")
                   || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                   || "google_sdk".equals(Build.PRODUCT);
    }


    public static boolean setIsMeetingActive(boolean meetingActiveState) {
        isMeetingActive = meetingActiveState;
        return isMeetingActive;
    }

    private static boolean isMeetingActive(){
        return isMeetingActive;
    }

    public static String formatMeetingRoomName(String firstName, String lastName) {

        return firstName + AppConstants.EMPTY_SPACE_SYMBOL + lastName;
    }

    public static void copyRoomURL(Context context, String joinUrl, int message) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData  clip = ClipData.newPlainText("Room URL", joinUrl);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        showToastMessage(message, context);
    }

    public static void showToastMessage(Integer message, @Nullable Context context){
        switch (message){
            case AppConstants.COPY_URL :
                Toast.makeText(context, R.string.copy_message, Toast.LENGTH_SHORT).show();
                break;
            case AppConstants.COPY_URL_UNIVERSAL:
                Toast.makeText(context, R.string.copy_message_universal, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, R.string.copy_message_universal, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isUsersLocaleGerman() {
        return Locale.getDefault().getLanguage().equals(new Locale("de").getLanguage());
    }

    public static boolean showEndMeetingThankYouSnackBar(boolean isShowEndMeetingThankYouSnackBar) {
        showEndMeetingThankYouSnackbar = isShowEndMeetingThankYouSnackBar;
        return isShowEndMeetingThankYouSnackBar;
    }

    public static boolean isEndMeetingThankYouSnackBarVisible() {
        return showEndMeetingThankYouSnackbar;
    }
}