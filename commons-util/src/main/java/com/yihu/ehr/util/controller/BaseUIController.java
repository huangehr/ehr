package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/** UI用controller工具类
 * Created by Administrator on 2016/3/16.
 */
public class BaseUIController {

    private static String ERR_SYSREM_DES = "系统错误,请联系管理员!";

    @Autowired
    public ObjectMapper objectMapper;

    public Envelop getEnvelop(String json){
        try {
            return objectMapper.readValue(json,Envelop.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object data){
        try {
            String json = objectMapper.writeValueAsString(data);
            return json;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public <T> T toModel(String json,Class<T> targetCls){
        try {
            T model = objectMapper.readValue(json, targetCls);
            return model;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *将envelop中的obj串转化为model
     * Envelop envelop = objectMapper.readValue(resultStr,Envelop.class)
     * jsonData = envelop.getObj()
     * @param jsonData
     * @param targetCls
     * @param <T>
     * @return
     */
    public <T> T getEnvelopModel(Object jsonData, Class<T> targetCls) {
        try {
            String objJsonData = objectMapper.writeValueAsString(jsonData);
            T model = objectMapper.readValue(objJsonData, targetCls);
            return model;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     *将envelop中的DetailList串转化为模板对象集合
     *Envelop envelop = objectMapper.readValue(resultStr,Envelop.class)
     * modelList = envelop.getDetailModelList()
     * @param modelList
     * @param targets
     * @param targetCls
     * @param <T>
     * @return
     */
    public <T> Collection<T> getEnvelopList(List modelList, Collection<T> targets, Class<T> targetCls) {
        try {
            for (Object aModelList : modelList) {
                String objJsonData = objectMapper.writeValueAsString(aModelList);
                T model = objectMapper.readValue(objJsonData, targetCls);
                targets.add(model);
            }
            return targets;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Envelop failed(String errMsg) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg(errMsg);
        return envelop;
    }

    public Envelop success(Object object) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(object);
        return envelop;
    }

    protected Envelop failedSystem() {
        return failed(ERR_SYSREM_DES);
    }
}