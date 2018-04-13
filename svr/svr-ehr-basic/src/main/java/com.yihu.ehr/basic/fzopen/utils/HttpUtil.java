package com.yihu.ehr.basic.fzopen.utils;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    public static String httpPost(String url, Map<String, Object> params) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build();//设置请求和传输超时时间

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if (params != null && params.size() > 0) {
                List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                    valuePairs.add(nameValuePair);
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
                httpPost.setEntity(formEntity);
            }
            CloseableHttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity entity = resp.getEntity();
                String respContent = EntityUtils.toString(entity, "UTF-8").trim();
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
