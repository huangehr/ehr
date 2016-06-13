package com.yihu.ehr.model.standard;

/**
 * @author linaz
 * @created 2016.06.08 16:50
 */
public class MCdaDataSet {

	private String id;
	private String cdaId;
	private String dataSetId;
	private String dataSetCode;

	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCdaId() {
		return cdaId;
	}

	public void setCdaId(String cdaId) {
		this.cdaId = cdaId;
	}

	public String getDataSetCode() {
		return dataSetCode;
	}

	public void setDataSetCode(String dataSetCode) {
		this.dataSetCode = dataSetCode;
	}
}