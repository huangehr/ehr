package com.yihu.ehr.util.http;

import com.yihu.ehr.util.log.LogService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utils - HTTP请求辅助工具类
 * Created by progr1mmer on 2017/9/27.
 */
public class HttpUtils {

    public static HttpResponse doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, null);
    }

    public static HttpResponse doGet(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return doGet(url, params, headers, null, null);
    }

    public static HttpResponse doGet(String url, Map<String, Object> params, Map<String, String> headers, String username, String password) throws Exception {
        String response;
        int status;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if(params != null) {
            for(String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
            }
        }
        String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
        HttpGet httpGet = new HttpGet(url + "?" + paramStr);
        if(headers != null) {
            for(String key : headers.keySet()) {
                httpGet.addHeader(key, headers.get(key));
            }
        }
        try {
            if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            closeableHttpResponse = httpClient.execute(httpGet);
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            status = closeableHttpResponse.getStatusLine().getStatusCode();
            response = getRespString(resEntity);
        }finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(status != HttpStatus.SC_OK) {
            LogService.getLogger().error(" GET: " + url + " " + status);
        }
        HttpResponse httpResponse = new HttpResponse(status, response);
        return httpResponse;

    }

    public static HttpResponse doPost(String url, Map<String, Object> params) throws Exception {
        return doPost(url, params, null);
    }

    public static HttpResponse doPost(String url, Map<String, Object> params, Map<String, String> headers) throws Exception{
        return doPost(url, params, headers, null, null);
    }

    public static HttpResponse doPost(String url, Map<String, Object> params, Map<String, String> headers, String username, String password) throws Exception{
        String response;
        int status;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if(params != null) {
            for(String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
        if(headers != null) {
            for(String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        }
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            status = closeableHttpResponse.getStatusLine().getStatusCode();
            response = getRespString(resEntity);
        }finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(status != HttpStatus.SC_OK) {
            LogService.getLogger().error(" POST: " + url + " " + status);
        }
        HttpResponse httpResponse = new HttpResponse(status, response);
        return httpResponse;
    }

    public static HttpResponse doPut(String url, Map<String, Object> params) throws Exception {
        return doPut(url, params, null);
    }

    public static HttpResponse doPut(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return doPut(url, params, headers, null, null);
    }

    public static HttpResponse doPut(String url, Map<String, Object> params, Map<String, String> headers, String username, String password) throws Exception {
        String response;
        int status;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        HttpPut httpPut = new HttpPut(url);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if(params != null) {
            for(String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
            }
        }
        httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
        if(headers != null) {
            for(String key : headers.keySet()) {
                httpPut.addHeader(key, headers.get(key));
            }
        }
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            closeableHttpResponse = httpClient.execute(httpPut);
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            status = closeableHttpResponse.getStatusLine().getStatusCode();
            response = getRespString(resEntity);
        }finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(status != HttpStatus.SC_OK) {
            LogService.getLogger().error(" PUT: " + url + " " + status);
        }
        HttpResponse httpResponse = new HttpResponse(status, response);
        return httpResponse;
    }

    public static HttpResponse doDelete(String url, Map<String, Object> params) throws Exception {
        return doDelete(url, params, null);
    }

    public static HttpResponse doDelete(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return doDelete(url, params, headers, null, null);
    }

    public static HttpResponse doDelete(String url, Map<String, Object> params, Map<String, String> headers, String username, String password) throws Exception {
        String response;
        int status;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if(params != null) {
            for(String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
            }
        }
        String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
        HttpDelete httpDelete = new HttpDelete(url + "?" + paramStr);
        if(headers != null) {
            for(String key : headers.keySet()) {
                httpDelete.addHeader(key, headers.get(key));
            }
        }
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            closeableHttpResponse = httpClient.execute(httpDelete);
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            status = closeableHttpResponse.getStatusLine().getStatusCode();
            response = getRespString(resEntity);
        }finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(status != HttpStatus.SC_OK) {
            LogService.getLogger().error(" DELETE: " + url + " " + status);
        }
        HttpResponse httpResponse = new HttpResponse(status, response);
        return httpResponse;
    }

    public static HttpResponse doUpload(String url, Map<String, Object> params, File file) throws Exception {
        return doUpload(url, params, null, file, null, null);
    }

    public static HttpResponse doUpload(String url, Map<String, Object> params, Map<String, String> headers, File file) throws Exception {
        return doUpload(url, params, headers, file, null, null);
    }

    public static HttpResponse doUpload(String url, Map<String, Object> params, Map<String, String> headers, File file, String username, String password) throws Exception {
        String response;
        int status;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        FileBody fileBody = new FileBody(file);
        multipartEntityBuilder.addPart("file", fileBody);
        if (params != null) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null) {
                    multipartEntityBuilder.addTextBody(key, String.valueOf(params.get(key)), ContentType.TEXT_PLAIN);
                }
            }
        }
        if (headers != null) {
            for(String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        }
        HttpEntity reqEntity = multipartEntityBuilder.build();
        httpPost.setEntity(reqEntity);
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            status = closeableHttpResponse.getStatusLine().getStatusCode();
            response = getRespString(resEntity);
        }finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(status != HttpStatus.SC_OK) {
            LogService.getLogger().error(" POST UPLOAD: " + url + " " + status);
        }
        HttpResponse httpResponse = new HttpResponse(status, response);
        return httpResponse;
    }

    private static String getRespString(HttpEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        InputStream is = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

}
