package com.yihu.ehr.util;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;


/** HTTP 请求工具类
 * Created by zqb on 2016/3/18.
 */
@Component
public class RestTemplates extends RestTemplate{
    public RestTemplates() {
        super();
    }
    //GET
    public String doGet(String url,Object... urlVariables) throws RestClientException {
       return getForObject(url,String.class,urlVariables);
    }

    public String doGet(String url, Map<String, ?> urlVariables) throws RestClientException {
       return getForObject(url,String.class,urlVariables);
    }
    //---
    public String doGet(URI url) throws RestClientException {
       return getForObject(url,String.class);
    }


    //POST
    public String doPost(String url, Object request, Object... uriVariables)
            throws RestClientException {
        return postForObject(url, request,String.class,uriVariables);
    }

    public String doPost(String url, Object request, Map<String, ?> uriVariables)
            throws RestClientException {
        return postForObject(url, request,String.class,uriVariables);
    }

    //---
    public String doPost(URI url, Object request) throws RestClientException {
        return postForObject(url, request,String.class);
    }
    //PUT
    public String doPut(String url, Object request, Object... urlVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.PUT, requestCallback, responseExtractor, urlVariables);
    }

    public String doPut(String url, Object request, Map<String, ?> urlVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.PUT, requestCallback, responseExtractor, urlVariables);
    }

    //---
    public String doPut(URI url, Object request) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.PUT, requestCallback, responseExtractor);
    }
    //DELETE
    public String doDelete(String url, Object... urlVariables) throws RestClientException {
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.DELETE, null, responseExtractor, urlVariables);
    }

    //---
    public String doDelete(String url, Map<String, ?> urlVariables) throws RestClientException {
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.DELETE, null, responseExtractor, urlVariables);
    }

    public String doDelete(URI url) throws RestClientException {
        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<String>(String.class, getMessageConverters());
        return execute(url, HttpMethod.DELETE, null, responseExtractor);
    }
}
