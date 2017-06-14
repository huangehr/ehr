package com.yihu.ehr.agModel.tj;

import java.util.Date;

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
    private TjQuotaDataSourceModel tjQuotaDataSourceModel;
    private TjQuotaDataSaveModel tjQuotaDataSaveModel;

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
}
