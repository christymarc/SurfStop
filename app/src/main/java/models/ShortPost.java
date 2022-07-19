package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("ShortPost")
@Parcel(analyze = ShortPost.class)
public class ShortPost extends Post implements BasePost{
    public static final String KEY_SURFHEIGHT = "surfHeight";
    public static final String KEY_TAG = "tag";
    public static final String KEY_BEACHGROUP = "beachGroup";

    public ShortPost() { }

    public ShortPost(RoomShortPost post, ParseUser user) {
        this.setKeyContent(post.content);
        this.setKeyUser(user);
        this.setKeyCreatedAt(post.createdAt);
        //this.setKeyGroup(post.group);
        //this.setKeyBeachGroup(post.beachGroup);
        this.setKeyTag(post.tag);
        this.setKeySurfHeight(post.surfHeight);
        if(post.imageUrl != null) {
            this.setKeyImage(post.image);
        }
    }

    public String getKeySurfHeight() { return getString(KEY_SURFHEIGHT); }

    public String getKeyTag() { return getString(KEY_TAG); }

    public BeachGroup getKeyBeachGroup() { return (BeachGroup) getParseObject(KEY_BEACHGROUP); }

    public void setKeySurfHeight(String surf_height) { put(KEY_SURFHEIGHT, surf_height); }

    public void setKeyTag(String tag) { put(KEY_TAG, tag); }

    public void setKeyBeachGroup(BeachGroup beachGroup) { put(KEY_BEACHGROUP, beachGroup); }
}