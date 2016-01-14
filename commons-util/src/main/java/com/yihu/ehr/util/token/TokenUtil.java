package com.yihu.ehr.util.token;

import com.yihu.ha.util.encrypt.DES;
import com.yihu.ha.util.encrypt.MD5;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2015/7/31.
 */
public class TokenUtil {

    private final static String NUM_CHAR = "0123456789";
    private static int charLen = NUM_CHAR.length();
    public static final String PASS_WORD = "ha_passw0rd";

    //随机数生成
    public static String getRandomNumberSeed(int randomNumberDigit) {

        long seed = System.currentTimeMillis();  // 获得系统时间，作为生成随机数的种子
        StringBuffer sb = new StringBuffer();    // 装载生成的随机数
        Random random = new Random(seed);        // 调用种子生成随机数

        for (int i = 0; i < randomNumberDigit; i++) {
            sb.append(NUM_CHAR.charAt(random.nextInt(charLen)));
        }
        return sb.toString();
    }

    /**
     * 1. 生成授权码 (按日期生成随机码进行MD5加密)
     *
     * @throws Exception
     */
    public static String genToken() throws Exception {

        //生成16位的随机数种子
        String seed  = getRandomNumberSeed(16);
        //MD5加密生成Token值
        String token = MD5.hash(seed);

        return token;
    }

    /**
     * 2. 生成授权码
     * 2-1 将createDate换算成秒数 + expiresIn = 失效的日期的秒数,
     * 2-2 秒数 + userId + appId进行组合DES加密。
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

        String token = DES.encrypt(sb.toString(), PASS_WORD);

        return token;
    }

    /**
     * 3. 校验请求的签名是否合法
     * token校验流程：
     * 1. 将token值进行解密分解
     * 2. 将三个参数字符串放入数组中，分解为userId,appId,expiredTime
     *
     * @param token
     * @return
     */
    public static Map tokenDecrypt(String token) throws Exception {

        Map map = new HashMap<>();

        String tokenStr = DES.decrypt(token, PASS_WORD);

        String[] tokenInfo = tokenStr.split(",");

        String userId = tokenInfo[0].toString();
        String appId = tokenInfo[1].toString();
        long expriedTime = Long.valueOf(tokenInfo[2].toString()).longValue();

        Date expriedDate = new Date(expriedTime);

        map.put("userId", userId);
        map.put("appId", appId);
        map.put("expriedTime", expriedDate.toString());

        return map;
    }
}
