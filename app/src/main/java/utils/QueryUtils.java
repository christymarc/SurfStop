package utils;

import static com.parse.Parse.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.FragmentTransaction;

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
import fragments.PopupDialogFragment;
import models.BaseGroup;
import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import models.Group;
import models.Post;
import models.RoomFavoriteGroups;
import models.RoomFavoriteGroupsDao;
import models.RoomPost;
import models.RoomPostDao;
import models.RoomPostWithObjects;
import models.RoomShortPost;
import models.RoomShortPostDao;
import models.RoomShortPostWithObjects;
import models.RoomUser;
import models.ShortPost;

public class QueryUtils {

    public static final String TAG = QueryUtils.class.getSimpleName();
    public static final int QUERY_MAX_ITEMS = 20;

    public static final RoomShortPostDao ROOM_SHORT_POST_DAO =
            ((ParseApplication) getApplicationContext()).getMyDatabase().roomShortPostDao();
    public static final RoomPostDao ROOM_POST_DAO =
            ((ParseApplication) getApplicationContext()).getMyDatabase().roomPostDao();
    public static final RoomFavoriteGroupsDao ROOM_FAVORITE_GROUPS_DAO =
            ((ParseApplication) getApplicationContext()).getMyDatabase().roomFavoriteGroupsDao();

    public static void queryShortPosts(Context context, List<BasePost> allPosts,
                                       PostAdapter adapter, BeachGroup currentBeach) {
        if (InternetUtil.isInternetConnected()) {
            queryShortPostsOnline(context, allPosts, adapter, currentBeach);
        }
        else {
            queryShortPostsOffline(context, allPosts, adapter, currentBeach);
        }
    }

