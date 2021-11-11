package com.pgi.convergencemeetings;

import androidx.annotation.NonNull;

import com.pgi.network.PgiWebServiceAPI;
import com.pgi.network.PiaServiceAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by nnennaiheke on 8/11/17.
 */

public final class FakeRestClient {

    private static PgiWebServiceAPI mRestService = null;

    private static PiaServiceAPI mPiaServiceAPI = null;

    private static FakeRestClient fakeRestClient;

    private FakeRestClient(){

    }

    public static FakeRestClient getInstance(){
        if(fakeRestClient == null){
            fakeRestClient = new FakeRestClient();
        }
        return fakeRestClient;
    }

    public PgiWebServiceAPI getClient() {
        if(mRestService == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl("http://servicespubqab.pgilab.com")
                    .client(client.build())
                    .build();

            mRestService = retrofit.create(PgiWebServiceAPI.class);
        }
        return mRestService;
    }

    public PiaServiceAPI getPiaClient() {
        if(mPiaServiceAPI == null) {
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.dispatcher(dispatcher);

            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl("https://servicespubdev.pgilab.com")
                    .client(client.build())
                    .build();

            mPiaServiceAPI = retrofit.create(PiaServiceAPI.class);
        }
        return mPiaServiceAPI;
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

}
