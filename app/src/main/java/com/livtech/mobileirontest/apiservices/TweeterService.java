package com.livtech.mobileirontest.apiservices;

import com.livtech.mobileirontest.models.Tweet;
import com.livtech.mobileirontest.models.TweetSearchResponse;
import com.livtech.mobileirontest.models.TwitterTokenType;
import com.livtech.mobileirontest.network.ApiResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface TweeterService {
    @GET("/1.1/search/tweets.json")
    Call<TweetSearchResponse> getTweets(
            @QueryMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<TwitterTokenType> getToken(
            @Header("Authorization") String authorization,
            @Field("grant_type") String grantType
    );
}
