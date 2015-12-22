package com.yihu.ehr.std.model;

import com.yihu.ehr.std.service.PropertyDifference;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据差异.
 * @author Sand
 * @version 1.0
 * @created 07-7月-2015 15:13:37
 */
public class MetaDataDifference {

	private Map<String, PropertyDifference> metaDataPropertyDiff;

	public MetaDataDifference(){
        metaDataPropertyDiff = new HashMap<>();
    }

    /**
     * 添加一个变化的属性.
     *
     * @param propertyName
     * @param originValue
     * @param newValue
     */
    public void addPropertyDifference(String propertyName, Object originValue, Object newValue) {
        metaDataPropertyDiff.put(propertyName, new PropertyDifference(originValue, newValue));
    }

	/**
	 * 获取被修改的属性.
	 */
	public String[] getChangedProperties(){
		return metaDataPropertyDiff.keySet().toArray(new String[metaDataPropertyDiff.size()]);
	}

	/**
	 * 获取修改后的值.
	 * 
	 * @param propertyName    propertyName
	 */
	public Object getNewValue(String propertyName){
        if(metaDataPropertyDiff.get(propertyName) == null){
		    return null;
        }

        return metaDataPropertyDiff.get(propertyName).newVaue;
	}

	/**
	 * 
	 * @param propertyName    propertyName
	 */
	public Object getOriginValue(String propertyName){
        if(metaDataPropertyDiff.get(propertyName) == null){
            return null;
        }

        return metaDataPropertyDiff.get(propertyName).originValue;
	}
}