package com.yihu.ehr.model.resource;

/**
 * 资源字典
 *
 * Created by lyr on 2016/5/13.
 */
public class MResourceDefaultParam {

    private long id;
    private String resourcesId;
    private String resourcesCode;
    private String paramKey;
    private String paramValue;

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
