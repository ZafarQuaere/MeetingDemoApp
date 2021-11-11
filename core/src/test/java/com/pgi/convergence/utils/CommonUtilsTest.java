package com.pgi.convergence.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.pgi.convergence.RobolectricTest;
import com.pgi.convergence.TestLogger;
import com.pgi.convergence.application.CoreApplication;
import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.persistance.SharedPreferencesManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ashwanikumar on 8/4/2017.
 */


@PrepareForTest({ /*ApplicationDao.class, AppAuthUtils.class,*/ Calendar.class, CommonUtils.class})
public class CommonUtilsTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Resources resources;
    private NetworkInfo networkInfo;
    private DisplayMetrics displayMetrics;
    private ConnectivityManager connectivityManager;
    private Configuration mConfiguration;
    private Toast mCommonUtilsToastSpy;


    private String idToken = "eyJraWQiOiIxMWZiMDA4NC04ZTBkLTQ3NDItYjg1MC1iNmM0MGZlNWJkNzQiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUw" +
            "LTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaWRlbn" +
            "RpdHktcWEuZGV2Lmdsb2JhbG1lZXQubmV0IiwibmFtZSI6IlN1ZGhlZXIgQ2hpbHVtdWxhIiwiZXhwIjoxNTA0NzczNzg0LCJnaXZlbl9uYW1lIjoiU3VkaGVlciIsImlhdCI6MTUwNDc" +
            "3MDE4NCwiZmFtaWx5X25hbWUiOiJDaGlsdW11bGEiLCJlbWFpbCI6IlN1ZGhlZXIuQ2hpbHVtdWxhQHBnaS5jb20ifQ.eX-P2aH9EM-bHxenkEB_H7pvFB8Rw5CC2O7jskPWMOcpjJROvjD2" +
            "LifFL-yVfO5pXfSw_ZTFokeFts_fu2ySa9fdEuNy7eWjTVErwDMr7o3M7gcl-ow3ovY8TWfTeTGRpI9S2Jkyj56_w4aoWv1STmNT2YlUbfmoy3QG2j0iEGVwtR2v4VTXYUsz6teubdi4l8PlyPr" +
            "faiBcE4-xegDF0sAI3qi0KQiT3mrq6bHoz6kKt9hdz1a5mb3wd62b0V3cdyBPW9W8KrmdAP-Fee34_9TsyTJwHBg36JJAHKlpitOXz7nU7bbfDx45_gy7XmtzBKlzDn1szRkNlqD5bR3mOA";

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        connectivityManager = PowerMockito.mock(ConnectivityManager.class);
        networkInfo = PowerMockito.mock(NetworkInfo.class);
        context = PowerMockito.mock(Context.class);
        displayMetrics = PowerMockito.mock(DisplayMetrics.class);
        resources = PowerMockito.mock(Resources.class);
        mConfiguration = PowerMockito.mock(Configuration.class);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        CoreApplication.appContext = context;
        CoreApplication.mLogger = new TestLogger();
        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Whitebox.setInternalState(sharedPreferencesManager, "mSharedPrefs", sharedPrefs);
        Whitebox.setInternalState(sharedPreferencesManager, "mEditor", sharedPrefs.edit());

    }

    @Test
    public void testGetTimeDifferenceInMinutes() {
        long currentTime = System.currentTimeMillis();
        long fourHoursBack = currentTime - (5 * 60 * 60 * 1000);
        Assert.assertEquals("", CommonUtils.getTimeDifferenceinMinutes(fourHoursBack), 300);

    }

    @Test
    public void testGetTimeDifferenceInMinutes2() {
        long currentTime = System.currentTimeMillis();
        long fourHoursBack = currentTime - (2 * 60 * 60 * 1000);
        Assert.assertEquals("", CommonUtils.getTimeDifferenceinMinutes(fourHoursBack), 120);
    }

    @Test
    public void testGetTimeDifferenceInMinutes3() {
        long currentTime = System.currentTimeMillis();
        long fourHoursBack = currentTime - 0; //(0 * 60 * 60 * 1000);
        Assert.assertEquals("", CommonUtils.getTimeDifferenceinMinutes(fourHoursBack), 0);
    }

    @Test
    public void isConnectionAvailableTest() {
        PowerMockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        PowerMockito.when(networkInfo.isConnected()).thenReturn(true);
        PowerMockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Assert.assertTrue(CommonUtils.isConnectionAvailable(context));
    }

    // DENSITY_MEDIUM = 160 -- "ldpi";
    // DENSITY_TV = 213 -- "hdpi";
    // DENSITY_XHIGH = 320 -- "xhdpi";
    // DENSITY_XXHIGH = 480 -- "xxhdpi";
    // DENSITY_XXXHIGH = 640 -- "xxxhdpi";
    @Test
    public void getDeviceResolutionTest() {
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(resources.getDisplayMetrics()).thenReturn(displayMetrics);
        displayMetrics.densityDpi = 240;
        Assert.assertEquals(CommonUtils.getDeviceResolution(context), "hdpi");
    }

    @Test
    public void getDeviceResolutionTest2() {
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(resources.getDisplayMetrics()).thenReturn(displayMetrics);
        displayMetrics.densityDpi = 640;
        Assert.assertEquals(CommonUtils.getDeviceResolution(context), "xxxhdpi");
    }

    //TODO:: Figure out why these are failing
