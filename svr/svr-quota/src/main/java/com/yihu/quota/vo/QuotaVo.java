package com.yihu.quota.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenweida on 2017/6/1.
 */
public class QuotaVo implements Serializable {
    private long serialVersionUID = 1L;

    private Integer id;
    private String code;
    private String compute;
    private String name;
    private String cron;//quartz时间表达式
    private String dataLevel;//1 全量  2增量 3 百分比
    private String jobClazz;
    private Date createTime;
    private String createUser;
    private String createUserName;
    private Date updateTime;
    private String updateUser;
    private String updateUserName;
    private String execType;
    private String status;
    private String remark;


    @Override
    public String toString() {
        return "QuotaVo{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", compute='" + compute + '\'' +
                ", name='" + name + '\'' +
                ", cron='" + cron + '\'' +
                ", dataLevel='" + dataLevel + '\'' +
                ", jobClazz='" + jobClazz + '\'' +
                ", createTime=" + createTime +
                ", createUser='" + createUser + '\'' +
                ", createUserName='" + createUserName + '\'' +
                ", updateTime=" + updateTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", execType='" + execType + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getJobClazz() {
        return jobClazz;
    }

    public void setJobClazz(String jobClazz) {
        this.jobClazz = jobClazz;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
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

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getCompute() {
        return compute;
    }

    public void setCompute(String compute) {
        this.compute = compute;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(String dataLevel) {
        this.dataLevel = dataLevel;
    }


}
