package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.net.URI;
import java.util.Date;

import utils.TimeUtils;

@Entity(foreignKeys = {
        @ForeignKey(entity=RoomUser.class, parentColumns="id", childColumns="roomUserId"),
        @ForeignKey(entity=RoomGroup.class, parentColumns="groupObjectId", childColumns="roomGroupId"),
        @ForeignKey(entity=RoomBeachGroup.class, parentColumns="beachGroupObjectId", childColumns="roomBeachGroupId")
})
public class RoomShortPost {
    @Ignore
    public ShortPost post;

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

    @Ignore
    public Date date;
    @ColumnInfo
    public String createdAt;

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

    @ColumnInfo
    public String surfHeight;

    @ColumnInfo
    public String tag;

    @Ignore
    public BeachGroup beachGroup;
    @Ignore
    public RoomBeachGroup roomBeachGroup;
    @ColumnInfo
    public String roomBeachGroupId;

    public RoomShortPost() { }

    @Ignore
    public RoomShortPost(ShortPost post) throws ParseException {
        this.post = post;
        this.id = post.getObjectId();
        this.user = post.getKeyUser();
        this.roomUser = new RoomUser(user);
        this.roomUserId = roomUser.id;
        this.content = post.getKeyContent();
        this.date = post.getCreatedAt();
        this.createdAt = TimeUtils.calculateTimeAgo(date);
        this.image = post.getKeyImage();
        if (image != null) {
            this.imageUrl = image.getUrl();
        }
        else {
            this.imageUrl = null;
        }
        this.group = post.getKeyGroup();
        //this.roomGroup = new RoomGroup(group);
        //this.roomGroupId = roomGroup.groupObjectId;
        this.surfHeight = post.getKeySurfHeight();
        this.tag = post.getKeyTag();
        this.beachGroup = post.getKeyBeachGroup();
        //this.roomBeachGroup = new RoomBeachGroup(beachGroup);
        //this.roomBeachGroupId = roomBeachGroup.beachGroupObjectId;
    }
}