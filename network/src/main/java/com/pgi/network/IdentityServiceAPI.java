package com.pgi.network;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This class is exposing APIs for User's login specific actions.
 */

public interface IdentityServiceAPI {
    @POST("/users/{email}/forgotPasswordEmail")
    Call<String> forgotPasswordRequest(@Path("email") String email, @Query("auth_by_client") String isAuthByClient);

    @POST("/users/logout")
    Observable<String> logoutUser(@Query("auth_by_client") boolean isAuthByClient);

    @POST("/oauth2/revoke")
    Observable<String> revokeUserToken(@Query("token") String token);

    @GET("/oauth2/userinfo")
    Call<String> getProfileImage();
}
