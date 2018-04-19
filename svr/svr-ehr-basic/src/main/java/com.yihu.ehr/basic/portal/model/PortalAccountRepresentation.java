package com.yihu.ehr.basic.portal.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseEmploy entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "portal_account_representation",schema = "",catalog = "healtharchive")
public class PortalAccountRepresentation implements Serializable {

	// Fields
	private Long id;
	private String code;
	private Integer status; //审批状态 0待审核1已审核
	private String eName; //名字
	private String eMobile; //手机号码
	private String eIdcard;//身份证
	private Integer type;//申述类型1手机号码变更 2重置密码 3其他
	private String typeContent;//问题说明
	private String photo1;//图片1
	private String photo2;//图片2
	private String photo3;//图片3
	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "create_time", nullable = false, length = 0,updatable = false)
	private Date createTime;

	@CreatedBy
	@Column(name = "create_user",updatable = false)
	private String createUser;

	@CreatedBy
	@Column(name = "create_user_name",updatable = false)
	private String createUserName;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "update_time", nullable = false, length = 0)
	private Date updateTime;

	@LastModifiedBy
	@Column(name = "update_user")
	private String updateUser;

	@LastModifiedBy
	@Column(name = "update_user_name")
	private String updateUserName;

	// Constructors

	/** default constructor */
	public PortalAccountRepresentation() {
	}

	public PortalAccountRepresentation(String code, Integer status ,String eName, String eMobile, String eIdcard, Integer type, String typeContent, String photo1, String photo2, String photo3, Date createTime, String createUser, String createUserName, Date updateTime, String updateUser, String updateUserName) {
		this.code = code;
		this.status = status;
		this.eName = eName;
		this.eMobile = eMobile;
		this.eIdcard = eIdcard;
		this.type = type;
		this.typeContent = typeContent;
		this.photo1 = photo1;
		this.photo2 = photo2;
		this.photo3 = photo3;
		this.createTime = createTime;
		this.createUser = createUser;
		this.createUserName = createUserName;
		this.updateTime = updateTime;
		this.updateUser = updateUser;
		this.updateUserName = updateUserName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "e_name", length = 50)
	public String geteName() {
		return eName;
	}

	public void seteName(String eName) {
		this.eName = eName;
	}

	@Column(name = "e_mobile", length = 50)
	public String geteMobile() {
		return eMobile;
	}

	public void seteMobile(String eMobile) {
		this.eMobile = eMobile;
	}

	@Column(name = "e_idcard", length = 50)
	public String geteIdcard() {
		return eIdcard;
	}

	public void seteIdcard(String eIdcard) {
		this.eIdcard = eIdcard;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "type_content", length = 300)
	public String getTypeContent() {
		return typeContent;
	}

	public void setTypeContent(String typeContent) {
		this.typeContent = typeContent;
	}

	@Column(name = "photo_1", length = 200)
	public String getPhoto1() {
		return photo1;
	}

	public void setPhoto1(String photo1) {
		this.photo1 = photo1;
	}

	@Column(name = "photo_2", length = 200)
	public String getPhoto2() {
		return photo2;
	}

	public void setPhoto2(String photo2) {
		this.photo2 = photo2;
	}

	@Column(name = "photo_3", length = 200)
	public String getPhoto3() {
		return photo3;
	}

	public void setPhoto3(String photo3) {
		this.photo3 = photo3;
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
}