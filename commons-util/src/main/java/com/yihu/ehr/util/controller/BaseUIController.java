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
    @Autowired
    ObjectMapper objectMapper;

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
}