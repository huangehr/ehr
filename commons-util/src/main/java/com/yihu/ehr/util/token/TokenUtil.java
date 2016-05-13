package com.yihu.ehr.util.token;

import com.yihu.ehr.util.encrypt.DES;
import com.yihu.ehr.util.encrypt.MD5;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token工具。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.31 10:55
 */
public class TokenUtil {
    private final static String TOKEN_ENCRYPT_WORD = "ha_passw0rd";

    /**
     * 生成授权码
     *
     * @param length
     * @return
     */
    public static String genToken(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    /**
     * hash授权码 (按日期生成随机码进行MD5加密)
     *
     * @throws Exception
     */
    public static String hashToken(String token) throws Exception {
        token = MD5.hash(token);

        return token;
    }

    /**
     * 加密授权码：
     * 1.将createDate换算成秒数 + expiresIn = 失效的日期的秒数,
     * 2.秒数 + userId + appId进行组合DES加密。
     *
     * @throws Exception
     */
    public static String tokenEncrypt(String userId, String appId, Date createDate, long expiresIn) throws Exception {

        long seed = createDate.getTime();

        String expiredTime = String.valueOf(seed + (expiresIn * 1000));

        String[] arrTmp = {userId, appId, expiredTime};

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arrTmp.length; i++) {
            if (i == 0) {
                sb.append(arrTmp[i]);
            } else {
                sb.append("," + arrTmp[i]);
            }
        }

        String token = DES.encrypt(sb.toString(), TOKEN_ENCRYPT_WORD);
        return token;
    }

    /**
     * 校验请求的签名是否合法，token校验流程：
     * 1. 将token值进行解密分解
     * 2. 将三个参数字符串放入数组中，分解为userId,appId,expiredTime
     *
     * @param token
     * @return
     */
    public static Map tokenDecrypt(String token) throws Exception {
        Map map = new HashMap<>();

        String tokenStr = DES.decrypt(token, TOKEN_ENCRYPT_WORD);

        String[] tokenInfo = tokenStr.split(",");

        String userId = tokenInfo[0].toString();
        String appId = tokenInfo[1].toString();
        Date expire = new Date(Long.valueOf(tokenInfo[2].toString()).longValue());

        map.put("userId", userId);
        map.put("appId", appId);
        map.put("expire", expire.toString());

        return map;
    }
}
