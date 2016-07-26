package com.yihu.ehr.util.HttpClientUtil;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/4/14.
 */
public class HttpClientUtil {


    /**
     * httpClient的post请求方式
     *
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params, String username, String password) throws Exception {
        String responString = "";
        CloseableHttpResponse response = null;
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpPost httpPost = new HttpPost(url);
            //设置请求信息
            RequestConfig requestConfig = RequestConfig.custom().
                    setAuthenticationEnabled(true).build();
            //设置参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

            httpPost.setEntity(entity);
            httpPost.setConfig(requestConfig);
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                HttpClientContext context = HttpClientContext.create();
                //通过http的上下文设置账号密码
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new org.apache.http.auth.AuthScope(org.apache.http.auth.AuthScope.ANY_HOST, org.apache.http.auth.AuthScope.ANY_PORT),
                        new org.apache.http.auth.UsernamePasswordCredentials(username, password));
                context.setCredentialsProvider(credsProvider);
                response = httpclient.execute(httpPost, context);
            } else {
                response = httpclient.execute(httpPost);
            }
            HttpEntity httpEntity = response.getEntity();
            //流转字符串
            responString = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
            httpclient.close();
        }
        return responString;
    }

    /**
     * httpClient的put请求方式
     *
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPut(String url, Map<String, Object> params, String username, String password) throws Exception {
        String responString = "";
        CloseableHttpResponse response1 = null;
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            //设置请求信息
            RequestConfig requestConfig = RequestConfig.custom().
                    setAuthenticationEnabled(true).build();

            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            HttpPut httpPut = new HttpPut(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
            httpPut.setConfig(requestConfig);
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                HttpClientContext context = HttpClientContext.create();
                //通过http的上下文设置账号密码
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new org.apache.http.auth.AuthScope(org.apache.http.auth.AuthScope.ANY_HOST, org.apache.http.auth.AuthScope.ANY_PORT),
                        new org.apache.http.auth.UsernamePasswordCredentials(username, password));
                context.setCredentialsProvider(credsProvider);
                response1 = httpclient.execute(httpPut, context);
            } else {
                response1 = httpclient.execute(httpPut);
            }
            HttpEntity entity1 = response1.getEntity();
            responString = EntityUtils.toString(entity1, "UTF-8");
        } finally {
            response1.close();
            httpclient.close();
        }
        return responString;
    }

    /**
     * httpClient的delete请求方式
     *
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doDelete(String url, Map<String, Object> params, String username, String password) throws Exception {
        String responString = "";
        CloseableHttpResponse response1 = null;
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            //设置请求信息
            RequestConfig requestConfig = RequestConfig.custom().
                    setAuthenticationEnabled(true).build();

            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            HttpDelete httpget = new HttpDelete(url + "?" + URLEncodedUtils.format(jsonParams, Consts.UTF_8));
            httpget.setConfig(requestConfig);
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                HttpClientContext context = HttpClientContext.create();
                //通过http的上下文设置账号密码
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new org.apache.http.auth.AuthScope(org.apache.http.auth.AuthScope.ANY_HOST, org.apache.http.auth.AuthScope.ANY_PORT),
                        new org.apache.http.auth.UsernamePasswordCredentials(username, password));
                context.setCredentialsProvider(credsProvider);
                response1 = httpclient.execute(httpget, context);
            } else {
                response1 = httpclient.execute(httpget);
            }
            HttpEntity entity1 = response1.getEntity();
            responString = EntityUtils.toString(entity1, "UTF-8");
        } finally {
            response1.close();
            httpclient.close();
        }
        return responString;
    }


}

