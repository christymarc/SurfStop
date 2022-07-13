package utils;

import static com.parse.Parse.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.surfstop.MainActivity;
import com.example.surfstop.ParseApplication;
import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
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
import models.RoomShortPost;
import models.RoomShortPostDao;
import models.RoomShortPostWithObjects;
import models.RoomUser;
import models.ShortPost;

public class QueryUtils {

    public static final String TAG = QueryUtils.class.getSimpleName();
    public static final int QUERY_MAX_ITEMS = 20;
    public static final RoomShortPostDao ROOM_SHORT_POST_DAO = ((ParseApplication) getApplicationContext()).getMyDatabase().roomShortPostDao();

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
                    Log.i(TAG, "Content: " + post.getKeyContent());
                    // Pin user into local data store
                    // so when we call RoomUser from SQL DB, we can match it with a ParseUser
                    post.getKeyUser().pinInBackground();
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Saving data into the database");
                        List<RoomShortPost> roomShortPosts = new ArrayList<>();
                        for (ShortPost post : posts) {
                            RoomShortPost roomPost = new RoomShortPost(post);
                            roomShortPosts.add(roomPost);
                        }
                        List<RoomUser> roomUsers = RoomShortPostWithObjects.usersFromRoomShortPosts(roomShortPosts);
                        // Must load in users before posts in order for foreign key to work
                        ROOM_SHORT_POST_DAO.insertModel(roomUsers.toArray(new RoomUser[0]));
                        ROOM_SHORT_POST_DAO.insertModel(roomShortPosts.toArray(new RoomShortPost[0]));
                    }
                });
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void queryShortPostOffline(Context context, List<BasePost> allPosts,
                                             PostAdapter adapter, BeachGroup beachGroup) {
        adapter.clear();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Loading in posts from DB...");
                List<RoomShortPostWithObjects> shortPostsDB = ROOM_SHORT_POST_DAO.currentItems();
                List<RoomShortPost> roomPosts = RoomShortPostWithObjects.getRoomShortPostList(shortPostsDB, beachGroup);
                for(RoomShortPost roomPost : roomPosts) {
                    Log.i(TAG, roomPost.content);
                    ParseUser user = ParseUser.createWithoutData(ParseUser.class, roomPost.roomUserId);
                    user.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                ShortPost post = new ShortPost(roomPost, user);
                                Log.i(TAG, "ShortPost created: " + post.getKeyContent());
                                user.unpinInBackground();
                                allPosts.add(post);

                                // UI elements must run on the thread they were created on
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                Log.e(TAG, "Error loading in users offline");
                            }
                        }
                    });
                }
            }
        });
    }

    public static void queryLongPosts(List<BasePost> allPosts, PostAdapter adapter, Group currentGroup) {
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
                    Log.i(TAG, "Content: " + post.getKeyUser());
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
                BeachGroup.pinAllInBackground(groups);
                SpinnerAdapter beachAdapter = new SpinnerAdapter(view.getContext(), R.layout.activity_custom_spinner, groups);
                spinnerBeach.setAdapter(beachAdapter);
            }
        });
    }

    public static void queryBeachesforSpinnerOffline(Spinner spinnerBeach, View view) {
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .fromLocalDatastore();
        query.findInBackground(new FindCallback<BeachGroup>() {
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "BeachGroups queried from local storage");
                } else {
                    Log.e(TAG, "Error loading in BeachGroups from local storage");
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
