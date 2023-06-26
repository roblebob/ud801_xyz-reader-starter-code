package com.example.xyzreader.util;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import kotlinx.datetime.*;


public final class Util {
    private Util() {}


    public static ArrayList<String> processArticleBody(String string) {

        ArrayList<String> body = new ArrayList<>( Arrays.asList( string.split("\r\n\r\n")));
        body.forEach(s -> s = s.replace("\r\n", " "));
        return body;
    }



    public static String extractYear(String string) {
        return String.format(
                Locale.getDefault(),
                "%d",
                java.time.LocalDateTime .ofInstant(
                        Instant.parse( string + "Z"),
                        ZoneId.systemDefault()) .getYear()
        );
    }



    public static <T extends Comparable<T>> T constrain(T val, T min, T max) {
        if (val.compareTo(min) < 0) {
            return min;
        } else if (val.compareTo(max) > 0) {
            return max;
        } else {
            return val;
        }
    }
}
