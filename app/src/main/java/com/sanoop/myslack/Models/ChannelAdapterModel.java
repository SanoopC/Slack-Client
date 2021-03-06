package com.sanoop.myslack.Models;

public class ChannelAdapterModel {
    private String message, senderName, channelName, channelID, messageTime;

    public ChannelAdapterModel() {
    }

    public ChannelAdapterModel(String message, String sederName, String channelName,
                               String channelID, String messageTime) {
        this.message = message;
        this.senderName = sederName;
        this.channelName = channelName;
        this.channelID = channelID;
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getChannelName() {
        return "# " + channelName;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
