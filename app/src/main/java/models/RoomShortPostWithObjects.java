package models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class RoomShortPostWithObjects {

    //Embedded flattens the properties of the object into the object, preserving encapsulation
    @Embedded(prefix="user_")
    RoomUser roomUser;

    @Embedded
    RoomShortPost roomShortPost;

    public static List<RoomShortPost> getRoomShortPostList(List<RoomShortPostWithObjects> postsWithObjects, BeachGroup beachGroup) {
        List<RoomShortPost> posts = new ArrayList<>();
        for (int i = 0; i < postsWithObjects.size(); i++) {
            RoomShortPost post = postsWithObjects.get(i).roomShortPost;
            post.roomUser = postsWithObjects.get(i).roomUser;
            if (post.roomBeachGroupId.equals(beachGroup.getObjectId())) {
                posts.add(post);
            }
        }
        return posts;
    }

    public static List<RoomShortPost> getRoomShortPostList(List<RoomShortPostWithObjects> postsWithObjects) {
        List<RoomShortPost> posts = new ArrayList<>();
        for (int i = 0; i < postsWithObjects.size(); i++) {
            RoomShortPost post = postsWithObjects.get(i).roomShortPost;
            post.roomUser = postsWithObjects.get(i).roomUser;
            posts.add(post);
        }
        return posts;
    }

    public static List<RoomUser> usersFromRoomShortPosts(List<RoomShortPost> posts) {
        List<RoomUser> users = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            RoomUser user = posts.get(i).roomUser;
            users.add(user);
        }
        return users;
    }

    //    @Embedded (prefix = "group_")
//    RoomGroup roomGroup;
//
//    @Embedded (prefix = "beach_")
//    RoomBeachGroup roomBeachGroup;
}
