package utils;

public class TempUtils {

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
