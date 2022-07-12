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
//    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
//            "RoomShortPost.* FROM RoomShortPost INNER JOIN RoomUser ON RoomShortPost.roomUserId = RoomUser.id " +
//            "ORDER BY RoomShortPost.createdAt DESC LIMIT 5")
    @Query("SELECT id, username, createdAt FROM RoomUser")
    List<RoomUser> currentItems();

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    public void insertShortPost(RoomShortPost post);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(RoomUser user);

    @Delete
    public void deleteShortPost(RoomShortPost post);
}
