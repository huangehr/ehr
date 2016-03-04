package com.yihu.ehr.standard.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.controller.BaseRestController;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/2/4
 */
public class ExtendController<T>  extends BaseRestController{

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
        return new ApiException(ErrorCode.SystemError, "系统出错！");
    }

    protected ApiException errParm(){
        return new ApiException(ErrorCode.InvalidParameter, "参数解析错误！");
    }

    protected ApiException errMissCode(){
        return errMissParm("代码");
    }

    protected ApiException errMissId(){
        return errMissParm("编号");
    }

    protected ApiException errRepeatCode(){
        return new ApiException(ErrorCode.RepeatCode);
    }

    protected ApiException errMissParm(String msg){
        return new ApiException(ErrorCode.MissParameter, msg);
    }

    protected ApiException errNotFound(String objName, Object id){
        return new ApiException(ErrorCode.NotFoundObj, objName, String.valueOf(id));
    }

    protected ApiException errMissVersion(){
        return new ApiException(ErrorCode.MissVersion, "版本号不能为空！");
    }
}
