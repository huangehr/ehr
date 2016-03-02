package com.yihu.ehr.agModel.adapter;

import com.yihu.ehr.util.validate.Required;
import com.yihu.ehr.util.validate.Valid;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@Valid
public class AdapterDictModel {
	private Long id;

	private Long adapterPlanId;

	/**
	 * 所属标准字典
	 */
	private Long dictId;
	private String dictCode;
	private String dictName;

	/**
	 * 标准字典项
	 */
	private Long dictEntryId;
	private String dictEntryCode;
	private String dictEntryName;

	/**
	 *  所属机构字典
	 */
	private Long orgDictSeq;
	private String orgDictCode;
	private String orgDictName;

	/**
	 * 机构字典项
	 */
	private Long orgDictEntrySeq;
	private String orgDictEntryCode;
	private String orgDictEntryName;

	/**
	 * 说明
	 */
	private String description;

	public AdapterDictModel(){	}

	public void finalize() throws Throwable {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required(filedName = "适配方案")
	public Long getAdapterPlanId() {
		return adapterPlanId;
	}

	public void setAdapterPlanId(Long adapterPlanId) {
		this.adapterPlanId = adapterPlanId;
	}

	@Required(filedName = "标准字典")
	public Long getDictId() {
		return dictId;
	}

	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	@Required(filedName = "标准字典项")
	public Long getDictEntryId() {
		return dictEntryId;
	}

	public void setDictEntryId(Long dictEntryId) {
		this.dictEntryId = dictEntryId;
	}

	public Long getOrgDictEntrySeq() {
		return orgDictEntrySeq;
	}

	public void setOrgDictEntrySeq(Long orgDictEntrySeq) {
		this.orgDictEntrySeq = orgDictEntrySeq;
	}

	public Long getOrgDictSeq() {
		return orgDictSeq;
	}

	public void setOrgDictSeq(Long orgDictSeq) {
		this.orgDictSeq = orgDictSeq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getDictEntryCode() {
		return dictEntryCode;
	}

	public void setDictEntryCode(String dictEntryCode) {
		this.dictEntryCode = dictEntryCode;
	}

	public String getDictEntryName() {
		return dictEntryName;
	}

	public void setDictEntryName(String dictEntryName) {
		this.dictEntryName = dictEntryName;
	}

	public String getOrgDictCode() {
		return orgDictCode;
	}

	public void setOrgDictCode(String orgDictCode) {
		this.orgDictCode = orgDictCode;
	}

	public String getOrgDictName() {
		return orgDictName;
	}

	public void setOrgDictName(String orgDictName) {
		this.orgDictName = orgDictName;
	}

	public String getOrgDictEntryCode() {
		return orgDictEntryCode;
	}

	public void setOrgDictEntryCode(String orgDictEntryCode) {
		this.orgDictEntryCode = orgDictEntryCode;
	}

	public String getOrgDictEntryName() {
		return orgDictEntryName;
	}

	public void setOrgDictEntryName(String orgDictEntryName) {
		this.orgDictEntryName = orgDictEntryName;
	}
}