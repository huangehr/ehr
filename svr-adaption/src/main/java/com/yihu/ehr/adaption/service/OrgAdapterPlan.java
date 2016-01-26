package com.yihu.ehr.adaption.service;

import com.yihu.ehr.model.dict.MBaseDict;

/**
 * 机构采集标准信息表
 * @author AndyCai
 * @version 1.0
 * @created 26-十月-2015 15:54:58
 */
public class OrgAdapterPlan  {

	/**
	 * 标准版本
	 */
	//private CDAVersion version;
	/**
	 * 方案代码
	 */
	private String code;
	/**
	 * 说明
	 */
	private String description;
	private Long id;
	/**
	 * 方案名称
	 */
	private String name;
	/**
	 * 适配机构
	 */
	private String org;
	/**
	 * 父级方案ID
	 */
	private Long parentId;
	/**
	 * 方案类别
	 */
	private MBaseDict type;

	private int status;

	public OrgAdapterPlan(){
		this.status=0;
	}

	public void finalize() throws Throwable {

	}

//	public CDAVersion getVersion(){
//		return version;
//	}

	public String getCode(){
		return code;
	}

	public String getDescription(){
		return description;
	}

	public Long getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getOrg(){
		return org;
	}

	public Long getParentId(){
		return parentId;
	}

	public MBaseDict getType(){
		return type;
	}

	/**
	 *
	 * @param version
	 */
//	public void setVersion(XCDAVersion version){
//		this.version = version;
//	}

	/**
	 *
	 * @param code
	 */
	public void setCode(String code){
		this.code = code;
	}

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description){
		this.description = description;
	}

	/**
	 *
	 * @param id
	 */
	public void setId(Long id){
		this.id = id;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 *
	 * @param org
	 */
	public void setOrg(String org){
		this.org = org;
	}

	/**
	 *
	 * @param parentId
	 */
	public void setParentId(Long parentId){
		this.parentId = parentId;
	}

	/**
	 *
	 * @param type
	 */
	public void setType(MBaseDict type){
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}