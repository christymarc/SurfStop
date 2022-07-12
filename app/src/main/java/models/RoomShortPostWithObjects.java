package models;

import androidx.room.Embedded;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class RoomShortPostWithObjects {

    //Embedded flattens the properties of the object into the object, preserving encapsulation
    @Embedded
    RoomUser roomUser;

//    @Embedded
//    RoomShortPost roomShortPost;

    public RoomUser getRoomUser() {
        return roomUser;
    }

//    public RoomShortPost getRoomShortPost() {
//        roomShortPost.roomUser = getRoomUser();
//        return roomShortPost;
//    }

    //    @Embedded (prefix = "group_")
//    RoomGroup roomGroup;
//
//    @Embedded (prefix = "beach_")
//    RoomBeachGroup roomBeachGroup;
}
