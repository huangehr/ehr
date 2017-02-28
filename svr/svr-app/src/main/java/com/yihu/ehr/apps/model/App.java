package com.yihu.ehr.apps.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "apps")
@Access(value = AccessType.FIELD)
public class App {
    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
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
    private String org;
    private String code;
    private int sourceType;

    @Column(name = "icon", nullable = true)
    private String icon;
    @Column(name = "release_flag", nullable = true)
    private int releaseFlag;

    @Column(name = "source_type", nullable = true)
    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
    @Column(name = "code", nullable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public App() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "secret", nullable = true)
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Column(name = "url", nullable = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "creator", nullable = true)
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "auditor", nullable = true)
    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Column(name = "create_time", nullable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "audit_time", nullable = true)
    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @Column(name = "catalog", nullable = true)
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Column(name = "status", nullable = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "tags", nullable = true)
    public List<String> getTags() {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(tags)) {
        } else {
            String[] arr = tags.split(";|；");
            list = Arrays.asList(arr);
        }
        return list;
    }

    public void setTags(List<String> tags) {
        if (tags.size() > 0) {
            this.tags = StringUtils.join(tags.toArray(), ";");
        } else {
            this.tags = "";
        }
    }

    @Column(name = "org", nullable = true)
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
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
}