package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class ChannelInfo {
    @SerializedName("id")
    private String channelId;
    @SerializedName("name")
    private String channelName;
    @SerializedName("is_member")
    private Boolean isMember;
    @SerializedName("latest")
    private LatestMessage latestMessage;

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public Boolean getMember() {
        return isMember;
    }

    public LatestMessage getLatestMessage() {
        return latestMessage;
    }
}
