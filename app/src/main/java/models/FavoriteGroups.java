package models;

import static utils.QueryUtils.ROOM_FAVORITE_GROUPS_DAO;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("FavoriteGroups")
public class FavoriteGroups extends ParseObject{
    public static final String TAG = FavoriteGroups.class.getSimpleName();

    public static final String KEY_USER = "user";
    public static final String KEY_GROUP = "group";

    public ParseUser getKeyUser() { return getParseUser(KEY_USER); }

    public Group getKeyGroup() { return (Group) getParseObject(KEY_GROUP); }

    public void setKeyUser(ParseUser user) { put(KEY_USER, user); }

    public void setKeyGroup(ParseObject group) { put(KEY_GROUP, group); }

    public static void addFavoriteGroup(BaseGroup group) {
        FavoriteGroups favoritedGroup = new FavoriteGroups();
        if (group instanceof BeachGroup) {
            BeachGroup beachGroup = (BeachGroup) group;
            favoritedGroup.setKeyGroup(beachGroup.getKeyGroup());
        }
        else {
            Group otherGroup = (Group) group;
            favoritedGroup.setKeyGroup(otherGroup);
        }
        favoritedGroup.setKeyUser(ParseUser.getCurrentUser());

        // Save to Parse DB
        favoritedGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while trying to save favorited BeachGroup", e);
                    return;
                }
            }
        });

        // Save to Room DB
        AsyncTask.execute(new Runnable() {
            RoomFavoriteGroups roomFavoriteGroup =
                    new RoomFavoriteGroups(favoritedGroup.getKeyGroup(), ParseUser.getCurrentUser());
            @Override
            public void run() {
                ROOM_FAVORITE_GROUPS_DAO.insertGroup(roomFavoriteGroup);
            }
        });
    }

    public static void deleteFavoriteGroup(BaseGroup group) {
        ParseQuery<FavoriteGroups> query = ParseQuery.getQuery(FavoriteGroups.class);
        if (group instanceof BeachGroup) {
            BeachGroup beachGroup = (BeachGroup) group;
            query.whereEqualTo(FavoriteGroups.KEY_GROUP, beachGroup.getKeyGroup());
            beachGroup.unpinInBackground();
        }
        else {
            Group otherGroup = (Group) group;
            query.whereEqualTo(FavoriteGroups.KEY_GROUP, otherGroup.getKeyGroup());
        }

        // Delete from Parse DB
        query.findInBackground(new FindCallback<FavoriteGroups>() {
            @Override
            public void done(List<FavoriteGroups> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while trying to delete favorited BeachGroup", e);
                    return;
                }
                for (ParseObject object : objects) {
                    object.deleteInBackground();
                }
            }
        });

        // Delete from Room DB
        AsyncTask.execute(new Runnable() {
            RoomFavoriteGroups roomFavoriteGroup =
                    new RoomFavoriteGroups(group.getKeyGroup(), ParseUser.getCurrentUser());
            @Override
            public void run() {
                ROOM_FAVORITE_GROUPS_DAO.delete(roomFavoriteGroup);
            }
        });
    }
}
