package com.yihu.ehr.model.app;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.user.MUser;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * APP对象。
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

public class MApp implements Serializable {
    private String id;
    private String name;
    private String secret;
    private String url;
    private MUser creator;
    private MUser auditor;
    private Date createTime;
    private Date auditTime;
    private MConventionalDict catalog;
    private MConventionalDict status;
    private String description;
    private Set<String> tags = new HashSet<>();

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

    public MUser getCreator() {
        return creator;
    }

    public void setCreator(MUser creator) {
        this.creator = creator;
    }

    public MUser getAuditor() {
        return auditor;
    }

    public void setAuditor(MUser auditor) {
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

    public MConventionalDict getCatalog() {
        return catalog;
    }

    public void setCatalog(MConventionalDict catalog) {
        this.catalog = catalog;
    }

    public MConventionalDict getStatus() {
        return status;
    }

    public void setStatus(MConventionalDict status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}