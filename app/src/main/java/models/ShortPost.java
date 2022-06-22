package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("ShortPost")
public class ShortPost extends ParseObject {
    public static final String KEY_CONTENT = "textContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "username";
    public static final String KEY_GROUP = "group";
    public static final String KEY_SURFHEIGHT = "surfHeight";
    public static final String KEY_TAG = "tag";
    public static final String KEY_LIKES = "likesCount";

    public String getKeyContent() {
        return getString(KEY_CONTENT);
    }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getKeyGroup() {
        return getParseObject(KEY_GROUP);
    }
    
    public String getKeySurfHeight() {
        return getString(KEY_SURFHEIGHT);
    }

    public String getKeyTag() {
        return getString(KEY_TAG);
    }

    public Number getKeyLikes() {
        return getNumber(KEY_LIKES);
    }

    public void setKeyContent(String content) {
        put(KEY_CONTENT, content);
    }

    public void setKeyImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setKeyGroup(ParseObject group) {
        put(KEY_GROUP, group);
    }

    public void setKeySurfHeight(String surf_height) {
        put(KEY_SURFHEIGHT, surf_height);
    }

    public void setKeyTag(String tag) {
        put(KEY_TAG, tag);
    }

    public void setKeyLikes(Number likes) {
        put(KEY_TAG, likes);
    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
