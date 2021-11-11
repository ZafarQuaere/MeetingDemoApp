package com.pgi.convergencemeetings.networkmocks;

import android.content.Context;
import androidx.annotation.NonNull;

import com.pgi.convergencemeetings.utils.AppAuthUtils;
import com.pgi.network.BuildConfig;
import com.pgi.network.repository.GMElasticSearchServiceAPI;
import com.pgi.network.IdentityServiceAPI;
import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PiaServiceAPI;
import com.pgi.network.RoomTypeServiceAPI;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by ashwanikumar on 8/3/2017.
 */
public final class FakeRestClient {

    private static PgiWebServiceAPI mRestPgiService = null;

    private static PiaServiceAPI mPiaService = null;

    private static IdentityServiceAPI mIdentityServiceAPI = null;

    private static FakeRestClient fakeRestClient;

    private static GMElasticSearchServiceAPI sGMElasticSearchServiceAPI = null;


    private FakeRestClient() {

    }

    public static FakeRestClient getInstance() {
        if (fakeRestClient == null) {
            fakeRestClient = new FakeRestClient();
        }
        return fakeRestClient;
    }

    public RoomTypeServiceAPI getRoomTypeClient(String furl) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.dispatcher(dispatcher);
        client.interceptors().add(new FakeInterceptor());
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(furl)
                .client(client.build())
                .build();
        return retrofit.create(RoomTypeServiceAPI.class);
    }

    public PgiWebServiceAPI getPGiClient() {
        if (mRestPgiService == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);
            //final OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new FakeInterceptor());
            client.interceptors().add(new FreshTokenInterceptorTest());

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(BuildConfig.PGI_BASE_URL)
                    .client(client.build())
                    .build();

            mRestPgiService = retrofit.create(PgiWebServiceAPI.class);
        }
        return mRestPgiService;
    }

    public PiaServiceAPI getPiaClient() {
        if (mPiaService == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);
            //final OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new FakeInterceptor());

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(BuildConfig.PIA_BASE_URL)
                    .client(client.build())
                    .build();

            mPiaService = retrofit.create(PiaServiceAPI.class);
        }
        return mPiaService;
    }

    public IdentityServiceAPI getIdentityClient() {
        if (mIdentityServiceAPI == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);
            //final OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new FakeInterceptor());
            client.interceptors().add(new FreshTokenInterceptorTest());

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(BuildConfig.IDENTITY_BASE_URL)
                    .client(client.build())
                    .build();

            mIdentityServiceAPI = retrofit.create(IdentityServiceAPI.class);
        }
        return mIdentityServiceAPI;
    }

    public GMElasticSearchServiceAPI getGMSearchClient() {
        if (sGMElasticSearchServiceAPI == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);
            //final OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new FakeInterceptor());
            client.interceptors().add(new FreshTokenInterceptorTest());


            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(BuildConfig.GMSEARCH_URL)
                    .client(client.build())
                    .build();

            sGMElasticSearchServiceAPI = retrofit.create(GMElasticSearchServiceAPI.class);
        }
        return sGMElasticSearchServiceAPI;
    }

    Dispatcher dispatcher = new Dispatcher(new AbstractExecutorService() {
        private boolean shutingDown = false;
        private boolean terminated = false;

        @Override
        public void shutdown() {
            this.shutingDown = true;
            this.terminated = true;
        }

        @NonNull
        @Override
        public List<Runnable> shutdownNow() {
            return new ArrayList<>();
        }

        @Override
        public boolean isShutdown() {
            return this.shutingDown;
        }

        @Override
        public boolean isTerminated() {
            return this.terminated;
        }

        @Override
        public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    });

    public AppAuthUtils mockAppAuthToken() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PowerMockito.mockStatic(AppAuthUtils.class);
        AppAuthUtils appAuthUtils = PowerMockito.mock(AppAuthUtils.class);
        String accessToken = "eyJraWQiOiIyMTQ5MGExOS1kMGIwLTQ5MTUtOGJmNi1mNGM5ZmMwMGQyMzIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJlMzcxMTJlZS00MjUwLTQxZTktYTk2Zi1hYzE2ZTE3MzhhYmMiLCJzdWIiOiJmYjQxYjE2Mi0zZmY0LTRlMDctOTQ2ZC0wM2RkZGZjNDExZTMiLCJzY3AiOlsiZ2xvYmFsbWVldCIsIndlYnNlcnZpY2VzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJwZ2lfc2Vzc2lvbiI6IjFmZWRjOGM3LTlhMDgtNDM1MC05NDQ2LThkOGZkMDc2NDA0NCIsInBnaV9pZF9nbSI6IjUwNzg3NTgiLCJpc3MiOiJpZGVudGl0eS1xYS5kZXYuZ2xvYmFsbWVldC5uZXQiLCJwZ2lfdXNlciI6dHJ1ZSwiZXhwIjoxNTA0NjgzODQxLCJpYXQiOjE1MDQ2ODAyNDF9.cDZSL-MPXV-cHAIm2vr7AOO5wgQOZOsA6KIZFwFMGPXfAuf214m3gM4sbks7iBpjDRmJSctenXD5G84c55SQWtRGFmBRHZdxOwKSuXLj-pHo-THQTRIHUzutoWh0LRKwJK0XJT729y_oMNtxjhvxWY6T78VtbQZBtdw2Y3HIJKV5MhOp38Cc0-sTjSHpdDlLeAIMIfEt07NpPVSSdYplYtCF8prcSWYmf-c_bHdP5pnRTuadhn041ly1tZ3iKH_WmnRpOG09VxD7c8-L1k2vlRqJOMxH_8262QAldodqlfTh_XKEDlG6AvIDEGbVdrb1zZ4c0pkLUR6iSuN1GqOjzQ";
        String firstName = "Amit";
        String lastName = "Kumar";
        String userType = "Host";
        String email = "amit.kumar@pgi.com";
        Mockito.when(AppAuthUtils.getInstance()).thenReturn(appAuthUtils);
        Mockito.when(appAuthUtils.getAccessToken()).thenReturn(accessToken);
        Mockito.when(appAuthUtils.getFirstName()).thenReturn(firstName);
        Mockito.when(appAuthUtils.getLastName()).thenReturn(lastName);
        Mockito.when(appAuthUtils.getPgiUserType()).thenReturn(userType);
        Mockito.when(appAuthUtils.getEmailId()).thenReturn(email);

        return appAuthUtils;
    }
}
