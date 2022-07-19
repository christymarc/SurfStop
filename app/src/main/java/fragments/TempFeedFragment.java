package fragments;

import static utils.QueryUtils.ROOM_SHORT_POST_DAO;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapters.PostAdapter;

import com.example.surfstop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import models.BasePost;
import models.BeachGroup;
import models.RoomShortPost;
import models.RoomUser;
import models.ShortPost;
import utils.InternetUtil;
import utils.QueryUtils;

public class TempFeedFragment extends Fragment implements ComposeDialogFragment.ComposeDialogListener {

    public static final String TAG = TempFeedFragment.class.getSimpleName();
    public static final String WEATHER_POPUP = "Weather is unavailable when offline. Connect to internet" +
            " to access live weather updates.";

    FloatingActionButton composeFab;

    private RecyclerView rvTempFeed;
    public List<BasePost> allPosts;
    public PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    DescriptionBoxFragment descriptionBoxFragment;

    public TempFeedFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        composeFab = view.findViewById(R.id.composeFab);

        rvTempFeed = view.findViewById(R.id.rvTempFeed);

        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        // Get and set description box fragment
        FragmentManager fm = this.getChildFragmentManager();
        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        descriptionBoxFragment = new DescriptionBoxFragment();
        descriptionBoxFragment.setAdapterAndList(adapter, allPosts);
        fragmentTransaction.replace(R.id.fragmentContainerView, descriptionBoxFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        rvTempFeed.setAdapter(adapter);
        rvTempFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        composeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton(view);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BeachGroup currentBeach = descriptionBoxFragment.getCurrentBeach();
                QueryUtils.queryShortPosts(getContext(), allPosts, adapter, currentBeach);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Notify user that live weather cannot be accessed when offline
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = pref.edit();
        if (!InternetUtil.isInternetConnected() && !pref.getBoolean("weatherShownAlready", false)) {
            PopupDialogFragment popupDialogFragment = PopupDialogFragment.newInstance(WEATHER_POPUP);
            popupDialogFragment.show(fm, "weather_fragment");
            // Update preferences to say this popup has been shown
            edit.putBoolean("weatherShownAlready", true);
            edit.commit();
        }
        else if (InternetUtil.isInternetConnected()) {
            // Update preference when internet is connected and popup has been shown so
            // when a user goes online then offline again, they'll see the notification
            if (pref.getBoolean("weatherShownAlready", false)) {
                edit.putBoolean("weatherShownAlready", false);
                edit.commit();
            }
        }
    }

    public void onComposeButton(View view) {
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance
                (descriptionBoxFragment.getCurrentBeach());
        composeDialogFragment.setListener(this);
        composeDialogFragment.show(fm, "compose_fragment");
    }

    @Override
    public void onFinishComposeDialog(BasePost post) {
        // Update the adapter to have the most recent post by the current user
        allPosts.add(0, post);
        // Update the adapter
        adapter.notifyItemInserted(0);
        rvTempFeed.smoothScrollToPosition(0);

        // Put new post into local DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RoomUser roomUser = new RoomUser(ParseUser.getCurrentUser());
                ParseUser.getCurrentUser().pinInBackground();
                RoomShortPost roomPost = new RoomShortPost((ShortPost) post);

                ROOM_SHORT_POST_DAO.insertUser(roomUser);
                ROOM_SHORT_POST_DAO.insertShortPost(roomPost);
            }
        });
    }
}