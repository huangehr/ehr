package com.yihu.ehr.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;

import java.io.Serializable;

/**
 * REST API return value, it's a json expression.
 * Created by Sand Wen on 2015.8.12.
 */
public class ApiErrorEcho implements Serializable {
    private final static String MESSAGE_NODE = "message";
    private final static String DOCUMENTATION_URL = "documentation_url";

    private ObjectNode root;

    public ApiErrorEcho() {
    }

    public ApiErrorEcho(String message, String documentationUrl) {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");

        root = objectMapper.createObjectNode();
        root.put(MESSAGE_NODE, message);
        root.put(DOCUMENTATION_URL, documentationUrl);
    }

    public String getCode() {
        return root.get(DOCUMENTATION_URL).asText();
    }

    public String getMessage() {
        return root.get(MESSAGE_NODE).asText();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
