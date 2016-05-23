package com.yihu.ehr.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求工具类
 *
 * @author : cwd
 * @version : 1.0.0
 * @date : 2016/1/14
 * @see : TODO
 */
public class HttpClientUtil {
    /**
     * 发送 GET 请求（HTTP），不带输入数据 不加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url) throws Exception {
        return doGet(url, new HashMap<String, Object>(), "", "");
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据 不加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, "", "");
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据 加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url, String username, String password) throws Exception {
        return doGet(url, new HashMap<String, Object>(), username, password);
    }

    /**
     * httpClient的get请求方式
     *
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> params, String username, String password)
            throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        GetMethod getMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            getMethod = new GetMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, "UTF-8");
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     *  httpClient的post请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params, String username, String password) throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        PostMethod postMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            postMethod = new PostMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("请求出错: " + postMethod.getStatusLine());
            }
            byte[] responseBody = postMethod.getResponseBody();
            response = new String(responseBody, "UTF-8");
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     *  httpClient的put请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPut(String url, Map<String, Object> params, String username, String password) throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        PutMethod putMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            putMethod = new PutMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            putMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(putMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("请求出错: " + putMethod.getStatusLine());
            }
            byte[] responseBody = putMethod.getResponseBody();
            response = new String(responseBody, "UTF-8");
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     *  httpClient的delete请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doDelete(String url, Map<String, Object> params, String username, String password) throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        DeleteMethod deleteMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            deleteMethod = new DeleteMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            deleteMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(deleteMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("请求出错: " + deleteMethod.getStatusLine());
            }
            byte[] responseBody = deleteMethod.getResponseBody();
            response = new String(responseBody, "UTF-8");
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }
}