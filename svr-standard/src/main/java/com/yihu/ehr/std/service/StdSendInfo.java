package com.yihu.ehr.std.service;

import java.util.Date;

/**
 * 保存标准下发信息
 * @author AndyCai
 * @version 1.0
 * @created 23-7月-2015 15:22:30
 */
public class StdSendInfo {

	/**
	 * 创建时间(即初次下发时间)
	 */
	Date createDTime;
	/**
	 * 创建人(即首次下发人)
	 */
	String createUser;
	long Id;
	/**
	 * 下发机构ID(可以是机构ID，或者其他接收对象的ID)
	 */
	long OrgId;
	/**
	 * 标准版本ID(该机构变更版本时更新该栏位，不用保留机构历史版本信息，新版本标准可兼容老版本标准)
	 */
	String StdVersionId;
	/**
	 * 更新时间
	 */
	Date UpdateDTime;
	/**
	 * 更新人
	 */
	String UpdateUser;

	public StdSendInfo(){

	}

	public void finalize() throws Throwable {

	}
	public Date getCreateDTime(){
		return createDTime;
	}

	public String getCreateUser(){
		return this.createUser;
	}

	public long getId(){
		return this.Id;
	}

	public long getOrgId(){
		return this.OrgId;
	}

	public String getStdVersionId(){
		return this.StdVersionId;
	}

	/**
	 * 根据版本ID 获取标准版本基本信息
	 * 
	 * @param versionId
	 */
	public CDAVersion getStdVersionInfo(long versionId){
		return null;
	}

	public Date getUpdateDTime(){
		return this.UpdateDTime;
	}

	public String getUpdateUser(){
		return this.UpdateUser;
	}

	/**
	 * 
	 * @param dateTime
	 */
	public void setCreateDTime(Date dateTime){
		this.createDTime=dateTime;
	}

	/**
	 * 
	 * @param strUser
	 */
	public void setCreateUser(String strUser){
		this.createUser=strUser;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id){
		this.Id=id;
	}

	/**
	 * 
	 * @param orgId
	 */
	public void setOrgId(long orgId){
		this.OrgId=orgId;
	}

	/**
	 * 
	 * @param versionId
	 */
	public void setStdVersionId(String versionId){
		this.StdVersionId=versionId;
	}

	/**
	 * 
	 * @param dateTime
	 */
	public void setUpdateDTime(Date dateTime){
		this.UpdateDTime=dateTime;
	}

	/**
	 * 
	 * @param strUser
	 */
	public void setUpdateUser(String strUser){
		this.UpdateUser=strUser;
	}
}//end StdSendInfo