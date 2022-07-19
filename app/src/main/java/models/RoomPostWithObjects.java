package models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class RoomPostWithObjects {
    @Embedded(prefix="user_")
    RoomUser roomUser;

    @Embedded
    RoomPost roomPost;

    public static List<RoomPost> getRoomPostList(List<RoomPostWithObjects> postsWithObjects, Group group) {
        List<RoomPost> posts = new ArrayList<>();
        for (int i = 0; i < postsWithObjects.size(); i++) {
            RoomPost post = postsWithObjects.get(i).roomPost;
            post.roomUser = postsWithObjects.get(i).roomUser;
            if (post.roomGroupId.equals(group.getObjectId())) {
                posts.add(post);
            }
        }
        return posts;
    }

    public static List<RoomUser> usersFromRoomPosts(List<RoomPost> posts) {
        List<RoomUser> users = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            RoomUser user = posts.get(i).roomUser;
            users.add(user);
        }
        return users;
    }
}
