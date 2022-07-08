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
import models.BaseGroup;
import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import models.Group;
import models.Post;
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

    public static void queryLongPosts(List<BasePost> allPosts, PostAdapter adapter, BaseGroup currentGroup) {
        adapter.clear();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class)
                .include(Post.KEY_USER);

        if (currentGroup != null) {
            query.whereEqualTo(Post.KEY_GROUP, currentGroup);
        }

        // Set number of items queried
        query.setLimit(QUERY_MAX_ITEMS)
                .addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Content: " + post.getKeyGroup());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void queryPersonalPosts(List<BasePost> allPosts, PostAdapter adapter) {
        adapter.clear();

        ParseQuery<ShortPost> query = ParseQuery.getQuery(ShortPost.class)
                .include(ShortPost.KEY_USER)
                .whereEqualTo(ShortPost.KEY_USER, ParseUser.getCurrentUser());

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

    public static void queryGroupsforGroups(Group otherGroup, Button favoriteButton, Button favoriteButtonPressed) {
        ParseQuery<FavoriteGroups> favQuery = ParseQuery.getQuery(FavoriteGroups.class)
                .include(FavoriteGroups.KEY_USER)
                .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<Group> groupQuery = ParseQuery.getQuery(Group.class)
                .whereEqualTo(Group.KEY_GROUP, otherGroup)
                .whereMatchesKeyInQuery(Group.KEY_GROUP, FavoriteGroups.KEY_GROUP, favQuery);
        groupQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query groups error", e);
                    return;
                }
                String currentGroupName = otherGroup.getKeyGroupName();
                for (Group group : groups) {
                    String queryGroupName = group.getKeyGroupName();
                    Log.i(TAG, "Group: " + queryGroupName);
                    if(queryGroupName.equals(currentGroupName)) {
                        favoriteButtonPressed.setVisibility(View.VISIBLE);
                        favoriteButton.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public static void queryGroups(List<BaseGroup> allGroups, GroupAdapter groupAdapter) {
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                .include(BeachGroup.KEY_GROUP);
        ParseQuery<Group> groupQuery = ParseQuery.getQuery(Group.class)
                .whereDoesNotMatchKeyInQuery(BeachGroup.KEY_GROUP, Group.KEY_GROUP, beachQuery);
        groupQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (Group group : groups) {
                    Log.i(TAG, "Non-beachGroup: " + group);
                }
                allGroups.addAll(groups);
                groupAdapter.notifyItemRangeChanged(0, allGroups.size());
            }
        });
    }

    public static void queryBeaches(List<BaseGroup> allBeaches, GroupAdapter adapter) {
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

    public static void queryFavorites(List<BaseGroup> favGroups, GroupAdapter adapter) {
        adapter.clear();

        ParseQuery<FavoriteGroups> favoriteGroupsQuery = ParseQuery.getQuery(FavoriteGroups.class)
                .include(FavoriteGroups.KEY_USER)
                .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<Group> allGroupsQuery = ParseQuery.getQuery(Group.class)
                .whereMatchesKeyInQuery(Group.KEY_GROUP, FavoriteGroups.KEY_GROUP, favoriteGroupsQuery);
        allGroupsQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (Group group : groups) {
                    Log.i(TAG, "Group: " + group);
                }
                favGroups.addAll(groups);
                adapter.notifyItemRangeChanged(0, favGroups.size());
            }
        });
    }
}
