package com.livtech.mobileirontest.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.livtech.imageloader.ImageLoader;
import com.livtech.mobileirontest.R;
import com.livtech.mobileirontest.listeners.OnItemClickListener;
import com.livtech.mobileirontest.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetHolder extends RecyclerView.ViewHolder {
    private TextView textView;
    private ImageView imageView;

    public TweetHolder(View itemView, OnItemClickListener itemClickListener) {
        super(itemView);
        textView = itemView.findViewById(R.id.text);
        imageView = itemView.findViewById(R.id.image_view);
        itemView.setOnClickListener(view -> {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        });
    }

    public void bindData(Tweet tweet) {
        textView.setText(tweet.getText());
        if (tweet.getUser() != null) {
           /* Picasso.get()
                    .load(tweet.getUser().getProfile_image_url_https())
                    .into(imageView);*/
            ImageLoader.get().loadImage(tweet.getUser().getProfile_image_url_https(),imageView);
        }
    }
}
