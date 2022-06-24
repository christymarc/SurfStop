package fragments;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import adapters.PostAdapter;
import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import adapters.SpinnerAdapter;
import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import models.Post;
import models.ShortPost;
import utils.QueryUtils;

public class TempFeedFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    public static final String TAG = "TempFeedActivity";

    Spinner spinnerBeach;
    SpinnerAdapter beachAdapter;
    BeachGroup current_beach;

    // Group Description Variables
    TextView tvGroupDescriptionLabel;
    TextView tvGroupDescription;
    TextView tvMinBreak;
    TextView tvMaxBreak;
    TextView tvAirTemp;
    TextView tvWaterTemp;
    TextView tvWeather;
    TextView tvSunsetTime;


    // Feed variables
    private RecyclerView rvTempFeed;
    public List<BasePost> allPosts;
    public PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    public TempFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temp_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerBeach = view.findViewById(R.id.spinnerBeach);
        spinnerBeach.setOnItemSelectedListener(this);

        // Get user's favorite groups to populate spinner
        ParseQuery<FavoriteGroups> groupsQuery = ParseQuery.getQuery(FavoriteGroups.class);
        groupsQuery.include(FavoriteGroups.KEY_USER);
        groupsQuery.whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class);
        beachQuery.whereMatchesKeyInQuery(BeachGroup.KEY_GROUP, FavoriteGroups.KEY_GROUP, groupsQuery);
        beachQuery.findInBackground(new FindCallback<BeachGroup>() {
            @Override
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (BeachGroup group : groups) {
                    Log.i(TAG, "Group: " + group.getKeyGroupName());
                }
                beachAdapter = new SpinnerAdapter(view.getContext(),
                        R.layout.activity_custom_spinner, groups);
                spinnerBeach.setAdapter(beachAdapter);
            }
        });


        // Get views for description variables
        tvGroupDescriptionLabel = view.findViewById(R.id.tvGroupDescriptionLabel);
        tvGroupDescription = view.findViewById(R.id.tvGroupDescription);
        tvMinBreak = view.findViewById(R.id.tvMinBreak);
        tvMaxBreak = view.findViewById(R.id.tvMaxBreak);
        tvAirTemp = view.findViewById(R.id.tvAirTemp);
        tvWaterTemp = view.findViewById(R.id.tvWaterTemp);
        tvWeather = view.findViewById(R.id.tvWeather);
        tvSunsetTime = view.findViewById(R.id.tvSunsetTime);

        rvTempFeed = view.findViewById(R.id.rvTempFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // set the adapter on the recycler view
        rvTempFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvTempFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        //QueryUtils.queryShortPosts(allPosts, adapter, current_beach);

        // query more posts
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                QueryUtils.queryShortPosts(allPosts, adapter, current_beach);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        adapter.clear();
        current_beach = (BeachGroup) parent.getSelectedItem();
        QueryUtils.queryShortPosts(allPosts, adapter, current_beach);
        Log.i(TAG, current_beach.getKeyGroupName() + " selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        adapter.clear();
        current_beach = (BeachGroup) parent.getItemAtPosition(0);
        QueryUtils.queryShortPosts(allPosts, adapter, current_beach);
    }
}