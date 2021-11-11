package com.pgi.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Intercepting the received cookie and savinf it in shared preferences.
 */
public class RecievedCookieInterceptor implements Interceptor {
    private Context context;

    /**
     * Instantiates a new Recieved cookie interceptor.
     *
     * @param context the context
     */
    public RecievedCookieInterceptor(Context context) {
        this.context = context;
    }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();

                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                }

                SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
                memes.putStringSet("Horses.ENV", cookies).apply();
                memes.commit();
            }
            return originalResponse;
        }
    }



