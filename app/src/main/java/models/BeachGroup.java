package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("BeachGroup")
public class BeachGroup extends Group{
    public static final String KEY_GROUP = "group";
    public static final String KEY_MAXBREAK = "maxBreak";
    public static final String KEY_MINBREAK = "minBreak";
    public static final String KEY_WATERTEMP = "waterTemp";
    public static final String KEY_LOCATIONID = "locationID";

    public ParseObject getKeyGroup() { return getParseObject(KEY_GROUP); }

    public String getKeyMaxbreak() { return getString(KEY_MAXBREAK); }

    public String getKeyMinbreak() { return getString(KEY_MINBREAK); }

    public String getKeyWatertemp() { return getString(KEY_WATERTEMP); }

    public Number getKeyLocationid() { return getNumber(KEY_LOCATIONID); }

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
}
