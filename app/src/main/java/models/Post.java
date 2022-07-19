package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject implements BasePost {
    public static final String KEY_CONTENT = "textContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "username";
    public static final String KEY_GROUP = "group";
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

    public Number getKeyLikes() { return getNumber(KEY_LIKES); }

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

    public void setKeyLikes(Number likes) { put(KEY_LIKES, likes); }
}
