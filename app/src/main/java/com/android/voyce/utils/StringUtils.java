package com.android.voyce.utils;

public final class StringUtils {

    public static String capitalize(String string) {
        string = string.toLowerCase();

        StringBuilder builder = new StringBuilder();

        String[] strings = string.split(" ");

        for (String s: strings) {
            String capitalized = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(capitalized).append(" ");
        }

        return builder.toString();
    }
}
