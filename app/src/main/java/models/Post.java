package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

import utils.InternetUtil;
import utils.TimeUtils;

@ParseClassName("Post")
@Parcel(analyze = Post.class)
public class Post extends ParseObject implements BasePost {
    public static final String KEY_CONTENT = "textContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "username";
    public static final String KEY_GROUP = "group";
    public Date createdAt;
    public String imageUrl;

    public Post() {}

    public Post(RoomPost post, ParseUser user) {
        this.setKeyContent(post.content);
        this.setKeyUser(user);
        this.setKeyCreatedAt(post.createdAt);
        if(post.imageUrl != null) {
            this.setKeyImageUrl(post.imageUrl);
        }
    }

    @Override
    public String getKeyContent() { return getString(KEY_CONTENT); }

    @Override
    public ParseFile getKeyImage() {
        ParseFile image = getParseFile(KEY_IMAGE);
        if (image != null) {
            this.imageUrl = image.getUrl();
        }
        return image;
    }

    @Override
    public String getKeyImageUrl() {
        if (getKeyImage() != null) {
            return getKeyImage().getUrl();
        }
        return this.imageUrl;
    }

    @Override
    public ParseUser getKeyUser() { return getParseUser(KEY_USER); }

    @Override
    public Group getKeyGroup() { return (Group) getParseObject(KEY_GROUP); }

    @Override
    public Date getCreatedAtOffline() { return createdAt; }

    public String getDisplayCreationTime() {
        if (InternetUtil.isInternetConnected()) {
            return TimeUtils.calculateTimeAgo(getCreatedAt());
        } else {
            return TimeUtils.calculateTimeAgo(getCreatedAtOffline());
        }
    }

    public void setKeyContent(String content) { put(KEY_CONTENT, content); }

    public void setKeyImage(ParseFile image) { put(KEY_IMAGE, image); }

    public void setKeyImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setKeyUser(ParseUser user) { put(KEY_USER, user); }

    public void setKeyGroup(Group group) { put(KEY_GROUP, group); }

    public void setKeyCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}