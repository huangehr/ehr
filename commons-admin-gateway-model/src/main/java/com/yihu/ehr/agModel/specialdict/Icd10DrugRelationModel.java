package com.yihu.ehr.agModel.specialdict;

import java.io.Serializable;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class Icd10DrugRelationModel implements Serializable{

	public Icd10DrugRelationModel() {
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