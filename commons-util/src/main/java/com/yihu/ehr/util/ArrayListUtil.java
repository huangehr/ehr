package com.yihu.ehr.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.10 13:37
 */
public class ArrayListUtil {

   public static String[] getArray(List<String> list){
        String[] str = list.toArray(new String[]{});
        return str;
    }

    public static List<String> getList(String[] str){
        List<String> list = Arrays.asList(str);
        return list;
    }
}
