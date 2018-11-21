package com.livtech.mobileirontest.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.livtech.mobileirontest.models.Tweet;

@Database(entities = {Tweet.class}, version = 1, exportSchema = false)
public abstract class TweetDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "tweet_database";
    private static TweetDatabase INSTANCE = null;

    public static TweetDatabase getDataBase() {
       return INSTANCE;
    }

    public static void initDatabase(Context context){
        if (INSTANCE == null) {
            synchronized (TweetDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            TweetDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
    }
    public abstract TweetDao tweetDao();
}
