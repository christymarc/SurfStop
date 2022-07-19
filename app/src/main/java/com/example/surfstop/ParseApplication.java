package com.example.surfstop;

import android.app.Application;

import androidx.room.Room;

import com.parse.Parse;
import com.parse.ParseObject;

import models.BeachGroup;
import models.FavoriteGroups;
import models.Group;
import models.Post;
import models.ShortPost;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    MyDatabase myDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(ShortPost.class);
        ParseObject.registerSubclass(FavoriteGroups.class);
        ParseObject.registerSubclass(BeachGroup.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.APPLICATION_KEY)
                .clientKey(BuildConfig.CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                        .enableLocalDataStore()
                .build());

        myDatabase = Room.databaseBuilder(this, MyDatabase.class, MyDatabase.NAME)
                .fallbackToDestructiveMigration().build();
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }
}
