package com.yihu.ehr.model.profile;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 11:09
 */
public class MTemplate implements Serializable {

    public enum Type {
        clinic, //门诊
        resident, //住院
        medicalExam //体检
    }

    private Integer id;
    private Date createDate;
    private String creator;
    private Date modifyDate;
    private String modifier;
    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String cdaDocumentName;
    private String pcUrl;
    private String mobileUrl;
    private String cdaCode;
    private Type type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
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

    public String getCdaDocumentName() {
        return cdaDocumentName;
    }

    public void setCdaDocumentName(String cdaDocumentName) {
        this.cdaDocumentName = cdaDocumentName;
    }

    public String getPcUrl() {
        return pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getCdaCode() {
        return cdaCode;
    }

    public void setCdaCode(String cdaCode) {
        this.cdaCode = cdaCode;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
