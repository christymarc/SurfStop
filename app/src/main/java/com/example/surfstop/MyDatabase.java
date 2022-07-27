package com.example.surfstop;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import models.RoomBeachGroup;
import models.RoomFavoriteGroups;
import models.RoomFavoriteGroupsDao;
import models.RoomGroup;
import models.RoomPost;
import models.RoomPostDao;
import models.RoomShortPost;
import models.RoomShortPostDao;
import models.RoomUser;

@Database(entities={RoomFavoriteGroups.class, RoomPost.class, RoomShortPost.class,
        RoomUser.class, RoomGroup.class, RoomBeachGroup.class}, version=10)
public abstract class MyDatabase extends RoomDatabase {
    // Declare your data access objects as abstract
    public abstract RoomShortPostDao roomShortPostDao();
    public abstract RoomPostDao roomPostDao();
    public abstract RoomFavoriteGroupsDao roomFavoriteGroupsDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
