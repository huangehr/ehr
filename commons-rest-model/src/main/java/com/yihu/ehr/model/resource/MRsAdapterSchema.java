package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/17.
 */
public class MRsAdapterSchema {
    private String id;
    private String type;
    private String name;
    private String code;
    private String adapterVersion;
    private String description;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getAdapterVersion() {
        return adapterVersion;
    }
    public void setAdapterVersion(String adapterVersion) {
        this.adapterVersion = adapterVersion;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
