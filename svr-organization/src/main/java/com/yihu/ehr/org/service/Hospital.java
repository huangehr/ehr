package com.yihu.ehr.org.service;


/**
 * 医疗机构。医院属性需要细化。卫生统计司处查找。
 * @author Sand
 * @version 1.0
 * @created 12-五月-2015 17:47:50
 */
public class Hospital extends Organization {

	/**
	 * 等级，如：三甲
	 */
	private int level;
	/**
	 * 公立或民营医院
	 */
	private boolean publiction;

	public Hospital(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
}//end Hospital