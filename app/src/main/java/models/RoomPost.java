package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseUser;

import java.util.Date;

import utils.TimeUtils;

@Entity(foreignKeys = {
        @ForeignKey(entity=RoomUser.class, parentColumns="postObjectId", childColumns="roomUserId"),
        @ForeignKey(entity=RoomGroup.class, parentColumns="postObjectId", childColumns="roomGroupId")
})
public class RoomPost {
    @Ignore
    public Post post;

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String postObjectId;

    @Ignore
    public ParseUser user;
    @Ignore
    public RoomUser roomUser;
    @ColumnInfo
    public String roomUserId;

    @ColumnInfo
    public String content;

    @Ignore
    public Date date;
    @ColumnInfo
    public String createdAt;

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
        this.postObjectId = post.getObjectId();
        this.user = post.getKeyUser();
        this.roomUser = new RoomUser(user);
        this.roomUserId = roomUser.id;
        this.content = post.getKeyContent();
        this.date = post.getCreatedAt();
        this.createdAt = TimeUtils.calculateTimeAgo(date);
        this.imageUrl = post.getKeyImage().getUrl();
        this.group = (Group) post.getKeyGroup();
        this.roomGroup = new RoomGroup(group);
        this.roomGroupId = roomGroup.groupObjectId;
    }
}
