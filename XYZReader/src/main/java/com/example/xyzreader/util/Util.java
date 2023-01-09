package com.example.xyzreader.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public final class Util {
    private Util() {}


    public static ArrayList<String> processArticleBody(String string) {

        ArrayList<String> body = new ArrayList<>( Arrays.asList( string.split("\r\n\r\n")));
        body.forEach(s -> s = s.replace("\r\n", " "));
        return body;
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
