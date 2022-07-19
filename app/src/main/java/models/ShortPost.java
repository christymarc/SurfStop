package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@ParseClassName("ShortPost")
@Parcel(analyze = ShortPost.class)
public class ShortPost extends Post implements BasePost{
    public static final String KEY_SURFHEIGHT = "surfHeight";
    public static final String KEY_TAG = "tag";
    public static final String KEY_BEACHGROUP = "beachGroup";
    
    public String getKeySurfHeight() {
        return getString(KEY_SURFHEIGHT);
    }

    public String getKeyTag() {
        return getString(KEY_TAG);
    }

    public ParseObject getKeyBeachGroup() { return getParseObject(KEY_BEACHGROUP); }

    public void setKeySurfHeight(String surf_height) {
        put(KEY_SURFHEIGHT, surf_height);
    }

    public void setKeyTag(String tag) {
        put(KEY_TAG, tag);
    }

    public void setKeyBeachGroup(ParseObject beachGroup) { put(KEY_BEACHGROUP, beachGroup); }
}
