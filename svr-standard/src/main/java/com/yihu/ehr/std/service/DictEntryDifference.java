package com.yihu.ehr.std.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AndyCai
 * @version 1.0
 * @created 20-7月-2015 17:35:11
 */
public class DictEntryDifference {

	private Map<String, PropertyDifference> dictEntryPropertyDiff;

	public void finalize() throws Throwable {

	}
	public DictEntryDifference(){
		this.dictEntryPropertyDiff = new HashMap<>();
	}

	/**
	 * 
	 * @param propertyName
	 * @param originValue
	 * @param newValue
	 */
	public void addPropertyDifference(String propertyName, Object originValue, Object newValue){
		dictEntryPropertyDiff.put(propertyName,new PropertyDifference(originValue,newValue));
	}

	public String[] getChangedProperties(){
		if (dictEntryPropertyDiff==null)
			return null;

		return dictEntryPropertyDiff.keySet().toArray(new String[dictEntryPropertyDiff.size()]);
	}

	/**
	 * 
	 * @param propertyName
	 */
	public Object getNewValue(String propertyName){
		if(dictEntryPropertyDiff.containsKey(propertyName))
		{
			PropertyDifference propertyDifference =(PropertyDifference) dictEntryPropertyDiff.get(propertyName);
			return propertyDifference.newVaue;
		}
		return null;
	}

	/**
	 * 
	 * @param propertyName
	 */
	public Object getOriginValue(String propertyName){
		if(dictEntryPropertyDiff.containsKey(propertyName))
		{
			PropertyDifference propertyDifference =(PropertyDifference) dictEntryPropertyDiff.get(propertyName);
			return propertyDifference.originValue;
		}
		return null;
	}
}//end DictEntryDifference