package com.example.xyzreader.util;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;



public final class Util {
    private Util() {}


    public static ArrayList<String> processArticleBody(String string) {

        return new ArrayList<>( Arrays.asList( string.split("\r\n\r\n")));
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


}
