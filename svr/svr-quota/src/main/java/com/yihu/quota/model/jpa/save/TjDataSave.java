package com.yihu.quota.model.jpa.save;// default package

import java.util.Date;
import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjDataSave entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_data_save")
public class TjDataSave implements java.io.Serializable {

	public static String type_es ="1";
	public static String type_solr="2";
	public static String type_mysql ="3";
	// Fields

	private Integer id;
	private String code;
	private String name;
	private String type;//1:mysql数据库 2:redis 3ES
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
	public TjDataSave() {
	}

	/** minimal constructor */
	public TjDataSave(Date createTime, Date updateTime) {
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	/** full constructor */
	public TjDataSave(String code, String name, String type,
			Date createTime, String createUser, String createUserName,
			Date updateTime, String updateUser, String updateUserName,
			String status, String remark) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.createTime = createTime;
		this.createUser = createUser;
		this.createUserName = createUserName;
		this.updateTime = updateTime;
		this.updateUser = updateUser;
		this.updateUserName = updateUserName;
		this.status = status;
		this.remark = remark;
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

	@Column(name = "type", length = 2)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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

}