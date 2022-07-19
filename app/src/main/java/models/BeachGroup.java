package models;

import com.parse.ParseClassName;

import org.parceler.Parcel;

import java.io.Serializable;

@ParseClassName("BeachGroup")
@Parcel(analyze = BeachGroup.class)
public class BeachGroup extends Group implements Serializable, BaseGroup {
    public static final String KEY_GROUP = "group";
    public static final String KEY_MAXBREAK = "maxBreak";
    public static final String KEY_MINBREAK = "minBreak";
    public static final String KEY_WATERTEMP = "waterTemp";
    public static final String KEY_LOCATIONID = "locationId";

    public Group getKeyGroup() { return (Group) getParseObject(KEY_GROUP); }

    public String getKeyMaxbreak() { return getString(KEY_MAXBREAK); }

    public String getKeyMinbreak() { return getString(KEY_MINBREAK); }

    public String getKeyWatertemp() { return getString(KEY_WATERTEMP); }

    public String getKeyLocationid() { return getString(KEY_LOCATIONID); }

    public void setKeyGroup(Group group) { put(KEY_GROUP, group); }

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
