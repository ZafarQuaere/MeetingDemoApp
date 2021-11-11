package com.pgi.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * The class prepare the instace of Retrofit and customizing the OkHttpClient.
 * With the help of {@link PiaServiceAPI} instace invoke web services.
 */
public class PiaServiceManager {

    private static PiaServiceManager instanceManager = null;
    private PiaServiceAPI piaServiceAPI = null;

    private PiaServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.PIA_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okClient())
                .build();
        piaServiceAPI = retrofit.create(PiaServiceAPI.class);
    }

    /**
     * @return the instance of PiaServiceManager
     */
    public static PiaServiceManager getInstance() {
        if (instanceManager == null) {
            instanceManager = new PiaServiceManager();
        }
        return instanceManager;
    }

    /**
     * Gets pia service api.
     *
     * @return PiaServiceAPI
     */
    public PiaServiceAPI getPiaServiceAPI() {
        return piaServiceAPI;
    }

    private static OkHttpClient okClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build();
    }

}
