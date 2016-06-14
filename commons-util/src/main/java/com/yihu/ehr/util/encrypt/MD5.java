package com.yihu.ehr.util.encrypt;

import com.yihu.ehr.util.encode.HexEncode;

import java.security.MessageDigest;

/**
 * @created  Air 2015/6/2.
 */
public class MD5 {
    static public String hash(String str) throws Exception {
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(str.getBytes());

        return HexEncode.toHexString(messageDigest.digest());
    }

    // 可逆的加密算法
    public static String encrypt(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String encryptStr = new String(a);
        return encryptStr;
    }

    // 加密后解密
    public static String decrypt(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String decryptStr = new String(a);
        return decryptStr;
    }
}
