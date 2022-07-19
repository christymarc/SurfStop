package fragments;

import static utils.WeatherConstants.*;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import adapters.PostAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.surfstop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import models.BasePost;
import models.BeachGroup;
import utils.QueryUtils;
import utils.TempUtils;
import utils.TimeUtils;

public class TempFeedFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        ComposeDialogFragment.ComposeDialogListener {

    public static final String TAG = TempFeedFragment.class.getSimpleName();

    Spinner spinnerBeach;
    BeachGroup current_beach;

    // Group Description Variables
    TextView tvGroupDescription;
    TextView tvMinBreak;
    TextView tvMaxBreak;
    TextView tvAirTemp;
    TextView tvWaterTemp;
    TextView tvWeather;
    TextView tvSunsetTime;

    FloatingActionButton composeFab;

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

        QueryUtils.queryBeachesForSpinner(spinnerBeach, view);

        // Get views for description variables
        tvGroupDescription = view.findViewById(R.id.tvGroupDescription);
        tvMinBreak = view.findViewById(R.id.tvMinBreak);
        tvMaxBreak = view.findViewById(R.id.tvMaxBreak);
        tvAirTemp = view.findViewById(R.id.tvAirTemp);
        tvWaterTemp = view.findViewById(R.id.tvWaterTemp);
        tvWeather = view.findViewById(R.id.tvWeather);
        tvSunsetTime = view.findViewById(R.id.tvSunsetTime);

        composeFab = view.findViewById(R.id.composeFab);

        rvTempFeed = view.findViewById(R.id.rvTempFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        // set the adapter on the recycler view
        rvTempFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvTempFeed.setLayoutManager(new LinearLayoutManager(getContext()));

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
        String temp_url = API_URL + "?id=" + locationID + "&appid=" + API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, temp_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i(TAG, "response: " + response);

                            JSONArray jsonWeatherArray = jsonResponse.getJSONArray(WEATHER_KEY);

                            JSONObject jsonWeatherObject = jsonWeatherArray.getJSONObject(0);
                            JSONObject jsonMainObject = jsonResponse.getJSONObject(MAIN_KEY);
                            JSONObject jsonSysObject = jsonResponse.getJSONObject(SYS_KEY);

                            description = jsonWeatherObject.getString(DESCRIPTION_KEY);
                            double tempKelvin = jsonMainObject.getDouble(TEMP_KEY);
                            temperature = TempUtils.kelvin_to_fahrenheit(Double.toString(tempKelvin));
                            long sunsetUTC = jsonSysObject.getLong(SUNSET_KEY);
                            long locationTimeZone = jsonResponse.getLong(TIMEZONE_KEY);
                            // need to account for location timezone and my timezone
                            sunsetTime = TimeUtils.unixToUTC(sunsetUTC + locationTimeZone - MYTIMEZONE);

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

    public void onComposeButton(View view) {
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(this.current_beach);
        composeDialogFragment.setListener(this);
        composeDialogFragment.show(fm, "compose_fragment");
    }

    @Override
    public void onFinishComposeDialog(BasePost post) {
        // Update the RecyclerView with this new tweet
        // Modify data source of tweets
        allPosts.add(0, post);
        // Update the adapter
        adapter.notifyItemInserted(0);
        rvTempFeed.smoothScrollToPosition(0);
    }
}