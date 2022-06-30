package utils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import adapters.GroupAdapter;
import adapters.PostAdapter;
import adapters.SpinnerAdapter;
import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import models.ShortPost;

public class QueryUtils {

    public static final String TAG = QueryUtils.class.getSimpleName();
    public static final int QUERY_MAX_ITEMS = 20;

    public static void queryShortPosts(List<BasePost> allPosts, PostAdapter adapter, BeachGroup current_beach) {
        adapter.clear();

        ParseQuery<ShortPost> query = ParseQuery.getQuery(ShortPost.class)
                .include(ShortPost.KEY_USER);

        if (current_beach != null) {
            query.whereEqualTo(ShortPost.KEY_BEACHGROUP, current_beach);
        }

        // Set number of items queried
        query.setLimit(QUERY_MAX_ITEMS)
                .addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<ShortPost>() {
            @Override
            public void done(List<ShortPost> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (ShortPost post : posts) {
                    Log.i(TAG, "Content: " + post.getKeyBeachGroup());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void queryBeachesForSpinner(Spinner spinnerBeach, View view) {
        // Get user's favorite groups to populate spinner
        ParseQuery<FavoriteGroups> groupsQuery = ParseQuery.getQuery(FavoriteGroups.class)
                .include(FavoriteGroups.KEY_USER)
                .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                .whereMatchesKeyInQuery(BeachGroup.KEY_GROUP, FavoriteGroups.KEY_GROUP, groupsQuery);
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
                SpinnerAdapter beachAdapter = new SpinnerAdapter(view.getContext(), R.layout.activity_custom_spinner, groups);
                spinnerBeach.setAdapter(beachAdapter);
            }
        });
    }

    public static void queryBeachesforGroups(BeachGroup beach, Button favoriteButton, Button favoriteButtonPressed){
        ParseQuery<FavoriteGroups> groupsQuery = ParseQuery.getQuery(FavoriteGroups.class)
                .include(FavoriteGroups.KEY_USER)
                .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                .whereEqualTo(BeachGroup.KEY_GROUP, beach)
                .whereMatchesKeyInQuery(BeachGroup.KEY_GROUP, FavoriteGroups.KEY_GROUP, groupsQuery);
        beachQuery.findInBackground(new FindCallback<BeachGroup>() {
            @Override
            public void done(List<BeachGroup> groups, ParseException e) {
                String queryBeachName;
                String currentBeachName = beach.getKeyGroupName();
                if (e != null) {
                    Log.e(TAG, "Query groups error", e);
                    return;
                }
                for (BeachGroup group : groups) {
                    queryBeachName = group.getKeyGroupName();
                    Log.i(TAG, "Group: " + queryBeachName);
                    if(queryBeachName.equals(currentBeachName)) {
                        favoriteButtonPressed.setVisibility(View.VISIBLE);
                        favoriteButton.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public static void queryBeaches(List<BeachGroup> allBeaches, GroupAdapter adapter) {
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .include(BeachGroup.KEY_GROUP);
        query.findInBackground(new FindCallback<BeachGroup>() {
            @Override
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (BeachGroup group : groups) {
                    Log.i(TAG, "Group: " + group);
                }
                allBeaches.addAll(groups);
                adapter.notifyItemRangeChanged(0, allBeaches.size());
            }
        });
    }
}
