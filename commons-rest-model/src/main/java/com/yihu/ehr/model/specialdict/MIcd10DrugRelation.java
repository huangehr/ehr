package com.yihu.ehr.model.specialdict;

import java.io.Serializable;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class MIcd10DrugRelation implements Serializable{

	public MIcd10DrugRelation() {
	}

	private String id;
	private String icd10Id;
	private String drugId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(String icd10Id) {
		this.icd10Id = icd10Id;
	}

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}
}