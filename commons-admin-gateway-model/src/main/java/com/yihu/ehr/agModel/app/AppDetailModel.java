package com.yihu.ehr.agModel.app;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yww on 2016/2/23.
 */
public class AppDetailModel {
    private String id;
    private String name;
    private String secret;
    private String url;
    private String creator;
    private String auditor;
    private String createTime;
    private String auditTime;
    private String catalog;
    private String catalogName;
    private String status;
    private String statusName;
    private String description;
    private String tags;

    private String org;
    private String orgName;

    private String code;

    private int sourceType;

    private String sourceTypeName;

    public String getSourceTypeName() {
        return sourceTypeName;
    }

    public void setSourceTypeName(String sourceTypeName) {
        this.sourceTypeName = sourceTypeName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
