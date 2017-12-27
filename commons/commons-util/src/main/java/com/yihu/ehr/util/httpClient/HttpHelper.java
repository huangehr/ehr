package com.yihu.ehr.util.httpClient;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.util.Map;

/**
 * Created by hzp on 2016/8/9.
 */
@Service
public class HttpHelper {

    @Value("${http-service.sslKeystore:''}")
    private String sslKeystore;

    @Value("${http-service.sslPassword:''}")
    private String sslPassword;

    @Value("${http-service.httpsUser:''}")
    private String httpsUser;

    @Value("${http-service.httpsUserPassword:''}")
    private String httpsUserPassword;

    private SSLConnectionSocketFactory  getDefaultSSL()
    {
        try {
            if (sslKeystore != null && sslKeystore.length() > 0 && sslPassword != null && sslPassword.length() > 0) {
                SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(new File(sslKeystore), sslPassword.toCharArray(),
                                new TrustSelfSignedStrategy())
                        .build();
                return new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1"},
                        null,
                        null);
            } else {
                return null;
            }
        }
        catch (Exception ex)
        {
            System.out.print("获取SSL配置失败！");
            return null;
        }
    }


    /************************** Get方法 ******************************************/
    public HttpResponse get(String url)
    {
        return get(url,null,null);
    }
    public HttpResponse get(String url,Map<String,Object> params)
    {
        return get(url,params,null);
    }
    public HttpResponse get(String url,Map<String,Object> params,Map<String,Object> header)
    {
        if(url.startsWith("https"))
        {
            return get(url,params,header,getDefaultSSL(),httpsUser,httpsUserPassword);
        }
        else{
            //默认http不走ssl和用户密码
            return get(url, params, header, null, null, null);
        }
    }
    public HttpResponse get(String url,Map<String,Object> params,Map<String,Object> header,SSLConnectionSocketFactory ssl,String user,String password)
    {

        return HttpClientUtil.request("GET", url, params, header, ssl, user, password);
    }

    /************************** Post方法 ******************************************/
    public HttpResponse post(String url)
    {
        return post(url, null, null);
    }
    public HttpResponse post(String url,Map<String,Object> params)
    {
        return post(url, params, null);
    }
    public HttpResponse post(String url,Map<String,Object> params,Map<String,Object> header)
    {
        if(url.startsWith("https"))
        {
            return post(url, params, header, getDefaultSSL(),httpsUser,httpsUserPassword);
        }
        else{
            //默认http不走ssl和用户密码
            return post(url, params, header, null, null, null);
        }
    }
    public HttpResponse post(String url,Map<String,Object> params,Map<String,Object> header,SSLConnectionSocketFactory ssl,String user,String password)
    {
        return HttpClientUtil.request("POST",url,params,header,ssl,user,password);
    }

    /************************** Put方法 ******************************************/
    public HttpResponse put(String url)
    {
        return put(url, null, null);
    }
    public HttpResponse put(String url,Map<String,Object> params)
    {
        return put(url, params, null);
    }
    public HttpResponse put(String url,Map<String,Object> params,Map<String,Object> header)
    {
        if(url.startsWith("https"))
        {
            return put(url, params, header, getDefaultSSL(),httpsUser,httpsUserPassword);
        }
        else{
            //默认http不走ssl和用户密码
            return put(url, params, header, null, null, null);
        }
    }

    public HttpResponse put(String url,Map<String,Object> params,Map<String,Object> header,SSLConnectionSocketFactory ssl,String user,String password)
    {

        return HttpClientUtil.request("PUT",url,params,header,ssl,user,password);
    }

    /************************** Delete方法 **************************************/
    public HttpResponse delete(String url)
    {
        return delete(url, null, null);
    }
    public HttpResponse delete(String url,Map<String,Object> params)
    {
        return delete(url, params, null);
    }
    public HttpResponse delete(String url,Map<String,Object> params,Map<String,Object> header)
    {
        if(url.startsWith("https"))
        {
            return delete(url, params, header, getDefaultSSL(),httpsUser,httpsUserPassword);
        }
        else{
            //默认http不走ssl和用户密码
            return delete(url, params, header, null, null, null);
        }
    }

    public static HttpResponse delete(String url,Map<String,Object> params,Map<String,Object> header,SSLConnectionSocketFactory ssl,String user,String password)
    {

        return HttpClientUtil.request("DELETE",url,params,header,ssl,user,password);
    }



}
