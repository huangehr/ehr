package com.yihu.ehr.apps.service;

import com.yihu.ehr.util.ObjectVersion;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * APP对象。
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "apps")
@Access(value = AccessType.PROPERTY)
public class App {
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
    private Set<String> tags = new HashSet<>();

	public App(){
        id  = new ObjectVersion().toString();
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "name",  nullable = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "secret",  nullable = true)
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Column(name = "url",  nullable = true)
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "creator",  nullable = true)
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "auditor",  nullable = true)
    public String getAuditor() {
        return auditor;
    }
    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Column(name = "create_time",  nullable = true)
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "audit_time",  nullable = true)
    public Date getAuditTime() {
        return auditTime;
    }
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @Column(name = "catalog",  nullable = true)
    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Column(name = "status",  nullable = true)
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "description",  nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "tags",  nullable = true)
    public String getTags() {
        return String.join(",", tags);
    }
    public void setTags(String tags) {
        if(tags == null) return;
        String[] tagToken = tags.split(",");
        for (String token: tagToken){
            token = token.trim();
            if(token.length() == 0) continue;
            this.tags.add(token);
        }
    }
    public void addTag(String tag) {
        if(tag == null || tag.length() == 0) return;
        if (tags.contains(tag)) return;
        tags.add(tag);
    }
//    public void removeTag(String tag) {
//        this.tags.remove(tag);
//    }
}