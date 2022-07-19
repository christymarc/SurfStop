package models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseUser;

@Entity
public class RoomFavoriteGroups {

    @Ignore
    public Group group;
    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String groupId;

    @Ignore
    public ParseUser user;
    @ColumnInfo
    public String userId;

    public RoomFavoriteGroups() { }

    public RoomFavoriteGroups(Group group, ParseUser user) {
        this.group = group;
        this.groupId = group.getObjectId();
        this.user = user;
        this.userId = user.getObjectId();
    }
}
