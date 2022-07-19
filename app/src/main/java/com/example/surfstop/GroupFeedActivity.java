package com.example.surfstop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import adapters.PostAdapter;
import models.BaseGroup;
import models.BasePost;
import models.BeachGroup;
import models.Group;
import utils.QueryUtils;

public class GroupFeedActivity extends AppCompatActivity {
    FloatingActionButton composeFab;

    // Feed variables
    private RecyclerView rvGroupFeed;
    public List<BasePost> allPosts;
    public PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    Group currentGroup;
    TextView tvGroupLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feed);

        composeFab = findViewById(R.id.composeFab);

        rvGroupFeed = findViewById(R.id.rvTempFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(this, allPosts);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        currentGroup = (Group) Parcels.unwrap(getIntent().getParcelableExtra(Group.class.getSimpleName()));
        tvGroupLabel = findViewById(R.id.tvGroupTimelineLabel);
        tvGroupLabel.setText(currentGroup.getKeyGroupName());

        // set the adapter on the recycler view
        rvGroupFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvGroupFeed.setLayoutManager(new LinearLayoutManager(this));

        QueryUtils.queryLongPosts(allPosts, adapter, currentGroup);

        composeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton(view);
            }
        });

        // query more posts
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                QueryUtils.queryLongPosts(allPosts, adapter, currentGroup);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void onComposeButton(View view) {
        //TODO: compose long post functionality
    }
}