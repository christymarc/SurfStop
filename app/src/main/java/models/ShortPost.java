package models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("ShortPost")
@Parcel(analyze = ShortPost.class)
public class ShortPost extends Post implements BasePost{
    public static final String KEY_SURFHEIGHT = "surfHeight";
    public static final String KEY_TAG = "tag";
    public static final String KEY_BEACHGROUP = "beachGroup";
    public static final String KEY_ISIMAGEBEACH = "isImageBeach";
    public static final String KEY_ISBEACHCLEAN = "isBeachClean";
    public Date createdAt;

    public ShortPost() { }

    public ShortPost(RoomShortPost post, ParseUser user) {
        this.setKeyContent(post.content);
        this.setKeyUser(user);
        this.createdAt = post.createdAt;
        this.setKeyCreatedAt(post.createdAt);
        this.setKeyTag(post.tag);
        this.setKeySurfHeight(post.surfHeight);
        if(post.imageUrl != null) {
            this.setKeyImageUrl(post.imageUrl);
        }
        this.setKeyIsBeachClean(post.isBeachClean);
    }

    public String getKeySurfHeight() { return getString(KEY_SURFHEIGHT); }

    public String getKeyTag() { return getString(KEY_TAG); }

    public BeachGroup getKeyBeachGroup() { return (BeachGroup) getParseObject(KEY_BEACHGROUP); }

    public Boolean getKeyIsImageBeach() { return getBoolean(KEY_ISIMAGEBEACH); }

    public Boolean getKeyIsBeachClean() { return getBoolean(KEY_ISBEACHCLEAN); }

    public void setKeySurfHeight(String surf_height) { put(KEY_SURFHEIGHT, surf_height); }

    public void setKeyTag(String tag) { put(KEY_TAG, tag); }

    public void setKeyBeachGroup(BeachGroup beachGroup) { put(KEY_BEACHGROUP, beachGroup); }

    public void setKeyIsImageBeach(Boolean isImageBeach) { put(KEY_ISIMAGEBEACH, isImageBeach); }
    public void setKeyIsBeachClean(Boolean isBeachClean) { put(KEY_ISBEACHCLEAN, isBeachClean); }
}