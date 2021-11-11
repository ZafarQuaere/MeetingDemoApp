package com.pgi.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class provides network callback on retrofit requet.
 */

public interface NetworkResponseHandler extends Callback<String> {
    @Override
    void onResponse(Call<String> call, Response<String> response);

    @Override
    void onFailure(Call<String> call, Throwable t);
}
