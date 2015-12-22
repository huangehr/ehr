package com.yihu.ehr.std.model;

import java.util.Date;

/**
 * Created by AndyCai on 2015/9/11.
 */
public class CDAVersionPageModel {
    private String author;
    private Date commitTime;

    private String version;
    private String baseVersion;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }
}
