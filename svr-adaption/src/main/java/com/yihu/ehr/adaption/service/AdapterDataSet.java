package com.yihu.ehr.adaption.service;

/**
 * @author AndyCai
 * @version 1.0
 * @created 26-十月-2015 15:54:57
 */
public class AdapterDataSet {

	private Long id;
	/**
	 * 所属方案
	 */
	private Long adapterPlanId;
	/**
	 * 所属标准数据集
	 */
	private Long dataSetId;
	/**
	 * 标准数据元
	 */
	private Long metaDataId;
	/**
	 * 数据类型
	 */
	private String dataType;
	/**
	 * 所属机构数据集
	 */
	private Long orgDataSetSeq;
	/**
	 * 机构数据元
	 */
	private Long orgMetaDataSeq;

	private String description;

	private Long stdDict;

	public AdapterDataSet(){	}

	public void finalize() throws Throwable {	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdapterPlanId() {
		return adapterPlanId;
	}

	public void setAdapterPlanId(Long adapterPlanId) {
		this.adapterPlanId = adapterPlanId;
	}

	public Long getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(Long dataSetId) {
		this.dataSetId = dataSetId;
	}

	public Long getMetaDataId() {
		return metaDataId;
	}

	public void setMetaDataId(Long metaDataId) {
		this.metaDataId = metaDataId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getOrgDataSetSeq() {
		return orgDataSetSeq;
	}

	public void setOrgDataSetSeq(Long orgDataSetSeq) {
		this.orgDataSetSeq = orgDataSetSeq;
	}

	public Long getOrgMetaDataSeq() {
		return orgMetaDataSeq;
	}

	public void setOrgMetaDataSeq(Long orgMetaDataSeq) {
		this.orgMetaDataSeq = orgMetaDataSeq;
	}

	public Long getStdDict() {
		return stdDict;
	}

	public void setStdDict(Long stdDict) {
		this.stdDict = stdDict;
	}

	public AdapterDataSet setNewObject(AdapterDataSet from){
		adapterPlanId=from.getAdapterPlanId();
		dataSetId=from.getDataSetId();
		metaDataId=from.getMetaDataId();
		dataType=from.getDataType();
		orgDataSetSeq=from.getOrgDataSetSeq();
		orgMetaDataSeq=from.getOrgMetaDataSeq();
		description=from.getDescription();
		stdDict=from.getStdDict();
		return  this;
	}
}