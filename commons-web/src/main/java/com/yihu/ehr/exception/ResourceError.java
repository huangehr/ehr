package com.yihu.ehr.exception;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.27 15:14
 */
public class ResourceError {
    String resource;
    String field;
    String code;

    public ResourceError(String resource, String field, String code) {
        this.resource = resource;
        this.field = field;
        this.code = code;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
