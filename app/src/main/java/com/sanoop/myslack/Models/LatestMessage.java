package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class LatestMessage {
    @SerializedName("user")
    private String userID;
    @SerializedName("text")
    private String textMessage;

    public String getUserID() {
        return userID;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
