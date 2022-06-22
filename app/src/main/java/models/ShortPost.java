package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("ShortPost")
public class ShortPost extends Post {
    public static final String KEY_SURFHEIGHT = "surfHeight";
    public static final String KEY_TAG = "tag";
    
    public String getKeySurfHeight() {
        return getString(KEY_SURFHEIGHT);
    }

    public String getKeyTag() {
        return getString(KEY_TAG);
    }

    public void setKeySurfHeight(String surf_height) {
        put(KEY_SURFHEIGHT, surf_height);
    }

    public void setKeyTag(String tag) {
        put(KEY_TAG, tag);
    }

}
