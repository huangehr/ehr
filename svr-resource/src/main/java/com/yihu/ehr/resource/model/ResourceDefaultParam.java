package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 资源字典
 *
 * Created by lyr on 2016/5/13.
 */
@Entity
@Table(name="rs_resource_default_params")
public class ResourceDefaultParam {

    private String id;
    private String resourcesId;
    private String resourcesCode;
    private String key;
    private String value;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id",nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "resources_id",nullable = false)
    public String getResourcesId() {
        return resourcesId;
    }
    public void setResourcesId(String resourcesId) {
        this.resourcesId = resourcesId;
    }

    @Column(name = "resources_code",nullable = true)
    public String getResourcesCode() {
        return resourcesCode;
    }
    public void setResourcesCode(String resourcesCode) {
        this.resourcesCode = resourcesCode;
    }

    @Column(name = "param_key",nullable = true)
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    @Column(name = "param_value",nullable = true)
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
