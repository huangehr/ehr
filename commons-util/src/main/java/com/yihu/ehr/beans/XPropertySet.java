package com.yihu.ehr.beans;


/**
 * 更加通用的对象属性获取接口. 就重构角度而言, 一个对象的常用属性做成get/set方法更方便, 对于不常用的方法则可以使用
 * getValue/setValue 等方法来获取数据, 以减少接口方法的数量, 这对对象的属性扩展也是个不错的方案. 在健康档案平台中,
 * 我们将混合采用这两种方式: 常用属性使用 getEntity/set 方法获取数据, 不常用属性将采用 getValue/setValue 方式来获取数据,
 * 不常用的属性将在对象的文档中进行标注.
 *
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:06:03
 */
public interface XPropertySet{

	/**
	 * 获取属性值.
	 * 
	 * @param propertyName
	 */
	public Object getPropertyValue(String propertyName);

	/**
	 * 设置属性值.
	 * 
	 * @param propertyName
	 * @param value
	 */
	public void setPropertyValue(String propertyName, Object value);

	/**
	 * 获取属性接口.
	 */
	public XPropertySetInfo getPropertySetInfo();

}