package models;

import com.parse.ParseClassName;

@ParseClassName("BeachGroup")
public class BeachGroup extends Group{
    public static final String KEY_MAXBREAK = "maxBreak";
    public static final String KEY_MINBREAK = "minBreak";
    public static final String KEY_WATERTEMP = "waterTemp";
    public static final String KEY_LOCATIONID = "locationID";

    public String getKeyMaxbreak() { return getString(KEY_MAXBREAK); }

    public String getKeyMinbreak() { return getString(KEY_MINBREAK); }

    public String getKeyWatertemp() { return getString(KEY_WATERTEMP); }

    public Number getKeyLocationid() { return getNumber(KEY_LOCATIONID); }

    public void setKeyMaxbreak(String maxBreak) {
        put(KEY_MAXBREAK, maxBreak);
    }

    public void setKeyMinbreak(String minBreak) {
        put(KEY_MINBREAK, minBreak);
    }

    public void setKeyWatertemp(String waterTemp) {
        put(KEY_MAXBREAK, waterTemp);
    }

    public void setKeyLocationid(String locationID) { put(KEY_LOCATIONID, locationID); }

    public static String celsius_to_fahrenheit(String celsius_temp){
        int c_temp = Integer.parseInt(celsius_temp);
        int f_temp = c_temp * 9 / 5 + 32;
        return String.valueOf(f_temp);
    }

    public String kelvin_to_fahrenheit(String kelvin_temp) {
        int c_temp = Integer.parseInt(kelvin_temp) - 273;
        return celsius_to_fahrenheit(String.valueOf(c_temp));
    }
}
