package com.nageoffer.shortlink.project.util;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * @author 20784
 */
public class HashUtil {

    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final int SIZE = CHARS.length;

    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    public static String hashToBase62(String str) {
        // 计算哈希
        int i = MurmurHash.hash32(str);
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;

        // 转 Base62
        String base62 = convertDecToBase62(num);

        // 固定长度为 6，不足补 '0'（Base62 的第一个字符）
        if (base62.length() < 6) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 6 - base62.length(); j++) {
                sb.append(CHARS[0]);
            }
            sb.append(base62);
            base62 = sb.toString();
        }

        // 超过 6 位直接截取前 6 位
        if (base62.length() > 6) {
            base62 = base62.substring(0, 6);
        }

        return base62;
    }

}

