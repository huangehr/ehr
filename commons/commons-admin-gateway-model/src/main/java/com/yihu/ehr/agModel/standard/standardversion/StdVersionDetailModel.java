package com.yihu.ehr.agModel.standard.standardversion;

/**
 * Created by yww on 2016/3/2.
 */
public class StdVersionDetailModel {
    private String author;
    private String commitTime;
    private boolean isInStage;
    private String stageName;
    private String version;
    private String versionName;
    private String baseVersion;
    private String baseVersionName;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public boolean isInStage() {
        return isInStage;
    }

    public void setIsInStage(boolean isInStage) {
        this.isInStage = isInStage;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
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

    public String getBaseVersionName() {
        return baseVersionName;
    }

    public void setBaseVersionName(String baseVersionName) {
        this.baseVersionName = baseVersionName;
    }
}
