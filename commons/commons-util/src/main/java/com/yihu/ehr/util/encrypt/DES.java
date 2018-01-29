package com.yihu.ehr.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @created Air 2015/6/2.
 */
public class DES {

    public static String encrypt(String data, String passWord) throws Exception {
        DESKeySpec desKey = new DESKeySpec(passWord.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return HexEncode.toHexString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String data, String passWord) throws Exception {
        DESKeySpec desKey = new DESKeySpec(passWord.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(HexEncode.toBytes(data)));
    }
}
