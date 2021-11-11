package com.pgi.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * This class is used by {@link LoggerServiceManager} to expose send logs API
 */

public interface LoggerServiceAPI {
        @POST("/logs")
        Call<String> sendLogs(@Header("Content-Type") String contentType, @Header("X-PGI-LOGSESSIONCONTROL") String logSessionType, @Header("Cookie") String cookie, @Body String logs);
}
