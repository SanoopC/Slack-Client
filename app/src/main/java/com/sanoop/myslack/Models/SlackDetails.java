package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SlackDetails {
    @SerializedName("ok")
    private String status;
    @SerializedName("self")
    private SelfInfo selfInfo;
    @SerializedName("channels")
    private List<ChannelInfo> channelInfoList;
    @SerializedName("users")
    private List<UserInfo> userInfoList;
    @SerializedName("url")
    private String websocketUrl;

    public SlackDetails() {
    }

    public String getStatus() {
        return status;
    }

    public SelfInfo getSelfInfo() {
        return selfInfo;
    }

    public List<ChannelInfo> getChannelInfoList() {
        return channelInfoList;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public String getWebsocketUrl() {
        return websocketUrl;
    }


}
