package com.sanoop.myslack.rest;

import com.sanoop.myslack.Models.ChannelInfoResponse;
import com.sanoop.myslack.Models.SlackDetails;
import com.sanoop.myslack.Models.RtmConnectResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("rtm.connect")
    Call<RtmConnectResponse> getWebSocketURL(@Header("Authorization") String bearerToken);

    @GET("rtm.start")
    Call<SlackDetails> getSlackResources(@Header("Authorization") String bearerToken);

    @GET("channels.info")
    Call<ChannelInfoResponse> getChannelInfo(@Header("Authorization") String bearerToken,
                                             @Query("channel") String channelID);

}
