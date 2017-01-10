package com.yihu.ehr.standard.document.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:08:41
 */
@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public class BaseCDADataSetRelationship {

	private String id;
	private String cdaId;
	private String dataSetId;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "cda_id", unique = true, nullable = false)
	public String getCdaId() {
		return cdaId;
	}

	public void setCdaId(String cdaId) {
		this.cdaId = cdaId;
	}

	@Column(name = "dataset_id", unique = true, nullable = false)
	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}
}