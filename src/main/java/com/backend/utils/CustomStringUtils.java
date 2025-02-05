package com.backend.utils;

public class CustomStringUtils {
    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isBlank(String str) {
        return str.isBlank();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
