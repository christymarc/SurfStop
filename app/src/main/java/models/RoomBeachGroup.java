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

    @Ignore
    public Group group;
    @ColumnInfo
    public String groupId;

    @ColumnInfo
    public String description;
    @Ignore
    public ParseFile image;
    @ColumnInfo
    public String imageUrl;
    @ColumnInfo
    public String groupName;

    @ColumnInfo
    public String maxBreak;
    @ColumnInfo
    public String minBreak;
    @ColumnInfo
    public String waterTemp;
    @ColumnInfo
    public String locationId;

    public RoomBeachGroup() { }

    @Ignore
    public RoomBeachGroup(BeachGroup beachGroup) {
        this.beachGroup = beachGroup;
        this.beachGroupObjectId = beachGroup.getObjectId();
//        this.group = beachGroup.getKeyGroup();
//        this.groupId = group.getObjectId();
//        this.description = group.getKeyDescription();
//        this.image = group.getKeyImage();
//        this.imageUrl = image.getUrl();
//        this.groupName = group.getKeyGroupName();
//        this.maxBreak = beachGroup.getKeyMaxbreak();
//        this.minBreak = beachGroup.getKeyMinbreak();
//        this.waterTemp = beachGroup.getKeyWatertemp();
//        this.locationId = beachGroup.getKeyLocationid();
    }
}
