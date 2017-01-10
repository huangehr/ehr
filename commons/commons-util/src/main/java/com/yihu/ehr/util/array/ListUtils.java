package com.yihu.ehr.util.array;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/7/25
 */
public class ListUtils {

    public static String toString(Collection c,Class clz, String method)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = clz.getMethod(method);
        String s = "";
        for(Object o : c){
            s += "," + m.invoke(o);
        }
        if(s.length()>0)
            return s.substring(1);
        return s;
    }
}
