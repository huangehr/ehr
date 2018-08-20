package com.yihu.ehr.model.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * APP Model。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

public class MApp implements Serializable {

    private String id;
    private String name;
    private String secret;
    private String url;
    private String outUrl;
    private String creator;
    private String auditor;
    private Date createTime;
    private Date auditTime;
    private String catalog;
    private String status;
    private String description;
    private String tags;
    private String org;
    private String code;
    private int sourceType;
    private String icon;
    private int releaseFlag;
    private String manageType; // 管理类型，dictId=94

    private String roleType; // 是否授权，用于医疗云平台首页，授权状态控制 0 - 默认未授权， 1 - 已授权


    /**
     * 医生工作站的应用分类,与catalog并无父子关系，1、在线学习，2在线服务
     */
    private String doctorManageType;


    public MApp(){
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getReleaseFlag() {
        return releaseFlag;
    }
    public void setReleaseFlag(int releaseFlag) {
        this.releaseFlag = releaseFlag;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public int getSourceType() {
        return sourceType;
    }
    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getOutUrl() {
        return outUrl;
    }
    public void setOutUrl(String outUrl) {
        this.outUrl = outUrl;
    }

    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuditor() {
        return auditor;
    }
    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getAuditTime() {
        return auditTime;
    }
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        List<String> list = new ArrayList<>();
        if(StringUtils.isEmpty(tags)){
        }else {
            String[] arr = tags.split(";|；");
            list = Arrays.asList(arr);
        }
        return list;
    }
    public void setTags(List<String> tags) {
        if(tags.size()>0){
            this.tags = StringUtils.join(tags.toArray(),";");
        }else {
            this.tags = "";
        }
    }

    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }

    public String getManageType() {
        return manageType;
    }
    public void setManageType(String manageType) {
        this.manageType = manageType;
    }

    public String getDoctorManageType() {
        return doctorManageType;
    }

    public void setDoctorManageType(String doctorManageType) {
        this.doctorManageType = doctorManageType;
    }
}