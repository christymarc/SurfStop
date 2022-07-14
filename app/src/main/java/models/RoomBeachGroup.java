package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.parse.ParseFile;

@Entity
public class RoomBeachGroup {
    @Ignore
    public BeachGroup beachGroup;

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String beachGroupObjectId;

    public RoomBeachGroup() { }

    @Ignore
    public RoomBeachGroup(BeachGroup beachGroup) {
        this.beachGroup = beachGroup;
        this.beachGroupObjectId = beachGroup.getObjectId();
    }
}
