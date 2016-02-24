package com.yihu.ehr.model.adaption;

/**
 * �����ֵ�ӳ��
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class MAdapterDict {
	private Long id;

	private Long adapterPlanId;

	/**
	 * ������׼�ֵ�
	 */
	private Long dictId;
	private String dictCode;
	private String dictName;

	/**
	 * ��׼�ֵ���
	 */
	private Long dictEntryId;
	private String dictEntryCode;
	private String dictEntryName;

	/**
	 * ���������ֵ�
	 */
	private Long orgDictSeq;
	private String orgDictCode;
	private String orgDictName;

	/**
	 * �����ֵ���
	 */
	private Long orgDictEntrySeq;
	private String orgDictEntryCode;
	private String orgDictEntryName;

	/**
	 * ˵��
	 */
	private String description;

	public MAdapterDict(){	}

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