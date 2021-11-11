package com.pgi.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RoomTypeServiceAPI {
    /**
     * Gets the room type.
     *
     * @param url the url of the meeting room
     * @return the room type
     */
    @GET
    Call<String> getRoomType(@Url String url);

}
