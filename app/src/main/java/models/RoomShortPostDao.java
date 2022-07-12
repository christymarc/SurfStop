package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;

import java.util.List;

@Dao
public interface RoomShortPostDao {
    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
            "RoomShortPost.* FROM RoomShortPost INNER JOIN RoomUser ON RoomUser.id = RoomShortPost.roomUserId " +
            "ORDER BY RoomShortPost.createdAt DESC LIMIT 2")
    List<RoomShortPostWithObjects> currentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomShortPost... posts);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomUser... users);

    @Delete
    public void deleteShortPost(RoomShortPost post);
}
