package com.yihu.ehr.entity.organizations;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机构Saas授权
 * Created by hzp on 2017/05/15.
 */
@Entity
@Table(name="org_saas")
public class OrgSaas {

    protected Long id; // 非业务主键
    private String orgCode; //机构代码
    private String type;// 授权类型 1区域 2机构
    private String saasCode;//区域代码或者机构代码
    private String saasName;//区域名称或者机构名称

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "saas_code")
    public String getSaasCode() {
        return saasCode;
    }

    public void setSaasCode(String saasCode) {
        this.saasCode = saasCode;
    }

    @Column(name = "saas_name")
    public String getSaasName() {
        return saasName;
    }

    public void setSaasName(String saasName) {
        this.saasName = saasName;
    }
}
