package com.nageoffer.shortlink.admin.util;
import java.util.Random;

/**
 * 6位gid生成工具类
 * @author 20784
 */
public final class CodeGenerator {

    // 定义字符池：包含 0-9 数字和大小写字母
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 生成一个 6 位的随机码gid（包含数字和英文字母）
     */
    public static String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());
            code.append(CHAR_POOL.charAt(index));
        }
        return code.toString();
    }

    // 测试方法
    public static void main(String[] args) {
        System.out.println("随机码: " + generateRandomCode());
    }
}

