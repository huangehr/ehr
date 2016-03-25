package com.yihu.ehr.adapter.service;

import com.yihu.ehr.util.HttpClientUtil;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/19
 */

public class ExtendService<T> {
    @Value("${service-gateway.username}")
    public String username;
    @Value("${service-gateway.password}")
    public String password;
    @Value("${service-gateway.url}")
    public String comUrl;

    public String modelUrl = "";
    public String addUrl = "";
    public String modifyUrl = "";
    public String deleteUrl = "";
    public String searchUrl = "";


    public void init(String searchUrl, String modelUrl, String addUrl, String modifyUrl, String deleteUrl ){
        this.addUrl = addUrl;
        this.deleteUrl = deleteUrl;
        this.modelUrl = modelUrl;
        this.modifyUrl = modifyUrl;
        this.searchUrl = searchUrl;
    }

    public String search(Map parms) throws Exception{

        return doGet(comUrl + searchUrl, parms);
    }

    public String search(String url, Map parms) throws Exception{

        return doGet(comUrl + url, parms);
    }

    public String getModel(Map parms) throws Exception{

        return doGet(comUrl + modelUrl.replace("{id}", String.valueOf(parms.get("id"))), parms);
    }

    public String delete(Map parms) throws Exception{

        return doDelete(comUrl + deleteUrl, parms);
    }

    public String update(Map parms) throws Exception{

        return doPut(comUrl + modifyUrl.replace("{id}", String.valueOf(parms.get("id"))), parms);
    }

    public String add(Map parms) throws Exception{

        return doPost(comUrl + addUrl, parms);
    }

    public String doPut(String url, Map parms) throws Exception{

        return HttpClientUtil.doPut(url, parms, username, password);
    }

    public String doPost(String url, Map parms) throws Exception{

        return HttpClientUtil.doPost(url, parms, username, password);
    }

    public String doLargePost(String url, Map<String, Object> params) throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = "";
        PostMethod postMethod = null;
        new StringBuilder();

        try {
            try {
                if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                    UsernamePasswordCredentials e = new UsernamePasswordCredentials(username, password);
                    httpClient.getState().setCredentials(AuthScope.ANY, e);
                }
//                int so = httpClient.getHttpConnectionManager().getParams().getSoTimeout();
//                so = httpClient.getHttpConnectionManager().getParams().getConnectionTimeout();
//                httpClient.getHttpConnectionManager().getParams().setSoTimeout(6000);
//                httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(6000);
                postMethod = new PostMethod(url);
                postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                for(Object key : params.keySet()){
                    postMethod.addParameter((String) key, String.valueOf(params.get(key)));
                }

                postMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler());
                int e2 = httpClient.executeMethod(postMethod);
//                HttpConnectionManagerParams httpConnectionManagerParams = new HttpConnectionManagerParams();
//                httpClient.getHttpConnectionManager().setParams();
                if(e2 != 200) {
                    throw new Exception("请求出错: " + postMethod.getStatusLine());
                }

                byte[] responseBody1 = postMethod.getResponseBody();
                response = new String(responseBody1, "UTF-8");
            } catch (HttpException var16) {
                var16.printStackTrace();
            } catch (IOException var17) {
                var17.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return response;
        } finally {
            ;
        }
    }

    public String doGet(String url, Map parms) throws Exception{

        return HttpClientUtil.doGet(url, parms, username, password);
    }

    public String doDelete(String url, Map parms) throws Exception{

        return HttpClientUtil.doDelete(url, parms, username, password);
    }

    protected Class getModelClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    public T newModel() {
        try {
            return (T) getModelClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
