package com.yihu.ehr.resource.model;

/**
 * Created by Administrator on 2017/12/19.
 */
public class ResourceQuotaJson {
    private String resourceId;
    private Integer quotaId;
    private Integer pid;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }
}
