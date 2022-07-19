package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomFavoriteGroupsDao {
    @Query("SELECT * FROM RoomFavoriteGroups WHERE RoomFavoriteGroups.userId = :userId")
    List<RoomFavoriteGroups> favoriteGroups(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomFavoriteGroups... groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(RoomFavoriteGroups group);

    @Delete
    public void delete(RoomFavoriteGroups group);
}
