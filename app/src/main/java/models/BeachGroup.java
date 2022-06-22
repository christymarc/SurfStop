package models;

import com.parse.ParseClassName;

@ParseClassName("BeachGroup")
public class BeachGroup extends Group{
    public static final String KEY_MAXBREAK = "maxBreak";
    public static final String KEY_MINBREAK = "minBreak";
    public static final String KEY_WATERTEMP = "waterTemp";

    public String getKeyMaxbreak() { return getString(KEY_MAXBREAK); }

    public String getKeyMinbreak() { return getString(KEY_MINBREAK); }

    public String getKeyWatertemp() { return getString(KEY_WATERTEMP); }

    public void setKeyMaxbreak(String maxBreak) {
        put(KEY_MAXBREAK, maxBreak);
    }

    public void setKeyMinbreak(String minBreak) {
        put(KEY_MINBREAK, minBreak);
    }

    public void setKeyWatertemp(String waterTemp) {
        put(KEY_MAXBREAK, waterTemp);
    }
}
