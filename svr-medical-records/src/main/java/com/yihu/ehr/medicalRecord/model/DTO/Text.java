package com.yihu.ehr.medicalRecord.model.DTO;

import com.yihu.ehr.medicalRecord.family.DocumentFamily;
import com.yihu.ehr.medicalRecord.family.TextFamily;

import java.util.Date;

/**
 * Created by hzp on 2016/8/1.
 */
public class Text {
    private String rowkey;
    private String content;
    private Date createTime;
    private String creater;
    private String createrName;
    private String patientId;
    private String patientName;
    private String businessClass;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
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

    public String getBusinessClass() {
        return businessClass;
    }

    public void setBusinessClass(String businessClass) {
        this.businessClass = businessClass;
    }

    /**
     * 获取列名集合
     */
    public String[] getColumns() {
        return new String[]{
                TextFamily.DataColumns.Content,
                TextFamily.DataColumns.Creater,
                TextFamily.DataColumns.CreaterName,
                TextFamily.DataColumns.CreateTime,
                TextFamily.DataColumns.PatientId,
                TextFamily.DataColumns.PatientName,
                TextFamily.DataColumns.BusinessClass
        };
    }

    /**
     * 获取列值集合
     */
    public Object[] getValues() {
        return new Object[]{
                getContent(),
                getCreater(),
                getCreaterName(),
                getCreateTime(),
                getPatientId(),
                getPatientName(),
                getBusinessClass()
        };
    }
}
