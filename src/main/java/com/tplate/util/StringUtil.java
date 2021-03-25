package com.tplate.util;

public class StringUtil {
    public static String truncate (String txt, String strTruncate) {
        return txt.contains(strTruncate) ? txt.substring(0, txt.indexOf(strTruncate)) : txt;
    }
}
