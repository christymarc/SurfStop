package models;

import com.parse.ParseFile;

public interface BaseGroup {
    String getKeyGroupName();

    ParseFile getKeyImage();

    Group getKeyGroup();
}
