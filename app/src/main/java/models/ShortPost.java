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
    public Date createdAt;
    public String imageUrl;

    public ShortPost() { }

    public ShortPost(RoomShortPost post, ParseUser user) {
        this.setKeyContent(post.content);
        this.setKeyUser(user);
        this.createdAt = post.createdAt;
        this.setKeyCreatedAt(post.createdAt);
        this.setKeyTag(post.tag);
        this.setKeySurfHeight(post.surfHeight);
        this.imageUrl = post.imageUrl;
        if(post.imageUrl != null) {
            this.setKeyImageUrl(post.imageUrl);
        }
    }

    public String getKeySurfHeight() { return getString(KEY_SURFHEIGHT); }

    public String getKeyTag() { return getString(KEY_TAG); }

    public BeachGroup getKeyBeachGroup() { return (BeachGroup) getParseObject(KEY_BEACHGROUP); }

    public void setKeySurfHeight(String surf_height) { put(KEY_SURFHEIGHT, surf_height); }

    public void setKeyTag(String tag) { put(KEY_TAG, tag); }

    public void setKeyBeachGroup(BeachGroup beachGroup) { put(KEY_BEACHGROUP, beachGroup); }
}