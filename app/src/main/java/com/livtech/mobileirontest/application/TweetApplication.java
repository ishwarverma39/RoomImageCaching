package com.livtech.mobileirontest.application;

import android.app.Application;

import com.livtech.mobileirontest.database.TweetDatabase;

public class TweetApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TweetDatabase.initDatabase(getApplicationContext());
    }
}
