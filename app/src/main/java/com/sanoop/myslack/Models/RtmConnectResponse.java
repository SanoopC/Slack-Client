package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class RtmConnectResponse {
    @SerializedName("ok")
    private String status;
    @SerializedName("url")
    private String websocketUrl;

    public RtmConnectResponse() {
    }

    public String getStatus() {
        return status;
    }

    public String getWebsocketUrl() {
        return websocketUrl;
    }
}
