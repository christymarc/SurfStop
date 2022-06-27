package models;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

public interface BasePost {
    String getKeyContent();

    Date getCreatedAt();

    ParseFile getKeyImage();

    ParseUser getKeyUser();
}