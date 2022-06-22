package models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("FavoriteGroups")
public class FavoriteGroups extends ParseObject {
    public static final String KEY_USER = "username";
    public static final String KEY_GROUP = "group";

    public ParseUser getKeyUser() { return getParseUser(KEY_USER); }

    public ParseObject getKeyGroup() { return getParseObject(KEY_GROUP); }

    public void setKeyUser(ParseUser username) { put(KEY_USER, username); }

    public void setKeyGroup(ParseObject group) { put(KEY_GROUP, group); }
}
