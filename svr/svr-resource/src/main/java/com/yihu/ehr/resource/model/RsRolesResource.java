package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by zdm on 2017/6/15
 */
@Entity
@Table(name="rs_roles_resource")
public class RsRolesResource {
    private String id;
    private String rolesId;
    private String resourceId;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name="Generator",strategy="assigned")
    @Column(name="id",unique = true,nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Column(name="roles_id",nullable = false)
    public String getRolesId() {
        return rolesId;
    }
    public void setRolesId(String rolesId) {
        this.rolesId = rolesId;
    }

    @Column(name="resource_id",nullable = false)
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
