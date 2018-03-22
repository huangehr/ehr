package com.yihu.ehr.adaption.common;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/2/4
 */
public class ExtendEndPoint<T> extends EnvelopRestEndPoint {

    protected <T> T toDecodeObj(String json ,Class<T> clz) throws IOException {

        return toEntity(URLDecoder.decode(json, "UTF-8"), clz)  ;
    }

    protected <T> T jsonToObj(String json ,Class<T> clz) throws IOException {

        return toEntity(json, clz);
    }

    protected T getModel(Object o){
        return (T) convertToModel(o, getModelClass());
    }

    public Class getModelClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    protected Long[] strToLongArr(String str){
        String[] strArr = str.split(",");
        Long[] longArr = new Long[strArr.length];
        for(int i =0; i<strArr.length; i++){
            longArr[i] = Long.parseLong(strArr[i]);
        }
        return longArr;
    }

    /*******************************************************************************************/
    /****************    以下是错误返回信息方法              **********************************/
    /*******************************************************************************************/

    protected ApiException errSystem(){
        return new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "系统出错！");
    }

    protected ApiException errParm(){
        return new ApiException(ErrorCode.BAD_REQUEST, "参数有误");
    }

    protected ApiException errMissCode(){
        return errMissParm("代码不能为空！");
    }

    protected ApiException errMissId(){
        return errMissParm("编号不能为空！");
    }

    protected ApiException errRepeatCode(){
        return new ApiException(ErrorCode.BAD_REQUEST, "重复的CODE");
    }

    protected ApiException errMissParm(String msg){
        return new ApiException(ErrorCode.BAD_REQUEST, msg);
    }

    protected ApiException errNotFound(){
        return new ApiException(ErrorCode.NOT_FOUND, "对象不存在");
    }

    protected ApiException errMissVersion(){
        return new ApiException(ErrorCode.BAD_REQUEST, "版本号不能为空！");
    }
}
