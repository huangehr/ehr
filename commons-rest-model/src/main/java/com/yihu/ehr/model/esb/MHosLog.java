package com.yihu.ehr.model.esb;

import java.io.Serializable;

/**
 * @author linaz
 * @created 2016.05.12 18:03
 */
public class MHosLog implements Serializable {

    private String id;
    private String orgCode;
    private String ip;
    private String filePath;
    private String uploadTime;


    public MHosLog(String id, String orgCode, String ip, String filePath, String uploadTime) {
        this.id = id;
        this.orgCode = orgCode;
        this.ip = ip;
        this.filePath = filePath;
        this.uploadTime = uploadTime;
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


