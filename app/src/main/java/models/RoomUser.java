package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseUser;

import java.util.Date;

import utils.TimeUtils;

@Entity
public class RoomUser {
    @Ignore
    ParseUser user;

    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo
    public String username;

    @Ignore
    public Date date;
    @ColumnInfo
    public String createdAt;

    public RoomUser() { }

    public RoomUser(ParseUser parseUser) {
        this.user = parseUser;
        this.id = user.getObjectId();
        this.username = user.getUsername();
        this.date = user.getCreatedAt();
        this.createdAt = TimeUtils.calculateTimeAgo(date);
    }
}
