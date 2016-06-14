package com.yihu.ehr.model.esb;// default package

import java.io.Serializable;
import java.util.Date;


/**
 * @author linaz
 * @created 2016.05.12 18:03
 */
public class MHosEsbMiniInstallLog implements Serializable {

    private String id;
    private String orgCode;
    private String systemCode;
    private String currentVersionName;
    private String currentVersionCode;
    private Date installTime;
    private String message;
    private String orgName;
    private String installDate;

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

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

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getCurrentVersionName() {
        return currentVersionName;
    }

    public void setCurrentVersionName(String currentVersionName) {
        this.currentVersionName = currentVersionName;
    }

    public String getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(String currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }

    public Date getInstallTime() {
        return installTime;
    }

    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}