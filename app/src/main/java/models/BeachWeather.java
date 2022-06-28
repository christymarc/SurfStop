package models;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.surfstop.BuildConfig;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import utils.TempUtils;
import utils.TimeUtils;

public class BeachWeather {

    final String TAG = BeachWeather.class.getSimpleName();

    private String locationID;
    private String description;
    private String temperature;
    // sunset time of local timezone
    private String sunsetTime;
    long myTimeZone = -25200;
    
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String api_key = BuildConfig.WEATHER_KEY;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public BeachWeather(Context context, String locationID) {
        this.locationID = locationID;
        this.getWeatherDetails(context);
        Log.i(TAG, "weather object made");
    }

    public void getWeatherDetails(Context context){
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public String getDescription() {
        return description;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
