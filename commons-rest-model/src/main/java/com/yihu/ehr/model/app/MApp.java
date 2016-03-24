package com.yihu.ehr.model.app;

import org.apache.commons.lang.StringUtils;

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
    private String creator;
    private String auditor;
    private Date createTime;
    private Date auditTime;
    private String catalog;
    private String status;
    private String description;
    private String tags;

	public MApp(){
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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
        if(org.springframework.util.StringUtils.isEmpty(tags)){
            return null;
        }else {
            String[] arr = tags.split("  ");
            List<String> list = Arrays.asList(arr);
            return list;
        }

    }
    public void setTags(List<String> tags) {
        if(tags.size()>0){
            this.tags = StringUtils.join(tags.toArray(),"  ");
        }else {
            this.tags = "";
        }

    }
}