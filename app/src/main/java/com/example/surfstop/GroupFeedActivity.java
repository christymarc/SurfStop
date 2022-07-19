package com.example.surfstop;

import static utils.QueryUtils.ROOM_POST_DAO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import adapters.PostAdapter;
import fragments.ComposeDialogGroupFragment;
import models.BasePost;
import models.Group;
import models.Post;
import models.RoomPost;
import models.RoomUser;
import utils.QueryUtils;

public class GroupFeedActivity extends AppCompatActivity implements
        ComposeDialogGroupFragment.ComposeDialogGroupListener {

    FloatingActionButton composeFab;

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

        allPosts = new ArrayList<>();
        adapter = new PostAdapter(this, allPosts);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        currentGroup = (Group) Parcels.unwrap(getIntent().getParcelableExtra(Group.class.getSimpleName()));
        tvGroupLabel = findViewById(R.id.tvGroupTimelineLabel);
        tvGroupLabel.setText(currentGroup.getKeyGroupName());

        // Set adapter on the recycler view
        rvGroupFeed.setAdapter(adapter);
        rvGroupFeed.setLayoutManager(new LinearLayoutManager(this));

        QueryUtils.queryLongPosts(this, allPosts, adapter, currentGroup);

        composeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton(view);
            }
        });

        // Query more posts upon refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                QueryUtils.queryLongPosts(GroupFeedActivity.this, allPosts, adapter, currentGroup);
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
        FragmentTransaction fm = this.getSupportFragmentManager().beginTransaction();
        ComposeDialogGroupFragment composeDialogGroupFragment = ComposeDialogGroupFragment.newInstance(currentGroup);
        composeDialogGroupFragment.setListener(this);
        composeDialogGroupFragment.show(fm, "compose_fragment");
    }

    @Override
    public void onFinishComposeDialog(BasePost post) {
        // Update adapter with newest post
        allPosts.add(0, post);
        adapter.notifyItemInserted(0);
        rvGroupFeed.smoothScrollToPosition(0);

        // Put new post into local DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RoomUser roomUser = new RoomUser(ParseUser.getCurrentUser());
                ParseUser.getCurrentUser().pinInBackground();
                RoomPost roomPost = new RoomPost((Post) post);

                ROOM_POST_DAO.insertUser(roomUser);
                ROOM_POST_DAO.insertPost(roomPost);
            }
        });
    }
}