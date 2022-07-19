package models;

import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

public interface BasePost {
    String getKeyContent();

    Date getCreatedAt();

    ParseFile getKeyImage();

    ParseUser getKeyUser();

    Date getCreatedAtOffline();

    String getKeyImageUrl();

    Group getKeyGroup();
}
