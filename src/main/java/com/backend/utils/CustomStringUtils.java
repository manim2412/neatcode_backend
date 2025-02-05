package com.backend.utils;

public class CustomStringUtils {
    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }
}
