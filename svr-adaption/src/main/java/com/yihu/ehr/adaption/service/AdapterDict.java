package com.yihu.ehr.adaption.service;

/**
 * 方案字典映射
 * @author AndyCai
 * @version 1.0
 * @created 26-十月-2015 15:54:57
 */
public class AdapterDict {


	private Long id;

	private Long adapterPlanId;

	/**
	 * 所属标准字典
	 */
	private Long dictId;
	/**
	 * 标准字典项
	 */
	private Long dictEntryId;

	/**
	 * 所属机构字典
	 */
	private Long orgDictSeq;
	/**
	 * 机构字典项
	 */
	private Long orgDictItemSeq;
	/**
	 * 说明
	 */
	private String description;


	public AdapterDict(){	}

	public void finalize() throws Throwable {}

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

	public Long getDictId() {
		return dictId;
	}

	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	public Long getDictEntryId() {
		return dictEntryId;
	}

	public void setDictEntryId(Long dictEntryId) {
		this.dictEntryId = dictEntryId;
	}

	public Long getOrgDictSeq() {
		return orgDictSeq;
	}

	public void setOrgDictSeq(Long orgDictSeq) {
		this.orgDictSeq = orgDictSeq;
	}

	public Long getOrgDictItemSeq() {
		return orgDictItemSeq;
	}

	public void setOrgDictItemSeq(Long orgDictItemSeq) {
		this.orgDictItemSeq = orgDictItemSeq;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	public XAdapterDict setNewObject(XAdapterDict from){
		adapterPlanId=from.getAdapterPlanId();
		dictId=from.getDictId();
		dictEntryId=from.getDictEntryId();
		orgDictSeq=from.getOrgDictSeq();
		orgDictItemSeq=from.getOrgDictItemSeq();
		description=from.getDescription();
		return  this;
	}

}