package com.yihu.ehr.ha.adapter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.validate.Length;
import com.yihu.ehr.util.validate.Required;
import com.yihu.ehr.util.validate.Valid;
import com.yihu.ehr.util.validate.ValidateResult;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/1
 */
public class ExtendController<T> extends BaseController {
    public static String ERR_SYSREM_DES = "系统错误！";
    private ObjectMapper objectMapper = new ObjectMapper();

    protected String objToJson(Object obj) throws JsonProcessingException {

        return objectMapper.writeValueAsString(obj);
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

    protected Envelop failed(String errMsg){
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

    protected Envelop success(Object object){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(convertToModel(object, getModelClass()));
        return envelop;
    }

    protected ValidateResult validate(T dataModel)
            throws InvocationTargetException, IllegalAccessException {

        Class clz = dataModel.getClass();
        if(clz.getAnnotation(Valid.class)==null)
            return ValidateResult.success();
        Method[] methods = clz.getMethods();
        ValidateResult validateResult;
        for (Method method : methods){
            if(!(validateResult = validateRequired(method, dataModel)).isRs())
                return validateResult;

            if(!(validateResult = validateLen(method, dataModel)).isRs())
                return validateResult;

            continue;
        }
        return ValidateResult.success();
    }

    private ValidateResult validateLen(Method method, T dataModel)
            throws InvocationTargetException, IllegalAccessException {

        Annotation annotation = method.getAnnotation(Length.class);
        if(annotation==null)
            return ValidateResult.success();
        Object obj = method.invoke(dataModel);
        Length length = ((Length) annotation);
        String str = "";
        if(!StringUtils.isEmpty(obj))
            str = obj.toString();

        if(str.length()<length.minLen() || str.length()>length.maxLen()){
            return ValidateResult.failedOfOutLen(length.msg(), method.getName().substring(3));
        }
        return ValidateResult.success();
    }

    private ValidateResult validateRequired(Method method, T dataModel)
            throws InvocationTargetException, IllegalAccessException {
        Annotation annotation = method.getAnnotation(Required.class);
        if(annotation==null)
            return ValidateResult.success();
        Object obj = method.invoke(dataModel);
        if(StringUtils.isEmpty(obj)){
            Required required = ((Required) annotation);
            return ValidateResult.failedOfNull(
                    formatValidateMsg(required.filedName(), required.msg(), Required.MSG)
                    , method.getName().substring(3));
        }
        return ValidateResult.success();
    }

    private String formatValidateMsg(String filedName, String msg, String def){
        if(StringUtils.isEmpty(msg))
            return filedName + def;
        return msg;
    }
}
