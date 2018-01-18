package com.yihu.ehr.agModel.tj;

/**
 * Created by Administrator on 2017/6/13.
 */
public class TjQuotaModel {
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
    private Integer quotaType;
    private String quotaTypeName;
    private String jobClazzName;
    private String isInitExec; // 是否初始执行过，0：否，1：是。
    private String resultGetType;

    private TjQuotaDataSourceModel tjQuotaDataSourceModel;
    private TjQuotaDataSaveModel tjQuotaDataSaveModel;
    private TjQuotaChartModel tjQuotaChartModel;

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

    public TjQuotaDataSourceModel getTjQuotaDataSourceModel() {
        return tjQuotaDataSourceModel;
    }

    public void setTjQuotaDataSourceModel(TjQuotaDataSourceModel tjQuotaDataSourceModel) {
        this.tjQuotaDataSourceModel = tjQuotaDataSourceModel;
    }

    public TjQuotaDataSaveModel getTjQuotaDataSaveModel() {
        return tjQuotaDataSaveModel;
    }

    public void setTjQuotaDataSaveModel(TjQuotaDataSaveModel tjQuotaDataSaveModel) {
        this.tjQuotaDataSaveModel = tjQuotaDataSaveModel;
    }

    public Integer getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(Integer quotaType) {
        this.quotaType = quotaType;
    }

    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    public TjQuotaChartModel getTjQuotaChartModel() {
        return tjQuotaChartModel;
    }

    public void setTjQuotaChartModel(TjQuotaChartModel tjQuotaChartModel) {
        this.tjQuotaChartModel = tjQuotaChartModel;
    }

    public String getJobClazzName() {
        return jobClazzName;
    }

    public void setJobClazzName(String jobClazzName) {
        this.jobClazzName = jobClazzName;
    }

    public String getIsInitExec() {
        return isInitExec;
    }

    public void setIsInitExec(String isInitExec) {
        this.isInitExec = isInitExec;
    }

    public String getResultGetType() {
        return resultGetType;
    }

    public void setResultGetType(String resultGetType) {
        this.resultGetType = resultGetType;
    }
}