    public static void queryShortPostsOnline(Context context, List<BasePost> allPosts,
                                             PostAdapter adapter, BeachGroup currentBeach) {
        allPosts.clear();

        ParseQuery<ShortPost> query = ParseQuery.getQuery(ShortPost.class)
                .include(ShortPost.KEY_USER);

        if (currentBeach != null) {
            query.whereEqualTo(ShortPost.KEY_BEACHGROUP, currentBeach);
        }

        // Set number of items queried and order
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
                    // Pin user into local data store
                    // so when we call RoomUser from SQL DB, we can match it with a ParseUser
                    post.getKeyUser().pinInBackground();
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Load post data in from the Room DB
                        List<RoomShortPostWithObjects> postsDB = ROOM_SHORT_POST_DAO.currentItems();
                        List<RoomShortPost> roomPosts = RoomShortPostWithObjects
                                .getRoomShortPostList(postsDB, currentBeach);

                        // Load in posts that are in the room DB but missing from the Parse DB
                        if (roomPosts.size() > posts.size()) {
                            for (int i = roomPosts.size() - posts.size() - 1; i >= 0; i--) {
                                RoomShortPost roomPost = roomPosts.get(i);

                                // Ensure posts being loaded in aren't over 12 hours old (edge cases)
                                long postCreatedAt = roomPost.createdAt.getTime();
                                long currentTime = System.currentTimeMillis();
                                double hourDifference = (currentTime - postCreatedAt)
                                        / (double) TimeUtils.HOUR_MILLIS;
                                if (hourDifference >= 12) {
                                    continue;
                                }

                                ParseUser user = ParseUser.createWithoutData(ParseUser.class, roomPost.roomUserId);

                                // Load User in from local Parse DB with post's associated roomUserId key
                                user.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                                    public void done(ParseUser user, ParseException e) {
                                        if (e == null) {
                                            ShortPost missingPost = new ShortPost(roomPost, user);

                                            BeachGroup postBeachGroup = BeachGroup.createWithoutData
                                                    (BeachGroup.class, roomPost.roomBeachGroupId);

                                            // Load BeachGroup in from local Parse DB
                                            postBeachGroup.fetchFromLocalDatastoreInBackground
                                                    (new GetCallback<BeachGroup>() {
                                                @Override
                                                public void done(BeachGroup beachGroup, ParseException e) {
                                                    // Set required ShortPost fields for saving
                                                    missingPost.setKeyBeachGroup(beachGroup);
                                                    missingPost.setKeyGroup(beachGroup.getKeyGroup());

                                                    missingPost.saveInBackground();
                                                }
                                            });
                                            posts.add(0, missingPost);
                                        }
                                    }
                                });
                            }
                        }
                        // Save data in Parse DB into the Room DB
                        else if (roomPosts.size() < posts.size()) {
                            roomPosts = new ArrayList<>();
                            for (ShortPost post : posts) {
                                RoomShortPost roomPost = new RoomShortPost(post);
                                roomPosts.add(roomPost);
                            }
                            List<RoomUser> roomUsers = RoomShortPostWithObjects
                                    .usersFromRoomShortPosts(roomPosts);
                            // Must load in users before posts in order for foreign key to work
                            ROOM_SHORT_POST_DAO.insertModel(roomUsers.toArray(new RoomUser[0]));
                            ROOM_SHORT_POST_DAO.insertModel(roomPosts.toArray(new RoomShortPost[0]));
                        }

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allPosts.addAll(posts);
                                adapter.notifyItemRangeChanged(0, allPosts.size());
                            }
                        });
                    }
                });
            }
        });
    }

    public static void queryShortPostsOffline(Context context, List<BasePost> allPosts,
                                             PostAdapter adapter, BeachGroup beachGroup) {
        allPosts.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Load post data in from Room DB
                List<RoomShortPostWithObjects> shortPostsDB = ROOM_SHORT_POST_DAO.currentItems();
                List<RoomShortPost> roomPosts = RoomShortPostWithObjects
                        .getRoomShortPostList(shortPostsDB, beachGroup);

                for(RoomShortPost roomPost : roomPosts) {
                    ParseUser user = ParseUser.createWithoutData(ParseUser.class, roomPost.roomUserId);

                    // Load user data in from local Parse DB
                    user.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                ShortPost post = new ShortPost(roomPost, user);
                                user.unpinInBackground();
                                allPosts.add(post);

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(allPosts.size() - 1);
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

    public static void queryLongPosts(Context context, List<BasePost> allPosts,
                                      PostAdapter adapter, Group currentGroup) {
        if (InternetUtil.isInternetConnected()) {
            queryLongPostsOnline(context, allPosts, adapter, currentGroup);
        }
        else {
            queryLongPostsOffline(context, allPosts, adapter, currentGroup);
        }
    }

    public static void queryLongPostsOnline(Context context, List<BasePost> allPosts,
                                            PostAdapter adapter, Group currentGroup) {
        allPosts.clear();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class)
                .include(Post.KEY_USER);

        if (currentGroup != null) {
            query.whereEqualTo(Post.KEY_GROUP, currentGroup);
        }

        // Set number of items queried and order
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
                    post.getKeyUser().pinInBackground();
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<RoomPostWithObjects> postsDB = ROOM_POST_DAO.currentItems();
                        List<RoomPost> roomPosts = RoomPostWithObjects
                                .getRoomPostList(postsDB, currentGroup);

                        if (roomPosts.size() > posts.size()) {
                            for (int i = roomPosts.size() - posts.size() - 1; i >= 0; i--) {
                                RoomPost roomPost = roomPosts.get(i);
                                ParseUser user = ParseUser
                                        .createWithoutData(ParseUser.class, roomPost.roomUserId);

                                user.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                                    public void done(ParseUser user, ParseException e) {
                                        if (e == null) {
                                            Post missingPost = new Post(roomPost, user);

                                            Group postGroup = Group.createWithoutData
                                                    (Group.class, roomPost.roomGroupId);
                                            postGroup.fetchFromLocalDatastoreInBackground
                                                    (new GetCallback<Group>() {
                                                @Override
                                                public void done(Group group, ParseException e) {
                                                    missingPost.setKeyGroup(group);
                                                    missingPost.saveInBackground();
                                                }
                                            });
                                            posts.add(0, missingPost);
                                        }
                                    }
                                });
                            }
                        }
                        else if (roomPosts.size() < posts.size()) {
                            roomPosts = new ArrayList<>();
                            for (Post post : posts) {
                                RoomPost roomPost = new RoomPost(post);
                                roomPosts.add(roomPost);
                            }
                            List<RoomUser> roomUsers = RoomPostWithObjects.usersFromRoomPosts(roomPosts);
                            // Must load in users before posts in order for foreign key to work
                            ROOM_POST_DAO.insertModel(roomUsers.toArray(new RoomUser[0]));
                            ROOM_POST_DAO.insertModel(roomPosts.toArray(new RoomPost[0]));
                        }

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allPosts.addAll(posts);
                                adapter.notifyItemRangeChanged(0, allPosts.size());
                            }
                        });
                    }
                });
            }
        });
    }

    public static void queryLongPostsOffline(Context context, List<BasePost> allPosts,
                                             PostAdapter adapter, Group currentGroup) {
        allPosts.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<RoomPostWithObjects> postsDB = ROOM_POST_DAO.currentItems();
                List<RoomPost> roomPosts = RoomPostWithObjects.getRoomPostList(postsDB, currentGroup);

                for(RoomPost roomPost : roomPosts) {
                    ParseUser user = ParseUser.createWithoutData(ParseUser.class, roomPost.roomUserId);
                    user.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Post post = new Post(roomPost, user);
                                user.unpinInBackground();
                                allPosts.add(post);

                                // UI elements must run on the thread they were created on
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(allPosts.size() - 1);
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

    public static void queryPersonalBeachPosts(Context context, List<BasePost> allPosts, PostAdapter adapter) {
        if (InternetUtil.isInternetConnected()) {
            queryPersonalBeachPostsOnline(allPosts, adapter);
        }
        else {
            queryPersonalBeachPostsOffline(context, allPosts, adapter);
        }
    }

    public static void queryPersonalBeachPostsOnline(List<BasePost> allPosts, PostAdapter adapter) {
        allPosts.clear();

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
                allPosts.addAll(posts);
                adapter.notifyItemRangeChanged(0, allPosts.size());
            }
        });
    }

    public static void queryPersonalBeachPostsOffline(Context context, List<BasePost> allPosts,
                                              PostAdapter adapter) {
        allPosts.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Loading in posts from DB...");
                String userId = ParseUser.getCurrentUser().getObjectId();
                List<RoomShortPostWithObjects> shortPostsDB = ROOM_SHORT_POST_DAO.personalPosts(userId);
                List<RoomShortPost> roomPosts = RoomShortPostWithObjects.getRoomShortPostList(shortPostsDB);

                for(RoomShortPost roomPost : roomPosts) {
                    ShortPost post = new ShortPost(roomPost, ParseUser.getCurrentUser());
                    allPosts.add(post);
                }

                // UI elements must run on the thread they were created on
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeChanged(0, allPosts.size());
                    }
                });
            }
        });
    }

    public static void queryBeachesForSpinner(Context context, FragmentTransaction fm,
                                              Spinner spinnerBeach, View view) {
        if (InternetUtil.isInternetConnected()) {
            queryBeachesForSpinnerOnline(context, fm, spinnerBeach, view);
        }
        else {
            queryBeachesforSpinnerOffline(spinnerBeach, view);
        }
    }
    
    public static void queryBeachesForSpinnerOnline(Context context, FragmentTransaction fm,
                                              Spinner spinnerBeach, View view) {
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
                    Log.e(TAG, "Query beach groups for spinner error", e);
                    return;
                }
                BeachGroup.pinAllInBackground(groups);
                SpinnerAdapter beachAdapter = new SpinnerAdapter
                        (view.getContext(), R.layout.activity_custom_spinner, groups);
                spinnerBeach.setAdapter(beachAdapter);

                // Popup to prompt user to favorite some beaches
                if (spinnerBeach.getAdapter().isEmpty()) {
                    String popupText = context.getResources().getString(R.string.no_beaches_popup);
                    PopupDialogFragment popupDialogFragment =
                            PopupDialogFragment.newInstance(popupText);
                    popupDialogFragment.show(fm, "weather_fragment");
                }
            }
        });
    }

    public static void queryBeachesforSpinnerOffline(Spinner spinnerBeach, View view) {
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .fromLocalDatastore();

        query.findInBackground(new FindCallback<BeachGroup>() {
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error loading in BeachGroups from local storage");
                }
                SpinnerAdapter beachAdapter = new SpinnerAdapter
                        (view.getContext(), R.layout.activity_custom_spinner, groups);
                spinnerBeach.setAdapter(beachAdapter);
            }
        });
    }

    public static void queryGroupsforGroupsAdapter(BaseGroup group, Button favoriteButton,
                                                   Button favoriteButtonPressed) {
        if (group instanceof BeachGroup) {
            queryBeachesforGroups((BeachGroup) group, favoriteButton, favoriteButtonPressed);
        }
        else {
            queryGroupsforGroups((Group) group, favoriteButton, favoriteButtonPressed);
        }
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
                if (e != null) {
                    Log.e(TAG, "Query groups error", e);
                    return;
                }
                for (BeachGroup group : groups) {
                    String queryBeachName = group.getKeyGroupName();
                    String currentBeachName = beach.getKeyGroupName();
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
                    if(queryGroupName.equals(currentGroupName)) {
                        favoriteButtonPressed.setVisibility(View.VISIBLE);
                        favoriteButton.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public static void queryGroups(List<BaseGroup> allGroups, GroupAdapter groupAdapter) {
        if (InternetUtil.isInternetConnected()) {
            queryGroupsOnline(allGroups, groupAdapter);
        } else {
            queryGroupsOffline(allGroups, groupAdapter);
        }
    }

    public static void queryGroupsOnline(List<BaseGroup> allGroups, GroupAdapter groupAdapter) {
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                .include(BeachGroup.KEY_GROUP);
        ParseQuery<Group> groupQuery = ParseQuery.getQuery(Group.class)
                .whereDoesNotMatchKeyInQuery(BeachGroup.KEY_GROUP, Group.KEY_GROUP, beachQuery);

        groupQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query groups error", e);
                    return;
                }
                Group.pinAllInBackground(groups);
                allGroups.addAll(groups);
                groupAdapter.notifyItemRangeChanged(0, allGroups.size());
            }
        });
    }

    public static void queryGroupsOffline(List<BaseGroup> allGroups, GroupAdapter groupAdapter) {
        ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                .fromLocalDatastore();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class)
                .fromLocalDatastore()
                .whereDoesNotMatchKeyInQuery(BeachGroup.KEY_GROUP, Group.KEY_GROUP, beachQuery);

        query.findInBackground(new FindCallback<Group>() {
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error loading in BeachGroups from local storage");
                }
                allGroups.addAll(groups);
                groupAdapter.notifyItemRangeChanged(0, allGroups.size());
            }
        });
    }

    public static void queryBeaches(List<BaseGroup> allBeaches, GroupAdapter adapter) {
        if (InternetUtil.isInternetConnected()) {
            queryBeachesOnline(allBeaches, adapter);
        } else {
            queryBeachesOffline(allBeaches, adapter);
        }
    }

    public static void queryBeachesOnline(List<BaseGroup> allBeaches, GroupAdapter adapter) {
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .include(BeachGroup.KEY_GROUP);

        query.findInBackground(new FindCallback<BeachGroup>() {
            @Override
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query beach group error", e);
                    return;
                }
                BeachGroup.pinAllInBackground(groups);
                allBeaches.addAll(groups);
                adapter.notifyItemRangeChanged(0, allBeaches.size());
            }
        });
    }

    public static void queryBeachesOffline(List<BaseGroup> allBeaches, GroupAdapter adapter) {
        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .fromLocalDatastore();

        query.findInBackground(new FindCallback<BeachGroup>() {
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error loading in BeachGroups from local storage");
                }
                allBeaches.addAll(groups);
                adapter.notifyItemRangeChanged(0, allBeaches.size());
            }
        });
    }

    public static void queryFavorites(Context context, List<BaseGroup> favGroups, GroupAdapter adapter) {
        if (InternetUtil.isInternetConnected()) {
            queryFavoritesOnline(favGroups, adapter);
        }
        else {
            queryFavoritesOffline(context, favGroups, adapter);
        }
    }

    public static void queryFavoritesOnline(List<BaseGroup> favGroups, GroupAdapter adapter) {
        favGroups.clear();

        ParseQuery<FavoriteGroups> favoriteGroupsQuery = ParseQuery.getQuery(FavoriteGroups.class)
                .include(FavoriteGroups.KEY_USER)
                .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
        ParseQuery<Group> allGroupsQuery = ParseQuery.getQuery(Group.class)
                .whereMatchesKeyInQuery(Group.KEY_GROUP, FavoriteGroups.KEY_GROUP, favoriteGroupsQuery);

        allGroupsQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query favorite groups error", e);
                    return;
                }
                favGroups.addAll(groups);
                adapter.notifyItemRangeChanged(0, favGroups.size());

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<RoomFavoriteGroups> roomFavoriteGroups = new ArrayList<>();
                        for (Group group : groups) {
                            RoomFavoriteGroups favoriteGroup = new RoomFavoriteGroups
                                    (group, ParseUser.getCurrentUser());
                            roomFavoriteGroups.add(favoriteGroup);
                        }
                        ROOM_FAVORITE_GROUPS_DAO.insertModel(roomFavoriteGroups.toArray
                                (new RoomFavoriteGroups[0]));
                    }
                });
            }
        });
    }

    public static void queryFavoritesOffline(Context context, List<BaseGroup> favGroups, GroupAdapter adapter) {
        favGroups.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String userId = ParseUser.getCurrentUser().getObjectId();
                List<RoomFavoriteGroups> favoriteGroups = ROOM_FAVORITE_GROUPS_DAO.favoriteGroups(userId);

                for(RoomFavoriteGroups favoriteGroup : favoriteGroups) {
                    Group group = Group.createWithoutData(Group.class, favoriteGroup.groupId);

                    group.fetchFromLocalDatastoreInBackground(new GetCallback<Group>() {
                        public void done(Group group, ParseException e) {
                            if (e == null) {
                                favGroups.add(group);
                            } else {
                                Log.e(TAG, "Error loading in favorited groups offline");
                            }
                            // UI elements must run on the thread they were created on
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemChanged(favGroups.size() - 1);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
