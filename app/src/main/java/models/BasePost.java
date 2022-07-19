package models;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

public interface BasePost {
    String getKeyContent();

    ParseFile getKeyImage();

    String getKeyImageUrl();

    Date getCreatedAt();

    ParseUser getKeyUser();

    Group getKeyGroup();

    Date getCreatedAtOffline();

    String getDisplayCreationTime();
}
