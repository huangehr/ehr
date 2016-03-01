package com.yihu.ehr.model.specialdict;

import java.io.Serializable;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class MHpIcd10Relation implements Serializable{

	public MHpIcd10Relation() {
	}

	private String id;
	private String hpId;
	private String icd10Id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHpId() {
		return hpId;
	}

	public void setHpId(String hpId) {
		this.hpId = hpId;
	}

	public String getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(String icd10Id) {
		this.icd10Id = icd10Id;
	}
}