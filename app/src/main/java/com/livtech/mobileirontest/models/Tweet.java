package com.livtech.mobileirontest.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

@Entity(tableName = "tweets")
public class Tweet {
    @NonNull
    @PrimaryKey
    private String id_str;
    private String text;
    private String source;
    private String entities_str;
    private String user_str;
    @Ignore
    private StatusEntity entities;
    @Ignore
    private TweetUser user;

    public String getText() {
        return text;
    }

    public String getId_str() {
        return id_str;
    }

    public String getSource() {
        return source;
    }

    public void setId_str(@NonNull String id_str) {
        this.id_str = id_str;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setEntities(StatusEntity entities) {
        this.entities = entities;
    }

    public StatusEntity getEntities() {
        if (entities == null) {
            entities = new Gson().fromJson(entities_str, StatusEntity.class);
        }
        return entities;
    }

    public String getEntities_str() {
        if (entities_str == null) {
            entities_str = new Gson().toJson(entities);
        }
        return entities_str;
    }

    public void setEntities_str(String entities_str) {
        this.entities_str = entities_str;
    }

    public String getUser_str() {
        if (user_str == null) {
            user_str = new Gson().toJson(user);
        }
        return user_str;
    }

    public void setUser_str(String user_str) {
        this.user_str = user_str;
    }

    public TweetUser getUser() {
        if (user == null)
            user = new Gson().fromJson(user_str, TweetUser.class);
        return user;
    }
}
