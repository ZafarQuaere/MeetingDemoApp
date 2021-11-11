package com.pgi.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RoomTypeServiceManager {
    private static RoomTypeServiceManager instanceManager = null;
    private RoomTypeServiceAPI roomTypeServiceAPI = null;

    public RoomTypeServiceManager(String furl) {
        String url = furl;
        // retrofit requires urls ending in a forward slash
        // check to see if the furl has such a final character
        if (url.charAt(url.length()-1) != '/' ) {
            url += "/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okClient())
                .build();
        roomTypeServiceAPI = retrofit.create(RoomTypeServiceAPI.class);
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

    /**
     * Gets room type service api.
     *
     * @return RoomTypeServiceAPI
     */
    public RoomTypeServiceAPI getRoomTypeServiceAPI() {
        return roomTypeServiceAPI;
    }

}
