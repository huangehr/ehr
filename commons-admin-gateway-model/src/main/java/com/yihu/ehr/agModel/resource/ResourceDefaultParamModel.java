package com.yihu.ehr.agModel.resource;

/**
 * Created by yww on 2016/7/20.
 */
public class ResourceDefaultParamModel {
    private long id;
    private String resourcesId;
    private String resourcesCode;
    private String paramKey;
    private String paramValue;

    public ResourceDefaultParamModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(String resourcesId) {
        this.resourcesId = resourcesId;
    }

    public String getResourcesCode() {
        return resourcesCode;
    }

    public void setResourcesCode(String resourcesCode) {
        this.resourcesCode = resourcesCode;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
