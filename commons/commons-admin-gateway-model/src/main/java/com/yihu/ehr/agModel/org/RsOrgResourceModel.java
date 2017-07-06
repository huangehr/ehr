package com.yihu.ehr.agModel.org;

/**
 * Created by zdm on 2017/6/15
 */

public class RsOrgResourceModel {
    private String id;
    private String organizationId;
    private String resourceId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
