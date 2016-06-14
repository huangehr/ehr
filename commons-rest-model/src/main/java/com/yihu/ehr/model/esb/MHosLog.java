package com.yihu.ehr.model.esb;

import java.io.Serializable;

/**
 * @author linaz
 * @created 2016.05.12 18:03
 */
public class MHosLog implements Serializable {

    String id;
    String orgCode;
    String ip;
    String filePath;
    String uploadTime;
    /**
     * modify linz
     * 添加组织名称字段，用于界面展示
     */
    String orgName;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}


