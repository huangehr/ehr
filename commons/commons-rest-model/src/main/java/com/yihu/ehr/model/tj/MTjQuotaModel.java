package com.yihu.ehr.model.tj;

/**
 * Created by Administrator on 2017/6/12.
 */
public class MTjQuotaModel {
    private Long id;
    private String code;
    private String name;
    private String cron;
    private String execType;
    private String execTime;
    private String jobClazz;
    private String createTime;
    private String createUser;
    private String createUserName;
    private String updateTime;
    private String updateUser;
    private String updateUserName;
    private Integer status;
    private Integer dataLevel;
    private String remark;
    private MTjQuotaDataSourceModel tjQuotaDataSourceModel;
    private MTjQuotaDataSaveModel tjQuotaDataSaveModel;
    private String execTypeName;
    private String statusName;
    private String dataLevelName;
    private Integer quotaType;
    private String quotaTypeName;
    private String metadataCode;
    private String isInitExec; // 是否初始执行过，0：否，1：是。
    private String jobClazzName;
    private String resultGetType;
    //周期指标执行状态：0未开启，1执行中
    private String jobStatus;

    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    public Integer getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(Integer quotaType) {
        this.quotaType = quotaType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public String getJobClazz() {
        return jobClazz;
    }

    public void setJobClazz(String jobClazz) {
        this.jobClazz = jobClazz;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(Integer dataLevel) {
        this.dataLevel = dataLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public MTjQuotaDataSourceModel getTjQuotaDataSourceModel() {
        return tjQuotaDataSourceModel;
    }

    public void setTjQuotaDataSourceModel(MTjQuotaDataSourceModel tjQuotaDataSourceModel) {
        this.tjQuotaDataSourceModel = tjQuotaDataSourceModel;
    }

    public MTjQuotaDataSaveModel getTjQuotaDataSaveModel() {
        return tjQuotaDataSaveModel;
    }

    public void setTjQuotaDataSaveModel(MTjQuotaDataSaveModel tjQuotaDataSaveModel) {
        this.tjQuotaDataSaveModel = tjQuotaDataSaveModel;
    }

    public String getExecTypeName() {
        return execTypeName;
    }

    public void setExecTypeName(String execTypeName) {
        this.execTypeName = execTypeName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDataLevelName() {
        return dataLevelName;
    }

    public void setDataLevelName(String dataLevelName) {
        this.dataLevelName = dataLevelName;
    }

    public String getMetadataCode() {
        return metadataCode;
    }

    public void setMetadataCode(String metadataCode) {
        this.metadataCode = metadataCode;
    }

    public String getIsInitExec() {
        return isInitExec;
    }

    public void setIsInitExec(String isInitExec) {
        this.isInitExec = isInitExec;
    }

    public String getJobClazzName() {
        return jobClazzName;
    }

    public void setJobClazzName(String jobClazzName) {
        this.jobClazzName = jobClazzName;
    }

    public String getResultGetType() {
        return resultGetType;
    }

    public void setResultGetType(String resultGetType) {
        this.resultGetType = resultGetType;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
