package com.yihu.ehr.basic.openplatform.util;

import com.yihu.ehr.basic.openplatform.config.OpenPlatformConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@Component
public class OpenPlatformUtil {

    private OpenPlatformConfiguration openPlatformConfiguration;

    public String callAPI(String apiName, Map<String, String> paramMap) throws Exception {
        String result = null;

        //加入时间戳
        String timestamp = Long.toString(System.currentTimeMillis());
        paramMap.put("timestamp",timestamp);

        String apiUrl = this.openPlatformConfiguration.getServerUrl()+apiName;
        StringBuilder stringBuilder = new StringBuilder();

        // 对参数名进行字典排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);
        // 拼接有序的参数名-值串
        stringBuilder.append(this.openPlatformConfiguration.getAppId());
        for (String key : keyArray) {
            stringBuilder.append(key).append(paramMap.get(key));
        }
        String codes = stringBuilder.append(this.openPlatformConfiguration.getSecret()).toString();
        org.apache.commons.codec.digest.DigestUtils.sha1Hex("1");
        // SHA-1编码，
        // 这里使用的是Apache-codec，即可获得签名(shaHex()会首先将中文转换为UTF8编码然后进行sha1计算，使用其他的工具包请注意UTF8编码转换)
        String sign = org.apache.commons.codec.digest.DigestUtils.shaHex(codes)
                .toUpperCase();

        // 添加签名
        paramMap.put("appId", this.openPlatformConfiguration.getAppId());
        paramMap.put("sign", sign);
//        System.out.println("请求参数为："+paramMap);
//        long start = System.currentTimeMillis();
        result = httpPost(apiUrl, paramMap);
//        long end = System.currentTimeMillis();
//        System.out.println("返回结果为："+result);

        return result;
    }

    public String httpPost(String url, Map<String, String> params)
            throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build();//设置请求和传输超时时间

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if (params != null && params.size() > 0) {
                List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(
                        params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    NameValuePair nameValuePair = new BasicNameValuePair(
                            entry.getKey(), String.valueOf(entry.getValue()));
                    valuePairs.add(nameValuePair);
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                        valuePairs, "UTF-8");
                httpPost.setEntity(formEntity);
            }
            CloseableHttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity entity = resp.getEntity();
                String respContent = EntityUtils.toString(entity, "UTF-8")
                        .trim();
                return respContent;
            } finally {
                resp.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            httpclient.close();
        }

    }
}
