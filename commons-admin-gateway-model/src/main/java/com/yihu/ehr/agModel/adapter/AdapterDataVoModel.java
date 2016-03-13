package com.yihu.ehr.agModel.adapter;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class AdapterDataVoModel {

	/**
	 * �������ݼ�ID
	 */
	private Long id;
	/**
	 * ��������
	 */
	private Long adapterPlanId;
	/**
	 * ������׼���ݼ�
	 */
	private Long dataSetId;
	private String dataSetCode;
	private String dataSetName;

	/**
	 * ��׼����Ԫ
	 */
	private Long metaDataId;
	private String metaDataCode;
	private String metaDataName;

	/**
	 * ��������
	 */
	private String dataType;
	private String dataTypeName;
	/**
	 * �����������ݼ�
	 */
	private Long orgDataSetSeq;
	private String orgDataSetCode;
	private String orgDataSetName;
	/**
	 * ��������Ԫ
	 */
	private Long orgMetaDataSeq;
	private String orgMetaDataCode;
	private String orgMetaDataName;

	private String description;

	public AdapterDataVoModel(){	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataSetCode() {
		return dataSetCode;
	}

	public void setDataSetCode(String dataSetCode) {
		this.dataSetCode = dataSetCode;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getMetaDataCode() {
		return metaDataCode;
	}

	public void setMetaDataCode(String metaDataCode) {
		this.metaDataCode = metaDataCode;
	}

	public String getMetaDataName() {
		return metaDataName;
	}

	public void setMetaDataName(String metaDataName) {
		this.metaDataName = metaDataName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public Long getOrgDataSetSeq() {
		return orgDataSetSeq;
	}

	public void setOrgDataSetSeq(Long orgDataSetSeq) {
		this.orgDataSetSeq = orgDataSetSeq;
	}

	public String getOrgDataSetCode() {
		return orgDataSetCode;
	}

	public void setOrgDataSetCode(String orgDataSetCode) {
		this.orgDataSetCode = orgDataSetCode;
	}

	public String getOrgDataSetName() {
		return orgDataSetName;
	}

	public void setOrgDataSetName(String orgDataSetName) {
		this.orgDataSetName = orgDataSetName;
	}

	public Long getOrgMetaDataSeq() {
		return orgMetaDataSeq;
	}

	public void setOrgMetaDataSeq(Long orgMetaDataSeq) {
		this.orgMetaDataSeq = orgMetaDataSeq;
	}

	public String getOrgMetaDataCode() {
		return orgMetaDataCode;
	}

	public void setOrgMetaDataCode(String orgMetaDataCode) {
		this.orgMetaDataCode = orgMetaDataCode;
	}

	public String getOrgMetaDataName() {
		return orgMetaDataName;
	}

	public void setOrgMetaDataName(String orgMetaDataName) {
		this.orgMetaDataName = orgMetaDataName;
	}
}