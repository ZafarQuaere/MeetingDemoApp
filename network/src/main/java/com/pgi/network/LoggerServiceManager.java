package com.pgi.network;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.pgi.network.interceptors.PGiHeaderInterceptor;
import com.pgi.network.interceptors.PGiTokenAuthenticator;

import net.openid.appauth.AuthState;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * This is exposing send logs API to send logs to network
 */

public class LoggerServiceManager {
    private static LoggerServiceManager instanceManager = null;
    private LoggerServiceAPI loggerServiceAPI = null;
    private static Context mContext;
    private static AuthState mAuthState;

    private LoggerServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.LOOGER_SERVICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okClient())
                .build();
        loggerServiceAPI = retrofit.create(LoggerServiceAPI.class);
    }

    /**
     * Gets instance.
     *
     * @return the instance of LoggerServiceManager
     */
    public static LoggerServiceManager getInstance() {
        if (instanceManager == null) {
            instanceManager = new LoggerServiceManager();
        }
        return instanceManager;
    }

    private static OkHttpClient okClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .addInterceptor(new PGiHeaderInterceptor())
                .addNetworkInterceptor(new StethoInterceptor())
                .authenticator(new PGiTokenAuthenticator())
                .build();
    }

    /**
     * Gets logger service api.
     *
     * @return the LoggerServiceAPI instance
     */
    public LoggerServiceAPI getLoggerServiceAPI() {
        return loggerServiceAPI;
    }
}
