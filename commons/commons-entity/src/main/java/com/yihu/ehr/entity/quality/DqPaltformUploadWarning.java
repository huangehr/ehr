package com.yihu.ehr.entity.quality;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 数据质量-平台上传预警值
 * @author yeshijie on 2018/5/28.
 */
@Entity
@Table(name = "dq_paltform_upload_warning", schema = "", catalog = "healtharchive")
public class DqPaltformUploadWarning {

    private Long id;
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String updateUserId;//操作人id
    private String updateUserName;//操作人名称
    private Long errorNum;//错误量
    private Long acrhiveNum;//档案数
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
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
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

    @Column(name = "error_num")
    public Long getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Long errorNum) {
        this.errorNum = errorNum;
    }

    @Column(name = "acrhive_num")
    public Long getAcrhiveNum() {
        return acrhiveNum;
    }

    public void setAcrhiveNum(Long acrhiveNum) {
        this.acrhiveNum = acrhiveNum;
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
