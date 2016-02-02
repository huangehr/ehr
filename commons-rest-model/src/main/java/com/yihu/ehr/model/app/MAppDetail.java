package com.yihu.ehr.model.app;

import com.yihu.ehr.model.dict.MBaseDict;

import java.util.Date;

/**
 * Created by AndyCai on 2016/2/2.
 */
public class MAppDetail {

    String id;
    String name;
    String secret;
    String url;
    String creator;
    String auditor;
    Date create_time;
    Date audit_time;
    MBaseDict catalog;
    String catalogValue;
    MBaseDict status;
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

    public MBaseDict getCatalog() {
        return catalog;
    }

    public void setCatalog(MBaseDict catalog) {
        this.catalog = catalog;
    }

    public String getCatalogValue() {
        return catalogValue;
    }

    public void setCatalogValue(String catalogValue) {
        this.catalogValue = catalogValue;
    }

    public MBaseDict getStatus() {
        return status;
    }

    public void setStatus(MBaseDict status) {
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
