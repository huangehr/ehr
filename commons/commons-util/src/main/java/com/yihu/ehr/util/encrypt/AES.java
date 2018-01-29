package com.yihu.ehr.util.encrypt;



import com.yihu.ehr.util.datetime.DateUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @created Air 2015/6/2.
 */
public class AES {

    public static String encrypt(String encData ,String secretKey, String vector) throws Exception {
        if(secretKey == null) {
            return null;
        }
        if(secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        return Base64.encode(cipher.doFinal(encData.getBytes("utf-8")));// 此处使用BASE64做转码。
    }

    public static String decrypt(String sSrc, String key, String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 生成长度为16倍数的DES密钥，不足则末位补0
     *  格式：conten+YYYYMMDD时间+ 补位符0
     * @param content 密钥
     * @return 长度为16倍数的密钥
     */
    public static String genAesPass(String content) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_CHAR_DATE_YMD_FORMAT);
        StringBuffer passWordBuffer = new StringBuffer();
        passWordBuffer = passWordBuffer.append(content).append(sdf.format(new Date()));
        Integer remainder = passWordBuffer.length() % 16;
        if (remainder != 0) {
            for (int i = 0; i < 16-remainder; i++) {
                passWordBuffer.append(0);
            }
        }
        return MD5.hash(passWordBuffer.toString()).toUpperCase();
    }

    public static String genKey(String content) throws Exception {
        return genAesPass(content).substring(0, 16).toUpperCase();
    }

    public static String genIV(String content) throws Exception {
        String iv = genAesPass(content);
        return iv.substring(iv.length() - 16, iv.length()).toUpperCase();
    }
}
