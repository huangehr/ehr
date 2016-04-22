package com.yihu.ehr.agModel.template;

import com.yihu.ehr.util.validate.Required;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.04.15
 */
public class TemplateModel {
    private int id;
    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String cdaDocumentName;
    private String organizationCode;
    private String organizationName;
    private String pcTplURL;
    private String mobileTplURL;
    private String createTime;
    private String province;
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Required(filedName = "模版名称")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Required(filedName = "版本号")
    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    @Required(filedName = "cda文档")
    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

    @Required(filedName = "医疗机构")
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMobileTplURL() {
        return mobileTplURL;
    }

    public void setMobileTplURL(String mobileTplURL) {
        this.mobileTplURL = mobileTplURL;
    }

    public String getCdaDocumentName() {
        return cdaDocumentName;
    }

    public void setCdaDocumentName(String cdaDocumentName) {
        this.cdaDocumentName = cdaDocumentName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
