package com.yihu.ehr.agModel.orgSaas;

import java.io.Serializable;

/**
 * @author zdm
 * @version 1.0
 * @updated 26-05-2017
 */
public class AreaSaasModel implements Serializable {

    protected String id; // 非业务主键
    private String orgCode; //机构代码
    private String type;// 授权类型 1区域 2机构
    private String saasCode;//区域代码或者机构代码
    private String saasName;//区域名称或者机构名称
    private String parent_hos_id;//上级机构id
    private boolean ischecked;//是否选中
    private int level;//等级

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSaasCode() {
        return saasCode;
    }

    public void setSaasCode(String saasCode) {
        this.saasCode = saasCode;
    }

    public String getSaasName() {
        return saasName;
    }

    public void setSaasName(String saasName) {
        this.saasName = saasName;
    }

    public String getParent_hos_id() {
        return parent_hos_id;
    }

    public void setParent_hos_id(String parent_hos_id) {
        this.parent_hos_id = parent_hos_id;
    }

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}