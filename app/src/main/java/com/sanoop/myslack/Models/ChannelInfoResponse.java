package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class ChannelInfoResponse {
    @SerializedName("ok")
    private String status;
    @SerializedName("channel")
    private ChannelInfo channelInfo;

    public ChannelInfoResponse() {
    }

    public String getStatus() {
        return status;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }
}
