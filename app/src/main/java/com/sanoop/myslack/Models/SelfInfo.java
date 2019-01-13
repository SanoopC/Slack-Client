package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class SelfInfo {
    @SerializedName("id")
    private String selfId;
    @SerializedName("name")
    private String selfName;

    public String getSelfId() {
        return selfId;
    }

    public String getSelfName() {
        return selfName;
    }
}
