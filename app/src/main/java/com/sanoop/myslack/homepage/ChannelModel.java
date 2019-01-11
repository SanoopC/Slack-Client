package com.sanoop.myslack.homepage;

public class ChannelModel {
    private String message, senderName, channelName;

    public ChannelModel() {
    }

    public ChannelModel(String message, String sederName, String channelName) {
        this.message = message;
        this.senderName = sederName;
        this.channelName = channelName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
