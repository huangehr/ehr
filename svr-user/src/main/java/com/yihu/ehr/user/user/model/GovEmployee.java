package com.yihu.ehr.user.user.model;


import com.yihu.ehr.user.user.service.User;

/**
 * 政府用户
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:07
 */
public class GovEmployee extends User {

	/**
	 * 行政职称
	 */
	private String administrativeTitle;

	public GovEmployee(){
	}

	/**
	 * 行政职称
	 */
	public String getAdministrativeTitle(){
		return administrativeTitle;
	}
}