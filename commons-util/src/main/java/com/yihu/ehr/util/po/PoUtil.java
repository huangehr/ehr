package com.yihu.ehr.util.po;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/2/23.
 */
public class PoUtil {

    /**
     * 通过Bean对象获取查询语句
     * @param obj 对象
     * @return Hql
     */
    public static String getHql(Object obj) throws Exception {
        StringBuffer strBuffer = new StringBuffer(" from "+ obj.getClass().getSimpleName()+" where 1=1");
        Class<? extends Object> objClass = obj.getClass();
        Field fields[] = objClass.getDeclaredFields();
        for (Field field:fields) {
            String fieldValue = getFieldValue(obj, field);
            if(fieldValue!=null){
                strBuffer.append(" and "+field.getName()+"='"+fieldValue+"'");
            }
        }
        return strBuffer.toString();
    }

    public static String getFieldValue(Object obj, Field field) throws Exception {
        String name = field.getName();
        String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        Method method = obj.getClass().getMethod(methodName);
        Object methodValue = method.invoke(obj);
        if(methodValue!=null){
            return methodValue.toString();
        }
        return null;
    }

}
