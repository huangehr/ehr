package com.yihu.ehr.model.standard;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
public class MDispatchLog {
    private String id;
    private String orgId;
    private String stdVersionId;
    private Date dispatchTime;
    private String filePath;
    private String fileGroup;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStdVersionId() {
        return stdVersionId;
    }

    public void setStdVersionId(String stdVersionId) {
        this.stdVersionId = stdVersionId;
    }

    public Date getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(Date dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passsword) {
        this.password = passsword;
    }
}
