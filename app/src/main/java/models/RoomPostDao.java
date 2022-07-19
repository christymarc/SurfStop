package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomPostDao {
    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
            "RoomPost.* FROM RoomPost INNER JOIN RoomUser ON RoomUser.id = RoomPost.roomUserId " +
            "ORDER BY RoomPost.createdAt DESC LIMIT 80")
    List<RoomPostWithObjects> currentItems();

    @Query("SELECT RoomUser.id AS user_id, RoomUser.username AS user_username, RoomUser.createdAt as user_createdAt, " +
            "RoomPost.* FROM RoomPost INNER JOIN RoomUser ON RoomUser.id = RoomPost.roomUserId " +
            "WHERE RoomUser.id = :userId ORDER BY RoomPost.createdAt DESC")
    List<RoomPostWithObjects> personalPosts(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomPost... posts);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(RoomUser... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(RoomUser user);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPost(RoomPost post);

    @Delete
    public void deletePost(RoomPost post);
}
