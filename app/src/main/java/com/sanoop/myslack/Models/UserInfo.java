package com.sanoop.myslack.Models;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("id")
    private String userId;
    @SerializedName("profile")
    private UserProfile userProfile;
    @SerializedName("deleted")
    private Boolean isUserDeleted;

    public String getUserId() {
        return userId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Boolean getUserDeleted() {
        return isUserDeleted;
    }
}
