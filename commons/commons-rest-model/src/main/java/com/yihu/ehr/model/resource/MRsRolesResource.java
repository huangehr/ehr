package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/4.
 */

public class MRsRolesResource {
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
