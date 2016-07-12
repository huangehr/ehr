package com.yihu.ehr.model.profile;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 11:09
 */
public class MTemplate {
    private int id;
    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String organizationCode;
    private String pcTplURL;
    private String mobileTplURL;
    private String cdaType;
    private Date createTime = new Date();
    private String cdaCode;

    public String getCdaCode() {
        return cdaCode;
    }

    public void setCdaCode(String cdaCode) {
        this.cdaCode = cdaCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getPcTplURL() {
        return pcTplURL;
    }

    public void setPcTplURL(String pcTplURL) {
        this.pcTplURL = pcTplURL;
    }

    public String getCdaType() {
        return cdaType;
    }

    public void setCdaType(String cdaType) {
        this.cdaType = cdaType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMobileTplURL() {
        return mobileTplURL;
    }

    public void setMobileTplURL(String mobileTplURL) {
        this.mobileTplURL = mobileTplURL;
    }
}
