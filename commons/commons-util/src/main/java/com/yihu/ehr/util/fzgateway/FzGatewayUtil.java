package com.yihu.ehr.util.fzgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.http.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 福州总部接口网关相关
 *
 * @author 张进军
 * @date 2018/3/29 11:44
 */
public class FzGatewayUtil {

    /**
     * HTTP POST 调用福州总部网关接口
     *
     * @param gatewayUrl    总部网关地址
     * @param clientId      渠道ID
     * @param clientVersion 接入方系统版本号
     * @param api           API 名称，格式为 a.b.c
     * @param apiParams     请求参数
     * @param apiVersion    API 版本号，版本号为整型，从数字 1 开始递增
     * @return 响应参数
     */
    public static String httpPost(String gatewayUrl, String clientId, String clientVersion,
                                  String api, Map apiParams, int apiVersion) {
        Map<String, String> authInfoMap = new HashMap<>();
        authInfoMap.put("ClientId", clientId);
        authInfoMap.put("ClientVersion", clientVersion);
        authInfoMap.put("Sign", "");
        authInfoMap.put("SessionKey", "");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();
        String result = null;
        try {
            params.put("AuthInfo", objectMapper.writeValueAsString(authInfoMap));
            params.put("SequenceNo", DateUtil.getNowDate().toString());
            params.put("Api", api);
            params.put("Param", objectMapper.writeValueAsString(apiParams));
            params.put("ParamType", 0);
            params.put("OutType", 0);
            params.put("V", apiVersion);
            result = HttpClientUtil.doPost(gatewayUrl, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
