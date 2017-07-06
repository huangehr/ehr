package com.yihu.ehr.agModel.resource;

/**
 * Created by zdm on 2017/6/15
 */

public class RsRolesResourceModel {
    private String id;
    private String rolesId;
    private String resourceId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getRolesId() {
        return rolesId;
    }

    public void setRolesId(String rolesId) {
        this.rolesId = rolesId;
    }

    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
