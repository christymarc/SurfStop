package fragments;

import static utils.WeatherConstants.API_KEY;
import static utils.WeatherConstants.API_URL;
import static utils.WeatherConstants.DESCRIPTION_KEY;
import static utils.WeatherConstants.MAIN_KEY;
import static utils.WeatherConstants.MYTIMEZONE;
import static utils.WeatherConstants.SUNSET_KEY;
import static utils.WeatherConstants.SYS_KEY;
import static utils.WeatherConstants.TEMP_KEY;
import static utils.WeatherConstants.TIMEZONE_KEY;
import static utils.WeatherConstants.WEATHER_KEY;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.surfstop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import adapters.PostAdapter;
import models.BasePost;
import models.BeachGroup;
import utils.InternetUtil;
import utils.QueryUtils;
import utils.TempUtils;
import utils.TimeUtils;

public class DescriptionBoxFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = DescriptionBoxFragment.class.getSimpleName();
    public static final String NO_BEACHES_POPUP = "You have no favorited beaches! To populate your main" +
            " timeline, go to the Groups' page and favorite some Beach Groups.";

    public List<BasePost> allPosts;
    public PostAdapter adapter;

    Spinner spinnerBeach;
    BeachGroup currentBeach;

    // BeachGroup description variables
    TextView tvGroupDescription;
    TextView tvMinBreak;
    TextView tvMaxBreak;
    TextView tvAirTemp;
    TextView tvWaterTemp;
    TextView tvWeather;
    TextView tvSunsetTime;

    // Weather data
    private String description;
    private String temperature;
    // Sunset time of local timezone
    private String sunsetTime;

    public DescriptionBoxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_description_box, container, false);
    }

    public void setAdapterAndList(PostAdapter adapter, List<BasePost> allPosts) {
        this.adapter = adapter;
        this.allPosts = allPosts;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();

        spinnerBeach = view.findViewById(R.id.spinnerBeach);
        spinnerBeach.setOnItemSelectedListener(this);

        if (InternetUtil.isInternetConnected()) {
            QueryUtils.queryBeachesForSpinner(fm, spinnerBeach, view);
        } else {
            QueryUtils.queryBeachesforSpinnerOffline(spinnerBeach, view);
        }

        tvGroupDescription = view.findViewById(R.id.tvGroupDescription);
        tvMinBreak = view.findViewById(R.id.tvMinBreak);
        tvMaxBreak = view.findViewById(R.id.tvMaxBreak);
        tvAirTemp = view.findViewById(R.id.tvAirTemp);
        tvWaterTemp = view.findViewById(R.id.tvWaterTemp);
        tvWeather = view.findViewById(R.id.tvWeather);
        tvSunsetTime = view.findViewById(R.id.tvSunsetTime);
    }

    public void bindDescription() {
        tvGroupDescription.setText(currentBeach.getKeyDescription());
        tvMinBreak.setText(currentBeach.getKeyMinbreak());
        tvMaxBreak.setText(currentBeach.getKeyMaxbreak());
        tvWaterTemp.setText(currentBeach.getKeyWatertemp());
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
                            temperature = TempUtils.kelvinToFahrenheit(Double.toString(tempKelvin));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        currentBeach = (BeachGroup) parent.getSelectedItem();
        setCurrentBeach(currentBeach);
        QueryUtils.queryShortPosts(getContext(), allPosts, adapter, currentBeach);
        Log.i(TAG, currentBeach.getKeyGroupName() + " selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        currentBeach = (BeachGroup) parent.getItemAtPosition(0);
        setCurrentBeach(currentBeach);
        QueryUtils.queryShortPosts(getContext(), allPosts, adapter, currentBeach);
    }

    public void setCurrentBeach(BeachGroup currentBeach) {
        this.currentBeach = currentBeach;
        bindDescription();
        getWeatherDetails(currentBeach.getKeyLocationid());
    }

    public BeachGroup getCurrentBeach() {
        return currentBeach;
    }
}
