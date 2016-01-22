package com.yihu.ehr.adaption.service;
import com.yihu.ha.geography.model.Address;

/**
 * Created by zqb on 2015/11/20.
 */
public class AdapterOrgModel {
    private String code;
    private String type;
    private String typeValue;
    private String name;
    private String description;
    private String parent;
    private String parentValue;
    private String org;
    private String orgValue;
    private Address area;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParentValue() {
        return parentValue;
    }

    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgValue() {
        return orgValue;
    }

    public void setOrgValue(String orgValue) {
        this.orgValue = orgValue;
    }

    public Address getArea() {
        return area;
    }

    public void setArea(Address area) {
        this.area = area;
    }
}
