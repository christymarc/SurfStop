package models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.List;

@ParseClassName("BeachGroup")
public class BeachGroup extends Group implements Serializable, BaseGroup {
    private static final String TAG = BeachGroup.class.getSimpleName();

    public static final String KEY_GROUP = "group";
    public static final String KEY_MAXBREAK = "maxBreak";
    public static final String KEY_MINBREAK = "minBreak";
    public static final String KEY_WATERTEMP = "waterTemp";
    public static final String KEY_LOCATIONID = "locationId";

    public ParseObject getKeyGroup() { return getParseObject(KEY_GROUP); }

    public String getKeyMaxbreak() { return getString(KEY_MAXBREAK); }

    public String getKeyMinbreak() { return getString(KEY_MINBREAK); }

    public String getKeyWatertemp() { return getString(KEY_WATERTEMP); }

    public String getKeyLocationid() { return getString(KEY_LOCATIONID); }

    public void setKeyGroup(ParseObject group) { put(KEY_GROUP, group); }

    public void setKeyMaxbreak(String maxBreak) {
        put(KEY_MAXBREAK, maxBreak);
    }

    public void setKeyMinbreak(String minBreak) {
        put(KEY_MINBREAK, minBreak);
    }

    public void setKeyWatertemp(String waterTemp) {
        put(KEY_MAXBREAK, waterTemp);
    }

    public void setKeyLocationid(String locationID) { put(KEY_LOCATIONID, locationID); }

    public static void addBeachGroup(BeachGroup beachGroup) {
        FavoriteGroups favoritedGroup = new FavoriteGroups();
        favoritedGroup.setKeyGroup(beachGroup.getKeyGroup());
        favoritedGroup.setKeyUser(ParseUser.getCurrentUser());
        favoritedGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while trying to save favorited BeachGroup", e);
                    return;
                }
                Log.i(TAG, "Favorited BeachGroup successfully saved");
            }
        });
    }

    public static void deleteBeachGroup(BeachGroup beachGroup) {
        ParseQuery<FavoriteGroups> query = ParseQuery.getQuery(FavoriteGroups.class)
                .whereEqualTo(FavoriteGroups.KEY_GROUP, beachGroup.getKeyGroup());
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
    }
}
