package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class LatestMessage {
    @SerializedName("user")
    private String userID;
    @SerializedName("text")
    private String textMessage;
    @SerializedName("ts")
    private String timeStamp;

    public String getUserID() {
        return userID;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
