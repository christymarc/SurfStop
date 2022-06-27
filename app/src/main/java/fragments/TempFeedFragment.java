package fragments;

import android.app.DownloadManager;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.surfstop.BuildConfig;
import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapters.SpinnerAdapter;
import models.BasePost;
import models.BeachGroup;
import models.BeachWeather;
import models.FavoriteGroups;
import models.Post;
import models.ShortPost;
import utils.QueryUtils;
import utils.TempUtils;
import utils.TimeUtils;

public class TempFeedFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    public static final String TAG = TempFeedFragment.class.getSimpleName();

    Spinner spinnerBeach;
    SpinnerAdapter beachAdapter;
    BeachGroup current_beach;

    BeachWeather weather;

    // Group Description Variables
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

    // Weather data
    private String description;
    private String temperature;
    // sunset time of local timezone
    private String sunsetTime;
    long myTimeZone = -25200;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String api_key = BuildConfig.WEATHER_KEY;

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

    public void bindDescription() {
        tvGroupDescription.setText(current_beach.getKeyDescription());
        tvMinBreak.setText(current_beach.getKeyMinbreak());
        tvMaxBreak.setText(current_beach.getKeyMaxbreak());
        tvWaterTemp.setText(current_beach.getKeyWatertemp());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        current_beach = (BeachGroup) parent.getSelectedItem();
        getWeatherDetails(current_beach.getKeyLocationid());
        QueryUtils.queryShortPosts(allPosts, adapter, current_beach);
        bindDescription();
        Log.i(TAG, current_beach.getKeyGroupName() + " selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        current_beach = (BeachGroup) parent.getItemAtPosition(0);
        getWeatherDetails(current_beach.getKeyLocationid());
        QueryUtils.queryShortPosts(allPosts, adapter, current_beach);
        bindDescription();
    }

    public void getWeatherDetails(String locationID){
        String temp_url = url + "?id=" + locationID + "&appid=" + api_key;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, temp_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i(TAG, "response: " + response);

                            JSONArray jsonWeatherArray = jsonResponse.getJSONArray("weather");

                            JSONObject jsonWeatherObject = jsonWeatherArray.getJSONObject(0);
                            JSONObject jsonMainObject = jsonResponse.getJSONObject("main");
                            JSONObject jsonSysObject = jsonResponse.getJSONObject("sys");

                            description = jsonWeatherObject.getString("description");
                            double tempKelvin = jsonMainObject.getDouble("temp");
                            temperature = TempUtils.kelvin_to_fahrenheit(Double.toString(tempKelvin));
                            long sunsetUTC = jsonSysObject.getLong("sunset");
                            long locationTimeZone = jsonResponse.getLong("timezone");
                            // need to account for location timezone and my timezone
                            sunsetTime = TimeUtils.unixToUTC(sunsetUTC + locationTimeZone - myTimeZone);

                            tvWeather.setText(description);
                            tvAirTemp.setText(temperature);
                            tvSunsetTime.setText(sunsetTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error loading in weather data.");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}