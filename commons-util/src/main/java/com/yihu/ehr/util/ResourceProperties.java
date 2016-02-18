package com.yihu.ehr.util;

import java.util.Properties;

/**
 * Created by szx on 2015/12/15.
 */
public class ResourceProperties {
    public static Properties properties;
    static{
        String classPath = ResourceProperties.class.getResource("/").getPath();
        System.out.println(classPath);
        properties = PropertyLoader.loadFile(classPath + "resource.properties");
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
