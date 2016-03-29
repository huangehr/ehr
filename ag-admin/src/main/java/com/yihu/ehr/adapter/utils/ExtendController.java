package com.yihu.ehr.adapter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.validate.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/1
 */
public class ExtendController<T> extends BaseController {
    public static String ERR_SYSREM_DES = "系统错误！";

    protected String objToJson(Object obj) throws JsonProcessingException {

        return objectMapper.writeValueAsString(obj);
    }

    protected String toEncodeJson(Object obj) throws JsonProcessingException, UnsupportedEncodingException {

        return URLEncoder.encode(objectMapper.writeValueAsString(obj), "UTF-8") ;
    }

    protected <T> T jsonToObj(String json) throws IOException {

        return (T) jsonToObj(json, getModelClass());
    }

    protected <T> T jsonToObj(String json , Class<T> clz) throws IOException {

        return objectMapper.readValue(json, clz);
    }

    protected Class getModelClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    protected T getModel(Object obj){

        return (T) convertToModel(obj, getModelClass());
    }

    public Envelop failed(String errMsg){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg(errMsg);
        return envelop;
    }

    protected Envelop failedSystem(){
        return failed(ERR_SYSREM_DES);
    }

    protected Envelop successList(List ls){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList((List) convertToModels(ls, new ArrayList<>(), getModelClass(), ""));
        return envelop;
    }

    public Envelop success(Object object){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(convertToModel(object, getModelClass()));
        return envelop;
    }

    protected ValidateResult validate(T dataModel)
            throws InvocationTargetException, IllegalAccessException {

        return ValidateUtil.validate(dataModel);
    }

}
