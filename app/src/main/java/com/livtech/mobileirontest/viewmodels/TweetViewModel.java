package com.livtech.mobileirontest.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.livtech.mobileirontest.models.Resource;
import com.livtech.mobileirontest.models.Tweet;
import com.livtech.mobileirontest.repositories.TweetRepository;

import java.util.HashMap;
import java.util.List;

public class TweetViewModel extends AndroidViewModel {
    private TweetRepository repository;

    public TweetViewModel(@NonNull Application application) {
        super(application);
        repository = new TweetRepository();
    }

    public LiveData<Resource<List<Tweet>>> getTweets(HashMap<String, String> params, String search) {
        return repository.getTweets(params, search);
    }

}
