package com.yihu.ehr.std.service;


/**
 * 属性差异.
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:06
 */
public class PropertyDifference {
	public Object newVaue;                  // 新值
	public Object originValue;              // 原始值

    public PropertyDifference(Object originValue, Object newValue){
        this.originValue = originValue;
        this.newVaue = newValue;
    }

	public void finalize() throws Throwable {
	}

    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof PropertyDifference) {
            PropertyDifference other = (PropertyDifference)obj;

            return this.originValue.equals(other.originValue) && this.newVaue.equals(other.newVaue);
        }

        return false;
    }
}