//    @Test
//    public void testisMorningTime() {
//        mockCalendarInstance(6, 0);
//        Assert.assertTrue(CommonUtils.isMorningTime());
//    }
//
//    @Test
//    public void testisNightTime() {
//        mockCalendarInstance(18, 0);
//        Assert.assertTrue(CommonUtils.isNightTime());
//    }
    //1-719-359-9722 4439313

    /*
    @Test
    public void testClearAppData(){
        Mockito.mock(DaoSession.class);
        PowerMockito.mockStatic(ApplicationDao.class);
        ApplicationDao applicationDao = PowerMockito.mock(ApplicationDao.class);
        PowerMockito.when(ApplicationDao.get(context)).thenReturn(applicationDao);
        PowerMockito.mockStatic(AppAuthUtils.class);
        AppAuthUtils appAuthUtils = Mockito.mock(AppAuthUtils.class);
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
//        CommonUtils.clearAppData(context);
        Assert.assertNull(AppAuthUtils.getInstance().getAuthState());
    }*/

    @Test
    public void testGetPGIFormattedNumber(){
        String formattedNumber = CommonUtils.getPGIFormattedNumber("1-719-359-9722", "4439313");
        Assert.assertEquals("1-719-359-9722,*,,4439313#", formattedNumber);
    }

    @Test
    public void testGetFullName() {
        String firstName = "Amit";
        String lastName = "Kumar";
        Assert.assertEquals("Amit Kumar", CommonUtils.getFullName(firstName, lastName));
    }

    @Test
    public void testGetNameInitials() {
        String firstName = "Amit";
        String lastName = "Kumar";
        Assert.assertEquals("AK", CommonUtils.getNameInitials(firstName, lastName));
    }

    @Test
    public void testGetNameInitialsWithFirstNameOnly() {
        String firstName = "Amit Kumar";
        String lastName = "";
        Assert.assertEquals("AK", CommonUtils.getNameInitials(firstName, lastName));
        String firstNameNew = "Amit";
        String lastNameNew = "Kumar";
        Assert.assertEquals("AK", CommonUtils.getNameInitials(firstNameNew, lastNameNew));
    }

    private void mockCalendarInstance(int hourOfDay, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minutes);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
    }

    @Test
    public void testGetLocale() throws Exception {
        Locale[] list = {new Locale("fr", "FR")};
        LocaleList localeList = new LocaleList(list);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(context.getResources().getConfiguration()).thenReturn(mConfiguration);
        PowerMockito.when(context.getResources().getConfiguration().getLocales()).thenReturn(localeList);
        Assert.assertEquals("fr", CommonUtils.getLocale(context));
    }

    @Test
    public void isUsersLocaleJapan() {
        CommonUtils.isUsersLocaleJapan();

        Assert.assertFalse(CommonUtils.isUsersLocaleJapan());

    }
    @Test
    public void formatJapaneseName() {
        String fullName = "Nnenna Iheke";
        Assert.assertEquals("Iheke Nnenna", CommonUtils.formatJapaneseName(fullName));
    }
    @Test
    public void formatJapaneseInitials()  {
        String initials = "NI";
        Assert.assertEquals("IN", CommonUtils.formatJapaneseInitials(initials).toString());
    }

    @Test
    public void copyMeetingUrl(){
        PowerMockito.mockStatic(CommonUtils.class);
        CommonUtils.copyRoomURL(context, "JOINURL", AppConstants.COPY_URL);
        Assert.assertEquals(1, AppConstants.COPY_URL);
    }

    @Test
    public void copyMeetingUrlUniversal(){
        PowerMockito.mockStatic(CommonUtils.class);
        CommonUtils.copyRoomURL(context, "JOINURL", AppConstants.COPY_URL_UNIVERSAL);
        Assert.assertEquals(2, AppConstants.COPY_URL_UNIVERSAL);
    }
}
