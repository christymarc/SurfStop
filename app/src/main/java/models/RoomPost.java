package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

import utils.DateConverter;
import utils.InternetUtil;
import utils.TimeUtils;

@Entity(foreignKeys = { @ForeignKey(entity=RoomUser.class, parentColumns="id", childColumns="roomUserId") })
@TypeConverters(DateConverter.class)
public class RoomPost {
    @Ignore
    public Post post;

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String id;

    @Ignore
    public ParseUser user;
    @Ignore
    public RoomUser roomUser;
    @ColumnInfo
    public String roomUserId;

    @ColumnInfo
    public String content;

    @ColumnInfo
    public Date createdAt;

    @Ignore
    public ParseFile image;
    @ColumnInfo
    public String imageUrl;

    @Ignore
    public Group group;
    @Ignore
    public RoomGroup roomGroup;
    @ColumnInfo
    public String roomGroupId;

    public RoomPost() { }

    @Ignore
    public RoomPost(Post post) {
        this.post = post;
        this.id = post.getObjectId();
        this.user = post.getKeyUser();
        this.roomUser = new RoomUser(user);
        this.roomUserId = roomUser.id;
        this.content = post.getKeyContent();
        if (InternetUtil.isInternetConnected()) {
            this.createdAt = post.getCreatedAt();
        } else {
            this.createdAt = post.getCreatedAtOffline();
        }
        this.image = post.getKeyImage();
        if (image != null) {
            this.imageUrl = image.getUrl();
        }
        else {
            this.imageUrl = null;
        }
        this.group = post.getKeyGroup();
        this.roomGroup = new RoomGroup(group);
        this.roomGroupId = roomGroup.groupObjectId;
    }
}
