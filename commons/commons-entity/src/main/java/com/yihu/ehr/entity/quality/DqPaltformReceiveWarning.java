package com.yihu.ehr.entity.quality;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 平台接收预警值
 * @author yeshijie on 2018/5/28.
 */
@Entity
@Table(name = "dq_paltform_receive_warning", schema = "", catalog = "healtharchive")
public class DqPaltformReceiveWarning {

    private Long id;
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String updateUserId;//操作人id
    private String updateUserName;//操作人名称
    private Long archiveNum;//档案数
    private Long errorNum;//质量问题数
    private Integer hospitalInTime;//住院及时时间
    private Integer outpatientInTime;//门诊及时时间
    private Integer peInTime;//体检及时时间
    private String hospitalInTimeRate;//住院及时率
    private String outpatientInTimeRate;//门诊及时率
    private String peInTimeRate;//体检及时率
    private Integer datasetWarningNum;//数据集数量
    private List<DqDatasetWarning> datasetWarningList;//数据集

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Column(name = "create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "update_user_id")
    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Column(name = "update_user_name")
    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    @Column(name = "archive_num")
    public Long getArchiveNum() {
        return archiveNum;
    }

    public void setArchiveNum(Long archiveNum) {
        this.archiveNum = archiveNum;
    }

    @Column(name = "error_num")
    public Long getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Long errorNum) {
        this.errorNum = errorNum;
    }

    @Column(name = "hospital_in_time")
    public Integer getHospitalInTime() {
        return hospitalInTime;
    }

    public void setHospitalInTime(Integer hospitalInTime) {
        this.hospitalInTime = hospitalInTime;
    }

    @Column(name = "outpatient_in_time")
    public Integer getOutpatientInTime() {
        return outpatientInTime;
    }

    public void setOutpatientInTime(Integer outpatientInTime) {
        this.outpatientInTime = outpatientInTime;
    }

    @Column(name = "pe_in_time")
    public Integer getPeInTime() {
        return peInTime;
    }

    public void setPeInTime(Integer peInTime) {
        this.peInTime = peInTime;
    }

    @Column(name = "hospital_in_time_rate")
    public String getHospitalInTimeRate() {
        return hospitalInTimeRate;
    }

    public void setHospitalInTimeRate(String hospitalInTimeRate) {
        this.hospitalInTimeRate = hospitalInTimeRate;
    }

    @Column(name = "outpatient_in_time_rate")
    public String getOutpatientInTimeRate() {
        return outpatientInTimeRate;
    }

    public void setOutpatientInTimeRate(String outpatientInTimeRate) {
        this.outpatientInTimeRate = outpatientInTimeRate;
    }

    @Column(name = "pe_in_time_rate")
    public String getPeInTimeRate() {
        return peInTimeRate;
    }

    public void setPeInTimeRate(String peInTimeRate) {
        this.peInTimeRate = peInTimeRate;
    }

    @Transient
    public Integer getDatasetWarningNum() {
        return datasetWarningNum;
    }

    public void setDatasetWarningNum(Integer datasetWarningNum) {
        this.datasetWarningNum = datasetWarningNum;
    }

    @Transient
    public List<DqDatasetWarning> getDatasetWarningList() {
        return datasetWarningList;
    }

    public void setDatasetWarningList(List<DqDatasetWarning> datasetWarningList) {
        this.datasetWarningList = datasetWarningList;
    }
}
