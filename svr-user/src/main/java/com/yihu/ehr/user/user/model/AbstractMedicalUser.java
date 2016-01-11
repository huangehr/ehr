package com.yihu.ehr.user.user.model;

import com.yihu.ha.dict.model.common.MedicalRole;

/**
 * 抽象医疗用户类.
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 20:33:36
 */
public class AbstractMedicalUser extends AbstractUser {

	private String major;

	private MedicalRole medicalRole;

	private String techTitle;

	public AbstractMedicalUser(){
	}

	public String getMajor(){
		return major;
	}

	public MedicalRole getMedicalRole(){
		return medicalRole;
	}

	public String getTechTitle(){
		return techTitle;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public void setMedicalRole(MedicalRole medicalRole) {
		this.medicalRole = medicalRole;
	}

	public void setTechTitle(String techTitle) {
		this.techTitle = techTitle;
	}
}