package com.oyp.yy.util;

/**
 * <p>date: Created by A18086 on 2019/10/18.</p>
 * <p>desc: </p>
 */
public class StringUtil {

    private StringUtil() {
        throw new RuntimeException("don`t do this");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

}
