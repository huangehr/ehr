package com.yihu.ehr.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sc_archive_security_setting")
@Access(value = AccessType.PROPERTY)
public class ArchiveSecuritySetting {

    long id;
    String userId;
    String securityKey;
    String archiveDefaultStatus;
    Date modifyTime;

    public ArchiveSecuritySetting() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_id", nullable = false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "security_key", nullable = true)
    public String getSecurityKey() {
        return securityKey;
    }
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @Column(name = "archive_default_status", nullable = true)
    public String getArchiveDefaultStatus() {
        return archiveDefaultStatus;
    }
    public void setArchiveDefaultStatus(String archiveDefaultStatus) {
        this.archiveDefaultStatus = archiveDefaultStatus;
    }

    @Column(name = "modify_time", nullable = true)
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}