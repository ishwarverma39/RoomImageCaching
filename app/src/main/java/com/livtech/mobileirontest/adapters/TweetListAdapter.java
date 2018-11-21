package com.livtech.mobileirontest.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livtech.mobileirontest.R;
import com.livtech.mobileirontest.listeners.OnItemClickListener;
import com.livtech.mobileirontest.models.Tweet;
import com.livtech.mobileirontest.ui.viewholders.TweetHolder;

import java.util.List;

public class TweetListAdapter extends RecyclerView.Adapter<TweetHolder> {
    private List<Tweet> tweets;
    private OnItemClickListener itemClickListener;

    public TweetListAdapter(List<Tweet> tweets, OnItemClickListener itemClickListener) {
        this.tweets = tweets;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TweetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,parent,false);
        return  new TweetHolder(view,itemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull TweetHolder holder, int position) {
        holder.bindData(tweets.get(position));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
