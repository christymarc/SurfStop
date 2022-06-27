package utils;

public class TempUtils {

    public static String celsiusToFahrenheit(String celsius_temp){
        double cTemp = Double.parseDouble(celsius_temp);
        int fTemp = (int) cTemp * 9 / 5 + 32;
        return String.valueOf(fTemp);
    }

    public static String kelvin_to_fahrenheit(String kelvinTemp) {
        double cTemp = Double.parseDouble(kelvinTemp) - 273.15;
        return celsiusToFahrenheit(String.valueOf(cTemp));
    }
}
