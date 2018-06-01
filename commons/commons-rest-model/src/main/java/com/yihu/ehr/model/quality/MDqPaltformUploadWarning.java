package com.yihu.ehr.model.quality;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 数据质量-平台上传预警值
 * @author yeshijie on 2018/5/29.
 */
public class MDqPaltformUploadWarning implements Serializable {

    private Long id;
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String updateUserId;//操作人id
    private String updateUserName;//操作人名称
    private String integrityRate;//完整率
    private Long errorNum;//错误量
    private Long dataNum;//数据量
    private List<MDqDatasetWarning> datasetWarningList;//数据集

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getIntegrityRate() {
        return integrityRate;
    }

    public void setIntegrityRate(String integrityRate) {
        this.integrityRate = integrityRate;
    }

    public Long getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Long errorNum) {
        this.errorNum = errorNum;
    }

    public Long getDataNum() {
        return dataNum;
    }

    public void setDataNum(Long dataNum) {
        this.dataNum = dataNum;
    }

    public List<MDqDatasetWarning> getDatasetWarningList() {
        return datasetWarningList;
    }

    public void setDatasetWarningList(List<MDqDatasetWarning> datasetWarningList) {
        this.datasetWarningList = datasetWarningList;
    }
}
