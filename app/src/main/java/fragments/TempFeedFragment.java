package fragments;

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

import java.util.ArrayList;
import java.util.List;

import adapters.SpinnerAdapter;
import models.BeachGroup;
import models.Post;
import models.ShortPost;
import utils.QueryUtils;

public class TempFeedFragment extends Fragment {

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
    public List<Post> allPosts;
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
        List<BeachGroup> beach_array = QueryUtils.queryFavoriteBeaches();
        beachAdapter = new SpinnerAdapter(view.getContext(),
                R.layout.activity_custom_spinner, beach_array);
        spinnerBeach.setAdapter(beachAdapter);

        spinnerBeach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                current_beach = (BeachGroup) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                current_beach = (BeachGroup) parent.getItemAtPosition(0);
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

        QueryUtils.queryShortPosts(allPosts, adapter);

        // query more posts
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                QueryUtils.queryShortPosts(allPosts, adapter);
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
}