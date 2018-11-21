package com.livtech.mobileirontest.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.livtech.mobileirontest.apiservices.TweeterService;
import com.livtech.mobileirontest.database.TweetDao;
import com.livtech.mobileirontest.models.Resource;
import com.livtech.mobileirontest.models.Tweet;
import com.livtech.mobileirontest.models.TweetSearchResponse;
import com.livtech.mobileirontest.network.ApiResponse;
import com.livtech.mobileirontest.network.NetworkBoundResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class TweetRepository extends BaseRepository {
    private TweetDao dao;
    private TweeterService tweeterService;

    public TweetRepository() {
        super();
        dao = db.tweetDao();
        tweeterService = getService(TweeterService.class);
    }

    public LiveData<Resource<List<Tweet>>> getTweets(HashMap<String, String> params, String search) {
        return new NetworkBoundResource<List<Tweet>, TweetSearchResponse>() {

            @Override
            public void saveApiCallResult(TweetSearchResponse response) {
                deleteAll();
                dao.insertTweets(response.getStatuses());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Tweet> data) {
                return !search.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Tweet>> loadFromDb() {
                return dao.getTweets();
            }
            
            @NonNull
            @Override
            protected Call<TweetSearchResponse> createCall() {
                return tweeterService.getTweets(params);
            }
        }.getAsLiveData();
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
