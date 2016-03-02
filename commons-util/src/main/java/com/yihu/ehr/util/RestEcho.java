package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.io.Serializable;

/**
 * REST API return value, it's a json expression.
 * Created by Sand Wen on 2015.8.12.
 */
@ApiModel
public class RestEcho implements Serializable {
    private final static String CodeNode = "code";
    private final static String ResultNode = "result";
    private final static String MessageNode = "message";

    private ObjectNode root;

    public RestEcho() {
    }

    public RestEcho putResult(String fieldName, Object data) {
        root.with(ResultNode).put(fieldName, data.toString());

        return this;
    }

    public RestEcho putResultToList(Object data) {
        //root.with(ResultNode).put(fieldName, data.toString());
        this.root.put(ResultNode, data.toString());
        return this;
    }

    public RestEcho putResult(final String subJson) {
        try {
            this.root.set(ResultNode, new ObjectMapper().readValue(subJson, ObjectNode.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public RestEcho putResult(final JsonNode subJson) {
        this.root.set(ResultNode, subJson);

        return this;
    }

    public RestEcho putMessage(String message) {
        this.root.put(MessageNode, message);

        return this;
    }

    @ApiModelProperty(required = true, readOnly = true, dataType = "String", allowableValues = "ok/null/ErrorCode", value = "����������롣��ȷʱΪok��null������ʱΪ��Ӧ�Ĵ�����ʾ")
    public JsonNode getCode() {
        return root.get(CodeNode);
    }

    @ApiModelProperty(required = true, readOnly = true, dataType = "String", value = "���������Ϣ�������ʾ")
    public JsonNode getMessage() {
        return root.get(MessageNode);
    }

    @ApiModelProperty(required = true, readOnly = true, dataType = "String", value = "���ֵ��һ��JSON�ӽڵ㡣��������ʱΪnull")
    public JsonNode getResult() {
        return root.get(ResultNode);
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    public RestEcho success() {
        ObjectMapper objectMapper = new ObjectMapper();
        root = objectMapper.createObjectNode();
        root.put(CodeNode, "0");

        return this;
    }

//    public RestEcho failed(ErrorCode errorCode, String... args) {
//        XTextResource textResource = ServiceFactory.getService(Services.TextResource);
//        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//
//        root = objectMapper.createObjectNode();
//        root.put(CodeNode, errorCode.getErrorCode());
//        root.put(MessageNode, textResource.getErrorPhrase(errorCode, args));
//
//        return this;
//    }
}
