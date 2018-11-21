package com.livtech.mobileirontest.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.livtech.imageloader.ImageLoader;
import com.livtech.mobileirontest.R;
import com.livtech.mobileirontest.adapters.TweetListAdapter;
import com.livtech.mobileirontest.enums.Status;
import com.livtech.mobileirontest.listeners.OnItemClickListener;
import com.livtech.mobileirontest.models.StatusUrl;
import com.livtech.mobileirontest.models.Tweet;
import com.livtech.mobileirontest.viewmodels.TweetViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private TweetViewModel viewModel;
    private ArrayList<Tweet> tweets;
    private EditText searchText;
    private RecyclerView recyclerView;
    private TweetListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoader.get().initLoader(getApplicationContext());
        initViewModel();
        initViews();
        fetchTweets("");
    }

    private void initViews() {
        initSearchView();
        initListView();
    }

    private void initSearchView() {
        searchText = findViewById(R.id.search_edit_text);
        searchText.addTextChangedListener(textWatcher);
    }

    private void initListView() {
        tweets = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new TweetListAdapter(tweets, this);
        recyclerView.setAdapter(adapter);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            fetchTweets(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initViewModel() {
        tweets = new ArrayList<>();
        viewModel = ViewModelProviders.of(this).get(TweetViewModel.class);
    }

    private void fetchTweets(String searchStr) {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", searchStr);
        viewModel.getTweets(params, searchStr).observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.ERROR) {
                    showMessage(listResource.message);
                } else if (listResource.status == Status.SUCCESS) {
                    updateSearchList(listResource.data);
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateSearchList(List<Tweet> list) {
        tweets.clear();
        tweets.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(View view, int position) {
        if (tweets.get(position).getEntities() != null) {
            Intent intent = new Intent(this, StatusViewActivity.class);
            List<StatusUrl> urlList = tweets.get(position).getEntities().getUrls();
            String url = urlList != null && !urlList.isEmpty() ? urlList.get(0).getExpanded_url() : "";
            if (url != null && !url.isEmpty()) {
                intent.putExtra("status_url", url);
                startActivity(intent);
            } else
                showMessage("You are viewing offline data");
        } else {
            showMessage("You are viewing offline data");
        }
    }
}
