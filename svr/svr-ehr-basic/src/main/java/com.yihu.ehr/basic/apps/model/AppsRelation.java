package com.yihu.ehr.basic.apps.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "apps_relation", schema = "", catalog = "healtharchive")
public class AppsRelation {
    private int id;
    private String parentAppId;
    private String parentAppName;
    private String appId;
    private String appName;
    private Integer type;



    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "identity")
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    @Column(name = "parent_app_id", nullable = false)
    public String getParentAppId() {
        return parentAppId;
    }

    public void setParentAppId(String parentAppId) {
        this.parentAppId = parentAppId;
    }

    @Column(name = "app_id", nullable = false)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "type", nullable = false)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "app_name", nullable = false)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Column(name = "parent_app_name", nullable = true, insertable = true, updatable = true)
    public String getParentAppName() {
        return parentAppName;
    }

    public void setParentAppName(String parentAppName) {
        this.parentAppName = parentAppName;
    }

}