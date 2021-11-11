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
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * This class prepare {@link IdentityServiceAPI} which will be consumed by service classes.
 */

public class IdentityServiceManager {
    private static IdentityServiceManager instanceManager = null;
    private IdentityServiceAPI piaServiceAPI = null;
    private static Context mContext;
    private static AuthState mAuthState;

    private IdentityServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.IDENTITY_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okClient())
                .build();
        piaServiceAPI = retrofit.create(IdentityServiceAPI.class);
    }

    /**
     * @return the instance of IdentityServiceManager
     */
    public static IdentityServiceManager getInstance() {
        if (instanceManager == null) {
            instanceManager = new IdentityServiceManager();
        }
        return instanceManager;
    }

    /**
     * Gets identity service api.
     *
     * @return the identity service api instance
     */
    public IdentityServiceAPI getIdentityServiceAPI() {
        return piaServiceAPI;
    }

    private static OkHttpClient okClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                   .addInterceptor(new HttpLoggingInterceptor())
                   .addInterceptor(new PGiHeaderInterceptor())
                   .addNetworkInterceptor(new StethoInterceptor())
                   .authenticator(new PGiTokenAuthenticator())
                .build();
    }
}
