package com.yihu.ehr.model.standard;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:08:41
 */
public class MCdaDataSetRelationship {

	private String id;
	private String cdaId;
	private String dataSetId;

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
}