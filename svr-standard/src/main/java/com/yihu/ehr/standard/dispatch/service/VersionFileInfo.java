package com.yihu.ehr.standard.dispatch.service;


import java.util.Date;

/**
 * �����׼�汾�ļ���Ϣ
 * @author AndyCai
 * @version 1.0
 * @created 24-7��-2015 13:56:27
 */
public class VersionFileInfo {

	/**
	 * ��������
	 */
	private Date CreateDTime;
	/**
	 * ������
	 */
	private String CreateUser;
	/**
	 * �ļ���
	 */
	private String FileName;
	/**
	 * �ļ�·��
	 */
	private String FilePath;
	private long Id;
	/**
	 * �Ƚϰ汾ID
	 */
	private String SourceVersionId;
	/**
	 * ���Ƚϰ汾ID
	 */
	private String TargetVersionId;

	String file_pwd;



	public VersionFileInfo(){
		this.TargetVersionId="";
	}

	public void finalize() throws Throwable {

	}
	public Date getCreateDTime(){
		return this.CreateDTime;
	}

	public String getCreateUser(){
		return this.CreateUser;
	}

	public String getFileName(){
		return this.FileName;
	}

	public String getFilePath(){
		return this.FilePath;
	}

	public long getId(){
		return this.Id;
	}

	public String getSourceVersionId(){
		return this.SourceVersionId;
	}

	public String getTargetVersionId(){
		return this.TargetVersionId;
	}

	/**
	 * 
	 * @param dateTime
	 */
	public void setCreateDTime(Date dateTime){
		this.CreateDTime=dateTime;
	}

	/**
	 * 
	 * @param strUser
	 */
	public void setCreateUser(String strUser){
		this.CreateUser=strUser;
	}

	/**
	 * 
	 * @param strFileName
	 */
	public void setFileName(String strFileName){
		this.FileName=strFileName;
	}

	/**
	 * 
	 * @param strFilePath
	 */
	public void setFilePath(String strFilePath){
		this.FilePath=strFilePath;
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
	 * @param souerceVersionId
	 */
	public void setSourceVersionId(String souerceVersionId){
		this.SourceVersionId=souerceVersionId;
	}

	/**
	 * 
	 * @param targetVersionId
	 */
	public void setTargetVersionId(String targetVersionId){
		this.TargetVersionId=targetVersionId;
	}

	public String getFile_pwd(){ return this.file_pwd;}
	public void setFile_pwd(String file_pwd)
	{
		this.file_pwd=file_pwd;
	}
}//end VersionFileInfo