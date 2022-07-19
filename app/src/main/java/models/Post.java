package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("Post")
@Parcel(analyze = Post.class)
public class Post extends ParseObject implements BasePost {
    public static final String KEY_CONTENT = "textContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "username";
    public static final String KEY_GROUP = "group";
    public Date createdAt;
    public String imageUrl;

    public String getKeyContent() { return getString(KEY_CONTENT); }

    public ParseFile getKeyImage() {
        ParseFile image = getParseFile(KEY_IMAGE);
        if (image != null) {
            this.imageUrl = image.getUrl();
        }
        return image;
    }

    public String getKeyImageUrl() { return this.imageUrl; }

    public ParseUser getKeyUser() { return getParseUser(KEY_USER); }

    public Group getKeyGroup() { return (Group) getParseObject(KEY_GROUP); }

    public Date getCreatedAtOffline() { return createdAt; }

    public void setKeyContent(String content) { put(KEY_CONTENT, content); }

    public void setKeyImage(ParseFile image) { put(KEY_IMAGE, image); }

    public void setKeyImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setKeyUser(ParseUser user) { put(KEY_USER, user); }

    public void setKeyGroup(Group group) { put(KEY_GROUP, group); }

    public void setKeyCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}