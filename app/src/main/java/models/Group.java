package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "groupCoverPhoto";
    public static final String KEY_NAME = "groupName";

    public String getKeyDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getKeyName() {
        return getString(KEY_NAME);
    }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setKeyImage(ParseFile groupCoverPhoto) {
        put(KEY_IMAGE, groupCoverPhoto);
    }

    public void setKeyName(String groupName) {
        put(KEY_NAME, groupName);
    }
}
