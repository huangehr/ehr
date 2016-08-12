package com.yihu.ehr.util.httpClient;

import org.apache.http.Consts;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/9.
 */
public class HttpClientUtil {

    /**************************** 私有方法 *****************************************/
    private static CloseableHttpClient getCloseableHttpClient(SSLConnectionSocketFactory ssl) {
        if(ssl == null)
        {
            return HttpClients.createDefault();
        }
        else{
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(ssl)
                    .build();
            return httpClient;
        }
    }

    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e) {
            System.out.print("关闭httpClient失败");
        }
        try {
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
            System.out.print("关闭response失败");
        }
    }

    private static HttpRequestBase getRequest(String method,String url,Map<String,Object> params,Map<String,Object> header) throws Exception
    {
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        //配置参数
        if(params!=null) {
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
        }
        HttpRequestBase request;
        if(method.equals("POST"))
        {
            request = new HttpPost(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
        }
        else if(method.equals("PUT"))
        {
            request = new HttpPut(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
        }
        else if(method.equals("DELETE"))
        {
            request = new HttpDelete(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
        }
        else
        {
            request = new HttpGet(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
        }
        //配置头部信息
        if(header!=null)
        {
            for (String key : header.keySet()) {
                request.addHeader(key, header.get(key).toString());
            }
        }
        return request;
    }
    /****************************** 公用方法 *******************************************/
    /**
     * get请求
     */
    public static HttpResponse request(String method,String url,Map<String,Object> params,Map<String,Object> header,SSLConnectionSocketFactory ssl,String user,String password) {
        HttpResponse re = new HttpResponse();
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = getCloseableHttpClient(ssl);

        //设置请求信息
        try {
            RequestConfig requestConfig = RequestConfig.custom().
                    setAuthenticationEnabled(true).build();
            HttpRequestBase request = getRequest(method,url,params,header);

            request.setConfig(requestConfig);
            //需要验证
            if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(password)) {
                HttpClientContext context = HttpClientContext.create();
                //通过http的上下文设置账号密码
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new org.apache.http.auth.AuthScope(org.apache.http.auth.AuthScope.ANY_HOST, org.apache.http.auth.AuthScope.ANY_PORT),new org.apache.http.auth.UsernamePasswordCredentials(user, password));
                context.setCredentialsProvider(credsProvider);
                response = httpclient.execute(request, context);
            } else {
                response = httpclient.execute(request);
            }
            re.setStatusCode(response.getStatusLine().getStatusCode());
            re.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (Exception e) {
            re.setStatusCode(201);
            re.setBody(e.getMessage());
        } finally {
            close(httpclient, response);
        }
        return re;
    }

}
