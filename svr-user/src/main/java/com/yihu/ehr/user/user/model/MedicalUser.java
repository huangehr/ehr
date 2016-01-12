package com.yihu.ehr.user.user.model;


/**
 * 抽象医疗用户类.
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 20:33:36
 */
public class MedicalUser extends User {

	private String major;

	private String medicalRole;

	private String techTitle;

	public MedicalUser(){
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMedicalRole() {
		return medicalRole;
	}

	public void setMedicalRole(String medicalRole) {
		this.medicalRole = medicalRole;
	}

	public String getTechTitle() {
		return techTitle;
	}

	public void setTechTitle(String techTitle) {
		this.techTitle = techTitle;
	}
}