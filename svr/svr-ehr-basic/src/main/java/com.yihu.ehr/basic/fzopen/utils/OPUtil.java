package com.yihu.ehr.basic.fzopen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.fzopen.utils.aes.MsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 福州总部接口调用工具类
 */
public class OPUtil {

    private static Logger logger = LoggerFactory.getLogger(OPUtil.class);

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

        ObjectMapper objectMapper = new ObjectMapper();
        String replyMsg = objectMapper.writeValueAsString(paramMap);

        String timestamp = Long.toString(System.currentTimeMillis());
        MsgCrypt pc = new MsgCrypt(secret, appId);
        Map<String, String> mingwen = pc.encryptMsg(replyMsg, timestamp);
        logger.info("加密后: " + mingwen.toString());
        String encrypt = mingwen.get("encrypt");
        String sign = mingwen.get("sign");
        String result2 = pc.decryptMsg(sign, timestamp, encrypt);
        logger.info("解密后明文: " + result2);

        String rep = HttpUtil.httpPost(apiUrl, mingwen);
        logger.info("服务器返回的结果：" + rep);

        // 解密服务器返回的结果
        Map repMap = objectMapper.readValue(rep, Map.class);
        sign = repMap.get("sign").toString();
        encrypt = repMap.get("encrypt").toString();
        result2 = pc.decryptMsg(sign, timestamp, encrypt);
        logger.info("解密服务器返回的结果: " + result2);

        return result2;
    }

}
