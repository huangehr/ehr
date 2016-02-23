package com.yihu.ehr.adaption.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.controller.BaseRestController;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/2/4
 */
public class ExtendController<T> extends BaseRestController {

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
        return errMissParm("代码不能为空！");
    }

    protected ApiException errMissId(){
        return errMissParm("编号不能为空！");
    }

    protected ApiException errRepeatCode(){
        return new ApiException(ErrorCode.RepeatCode, "代码已存在!");
    }

    protected ApiException errMissParm(String msg){
        return new ApiException(ErrorCode.MissParameter, msg);
    }

    protected ApiException errNotFound(){
        return new ApiException(ErrorCode.NotFoundObj, "不存在该对象！");
    }

    protected ApiException errMissVersion(){
        return new ApiException(ErrorCode.MissVersion, "版本号不能为空！");
    }
}
