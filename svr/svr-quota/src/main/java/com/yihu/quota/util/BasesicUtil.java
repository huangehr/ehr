package com.yihu.quota.util;

import java.lang.reflect.Method;

/**
 * Created by janseny on 2017/12/14.
 */
public class BasesicUtil {

    /**
     * 根据属性名获取属性值
     * */
    public String getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
