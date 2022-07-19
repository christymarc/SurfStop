package com.example.surfstop;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import models.RoomBeachGroup;
import models.RoomGroup;
import models.RoomPost;
import models.RoomPostDao;
import models.RoomShortPost;
import models.RoomShortPostDao;
import models.RoomUser;

@Database(entities={RoomPost.class, RoomShortPost.class, RoomUser.class, RoomGroup.class, RoomBeachGroup.class}, version=7)
public abstract class MyDatabase extends RoomDatabase {
    // Declare your data access objects as abstract
    public abstract RoomShortPostDao roomShortPostDao();

    public abstract RoomPostDao roomPostDao();
    // Database name to be used
    public static final String NAME = "MyDataBase";
}
