package com.yihu.ehr.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.StringBuilderUtil;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

/**
 * API 异常。使用错误代码初始化，并可接收用于补充错误消息的参数。
 * 用于描述错误代码的信息配置在各服务配置文件中，并由服务配置中心统一管理。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:05
 */
public class ApiException extends RuntimeException {
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    HttpStatus httpStatus;

    String message;
    String documentURL;
    Object errors;

    private ErrorCode errorCode;
    private String[] args;

    public ApiException(HttpStatus httpStatus, String message, String documentURL, Object errors, ErrorCode errorCode, String... args){
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.documentURL = documentURL;
        this.errors = errors;

        this.errorCode = errorCode;
        this.args = args;
    }

    public ApiException(HttpStatus httpStatus, String message, String documentURL, Object errors){
        this.httpStatus = httpStatus;
        this.message = message;
        this.documentURL = documentURL;
        this.errors = errors;
    }

    public ApiException(HttpStatus httpStatus, String message, String documentURL){
        this.httpStatus = httpStatus;
        this.message = message;
        this.documentURL = documentURL;
    }

    public ApiException(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString(){
        Environment environment = SpringContext.getService(Environment.class);
        String description = environment.getProperty(errorCode.toString());

        if (null != description && null != args && args.length > 0){
            StringBuilderUtil util = new StringBuilderUtil(description);
            for (int i = 0; i < args.length; ++i){
                util.replace("{" + i + "}", args[i]);
            }

            description = util.toString();
        }

        return description;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (message != null) objectNode.put("message", message);
        if (documentURL != null) objectNode.put("document_url", documentURL);
        if (errors != null) objectNode.put("errors", objectMapper.writeValueAsString(errors));


        return objectNode.toString();
    }
}
