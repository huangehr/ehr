package com.yihu.ehr.basic.fzopen.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * 福州总部接口调用工具类
 */
public class OPPlaintTextUtil {

    private static Logger logger = LoggerFactory.getLogger(OPPlaintTextUtil.class);

    /**
     * 调用福州总部接口
     *
     * @param appId    渠道号
     * @param secret   秘钥
     * @param apiUrl   完整请求地址
     * @param paramMap 接口参数
     * @return
     * @throws Exception
     */
    public static String callApi(String appId, String secret, String apiUrl,
                                 Map<String, String> paramMap) throws Exception {
        String result = null;

        //加入时间戳
        String timestamp = Long.toString(System.currentTimeMillis());
        paramMap.put("timestamp", timestamp);

        StringBuilder stringBuilder = new StringBuilder();

        // 对参数名进行字典排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);
        // 拼接有序的参数名-值串
        stringBuilder.append(appId);
        for (String key : keyArray) {
            stringBuilder.append(key).append(paramMap.get(key));
        }
        String codes = stringBuilder.append(secret).toString();
        org.apache.commons.codec.digest.DigestUtils.sha1Hex("1");
        // SHA-1编码，
        // 这里使用的是Apache-codec，即可获得签名(shaHex()会首先将中文转换为UTF8编码然后进行sha1计算，使用其他的工具包请注意UTF8编码转换)
        String sign = DigestUtils.sha1Hex(codes).toUpperCase();

        // 添加签名
        paramMap.put("appId", appId);
        paramMap.put("sign", sign);
        logger.info("请求参数为：" + paramMap);
        result = HttpUtil.httpPost(apiUrl, paramMap);
        logger.info("返回结果为：" + result);

        return result;
    }

}
