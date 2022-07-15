package utils;

import java.util.Random;

public class KeyGeneratorUtil {
    private static final int KEY_LENGTH = 10;

    public static String generateRandomKey() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder stringBuilder = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++)
            stringBuilder.append(characters.charAt(rnd.nextInt(characters.length())));
        return stringBuilder.toString();
    }
}
