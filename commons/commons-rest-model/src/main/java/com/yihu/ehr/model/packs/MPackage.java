package com.yihu.ehr.model.packs;

import com.yihu.ehr.constants.ArchiveStatus;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 16:11
 */
public class MPackage implements Serializable {

    private String id;
    private String pwd;
    private String remotePath;
    private Date receiveDate;
    private Date parseDate;
    private Date finishDate;
    private ArchiveStatus archiveStatus;
    private String message;
    private String orgCode;
    private String clientId;
    boolean resourced;
    private String md5;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRemotePath() {
        return remotePath;
    }
    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }
    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getParseDate() {
        return parseDate;
    }
    public void setParseDate(Date parseDate) {
        this.parseDate = parseDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public ArchiveStatus getArchiveStatus() {
        return archiveStatus;
    }
    public void setArchiveStatus(ArchiveStatus archiveStatus) {
        this.archiveStatus = archiveStatus;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getClientId() {
        return StringUtils.isEmpty(clientId) ? "kHAbVppx44" : clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isResourced() {
        return resourced;
    }
    public void setResourced(boolean resourced) {
        this.resourced = resourced;
    }

    public String getMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }

}
