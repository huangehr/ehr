package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.util.RestEcho;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * REST风格控控制器基类。此控制器用于对API进行校验，并处理平台根层级的业务，如API参数校验，错误及返回码设定等。
 * <p>
 * 根层级的校验，如果是正确的，直接返回HTTP代码200，若出错，则会将HTTP返回代码设置为1X或2X，并在HTTP响应体中包含响应的信息。
 * HTTP响应体格式为JSON。
 * + 成功：会根据各业务逻辑自行决定要返回的数据，各业务模块的返回结构不同。
 * + 失败：{"code":"错误代码", "message":"错误原因"}
 *
 * @author zhiyong
 * @author Sand
 */
@Controller
@RequestMapping("/rest")
public class BaseRestController extends AbstractController{
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    /**
     * 通用：请求成功.
     *
     * 请求成功后, 返回的内容为: { code: "0",result: "json-format-data"}
     * 其中 code 必须为 0, message 必须为空, result 为业务请求的结果, 一个JSON格式的数据.
     *
     * @param subJson 有效的json格式字符串
     * @return
     */
    public static Object succeed(String subJson){
        RestEcho restEcho = new RestEcho().success();
        restEcho.putResult(subJson);

        return restEcho;
    }

    public static Object succeed(JsonNode jsonNode){
        RestEcho restEcho = new RestEcho().success();
        restEcho.putResult(jsonNode);

        return restEcho;
    }

    /**
     * 简化返回值返回，直接返回一个消息，不包含其他信息。
     */
    public static Object succeedWithMessage(String message){
        RestEcho restEcho = new RestEcho().success();
        restEcho.putMessage(message);

        return restEcho;
    }

    /**
     * 通用：请求失败.
     *
     * 请求成功后, 返回的内容为: { code: "ha.archive.upload.failed", message: "档案上传失败"}
     * 其中 code 必须为特定的错误代码, message 必须为错误描述. 没有其他字段。
     *
     * @param errorCode
     * @param args
     * @return
     */
    public static Object failed(ErrorCode errorCode, String...args){
        return new RestEcho().failed(errorCode, args);
    }

    /**
     * 通用：无效方法。
     *
     * @return
     */
    public static Object invalidMethod(){
        return failed(ErrorCode.InvalidMethod);
    }

    /**
     * 通用：无效时间戳。
     *
     * @return
     */
    public static Object invalidTimestamp(){
        return failed(ErrorCode.InvalidTimestamp);
    }

    /**
     * 通用：无效APP ID。
     *
     * @return
     */
    public static Object invalidAppId(){
        return failed(ErrorCode.InvalidAppId);
    }

    /**
     * 通用：无效APP ID。
     *
     * @return
     */
    public static Object invalidApiVersion(){
        return failed(ErrorCode.InvalidApiVersion);
    }

    /**
     * 通用：无效请求签名。
     *
     * @return
     */
    public static Object invalidSign(){
        return failed(ErrorCode.InvalidSign);
    }

    /**
     * 通用：无效请求签名方式。
     *
     * @return
     */
    public static Object invalidSignMethod(){
        return failed(ErrorCode.InvalidSignMethod);
    }

    /**
     * 特定业务领域专用：缺失参数。
     *
     * @param parameterName
     * @return
     */
    public static Object missParameter(String parameterName){
        return failed(ErrorCode.MissParameter, parameterName);
    }

    /**
     * 通用：无效参数
     * @param parameterName
     * @return
     */
    public static Object invalidParameter(String parameterName){
        return failed(ErrorCode.InvalidParameter, parameterName);
    }

    /**
     * 通用：App Token过期
     * @return
     */
    public static Object appTokenExpired(){
        return failed(ErrorCode.AppTokenExpired);
    }

    /**
     * 设置错误码对应的错误状态码
     *
     * @param response
     * @param statusCode 状态码
     * @return
     */
    public void setResponseStatus(HttpServletResponse response, Integer statusCode) {
        response.setStatus(statusCode);
    }
}
