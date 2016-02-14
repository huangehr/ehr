package com.yihu.ehr.standard.cda.service;

/**
 * Created by Administrator on 2015/12/31.
 */
public class CDAVersionModel {

    private String author;
    private String commitTime;
    //private Date commitTime;
    //private int stage;
    private Boolean stage;
    private String version;
    private String versionName;
    private String baseVersion;

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}
    public void setStage(Boolean stage) {this.stage = stage;}
    public Boolean getStage() {return stage;}
    //public void setStage(int stage) {this.stage = stage;}
    //public int getStage() {return stage;}
    public String getCommitTime() {return commitTime;}
    public void setCommitTime(String commitTime) {this.commitTime = commitTime;}
    //public Date getCommitTime() {return commitTime;}
    //public void setCommitTime(Date commitTime) {this.commitTime = commitTime;}
    public String getVersionName() {return versionName;}
    public void setVersionName(String versionName) {this.versionName = versionName;}
    public void setBaseVersion(String baseVersion) {this.baseVersion = baseVersion;}
    public String getBaseVersion() {return baseVersion;}
    public void setVersion(String version) {this.version = version;}
    public String getVersion() {return version;}
}
