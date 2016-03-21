package com.yihu.ehr.adapter.service;

import com.yihu.ehr.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public String getModel(Map parms) throws Exception{

        return doGet(comUrl + modelUrl.replace("{id}", String.valueOf(parms.get("id"))), parms);
    }

    public String delete(Map parms) throws Exception{

        return doDelete(comUrl + deleteUrl, parms);
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
