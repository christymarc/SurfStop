package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class RoomGroup {
    @Ignore
    public Group group;

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String groupObjectId;

    public RoomGroup() { }

    @Ignore
    public RoomGroup(Group group) {
        this.group = group;
        this.groupObjectId = group.getObjectId();
    }
}

