package com.yihu.ehr.model.adaption;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MAdapterRelationship {
    long id;
    String code;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
