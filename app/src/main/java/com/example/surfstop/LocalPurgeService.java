package com.example.surfstop;

import static utils.QueryUtils.ROOM_SHORT_POST_DAO;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import models.RoomShortPost;
import models.RoomShortPostWithObjects;
import utils.TimeUtils;

public class LocalPurgeService extends IntentService {
    public static final String TAG = LocalPurgeService.class.getSimpleName();

    public LocalPurgeService(){
        super("localPurgeService");
    }

    @Override
    protected  void onHandleIntent(Intent intent){
        Log.i(TAG, "service running");
        List<RoomShortPostWithObjects> shortPostsDB = ROOM_SHORT_POST_DAO.currentItems();
        List<RoomShortPost> roomPosts = RoomShortPostWithObjects.getRoomShortPostList(shortPostsDB);
        for(RoomShortPost roomPost : roomPosts) {
            // Check how old post is for local DB auto-purge
            long postCreatedAt = roomPost.createdAt.getTime();
            long currentTime = System.currentTimeMillis();
            double hourDifference = (currentTime - postCreatedAt) / (double) TimeUtils.HOUR_MILLIS;
            if (hourDifference >= 12) {
                ROOM_SHORT_POST_DAO.deleteShortPost(roomPost);
            }
        }
    }
}
