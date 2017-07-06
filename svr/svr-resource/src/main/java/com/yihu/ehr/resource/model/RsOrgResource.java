package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by zdm on 2017/6/15
 */
@Entity
@Table(name="rs_organization_resource")
public class RsOrgResource {
    private String id;
    private String organizationId;
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
    @Column(name="organization_id",nullable = false)
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }




    @Column(name="resource_id",nullable = false)
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
