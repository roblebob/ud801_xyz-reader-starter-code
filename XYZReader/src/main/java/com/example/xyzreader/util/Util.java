package com.example.xyzreader.util;

import java.util.ArrayList;
import java.util.Arrays;

public final class Util {
    private Util() {}


    public static ArrayList<String> processArticleBody(String string) {

        ArrayList<String> body = new ArrayList<>( Arrays.asList( string.split("\r\n\r\n")));
        body.forEach(s -> s = s.replace("\r\n", " "));
        return body;
    }





    public static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }
}
