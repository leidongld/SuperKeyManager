package com.example.leidong.superkeymanager.utils;

import java.security.MessageDigest;

/**
 * Created by leidong on 2017/1/14.
 * 数据库中存储主密码的摘要，登录时匹配摘要是否相等即可
 * 主密码不直接出现在数据库
 */

public class SHAUtils {
    private static final String DIGEST_ALGORITHM_NAME = "SHA-512";

    public static String shaDigest(String inputStr) throws Exception{
        byte[] inputByte = inputStr.getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM_NAME);
        byte[] resultByte = messageDigest.digest(inputByte);
        String resultStr = hex(resultByte);
        return resultStr;
    }

    // 返回十六进制字符串
    private static String hex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,
                    3));
        }
        return sb.toString();
    }
}