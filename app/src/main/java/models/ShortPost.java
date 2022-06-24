package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("ShortPost")
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
