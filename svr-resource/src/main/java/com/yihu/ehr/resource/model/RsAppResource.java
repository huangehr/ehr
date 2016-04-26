package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by lyr on 2016/4/26.
 */
@Entity
@Table(name="rs_app_resource")
public class RsAppResource {
    private String id;
    private String appId;
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

    @Column(name="app_id",nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name="resource_id",nullable = false)
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
