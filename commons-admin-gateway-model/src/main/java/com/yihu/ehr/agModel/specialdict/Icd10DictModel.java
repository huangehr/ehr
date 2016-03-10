package com.yihu.ehr.agModel.specialdict;

import java.io.Serializable;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class Icd10DictModel implements Serializable{

	public Icd10DictModel() {
	}

	private String id;
	private String code;
	private String name;
	private String phoneticCode;
	private String chronicFlag;
	private String infectiousFlag;
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneticCode() {
		return phoneticCode;
	}

	public void setPhoneticCode(String phoneticCode) {
		this.phoneticCode = phoneticCode;
	}

	public String getChronicFlag() {
		return chronicFlag;
	}

	public void setChronicFlag(String chronicFlag) {
		this.chronicFlag = chronicFlag;
	}

	public String getInfectiousFlag() {
		return infectiousFlag;
	}

	public void setInfectiousFlag(String infectiousFlag) {
		this.infectiousFlag = infectiousFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}