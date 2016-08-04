package com.yihu.ehr.medicalRecords.model.DTO;

import com.yihu.ehr.medicalRecords.family.DocumentFamily;

/**
 * Created by hzp on 2016/8/1.
 */
public class Document {
    private String rowkey;
    private String documentName;
    private String createTime;
    private String creater;
    private String createrName;
    private String patientId;
    private String patientName;
    private String documentContent;
    private String fileType;
    private String fileUrl;
    private String dataFrom;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    /**
     * 获取列名集合
     */
    public String[] getColumns() {
        return new String[]{
                DocumentFamily.DataColumns.DocumentName,
                DocumentFamily.DataColumns.CreateTime,
                DocumentFamily.DataColumns.Creater,
                DocumentFamily.DataColumns.CreaterName,
                DocumentFamily.DataColumns.PatientId,
                DocumentFamily.DataColumns.PatientName,
                DocumentFamily.DataColumns.DocumentContent,
                DocumentFamily.DataColumns.FileType,
                DocumentFamily.DataColumns.FileUrl,
                DocumentFamily.DataColumns.DataFrom
        };
    }

    /**
     * 获取列值集合
     */
    public Object[] getValues() {
        return new Object[]{
                getDocumentName(),
                getCreateTime(),
                getCreater(),
                getCreaterName(),
                getPatientId(),
                getPatientName(),
                getDocumentContent(),
                getFileType(),
                getFileUrl(),
                getDataFrom()
        };
    }
}
