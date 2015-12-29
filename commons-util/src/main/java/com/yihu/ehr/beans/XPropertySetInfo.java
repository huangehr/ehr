package com.yihu.ehr.beans;


import com.yihu.ehr.beans.Property;

/**
 * 属性集接口, 用于检测一个对象支持的属性集及属性特性. 此接口目前先设计, 但不实现, 为后期给跨语言调用JAVA接口做准备.
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:08:38
 */
public interface XPropertySetInfo {

	/**
	 * 获取属性数组
	 */
	public Property[] getProperties();

	/**
	 * 获取指定属性
	 * 
	 * @param properyName
	 */
	public Property getProperty(String properyName);

	/**
	 * 判断是否具有某种属性.
	 * 
	 * @param propertyName
	 */
	public boolean hasProperty(String propertyName);

}