package com.yihu.ehr.std.model;

/**
 * Created by AndyCai on 2015/12/15.
 */
public class CDATreeModel {
    private String id;
    private String code;
    private String name;
    //0:cda类别 1：cda
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
