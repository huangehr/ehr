package com.yihu.ehr.apps.model;


import com.yihu.ehr.dict.service.AppCatalog;
import com.yihu.ehr.dict.service.AppStatus;
import com.yihu.ehr.model.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/16.
 */
public class AppDetailModel {

    String id;
    String name;
    String secret;
    String url;
    String creatorId;
    String auditor;
    Date create_time;
    Date audit_time;
    AppCatalog catalog;
    String catalogValue;
    AppStatus status;
    String statusValue;
    String description;
    String strTags;

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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getAudit_time() {
        return audit_time;
    }

    public void setAudit_time(Date audit_time) {
        this.audit_time = audit_time;
    }

    public AppCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(AppCatalog catalog) {
        this.catalog = catalog;
    }

    public String getCatalogValue() {
        return catalogValue;
    }

    public void setCatalogValue(String catalogValue) {
        this.catalogValue = catalogValue;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrTags() {
        return strTags;
    }

    public void setStrTags(String strTags) {
        this.strTags = strTags;
    }
}
