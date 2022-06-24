package utils;

import static fragments.TempFeedFragment.TAG;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import adapters.PostAdapter;
import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import models.Group;
import models.Post;
import models.ShortPost;

public class QueryUtils {

    public static void queryShortPosts(List<BasePost> allPosts, PostAdapter adapter, BeachGroup current_beach) {
        ParseQuery<ShortPost> query = ParseQuery.getQuery(ShortPost.class);
        query.include(ShortPost.KEY_USER);

        if (current_beach != null) {
            query.whereEqualTo(ShortPost.KEY_BEACHGROUP, current_beach);
        }

        // Set number of items queried
        query.setLimit(20);
        // Order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
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

    public static List<BeachGroup> queryFavoriteBeaches() {
        List<BeachGroup> favorite_beaches = new ArrayList<>();

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
                favorite_beaches.addAll(groups);
                Log.i(TAG, favorite_beaches.toString());
            }
        });
        return favorite_beaches;
    }

    public static List<BeachGroup> queryBeaches() {
        List<BeachGroup> beaches = new ArrayList<>();
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class);
        query.include(BeachGroup.KEY_GROUP);
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
                beaches.addAll(groups);
            }
        });
        return beaches;
    }
}
