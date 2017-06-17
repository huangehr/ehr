package com.yihu.quota.model.jpa;// default package

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjQuota entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_quota")
public class TjQuota implements java.io.Serializable {

	// Fields

	private Integer id;
	private String code;
	private String name;
	private String cron;//quartz时间表达式
	private String execType;
	private String jobClazz; //类class
	private String dataLevel;//1 全量  2增量
	private Date createTime;
	private String createUser;
	private String createUserName;
	private Date updateTime;
	private String updateUser;
	private String updateUserName;
	private String status;//1: 正常 0：不可用  -1删除
	private String remark;

	// Constructors

	/** default constructor */
	public TjQuota() {
	}

	/** minimal constructor */
	public TjQuota(Date createTime, Date updateTime) {
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	/** full constructor */
	public TjQuota(String code, String name, String jobClazz,
				   Date createTime, String createUser, String createUserName,
				   Date updateTime, String updateUser, String updateUserName,
				   String status, String remark,String dataLevel ,String cron ,String execType) {
		this.code = code;
		this.name = name;
		this.jobClazz = jobClazz;
		this.createTime = createTime;
		this.createUser = createUser;
		this.createUserName = createUserName;
		this.updateTime = updateTime;
		this.updateUser = updateUser;
		this.updateUserName = updateUserName;
		this.status = status;
		this.remark = remark;
		this.dataLevel = dataLevel;
		this.cron = cron;
		this.execType = execType;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "code", length = 100)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name", length = 200)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "job_clazz", length = 500)
	public String getJobClazz() {
		return this.jobClazz;
	}

	public void setJobClazz(String jobClazz) {
		this.jobClazz = jobClazz;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false, length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "create_user", length = 100)
	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Column(name = "create_user_name", length = 50)
	public String getCreateUserName() {
		return this.createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", nullable = false, length = 0)
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "update_user", length = 100)
	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@Column(name = "update_user_name", length = 50)
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	@Column(name = "status", length = 1)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "remark", length = 1500)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "cron", length = 100)
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	@Column(name = "data_level", length = 2)
	public String getDataLevel() {
		return dataLevel;
	}

	public void setDataLevel(String dataLevel) {
		this.dataLevel = dataLevel;
	}

	@Column(name = "exec_type", length = 1)
	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}
}