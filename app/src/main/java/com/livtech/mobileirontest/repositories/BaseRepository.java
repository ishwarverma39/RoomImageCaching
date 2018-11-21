package com.livtech.mobileirontest.repositories;

import android.app.Application;

import com.livtech.mobileirontest.database.TweetDatabase;
import com.livtech.mobileirontest.network.ApiCallHandler;

public class BaseRepository {
    private ApiCallHandler apiCallHandler;
    protected TweetDatabase db;
    protected final String TAG;

    public BaseRepository() {
        apiCallHandler = ApiCallHandler.newInstance();
        db = TweetDatabase.getDataBase();
        TAG = this.getClass().getSimpleName();
    }

    protected <T> T getService(Class<T> service) {
        return apiCallHandler.createService(service);
    }
}
