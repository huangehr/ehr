package com.yihu.ehr.model.standard;


import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class MCDAVersion {
    private String author;
    private Date commitTime;

    private Boolean isInStage;
    private String version;
    private String versionName;
    private String baseVersion;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean isInStage() {
        return isInStage;
    }

    public void setInStage(Boolean inStage) {
        isInStage = inStage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }
}