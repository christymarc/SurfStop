package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomShortPostDao {
    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
            "RoomShortPost.* FROM RoomShortPost INNER JOIN RoomUser ON RoomUser.id = RoomShortPost.roomUserId " +
            "ORDER BY RoomShortPost.createdAt DESC LIMIT 80")
    List<RoomShortPostWithObjects> currentItems();

    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
            "RoomShortPost.* FROM RoomShortPost INNER JOIN RoomUser ON RoomUser.id = RoomShortPost.roomUserId " +
            "WHERE RoomUser.id = :userId ORDER BY RoomShortPost.createdAt DESC")
    List<RoomShortPostWithObjects> personalPosts(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomShortPost... posts);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomUser... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(RoomUser user);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertShortPost(RoomShortPost post);

    @Delete
    public void deleteShortPost(RoomShortPost post);
}
