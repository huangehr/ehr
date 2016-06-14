package com.yihu.ehr.beans;


/**
 * 属性结构体, 表示一个属性的名称, 类型, 处理方式, 属性的特性.
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:04:46
 */
public class Property {

	/**
	 * 属性的特性
	 */
	public PropertyAttribute Attributes;
	/**
	 * 属性处理方式
	 */
	public long Handle;
	/**
	 * 属性名
	 */
	public String Name;
	/**
	 * 属性类型
	 */
	public Class Type;
}//end Property