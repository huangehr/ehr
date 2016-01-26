package com.yihu.ehr.adaption.service;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.model.org.MOrganization;

/** 适配管理映射机构
 * Created by zqb on 2015/11/18.
 */
public class AdapterOrg {
    private String code;
    private MBaseDict type;
    private String name;
    private String description;
    private String parent;
    private MOrganization org;
    private MAddress area;

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

    public MBaseDict getType() {
        return type;
    }

    public void setType(MBaseDict type) {
        this.type = type;
    }

    public MOrganization getOrg() {
        return org;
    }

    public void setOrg(MOrganization org) {
        this.org = org;
    }

    public MAddress getArea() {
        return area;
    }

    public void setArea(MAddress area) {
        this.area = area;
    }
}
