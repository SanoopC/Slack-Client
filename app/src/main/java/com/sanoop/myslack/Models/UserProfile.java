package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("real_name")
    private String realName;
    @SerializedName("display_name")
    private String displayName;

    public String getRealName() {
        return realName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
