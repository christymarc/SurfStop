package fragments;

import static utils.QueryUtils.ROOM_SHORT_POST_DAO;
import static utils.WeatherConstants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import models.RoomShortPost;
import models.RoomUser;
import models.ShortPost;
import utils.InternetUtil;
import utils.QueryUtils;
import utils.TempUtils;
import utils.TimeUtils;

public class TempFeedFragment extends Fragment implements ComposeDialogFragment.ComposeDialogListener {

    public static final String TAG = TempFeedFragment.class.getSimpleName();
    public static final String WEATHER_POPUP = "Weather is unavailable when offline. Connect to internet" +
            " to access live weather updates.";

    FloatingActionButton composeFab;

    // Feed variables
    private RecyclerView rvTempFeed;
    public List<BasePost> allPosts;
    public PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    DescriptionBoxFragment descriptionBoxFragment;

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

        composeFab = view.findViewById(R.id.composeFab);

        rvTempFeed = view.findViewById(R.id.rvTempFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        // get and set description box fragment
        FragmentManager fm = this.getChildFragmentManager();
        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        descriptionBoxFragment = new DescriptionBoxFragment();
        descriptionBoxFragment.setAdapterAndList(adapter, allPosts);
        fragmentTransaction.replace(R.id.fragmentContainerView, descriptionBoxFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

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
                BeachGroup currentBeach = descriptionBoxFragment.getCurrentBeach();
                if (InternetUtil.isInternetConnected()) {
                    QueryUtils.queryShortPosts(allPosts, adapter, currentBeach);
                } else {
                    QueryUtils.queryShortPostOffline(getContext(), allPosts, adapter, currentBeach);
                }
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Live weather cannot be accessed when offline
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = pref.edit();
        if (!InternetUtil.isInternetConnected() && !pref.getBoolean("weatherShownAlready", false)) {
            PopupDialogFragment popupDialogFragment = PopupDialogFragment.newInstance(WEATHER_POPUP);
            popupDialogFragment.show(fm, "weather_fragment");
            // Updating preferences to say this popup has been shown
            edit.putBoolean("weatherShownAlready", true);
            edit.commit();
        }
        else if (InternetUtil.isInternetConnected()) {
            // Updating preference when internet is connected and popup has been shown so
            // when a user goes online then offline again, they'll see the notification
            if (pref.getBoolean("weatherShownAlready", false)) {
                edit.putBoolean("weatherShownAlready", false);
                edit.commit();
            }
        }
    }

    public void onComposeButton(View view) {
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(descriptionBoxFragment.getCurrentBeach());
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

        // Put new post into local DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RoomUser roomUser = new RoomUser(post.getKeyUser());
                RoomShortPost roomShortPost = new RoomShortPost((ShortPost) post);
                ROOM_SHORT_POST_DAO.insertUser(roomUser);
                ROOM_SHORT_POST_DAO.insertShortPost(roomShortPost);
            }
        });
    }
}