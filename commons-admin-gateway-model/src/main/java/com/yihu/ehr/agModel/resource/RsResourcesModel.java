package com.yihu.ehr.agModel.resource;

/**
 * Created by hzp on 2016/5/4.
 * 资源列表
 */
public class RsResourcesModel {
    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String rsInterface;
    private String grantType;
    private String description;

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

    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRsInterface()
    {
        return rsInterface;
    }
    public void setRsInterface(String rsInterface)
    {
        this.rsInterface = rsInterface;
    }

    public String getGrantType()
    {
        return grantType;
    }
    public void setGrantType(String grantType)
    {
        this.grantType = grantType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
