package com.livtech.mobileirontest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.livtech.mobileirontest.models.Tweet;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT * from tweets")
    LiveData<List<Tweet>> getTweets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweets(List<Tweet> tweets);

    @Query("DELETE FROM tweets")
    void deleteAll();
}
