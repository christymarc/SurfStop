package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("FavoriteGroups")
public class FavoriteGroups extends ParseObject{
    public static final String KEY_USER = "user";
    public static final String KEY_GROUP = "group";

    public ParseUser getKeyUser() { return getParseUser(KEY_USER); }

    public ParseObject getKeyGroup() { return getParseObject(KEY_GROUP); }

    public void setKeyUser(ParseUser user) { put(KEY_USER, user); }

    public void setKeyGroup(ParseObject group) { put(KEY_GROUP, group); }
}
