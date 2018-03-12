package com.yihu.ehr.model.archivesecurity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public class MArchiveSecuritySetting {

    long id;
    String userId;
    String securityKey;
    String archiveDefaultStatus;
    Date modifyTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getArchiveDefaultStatus() {
        return archiveDefaultStatus;
    }

    public void setArchiveDefaultStatus(String archiveDefaultStatus) {
        this.archiveDefaultStatus = archiveDefaultStatus;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}