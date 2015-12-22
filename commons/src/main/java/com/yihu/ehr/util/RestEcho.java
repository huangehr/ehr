package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.lang.ServiceFactory;
import com.yihu.ehr.resources.XTextResource;
import com.yihu.ehr.util.log.LogService;

import java.io.IOException;
import java.io.Serializable;

/**
 * REST API return value, it's a json expression.
 * Created by Sand Wen on 2015.8.12.
 */
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
            LogService.getLogger(RestEcho.class).info(e.getMessage());
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

    public JsonNode getCode() {
        return root.get(CodeNode);
    }

    public JsonNode getMessage() {
        return root.get(MessageNode);
    }

    public JsonNode getResult() {
        return root.get(ResultNode);
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    public RestEcho success() {
        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
        root = objectMapper.createObjectNode();
        root.put(CodeNode, "0");

        return this;
    }

    public RestEcho failed(ErrorCode errorCode, String... args) {
        XTextResource textResource = ServiceFactory.getService(Services.TextResource);
        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);

        root = objectMapper.createObjectNode();
        root.put(CodeNode, errorCode.getErrorCode());
        root.put(MessageNode, textResource.getErrorPhrase(errorCode, args));

        return this;
    }
}
