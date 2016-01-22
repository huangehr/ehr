package com.yihu.ehr.adaption.service;

import com.yihu.ha.dict.model.common.AdapterType;
import com.yihu.ha.geography.model.XAddress;
import com.yihu.ha.organization.model.XOrganization;

/** 适配管理映射机构
 * Created by zqb on 2015/11/18.
 */
public class AdapterOrg implements XAdapterOrg{
    private String code;
    private AdapterType type;
    private String name;
    private String description;
    private String parent;
    private XOrganization org;
    private XAddress area;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdapterType getType() {
        return type;
    }

    public void setType(AdapterType type) {
        this.type = type;
    }

    public XOrganization getOrg() {
        return org;
    }

    public void setOrg(XOrganization org) {
        this.org = org;
    }

    public XAddress getArea() {
        return area;
    }

    public void setArea(XAddress area) {
        this.area = area;
    }
}
