package com.yihu.ehr.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * API 异常。使用错误代码初始化，并可接收用于补充错误消息的参数。
 * 用于描述错误代码的信息配置在各服务配置文件中，并由服务配置中心统一管理。
 *
 * 错误描述结构，结构(字段errors对资源而言，REST规范错误不包含此结构)：
 * {
 *    "message": "Validation Failed",
 *    "document_url": "https://ehr.yihu.com/docs/api/somewhere"
 *    "errors": [
 *        {
 *            "resource": "User",
 *            "field": "title",
 *            "code": "missing_field"
 *        }
 *    ]
 * }
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:05
 */
public class ApiException extends RuntimeException {
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    HttpStatus httpStatus;
    ErrorCode errorCode;        // 用于从配置环境中提取错误信息
    String[] errorArgs;         // 格式化配置环境中的错误信息
    String documentURL;
    String message;             // 错误消息

    List<ResourceError> resourceErrors;         // 资源具体错误描述

    private ApiException(HttpStatus httpStatus, ErrorCode errorCode, String documentURL, List<ResourceError> resourceErrors, String... errorArgs){
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorArgs = errorArgs;
        this.documentURL = documentURL;

        this.resourceErrors = resourceErrors;
    }

    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String documentURL, String... errorArgs){
        this(httpStatus, errorCode, documentURL, null, errorArgs);
    }

    public ApiException(HttpStatus httpStatus, ErrorCode errorCode){
        this(httpStatus, errorCode, null, null, null);
    }

    public ApiException(HttpStatus httpStatus, String message){
        this(httpStatus, null, null, null, null);

        this.message = message;
    }

    public ApiException(ErrorCode errorCode, String... errorArgs) {
        this(HttpStatus.FORBIDDEN, errorCode, null, null, errorArgs);
    }

    // legacy support
    public ApiException(ErrorCode errorCode) {
        this(HttpStatus.FORBIDDEN, errorCode, null, null, null);
    }

    @Override
    public String toString(){
        try {
            return toJson();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String toJson() throws JsonProcessingException {
        Environment environment = SpringContext.getService(Environment.class);
        message = errorCode == null ? message : environment.getProperty(errorCode.getErrorCode());

        if (null != message && null != errorArgs && errorArgs.length > 0){
            StringBuilderEx util = new StringBuilderEx(message);
            for (int i = 0; i < errorArgs.length; ++i){
                util.replace("{" + i + "}", errorArgs[i]);
            }

            message = util.toString();
        }

        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("message", message != null ? message : "");
        objectNode.put("document_url", documentURL != null ? documentURL : "");

        if (resourceErrors != null) objectNode.put("errors", objectMapper.writeValueAsString(resourceErrors));

        return objectNode.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
