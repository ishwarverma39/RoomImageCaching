package com.livtech.mobileirontest;

import android.app.Instrumentation;
import android.content.Context;

import com.livtech.mobileirontest.repositories.TweetRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class TweetSearchTest {
    private Context context;
    @Before
    public void setUp(){
        context= Instrumentation.
    }

    @Test
    public void searchTweets() {
        TweetRepository tweetRepository = new TweetRepository();
        tweetRepository.getTweets(getSearchParam(), "Google");
    }

    private HashMap<String, String> getSearchParam() {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", "Google");
        return params;
    }
}
