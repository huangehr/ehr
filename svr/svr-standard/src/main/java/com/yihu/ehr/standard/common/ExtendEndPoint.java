package com.yihu.ehr.standard.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.controller.EnvelopRestEndPoint;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/2/4
 */
public class ExtendEndPoint<T>  extends EnvelopRestEndPoint {

    protected Long[] strToLongArr(String str){
        if(str==null || str.trim().length()==0)
            return new Long[0];
        return strArrToLongArr(str.split(","));
    }

    protected Long[] strArrToLongArr(String[] strArr){

        Long[] rs = new Long[strArr.length];
        for (int i=0; i< strArr.length; i++){
            rs[i] = Long.valueOf(strArr[i]);
        }
        return rs;
    }
    protected <T> T jsonToObj(String json ,Class<T> clz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clz);
    }

    protected T getModel(Object o){
        return (T) convertToModel(o, getModelClass());
    }

    public Class getModelClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    /*******************************************************************************************/
    /****************    以下是错误返回信息方法              **********************************/
    /*******************************************************************************************/

    protected ApiException errSystem(){
        return new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "系统出错！");
    }

    protected ApiException errParm(){
        return new ApiException(ErrorCode.BAD_REQUEST, "参数解析错误！");
    }

    protected ApiException errMissCode(){
        return errMissParm("代码");
    }

    protected ApiException errMissId(){
        return errMissParm("编号");
    }

    protected ApiException errRepeatCode(){
        return new ApiException(ErrorCode.BAD_REQUEST, "重复的CODE");
    }

    protected ApiException errMissParm(String msg){
        return new ApiException(ErrorCode.BAD_REQUEST, msg);
    }

    protected ApiException errNotFound(String objName, Object id){
        return new ApiException(ErrorCode.NOT_FOUND, objName);
    }

    protected ApiException errMissVersion(){
        return new ApiException(ErrorCode.NOT_FOUND, "版本号不能为空！");
    }
}
