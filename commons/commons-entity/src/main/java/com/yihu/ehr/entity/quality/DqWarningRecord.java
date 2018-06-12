package com.yihu.ehr.entity.quality;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yeshijie on 2018/6/11.
 */
@Entity
@Table(name = "dq_warning_record", schema = "", catalog = "healtharchive")
public class DqWarningRecord {

    private String id;
    private String type;//类型(1接收，2资源化，3上传)
    /**
     * 预警问题类型
     * 和type对应 type=1 warningType从100开始，type=2 warningType从200开始，type=3 warningType从300开始
     *
     */
    private String warningType;
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date warningTime;//预警时间
    private Date recordTime;//就诊（资源化，上传）时间
    private String status;//状态（1未解决，2已解决）
    private String quota;//指标
    private String actualValue;//实际值
    private String warningValue;//预警值
    private Date solveTime;//解决时间
    private String solveId;//解决人id
    private String solveName;//解决人姓名
    private String solveType;//解决方式（1已解决，2忽略，3无法解决，4不是问题）
    private String problemDescription;//问题描述

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "warning_type")
    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "warning_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(Date warningTime) {
        this.warningTime = warningTime;
    }

    @Column(name = "record_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "quota")
    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    @Column(name = "actual_value")
    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    @Column(name = "warning_value")
    public String getWarningValue() {
        return warningValue;
    }

    public void setWarningValue(String warningValue) {
        this.warningValue = warningValue;
    }

    @Column(name = "solve_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getSolveTime() {
        return solveTime;
    }

    public void setSolveTime(Date solveTime) {
        this.solveTime = solveTime;
    }

    @Column(name = "solve_id")
    public String getSolveId() {
        return solveId;
    }

    public void setSolveId(String solveId) {
        this.solveId = solveId;
    }

    @Column(name = "solve_name")
    public String getSolveName() {
        return solveName;
    }

    public void setSolveName(String solveName) {
        this.solveName = solveName;
    }

    @Column(name = "solve_type")
    public String getSolveType() {
        return solveType;
    }

    public void setSolveType(String solveType) {
        this.solveType = solveType;
    }

    @Column(name = "problem_description")
    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public enum DqWarningRecordWarningType {
        archives("档案数", "101"),
        errorNum("质量异常问题数", "102"),
        datasetWarningNum("数据集", "103"),
        outpatientInTimeRate("门诊及时率", "104"),
        hospitalInTimeRate("住院及时率", "105"),
        peInTimeRate("体检及时率", "106"),
        resourceFailureNum("解析失败数", "201"),
        resourceErrorNum("解析质量问题数", "202"),
        unArchiveNum("未解析量", "203"),
        archiveNum("档案数", "301"),
        dataErrorNum("数据错误问题数", "302"),
        uploadDatasetNum("数据集", "303");
        private String name;
        private String value;

        DqWarningRecordWarningType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum DqWarningRecordType {
        receive("平台接收", "1"),
        resource("资源化", "2"),
        upload("平台上传", "3");
        private String name;
        private String value;

        DqWarningRecordType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
