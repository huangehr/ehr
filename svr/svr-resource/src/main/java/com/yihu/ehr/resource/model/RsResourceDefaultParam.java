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
public class RsResourceDefaultParam {

    private String id;
    private String resourcesId;
    private String resourcesCode;
    private String paramKey;
    private String paramValue;

    @Id
   // @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name="systemUUID", strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    @Column(name = "id", unique = true, nullable = false, length = 32)
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

    @Column(name = "resources_code", nullable = false)
    public String getResourcesCode() {
        return resourcesCode;
    }
    public void setResourcesCode(String resourcesCode) {
        this.resourcesCode = resourcesCode;
    }

    @Column(name = "param_key", nullable = false)
    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    @Column(name = "param_value",nullable = false)
    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
