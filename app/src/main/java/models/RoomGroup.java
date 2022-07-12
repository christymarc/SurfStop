package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Entity
public class RoomGroup {
    @Ignore
    public Group group;

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String groupObjectId;

    @ColumnInfo
    public String description;
    @Ignore
    public ParseFile image;
    @ColumnInfo
    public String imageUrl;
    @ColumnInfo
    public String groupName;

    public RoomGroup() { }

    @Ignore
    public RoomGroup(Group group) {
        this.group = group;
        this.groupObjectId = group.getObjectId();
        this.description = group.getKeyDescription();
        this.image = group.getKeyImage();
        this.imageUrl = image.getUrl();
        this.groupName = group.getKeyGroupName();
    }
}

