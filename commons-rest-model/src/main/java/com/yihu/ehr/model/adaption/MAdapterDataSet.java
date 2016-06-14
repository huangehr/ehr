package com.yihu.ehr.model.adaption;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class MAdapterDataSet {

	private Long id;

	private Long adapterPlanId;

	private Long dataSetId;

	private Long metaDataId;

	private String dataType;

	private Long orgDataSetSeq;

	private Long orgMetaDataSeq;

	private String description;

	private Long stdDict;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStdDict() {
		return stdDict;
	}

	public void setStdDict(Long stdDict) {
		this.stdDict = stdDict;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getOrgMetaDataSeq() {
		return orgMetaDataSeq;
	}

	public void setOrgMetaDataSeq(Long orgMetaDataSeq) {
		this.orgMetaDataSeq = orgMetaDataSeq;
	}

	public Long getOrgDataSetSeq() {
		return orgDataSetSeq;
	}

	public void setOrgDataSetSeq(Long orgDataSetSeq) {
		this.orgDataSetSeq = orgDataSetSeq;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getMetaDataId() {
		return metaDataId;
	}

	public void setMetaDataId(Long metaDataId) {
		this.metaDataId = metaDataId;
	}

	public Long getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(Long dataSetId) {
		this.dataSetId = dataSetId;
	}

	public Long getAdapterPlanId() {
		return adapterPlanId;
	}

	public void setAdapterPlanId(Long adapterPlanId) {
		this.adapterPlanId = adapterPlanId;
	}

}