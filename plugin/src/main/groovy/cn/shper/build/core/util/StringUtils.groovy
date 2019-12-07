package cn.shper.build.core.util

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class StringUtils {

    static boolean isNullOrEmpty(String str) {
        return !str || str.size() < 1
    }

    static boolean isNotNullAndNotEmpty(String str) {
        return !isNullOrEmpty(str)
    }

    static String toUpperCase(String str, int length) {
        char[] chars = str.toCharArray()

        for (int index = 0; index < length; index++) {
            if (chars[index] >= 97 && chars[index] <= 122) {
                chars[index] -= 32
            }
        }
        return String.valueOf(chars)
    }

}