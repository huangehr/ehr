package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.lang.SpringContext;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * REST API return value, it's a json expression.
 * Created by Sand Wen on 2015.8.12.
 */
@ApiModel
public class ApiErrorEcho implements Serializable {
    private final static String CodeNode = "code";
    private final static String MessageNode = "message";

    private ObjectNode root;

    public ApiErrorEcho() {
    }

    public ApiErrorEcho(ErrorCode errorCode, String errorDescription, String... args) {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");

        root = objectMapper.createObjectNode();
        root.put(CodeNode, errorCode.getErrorCode());
        root.put(MessageNode, errorDescription);
    }

    public ApiErrorEcho putMessage(String message) {
        this.root.put(MessageNode, message);

        return this;
    }

    @ApiModelProperty(required = true, readOnly = true, dataType = "String", allowableValues = "ok/null/ErrorCode", value = "操作结果代码。正确时为ok或null，错误时为相应的错误提示")
    public String getCode() {
        return root.get(CodeNode).asText();
    }

    @ApiModelProperty(required = true, readOnly = true, dataType = "String", value = "操作结果消息或错误提示")
    public String getMessage() {
        return root.get(MessageNode).asText();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
