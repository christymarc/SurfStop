package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.io.Serializable;

@ParseClassName("Group")
@Parcel(analyze = Group.class)
public class Group extends ParseObject implements Serializable, BaseGroup {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "groupCoverPhoto";
    public static final String KEY_GROUPNAME = "groupName";
    public static final String KEY_GROUP = "group";

    public String getKeyDescription() { return getString(KEY_DESCRIPTION); }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getKeyGroupName() { return getString(KEY_GROUPNAME); }

    public Group getKeyGroup() { return (Group) getParseObject(KEY_GROUP); }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setKeyImage(ParseFile groupCoverPhoto) {
        put(KEY_IMAGE, groupCoverPhoto);
    }

    public void setKeyGroupName(String groupName) {
        put(KEY_GROUPNAME, groupName);
    }

    public void setKeyGroup(Group group) {
        put(KEY_GROUP, group);
    }
}
