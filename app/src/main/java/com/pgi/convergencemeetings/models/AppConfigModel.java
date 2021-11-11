package com.pgi.convergencemeetings.models;

/**
 * Created by amit1829 on 7/26/2017.
 */

public class AppConfigModel {

    private String pgiWebBaseUrl;
    private String uapiBaseUrl;
    private String piaBaseUrl;

    public String getPgiWebBaseUrl() {
        return pgiWebBaseUrl;
    }

    public void setPgiWebBaseUrl(String pgiWebBaseUrl) {
        this.pgiWebBaseUrl = pgiWebBaseUrl;
    }

    public String getUapiBaseUrl() {
        return uapiBaseUrl;
    }

    public void setUapiBaseUrl(String uapiBaseUrl) {
        this.uapiBaseUrl = uapiBaseUrl;
    }

    public String getPiaBaseUrl() {
        return piaBaseUrl;
    }

    public void setPiaBaseUrl(String piaBaseUrl) {
        this.piaBaseUrl = piaBaseUrl;
    }
}
