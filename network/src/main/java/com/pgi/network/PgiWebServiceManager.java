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
 * The class prepare the instace of Retrofit and customizing the OkHttpClient.
 * With the help of {@link PgiWebServiceManager} instace invoke web services.
 */
public class PgiWebServiceManager {

    private static PgiWebServiceManager instanceManager = null;
    private PgiWebServiceAPI pgiWebServiceAPI = null;
    private static Context mContext;
    private static AuthState mAuthState;

    private PgiWebServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.PGI_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okClient())
                .build();
        pgiWebServiceAPI = retrofit.create(PgiWebServiceAPI.class);
    }

    /**
     * @return the instance of PgiWebServiceManager
     */
    public static PgiWebServiceManager getInstance() {
        if (instanceManager == null) {
            instanceManager = new PgiWebServiceManager();
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
                   .addInterceptor(new PGiHeaderInterceptor())
                   .addNetworkInterceptor(new StethoInterceptor())
                   .authenticator(new PGiTokenAuthenticator())
                .build();
    }

    /**
     * Gets pgi web service api.
     *
     * @return PgiWebServiceAPI
     */
    public PgiWebServiceAPI getPgiWebServiceAPI() {
        return pgiWebServiceAPI;
    }

}