package com.yihu.ehr.util.encode;

import com.yihu.ehr.util.log.LogService;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/7/31.
 */
public class HashUtil {

    /**
     * 为传入的参数生成hash值.
     *
     * @param str
     */
    public static String hashStr(String str) {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            LogService.getLogger(HashUtil.class).error(ex.getMessage());
        }

        return "";
    }
}
